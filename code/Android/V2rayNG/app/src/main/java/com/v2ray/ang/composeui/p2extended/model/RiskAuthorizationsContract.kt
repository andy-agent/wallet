package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition

data class RiskAuthorizationsUiState(
    val title: String = "风险授权",
    val subtitle: String = "RISK AUTHORIZATIONS",
    val badge: String = "同步中",
    val summary: String = "正在读取授权记录和风险状态；若没有真实来源，将显示空态或阻塞说明。",
    val primaryActionLabel: String? = null,
    val secondaryActionLabel: String? = null,
    val heroAccent: String = "risk_authorizations",
    val metrics: List<FeatureMetric> = listOf(
        FeatureMetric(label = "授权总数", value = "读取中"),
        FeatureMetric(label = "高风险", value = "读取中"),
        FeatureMetric(label = "会话状态", value = "读取中"),
    ),
    val fields: List<FeatureField> = emptyList(),
    val highlights: List<FeatureListItem> = listOf(
        FeatureListItem(title = "当前状态", subtitle = "正在读取授权记录", trailing = "", badge = "LOADING"),
    ),
    val checklist: List<FeatureBullet> = listOf(
        FeatureBullet(title = "说明", detail = "默认态不再展示演示授权数量和假操作入口。"),
    ),
    val note: String = "刷新后会替换为真实状态、空态或明确阻塞说明。",
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
