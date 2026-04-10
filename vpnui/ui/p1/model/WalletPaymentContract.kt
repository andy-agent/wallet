package com.cryptovpn.ui.p1.model

import com.cryptovpn.navigation.CryptoVpnRouteSpec
import com.cryptovpn.navigation.RouteDefinition
import com.cryptovpn.ui.common.model.FeatureBullet
import com.cryptovpn.ui.common.model.FeatureField
import com.cryptovpn.ui.common.model.FeatureListItem
import com.cryptovpn.ui.common.model.FeatureMetric

data class WalletPaymentUiState(
        val title: String = "钱包支付",
        val subtitle: String = "WALLET PAYMENT",
        val badge: String = "P1 · FLOW",
        val summary: String = "钱包分组下的支付页，独立于 VPN 购买流的 wallet_payment_confirm 交付。",
        val primaryActionLabel: String = "进入支付确认",
        val secondaryActionLabel: String? = "返回套餐页",
        val heroAccent: String = "wallet_payment",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "可用余额", value = "12,871.99"),
    FeatureMetric(label = "默认网络", value = "TRON"),
    FeatureMetric(label = "预估手续费", value = "1.69 USDT"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "source", label = "扣款钱包", value = "TQx...kJvV", supportingText = "可替换真实钱包地址"),
    FeatureField(key = "memo", label = "支付备注", value = "VPN Annual Pro", supportingText = "对账或客服追踪用"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "钱包分组下的支付页，独立于 VPN 购买流的 wallet_payment_confirm 交付。", trailing = "wallet_payment", badge = "P1"),
    FeatureListItem(title = "导航参数", subtitle = "当前页面无必填导航参数", trailing = "0 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "扣款钱包、支付备注", trailing = "2 项", badge = "Form"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "钱包支付 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 WalletPaymentPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "当前页面无必填参数，但已纳入统一 RouteSpec 台账。"),
),
        val note: String = "钱包支付 已按 P1 页面补齐，可继续替换为真实业务逻辑与接口数据。",
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

    fun walletPaymentPreviewState(): WalletPaymentUiState = WalletPaymentUiState()
