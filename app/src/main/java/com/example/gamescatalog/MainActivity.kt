package com.example.gamescatalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gamescatalog.ui.MainViewModel
import com.example.gamescatalog.ui.ViewModelFactory
import com.example.gamescatalog.ui.auth.LoginScreen
import com.example.gamescatalog.ui.auth.RegisterScreen
import com.example.gamescatalog.ui.detail.DetailScreen
import com.example.gamescatalog.ui.favorite.FavoriteScreen
import com.example.gamescatalog.ui.home.HomeScreen
import com.example.gamescatalog.ui.navigation.Screen
import com.example.gamescatalog.ui.profile.ProfileScreen
import com.example.gamescatalog.ui.theme.GamesCatalogTheme
import com.example.gamescatalog.ui.theme.YellowSplash

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GamesCatalogTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    // Mengambil MainViewModel untuk logika awal aplikasi
                    val mainViewModel: MainViewModel = viewModel(
                        factory = ViewModelFactory.getInstance(context)
                    )
                    // Mengobservasi state rute awal dari MainViewModel
                    val startDestination by mainViewModel.startDestination.collectAsState()

                    // Logika "Splash Screen":
                    // Selama startDestination masih null, ViewModel sedang memeriksa DataStore.
                    // Tampilkan indikator loading selama proses ini.
                    if (startDestination != null) {
                        // Jika rute awal sudah ditentukan, tampilkan NavHost
                        AppNavHost(startDestination = startDestination!!)
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}

/**
 * Composable yang bertanggung jawab untuk mengatur semua navigasi di aplikasi.
 * @param startDestination Rute awal yang ditentukan oleh MainViewModel (Login atau Home).
 */
@Composable
fun AppNavHost(
    startDestination: String,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    // Durasi animasi yang digunakan secara konsisten
    val animationDuration = 500

    // NavHost adalah container yang akan menampilkan screen sesuai rute saat ini.
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Mendefinisikan screen untuk rute "login"
        composable(
            route = Screen.Login.route,
            // Animasi untuk Login (bisa disesuaikan, contoh slide dari bawah)
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(animationDuration)) + fadeIn(animationSpec = tween(animationDuration))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(animationDuration)) + fadeOut(animationSpec = tween(animationDuration))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(animationDuration)) + fadeIn(animationSpec = tween(animationDuration))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(animationDuration)) + fadeOut(animationSpec = tween(animationDuration))
            }
        ) {
            LoginScreen(
                navigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                navigateToHome = {
                    // Navigasi ke home dan hapus semua backstack sebelumnya agar
                    // pengguna tidak bisa kembali ke halaman login dengan tombol back.
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        // Mendefinisikan screen untuk rute "register"
        composable(
            route = Screen.Register.route,
            // Animasi untuk Register (bisa disesuaikan, contoh slide dari bawah)
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(animationDuration)) + fadeIn(animationSpec = tween(animationDuration))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(animationDuration)) + fadeOut(animationSpec = tween(animationDuration))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(animationDuration)) + fadeIn(animationSpec = tween(animationDuration))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(animationDuration)) + fadeOut(animationSpec = tween(animationDuration))
            }
        ) {
            RegisterScreen(
                navigateToLogin = {
                    navController.navigateUp() // Kembali ke layar sebelumnya (Login)
                }
            )
        }
        // Mendefinisikan screen untuk rute "home" (Tidak ada transisi khusus saat masuk/keluar dari home secara langsung,
        // karena transisi diatur oleh layar tujuan/asal)
        composable(Screen.Home.route) {
            HomeScreen(
                navigateToDetail = { gameId ->
                    navController.navigate(Screen.Detail.createRoute(gameId))
                },
                navigateToFavorite = {
                    navController.navigate(Screen.Favorite.route)
                },
                // Teruskan aksi navigasi ke profil
                navigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        // Rute Profile Screen
        composable(
            route = Screen.Profile.route,
            enterTransition = {
                // Animasi masuk: Slide dari kanan (End) ke kiri (Start)
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(durationMillis = animationDuration)) + fadeIn(animationSpec = tween(durationMillis = animationDuration))
            },
            exitTransition = {
                // Animasi keluar: Slide ke kiri (Start)
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(durationMillis = animationDuration)) + fadeOut(animationSpec = tween(durationMillis = animationDuration))
            },
            popEnterTransition = {
                // Animasi masuk saat Pop: Slide dari kiri (Start) ke kanan (End)
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(durationMillis = animationDuration)) + fadeIn(animationSpec = tween(durationMillis = animationDuration))
            },
            popExitTransition = {
                // Animasi keluar saat Pop: Slide ke kanan (End)
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(durationMillis = animationDuration)) + fadeOut(animationSpec = tween(durationMillis = animationDuration))
            }
        ) {
            ProfileScreen(
                navigateBack = { navController.navigateUp() },
                navigateToLogin = {
                    // Navigasi ke login dan hapus semua backstack
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // Rute Favorite Screen
        composable(
            route = Screen.Favorite.route,
            enterTransition = {
                // Animasi masuk: Slide dari kanan (End) ke kiri (Start)
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(durationMillis = animationDuration)) + fadeIn(animationSpec = tween(durationMillis = animationDuration))
            },
            exitTransition = {
                // Animasi keluar: Slide ke kiri (Start)
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(durationMillis = animationDuration)) + fadeOut(animationSpec = tween(durationMillis = animationDuration))
            },
            popEnterTransition = {
                // Animasi masuk saat Pop: Slide dari kiri (Start) ke kanan (End)
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(durationMillis = animationDuration)) + fadeIn(animationSpec = tween(durationMillis = animationDuration))
            },
            popExitTransition = {
                // Animasi keluar saat Pop: Slide ke kanan (End)
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(durationMillis = animationDuration)) + fadeOut(animationSpec = tween(durationMillis = animationDuration))
            }
        ) {
            FavoriteScreen(
                navigateBack = { navController.navigateUp() },
                navigateToDetail = { gameId ->
                    navController.navigate(Screen.Detail.createRoute(gameId))
                }
            )
        }

        // Rute Detail Screen (tetap menggunakan slide horizontal)
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("gameId") { type = NavType.IntType }),
            enterTransition = { // Animasi masuk untuk DetailScreen (slide dari kanan)
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(animationDuration)) + fadeIn(animationSpec = tween(animationDuration))
            },
            exitTransition = { // Animasi keluar dari DetailScreen (slide ke kiri)
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(animationDuration)) + fadeOut(animationSpec = tween(animationDuration))
            },
            popEnterTransition = { // Animasi masuk saat kembali ke DetailScreen (dari pop, slide dari kiri)
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(animationDuration)) + fadeIn(animationSpec = tween(animationDuration))
            },
            popExitTransition = { // Animasi keluar dari DetailScreen saat pop (slide ke kanan)
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(animationDuration)) + fadeOut(animationSpec = tween(animationDuration))
            }
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("gameId") ?: -1
            // PERBAIKAN: Hapus parameter gameId dari panggilan DetailScreen.
            // DetailViewModel sudah mengambil gameId dari SavedStateHandle.
            DetailScreen(
                // gameId = id, // Baris ini dihapus
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}