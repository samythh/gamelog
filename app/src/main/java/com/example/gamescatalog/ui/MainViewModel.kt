// File: gamesCatalog2/app/src/main/java/com/example/gamescatalog/ui/MainViewModel.kt
package com.example.gamescatalog.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gamescatalog.data.CatalogRepository
import com.example.gamescatalog.data.preferences.UserPreferences
import com.example.gamescatalog.di.Injection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: CatalogRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    fun checkLoginStatus() {
        viewModelScope.launch {
            _isLoggedIn.value = userPreferences.getSession().first().second != 0
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.clearSession()
            _isLoggedIn.value = false
        }
    }

    companion object {
        fun provideFactory(
            repository: CatalogRepository,
            context: Context
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                        // ke Injection.provideUserPreferences.
                        return MainViewModel(repository, Injection.provideUserPreferences(context)) as T // Baris 57
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}