package com.sample.noeventbus.domain.repository

import com.sample.noeventbus.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val user: Flow<User>
    suspend fun updateName(newName: String)
}
