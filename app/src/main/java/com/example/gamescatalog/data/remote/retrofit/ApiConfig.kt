package com.example.gamescatalog.data.remote.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    /**
     * Fungsi untuk menyediakan instance ApiService.
     */
    fun getApiService(): ApiService {
        // Membuat builder Retrofit
        val retrofit = Retrofit.Builder()
            // Menetapkan URL dasar untuk semua endpoint API.
            .baseUrl("https://api.rawg.io/api/")
            // Menambahkan converter factory Gson untuk mengubah JSON ke objek Kotlin.
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        // Membuat implementasi dari ApiService interface.
        return retrofit.create(ApiService::class.java)
    }
}