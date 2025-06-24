// File: data/remote/response/GameResponse.kt
// Salin dan ganti seluruh isi file Anda dengan kode di bawah ini untuk memperbaikinya.

package com.example.gamescatalog.data.remote.response

import com.google.gson.annotations.SerializedName

// Kelas ini tidak perlu diubah.
data class GamesResponse(
    @field:SerializedName("results")
    val results: List<Game>
)

// Kelas ini sudah Anda perbarui, pastikan semua properti ada.
data class Game(
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("background_image")
    val backgroundImage: String?,

    @field:SerializedName("rating")
    val rating: Double,

    @field:SerializedName("released")
    val released: String?,

    @field:SerializedName("genres")
    val genres: List<Genre>, // Sekarang 'Genre' akan dikenali

    @field:SerializedName("esrb_rating")
    val esrbRating: EsrbRating?, // Sekarang 'EsrbRating' akan dikenali

    @field:SerializedName("metacritic")
    val metacritic: Int?,

    @field:SerializedName("playtime")
    val playtime: Int
)


// --- BAGIAN YANG KEMUNGKINAN HILANG ADA DI SINI ---
// Pastikan dua data class di bawah ini ada di dalam file yang sama.

/**
 * Kelas data untuk merepresentasikan satu objek genre dari API.
 * Contoh dari API: { "id": 4, "name": "Action", ... }
 */
data class Genre(
    @field:SerializedName("name")
    val name: String
)

/**
 * Kelas data untuk merepresentasikan objek rating usia (ESRB) dari API.
 * Contoh dari API: { "id": 4, "name": "Mature 17+", ... }
 */
data class EsrbRating(
    @field:SerializedName("name")
    val name: String
)