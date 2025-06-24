package com.example.gamescatalog.ui.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gamescatalog.data.Result // Import Result masih diperlukan untuk uiState
import com.example.gamescatalog.data.remote.response.GameDetailResponse
import com.example.gamescatalog.di.Injection
import com.example.gamescatalog.ui.theme.DarkGreen
import com.example.gamescatalog.ui.theme.LightGreen
import com.example.gamescatalog.ui.theme.Red80
import com.google.gson.Gson // Gson masih diperlukan untuk deserialisasi dari BookmarkedItem
import com.google.gson.reflect.TypeToken // TypeToken masih diperlukan untuk deserialisasi dari BookmarkedItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: DetailViewModel = viewModel(
        factory = DetailViewModel.provideFactory(
            repository = Injection.provideRepository(context),
            preferences = Injection.provideUserPreferences(context)
        )
    )
    val uiState by viewModel.uiState.collectAsState()
    // Hapus baris screenshotsState jika tidak digunakan, agar tidak ada import yang tidak perlu:
    // val screenshotsState by viewModel.screenshots.collectAsState() // Dihapus

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Detail Game", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                        )
                    }
                },
                // Mengembalikan warna TopAppBar ke default MaterialTheme
                // Colors properties dihilangkan untuk menggunakan default theme
            )
        },
        floatingActionButton = {
            if (uiState is DetailUiState.Success) {
                val successState = uiState as DetailUiState.Success
                FloatingActionButton(
                    onClick = {
                        viewModel.toggleBookmark(successState.gameDetail)
                    },
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ) {
                    Icon(
                        imageVector = if (successState.isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Bookmark",
                        tint = if (successState.isBookmarked) Red80 else MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is DetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            is DetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Gagal memuat: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
            is DetailUiState.Success -> {
                DetailContent(
                    modifier = modifier.padding(innerPadding),
                    game = state.gameDetail,
                    // Hapus parameter screenshotsState jika tidak digunakan:
                    // screenshotsState = screenshotsState
                )
            }
        }
    }
}


@Composable
private fun DetailContent(
    modifier: Modifier = Modifier,
    game: GameDetailResponse
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val gson = Gson() // Gson masih diperlukan karena deserialisasi JSON di CatalogRepository

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp) // Padding keseluruhan konten
    ) {
        // Gambar Latar Belakang Game
        AsyncImage(
            model = game.backgroundImage,
            contentDescription = "Gambar ${game.name}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(8.dp)) // Membulatkan sudut gambar
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Judul Game
        Text(
            text = game.name,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // --- Bagian Informasi Utama (Rating, Rilis, Metacritic, Playtime) dalam Card ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) // Warna card
        ) {
            Column(Modifier.padding(16.dp)) {
                // Rating dan Tanggal Rilis
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)) {
                                append("Rating: ")
                            }
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                                append("${game.rating}")
                            }
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)) {
                                append("Rilis: ")
                            }
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                                append("${game.released ?: "N/A"}")
                            }
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp) // Pembatas
                Spacer(modifier = Modifier.height(8.dp))

                // Metacritic dan Playtime
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    game.metacritic?.let {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)) {
                                    append("Metacritic: ")
                                }
                                withStyle(style = SpanStyle(color = when {
                                    it >= 75 -> DarkGreen
                                    it >= 50 -> LightGreen
                                    else -> Color.Red
                                })) {
                                    append("$it")
                                }
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)) {
                                append("Playtime: ")
                            }
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                                append("${game.playtime} jam")
                            }
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // ESRB Rating (jika ada)
        game.esrbRating?.name?.let { esrb ->
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)) {
                        append("ESRB Rating: ")
                    }
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                        append(esrb)
                    }
                },
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- Bagian Deskripsi ---
        Text(
            text = "Deskripsi",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = game.description?.replace(Regex("<.*?>"), "") ?: "Tidak ada deskripsi.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- Bagian Informasi Tambahan (Genre, Platform, Developer, Publisher) ---
        // Fungsi pembantu untuk menampilkan daftar item
        @Composable
        fun InfoSection(title: String, items: List<String>?) {
            items?.takeIf { it.isNotEmpty() }?.let {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = it.joinToString(", "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        InfoSection("Platforms", game.platforms?.mapNotNull { it.platform?.name })
        InfoSection("Genres", game.genres?.mapNotNull { it.name })
        InfoSection("Developers", game.developers?.mapNotNull { it.name })
        InfoSection("Publishers", game.publishers?.mapNotNull { it.name })


        // Tombol Website (jika ada)
        game.website?.takeIf { it.isNotBlank() }?.let { url ->
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Icon(imageVector = Icons.Default.Language, contentDescription = "Kunjungi Website", tint = MaterialTheme.colorScheme.onSecondaryContainer)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Kunjungi Website Resmi", color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }

        // Memberi ruang di bagian bawah agar FloatingActionButton tidak menutupi konten
        Spacer(modifier = Modifier.height(80.dp))
    }
}