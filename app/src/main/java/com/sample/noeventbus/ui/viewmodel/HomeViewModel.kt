package com.sample.noeventbus.ui.viewmodel

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
    private val authRepository: AuthRepository
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

    val shouldShowMessageBadge: StateFlow<Boolean> = combine(
        authRepository.loginState,
        messageRepository.count
    ) { state, count ->
        state is LoginState.Login && count > 0
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )
}
