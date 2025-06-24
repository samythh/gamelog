package com.example.gamescatalog.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamescatalog.data.CatalogRepository
import com.example.gamescatalog.data.local.entity.User
import com.example.gamescatalog.data.preferences.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: CatalogRepository,
    private val preferences: UserPreferences
) : ViewModel() {

    // State internal yang bisa diubah di dalam ViewModel
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    // State publik yang hanya bisa dibaca (read-only) oleh UI
    val uiState = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        // Validasi input tidak boleh kosong
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Email dan password tidak boleh kosong.")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val user = repository.loginUser(email, password)
            if (user != null) {
                // Jika user ditemukan, simpan sesi ke DataStore
                preferences.saveSession(true, user.id)
                _uiState.value = AuthUiState.Success
            } else {
                // Jika user tidak ditemukan, tampilkan error
                _uiState.value = AuthUiState.Error("Email atau password salah.")
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        // Validasi input
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Semua field wajib diisi.")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            // Buat objek User baru
            val newUser = User(name = name, email = email, password = password)
            repository.registerUser(newUser)
            // Setelah berhasil register, ubah state menjadi Success
            _uiState.value = AuthUiState.Success
        }
    }

    // Fungsi untuk mereset state kembali ke Idle, berguna setelah menampilkan pesan error
    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    fun logout() {
        viewModelScope.launch {
            // Hapus sesi dari DataStore
            preferences.clearSession()
        }
    }
}