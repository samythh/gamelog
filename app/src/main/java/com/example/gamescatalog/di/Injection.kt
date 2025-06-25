package com.example.gamescatalog.di

import android.content.Context
import com.example.gamescatalog.data.CatalogRepository
import com.example.gamescatalog.data.local.CatalogDatabase
import com.example.gamescatalog.data.preferences.UserPreferences
import com.example.gamescatalog.data.preferences.dataStore
import com.example.gamescatalog.data.remote.retrofit.ApiConfig

object Injection {
    /**
     * Menyediakan instance tunggal dari CatalogRepository.
     */
    fun provideRepository(context: Context): CatalogRepository {
        val database = CatalogDatabase.getDatabase(context) // Perbaikan
        val apiService = ApiConfig.getApiService()
        return CatalogRepository.getInstance(database.userDao(), database.bookmarkedItemDao(), apiService)
    }

    /**
     * Menyediakan instance tunggal dari UserPreferences.
     */
    fun provideUserPreferences(context: Context): UserPreferences {
        return UserPreferences.getInstance(context.dataStore)
    }
}