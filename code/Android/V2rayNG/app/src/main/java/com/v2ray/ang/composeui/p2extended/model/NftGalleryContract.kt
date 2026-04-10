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
        val summary: String = "NFT 画廊页用于展示收藏、地板价与快捷打开市场。",
        val primaryActionLabel: String = "返回钱包首页",
        val secondaryActionLabel: String? = "打开 DApp 浏览器",
        val heroAccent: String = "nft_gallery",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "收藏系列", value = "8"),
    FeatureMetric(label = "地板价", value = "12.6 SOL"),
    FeatureMetric(label = "在售数量", value = "3"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "NFT 画廊页用于展示收藏、地板价与快捷打开市场。", trailing = "nft_gallery", badge = "P2 扩展页"),
    FeatureListItem(title = "导航参数", subtitle = "当前页面无必填导航参数", trailing = "0 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "当前页面以信息展示与确认动作为主", trailing = "0 项", badge = "Info"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "NFT 画廊 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 NftGalleryPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "当前页面无必填参数，但已纳入统一 RouteSpec 台账。"),
),
        val note: String = "NFT 画廊 已按 P2 扩展页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
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
