package com.example.gamescatalog.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Skema untuk tema gelap (sesuai permintaan Anda)
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryYellow,      // Warna utama untuk komponen interaktif seperti Tombol, dan Aksen
    onPrimary = TextOnLight,      // Warna teks di atas Primary (Hitam di atas Kuning)

    secondary = SecondaryYellow,  // Warna aksen sekunder
    onSecondary = TextOnLight,    // Teks di atas Secondary

    background = AppBackground,   // Warna latar belakang utama aplikasi
    onBackground = TextOnDark,    // Warna teks di atas Background (Putih di atas Hitam)

    surface = CardBackground,     // Warna latar belakang untuk Kartu dan TopAppBar
    onSurface = TextOnDark        // Warna teks di atas Kartu dan TopAppBar (Putih di atas Abu Gelap)
)

// Versi terang sebagai alternatif
private val LightColorScheme = lightColorScheme(
    primary = PrimaryYellow,
    onPrimary = TextOnLight,
    secondary = SecondaryYellow,
    onSecondary = TextOnLight,
    background = TextOnDark,      // Latar belakang "Anti-Flash White"
    onBackground = TextOnLight,
    surface = PureWhite,          // Kartu berwarna putih bersih
    onSurface = TextOnLight
)


@Composable
fun GamesCatalogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // --- UBAH BAGIAN INI ---
    // Ganti nilai default 'true' menjadi 'false' untuk menonaktifkan Dynamic Color
    // dan memaksa aplikasi untuk SELALU menggunakan skema warna kustom kita
    dynamicColor: Boolean = false, // <-- Ubah nilai ini menjadi 'false'
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Kondisi ini sekarang akan 'false' secara default, sehingga akan dilewati.
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        // Aplikasi sekarang akan masuk ke kondisi ini dan menggunakan skema kustom kita.
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}