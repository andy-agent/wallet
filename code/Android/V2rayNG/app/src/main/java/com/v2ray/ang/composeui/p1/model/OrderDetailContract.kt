package com.v2ray.ang.composeui.p1.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition

data class OrderDetailRouteArgs(val orderId: String = "")

data class OrderDetailRowUi(
    val label: String,
    val value: String,
)

data class OrderDetailUiState(
    val title: String = "订单详情",
    val subtitle: String = "ORDER DETAIL",
    val badge: String = "P1 · LIVE",
    val summary: String = "加载真实订单详情中…",
    val primaryActionLabel: String = "返回订单中心",
    val secondaryActionLabel: String? = "返回套餐页",
    val heroAccent: String = "order_detail",
    val screenState: P1ScreenState = P1ScreenState(isLoading = true),
    val orderNo: String? = null,
    val planCode: String? = null,
    val planTitle: String = "",
    val status: String? = null,
    val statusText: String = "",
    val rows: List<OrderDetailRowUi> = emptyList(),
    val note: String = "",
)

sealed interface OrderDetailEvent {
    data object Refresh : OrderDetailEvent
    data object PrimaryActionClicked : OrderDetailEvent
    data object SecondaryActionClicked : OrderDetailEvent
}

val orderDetailNavigation: RouteDefinition = CryptoVpnRouteSpec.orderDetail

fun orderDetailPreviewState(): OrderDetailUiState = OrderDetailUiState(
    summary = "",
    screenState = P1ScreenState(),
    orderNo = "",
    planCode = "",
    planTitle = "",
    status = "PENDING",
    statusText = "待同步",
    rows = listOf(
        OrderDetailRowUi("订单号", "待接口返回"),
        OrderDetailRowUi("支付金额", "待接口返回"),
        OrderDetailRowUi("网络", "待接口返回"),
        OrderDetailRowUi("收款地址", "待接口返回"),
    ),
    note = "",
)
