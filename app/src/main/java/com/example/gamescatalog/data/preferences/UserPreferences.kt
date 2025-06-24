package com.example.gamescatalog.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Membuat extension untuk Context agar mudah diakses dari mana saja.
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    // Mendefinisikan kunci untuk setiap data yang akan disimpan.
    private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
    private val USER_ID_KEY = intPreferencesKey("user_id")

    /**
     * Mengambil data sesi (status login dan user id) dari DataStore.
     * Mengembalikan Flow agar bisa diobservasi secara real-time.
     */
    fun getSession(): Flow<Pair<Boolean, Int>> {
        return dataStore.data.map { preferences ->
            Pair(
                preferences[IS_LOGGED_IN_KEY] ?: false,
                preferences[USER_ID_KEY] ?: -1 // -1 menandakan tidak ada user id
            )
        }
    }

    /**
     * Menyimpan data sesi baru ke DataStore.
     */
    suspend fun saveSession(isLoggedIn: Boolean, userId: Int) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = isLoggedIn
            preferences[USER_ID_KEY] = userId
        }
    }

    /**
     * Menghapus semua data sesi saat logout.
     */
    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}