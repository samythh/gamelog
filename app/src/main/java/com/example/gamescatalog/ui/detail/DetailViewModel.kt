// File: gamesCatalog2/app/src/main/java/com/example/gamescatalog/ui/detail/DetailViewModel.kt
package com.example.gamescatalog.ui.detail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gamescatalog.data.CatalogRepository
import com.example.gamescatalog.data.Result
import com.example.gamescatalog.data.preferences.UserPreferences
import com.example.gamescatalog.data.remote.response.GameDetailResponse
import com.example.gamescatalog.data.remote.response.GameScreenshotsResponse
import com.example.gamescatalog.di.Injection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: CatalogRepository,
    private val preferences: UserPreferences // Konfirmasikan parameter ini ada
) : ViewModel() {

    private val _gameDetail = MutableStateFlow<Result<GameDetailResponse>>(Result.Loading)
    val gameDetail: StateFlow<Result<GameDetailResponse>> = _gameDetail.asStateFlow()

    private val _isBookmarked = MutableStateFlow(false)
    val isBookmarked: StateFlow<Boolean> = _isBookmarked.asStateFlow()

    private val _userId = MutableStateFlow(0)
    val userId: StateFlow<Int> = _userId.asStateFlow()

    private val _gameScreenshots = MutableStateFlow<Result<GameScreenshotsResponse>>(Result.Loading)
    val gameScreenshots: StateFlow<Result<GameScreenshotsResponse>> = _gameScreenshots.asStateFlow()

    init {
        viewModelScope.launch {
            preferences.getSession().collect { (_, currentUserId) ->
                _userId.value = currentUserId
            }
        }
    }

    fun getGameDetail(gameId: Int, userId: Int) {
        viewModelScope.launch {
            repository.getGameDetail(gameId, userId).collect { result ->
                _gameDetail.value = result
            }
        }
        getGameScreenshots(gameId)
    }

    fun getGameScreenshots(gameId: Int) {
        viewModelScope.launch {
            repository.getGameScreenshots(gameId).collect { result ->
                _gameScreenshots.value = result
            }
        }
    }

    fun checkBookmarkStatus(itemId: Int, userId: Int) {
        viewModelScope.launch {
            repository.isBookmarked(itemId, userId).collect { isBookmarked ->
                _isBookmarked.value = isBookmarked
            }
        }
    }

    fun toggleBookmark(gameDetail: GameDetailResponse, userId: Int) {
        viewModelScope.launch {
            repository.toggleBookmark(gameDetail, userId)
            checkBookmarkStatus(gameDetail.id, userId)
        }
    }

    companion object {
        fun provideFactory(
            repository: CatalogRepository,
            context: Context
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
                        // UserPreferencesg dibuat dengan context yang valid.
                        return DetailViewModel(repository, Injection.provideUserPreferences(context)) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
                }
            }
    }
}