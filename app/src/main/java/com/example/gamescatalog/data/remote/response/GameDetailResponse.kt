package com.example.gamescatalog.data.remote.response

import com.google.gson.annotations.SerializedName

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
    val metacritic: Int?,

    @SerializedName("playtime")
    val playtime: Int,

    @SerializedName("esrb_rating")
    val esrbRating: EsrbRating?,

    @SerializedName("website")
    val website: String?,

    @SerializedName("platforms")
    val platforms: List<Platform>?,

    @SerializedName("genres")
    val genres: List<Genre>?,

    @SerializedName("developers")
    val developers: List<Developer>?,

    @SerializedName("words")
    val publishers: List<Publisher>?
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