package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class BridgeRouteArgs(val fromChainId: String = "", val toChainId: String = "")

data class BridgeUiState(
        val title: String = "跨链桥接",
        val subtitle: String = "BRIDGE",
        val badge: String = "未接入",
        val summary: String = "",
        val primaryActionLabel: String = "继续",
        val secondaryActionLabel: String? = "回到钱包首页",
        val heroAccent: String = "bridge",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "起始链", value = "待选择"),
    FeatureMetric(label = "目标链", value = "待选择"),
    FeatureMetric(label = "预计耗时", value = "待评估"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "to", label = "目标地址", value = "", supportingText = ""),
    FeatureField(key = "amount", label = "桥接数量", value = "", supportingText = ""),
),
        val highlights: List<FeatureListItem> = emptyList(),
        val checklist: List<FeatureBullet> = emptyList(),
        val note: String = "",
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
