package com.example.gamescatalog.data.remote.response

import com.google.gson.annotations.SerializedName

data class GameScreenshotsResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("results")
    val results: List<Screenshot>
) {
    data class Screenshot(
        @SerializedName("id")
        val id: Int,
        @SerializedName("image")
        val image: String
    )
}