package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition

data class GasSettingsRouteArgs(val chainId: String = "ethereum")

data class GasSettingsUiState(
    val title: String = "Gas 设置",
    val subtitle: String = "GAS SETTINGS",
    val badge: String = "同步中",
    val summary: String = "正在检查链上估算能力；若未接真实 gas estimator，将显示阻塞说明。",
    val primaryActionLabel: String? = null,
    val secondaryActionLabel: String? = null,
    val heroAccent: String = "gas_settings",
    val metrics: List<FeatureMetric> = listOf(
        FeatureMetric(label = "目标链", value = "读取中"),
        FeatureMetric(label = "推荐档位", value = "读取中"),
        FeatureMetric(label = "估算状态", value = "读取中"),
    ),
    val fields: List<FeatureField> = listOf(
        FeatureField(key = "maxFee", label = "Max Fee", value = "", supportingText = "等待真实链上估算或阻塞说明。"),
        FeatureField(key = "priorityFee", label = "Priority Fee", value = "", supportingText = "若能力未接通，将显示阻塞说明。"),
    ),
    val highlights: List<FeatureListItem> = listOf(
        FeatureListItem(title = "当前状态", subtitle = "正在检查 Gas 估算能力", trailing = "", badge = "LOADING"),
    ),
    val checklist: List<FeatureBullet> = listOf(
        FeatureBullet(title = "说明", detail = "默认态不再展示演示费率、档位和假保存动作。"),
    ),
    val note: String = "刷新后会替换为真实状态或明确阻塞说明。",
)

sealed interface GasSettingsEvent {
    data object Refresh : GasSettingsEvent
    data object PrimaryActionClicked : GasSettingsEvent
    data object SecondaryActionClicked : GasSettingsEvent
    data class FieldChanged(
        val key: String,
        val value: String,
    ) : GasSettingsEvent
}

val gasSettingsNavigation: RouteDefinition = CryptoVpnRouteSpec.gasSettings

fun gasSettingsPreviewState(): GasSettingsUiState = GasSettingsUiState()
