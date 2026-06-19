package com.sample.noeventbus.domain.repository

import com.sample.noeventbus.domain.model.LoginState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val loginState: Flow<LoginState>
    suspend fun login()
    suspend fun logout()
}
