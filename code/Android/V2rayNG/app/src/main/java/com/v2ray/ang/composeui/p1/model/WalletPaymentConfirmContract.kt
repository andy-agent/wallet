package com.v2ray.ang.composeui.p1.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition

data class WalletPaymentConfirmRouteArgs(val orderId: String = "")

data class WalletPaymentConfirmUiState(
    val title: String = "支付确认",
    val subtitle: String = "WALLET PAYMENT CONFIRM",
    val badge: String = "P1 · LIVE",
    val summary: String = "加载真实支付确认信息中…",
    val primaryActionLabel: String = "查看订单状态",
    val secondaryActionLabel: String? = "返回结算页",
    val heroAccent: String = "wallet_payment_confirm",
    val screenState: P1ScreenState = P1ScreenState(isLoading = true),
    val orderNo: String? = null,
    val planCode: String? = null,
    val planTitle: String = "",
    val status: String? = null,
    val statusText: String = "",
    val assetCode: String = "",
    val networkCode: String = "",
    val payableAmount: String = "",
    val baseAmount: String? = null,
    val uniqueAmountDelta: String? = null,
    val collectionAddress: String = "",
    val qrText: String = "",
    val expiresAt: String? = null,
    val txHash: String? = null,
    val note: String = "",
)

sealed interface WalletPaymentConfirmEvent {
    data object Refresh : WalletPaymentConfirmEvent
    data object PrimaryActionClicked : WalletPaymentConfirmEvent
    data object SecondaryActionClicked : WalletPaymentConfirmEvent
}

val walletPaymentConfirmNavigation: RouteDefinition = CryptoVpnRouteSpec.walletPaymentConfirm

fun walletPaymentConfirmPreviewState(): WalletPaymentConfirmUiState = WalletPaymentConfirmUiState(
    summary = "",
    screenState = P1ScreenState(),
    orderNo = "",
    planCode = "",
    planTitle = "",
    status = "PENDING",
    statusText = "待支付信息同步",
    assetCode = "",
    networkCode = "",
    payableAmount = "待接口返回",
    baseAmount = null,
    uniqueAmountDelta = null,
    collectionAddress = "待接口返回",
    qrText = "",
    expiresAt = null,
    note = "",
)
