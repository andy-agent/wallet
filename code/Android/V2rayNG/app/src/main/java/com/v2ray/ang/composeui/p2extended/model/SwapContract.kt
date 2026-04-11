package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition

data class SwapRouteArgs(val fromAsset: String = "USDT", val toAsset: String = "SOL")

data class SwapUiState(
    val title: String = "币币兑换",
    val subtitle: String = "SWAP",
    val badge: String = "同步中",
    val summary: String = "正在读取兑换上下文；若未接真实报价与执行能力，将显示阻塞说明。",
    val primaryActionLabel: String? = null,
    val secondaryActionLabel: String? = null,
    val heroAccent: String = "swap",
    val metrics: List<FeatureMetric> = listOf(
        FeatureMetric(label = "源资产", value = "--"),
        FeatureMetric(label = "目标资产", value = "--"),
        FeatureMetric(label = "报价状态", value = "读取中"),
    ),
    val fields: List<FeatureField> = listOf(
        FeatureField(key = "amount", label = "兑换数量", value = "", supportingText = "等待真实报价能力同步。"),
        FeatureField(key = "slippage", label = "滑点容忍", value = "", supportingText = "若能力未接通，将显示阻塞说明。"),
    ),
    val highlights: List<FeatureListItem> = listOf(
        FeatureListItem(title = "当前状态", subtitle = "正在读取兑换能力", trailing = "", badge = "LOADING"),
    ),
    val checklist: List<FeatureBullet> = listOf(
        FeatureBullet(title = "说明", detail = "默认态不再展示演示价格、假表单和假按钮。"),
    ),
    val note: String = "刷新后会替换为真实状态或明确阻塞说明。",
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
