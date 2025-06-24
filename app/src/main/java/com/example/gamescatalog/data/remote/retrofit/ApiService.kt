// File: data/remote/retrofit/ApiService.kt

package com.example.gamescatalog.data.remote.retrofit

import com.example.gamescatalog.data.remote.response.GameDetailResponse
import com.example.gamescatalog.data.remote.response.GamesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    /**
     * Mengambil detail dari satu game berdasarkan ID-nya.
     * @Path("id") akan menggantikan {id} di URL dengan nilai dari parameter id.
     */
    @GET("games/{id}")
    suspend fun getGameDetail(
        @Path("id") id: Int,
        @Query("key") apiKey: String
    ): GameDetailResponse

    /**
     * Fungsi untuk mengambil daftar game.
     * @GET("games") menandakan metode HTTP GET ke endpoint "games".
     * @Query("key") akan menambahkan parameter "?key=YOUR_API_KEY" ke URL.
     * Ditandai 'suspend' karena ini adalah network call yang harus berjalan di coroutine.
     */
    @GET("games")
    suspend fun getGamesList(
        @Query("key") apiKey: String,
        // ---> PENAMBAHAN ADA DI SINI <---
        // Menambahkan parameter 'page' untuk memberi tahu API halaman mana yang akan diambil.
        @Query("page") page: Int
    ): GamesResponse
}