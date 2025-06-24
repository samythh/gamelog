package com.example.gamescatalog.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Mendefinisikan struktur tabel untuk data pengguna.
 * Setiap instance dari kelas ini merepresentasikan satu baris di dalam tabel "user_table".
 * Sesuai dengan spesifikasi proyek.
 */
@Entity(tableName = "user_table")
data class User(
    // PrimaryKey menandakan ini adalah kunci unik untuk setiap baris.
    // autoGenerate = true berarti Room akan secara otomatis membuat ID untuk setiap user baru.
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    // Kolom untuk menyimpan nama pengguna.
    @ColumnInfo(name = "name")
    val name: String,

    // Kolom untuk menyimpan email pengguna.
    @ColumnInfo(name = "email")
    val email: String,

    // Kolom untuk menyimpan password pengguna.
    @ColumnInfo(name = "password")
    val password: String
)