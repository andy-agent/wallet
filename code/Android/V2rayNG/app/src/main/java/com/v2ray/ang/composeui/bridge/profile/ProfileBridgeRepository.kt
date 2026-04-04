package com.v2ray.ang.composeui.bridge.profile

import android.content.Context
import com.v2ray.ang.payment.data.local.entity.OrderEntity
import com.v2ray.ang.payment.data.local.entity.UserEntity
import com.v2ray.ang.payment.data.repository.PaymentRepository

class ProfileBridgeRepository(context: Context) {
    private val paymentRepository = PaymentRepository(context)

    suspend fun getCachedCurrentUser(): UserEntity? {
        return paymentRepository.getCachedCurrentUser()
    }

    suspend fun refreshCurrentUser(): Result<UserEntity> {
        val meResult = paymentRepository.getMe()
        return meResult.mapCatching { me ->
            val currentUser = paymentRepository.getCachedCurrentUser()
            val userEntity = UserEntity(
                userId = me.accountId,
                username = me.email,
                email = me.email,
                accessToken = paymentRepository.getAccessToken() ?: currentUser?.accessToken ?: "",
                refreshToken = paymentRepository.getRefreshToken(),
                loginAt = System.currentTimeMillis(),
            )
            paymentRepository.getLocalRepository().saveUser(userEntity)
            paymentRepository.saveCurrentUserId(me.accountId)
            userEntity
        }
    }

    suspend fun getCachedOrders(userId: String): List<OrderEntity> {
        return paymentRepository.getCachedOrders(userId)
    }

    suspend fun logout() {
        paymentRepository.logout()
    }
}
