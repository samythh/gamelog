package com.example.gamescatalog.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gamescatalog.data.local.dao.BookmarkedItemDao
import com.example.gamescatalog.data.local.dao.UserDao
import com.example.gamescatalog.data.local.entity.BookmarkedItem
import com.example.gamescatalog.data.local.entity.User

// Versi database dinaikkan menjadi 3 karena ada perubahan skema (penambahan kolom di BookmarkedItem)
@Database(
    entities = [User::class, BookmarkedItem::class],
    version = 3,
    exportSchema = false
)
abstract class CatalogDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun bookmarkedItemDao(): BookmarkedItemDao

    companion object {
        @Volatile
        private var INSTANCE: CatalogDatabase? = null

        fun getDatabase(context: Context): CatalogDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CatalogDatabase::class.java,
                    "catalog_database"
                )
                    // Izinkan migrasi destruktif jika tidak ada migrasi yang ditemukan.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}