package com.app.domain.auth

import com.app.AppGraph
import com.app.data.repository.AuthRepository

class CheckForceUpdateUseCase(
    private val repository: AuthRepository = AppGraph.authRepository,
) {
    suspend operator fun invoke(): Boolean = repository.checkForceUpdate()
}
