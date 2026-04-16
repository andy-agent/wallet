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
        val summary: String = "",
        val primaryActionLabel: String = "发送资产",
        val secondaryActionLabel: String? = "收款",
        val heroAccent: String = "asset_detail",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "资产", value = "USDT"),
    FeatureMetric(label = "余额", value = "--"),
    FeatureMetric(label = "今日", value = "--"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "链路状态", subtitle = "当前未接入该资产的服务端明细", trailing = "", badge = "EMPTY"),
    FeatureListItem(title = "最近订单", subtitle = "暂无记录", trailing = "--", badge = "REAL"),
    FeatureListItem(title = "地址状态", subtitle = "暂无公开地址", trailing = "", badge = "ADDR"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "网络", detail = "--"),
    FeatureBullet(title = "支付订单数", detail = "0"),
    FeatureBullet(title = "公开地址数", detail = "0"),
    FeatureBullet(title = "数据源", detail = "wallet/overview"),
),
        val note: String = "",
        val banner: P2SurfaceBanner = p2ReadyBanner(),
        val feedbackMessage: String? = null,
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
