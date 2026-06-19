package com.sample.noeventbus.data

data class User(val name: String)

sealed interface LoginState {
    object Logout : LoginState
    data class Login(val token: String) : LoginState
}

enum class AppTheme {
    Light, Dark
}
