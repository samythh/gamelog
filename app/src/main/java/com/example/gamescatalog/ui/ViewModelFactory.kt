// File: gamesCatalog2/app/src/main/java/com/example/gamescatalog/ui/ViewModelFactory.kt
package com.example.gamescatalog.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gamescatalog.data.CatalogRepository
import com.example.gamescatalog.data.preferences.UserPreferences
import com.example.gamescatalog.di.Injection
import com.example.gamescatalog.ui.auth.AuthViewModel
import com.example.gamescatalog.ui.favorite.FavoriteViewModel
import com.example.gamescatalog.ui.home.HomeViewModel
import com.example.gamescatalog.ui.profile.ProfileViewModel

class ViewModelFactory(
    private val repository: CatalogRepository,
    private val preferences: UserPreferences
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Menggunakan 'when' untuk mencocokkan ViewModel yang diminta
        // dengan constructor yang benar.
        return when {
            // AuthViewModel membutuhkan 'repository' DAN 'preferences'.
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(repository, preferences) as T
            }
            // MainViewModel membutuhkan 'repository' sebagai parameter pertama
            // dan 'preferences' sebagai parameter kedua.
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository, preferences) as T // Baris 31
            }
            // HomeViewModel hanya butuh 'repository'.
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            // FavoriteViewModel butuh keduanya.
            modelClass.isAssignableFrom(FavoriteViewModel::class.java) -> {
                FavoriteViewModel(repository, preferences) as T
            }
            // ProfileViewModel butuh keduanya.
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(repository, preferences) as T
            }
            // Penting: DetailViewModel sengaja tidak ada di sini karena ia
            // dibuat menggunakan factory khususnya sendiri di DetailScreen.

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(
                    Injection.provideRepository(context),
                    Injection.provideUserPreferences(context)
                )
            }.also { INSTANCE = it }
    }
}