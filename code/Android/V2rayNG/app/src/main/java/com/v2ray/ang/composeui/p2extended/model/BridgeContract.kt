package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class BridgeRouteArgs(val fromChainId: String = "tron", val toChainId: String = "solana")

data class BridgeUiState(
        val title: String = "跨链桥接",
        val subtitle: String = "BRIDGE",
        val badge: String = "P2 · EXTENDED",
        val summary: String = "Bridge 页补齐跨链资产搬运、目标地址与预计到账时间。",
        val primaryActionLabel: String = "继续桥接预览",
        val secondaryActionLabel: String? = "回到钱包首页",
        val heroAccent: String = "bridge",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "起始链", value = "TRON"),
    FeatureMetric(label = "目标链", value = "Solana"),
    FeatureMetric(label = "预计耗时", value = "3 分钟"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "to", label = "目标地址", value = "So1...x92k", supportingText = "桥接到账地址"),
    FeatureField(key = "amount", label = "桥接数量", value = "580.00", supportingText = "默认桥接金额"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "Bridge 页补齐跨链资产搬运、目标地址与预计到账时间。", trailing = "bridge", badge = "P2 扩展页"),
    FeatureListItem(title = "导航参数", subtitle = "fromChainId / toChainId", trailing = "2 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "目标地址、桥接数量", trailing = "2 项", badge = "Form"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "跨链桥接 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 BridgePreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "跨链桥接 已按 P2 扩展页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
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
