package com.example.gamescatalog.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.gamescatalog.data.CatalogRepository
import com.example.gamescatalog.data.Result
import com.example.gamescatalog.data.preferences.UserPreferences
import com.example.gamescatalog.data.remote.response.GameDetailResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// ... (sealed interface DetailUiState tidak berubah)
sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(
        val gameDetail: GameDetailResponse,
        val isBookmarked: Boolean = false
    ) : DetailUiState
    data class Error(val message: String) : DetailUiState
}


class DetailViewModel(
    private val repository: CatalogRepository,
    private val preferences: UserPreferences,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState
    private val gameId: Int = savedStateHandle.get<Int>("gameId") ?: -1

    init {
        if (gameId != -1) {
            getGameDetails()
        } else {
            _uiState.value = DetailUiState.Error("Invalid Game ID")
        }
    }


    private fun getGameDetails() {
        viewModelScope.launch {
            // Mengakses elemen 'second' dari Pair untuk mendapatkan userId.
            val userId = preferences.getSession().first().second
            if (userId != -1) {
                // PERBAIKAN: Teruskan userId ke repository.getGameDetail
                repository.getGameDetail(gameId, userId).collect { result -> // Baris ini diperbaiki
                    _uiState.value = when (result) {
                        is Result.Success -> {
                            // Status bookmark akan dicek di dalam repository,
                            // jadi kita hanya perlu memperbarui uiState dengan data yang diterima.
                            // isBookmarked akan diperbarui secara reaktif oleh checkBookmarkStatus jika perlu
                            // atau sudah ada di GameDetailResponse dari bookmark.
                            val isBookmarkedFromRepo = repository.isBookmarked(result.data.id, userId).first() // Pastikan status bookmark di-refresh
                            DetailUiState.Success(result.data, isBookmarked = isBookmarkedFromRepo)
                        }
                        is Result.Error -> DetailUiState.Error(result.message ?: "Unknown Error")
                        is Result.Loading -> DetailUiState.Loading
                    }
                }
            } else {
                _uiState.value = DetailUiState.Error("User session not found.")
            }
        }
    }

    fun toggleBookmark(game: GameDetailResponse) {
        viewModelScope.launch {
            val userId = preferences.getSession().first().second
            if (userId != -1) {
                repository.toggleBookmark(game, userId)
                // Setelah toggle, refresh status bookmark
                checkBookmarkStatus(game.id, userId)
            }
        }
    }

    private fun checkBookmarkStatus(gameId: Int, userId: Int) {
        viewModelScope.launch {
            repository.isBookmarked(gameId, userId).collect { isBookmarked ->
                val currentState = _uiState.value
                if (currentState is DetailUiState.Success) {
                    _uiState.value = currentState.copy(isBookmarked = isBookmarked)
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            repository: CatalogRepository,
            preferences: UserPreferences
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val savedStateHandle = extras.createSavedStateHandle()
                return DetailViewModel(repository, preferences, savedStateHandle) as T
            }
        }
    }
}