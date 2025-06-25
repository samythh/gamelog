// File: gamesCatalog2/app/src/main/java/com/example/gamescatalog/ui/favorite/FavoriteViewModel.kt
package com.example.gamescatalog.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamescatalog.data.CatalogRepository
import com.example.gamescatalog.data.Result
import com.example.gamescatalog.data.local.entity.BookmarkedItem
import com.example.gamescatalog.data.preferences.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class FavoriteViewModel(
    repository: CatalogRepository,
    preferences: UserPreferences
) : ViewModel() {

    // Mengambil daftar bookmark secara reaktif.
    // flatMapLatest akan otomatis memanggil ulang query ke database
    // jika userId berubah (meskipun dalam kasus ini tidak akan berubah selama sesi).
    val bookmarkedGames: StateFlow<List<BookmarkedItem>> =
        preferences.getSession().flatMapLatest { (_, userId) ->
            repository.getAllBookmarks(userId)
            // Setelah mendapatkan Flow<Result<List<BookmarkedItem>>>,
            // kita perlu mengubahnya menjadi Flow<List<BookmarkedItem>>.
            // Gunakan operator 'map' untuk mengekstrak data dari Result.
            // Jika Result adalah Success, ambil datanya.
            // Jika Result adalah Loading atau Error, kembalikan daftar kosong
            // agar StateFlow selalu memiliki List<BookmarkedItem>.
        }.map { result ->
            when (result) {
                is Result.Success -> result.data
                is Result.Loading -> emptyList() // Mengembalikan daftar kosong saat memuat
                is Result.Error -> emptyList() // Mengembalikan daftar kosong saat terjadi kesalahan
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}