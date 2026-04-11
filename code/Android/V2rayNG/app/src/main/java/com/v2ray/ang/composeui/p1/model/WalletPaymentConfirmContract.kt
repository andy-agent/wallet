package com.v2ray.ang.composeui.p1.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class WalletPaymentConfirmRouteArgs(val orderId: String = "")

data class WalletPaymentConfirmUiState(
    val title: String = "钱包支付确认",
    val subtitle: String = "WALLET PAYMENT CONFIRM",
    val badge: String = "P1 · REAL",
    val summary: String = "支付确认页只展示真实订单、真实金额和真实支付状态，不再假装自动开通成功。",
    val primaryActionLabel: String = "查看支付状态",
    val secondaryActionLabel: String? = "返回收银台",
    val heroAccent: String = "wallet_payment_confirm",
    val stateInfo: P1StateInfo = P1StateInfo(),
    val order: P1OrderSummary? = null,
    val metrics: List<FeatureMetric> = emptyList(),
    val highlights: List<FeatureListItem> = emptyList(),
    val detailLines: List<P1DetailLine> = emptyList(),
    val riskLines: List<P1DetailLine> = emptyList(),
    val checklist: List<FeatureBullet> = emptyList(),
    val note: String = "",
)

    sealed interface WalletPaymentConfirmEvent {
        data object Refresh : WalletPaymentConfirmEvent
        data object PrimaryActionClicked : WalletPaymentConfirmEvent
        data object SecondaryActionClicked : WalletPaymentConfirmEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : WalletPaymentConfirmEvent
    }

    val walletPaymentConfirmNavigation: RouteDefinition = CryptoVpnRouteSpec.walletPaymentConfirm

    fun walletPaymentConfirmPreviewState(): WalletPaymentConfirmUiState = WalletPaymentConfirmUiState(
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
            FeatureMetric("订单状态", "待支付"),
            FeatureMetric("支付网络", "SOLANA"),
            FeatureMetric("支付币种", "USDT"),
        ),
        detailLines = listOf(
            P1DetailLine("订单号", "ORD-PREVIEW-0001"),
            P1DetailLine("应付金额", "10.000001 USDT"),
            P1DetailLine("收款地址", "EVYe1J..."),
        ),
        riskLines = listOf(
            P1DetailLine("自动扫链", "无需手填 txHash，系统会按共享地址 + 尾差识别订单。"),
        ),
    )
