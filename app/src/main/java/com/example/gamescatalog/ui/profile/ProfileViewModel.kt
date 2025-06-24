package com.example.gamescatalog.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamescatalog.data.CatalogRepository
import com.example.gamescatalog.data.local.entity.User
import com.example.gamescatalog.data.preferences.UserPreferences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// State holder untuk ProfileScreen
data class ProfileUiState(
    val user: User? = null
)

class ProfileViewModel(
    repository: CatalogRepository,
    private val preferences: UserPreferences
) : ViewModel() {

    // State untuk menampung data user
    val uiState: StateFlow<ProfileUiState> =
        preferences.getSession().flatMapLatest { (_, userId) ->
            repository.getUserById(userId)
        }.map { user ->
            ProfileUiState(user = user)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProfileUiState()
        )

    // Channel untuk mengirim event sekali jalan, seperti event setelah logout
    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent = _logoutEvent.asSharedFlow()

    /**
     * Menjalankan proses logout dengan menghapus sesi dari DataStore.
     */
    fun logout() {
        viewModelScope.launch {
            preferences.clearSession()
            // Kirim event bahwa logout telah selesai
            _logoutEvent.emit(Unit)
        }
    }
}