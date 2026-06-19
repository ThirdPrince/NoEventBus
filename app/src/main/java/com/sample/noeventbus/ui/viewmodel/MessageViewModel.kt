package com.sample.noeventbus.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.noeventbus.domain.model.LoginState
import com.sample.noeventbus.domain.repository.AuthRepository
import com.sample.noeventbus.domain.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // 属于消息页自己的逻辑：合并认证状态和消息计数
    val displayMessageCount: StateFlow<Int?> = combine(
        authRepository.loginState,
        messageRepository.count
    ) { state, count ->
        if (state is LoginState.Login) count else null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
    
    fun addMessage() {
        viewModelScope.launch {
            messageRepository.addMessage()
        }
    }
    
    fun clearMessages() {
        viewModelScope.launch {
            messageRepository.clearMessages()
        }
    }
}
