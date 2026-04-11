package com.v2ray.ang.composeui.p1.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class WalletPaymentUiState(
    val title: String = "钱包支付",
    val subtitle: String = "WALLET PAYMENT",
    val badge: String = "P1 · REAL",
    val summary: String = "钱包支付页降为真实支付会话摘要，不再伪装真实钱包资产总览。",
    val primaryActionLabel: String = "进入支付确认",
    val secondaryActionLabel: String? = "返回套餐页",
    val heroAccent: String = "wallet_payment",
    val stateInfo: P1StateInfo = P1StateInfo(),
    val order: P1OrderSummary? = null,
    val metrics: List<FeatureMetric> = emptyList(),
    val fields: List<FeatureField> = emptyList(),
    val highlights: List<FeatureListItem> = emptyList(),
    val detailLines: List<P1DetailLine> = emptyList(),
    val checklist: List<FeatureBullet> = emptyList(),
    val note: String = "",
)

    sealed interface WalletPaymentEvent {
        data object Refresh : WalletPaymentEvent
        data object PrimaryActionClicked : WalletPaymentEvent
        data object SecondaryActionClicked : WalletPaymentEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : WalletPaymentEvent
    }

    val walletPaymentNavigation: RouteDefinition = CryptoVpnRouteSpec.walletPayment

    fun walletPaymentPreviewState(): WalletPaymentUiState = WalletPaymentUiState(
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
        ),
        metrics = listOf(
            FeatureMetric("当前订单", "ORD-PREVIEW-0001"),
            FeatureMetric("支付网络", "SOLANA"),
            FeatureMetric("支付币种", "USDT"),
        ),
        fields = listOf(
            FeatureField("source", "支付账户", "acct:preview", "当前登录账号"),
        ),
        detailLines = listOf(
            P1DetailLine("支付金额", "10.000001 USDT"),
            P1DetailLine("订单状态", "待支付"),
        ),
    )
