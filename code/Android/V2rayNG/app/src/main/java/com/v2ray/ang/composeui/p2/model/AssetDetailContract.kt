package com.v2ray.ang.composeui.p2.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class AssetDetailRouteArgs(val assetId: String = "USDT", val chainId: String = "tron")

data class AssetDetailUiState(
        val title: String = "资产详情",
        val subtitle: String = "ASSET DETAIL",
        val badge: String = "稳定币",
        val summary: String = "稳定币资产详情与最近交易，支持直接衔接到发送、收款和订单链路。",
        val primaryActionLabel: String = "发送资产",
        val secondaryActionLabel: String? = "收款",
        val heroAccent: String = "asset_detail",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "资产", value = "USDT"),
    FeatureMetric(label = "余额", value = "12,840 USDT"),
    FeatureMetric(label = "今日", value = "+0.12%"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "支付年度 Pro 套餐", subtitle = "TRON · 58.00 USDT", trailing = "成功"),
    FeatureListItem(title = "佣金返还", subtitle = "Solana · 18.50 USDT", trailing = "确认中"),
    FeatureListItem(title = "发送到冷钱包", subtitle = "TRON · 500 USDT", trailing = "已签名"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "分布", detail = "TRON · 8,420 USDT"),
    FeatureBullet(title = "分布", detail = "Solana · 4,420 USDT"),
    FeatureBullet(title = "可支付", detail = "VPN 年付 · 支持一键扣款"),
    FeatureBullet(title = "区间", detail = "24H / 7D / 30D / 入账出账"),
),
        val note: String = "用于续费最多",
    )

    sealed interface AssetDetailEvent {
        data object Refresh : AssetDetailEvent
        data object PrimaryActionClicked : AssetDetailEvent
        data object SecondaryActionClicked : AssetDetailEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : AssetDetailEvent
    }

    val assetDetailNavigation: RouteDefinition = CryptoVpnRouteSpec.assetDetail

    fun assetDetailPreviewState(): AssetDetailUiState = AssetDetailUiState()
