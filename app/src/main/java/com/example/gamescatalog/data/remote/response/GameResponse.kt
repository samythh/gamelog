// File: gamesCatalog2/app/src/main/java/com/example/gamescatalog/data/remote/response/GameResponse.kt
package com.example.gamescatalog.data.remote.response

import com.google.gson.annotations.SerializedName

data class GamesResponse(
    // Ini sangat penting karena API Rawg mengembalikan total hitungan item di sini,
    // dan ViewModel Anda mencoba mengaksesnya.
    @field:SerializedName("count")
    val count: Int,

    @field:SerializedName("results")
    val results: List<Game>
)

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
    val genres: List<Genre>,

    @field:SerializedName("esrb_rating")
    val esrbRating: EsrbRating?,

    @field:SerializedName("metacritic")
    val metacritic: Int?,

    @field:SerializedName("playtime")
    val playtime: Int
)

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