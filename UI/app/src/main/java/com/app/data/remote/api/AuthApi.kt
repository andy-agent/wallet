package com.app.data.remote.api

interface AuthApi {
    suspend fun login(email: String, password: String): Boolean
    suspend fun register(email: String, password: String, inviteCode: String): Boolean
    suspend fun resetPassword(email: String): Boolean
    suspend fun checkForceUpdate(): Boolean
    suspend fun checkVersionUpdate(): Boolean
}
