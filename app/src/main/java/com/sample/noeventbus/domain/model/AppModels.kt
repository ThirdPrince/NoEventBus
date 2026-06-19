package com.sample.noeventbus.domain.model

/**
 * 现代架构核心：UI 是状态的快照。
 * 我们不再需要 NameChangedEvent, LoginSuccessEvent 等，
 * 只需要观察这些状态的流。
 */

data class User(val name: String)

sealed interface LoginState {
    object Logout : LoginState
    object Loading : LoginState 
    data class Login(val token: String) : LoginState
}

enum class AppTheme {
    Light, Dark
}
