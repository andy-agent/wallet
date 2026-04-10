package com.cryptovpn.ui.p1.model

import com.cryptovpn.navigation.CryptoVpnRouteSpec
import com.cryptovpn.navigation.RouteDefinition
import com.cryptovpn.ui.common.model.FeatureBullet
import com.cryptovpn.ui.common.model.FeatureField
import com.cryptovpn.ui.common.model.FeatureListItem
import com.cryptovpn.ui.common.model.FeatureMetric

data class RegionSelectionUiState(
        val title: String = "选择最佳节点",
        val subtitle: String = "REGION SELECTION",
        val badge: String = "P1 · FLOW",
        val summary: String = "节点选择页展示延迟、负载与协议，便于用户切换最快入口。",
        val primaryActionLabel: String = "使用当前最佳节点",
        val secondaryActionLabel: String? = "返回首页",
        val heroAccent: String = "region_selection",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "最佳延迟", value = "41 ms"),
    FeatureMetric(label = "可用节点", value = "62"),
    FeatureMetric(label = "优先协议", value = "Reality"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "search", label = "节点搜索", value = "东京 / 日本 / JP", supportingText = "支持地区、国家或节点 ID 过滤"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "节点选择页展示延迟、负载与协议，便于用户切换最快入口。", trailing = "region_selection", badge = "P1"),
    FeatureListItem(title = "导航参数", subtitle = "当前页面无必填导航参数", trailing = "0 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "节点搜索", trailing = "1 项", badge = "Form"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "选择最佳节点 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 RegionSelectionPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "当前页面无必填参数，但已纳入统一 RouteSpec 台账。"),
),
        val note: String = "选择最佳节点 已按 P1 页面补齐，可继续替换为真实业务逻辑与接口数据。",
    )

    sealed interface RegionSelectionEvent {
        data object Refresh : RegionSelectionEvent
        data object PrimaryActionClicked : RegionSelectionEvent
        data object SecondaryActionClicked : RegionSelectionEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : RegionSelectionEvent
    }

    val regionSelectionNavigation: RouteDefinition = CryptoVpnRouteSpec.regionSelection

    fun regionSelectionPreviewState(): RegionSelectionUiState = RegionSelectionUiState()
