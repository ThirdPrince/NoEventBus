package com.sample.noeventbus.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.noeventbus.domain.model.LoginState
import com.sample.noeventbus.domain.model.User
import com.sample.noeventbus.domain.repository.AuthRepository
import com.sample.noeventbus.domain.repository.MessageRepository
import com.sample.noeventbus.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    userRepository: UserRepository,
    messageRepository: MessageRepository,
    authRepository: AuthRepository
) : ViewModel() {

    val user: StateFlow<User> = userRepository.user
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = User("加载中...")
        )

    val loginState: StateFlow<LoginState> = authRepository.loginState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LoginState.Logout
        )

    // 只有在已登录 且 消息数 > 0 时，才显示红点
    val shouldShowMessageBadge: StateFlow<Boolean> = combine(
        authRepository.loginState,
        messageRepository.count
    ) { login, count ->
        login is LoginState.Login && count > 0
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    // 消息页显示逻辑：未登录返回 null，UI 据此判断是否展示内容
    val displayMessageCount: StateFlow<Int?> = combine(
        authRepository.loginState,
        messageRepository.count
    ) { login, count ->
        if (login is LoginState.Login) count else null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
}
