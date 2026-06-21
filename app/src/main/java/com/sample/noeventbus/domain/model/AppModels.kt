package com.sample.noeventbus.domain.model

data class User(val name: String)

data class Message(
    val id: String,
    val content: String,
    val timestamp: Long
)

sealed interface LoginState {
    object Logout : LoginState
    object Loading : LoginState 
    data class Login(val token: String) : LoginState
}

enum class AppTheme {
    Light, Dark
}
