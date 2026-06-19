package com.sample.noeventbus.domain.repository

import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    val count: Flow<Int>
    suspend fun addMessage()
    suspend fun clearMessages()
}
