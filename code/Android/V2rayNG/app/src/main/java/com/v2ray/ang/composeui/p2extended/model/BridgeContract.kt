package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition

data class BridgeRouteArgs(val fromChainId: String = "tron", val toChainId: String = "solana")

data class BridgeUiState(
    val title: String = "跨链桥接",
    val subtitle: String = "BRIDGE",
    val badge: String = "同步中",
    val summary: String = "正在读取桥接上下文；若未接真实 bridge quote / execute，将显示阻塞说明。",
    val primaryActionLabel: String? = null,
    val secondaryActionLabel: String? = null,
    val heroAccent: String = "bridge",
    val metrics: List<FeatureMetric> = listOf(
        FeatureMetric(label = "起始链", value = "--"),
        FeatureMetric(label = "目标链", value = "--"),
        FeatureMetric(label = "桥接状态", value = "读取中"),
    ),
    val fields: List<FeatureField> = listOf(
        FeatureField(key = "to", label = "目标地址", value = "", supportingText = "等待真实桥接能力同步。"),
        FeatureField(key = "amount", label = "桥接数量", value = "", supportingText = "若能力未接通，将显示阻塞说明。"),
    ),
    val highlights: List<FeatureListItem> = listOf(
        FeatureListItem(title = "当前状态", subtitle = "正在读取桥接能力", trailing = "", badge = "LOADING"),
    ),
    val checklist: List<FeatureBullet> = listOf(
        FeatureBullet(title = "说明", detail = "默认态不再展示演示地址、金额和假桥接动作。"),
    ),
    val note: String = "刷新后会替换为真实状态或明确阻塞说明。",
)

sealed interface BridgeEvent {
    data object Refresh : BridgeEvent
    data object PrimaryActionClicked : BridgeEvent
    data object SecondaryActionClicked : BridgeEvent
    data class FieldChanged(
        val key: String,
        val value: String,
    ) : BridgeEvent
}

val bridgeNavigation: RouteDefinition = CryptoVpnRouteSpec.bridge

fun bridgePreviewState(): BridgeUiState = BridgeUiState()
