package com.app.data.repository

import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val loggedIn: StateFlow<Boolean>
    suspend fun checkForceUpdate(): Boolean
    suspend fun checkVersionUpdate(): Boolean
    suspend fun login(email: String, password: String): Boolean
    suspend fun register(email: String, password: String, inviteCode: String): Boolean
    suspend fun resetPassword(email: String): Boolean
}
