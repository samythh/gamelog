package com.example.gamescatalog.data

import com.example.gamescatalog.BuildConfig
import com.example.gamescatalog.data.local.dao.BookmarkedItemDao
import com.example.gamescatalog.data.local.dao.UserDao
import com.example.gamescatalog.data.local.entity.BookmarkedItem
import com.example.gamescatalog.data.local.entity.User
import com.example.gamescatalog.data.remote.response.GameDetailResponse
import com.example.gamescatalog.data.remote.response.GamesResponse
import com.example.gamescatalog.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import com.google.gson.Gson // Import Gson
import com.google.gson.reflect.TypeToken // Import TypeToken untuk deserialisasi

/**
 * Repository yang mengelola semua sumber data, baik lokal maupun remote.
 * Ini adalah satu-satunya sumber kebenaran (Single Source of Truth) untuk aplikasi.
 *
 * @param userDao DAO untuk operasi pengguna.
 * @param bookmarkedItemDao DAO untuk operasi bookmark.
 * @param apiService Service untuk panggilan jaringan Retrofit.
 */
class CatalogRepository(
    private val userDao: UserDao,
    private val bookmarkedItemDao: BookmarkedItemDao,
    private val apiService: ApiService
) {
    private val gson = Gson() // Inisialisasi Gson

    // --- FUNGSI UNTUK OTENTIKASI (Menggunakan DAO) ---

    suspend fun registerUser(user: User) {
        userDao.registerUser(user)
    }

    suspend fun loginUser(email: String, password: String): User? {
        return userDao.loginUser(email, password)
    }

    fun getUserById(userId: Int): Flow<User?> {
        return userDao.getUserById(userId)
    }

    // --- FUNGSI UNTUK NETWORK CALL (Menggunakan Retrofit) ---

    /**
     * Mengambil daftar game dari API RAWG.
     * Menggunakan Flow untuk memancarkan status Loading, Success, atau Error.
     */
    fun getGamesList(page: Int): Flow<Result<GamesResponse>> = flow {
        emit(Result.Loading)
        try {
            val apiKey = BuildConfig.API_KEY
            val response = apiService.getGamesList(apiKey = apiKey, page = page)
            emit(Result.Success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }

    // --- FUNGSI UNTUK BOOKMARK (Menggunakan DAO) ---

    fun getAllBookmarks(ownerId: Int): Flow<List<BookmarkedItem>> {
        return bookmarkedItemDao.getAllBookmarks(ownerId)
    }

    suspend fun addBookmark(item: BookmarkedItem) {
        bookmarkedItemDao.addBookmark(item)
    }

    suspend fun removeBookmark(itemId: Int, ownerId: Int) {
        bookmarkedItemDao.removeBookmark(itemId, ownerId)
    }

    fun isBookmarked(itemId: Int, ownerId: Int): Flow<Boolean> {
        return bookmarkedItemDao.isBookmarked(itemId, ownerId)
    }

    /**
     * Mengubah status bookmark untuk sebuah game.
     * Jika game sudah di-bookmark, maka akan dihapus. Jika belum, akan ditambahkan.
     */
    suspend fun toggleBookmark(gameDetail: GameDetailResponse, userId: Int) {
        val isCurrentlyBookmarked = bookmarkedItemDao.isBookmarked(gameDetail.id, userId).first()
        if (isCurrentlyBookmarked) {
            bookmarkedItemDao.removeBookmark(gameDetail.id, userId)
        } else {
            // Konversi List of Objects menjadi JSON String menggunakan Gson
            val platformsJson = gson.toJson(gameDetail.platforms?.map { it.platform?.name })
            val genresJson = gson.toJson(gameDetail.genres?.map { it.name })
            val developersJson = gson.toJson(gameDetail.developers?.map { it.name })
            val publishersJson = gson.toJson(gameDetail.publishers?.map { it.name })

            val bookmarkedItem = BookmarkedItem(
                itemId = gameDetail.id,
                title = gameDetail.name,
                imageUrl = gameDetail.backgroundImage ?: "",
                rating = gameDetail.rating,
                metacritic = gameDetail.metacritic,
                releaseDate = gameDetail.released,
                playtime = gameDetail.playtime,
                esrbRating = gameDetail.esrbRating?.name,
                descriptionRaw = gameDetail.description, // Menambahkan deskripsi
                websiteUrl = gameDetail.website, // Menambahkan website
                platformsJson = platformsJson, // Menyimpan JSON platforms
                genresJson = genresJson, // Menyimpan JSON genres
                developersJson = developersJson, // Menyimpan JSON developers
                publishersJson = publishersJson, // Menyimpan JSON publishers
                ownerId = userId
            )
            bookmarkedItemDao.addBookmark(bookmarkedItem)
        }
    }

    // Fungsi untuk mendapatkan detail game, kini juga memeriksa bookmark
    fun getGameDetail(id: Int, userId: Int): Flow<Result<GameDetailResponse>> = flow {
        emit(Result.Loading)
        try {
            // Pertama, coba ambil dari database lokal (jika sudah di-bookmark)
            val bookmarkedItemFlow = bookmarkedItemDao.getBookmarkById(id, userId)
            val bookmarkedItem = bookmarkedItemFlow.first() // Ambil nilai pertama

            if (bookmarkedItem != null) {
                // Jika ditemukan di bookmark, konversi kembali ke GameDetailResponse
                val platformsType = object : TypeToken<List<String>>() {}.type
                val genresType = object : TypeToken<List<String>>() {}.type
                val developersType = object : TypeToken<List<String>>() {}.type
                val publishersType = object : TypeToken<List<String>>() {}.type

                val platforms = bookmarkedItem.platformsJson?.let { json -> gson.fromJson<List<String>>(json, platformsType) } ?: emptyList()
                val genres = bookmarkedItem.genresJson?.let { json -> gson.fromJson<List<String>>(json, genresType) } ?: emptyList()
                val developers = bookmarkedItem.developersJson?.let { json -> gson.fromJson<List<String>>(json, developersType) } ?: emptyList()
                val publishers = bookmarkedItem.publishersJson?.let { json -> gson.fromJson<List<String>>(json, publishersType) } ?: emptyList()

                val gameDetailFromBookmark = GameDetailResponse(
                    id = bookmarkedItem.itemId,
                    name = bookmarkedItem.title,
                    released = bookmarkedItem.releaseDate,
                    backgroundImage = bookmarkedItem.imageUrl,
                    rating = bookmarkedItem.rating,
                    metacritic = bookmarkedItem.metacritic,
                    playtime = bookmarkedItem.playtime,
                    esrbRating = if (bookmarkedItem.esrbRating != null) GameDetailResponse.EsrbRating(null, bookmarkedItem.esrbRating, null) else null,
                    description = bookmarkedItem.descriptionRaw,
                    website = bookmarkedItem.websiteUrl,
                    platforms = platforms.map { GameDetailResponse.Platform(GameDetailResponse.PlatformX(null, it, null)) }, // Konversi kembali
                    genres = genres.map { GameDetailResponse.Genre(null, it, null) }, // Konversi kembali
                    developers = developers.map { GameDetailResponse.Developer(null, it) }, // Konversi kembali
                    publishers = publishers.map { GameDetailResponse.Publisher(null, it) } // Konversi kembali
                )
                emit(Result.Success(gameDetailFromBookmark))
            } else {
                // Jika tidak ada di bookmark, ambil dari API
                val apiKey = BuildConfig.API_KEY
                val response = apiService.getGameDetail(id, apiKey)
                emit(Result.Success(response))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }

    // Companion object untuk menyediakan instance tunggal dari Repository (Singleton).
    companion object {
        @Volatile
        private var instance: CatalogRepository? = null

        fun getInstance(
            userDao: UserDao,
            bookmarkedItemDao: BookmarkedItemDao,
            apiService: ApiService
        ): CatalogRepository =
            instance ?: synchronized(this) {
                instance ?: CatalogRepository(userDao, bookmarkedItemDao, apiService)
            }.also { instance = it }
    }
}