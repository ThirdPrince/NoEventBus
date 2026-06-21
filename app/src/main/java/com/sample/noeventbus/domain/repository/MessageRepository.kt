package com.sample.noeventbus.domain.repository

import com.sample.noeventbus.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    // 暴露消息列表流
    val messages: Flow<List<Message>>
    // 辅助暴露计数流
    val count: Flow<Int>
    
    suspend fun addMessage(content: String)
    suspend fun clearMessages()
}
