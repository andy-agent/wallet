package com.app.data.remote.mock

import com.app.data.local.prefs.AppPreferences
import com.app.data.remote.api.AuthApi
import kotlinx.coroutines.delay

class MockAuthDataSource(
    private val preferences: AppPreferences,
) : AuthApi {
    override suspend fun login(email: String, password: String): Boolean {
        delay(220)
        val success = email.isNotBlank() && password.length >= 6
        if (success) preferences.setLoggedIn(true)
        return success
    }

    override suspend fun register(email: String, password: String, inviteCode: String): Boolean {
        delay(260)
        val success = email.isNotBlank() && password.length >= 6
        if (success) preferences.setLoggedIn(true)
        return success
    }

    override suspend fun resetPassword(email: String): Boolean {
        delay(180)
        return email.contains('@')
    }

    override suspend fun checkForceUpdate(): Boolean = preferences.forceUpdateRequired.value
    override suspend fun checkVersionUpdate(): Boolean = preferences.versionUpdateAvailable.value
}
