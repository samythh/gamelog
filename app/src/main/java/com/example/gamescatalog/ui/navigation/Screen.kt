// File: gamesCatalog2/app/src/main/java/com/example/gamescatalog/ui/navigation/Screen.kt

package com.example.gamescatalog.ui.navigation

// Sealed class untuk mendefinisikan semua rute navigasi secara terpusat dan aman.
sealed class Screen(val route: String) {
    // Menggunakan 'data object' untuk konsistensi di semua objek layar sederhana.
    // 'data object' adalah praktik modern di Kotlin.
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object Favorite : Screen("favorite")
    data object Profile : Screen("profile")

    // Untuk layar dengan argumen, tidak menggunakan 'data object'.
    object Detail : Screen("detail/{gameId}") {
        // Fungsi ini digunakan untuk membangun rute lengkap dengan ID yang spesifik.
        fun createRoute(gameId: Int) = "detail/$gameId"
    }


}