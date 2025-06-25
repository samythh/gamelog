package com.example.gamescatalog.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Mendefinisikan struktur tabel untuk item yang di-bookmark.
 * Menambahkan kolom-kolom baru untuk menyimpan lebih banyak detail game.
 */
@Entity(tableName = "bookmarked_item_table")
data class BookmarkedItem(
    @PrimaryKey
    @ColumnInfo(name = "item_id")
    val itemId: Int, // ID unik game

    @ColumnInfo(name = "title")
    val title: String, // Judul game

    @ColumnInfo(name = "image_url")
    val imageUrl: String, // URL gambar latar belakang

    @ColumnInfo(name = "rating")
    val rating: Double, // Rating game

    @ColumnInfo(name = "metacritic")
    val metacritic: Int?, // Skor Metacritic

    @ColumnInfo(name = "release_date")
    val releaseDate: String?, // Tanggal rilis

    @ColumnInfo(name = "playtime")
    val playtime: Int, // Waktu bermain rata-rata

    @ColumnInfo(name = "esrb_rating")
    val esrbRating: String?, // Rating ESRB (nama)

    @ColumnInfo(name = "description_raw")
    val descriptionRaw: String?, // Deskripsi game mentah (HTML)

    @ColumnInfo(name = "website_url")
    val websiteUrl: String?, // URL website resmi game

    // Karena platforms, genres, developers, publishers adalah List/Array,
    // Akan disimpan sebagai String JSON untuk kesederhanaan.
    @ColumnInfo(name = "platforms_json")
    val platformsJson: String?,

    @ColumnInfo(name = "genres_json")
    val genresJson: String?,

    @ColumnInfo(name = "developers_json")
    val developersJson: String?,

    @ColumnInfo(name = "publishers_json")
    val publishersJson: String?,

    @ColumnInfo(name = "owner_id", index = true)
    val ownerId: Int // ID pemilik bookmark
)