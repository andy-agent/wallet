package com.v2ray.ang.composeui.p1.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition

data class OrderListItemUi(
    val orderNo: String,
    val planTitle: String,
    val status: String,
    val statusText: String,
    val amountText: String,
    val createdAt: String,
)

data class OrderListUiState(
    val title: String = "订单中心",
    val subtitle: String = "ORDER LIST",
    val badge: String = "P1 · LIVE",
    val summary: String = "加载真实订单记录中…",
    val primaryActionLabel: String = "查看最近订单",
    val secondaryActionLabel: String? = "返回首页",
    val heroAccent: String = "order_list",
    val screenState: P1ScreenState = P1ScreenState(isLoading = true),
    val orders: List<OrderListItemUi> = emptyList(),
    val note: String = "",
)

sealed interface OrderListEvent {
    data object Refresh : OrderListEvent
    data object PrimaryActionClicked : OrderListEvent
    data object SecondaryActionClicked : OrderListEvent
}

val orderListNavigation: RouteDefinition = CryptoVpnRouteSpec.orderList

fun orderListPreviewState(): OrderListUiState = OrderListUiState(
    summary = "",
    screenState = P1ScreenState(),
    orders = listOf(
        OrderListItemUi(
            orderNo = "",
            planTitle = "",
            status = "PENDING",
            statusText = "待同步",
            amountText = "待接口返回",
            createdAt = "--",
        ),
        OrderListItemUi(
            orderNo = "",
            planTitle = "",
            status = "BLOCKED",
            statusText = "待接入",
            amountText = "待接口返回",
            createdAt = "--",
        ),
    ),
    note = "",
)
