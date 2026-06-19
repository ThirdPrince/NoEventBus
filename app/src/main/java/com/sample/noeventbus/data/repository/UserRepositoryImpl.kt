package com.sample.noeventbus.data.repository

import com.sample.noeventbus.data.local.UserDataSource
import com.sample.noeventbus.domain.model.User
import com.sample.noeventbus.domain.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource
) : UserRepository {

    override val user: Flow<User> = userDataSource.userName
        .map { savedName -> User(savedName ?: "未登录用户") }

    override suspend fun updateName(newName: String) {
        delay(1500) 
        userDataSource.saveUserName(newName)
    }
}
