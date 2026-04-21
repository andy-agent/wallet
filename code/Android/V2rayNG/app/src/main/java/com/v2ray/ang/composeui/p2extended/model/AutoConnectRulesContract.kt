package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class AutoConnectRulesUiState(
        val title: String = "自动连接规则",
        val subtitle: String = "AUTO CONNECT RULES",
        val badge: String = "P2 · EXTENDED",
        val summary: String = "自动连接规则待接口返回。",
        val primaryActionLabel: String = "保存并返回首页",
        val secondaryActionLabel: String? = "进入安全中心",
        val heroAccent: String = "auto_connect_rules",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "规则数量", value = "待接口返回"),
    FeatureMetric(label = "Wi-Fi 场景", value = "待接口返回"),
    FeatureMetric(label = "可信网络", value = "待接口返回"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "rule", label = "默认规则", value = "", supportingText = "当前无规则数据"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "规则状态", subtitle = "自动连接规则列表待接口返回", trailing = "待接口返回", badge = "Runtime"),
    FeatureListItem(title = "网络策略", subtitle = "公共网络、后台唤醒和应用策略待接口返回", trailing = "待同步", badge = "Rules"),
    FeatureListItem(title = "空态处理", subtitle = "未配置规则时显示当前无规则数据", trailing = "空态", badge = "Empty"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "数据来源", detail = "自动连接规则必须来自真实配置数据。"),
    FeatureBullet(title = "空态策略", detail = "无配置时显示当前无规则数据，不展示演示规则。"),
    FeatureBullet(title = "异常处理", detail = "保存或读取失败时应展示真实错误。"),
    FeatureBullet(title = "能力状态", detail = "未接入配置接口前应保持待接口返回语义。"),
),
        val note: String = "自动连接规则等待真实配置数据返回。",
    )

    sealed interface AutoConnectRulesEvent {
        data object Refresh : AutoConnectRulesEvent
        data object PrimaryActionClicked : AutoConnectRulesEvent
        data object SecondaryActionClicked : AutoConnectRulesEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : AutoConnectRulesEvent
    }

    val autoConnectRulesNavigation: RouteDefinition = CryptoVpnRouteSpec.autoConnectRules

    fun autoConnectRulesPreviewState(): AutoConnectRulesUiState = AutoConnectRulesUiState()
