package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class ChainManagerRouteArgs(
    val walletId: String = "primary_wallet",
    val chainId: String = "solana",
)

data class ChainManagerUiState(
        val title: String = "代币管理",
        val subtitle: String = "TOKEN MANAGER",
        val badge: String = "P2 · EXTENDED",
        val summary: String = "代币管理页用于查看当前钱包某条链下的资产，并管理是否展示与自定义代币。",
        val primaryActionLabel: String = "添加自定义代币",
        val secondaryActionLabel: String? = "返回钱包首页",
        val heroAccent: String = "chain_manager",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "当前链", value = "待同步"),
    FeatureMetric(label = "代币数量", value = "0"),
    FeatureMetric(label = "可见资产", value = "0"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "当前钱包", subtitle = "walletId 用于读取真实链下资产。", trailing = "1 个", badge = "参数"),
    FeatureListItem(title = "资产列表", subtitle = "按当前链展示真实代币与余额。", trailing = "待同步", badge = "ASSET"),
    FeatureListItem(title = "自定义代币", subtitle = "支持把链上合约代币加入资产视图。", trailing = "入口", badge = "TOKEN"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "真实资产", detail = "仅展示当前钱包当前链的真实代币与余额。"),
    FeatureBullet(title = "显示控制", detail = "后续支持隐藏垃圾币与恢复已隐藏代币。"),
    FeatureBullet(title = "自定义代币", detail = "通过合约地址手动添加当前链代币。"),
),
        val note: String = "当前以真实资产数据为准；未接入隐藏/排序时保持显式说明。",
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
