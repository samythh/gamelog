package com.example.gamescatalog.ui.auth

// Sealed interface untuk merepresentasikan semua kemungkinan state pada layar otentikasi.
sealed interface AuthUiState {
    data object Idle : AuthUiState // State awal, tidak melakukan apa-apa
    data object Loading : AuthUiState // State saat proses login/register berjalan
    data object Success : AuthUiState // State setelah operasi berhasil
    data class Error(val message: String) : AuthUiState // State jika terjadi error
}