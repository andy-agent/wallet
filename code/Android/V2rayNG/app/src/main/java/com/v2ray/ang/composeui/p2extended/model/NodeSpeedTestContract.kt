package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition

data class NodeSpeedTestRouteArgs(val nodeGroupId: String = "premium_apac")

data class NodeSpeedTestUiState(
    val title: String = "节点测速",
    val subtitle: String = "NODE SPEED TEST",
    val badge: String = "同步中",
    val summary: String = "正在读取节点测速缓存和分组信息。",
    val primaryActionLabel: String? = null,
    val secondaryActionLabel: String? = null,
    val heroAccent: String = "node_speed_test",
    val metrics: List<FeatureMetric> = listOf(
        FeatureMetric(label = "已测速", value = "读取中"),
        FeatureMetric(label = "最佳延迟", value = "读取中"),
        FeatureMetric(label = "节点分组", value = "读取中"),
    ),
    val fields: List<FeatureField> = emptyList(),
    val highlights: List<FeatureListItem> = listOf(
        FeatureListItem(title = "当前状态", subtitle = "正在读取测速缓存", trailing = "", badge = "LOADING"),
    ),
    val checklist: List<FeatureBullet> = listOf(
        FeatureBullet(title = "说明", detail = "默认态不再展示演示测速结果和模板交付信息。"),
    ),
    val note: String = "刷新后会替换为真实测速缓存或明确阻塞说明。",
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
