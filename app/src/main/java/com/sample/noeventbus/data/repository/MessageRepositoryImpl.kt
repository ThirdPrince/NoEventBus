package com.sample.noeventbus.data.repository

import com.sample.noeventbus.data.local.MessageDataSource
import com.sample.noeventbus.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepositoryImpl @Inject constructor(
    private val messageDataSource: MessageDataSource
) : MessageRepository {

    override val count: Flow<Int> = messageDataSource.messageCount

    override suspend fun addMessage() {
        val currentCount = count.first()
        messageDataSource.updateMessageCount(currentCount + 1)
    }

    override suspend fun clearMessages() {
        messageDataSource.updateMessageCount(0)
    }
}
