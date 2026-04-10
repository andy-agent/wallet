package com.cryptovpn.ui.p0.model

import com.cryptovpn.navigation.CryptoVpnRouteSpec
import com.cryptovpn.navigation.RouteDefinition
import com.cryptovpn.ui.common.model.FeatureBullet
import com.cryptovpn.ui.common.model.FeatureField
import com.cryptovpn.ui.common.model.FeatureListItem
import com.cryptovpn.ui.common.model.FeatureMetric

data class ForceUpdateUiState(
        val title: String = "强制更新",
        val subtitle: String = "FORCE UPDATE",
        val badge: String = "P0 · CORE",
        val summary: String = "当前版本不满足最低可用要求，必须完成升级后才能继续使用。",
        val primaryActionLabel: String = "立即更新",
        val secondaryActionLabel: String? = "退出应用",
        val heroAccent: String = "force_update",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "目标版本", value = "v2.8.0"),
    FeatureMetric(label = "安全等级", value = "必须更新"),
    FeatureMetric(label = "影响模块", value = "3 项"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "当前版本不满足最低可用要求，必须完成升级后才能继续使用。", trailing = "force_update", badge = "P0"),
    FeatureListItem(title = "导航参数", subtitle = "当前页面无必填导航参数", trailing = "0 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "当前页面以信息展示与确认动作为主", trailing = "0 项", badge = "Info"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "强制更新 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 ForceUpdatePreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "当前页面无必填参数，但已纳入统一 RouteSpec 台账。"),
),
        val note: String = "强制更新 已按 P0 页面补齐，可继续替换为真实业务逻辑与接口数据。",
    )

    sealed interface ForceUpdateEvent {
        data object Refresh : ForceUpdateEvent
        data object PrimaryActionClicked : ForceUpdateEvent
        data object SecondaryActionClicked : ForceUpdateEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : ForceUpdateEvent
    }

    val forceUpdateNavigation: RouteDefinition = CryptoVpnRouteSpec.forceUpdate

    fun forceUpdatePreviewState(): ForceUpdateUiState = ForceUpdateUiState()
