// File: gamesCatalog2/app/src/main/java/com/example/gamescatalog/MainActivity.kt
package com.example.gamescatalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gamescatalog.di.Injection
import com.example.gamescatalog.ui.MainViewModel
import com.example.gamescatalog.ui.auth.LoginScreen
import com.example.gamescatalog.ui.auth.RegisterScreen
import com.example.gamescatalog.ui.detail.DetailScreen
import com.example.gamescatalog.ui.favorite.FavoriteScreen
import com.example.gamescatalog.ui.home.HomeScreen
import com.example.gamescatalog.ui.navigation.Screen
import com.example.gamescatalog.ui.profile.ProfileScreen
import com.example.gamescatalog.ui.theme.GamesCatalogTheme

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
                    val mainViewModel: MainViewModel = viewModel(
                        factory = MainViewModel.provideFactory(Injection.provideRepository(context), context)
                    )
                    val isLoggedIn by mainViewModel.isLoggedIn.collectAsState(initial = false)
                    val navController = rememberNavController()

                    LaunchedEffect(Unit) {
                        mainViewModel.checkLoginStatus()
                    }

                    val startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route

                    GamesCatalogApp(navController = navController, startDestination = startDestination)
                }
            }
        }
    }
}

@Composable
fun GamesCatalogApp(navController: NavHostController, startDestination: String) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Transisi default untuk sebagian besar layar:
        // Masuk: Layar baru masuk dari kanan (slide in dari 100% lebar)
        // Keluar: Layar lama keluar ke kiri (slide out ke -100% lebar)
        // Pop Masuk (saat kembali): Layar baru (sebelumnya) masuk dari kiri
        // Pop Keluar (saat kembali): Layar lama (saat ini) keluar ke kanan
        val slideInFromRight = slideInHorizontally(animationSpec = tween(700)) { it } + fadeIn(animationSpec = tween(700))
        val slideOutToLeft = slideOutHorizontally(animationSpec = tween(700)) { -it } + fadeOut(animationSpec = tween(700))
        val slideInFromLeft = slideInHorizontally(animationSpec = tween(700)) { -it } + fadeIn(animationSpec = tween(700))
        val slideOutToRight = slideOutHorizontally(animationSpec = tween(700)) { it } + fadeOut(animationSpec = tween(700))


        composable(
            Screen.Login.route,
            // BARIS INI DITAMBAHKAN/DIPERBAIKI: Menambahkan transisi animasi
            enterTransition = { slideInFromRight },
            exitTransition = { slideOutToLeft },
            popEnterTransition = { slideInFromLeft },
            popExitTransition = { slideOutToRight }
        ) {
            LoginScreen(
                navigateToRegister = { navController.navigate(Screen.Register.route) },
                navigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(
            Screen.Register.route,
            //Menambahkan transisi animasi
            enterTransition = { slideInFromRight },
            exitTransition = { slideOutToLeft },
            popEnterTransition = { slideInFromLeft },
            popExitTransition = { slideOutToRight }
        ) {
            RegisterScreen(
                navigateToLogin = { navController.navigate(Screen.Login.route) }
            )
        }
        composable(
            Screen.Home.route,
            //Menambahkan transisi animasi
            enterTransition = { slideInFromRight },
            exitTransition = { slideOutToLeft },
            popEnterTransition = { slideInFromLeft },
            popExitTransition = { slideOutToRight }
        ) {
            HomeScreen(
                navigateToDetail = { gameId ->
                    navController.navigate(Screen.Detail.createRoute(gameId))
                },
                navigateToProfile = { navController.navigate(Screen.Profile.route) },
                navigateToFavorite = { navController.navigate(Screen.Favorite.route) }
            )
        }
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("gameId") { type = NavType.IntType }),
            //Menambahkan transisi animasi
            enterTransition = { slideInFromRight },
            exitTransition = { slideOutToLeft },
            popEnterTransition = { slideInFromLeft },
            popExitTransition = { slideOutToRight }
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getInt("gameId") ?: return@composable
            DetailScreen(
                gameId = gameId,
                navigateBack = { navController.popBackStack() }
            )
        }
        composable(
            Screen.Profile.route,
            //Menambahkan transisi animasi
            enterTransition = { slideInFromRight },
            exitTransition = { slideOutToLeft },
            popEnterTransition = { slideInFromLeft },
            popExitTransition = { slideOutToRight }
        ) {
            ProfileScreen(
                navigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                navigateBack = { navController.popBackStack() }
            )
        }
        composable(
            Screen.Favorite.route,
            //Menambahkan transisi animasi
            enterTransition = { slideInFromRight },
            exitTransition = { slideOutToLeft },
            popEnterTransition = { slideInFromLeft },
            popExitTransition = { slideOutToRight }
        ) {
            FavoriteScreen(
                navigateBack = { navController.popBackStack() },
                navigateToDetail = { gameId ->
                    navController.navigate(Screen.Detail.createRoute(gameId))
                }
            )
        }
    }
}