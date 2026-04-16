package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class ChainManagerRouteArgs(val walletId: String = "primary_wallet")

data class ChainManagerUiState(
        val title: String = "链管理",
        val subtitle: String = "CHAIN MANAGER",
        val badge: String = "P2 · EXTENDED",
        val summary: String = "链管理页用于查看启用链、RPC 状态与扩展网络接入情况。",
        val primaryActionLabel: String = "添加自定义代币",
        val secondaryActionLabel: String? = "返回钱包首页",
        val heroAccent: String = "chain_manager",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "已启用", value = "状态待同步"),
    FeatureMetric(label = "自定义链", value = "待接入"),
    FeatureMetric(label = "RPC 健康度", value = "待同步"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "当前钱包", subtitle = "walletId 用于读取真实链配置。", trailing = "1 个", badge = "参数"),
    FeatureListItem(title = "网络状态", subtitle = "RPC 延迟与可用性待真实接口同步。", trailing = "待同步", badge = "RPC"),
    FeatureListItem(title = "扩展网络", subtitle = "自定义网络管理能力待接入。", trailing = "阻塞", badge = "扩展"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "读取链配置", detail = "仅展示真实链启停状态，不再回退默认数量。"),
    FeatureBullet(title = "RPC 健康检查", detail = "待链探测接口接入后再展示实时值。"),
    FeatureBullet(title = "扩展链写入", detail = "未接入前保持显式阻塞态。"),
),
        val note: String = "当前以真实链配置或阻塞态为准，不再注入默认说明文本。",
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
