package com.cryptovpn.ui.p1.model

import com.cryptovpn.navigation.CryptoVpnRouteSpec
import com.cryptovpn.navigation.RouteDefinition
import com.cryptovpn.ui.common.model.FeatureBullet
import com.cryptovpn.ui.common.model.FeatureField
import com.cryptovpn.ui.common.model.FeatureListItem
import com.cryptovpn.ui.common.model.FeatureMetric

data class WalletPaymentConfirmRouteArgs(val orderId: String = "ORD-2025-0001")

data class WalletPaymentConfirmUiState(
        val title: String = "钱包支付确认",
        val subtitle: String = "WALLET PAYMENT CONFIRM",
        val badge: String = "P1 · FLOW",
        val summary: String = "针对 VPN 购买流的独立确认页，校验订单、金额与风险提示后再发起支付。",
        val primaryActionLabel: String = "确认支付并开通",
        val secondaryActionLabel: String? = "返回订单收银台",
        val heroAccent: String = "wallet_payment_confirm",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "订单号", value = "ORD-2025-0001"),
    FeatureMetric(label = "支付币种", value = "USDT"),
    FeatureMetric(label = "网络手续费", value = "1.69 USDT"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "针对 VPN 购买流的独立确认页，校验订单、金额与风险提示后再发起支付。", trailing = "wallet_payment_confirm", badge = "P1"),
    FeatureListItem(title = "导航参数", subtitle = "orderId", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "当前页面以信息展示与确认动作为主", trailing = "0 项", badge = "Info"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "钱包支付确认 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 WalletPaymentConfirmPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "钱包支付确认 已按 P1 页面补齐，可继续替换为真实业务逻辑与接口数据。",
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

    fun walletPaymentConfirmPreviewState(): WalletPaymentConfirmUiState = WalletPaymentConfirmUiState()
