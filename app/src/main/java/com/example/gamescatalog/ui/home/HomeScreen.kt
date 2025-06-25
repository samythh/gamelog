// File: gamesCatalog2/app/src/main/java/com/example/gamescatalog/ui/home/HomeScreen.kt
package com.example.gamescatalog.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamescatalog.di.Injection
import com.example.gamescatalog.ui.components.GameItemCard
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Favorite
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToDetail: (Int) -> Unit,
    navigateToProfile: () -> Unit,
    navigateToFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.provideFactory(Injection.provideRepository(context))
    )
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResult by viewModel.searchResult.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .filter { index ->
                if (index == null) return@filter false
                val totalItems = listState.layoutInfo.totalItemsCount
                val threshold = totalItems - 5
                index >= threshold
            }
            .distinctUntilChanged()
            .collect {
                viewModel.loadNextPage()
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Katalog Game",
                        modifier = Modifier.clickable {
                            viewModel.refreshGames() // Memanggil fungsi refresh
                            coroutineScope.launch {
                                listState.animateScrollToItem(0) // Menggulir ke item paling atas dengan animasi
                            }
                        }
                    )
                },
                actions = {
                    IconButton(onClick = navigateToFavorite) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorite Games",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = navigateToProfile) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profil Pengguna",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // --- BAGIAN SEARCH BAR ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                label = { Text("Cari game...") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Cari") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            // --- AKHIR BAGIAN SEARCH BAR ---

            val currentContentState = if (searchQuery.isNotBlank()) searchResult else uiState

            when (currentContentState) {
                is HomeUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is HomeUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Gagal memuat: ${currentContentState.message}")
                    }
                }
                is HomeUiState.Success -> {
                    SwipeRefresh(
                        state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
                        onRefresh = {
                            viewModel.refreshGames() // Panggil fungsi refresh di ViewModel saat ditarik
                            coroutineScope.launch {
                                listState.animateScrollToItem(0) // Menggulir ke item paling atas dengan animasi
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (currentContentState.gamesResponse.results.isNotEmpty()) {
                            LazyColumn(
                                state = listState,
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(currentContentState.gamesResponse.results) { game ->
                                    GameItemCard(
                                        name = game.name,
                                        imageUrl = game.backgroundImage ?: "",
                                        rating = game.rating,
                                        metacriticScore = game.metacritic,
                                        releaseDate = game.released,
                                        playtime = game.playtime,
                                        esrbRating = game.esrbRating?.name,
                                        onClick = { navigateToDetail(game.id) },
                                        modifier = Modifier.clickable { navigateToDetail(game.id) }
                                    )
                                }
                                if (currentContentState.isLoadingMore) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                        }
                                    }
                                }
                                if (!currentContentState.canLoadMore && !currentContentState.isLoadingMore && currentContentState.gamesResponse.results.isNotEmpty()) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Semua game telah dimuat.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = if (searchQuery.isNotBlank()) "Tidak ada hasil untuk \"$searchQuery\"" else "Tidak ada game tersedia.")
                            }
                        }
                    }
                }
            }
        }
    }
}