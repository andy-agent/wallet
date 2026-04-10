package com.cryptovpn.ui.p2extended.model

import com.cryptovpn.navigation.CryptoVpnRouteSpec
import com.cryptovpn.navigation.RouteDefinition
import com.cryptovpn.ui.common.model.FeatureBullet
import com.cryptovpn.ui.common.model.FeatureField
import com.cryptovpn.ui.common.model.FeatureListItem
import com.cryptovpn.ui.common.model.FeatureMetric

data class ChainManagerRouteArgs(val walletId: String = "primary_wallet")

data class ChainManagerUiState(
        val title: String = "链管理",
        val subtitle: String = "CHAIN MANAGER",
        val badge: String = "P2 · EXTENDED",
        val summary: String = "链管理页用于启停链、排序、查看 RPC 状态与添加扩展网络。",
        val primaryActionLabel: String = "添加自定义代币",
        val secondaryActionLabel: String? = "返回钱包首页",
        val heroAccent: String = "chain_manager",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "已启用", value = "5 条"),
    FeatureMetric(label = "自定义链", value = "1 条"),
    FeatureMetric(label = "已停用", value = "2 条"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "链管理页用于启停链、排序、查看 RPC 状态与添加扩展网络。", trailing = "chain_manager", badge = "P2 扩展页"),
    FeatureListItem(title = "导航参数", subtitle = "walletId", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "当前页面以信息展示与确认动作为主", trailing = "0 项", badge = "Info"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "链管理 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 ChainManagerPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "链管理 已按 P2 扩展页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
    )

    sealed interface ChainManagerEvent {
        data object Refresh : ChainManagerEvent
        data object PrimaryActionClicked : ChainManagerEvent
        data object SecondaryActionClicked : ChainManagerEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : ChainManagerEvent
    }

    val chainManagerNavigation: RouteDefinition = CryptoVpnRouteSpec.chainManager

    fun chainManagerPreviewState(): ChainManagerUiState = ChainManagerUiState()
