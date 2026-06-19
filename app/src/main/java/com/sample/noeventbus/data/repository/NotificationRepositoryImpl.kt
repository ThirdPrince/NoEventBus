package com.sample.noeventbus.data.repository

import com.sample.noeventbus.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor() : NotificationRepository {
    private val _notifications = MutableSharedFlow<String>()
    override val notifications: SharedFlow<String> = _notifications.asSharedFlow()

    override suspend fun showNotification(message: String) {
        _notifications.emit(message)
    }
}
