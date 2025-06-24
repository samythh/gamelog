package com.example.gamescatalog.data

/**
 * Sebuah sealed class untuk membungkus respons dari repository.
 * Ini membantu memodelkan status UI dengan cara yang aman dan bersih.
 */
sealed class Result<out T> {
    // Menandakan proses pengambilan data sedang berlangsung.
    data object Loading : Result<Nothing>()

    // Menandakan data berhasil diambil.
    data class Success<out T>(val data: T) : Result<T>()

    // Menandakan terjadi error saat mengambil data.
    data class Error(val message: String) : Result<Nothing>()
}