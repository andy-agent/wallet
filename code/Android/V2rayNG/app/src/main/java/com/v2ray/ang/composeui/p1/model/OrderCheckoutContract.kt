package com.v2ray.ang.composeui.p1.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import java.util.Locale

data class OrderCheckoutRouteArgs(
    val planId: String = "",
    val assetCode: String = "",
    val networkCode: String = "",
)

data class CheckoutPaymentOptionUi(
    val assetCode: String,
    val networkCode: String,
    val label: String,
    val selected: Boolean = false,
)

data class OrderCheckoutUiState(
    val title: String = "订单结算",
    val subtitle: String = "ORDER CHECKOUT",
    val badge: String = "P1 · LIVE",
    val summary: String = "正在创建订单",
    val primaryActionLabel: String = "查看支付确认",
    val secondaryActionLabel: String? = "返回套餐页",
    val heroAccent: String = "order_checkout",
    val screenState: P1ScreenState = P1ScreenState(isLoading = true),
    val planCode: String? = null,
    val planTitle: String = "",
    val selectedRegionCode: String = "",
    val selectedRegionLabel: String = "",
    val orderNo: String? = null,
    val orderStatus: String? = null,
    val assetCode: String = "",
    val networkCode: String = "",
    val payableAmount: String = "",
    val baseAmount: String? = null,
    val uniqueAmountDelta: String? = null,
    val collectionAddress: String = "",
    val qrText: String = "",
    val expiresAt: String? = null,
    val invoiceEmail: String? = null,
    val serviceEnabled: Boolean = false,
    val paymentOptions: List<CheckoutPaymentOptionUi> = emptyList(),
    val note: String = "",
)

sealed interface OrderCheckoutEvent {
    data object Refresh : OrderCheckoutEvent
    data object CreateOrderClicked : OrderCheckoutEvent
    data object PrimaryActionClicked : OrderCheckoutEvent
    data object SecondaryActionClicked : OrderCheckoutEvent
}

val orderCheckoutNavigation: RouteDefinition = CryptoVpnRouteSpec.orderCheckout

fun orderCheckoutPreviewState(): OrderCheckoutUiState = OrderCheckoutUiState(
    summary = "预览态：展示订单结算布局。",
    screenState = P1ScreenState(),
    planCode = "PLAN_CODE",
    planTitle = "套餐名称",
    selectedRegionCode = "JP_BASIC",
    selectedRegionLabel = "日本基础线路 / NODE_A",
    orderNo = "ORDER_NO",
    orderStatus = "PENDING",
    assetCode = "ASSET",
    networkCode = "NETWORK",
    payableAmount = "待接口返回",
    baseAmount = null,
    uniqueAmountDelta = null,
    collectionAddress = "待接口返回",
    qrText = "",
    expiresAt = null,
    invoiceEmail = null,
    serviceEnabled = false,
    paymentOptions = listOf(
        CheckoutPaymentOptionUi("ASSET_A", "NETWORK_A", "待接口返回"),
        CheckoutPaymentOptionUi("ASSET_B", "NETWORK_B", "待接口返回", selected = true),
    ),
    note = "仅用于本地预览，不代表真实订单或支付参数。",
)

fun checkoutPaymentLabel(assetCode: String, networkCode: String): String {
    if (assetCode.isBlank() || networkCode.isBlank()) {
        return listOf(assetCode, networkCode).filter { it.isNotBlank() }.joinToString(".")
    }
    val assetLabel = if (assetCode.equals("SOL", ignoreCase = true)) {
        "sol"
    } else {
        assetCode.uppercase(Locale.ROOT)
    }
    val networkLabel = when (networkCode.uppercase(Locale.ROOT)) {
        "SOLANA" -> "solana"
        "TRON" -> "tron"
        else -> networkCode.lowercase(Locale.ROOT)
    }
    return "$assetLabel.$networkLabel"
}
