package com.example.absensiassesment2mobpro.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreUtil(val context: Context) {

    val username: Flow<String?>
        get() = context.dataStore.data.map {
            it[SESSION_KEY]
        }

    suspend fun saveSession(username: String) {
        context.dataStore.edit {
            it[SESSION_KEY] = username
        }
    }

    suspend fun deleteSession() {
        context.dataStore.edit {
            it.clear()
        }
    }

    companion object {
        val SESSION_KEY = stringPreferencesKey("SESSION_KEY")
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
    }
}