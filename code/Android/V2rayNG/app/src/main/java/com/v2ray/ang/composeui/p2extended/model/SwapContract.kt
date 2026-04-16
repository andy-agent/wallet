package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class SwapRouteArgs(val fromAsset: String = "", val toAsset: String = "")

data class SwapUiState(
        val title: String = "币币兑换",
        val subtitle: String = "SWAP",
        val badge: String = "未接入",
        val summary: String = "",
        val primaryActionLabel: String = "继续",
        val secondaryActionLabel: String? = "回到钱包首页",
        val heroAccent: String = "swap",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "源币", value = "待选择"),
    FeatureMetric(label = "目标币", value = "待选择"),
    FeatureMetric(label = "价格影响", value = "待报价"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "amount", label = "兑换数量", value = "", supportingText = ""),
    FeatureField(key = "slippage", label = "滑点容忍", value = "", supportingText = ""),
),
        val highlights: List<FeatureListItem> = emptyList(),
        val checklist: List<FeatureBullet> = emptyList(),
        val note: String = "",
    )

    sealed interface SwapEvent {
        data object Refresh : SwapEvent
        data object PrimaryActionClicked : SwapEvent
        data object SecondaryActionClicked : SwapEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : SwapEvent
    }

    val swapNavigation: RouteDefinition = CryptoVpnRouteSpec.swap

    fun swapPreviewState(): SwapUiState = SwapUiState()
