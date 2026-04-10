package com.cryptovpn.ui.p2.model

import com.cryptovpn.navigation.CryptoVpnRouteSpec
import com.cryptovpn.navigation.RouteDefinition
import com.cryptovpn.ui.common.model.FeatureBullet
import com.cryptovpn.ui.common.model.FeatureField
import com.cryptovpn.ui.common.model.FeatureListItem
import com.cryptovpn.ui.common.model.FeatureMetric

data class CommissionLedgerUiState(
        val title: String = "佣金账本",
        val subtitle: String = "COMMISSION LEDGER",
        val badge: String = "P2 · BASE",
        val summary: String = "账本页展示分润流水、待结算项与已结算收益。",
        val primaryActionLabel: String = "去提现",
        val secondaryActionLabel: String? = "返回邀请中心",
        val heroAccent: String = "commission_ledger",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "本月收益", value = "+$537.25"),
    FeatureMetric(label = "待结算", value = "89 USDT"),
    FeatureMetric(label = "已结算", value = "2,481 USDT"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "账本页展示分润流水、待结算项与已结算收益。", trailing = "commission_ledger", badge = "P2 基础文档页"),
    FeatureListItem(title = "导航参数", subtitle = "当前页面无必填导航参数", trailing = "0 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "当前页面以信息展示与确认动作为主", trailing = "0 项", badge = "Info"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "佣金账本 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 CommissionLedgerPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "当前页面无必填参数，但已纳入统一 RouteSpec 台账。"),
),
        val note: String = "佣金账本 已按 P2 基础文档页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
    )

    sealed interface CommissionLedgerEvent {
        data object Refresh : CommissionLedgerEvent
        data object PrimaryActionClicked : CommissionLedgerEvent
        data object SecondaryActionClicked : CommissionLedgerEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : CommissionLedgerEvent
    }

    val commissionLedgerNavigation: RouteDefinition = CryptoVpnRouteSpec.commissionLedger

    fun commissionLedgerPreviewState(): CommissionLedgerUiState = CommissionLedgerUiState()
