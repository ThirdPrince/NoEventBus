package com.sample.noeventbus.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.messageDataStore by preferencesDataStore(name = "message_prefs")

@Singleton
class MessageDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val messageCountKey = intPreferencesKey("global_msg_count")

    val messageCount: Flow<Int> = context.messageDataStore.data.map { prefs ->
        prefs[messageCountKey] ?: 0
    }

    suspend fun updateMessageCount(count: Int) {
        context.messageDataStore.edit { prefs ->
            prefs[messageCountKey] = count
        }
    }
}
