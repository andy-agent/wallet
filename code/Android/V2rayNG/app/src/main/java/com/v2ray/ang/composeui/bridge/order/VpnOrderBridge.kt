package com.v2ray.ang.composeui.bridge.order

import android.app.Application
import com.v2ray.ang.payment.PaymentConfig
import com.v2ray.ang.payment.data.local.entity.OrderEntity
import com.v2ray.ang.payment.data.model.Order
import com.v2ray.ang.payment.data.model.Plan
import com.v2ray.ang.payment.data.repository.PaymentRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class VpnPlanBridgeItem(
    val id: String,
    val name: String,
    val duration: String,
    val price: String,
    val originalPrice: String?,
    val isRecommended: Boolean,
    val features: List<String>,
    val badge: String?,
)

data class CheckoutBridgeData(
    val orderNo: String,
    val planName: String,
    val duration: String,
    val amount: String,
    val totalAmount: String,
    val receiveAddress: String,
    val qrText: String,
    val expiresInSeconds: Int,
)

class VpnOrderBridge(application: Application) {
    private val repository = PaymentRepository(application)

    suspend fun loadPlans(): Result<List<VpnPlanBridgeItem>> {
        return repository.getPlans().map { plans ->
            plans.sortedBy { it.displayOrder }.map { it.toBridgeItem() }
        }
    }

    suspend fun createOrder(planCode: String, useWalletPath: Boolean): Result<CheckoutBridgeData> {
        val network = if (useWalletPath) {
            PaymentConfig.NetworkCode.SOLANA
        } else {
            PaymentConfig.NetworkCode.TRON
        }
        val asset = if (useWalletPath) {
            PaymentConfig.AssetCode.SOL
        } else {
            PaymentConfig.AssetCode.USDT
        }
        return repository.createOrder(
            planId = planCode,
            assetCode = asset,
            networkCode = network,
        ).map { order ->
            order.toCheckoutBridgeData()
        }
    }

    suspend fun refreshOrder(orderNo: String): Result<Order> {
        return repository.getOrder(orderNo)
    }

    suspend fun loadCachedOrders(): Result<List<OrderEntity>> {
        val userId = repository.getCurrentUserId()
            ?: return Result.failure(IllegalStateException("未登录"))
        return Result.success(repository.getCachedOrders(userId))
    }

    private fun Plan.toBridgeItem(): VpnPlanBridgeItem {
        val basePrice = priceUsd.toDoubleOrNull()
        val original = if (billingCycleMonths >= 3 && basePrice != null) {
            String.format(Locale.US, "$%.2f", basePrice * 1.10)
        } else {
            null
        }
        val features = listOf(
            getTrafficDisplay(),
            "${maxActiveSessions} 设备同时连接",
            "区域策略: $regionAccessPolicy",
            if (includesAdvancedRegions) "含高级区域节点" else "标准区域节点",
        )
        return VpnPlanBridgeItem(
            id = planCode,
            name = name,
            duration = getDurationDisplay(),
            price = "$$priceUsd",
            originalPrice = original,
            isRecommended = displayOrder == 1,
            features = features,
            badge = badge,
        )
    }

    private fun Order.toCheckoutBridgeData(): CheckoutBridgeData {
        val payable = paymentTarget?.payableAmount ?: payableAmount
        return CheckoutBridgeData(
            orderNo = orderNo,
            planName = planName,
            duration = plan.id,
            amount = "$$quoteUsdAmount",
            totalAmount = "$payable ${payment.assetCode}",
            receiveAddress = payment.receiveAddress,
            qrText = payment.qrText,
            expiresInSeconds = expiresAt.secondsUntilNow().coerceAtLeast(0),
        )
    }
}

private fun String.secondsUntilNow(): Int {
    val fallback = 15 * 60
    val formats = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        },
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        },
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US),
    )
    val now = System.currentTimeMillis()
    for (format in formats) {
        val ts = runCatching { format.parse(this)?.time }.getOrNull()
        if (ts != null) {
            return ((ts - now) / 1000L).toInt()
        }
    }
    return fallback
}

fun OrderEntity.toOrderStatusLabel(): String {
    return when (status) {
        PaymentConfig.OrderStatus.PENDING_PAYMENT -> "待支付"
        PaymentConfig.OrderStatus.PAID_SUCCESS -> "已支付"
        PaymentConfig.OrderStatus.FULFILLED -> "已完成"
        PaymentConfig.OrderStatus.EXPIRED,
        PaymentConfig.OrderStatus.LATE_PAID -> "已取消"
        PaymentConfig.OrderStatus.FAILED -> "已退款"
        else -> status
    }
}
