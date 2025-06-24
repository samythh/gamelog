package com.example.gamescatalog.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamescatalog.data.preferences.UserPreferences
import com.example.gamescatalog.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(private val preferences: UserPreferences) : ViewModel() {

    // State untuk menyimpan rute awal, null berarti sedang loading.
    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination = _startDestination.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            // Ambil data sesi dari DataStore. .first() mengambil nilai pertama lalu berhenti.
            val (isLoggedIn, _) = preferences.getSession().first()
            if (isLoggedIn) {
                // Jika sudah login, rute awal adalah Home.
                _startDestination.value = Screen.Home.route
            } else {
                // Jika belum, rute awal adalah Login.
                _startDestination.value = Screen.Login.route
            }
        }
    }
}