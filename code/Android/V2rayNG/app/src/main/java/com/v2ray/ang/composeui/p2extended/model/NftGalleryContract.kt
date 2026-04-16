package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class NftGalleryUiState(
        val title: String = "NFT 画廊",
        val subtitle: String = "NFT GALLERY",
        val badge: String = "P2 · EXTENDED",
        val summary: String = "NFT 资产数据待接口返回。",
        val primaryActionLabel: String = "返回钱包首页",
        val secondaryActionLabel: String? = "打开 DApp 浏览器",
        val heroAccent: String = "nft_gallery",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "收藏系列", value = "待接口返回"),
    FeatureMetric(label = "地板价", value = "待接口返回"),
    FeatureMetric(label = "在售数量", value = "待接口返回"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "资产状态", subtitle = "NFT 持仓与报价待接口返回", trailing = "待接口返回", badge = "Runtime"),
    FeatureListItem(title = "市场数据", subtitle = "地板价和在售状态待市场接口返回", trailing = "待同步", badge = "Market"),
    FeatureListItem(title = "空态处理", subtitle = "当前无 NFT 资产时显示空态", trailing = "空态", badge = "Empty"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "数据来源", detail = "NFT 列表和市场数据必须来自真实钱包与市场接口。"),
    FeatureBullet(title = "空态策略", detail = "无 NFT 资产时显示当前无数据，不展示演示价格。"),
    FeatureBullet(title = "异常处理", detail = "接口失败时应展示真实错误。"),
    FeatureBullet(title = "能力状态", detail = "未接入 NFT 数据接口前保持待接口返回语义。"),
),
        val note: String = "NFT 画廊等待真实资产与市场数据返回。",
    )

    sealed interface NftGalleryEvent {
        data object Refresh : NftGalleryEvent
        data object PrimaryActionClicked : NftGalleryEvent
        data object SecondaryActionClicked : NftGalleryEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : NftGalleryEvent
    }

    val nftGalleryNavigation: RouteDefinition = CryptoVpnRouteSpec.nftGallery

    fun nftGalleryPreviewState(): NftGalleryUiState = NftGalleryUiState()
