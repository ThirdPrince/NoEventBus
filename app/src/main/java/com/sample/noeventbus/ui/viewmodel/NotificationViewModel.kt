package com.sample.noeventbus.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.sample.noeventbus.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {
    val notifications = notificationRepository.notifications
}
