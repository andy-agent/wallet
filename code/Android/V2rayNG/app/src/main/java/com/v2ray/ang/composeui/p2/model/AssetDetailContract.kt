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
        val badge: String = "P2 · BASE",
        val summary: String = "资产详情页承接行情、持仓、最近交易与快捷发送/收款操作。",
        val primaryActionLabel: String = "发送该资产",
        val secondaryActionLabel: String? = "收款到当前地址",
        val heroAccent: String = "asset_detail",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "资产", value = "USDT"),
    FeatureMetric(label = "持仓", value = "12,840"),
    FeatureMetric(label = "盈亏", value = "+1.2%"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "资产详情页承接行情、持仓、最近交易与快捷发送/收款操作。", trailing = "asset_detail", badge = "P2 基础文档页"),
    FeatureListItem(title = "导航参数", subtitle = "assetId / chainId", trailing = "2 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "当前页面以信息展示与确认动作为主", trailing = "0 项", badge = "Info"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "资产详情 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 AssetDetailPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "资产详情 已按 P2 基础文档页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
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
