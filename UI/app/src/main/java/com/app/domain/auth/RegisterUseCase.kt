package com.app.domain.auth

import com.app.AppGraph
import com.app.data.repository.AuthRepository

class RegisterUseCase(
    private val repository: AuthRepository = AppGraph.authRepository,
) {
    suspend operator fun invoke(email: String, password: String, inviteCode: String): Boolean = repository.register(email, password, inviteCode)
}
