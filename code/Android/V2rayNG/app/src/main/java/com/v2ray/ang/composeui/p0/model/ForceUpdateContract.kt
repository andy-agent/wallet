package com.v2ray.ang.composeui.p0.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class ForceUpdateUiState(
    val title: String = "需要更新应用",
    val subtitle: String = "FORCE UPDATE",
    val badge: String = "P0 · CORE",
    val summary: String = "当前版本暂不支持继续使用，请安装最新版本。",
    val primaryActionLabel: String = "立即更新",
    val secondaryActionLabel: String? = "返回",
    val heroAccent: String = "force_update",
    val metrics: List<FeatureMetric> = listOf(
        FeatureMetric(label = "升级状态", value = "需要更新"),
        FeatureMetric(label = "影响范围", value = "当前应用入口"),
        FeatureMetric(label = "处理方式", value = "安装最新版本"),
    ),
    val fields: List<FeatureField> = emptyList(),
    val highlights: List<FeatureListItem> = listOf(
        FeatureListItem(
            title = "版本校验",
            subtitle = "当前版本低于最低可用要求，升级后才能继续使用。",
            trailing = "FORCE",
            badge = "P0",
        ),
    ),
    val checklist: List<FeatureBullet> = listOf(
        FeatureBullet(title = "下载新版本", detail = "前往更新入口下载安装最新版本。"),
        FeatureBullet(title = "完成后重试", detail = "安装完成后重新打开应用继续操作。"),
    ),
    val note: String = "更新检查当前来自客户端运行时状态；安装完成后重新打开应用。",
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
