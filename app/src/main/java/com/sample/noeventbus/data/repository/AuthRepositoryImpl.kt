package com.sample.noeventbus.data.repository

import com.sample.noeventbus.data.local.TokenManager
import com.sample.noeventbus.domain.model.LoginState
import com.sample.noeventbus.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val tokenManager: TokenManager
) : AuthRepository {

    override val loginState: Flow<LoginState> = tokenManager.token
        .map { token ->
            if (token != null) LoginState.Login(token) else LoginState.Logout
        }

    override suspend fun login() {
        val mockToken = "persistent_token_${System.currentTimeMillis()}"
        tokenManager.saveToken(mockToken)
    }

    override suspend fun logout() {
        tokenManager.clearToken()
    }
}
