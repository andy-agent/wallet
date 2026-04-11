package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition

data class NftGalleryUiState(
    val title: String = "NFT 画廊",
    val subtitle: String = "NFT GALLERY",
    val badge: String = "同步中",
    val summary: String = "正在读取 NFT 数据源；若未接索引能力，将显示空态或阻塞说明。",
    val primaryActionLabel: String? = null,
    val secondaryActionLabel: String? = null,
    val heroAccent: String = "nft_gallery",
    val metrics: List<FeatureMetric> = listOf(
        FeatureMetric(label = "收藏系列", value = "读取中"),
        FeatureMetric(label = "地板价", value = "读取中"),
        FeatureMetric(label = "在售数量", value = "读取中"),
    ),
    val fields: List<FeatureField> = emptyList(),
    val highlights: List<FeatureListItem> = listOf(
        FeatureListItem(title = "当前状态", subtitle = "正在读取 NFT 数据源", trailing = "", badge = "LOADING"),
    ),
    val checklist: List<FeatureBullet> = listOf(
        FeatureBullet(title = "说明", detail = "默认态不再展示演示藏品数量、价格和假入口。"),
    ),
    val note: String = "刷新后会替换为真实状态、空态或明确阻塞说明。",
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
