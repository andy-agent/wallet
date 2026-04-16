package com.app.domain.auth

import com.app.AppGraph
import com.app.data.repository.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository = AppGraph.authRepository,
) {
    suspend operator fun invoke(email: String, password: String): Boolean = repository.login(email, password)
}
