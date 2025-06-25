// File: gamesCatalog2/app/src/main/java/com/example/gamescatalog/data/CatalogRepository.kt
package com.example.gamescatalog.data

import com.example.gamescatalog.BuildConfig
import com.example.gamescatalog.data.local.dao.BookmarkedItemDao
import com.example.gamescatalog.data.local.dao.UserDao
import com.example.gamescatalog.data.local.entity.BookmarkedItem
import com.example.gamescatalog.data.local.entity.User
import com.example.gamescatalog.data.remote.response.GameDetailResponse
import com.example.gamescatalog.data.remote.response.GameScreenshotsResponse
import com.example.gamescatalog.data.remote.response.GamesResponse
import com.example.gamescatalog.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CatalogRepository(
    private val userDao: UserDao,
    private val bookmarkedItemDao: BookmarkedItemDao,
    private val apiService: ApiService
) {
    private val gson = Gson()

    /**
     * Mendaftarkan pengguna baru ke database lokal.
     * @param user Objek User yang akan didaftarkan.
     */
    suspend fun registerUser(user: User) {
        userDao.registerUser(user)
    }

    /**
     * Melakukan login pengguna dengan email dan password.
     * Mencari pengguna di database lokal.
     * @param email Email pengguna.
     * @param password Password pengguna.
     * @return Objek User jika kredensial benar, null jika tidak ditemukan atau password salah.
     */
    suspend fun loginUser(email: String, password: String): User? {
        return userDao.loginUser(email, password)
    }

    /**
     * Mendapatkan pengguna berdasarkan ID-nya dari database lokal.
     * @param userId ID pengguna.
     * @return Flow dari objek User jika ditemukan, null jika tidak.
     */
    fun getUserById(userId: Int): Flow<User?> {
        return userDao.getUserById(userId)
    }

    /**
     * Mendapatkan daftar game dari API.
     * @param page Nomor halaman yang akan diambil.
     * @return Flow dari Result yang berisi GamesResponse.
     */
    fun getGamesList(page: Int): Flow<Result<GamesResponse>> = flow {
        emit(Result.Loading)
        try {
            val apiKey = BuildConfig.API_KEY
            val response = apiService.getGames(key = apiKey, page = page) // BARIS INI DITAMBAHKAN/DIPERBAIKI: Meneruskan parameter 'page'
            emit(Result.Success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }

    /**
     * Mencari game dari API berdasarkan query.
     * @param query Kata kunci pencarian.
     * @param page Nomor halaman yang akan diambil.
     * @return Flow dari Result yang berisi GamesResponse.
     */

    fun searchGames(query: String, page: Int): Flow<Result<GamesResponse>> = flow {
        emit(Result.Loading)
        try {
            val apiKey = BuildConfig.API_KEY
            val response = apiService.searchGames(query = query, key = apiKey, page = page) // BARIS INI DITAMBAHKAN/DIPERBAIKI: Meneruskan parameter 'page'
            emit(Result.Success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getGameDetail(id: Int, userId: Int): Flow<Result<GameDetailResponse>> = flow {
        emit(Result.Loading)
        try {
            val bookmarkedItemFlow = bookmarkedItemDao.getBookmarkById(id, userId)
            val bookmarkedItem = bookmarkedItemFlow.first()

            if (bookmarkedItem != null) {
                val platformsType = object : TypeToken<List<String>>() {}.type
                val genresType = object : TypeToken<List<String>>() {}.type
                val developersType = object : TypeToken<List<String>>() {}.type
                val publishersType = object : TypeToken<List<String>>() {}.type

                val platformsList = bookmarkedItem.platformsJson?.let { json -> gson.fromJson<List<String>>(json, platformsType) } ?: emptyList()
                val genresList = bookmarkedItem.genresJson?.let { json -> gson.fromJson<List<String>>(json, genresType) } ?: emptyList()
                val developersList = bookmarkedItem.developersJson?.let { json -> gson.fromJson<List<String>>(json, developersType) } ?: emptyList()
                val publishersList = bookmarkedItem.publishersJson?.let { json -> gson.fromJson<List<String>>(json, publishersType) } ?: emptyList()

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
                    platforms = platformsList.map { GameDetailResponse.Platform(platform = GameDetailResponse.PlatformX(null, it, null)) },
                    genres = genresList.map { GameDetailResponse.Genre(null, it, null) },
                    developers = developersList.map { GameDetailResponse.Developer(null, it) },
                    publishers = publishersList.map { GameDetailResponse.Publisher(null, it) }
                )
                emit(Result.Success(gameDetailFromBookmark))
            } else {
                val apiKey = BuildConfig.API_KEY
                val response = apiService.getGameDetail(id, apiKey)
                emit(Result.Success(response))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }

    /**
     * Mendapatkan daftar screenshot untuk sebuah game berdasarkan ID.
     * @param gameId ID unik dari game.
     * @return Flow dari Result yang berisi GameScreenshotsResponse.
     */
    fun getGameScreenshots(gameId: Int): Flow<Result<GameScreenshotsResponse>> = flow {
        emit(Result.Loading)
        try {
            val apiKey = BuildConfig.API_KEY
            val response = apiService.getGameScreenshots(gameId, apiKey)
            emit(Result.Success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }

    /**
     * Mendapatkan semua item yang di-bookmark oleh pengguna dari database lokal.
     * @param userId ID pengguna yang sedang login.
     * @return Flow dari Result yang berisi daftar BookmarkedItem.
     */
    fun getAllBookmarks(userId: Int): Flow<Result<List<BookmarkedItem>>> = flow {
        emit(Result.Loading)
        try {
            val bookmarks = bookmarkedItemDao.getAllBookmarks(userId).first()
            emit(Result.Success(bookmarks))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }

    /**
     * Memeriksa apakah suatu game sudah di-bookmark oleh pengguna.
     * @param itemId ID game.
     * @param userId ID pengguna.
     * @return Boolean, true jika sudah di-bookmark, false jika tidak.
     */
    fun isBookmarked(itemId: Int, userId: Int): Flow<Boolean> = flow {
        val bookmark = bookmarkedItemDao.getBookmarkById(itemId, userId).first()
        emit(bookmark != null)
    }

    /**
     * Mengubah status bookmark suatu game (menambahkan jika belum ada, menghapus jika sudah ada).
     * @param gameDetail Detail game yang akan di-toggle bookmark-nya.
     * @param userId ID pengguna yang sedang login.
     */
    suspend fun toggleBookmark(gameDetail: GameDetailResponse, userId: Int) {
        val isBookmarked = bookmarkedItemDao.getBookmarkById(gameDetail.id, userId).first() != null

        if (isBookmarked) {
            bookmarkedItemDao.removeBookmark(gameDetail.id, userId)
        } else {
            val bookmarkedItem = BookmarkedItem(
                itemId = gameDetail.id,
                title = gameDetail.name,
                imageUrl = gameDetail.backgroundImage ?: "",
                rating = gameDetail.rating,
                releaseDate = gameDetail.released,
                metacritic = gameDetail.metacritic,
                playtime = gameDetail.playtime,
                esrbRating = gameDetail.esrbRating?.name,
                descriptionRaw = gameDetail.description,
                websiteUrl = gameDetail.website,
                platformsJson = gson.toJson(gameDetail.platforms?.map { it.platform?.name }),
                genresJson = gson.toJson(gameDetail.genres?.map { it.name }),
                developersJson = gson.toJson(gameDetail.developers?.map { it.name }),
                publishersJson = gson.toJson(gameDetail.publishers?.map { it.name }),
                ownerId = userId
            )
            bookmarkedItemDao.addBookmark(bookmarkedItem)
        }
    }

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