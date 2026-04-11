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
        val badge: String = "P2 · EXTENDED",
        val summary: String = "Gas 设置页支持慢 / 中 / 快档位与高级自定义参数。",
        val primaryActionLabel: String? = "保存并返回发送页",
        val secondaryActionLabel: String? = "取消修改",
        val heroAccent: String = "gas_settings",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "推荐档位", value = "Medium"),
    FeatureMetric(label = "Base Fee", value = "21 gwei"),
    FeatureMetric(label = "优先费", value = "2 gwei"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "maxFee", label = "Max Fee", value = "28", supportingText = "可切换到真实单位与链上估算"),
    FeatureField(key = "priorityFee", label = "Priority Fee", value = "2", supportingText = "与网络拥堵动态联动"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "Gas 设置页支持慢 / 中 / 快档位与高级自定义参数。", trailing = "gas_settings", badge = "P2 扩展页"),
    FeatureListItem(title = "导航参数", subtitle = "chainId", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "Max Fee、Priority Fee", trailing = "2 项", badge = "Form"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "Gas 设置 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 GasSettingsPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "Gas 设置 已按 P2 扩展页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
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
