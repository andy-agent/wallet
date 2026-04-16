package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class GasSettingsRouteArgs(val chainId: String = "ethereum")

data class GasSettingsUiState(
        val title: String = "Gas 设置",
        val subtitle: String = "GAS SETTINGS",
        val badge: String = "待估算",
        val summary: String = "等待链上 gas 估算结果返回。",
        val primaryActionLabel: String = "保存并返回发送页",
        val secondaryActionLabel: String? = "取消修改",
        val heroAccent: String = "gas_settings",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "推荐档位", value = "待估算"),
    FeatureMetric(label = "Base Fee", value = "待估算"),
    FeatureMetric(label = "优先费", value = "待估算"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "maxFee", label = "Max Fee", value = "", supportingText = "等待链上估算返回"),
    FeatureField(key = "priorityFee", label = "Priority Fee", value = "", supportingText = "等待优先费估算返回"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "估算状态", subtitle = "等待链上返回 gas 推荐值。", trailing = "待估算", badge = "State"),
    FeatureListItem(title = "导航参数", subtitle = "chainId", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单状态", subtitle = "Max Fee 与 Priority Fee 待返回", trailing = "2 项", badge = "Form"),
    FeatureListItem(title = "数据来源", subtitle = "由链上估算接口实时返回。", trailing = "Runtime", badge = "Source"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "链上估算", detail = "未返回真实 gas 前，不展示伪造费率。"),
    FeatureBullet(title = "参数保存", detail = "保存前需确认估算结果已返回。"),
    FeatureBullet(title = "导航参数", detail = "根据 chainId 选择链路。"),
),
        val note: String = "当前未返回 gas 估算结果。",
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
