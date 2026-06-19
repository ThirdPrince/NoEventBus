package com.sample.noeventbus.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.noeventbus.domain.model.LoginState
import com.sample.noeventbus.domain.model.User
import com.sample.noeventbus.domain.repository.AuthRepository
import com.sample.noeventbus.domain.repository.NotificationRepository
import com.sample.noeventbus.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    // 观察用户信息
    val user: StateFlow<User> = userRepository.user
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), User("加载中..."))

    // 观察登录状态
    val loginState: StateFlow<LoginState> = authRepository.loginState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LoginState.Logout)

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating = _isUpdating.asStateFlow()
    
    fun updateNickname(newName: String) {
        // 核心权限逻辑：只有登录了才允许修改
        if (loginState.value !is LoginState.Login) return
        
        viewModelScope.launch {
            _isUpdating.value = true
            // 执行异步持久化
            userRepository.updateName(newName)
            _isUpdating.value = false
            
            // 成功后通过全局通知流发送反馈，替代 EventBus post
            notificationRepository.showNotification("昵称已成功更新并持久化。")
        }
    }
}
