package com.sample.noeventbus.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.noeventbus.domain.model.LoginState
import com.sample.noeventbus.domain.repository.AuthRepository
import com.sample.noeventbus.domain.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // 属于消息页自己的逻辑：仅在已登录时才向 UI 暴露数字
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
