// File: ui/home/HomeScreen.kt

package com.example.gamescatalog.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamescatalog.ui.ViewModelFactory
import com.example.gamescatalog.ui.components.GameItemCard
// ---> Menggunakan import untuk API PullToRefresh yang baru dan benar <---
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    // Nanti akan kita gunakan untuk navigasi ke halaman detail
    navigateToDetail: (Int) -> Unit,
    navigateToFavorite: () -> Unit,
    navigateToProfile: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel(
        factory = ViewModelFactory.getInstance(context)
    )
    val uiState by viewModel.uiState.collectAsState()

    // --- LOGIKA UNTUK PULL TO REFRESH & INFINITE SCROLL ---

    // 1. State untuk PullToRefresh
    val pullToRefreshState = rememberPullToRefreshState()
    if (pullToRefreshState.isRefreshing) {
        // Memicu refresh saat pengguna menarik layar
        LaunchedEffect(true) {
            viewModel.fetchGames(isInitialLoad = true)
        }
    }
    // Menghentikan animasi refresh saat data selesai dimuat
    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            pullToRefreshState.endRefresh()
        }
    }

    // 2. State untuk Infinite Scroll
    val lazyListState = rememberLazyListState()
    // State turunan untuk mendeteksi apakah pengguna sudah di akhir daftar
    val isAtBottom by remember {
        derivedStateOf {
            val lastVisibleItem = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index != null && lastVisibleItem.index >= lazyListState.layoutInfo.totalItemsCount - 5
        }
    }
    // Memicu pemuatan halaman berikutnya saat pengguna mencapai akhir daftar
    LaunchedEffect(isAtBottom) {
        if (isAtBottom && !uiState.isLoadingMore) {
            viewModel.fetchGames(isInitialLoad = false)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Katalog Game") },
                actions = {
                    IconButton(onClick = navigateToFavorite) {
                        Icon(
                            imageVector = Icons.Filled.Favorite, // Gunakan Icons.Filled.Favorite
                            contentDescription = "Halaman Favorit",
                            tint = MaterialTheme.colorScheme.primary // Sesuaikan warna ikon
                        )
                    }
                    IconButton(onClick = navigateToProfile) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Halaman Profil",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        // Box pembungkus sekarang menggunakan modifier .nestedScroll
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            // Jika sedang loading DAN daftar game masih kosong, tampilkan progress indicator di tengah layar
            if (uiState.isLoading && uiState.games.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            // Jika ada error DAN daftar game masih kosong, tampilkan pesan error
            else if (uiState.error != null && uiState.games.isEmpty()) {
                Column(/*...*/) { /*...*/ }
            }
            // Jika data berhasil dimuat, tampilkan daftar game
            else {
                LazyColumn(
                    state = lazyListState, // <-- Hubungkan LazyListState di sini
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.games, key = { it.id }) { game ->
                        // Menggunakan GameItemCard yang sudah direfactor
                        GameItemCard(
                            // 1. Ganti nama parameter 'title' menjadi 'name'
                            name = game.name,
                            imageUrl = game.backgroundImage ?: "",
                            // 2. Teruskan semua data lain yang sekarang dibutuhkan oleh GameItemCard
                            rating = game.rating,
                            metacriticScore = game.metacritic,
                            releaseDate = game.released,
                            playtime = game.playtime,
                            esrbRating = game.esrbRating?.name,
                            onClick = { navigateToDetail(game.id) }
                        )
                    }
                    // --- FOOTER SEKARANG HANYA UNTUK INDIKATOR LOADING ---
                    item {
                        if (uiState.isLoadingMore) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }

            // Indikator Pull-to-Refresh sekarang menggunakan PullToRefreshContainer
            PullToRefreshContainer(
                state = pullToRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }
    }
}