package com.example.gamescatalog.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gamescatalog.data.local.entity.User
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object untuk entitas User.
 * Berisi metode-metode untuk berinteraksi dengan tabel pengguna di database.
 */
@Dao
interface UserDao {

    /**
     * Menyisipkan satu pengguna baru ke dalam tabel.
     * onConflict = OnConflictStrategy.IGNORE berarti jika ada email yang sama,
     * operasi insert akan diabaikan untuk mencegah duplikat.
     * Ditandai 'suspend' karena operasi database harus dijalankan di luar main thread.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun registerUser(user: User)

    /**
     * Mencari pengguna berdasarkan email dan password untuk proses login.
     * Mengembalikan satu objek User jika ditemukan, atau null jika tidak cocok.
     */
    @Query("SELECT * FROM user_table WHERE email = :email AND password = :password LIMIT 1")
    suspend fun loginUser(email: String, password: String): User?

    /**
     * Mengambil data pengguna berdasarkan ID-nya.
     * Digunakan untuk halaman profil.
     * Menggunakan Flow agar UI bisa update otomatis jika ada perubahan data user.
     */
    @Query("SELECT * FROM user_table WHERE id = :userId")
    fun getUserById(userId: Int): Flow<User?>
}