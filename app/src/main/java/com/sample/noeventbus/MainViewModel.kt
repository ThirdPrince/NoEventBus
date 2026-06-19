package com.sample.noeventbus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    // 状态管理 (取代 EventBus 传递持久状态)
    private val _message = MutableStateFlow("等待发送...")
    val message = _message.asStateFlow()

    // 事件管理 (取代 EventBus 传递一次性通知)
    private val _event = MutableSharedFlow<String>()
    val event = _event.asSharedFlow()

    fun updateMessage(newMessage: String) {
        _message.value = newMessage
    }

    fun triggerEvent(eventContent: String) {
        viewModelScope.launch {
            _event.emit(eventContent)
        }
    }
}
