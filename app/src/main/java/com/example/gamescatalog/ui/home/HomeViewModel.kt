// File: gamesCatalog2/app/src/main/java/com/example/gamescatalog/ui/home/HomeViewModel.kt
package com.example.gamescatalog.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gamescatalog.data.CatalogRepository
import com.example.gamescatalog.data.Result
import com.example.gamescatalog.data.remote.response.GamesResponse
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

// Sealed class untuk merepresentasikan state UI HomeScreen
sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val gamesResponse: GamesResponse,
        val isLoadingMore: Boolean = false,
        val canLoadMore: Boolean = true
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@OptIn(FlowPreview::class)
class HomeViewModel(private val repository: CatalogRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResult = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val searchResult: StateFlow<HomeUiState> = _searchResult

    private val _currentPage = MutableStateFlow(1)
    private val _currentSearchPage = MutableStateFlow(1)
    private val _isPaginating = MutableStateFlow(false)

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    init {
        getGames(isPaginating = false)

        viewModelScope.launch {
            _searchQuery
                .debounce(500L)
                .distinctUntilChanged()
                .collect { query ->
                    _isPaginating.value = false
                    _isRefreshing.value = false
                    if (query.isBlank()) {
                        getGames(isPaginating = false)
                    } else {
                        performSearch(query, isPaginating = false)
                    }
                }
        }
    }

    private fun getGames(isPaginating: Boolean) {
        if (_isPaginating.value && isPaginating) return
        if (isPaginating && (_uiState.value as? HomeUiState.Success)?.canLoadMore == false) return

        _isPaginating.value = isPaginating

        viewModelScope.launch {
            if (!isPaginating) {
                _currentPage.value = 1
                // Jangan langsung set ke HomeUiState.Loading jika sudah ada data sebelumnya (saat refresh).
                // Hanya set ke Loading jika ini adalah pemuatan awal (saat _uiState bukan Success).
                if (_uiState.value !is HomeUiState.Success) {
                    _uiState.value = HomeUiState.Loading
                }
                _isRefreshing.value = true // Aktifkan indikator refresh
            } else {
                _currentPage.value++
            }

            repository.getGamesList(page = _currentPage.value).collect { result ->
                _uiState.value = when (result) {
                    is Result.Loading -> {
                        // Untuk refresh, tetap tampilkan data lama dengan indikator loading aktif
                        (_uiState.value as? HomeUiState.Success)?.copy(isLoadingMore = true)
                            ?: HomeUiState.Loading // Jika belum Success, set ke Loading awal
                    }
                    is Result.Success -> {
                        val currentGames = if (isPaginating || _uiState.value is HomeUiState.Success) { // Gabungkan jika paginasi atau jika sudah ada data
                            (_uiState.value as? HomeUiState.Success)?.gamesResponse?.results?.toMutableList() ?: mutableListOf()
                        } else {
                            mutableListOf()
                        }
                        currentGames.addAll(result.data.results)

                        val canLoadMore = result.data.results.isNotEmpty() && (result.data.results.size == 20)
                        HomeUiState.Success(
                            GamesResponse(count = result.data.count, results = currentGames),
                            isLoadingMore = false,
                            canLoadMore = canLoadMore
                        )
                    }
                    is Result.Error -> HomeUiState.Error(result.message ?: "Unknown error")
                }
                _isPaginating.value = false
                _isRefreshing.value = false // Nonaktifkan indikator refresh setelah selesai
            }
        }
    }

    private fun performSearch(query: String, isPaginating: Boolean) {
        if (_isPaginating.value && isPaginating) return
        if (isPaginating && (_searchResult.value as? HomeUiState.Success)?.canLoadMore == false) return

        _isPaginating.value = isPaginating

        viewModelScope.launch {
            if (!isPaginating) {
                _currentSearchPage.value = 1
                // BARIS INI DITAMBAHKAN/DIPERBAIKI:
                // Sama seperti getGames(), jangan langsung set ke Loading jika sudah ada hasil sebelumnya.
                if (_searchResult.value !is HomeUiState.Success) {
                    _searchResult.value = HomeUiState.Loading
                }
                _isRefreshing.value = true // Aktifkan indikator refresh untuk pencarian
            } else {
                _currentSearchPage.value++
            }

            repository.searchGames(query = query, page = _currentSearchPage.value).collect { result ->
                _searchResult.value = when (result) {
                    is Result.Loading -> {
                        (_searchResult.value as? HomeUiState.Success)?.copy(isLoadingMore = true)
                            ?: HomeUiState.Loading
                    }
                    is Result.Success -> {
                        val currentSearchResults = if (isPaginating || _searchResult.value is HomeUiState.Success) {
                            (_searchResult.value as? HomeUiState.Success)?.gamesResponse?.results?.toMutableList() ?: mutableListOf()
                        } else {
                            mutableListOf()
                        }
                        currentSearchResults.addAll(result.data.results)

                        val canLoadMore = result.data.results.isNotEmpty() && (result.data.results.size == 20)
                        HomeUiState.Success(
                            GamesResponse(count = result.data.count, results = currentSearchResults),
                            isLoadingMore = false,
                            canLoadMore = canLoadMore
                        )
                    }
                    is Result.Error -> HomeUiState.Error(result.message ?: "Unknown error")
                }
                _isPaginating.value = false
                _isRefreshing.value = false
            }
        }
    }

    fun loadNextPage() {
        val currentQuery = _searchQuery.value
        if (currentQuery.isBlank()) {
            getGames(isPaginating = true)
        } else {
            performSearch(currentQuery, isPaginating = true)
        }
    }

    fun refreshGames() {
        val currentQuery = _searchQuery.value
        if (currentQuery.isBlank()) {
            getGames(isPaginating = false)
        } else {
            performSearch(currentQuery, isPaginating = false)
        }
    }

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    companion object {
        fun provideFactory(repository: CatalogRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                        return HomeViewModel(repository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
                }
            }
    }
}