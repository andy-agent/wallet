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
        val badge: String = "",
        val summary: String = "未接入",
        val primaryActionLabel: String = "进入支付确认",
        val secondaryActionLabel: String? = "返回套餐页",
        val heroAccent: String = "wallet_payment",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "可用余额", value = "待接口返回"),
    FeatureMetric(label = "默认网络", value = "待接口返回"),
    FeatureMetric(label = "预估手续费", value = "待链上估算"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "source", label = "扣款钱包", value = "待接口返回", supportingText = ""),
    FeatureField(key = "memo", label = "支付备注", value = "待接口返回", supportingText = ""),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "能力状态", subtitle = "未接入", trailing = "BLOCKED", badge = ""),
    FeatureListItem(title = "导航参数", subtitle = "当前无数据", trailing = "", badge = ""),
    FeatureListItem(title = "表单状态", subtitle = "待接口返回", trailing = "", badge = ""),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "能力状态", detail = "未接入"),
    FeatureBullet(title = "余额", detail = "待接口返回"),
    FeatureBullet(title = "手续费", detail = "待链上估算"),
),
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

    fun walletPaymentPreviewState(): WalletPaymentUiState = WalletPaymentUiState()
