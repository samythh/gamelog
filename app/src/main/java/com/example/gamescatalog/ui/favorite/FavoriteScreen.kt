package com.example.gamescatalog.ui.favorite

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamescatalog.ui.ViewModelFactory
import com.example.gamescatalog.ui.components.GameItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    navigateBack: () -> Unit,
    navigateToDetail: (Int) -> Unit,
) {
    val context = LocalContext.current
    val viewModel: FavoriteViewModel = viewModel(
        factory = ViewModelFactory.getInstance(context)
    )
    val bookmarkedGames by viewModel.bookmarkedGames.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Game Favorit") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (bookmarkedGames.isEmpty()) {
                // Tampilkan pesan jika tidak ada game yang difavoritkan
                Text(
                    text = "Anda belum memiliki game favorit.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(bookmarkedGames, key = { it.itemId }) { bookmarkedGame ->
                        // ---> PERBAIKAN PEMANGGILAN GameItemCard ADA DI SINI <---
                        GameItemCard(
                            name = bookmarkedGame.title, // 1. Ganti 'title' menjadi 'name'
                            imageUrl = bookmarkedGame.imageUrl,
                            // 2. Sekarang kita bisa meneruskan semua data kaya lainnya
                            rating = bookmarkedGame.rating,
                            metacriticScore = bookmarkedGame.metacritic,
                            releaseDate = bookmarkedGame.releaseDate,
                            playtime = bookmarkedGame.playtime,
                            esrbRating = bookmarkedGame.esrbRating,
                            onClick = { navigateToDetail(bookmarkedGame.itemId) }
                        )
                    }
                }
            }
        }
    }
}