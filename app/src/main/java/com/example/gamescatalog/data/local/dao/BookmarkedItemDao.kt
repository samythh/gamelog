package com.example.gamescatalog.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gamescatalog.data.local.entity.BookmarkedItem
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkedItemDao {
    @Query("SELECT * FROM bookmarked_item_table WHERE owner_id = :ownerId")
    fun getAllBookmarks(ownerId: Int): Flow<List<BookmarkedItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(item: BookmarkedItem)

    @Query("DELETE FROM bookmarked_item_table WHERE item_id = :itemId AND owner_id = :ownerId")
    suspend fun removeBookmark(itemId: Int, ownerId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarked_item_table WHERE item_id = :itemId AND owner_id = :ownerId)")
    fun isBookmarked(itemId: Int, ownerId: Int): Flow<Boolean>

    // PERBAIKAN: Menambahkan query untuk mendapatkan bookmark berdasarkan ID game dan owner ID
    @Query("SELECT * FROM bookmarked_item_table WHERE item_id = :itemId AND owner_id = :ownerId LIMIT 1")
    fun getBookmarkById(itemId: Int, ownerId: Int): Flow<BookmarkedItem?>
}