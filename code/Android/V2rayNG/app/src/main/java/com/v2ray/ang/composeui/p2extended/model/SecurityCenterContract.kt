package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition

data class SecurityCenterUiState(
    val title: String = "安全中心",
    val subtitle: String = "SECURITY CENTER",
    val badge: String = "同步中",
    val summary: String = "正在同步安全中心状态；若能力未接通，将显示只读或阻塞说明。",
    val primaryActionLabel: String? = null,
    val secondaryActionLabel: String? = null,
    val heroAccent: String = "security_center",
    val metrics: List<FeatureMetric> = listOf(
        FeatureMetric(label = "会话状态", value = "读取中"),
        FeatureMetric(label = "账户", value = "--"),
        FeatureMetric(label = "安全项", value = "读取中"),
    ),
    val fields: List<FeatureField> = emptyList(),
    val highlights: List<FeatureListItem> = listOf(
        FeatureListItem(title = "当前状态", subtitle = "正在读取安全中心概览", trailing = "", badge = "LOADING"),
    ),
    val checklist: List<FeatureBullet> = listOf(
        FeatureBullet(title = "说明", detail = "默认态仅展示同步状态，不再暴露模板交付信息。"),
    ),
    val note: String = "刷新后将替换为真实会话数据或明确阻塞说明。",
)

sealed interface SecurityCenterEvent {
    data object Refresh : SecurityCenterEvent
    data object PrimaryActionClicked : SecurityCenterEvent
    data object SecondaryActionClicked : SecurityCenterEvent
    data class FieldChanged(
        val key: String,
        val value: String,
    ) : SecurityCenterEvent
}

val securityCenterNavigation: RouteDefinition = CryptoVpnRouteSpec.securityCenter

fun securityCenterPreviewState(): SecurityCenterUiState = SecurityCenterUiState()
