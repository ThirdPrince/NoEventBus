package com.sample.noeventbus.domain.usecase

import com.sample.noeventbus.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() = repository.login()
}

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() = repository.logout()
}

class GetLoginStateUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke() = repository.loginState
}
