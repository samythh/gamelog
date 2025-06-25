// File: gamesCatalog2/app/src/main/java/com/example/gamescatalog/ui/components/GameItemCard.kt

package com.example.gamescatalog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.material.icons.filled.Schedule

/**
 * Versi baru GameItemCard dengan desain vertikal yang lebih kaya dan informatif.
 */
@Composable
fun GameItemCard(
    name: String,
    imageUrl: String,
    rating: Double,
    metacriticScore: Int?, // Skor bisa null
    releaseDate: String?,   // Tanggal rilis bisa null
    playtime: Int,
    esrbRating: String?,     // Rating usia bisa null
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        // Menggunakan warna surface dari tema yang sudah kita atur (abu-abu gelap)
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column {
            // Box untuk menampung gambar dan skor Metacritic di atasnya
            Box(modifier = Modifier.height(180.dp)) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Gambar $name",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Tampilkan skor Metacritic hanya jika ada (tidak null)
                metacriticScore?.let {
                    MetacriticChip(score = it, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp))
                }
            }

            // Kolom untuk semua informasi teks di bawah gambar
            Column(modifier = Modifier.padding(16.dp)) {
                // Nama Game sebagai judul utama
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Baris pertama untuk info rating dan playtime
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoChip(icon = Icons.Default.Star, text = rating.toString())
                    InfoChip(icon = Icons.Default.Schedule, text = "$playtime jam")
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Baris kedua untuk info tanggal rilis dan rating usia
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoChip(icon = Icons.Default.DateRange, text = releaseDate ?: "N/A")
                    // Tampilkan ESRB rating hanya jika ada
                    esrbRating?.let {
                        InfoChip(text = it)
                    }
                }
            }
        }
    }
}

/**
 * Composable bantuan untuk menampilkan skor Metacritic dengan latar belakang berwarna.
 */
@Composable
fun MetacriticChip(score: Int, modifier: Modifier = Modifier) {
    // Tentukan warna berdasarkan skor
    val color = when {
        score >= 75 -> Color(0xFF66CC33) // Hijau untuk skor bagus
        score >= 50 -> Color(0xFFFFCC33) // Kuning untuk skor sedang
        else -> Color(0xFFFF0000)      // Merah untuk skor buruk
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = score.toString(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

/**
 * Composable bantuan untuk menampilkan potongan info dengan ikon.
 * BARIS INI DITAMBAHKAN/DIPERBAIKI: Menambahkan Surface untuk latar belakang dan padding.
 * Juga menambahkan parameter optional 'onClick' untuk membuat chip bisa diklik.
 */
@Composable
fun InfoChip(
    text: String,
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null, // Parameter onClick baru (nullable)
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp)) // Bentuk chip
            .background(MaterialTheme.colorScheme.surfaceVariant) // Warna latar belakang dari tema
            .let { m ->
                if (onClick != null) m.clickable(onClick = onClick) else m // Membuat chip bisa diklik jika onClick disediakan
            },
        color = MaterialTheme.colorScheme.surfaceVariant, // Warna Surface
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant // Warna konten di Surface (ikon dan teks)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), // Padding internal chip
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tampilkan ikon hanya jika disediakan
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant // Warna ikon sesuai konten Surface
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant // Warna teks sesuai konten Surface
            )
        }
    }
}