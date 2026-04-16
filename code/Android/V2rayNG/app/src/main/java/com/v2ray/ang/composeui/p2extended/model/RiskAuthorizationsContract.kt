package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class RiskAuthorizationsUiState(
        val title: String = "风险授权",
        val subtitle: String = "RISK AUTHORIZATIONS",
        val badge: String = "P2 · EXTENDED",
        val summary: String = "风险授权信息待接口返回。",
        val primaryActionLabel: String = "返回钱包首页",
        val secondaryActionLabel: String? = "进入安全中心",
        val heroAccent: String = "risk_authorizations",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "授权总数", value = "待接口返回"),
    FeatureMetric(label = "高风险", value = "待接口返回"),
    FeatureMetric(label = "已撤销", value = "待接口返回"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "授权状态", subtitle = "授权记录与风险等级待接口返回", trailing = "待接口返回", badge = "Runtime"),
    FeatureListItem(title = "撤销记录", subtitle = "撤销历史待接口返回", trailing = "待同步", badge = "Audit"),
    FeatureListItem(title = "空态处理", subtitle = "当前无授权时显示空态", trailing = "空态", badge = "Empty"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "数据来源", detail = "授权明细和风险等级必须来自真实授权记录。"),
    FeatureBullet(title = "空态策略", detail = "无授权时显示当前无数据，不展示演示数量。"),
    FeatureBullet(title = "异常处理", detail = "接口失败时应展示真实错误。"),
    FeatureBullet(title = "能力状态", detail = "未接入接口前保持待接口返回语义。"),
),
        val note: String = "风险授权页面等待真实授权数据返回。",
    )

    sealed interface RiskAuthorizationsEvent {
        data object Refresh : RiskAuthorizationsEvent
        data object PrimaryActionClicked : RiskAuthorizationsEvent
        data object SecondaryActionClicked : RiskAuthorizationsEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : RiskAuthorizationsEvent
    }

    val riskAuthorizationsNavigation: RouteDefinition = CryptoVpnRouteSpec.riskAuthorizations

    fun riskAuthorizationsPreviewState(): RiskAuthorizationsUiState = RiskAuthorizationsUiState()
