package com.app.domain.auth

import com.app.AppGraph
import com.app.data.repository.AuthRepository

class ResetPasswordUseCase(
    private val repository: AuthRepository = AppGraph.authRepository,
) {
    suspend operator fun invoke(email: String): Boolean = repository.resetPassword(email)
}
