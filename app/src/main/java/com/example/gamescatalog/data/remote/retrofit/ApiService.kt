// File: gamesCatalog2/app/src/main/java/com/example/gamescatalog/data/remote/retrofit/ApiService.kt
package com.example.gamescatalog.data.remote.retrofit

import com.example.gamescatalog.data.remote.response.GameDetailResponse
import com.example.gamescatalog.data.remote.response.GameScreenshotsResponse
import com.example.gamescatalog.data.remote.response.GamesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Antarmuka untuk mendefinisikan endpoint API yang akan diakses menggunakan Retrofit.
 */
interface ApiService {

    /**
     * Mendapatkan daftar game.
     * @param key Kunci API untuk otentikasi.
     * @param page Nomor halaman yang akan diambil.
     * @return Objek GamesResponse yang berisi daftar game.
     */
    @GET("games")
    suspend fun getGames(
        @Query("key") key: String,
        @Query("page") page: Int = 1 // BARIS INI DITAMBAHKAN/DIPERBAIKI: Menambahkan parameter halaman dengan nilai default 1
    ): GamesResponse

    /**
     * Mendapatkan detail sebuah game berdasarkan ID.
     * @param id ID unik dari game.
     * @param key Kunci API untuk otentikasi.
     * @return Objek GameDetailResponse yang berisi detail game.
     */
    @GET("games/{id}")
    suspend fun getGameDetail(
        @Path("id") id: Int,
        @Query("key") key: String
    ): GameDetailResponse

    /**
     * Mendapatkan daftar screenshot untuk sebuah game berdasarkan ID.
     * @param gameId ID unik dari game.
     * @param key Kunci API untuk otentikasi.
     * @return Objek GameScreenshotsResponse yang berisi daftar screenshot.
     */
    @GET("games/{gameId}/screenshots")
    suspend fun getGameScreenshots(
        @Path("gameId") gameId: Int,
        @Query("key") key: String
    ): GameScreenshotsResponse

    /**
     * Mencari daftar game berdasarkan query pencarian.
     * Menggunakan endpoint yang sama dengan getGames, tetapi dengan parameter 'search'.
     * @param query String pencarian.
     * @param key Kunci API untuk otentikasi.
     * @param page Nomor halaman yang akan diambil.
     * @return Objek GamesResponse yang berisi daftar game hasil pencarian.
     */
    @GET("games")
    suspend fun searchGames(
        @Query("search") query: String,
        @Query("key") key: String,
        @Query("page") page: Int = 1
    ): GamesResponse
}