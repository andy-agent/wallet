package com.v2ray.ang.composeui.p0.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class OptionalUpdateUiState(
    val title: String = "发现新版本",
    val subtitle: String = "OPTIONAL UPDATE",
    val badge: String = "P0 · CORE",
    val summary: String = "检测到可选更新，可立即升级，也可稍后处理。",
    val primaryActionLabel: String = "立即更新",
    val secondaryActionLabel: String? = "稍后提醒",
    val heroAccent: String = "optional_update",
    val metrics: List<FeatureMetric> = listOf(
        FeatureMetric(label = "更新类型", value = "可选更新"),
        FeatureMetric(label = "推荐时机", value = "空闲时升级"),
        FeatureMetric(label = "预期收益", value = "修复与体验优化"),
    ),
    val fields: List<FeatureField> = emptyList(),
    val highlights: List<FeatureListItem> = listOf(
        FeatureListItem(
            title = "版本提示",
            subtitle = "当前版本仍可继续使用，但建议尽快升级到最新版。",
            trailing = "optional_update",
            badge = "P0",
        ),
    ),
    val checklist: List<FeatureBullet> = listOf(
        FeatureBullet(title = "立即更新", detail = "现在升级以获得最新修复与优化。"),
        FeatureBullet(title = "稍后提醒", detail = "保留当前版本，稍后在合适时间更新。"),
    ),
    val note: String = "更新检查当前来自客户端运行时状态。",
)

sealed interface OptionalUpdateEvent {
    data object Refresh : OptionalUpdateEvent
    data object PrimaryActionClicked : OptionalUpdateEvent
    data object SecondaryActionClicked : OptionalUpdateEvent
    data class FieldChanged(
        val key: String,
        val value: String,
    ) : OptionalUpdateEvent
}

val optionalUpdateNavigation: RouteDefinition = CryptoVpnRouteSpec.optionalUpdate

fun optionalUpdatePreviewState(): OptionalUpdateUiState = OptionalUpdateUiState()
