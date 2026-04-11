package com.v2ray.ang.composeui.p1.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class OrderDetailRouteArgs(val orderId: String = "")

data class OrderDetailUiState(
    val title: String = "订单详情",
    val subtitle: String = "ORDER DETAIL",
    val badge: String = "P1 · REAL",
    val summary: String = "详情页只渲染真实订单字段与真实支付结果，不再套用固定成功时间线。",
    val primaryActionLabel: String = "返回订单中心",
    val secondaryActionLabel: String? = "查看支付状态",
    val heroAccent: String = "order_detail",
    val stateInfo: P1StateInfo = P1StateInfo(),
    val order: P1OrderSummary? = null,
    val metrics: List<FeatureMetric> = emptyList(),
    val highlights: List<FeatureListItem> = emptyList(),
    val detailLines: List<P1DetailLine> = emptyList(),
    val checklist: List<FeatureBullet> = emptyList(),
    val note: String = "",
)

    sealed interface OrderDetailEvent {
        data object Refresh : OrderDetailEvent
        data object PrimaryActionClicked : OrderDetailEvent
        data object SecondaryActionClicked : OrderDetailEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : OrderDetailEvent
    }

    val orderDetailNavigation: RouteDefinition = CryptoVpnRouteSpec.orderDetail

    fun orderDetailPreviewState(): OrderDetailUiState = OrderDetailUiState(
        stateInfo = P1StateInfo(P1ScreenState.Content),
        order = P1OrderSummary(
            orderNo = "ORD-PREVIEW-0001",
            planCode = "basic_12m",
            planName = "年卡",
            status = "COMPLETED",
            statusText = "已完成",
            amountText = "58.000004 USDT",
            assetCode = "USDT",
            networkCode = "SOLANA",
            createdAt = "2026-04-10 10:00",
            expiresAt = "2027-04-10 10:00",
            txHash = "2xqD9V...",
            subscriptionUrl = "vless://preview",
        ),
        metrics = listOf(
            FeatureMetric("订单金额", "58.000004 USDT"),
            FeatureMetric("订单状态", "已完成"),
            FeatureMetric("支付网络", "SOLANA"),
        ),
        detailLines = listOf(
            P1DetailLine("订单号", "ORD-PREVIEW-0001"),
            P1DetailLine("链上交易", "2xqD9V..."),
            P1DetailLine("订阅链接", "vless://preview"),
        ),
    )
