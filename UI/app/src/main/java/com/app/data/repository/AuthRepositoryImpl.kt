package com.app.data.repository

import com.app.data.local.prefs.AppPreferences
import com.app.data.remote.mock.MockAuthDataSource
import kotlinx.coroutines.flow.StateFlow

class AuthRepositoryImpl(
    private val remote: MockAuthDataSource,
    private val preferences: AppPreferences,
) : AuthRepository {
    override val loggedIn: StateFlow<Boolean> = preferences.loggedIn
    override suspend fun checkForceUpdate(): Boolean = remote.checkForceUpdate()
    override suspend fun checkVersionUpdate(): Boolean = remote.checkVersionUpdate()
    override suspend fun login(email: String, password: String): Boolean = remote.login(email, password)
    override suspend fun register(email: String, password: String, inviteCode: String): Boolean = remote.register(email, password, inviteCode)
    override suspend fun resetPassword(email: String): Boolean = remote.resetPassword(email)
}
