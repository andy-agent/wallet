package com.v2ray.ang.composeui.p1.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition

data class OrderResultRouteArgs(val orderId: String = "")

data class OrderResultUiState(
    val title: String = "订单状态",
    val subtitle: String = "ORDER RESULT",
    val badge: String = "P1 · LIVE",
    val summary: String = "刷新真实订单状态中…",
    val primaryActionLabel: String = "刷新状态",
    val secondaryActionLabel: String? = "查看订单详情",
    val heroAccent: String = "order_result",
    val screenState: P1ScreenState = P1ScreenState(isLoading = true),
    val orderNo: String? = null,
    val planCode: String? = null,
    val planTitle: String = "",
    val status: String? = null,
    val statusText: String = "",
    val payableAmount: String = "",
    val assetCode: String = "",
    val networkCode: String = "",
    val txHash: String? = null,
    val paymentMatchedAt: String? = null,
    val subscriptionUrl: String? = null,
    val expiresAt: String? = null,
    val completedAt: String? = null,
    val failureReason: String? = null,
    val note: String = "",
)

sealed interface OrderResultEvent {
    data object Refresh : OrderResultEvent
    data object PrimaryActionClicked : OrderResultEvent
    data object SecondaryActionClicked : OrderResultEvent
}

val orderResultNavigation: RouteDefinition = CryptoVpnRouteSpec.orderResult

fun orderResultPreviewState(): OrderResultUiState = OrderResultUiState(
    summary = "",
    screenState = P1ScreenState(),
    orderNo = "",
    planCode = "",
    planTitle = "",
    status = "PENDING",
    statusText = "待同步",
    payableAmount = "待接口返回",
    assetCode = "",
    networkCode = "",
    txHash = null,
    paymentMatchedAt = null,
    subscriptionUrl = null,
    completedAt = null,
    note = "",
)
