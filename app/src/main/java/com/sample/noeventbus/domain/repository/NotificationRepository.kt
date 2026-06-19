package com.sample.noeventbus.domain.repository

import kotlinx.coroutines.flow.SharedFlow

interface NotificationRepository {
    val notifications: SharedFlow<String>
    suspend fun showNotification(message: String)
}
