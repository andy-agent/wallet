package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class SwapRouteArgs(val fromAsset: String = "USDT", val toAsset: String = "SOL")

data class SwapUiState(
        val title: String = "币币兑换",
        val subtitle: String = "SWAP",
        val badge: String = "P2 · EXTENDED",
        val summary: String = "Swap 页展示源币、目标币、滑点与预估接收数量。",
        val primaryActionLabel: String = "提交兑换预览",
        val secondaryActionLabel: String? = "回到钱包首页",
        val heroAccent: String = "swap",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "源币", value = "USDT 580"),
    FeatureMetric(label = "目标币", value = "SOL 82.6"),
    FeatureMetric(label = "价格影响", value = "0.5%"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "amount", label = "兑换数量", value = "580", supportingText = "源币数量"),
    FeatureField(key = "slippage", label = "滑点容忍", value = "0.5", supportingText = "默认演示值，单位 %"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "Swap 页展示源币、目标币、滑点与预估接收数量。", trailing = "swap", badge = "P2 扩展页"),
    FeatureListItem(title = "导航参数", subtitle = "fromAsset / toAsset", trailing = "2 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "兑换数量、滑点容忍", trailing = "2 项", badge = "Form"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "币币兑换 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 SwapPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "币币兑换 已按 P2 扩展页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
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
