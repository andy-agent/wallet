package com.v2ray.ang.composeui.p1.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition

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
    val summary: String = "创建真实订单中…",
    val primaryActionLabel: String = "查看支付确认",
    val secondaryActionLabel: String? = "返回套餐页",
    val heroAccent: String = "order_checkout",
    val screenState: P1ScreenState = P1ScreenState(isLoading = true),
    val planCode: String? = null,
    val planTitle: String = "",
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
    data object PrimaryActionClicked : OrderCheckoutEvent
    data object SecondaryActionClicked : OrderCheckoutEvent
}

val orderCheckoutNavigation: RouteDefinition = CryptoVpnRouteSpec.orderCheckout

fun orderCheckoutPreviewState(): OrderCheckoutUiState = OrderCheckoutUiState(
    summary = "真实订单已创建。",
    screenState = P1ScreenState(),
    planCode = "BASIC_1M",
    planTitle = "月卡",
    orderNo = "ORD-EXAMPLE-0001",
    orderStatus = "AWAITING_PAYMENT",
    assetCode = "USDT",
    networkCode = "SOLANA",
    payableAmount = "1.000001",
    baseAmount = "1.000000",
    uniqueAmountDelta = "0.000001",
    collectionAddress = "EVYe1JoVU9m46o5QLgJdZM6CCG996jfCvYoKu5DTNEjj",
    qrText = "solana:EVYe1JoVU9m46o5QLgJdZM6CCG996jfCvYoKu5DTNEjj?amount=1.000001",
    expiresAt = "2026-04-11T14:53:15.306Z",
    invoiceEmail = "user@example.com",
    serviceEnabled = true,
    paymentOptions = listOf(
        CheckoutPaymentOptionUi("USDT", "TRON", "USDT · TRON"),
        CheckoutPaymentOptionUi("SOL", "SOLANA", "SOL · SOLANA", selected = true),
    ),
    note = "Preview only.",
)
