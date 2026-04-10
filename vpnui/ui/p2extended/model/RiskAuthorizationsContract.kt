package com.cryptovpn.ui.p2extended.model

import com.cryptovpn.navigation.CryptoVpnRouteSpec
import com.cryptovpn.navigation.RouteDefinition
import com.cryptovpn.ui.common.model.FeatureBullet
import com.cryptovpn.ui.common.model.FeatureField
import com.cryptovpn.ui.common.model.FeatureListItem
import com.cryptovpn.ui.common.model.FeatureMetric

data class RiskAuthorizationsUiState(
        val title: String = "风险授权",
        val subtitle: String = "RISK AUTHORIZATIONS",
        val badge: String = "P2 · EXTENDED",
        val summary: String = "风险授权页汇总高风险批准项、过期会话与可撤销授权。",
        val primaryActionLabel: String = "返回钱包首页",
        val secondaryActionLabel: String? = "进入安全中心",
        val heroAccent: String = "risk_authorizations",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "授权总数", value = "6"),
    FeatureMetric(label = "高风险", value = "2"),
    FeatureMetric(label = "已撤销", value = "1"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "风险授权页汇总高风险批准项、过期会话与可撤销授权。", trailing = "risk_authorizations", badge = "P2 扩展页"),
    FeatureListItem(title = "导航参数", subtitle = "当前页面无必填导航参数", trailing = "0 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "当前页面以信息展示与确认动作为主", trailing = "0 项", badge = "Info"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "风险授权 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 RiskAuthorizationsPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "当前页面无必填参数，但已纳入统一 RouteSpec 台账。"),
),
        val note: String = "风险授权 已按 P2 扩展页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
    )

    sealed interface RiskAuthorizationsEvent {
        data object Refresh : RiskAuthorizationsEvent
        data object PrimaryActionClicked : RiskAuthorizationsEvent
        data object SecondaryActionClicked : RiskAuthorizationsEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : RiskAuthorizationsEvent
    }

    val riskAuthorizationsNavigation: RouteDefinition = CryptoVpnRouteSpec.riskAuthorizations

    fun riskAuthorizationsPreviewState(): RiskAuthorizationsUiState = RiskAuthorizationsUiState()
