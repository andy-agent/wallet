package com.v2ray.ang.composeui.p1.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class OrderResultRouteArgs(val orderId: String = "")

data class OrderResultUiState(
    val title: String = "订单状态",
    val subtitle: String = "ORDER RESULT",
    val badge: String = "P1 · REAL",
    val summary: String = "结果页按真实订单状态机渲染，不再恒定显示成功态。",
    val primaryActionLabel: String = "刷新订单状态",
    val secondaryActionLabel: String? = "查看订单详情",
    val heroAccent: String = "order_result",
    val stateInfo: P1StateInfo = P1StateInfo(),
    val order: P1OrderSummary? = null,
    val metrics: List<FeatureMetric> = emptyList(),
    val highlights: List<FeatureListItem> = emptyList(),
    val detailLines: List<P1DetailLine> = emptyList(),
    val canEnterHome: Boolean = false,
    val checklist: List<FeatureBullet> = emptyList(),
    val note: String = "",
)

    sealed interface OrderResultEvent {
        data object Refresh : OrderResultEvent
        data object PrimaryActionClicked : OrderResultEvent
        data object SecondaryActionClicked : OrderResultEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : OrderResultEvent
    }

    val orderResultNavigation: RouteDefinition = CryptoVpnRouteSpec.orderResult

    fun orderResultPreviewState(): OrderResultUiState = OrderResultUiState(
        stateInfo = P1StateInfo(P1ScreenState.Content),
        order = P1OrderSummary(
            orderNo = "ORD-PREVIEW-0001",
            planCode = "basic_1m",
            planName = "月卡",
            status = "COMPLETED",
            statusText = "已完成",
            amountText = "10.000001 USDT",
            assetCode = "USDT",
            networkCode = "SOLANA",
            createdAt = "2026-04-11 10:00",
            expiresAt = "2026-05-11 10:00",
            txHash = "2xqD9V...",
            subscriptionUrl = "vless://preview",
        ),
        metrics = listOf(
            FeatureMetric("订单状态", "已完成"),
            FeatureMetric("支付网络", "SOLANA"),
            FeatureMetric("订阅状态", "ACTIVE"),
        ),
        detailLines = listOf(
            P1DetailLine("链上交易", "2xqD9V..."),
            P1DetailLine("订阅链接", "vless://preview"),
        ),
        canEnterHome = true,
    )
