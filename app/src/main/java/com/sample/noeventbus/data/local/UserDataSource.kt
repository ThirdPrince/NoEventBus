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

private val Context.userDataSourceDataStore by preferencesDataStore(name = "user_prefs")

@Singleton
class UserDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val userNameKey = stringPreferencesKey("user_name")

    val userName: Flow<String?> = context.userDataSourceDataStore.data.map { prefs ->
        prefs[userNameKey]
    }

    suspend fun saveUserName(name: String) {
        context.userDataSourceDataStore.edit { prefs ->
            prefs[userNameKey] = name
        }
    }
}
