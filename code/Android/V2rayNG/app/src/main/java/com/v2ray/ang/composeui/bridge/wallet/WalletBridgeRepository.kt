package com.v2ray.ang.composeui.bridge.wallet

import android.content.Context
import com.v2ray.ang.payment.data.api.MeData
import com.v2ray.ang.payment.data.local.entity.OrderEntity
import com.v2ray.ang.payment.data.model.Order
import com.v2ray.ang.payment.data.repository.PaymentRepository

class WalletBridgeRepository(context: Context) {
    private val paymentRepository = PaymentRepository(context)

    suspend fun getCurrentUserId(): String? {
        return paymentRepository.getCurrentUserId()
    }

    suspend fun getCachedOrders(userId: String): List<OrderEntity> {
        return paymentRepository.getCachedOrders(userId)
    }

    suspend fun getMe(): Result<MeData> {
        return paymentRepository.getMe()
    }

    fun getCurrentOrderId(): String? {
        return paymentRepository.getCurrentOrderId()
    }

    suspend fun getOrder(orderNo: String): Result<Order> {
        return paymentRepository.getOrder(orderNo)
    }

    suspend fun getCachedOrder(orderNo: String): OrderEntity? {
        return paymentRepository.getLocalRepository().getOrderByOrderNo(orderNo)
    }

    fun currentWalletAddressFallback(userId: String?): String {
        if (userId.isNullOrBlank()) return "--"
        return "acct:${userId.take(6)}...${userId.takeLast(4)}"
    }
}
