package com.sample.noeventbus.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.noeventbus.domain.model.LoginState
import com.sample.noeventbus.domain.repository.AuthRepository
import com.sample.noeventbus.domain.repository.NotificationRepository
import com.sample.noeventbus.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _loginIntent = MutableSharedFlow<LoginState>()

    val loginState: StateFlow<LoginState> = merge(
        authRepository.loginState,
        _loginIntent
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LoginState.Logout
    )

    fun login(userName: String) {
        viewModelScope.launch {
            _loginIntent.emit(LoginState.Loading)
            delay(2000) 
            
            authRepository.login()
            userRepository.updateName(userName)
            
            // 替代 EventBus 发送 Toast 的逻辑
            notificationRepository.showNotification("欢迎回来，$userName！状态已同步。")
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            notificationRepository.showNotification("您已退出登录。")
        }
    }
}
