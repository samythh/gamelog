// File: ui/home/HomeViewModel.kt

package com.example.gamescatalog.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamescatalog.data.CatalogRepository
import com.example.gamescatalog.data.Result
import com.example.gamescatalog.data.remote.response.Game
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


// Data class untuk menampung semua state yang dibutuhkan oleh HomeScreen
// Kita tambahkan state baru untuk pagination
data class HomeUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false, // State untuk loading di bagian bawah list
    val games: List<Game> = emptyList(),
    val error: String? = null,
    val isLastPage: Boolean = false // State untuk menandakan semua data sudah dimuat
)

class HomeViewModel(private val repository: CatalogRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    // Variabel untuk melacak nomor halaman saat ini
    private var currentPage = 1

    // Blok init akan dieksekusi saat ViewModel pertama kali dibuat
    init {
        // Langsung panggil untuk pemuatan data awal
        fetchGames(isInitialLoad = true)
    }

    /**
     * Mengambil daftar game dari repository.
     * @param isInitialLoad menandakan apakah ini pemuatan awal (refresh) atau halaman berikutnya.
     */
    fun fetchGames(isInitialLoad: Boolean = false) {
        // Jika ini adalah pemuatan awal, reset semua state dan halaman ke 1
        if (isInitialLoad) {
            currentPage = 1
            _uiState.update { it.copy(games = emptyList(), isLastPage = false) }
        }

        // Mencegah pemanggilan API jika sudah di halaman terakhir atau sedang loading
        if (_uiState.value.isLastPage || _uiState.value.isLoading || _uiState.value.isLoadingMore) {
            return
        }

        viewModelScope.launch {
            // Panggil repository dengan nomor halaman saat ini
            repository.getGamesList(page = currentPage).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        // Bedakan state loading awal (seluruh layar) dengan loading berikutnya (di bawah)
                        if (currentPage == 1) {
                            _uiState.update { it.copy(isLoading = true) }
                        } else {
                            _uiState.update { it.copy(isLoadingMore = true) }
                        }
                    }
                    is Result.Success -> {
                        val newGames = result.data.results
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isLoadingMore = false,
                                // Logika Kunci: Tambahkan game baru ke daftar yang sudah ada
                                games = it.games + newGames,
                                // Jika API mengembalikan daftar kosong, kita anggap sudah halaman terakhir
                                isLastPage = newGames.isEmpty()
                            )
                        }
                        // Jika berhasil, naikkan nomor halaman untuk permintaan selanjutnya
                        currentPage++
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(isLoading = false, isLoadingMore = false, error = result.message) }
                    }
                }
            }
        }
    }
}