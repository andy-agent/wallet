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
        val summary: String = "节点测速结果待接口返回。",
        val primaryActionLabel: String = "回到首页",
        val secondaryActionLabel: String? = "返回节点选择",
        val heroAccent: String = "node_speed_test",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "已测速", value = "待接口返回"),
    FeatureMetric(label = "最佳延迟", value = "待接口返回"),
    FeatureMetric(label = "抖动", value = "待接口返回"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "测速状态", subtitle = "节点列表与测速结果待接口返回", trailing = "待接口返回", badge = "Runtime"),
    FeatureListItem(title = "网络指标", subtitle = "延迟、抖动和丢包率待实际测速完成", trailing = "待测速", badge = "Metrics"),
    FeatureListItem(title = "节点分组", subtitle = "nodeGroupId 仅用于查询真实节点分组", trailing = "待同步", badge = "Routing"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "数据来源", detail = "测速结果必须来自真实节点探测数据。"),
    FeatureBullet(title = "空态策略", detail = "未完成测速时显示待测速，不展示演示毫秒值。"),
    FeatureBullet(title = "异常处理", detail = "测速失败时应展示真实失败原因。"),
    FeatureBullet(title = "参数约束", detail = "nodeGroupId 仅作为分组查询参数。"),
),
        val note: String = "节点测速能力待真实测速数据返回。",
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
