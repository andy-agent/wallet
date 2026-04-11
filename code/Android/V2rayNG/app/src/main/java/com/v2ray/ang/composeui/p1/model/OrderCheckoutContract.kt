package com.v2ray.ang.composeui.p1.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class OrderCheckoutRouteArgs(val planId: String = "")

data class OrderCheckoutUiState(
    val title: String = "订单收银台",
    val subtitle: String = "ORDER CHECKOUT",
    val badge: String = "P1 · REAL",
    val summary: String = "结算页直接绑定真实订单与 paymentTarget，不再显示假地址和假二维码。",
    val primaryActionLabel: String = "查看支付确认",
    val secondaryActionLabel: String? = "返回套餐页",
    val heroAccent: String = "order_checkout",
    val stateInfo: P1StateInfo = P1StateInfo(),
    val order: P1OrderSummary? = null,
    val metrics: List<FeatureMetric> = emptyList(),
    val fields: List<FeatureField> = emptyList(),
    val detailLines: List<P1DetailLine> = emptyList(),
    val checklist: List<FeatureBullet> = emptyList(),
    val note: String = "",
)

    sealed interface OrderCheckoutEvent {
        data object Refresh : OrderCheckoutEvent
        data object PrimaryActionClicked : OrderCheckoutEvent
        data object SecondaryActionClicked : OrderCheckoutEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : OrderCheckoutEvent
    }

    val orderCheckoutNavigation: RouteDefinition = CryptoVpnRouteSpec.orderCheckout

    fun orderCheckoutPreviewState(): OrderCheckoutUiState = OrderCheckoutUiState(
        stateInfo = P1StateInfo(P1ScreenState.Content),
        order = P1OrderSummary(
            orderNo = "ORD-PREVIEW-0001",
            planCode = "basic_1m",
            planName = "月卡",
            status = "AWAITING_PAYMENT",
            statusText = "待支付",
            amountText = "10.000001 USDT",
            assetCode = "USDT",
            networkCode = "SOLANA",
            createdAt = "2026-04-11 10:00",
            expiresAt = "2026-04-11 12:00",
            collectionAddress = "EVYe1J...",
            qrText = "solana:EVYe1J...?amount=10.000001",
            baseAmount = "10.000000",
            uniqueAmountDelta = "0.000001",
            payableAmount = "10.000001",
        ),
        metrics = listOf(
            FeatureMetric("套餐", "月卡"),
            FeatureMetric("支付金额", "10.000001 USDT"),
            FeatureMetric("支付网络", "SOLANA"),
        ),
        fields = listOf(
            FeatureField("invoice", "账单邮箱", "preview@example.com"),
        ),
        detailLines = listOf(
            P1DetailLine("收款地址", "EVYe1J..."),
            P1DetailLine("基础金额", "10.000000"),
            P1DetailLine("尾差金额", "0.000001"),
        ),
    )
