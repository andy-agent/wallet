package com.v2ray.ang.composeui.p1.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class OrderListUiState(
    val title: String = "订单中心",
    val subtitle: String = "ORDER LIST",
    val badge: String = "P1 · REAL",
    val summary: String = "列表页展示当前账号的真实订单缓存，点进详情时会刷新真实订单状态。",
    val primaryActionLabel: String = "打开最新订单",
    val secondaryActionLabel: String? = "返回首页",
    val heroAccent: String = "order_list",
    val stateInfo: P1StateInfo = P1StateInfo(),
    val metrics: List<FeatureMetric> = emptyList(),
    val searchField: FeatureField = FeatureField("search", "搜索订单", ""),
    val highlights: List<FeatureListItem> = emptyList(),
    val orders: List<P1OrderSummary> = emptyList(),
    val checklist: List<FeatureBullet> = emptyList(),
    val note: String = "",
)

    sealed interface OrderListEvent {
        data object Refresh : OrderListEvent
        data object PrimaryActionClicked : OrderListEvent
        data object SecondaryActionClicked : OrderListEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : OrderListEvent
    }

    val orderListNavigation: RouteDefinition = CryptoVpnRouteSpec.orderList

    fun orderListPreviewState(): OrderListUiState = OrderListUiState(
        stateInfo = P1StateInfo(P1ScreenState.Content),
        metrics = listOf(
            FeatureMetric("订单总数", "2"),
            FeatureMetric("进行中", "1"),
            FeatureMetric("已完成", "1"),
        ),
        orders = listOf(
            P1OrderSummary(
                orderNo = "ORD-PREVIEW-0002",
                planCode = "basic_1m",
                planName = "月卡",
                status = "AWAITING_PAYMENT",
                statusText = "待支付",
                amountText = "10.000001 USDT",
                assetCode = "USDT",
                networkCode = "SOLANA",
                createdAt = "2026-04-11 10:00",
                expiresAt = "2026-04-11 12:00",
            ),
            P1OrderSummary(
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
            ),
        ),
    )
