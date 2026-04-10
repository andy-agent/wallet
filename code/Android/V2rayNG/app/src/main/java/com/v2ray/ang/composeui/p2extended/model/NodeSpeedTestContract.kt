package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class NodeSpeedTestRouteArgs(val nodeGroupId: String = "premium_apac")

data class NodeSpeedTestUiState(
        val title: String = "节点测速",
        val subtitle: String = "NODE SPEED TEST",
        val badge: String = "P2 · EXTENDED",
        val summary: String = "节点测速页展示多节点测速结果、抖动与丢包率，辅助用户选择最佳线路。",
        val primaryActionLabel: String = "返回节点选择",
        val secondaryActionLabel: String? = "回到首页",
        val heroAccent: String = "node_speed_test",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "已测速", value = "12"),
    FeatureMetric(label = "最佳延迟", value = "41 ms"),
    FeatureMetric(label = "抖动", value = "2 ms"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "节点测速页展示多节点测速结果、抖动与丢包率，辅助用户选择最佳线路。", trailing = "node_speed_test", badge = "P2 扩展页"),
    FeatureListItem(title = "导航参数", subtitle = "nodeGroupId", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "当前页面以信息展示与确认动作为主", trailing = "0 项", badge = "Info"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "节点测速 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 NodeSpeedTestPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "节点测速 已按 P2 扩展页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
    )

    sealed interface NodeSpeedTestEvent {
        data object Refresh : NodeSpeedTestEvent
        data object PrimaryActionClicked : NodeSpeedTestEvent
        data object SecondaryActionClicked : NodeSpeedTestEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : NodeSpeedTestEvent
    }

    val nodeSpeedTestNavigation: RouteDefinition = CryptoVpnRouteSpec.nodeSpeedTest

    fun nodeSpeedTestPreviewState(): NodeSpeedTestUiState = NodeSpeedTestUiState()
