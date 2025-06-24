package com.example.gamescatalog.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamescatalog.data.CatalogRepository
import com.example.gamescatalog.data.local.entity.BookmarkedItem
import com.example.gamescatalog.data.preferences.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
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
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}