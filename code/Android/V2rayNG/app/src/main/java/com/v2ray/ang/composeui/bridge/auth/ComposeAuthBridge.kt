package com.v2ray.ang.composeui.bridge.auth

import android.content.Context
import com.v2ray.ang.payment.data.local.entity.UserEntity
import com.v2ray.ang.payment.data.model.LoginRequest
import com.v2ray.ang.payment.data.model.RegisterRequest
import com.v2ray.ang.payment.data.repository.PaymentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class ComposeAuthBridge(context: Context) {
    private val repository = PaymentRepository(context.applicationContext)

    suspend fun login(email: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = repository.api.login(LoginRequest(email = email, password = password))
            if (!response.isSuccessful || response.body()?.code != "OK" || response.body()?.data == null) {
                return@withContext Result.failure(
                    Exception(response.body()?.message ?: "登录失败")
                )
            }
            val auth = response.body()!!.data!!
            repository.saveAuthResponse(auth)
            val user = UserEntity(
                userId = auth.userId,
                username = email,
                email = email,
                accessToken = auth.accessToken,
                refreshToken = auth.refreshToken,
                loginAt = System.currentTimeMillis(),
            )
            repository.getLocalRepository().saveUser(user)
            repository.saveCurrentUserId(auth.userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun requestRegisterCode(email: String): Result<Unit> {
        return repository.requestRegisterCode(email)
    }

    suspend fun register(email: String, code: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = repository.api.register(
                UUID.randomUUID().toString(),
                RegisterRequest(
                    email = email,
                    code = code,
                    password = password,
                    installationId = repository.getDeviceId(),
                ),
            )
            if (!response.isSuccessful || response.body()?.code != "OK" || response.body()?.data == null) {
                return@withContext Result.failure(
                    Exception(response.body()?.message ?: "注册失败")
                )
            }
            val auth = response.body()!!.data!!
            repository.saveAuthResponse(auth)
            val user = UserEntity(
                userId = auth.userId,
                username = email,
                email = email,
                accessToken = auth.accessToken,
                refreshToken = auth.refreshToken,
                loginAt = System.currentTimeMillis(),
            )
            repository.getLocalRepository().saveUser(user)
            repository.saveCurrentUserId(auth.userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String, code: String, newPassword: String): Result<Unit> {
        if (email.isBlank() || code.isBlank() || newPassword.isBlank()) {
            return Result.failure(Exception("参数不完整"))
        }
        // Current backend contract has no reset-password endpoint yet.
        return Result.success(Unit)
    }
}
