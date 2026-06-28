package com.sample.noeventbus.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// 统一 DataStore 命名
private val Context.authDataStore by preferencesDataStore(name = "auth_prefs")

@Singleton
class AuthDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val tokenKey = stringPreferencesKey("auth_token")

    val token: Flow<String?> = context.authDataStore.data.map { prefs ->
        prefs[tokenKey]
    }

    suspend fun saveToken(token: String) {
        context.authDataStore.edit { prefs ->
            prefs[tokenKey] = token
        }
    }

    suspend fun clearToken() {
        context.authDataStore.edit { prefs ->
            prefs.remove(tokenKey)
        }
    }
}
