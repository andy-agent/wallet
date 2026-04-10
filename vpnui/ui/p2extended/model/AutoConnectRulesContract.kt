package com.cryptovpn.ui.p2extended.model

import com.cryptovpn.navigation.CryptoVpnRouteSpec
import com.cryptovpn.navigation.RouteDefinition
import com.cryptovpn.ui.common.model.FeatureBullet
import com.cryptovpn.ui.common.model.FeatureField
import com.cryptovpn.ui.common.model.FeatureListItem
import com.cryptovpn.ui.common.model.FeatureMetric

data class AutoConnectRulesUiState(
        val title: String = "自动连接规则",
        val subtitle: String = "AUTO CONNECT RULES",
        val badge: String = "P2 · EXTENDED",
        val summary: String = "自动连接规则页配置不安全网络、后台唤醒与指定 App 的连接策略。",
        val primaryActionLabel: String = "保存并返回首页",
        val secondaryActionLabel: String? = "回到个人中心",
        val heroAccent: String = "auto_connect_rules",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "规则数量", value = "4"),
    FeatureMetric(label = "Wi‑Fi 场景", value = "3"),
    FeatureMetric(label = "可信网络", value = "6"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "rule", label = "默认规则", value = "公共 Wi‑Fi 自动连 VPN", supportingText = "可替换为真实规则配置"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "自动连接规则页配置不安全网络、后台唤醒与指定 App 的连接策略。", trailing = "auto_connect_rules", badge = "P2 扩展页"),
    FeatureListItem(title = "导航参数", subtitle = "当前页面无必填导航参数", trailing = "0 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "默认规则", trailing = "1 项", badge = "Form"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "自动连接规则 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 AutoConnectRulesPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "当前页面无必填参数，但已纳入统一 RouteSpec 台账。"),
),
        val note: String = "自动连接规则 已按 P2 扩展页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
    )

    sealed interface AutoConnectRulesEvent {
        data object Refresh : AutoConnectRulesEvent
        data object PrimaryActionClicked : AutoConnectRulesEvent
        data object SecondaryActionClicked : AutoConnectRulesEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : AutoConnectRulesEvent
    }

    val autoConnectRulesNavigation: RouteDefinition = CryptoVpnRouteSpec.autoConnectRules

    fun autoConnectRulesPreviewState(): AutoConnectRulesUiState = AutoConnectRulesUiState()
