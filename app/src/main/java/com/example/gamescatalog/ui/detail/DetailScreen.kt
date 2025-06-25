// File: gamesCatalog2/app/src/main/java/com/example/gamescatalog/ui/detail/DetailScreen.kt
package com.example.gamescatalog.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gamescatalog.data.Result
import com.example.gamescatalog.data.remote.response.GameDetailResponse
import com.example.gamescatalog.data.remote.response.GameScreenshotsResponse
import com.example.gamescatalog.di.Injection
import com.example.gamescatalog.ui.components.InfoChip
import com.example.gamescatalog.ui.components.MetacriticChip
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import kotlinx.coroutines.launch
import android.content.Intent
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    gameId: Int,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val detailViewModel: DetailViewModel = viewModel(
        factory = DetailViewModel.provideFactory(Injection.provideRepository(context), context)
    )
    val gameDetailState by detailViewModel.gameDetail.collectAsState()
    val gameScreenshotsState by detailViewModel.gameScreenshots.collectAsState()
    val userId by detailViewModel.userId.collectAsState()
    val isBookmarked by detailViewModel.isBookmarked.collectAsState(initial = false)

    detailViewModel.getGameDetail(gameId, userId)
    detailViewModel.checkBookmarkStatus(gameId, userId)

    Scaffold(
        topBar = {
            val titleText = when (gameDetailState) {
                is Result.Success -> (gameDetailState as Result.Success).data.name
                else -> "Detail Game"
            }
            TopAppBar(
                title = { Text(titleText) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        gameDetailState.let { result ->
                            if (result is Result.Success) {
                                detailViewModel.toggleBookmark(result.data, userId)
                            }
                        }
                    }) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isBookmarked) "Hapus dari Bookmark" else "Tambah ke Bookmark",
                            tint = if (isBookmarked) Color.Red else MaterialTheme.colorScheme.onSurface
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
                .verticalScroll(rememberScrollState())
        ) {
            when (gameDetailState) {
                is Result.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is Result.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Gagal memuat detail game: ${(gameDetailState as Result.Error).message}")
                    }
                }
                is Result.Success -> {
                    val gameDetail = (gameDetailState as Result.Success).data
                    DetailContent(
                        gameDetail = gameDetail,
                        gameScreenshotsState = gameScreenshotsState
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailContent(
    gameDetail: GameDetailResponse,
    gameScreenshotsState: Result<GameScreenshotsResponse>
) {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Column { // Main Column of DetailContent
        Box( // Main image Box
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            AsyncImage(
                model = gameDetail.backgroundImage,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black),
                            startY = 0f,
                            endY = 1000f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = gameDetail.name,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = gameDetail.rating.toString(),
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    gameDetail.metacritic?.let {
                        MetacriticChip(score = it)
                    }
                }
            }
        }

        // --- Bagian Detail Info utama dengan padding horizontal konsisten ---
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            // Info tambahan: Tanggal Rilis, Playtime, ESRB Rating, Website
            Spacer(modifier = Modifier.height(16.dp)) // Spacer awal untuk jarak dari bagian gambar atas
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                InfoChip(icon = Icons.Default.DateRange, text = gameDetail.released ?: "N/A")
                InfoChip(icon = Icons.Default.PlayArrow, text = "${gameDetail.playtime} jam")
                gameDetail.esrbRating?.name?.let {
                    InfoChip(text = it)
                }
                gameDetail.website?.let { websiteUrl ->
                    ElevatedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl))
                            context.startActivity(intent)
                        },
                        // Tidak perlu padding top jika sudah ada Spacer di atas FlowRow
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Kunjungi Website",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Kunjungi Website")
                    }
                }
            }

            // Deskripsi Game (sekarang di bawah info tanggal, dll.)
            Spacer(modifier = Modifier.height(16.dp)) // Spasi antara FlowRow info dan judul Deskripsi
            Text(
                text = "Deskripsi",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = gameDetail.description ?: "Tidak ada deskripsi tersedia.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            // Platforms
            gameDetail.platforms?.let { platforms ->
                if (platforms.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Platform",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        platforms.mapNotNull { it.platform?.name }.forEach { platformName ->
                            FilterChip(
                                selected = false,
                                onClick = { /* Tidak ada aksi untuk chip ini */ },
                                label = { Text(platformName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }

            // Genres
            gameDetail.genres?.let { genres ->
                if (genres.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Genre",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        genres.mapNotNull { it.name }.forEach { genreName ->
                            FilterChip(
                                selected = false,
                                onClick = { /* Tidak ada aksi untuk chip ini */ },
                                label = { Text(genreName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }

            // Developers
            gameDetail.developers?.let { developers ->
                if (developers.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Developer",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        developers.mapNotNull { it.name }.forEach { devName ->
                            FilterChip(
                                selected = false,
                                onClick = { /* Tidak ada aksi untuk chip ini */ },
                                label = { Text(devName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }

            // Publishers
            gameDetail.publishers?.let { publishers ->
                if (publishers.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Publisher",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        publishers.mapNotNull { it.name }.forEach { pubName ->
                            FilterChip(
                                selected = false,
                                onClick = { /* Tidak ada aksi untuk chip ini */ },
                                label = { Text(pubName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        } // Penutup Column(modifier = Modifier.padding(horizontal = 16.dp))

        // --- Bagian Screenshots ---
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Screenshots",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(8.dp))

            when (gameScreenshotsState) {
                is Result.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is Result.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Gagal memuat screenshot: ${(gameScreenshotsState as Result.Error).message}")
                    }
                }
                is Result.Success -> {
                    val screenshots = gameScreenshotsState.data.results
                    if (screenshots.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            LazyRow(
                                state = scrollState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 0.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(screenshots) { screenshot ->
                                    AsyncImage(
                                        model = screenshot.image,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .width(300.dp)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                }
                            }

                            if (scrollState.firstVisibleItemIndex > 0) {
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            val targetIndex = (scrollState.firstVisibleItemIndex - 1).coerceAtLeast(0)
                                            scrollState.animateScrollToItem(targetIndex)
                                        }
                                    },
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .padding(start = 8.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.5f))
                                ) {
                                    Icon(
                                        Icons.Default.KeyboardArrowLeft,
                                        contentDescription = "Previous screenshot",
                                        tint = Color.White
                                    )
                                }
                            }

                            val lastVisibleItemIndex = scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                            val totalItems = screenshots.size
                            if (lastVisibleItemIndex != null && lastVisibleItemIndex < totalItems - 1) {
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            val targetIndex = (scrollState.firstVisibleItemIndex + 1).coerceAtMost(totalItems - 1)
                                            scrollState.animateScrollToItem(targetIndex)
                                        }
                                    },
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .padding(end = 8.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.5f))
                                ) {
                                    Icon(
                                        Icons.Default.KeyboardArrowRight,
                                        contentDescription = "Next screenshot",
                                        tint = Color.White
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 8.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                screenshots.forEachIndexed { index, _ ->
                                    val isSelected = index == scrollState.firstVisibleItemIndex
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f))
                                    )
                                    if (index < screenshots.size - 1) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Tidak ada screenshot tersedia.")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}