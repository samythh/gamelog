package com.example.gamescatalog.data.remote.response

import com.google.gson.annotations.SerializedName

// Pastikan kelas ini mendefinisikan semua properti dengan @SerializedName yang benar.
data class GameDetailResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("released")
    val released: String?,

    @SerializedName("background_image")
    val backgroundImage: String?,

    @SerializedName("rating")
    val rating: Double,

    @SerializedName("description_raw")
    val description: String?,

    @SerializedName("metacritic")
    val metacritic: Int?, // Dibuat nullable untuk konsistensi dengan BookmarkedItem

    @SerializedName("playtime")
    val playtime: Int,

    @SerializedName("esrb_rating")
    val esrbRating: EsrbRating?,

    // --- PROPERTI BARU YANG DITAMBAHKAN ---
    @SerializedName("website")
    val website: String?,

    @SerializedName("platforms")
    val platforms: List<Platform>?,

    @SerializedName("genres")
    val genres: List<Genre>?,

    @SerializedName("developers")
    val developers: List<Developer>?,

    @SerializedName("words") // Ini umumnya untuk penerbit
    val publishers: List<Publisher>? // Properti ini biasanya bernama 'publishers' atau serupa, saya asumsikan 'words' untuk saat ini berdasarkan kebutuhan di DetailScreen
) {
    // --- NESTED DATA CLASS UNTUK STRUKTUR API ---

    data class EsrbRating(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("slug")
        val slug: String?
    )

    data class Platform(
        @SerializedName("platform")
        val platform: PlatformX?, // Properti 'platform' berisi objek PlatformX
        // properti lain seperti 'released_at', 'requirements_en', dll. bisa ditambahkan jika perlu
    )

    data class PlatformX( // Detail sebenarnya dari platform
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("slug")
        val slug: String?
    )

    data class Genre(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("slug")
        val slug: String?
    )

    data class Developer(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?
    )

    data class Publisher(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?
    )
}