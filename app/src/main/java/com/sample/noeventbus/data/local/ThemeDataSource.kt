package com.sample.noeventbus.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.sample.noeventbus.domain.model.AppTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.themeDataStore by preferencesDataStore(name = "theme_prefs")

@Singleton
class ThemeDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val isDarkModeKey = booleanPreferencesKey("is_dark_mode")

    val theme: Flow<AppTheme> = context.themeDataStore.data.map { prefs ->
        if (prefs[isDarkModeKey] == true) AppTheme.Dark else AppTheme.Light
    }

    suspend fun saveTheme(isDark: Boolean) {
        context.themeDataStore.edit { prefs ->
            prefs[isDarkModeKey] = isDark
        }
    }
}
