package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class DappBrowserRouteArgs(val entry: String = "")

data class DappBrowserUiState(
        val title: String = "DApp 浏览器",
        val subtitle: String = "DAPP BROWSER",
        val badge: String = "P2 · EXTENDED",
        val summary: String = "DApp 浏览器页仅展示真实站点入口与风控状态，不再预置固定站点。",
        val primaryActionLabel: String = "查看 WalletConnect 会话",
        val secondaryActionLabel: String? = "返回钱包首页",
        val heroAccent: String = "dapp_browser",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "最近访问", value = "待同步"),
    FeatureMetric(label = "安全评分", value = "待校验"),
    FeatureMetric(label = "分类", value = "待同步"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "entry", label = "站点入口", value = "", supportingText = "输入真实 URL、域名或已收藏 DApp"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "已收藏站点", subtitle = "收藏夹数据待真实钱包浏览器能力接入。", trailing = "待接入", badge = "收藏"),
    FeatureListItem(title = "安全评级", subtitle = "站点风控评分待安全服务返回。", trailing = "待校验", badge = "风控"),
    FeatureListItem(title = "未知来源", subtitle = "未校验域名需显式提示谨慎访问。", trailing = "阻塞", badge = "风险"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "站点访问", detail = "未接入真实浏览能力前仅保留阻塞态。"),
    FeatureBullet(title = "搜索历史", detail = "不再展示默认站点或固定域名。"),
    FeatureBullet(title = "风控提示", detail = "域名校验结果必须来自真实服务。"),
),
        val note: String = "当前不再预置特定 DApp 域名；仅展示真实输入和显式风控状态。",
    )

    sealed interface DappBrowserEvent {
        data object Refresh : DappBrowserEvent
        data object PrimaryActionClicked : DappBrowserEvent
        data object SecondaryActionClicked : DappBrowserEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : DappBrowserEvent
    }

    val dappBrowserNavigation: RouteDefinition = CryptoVpnRouteSpec.dappBrowser

    fun dappBrowserPreviewState(): DappBrowserUiState = DappBrowserUiState()
