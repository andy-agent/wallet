package com.cryptovpn.ui.p2extended.model

import com.cryptovpn.navigation.CryptoVpnRouteSpec
import com.cryptovpn.navigation.RouteDefinition
import com.cryptovpn.ui.common.model.FeatureBullet
import com.cryptovpn.ui.common.model.FeatureField
import com.cryptovpn.ui.common.model.FeatureListItem
import com.cryptovpn.ui.common.model.FeatureMetric

data class DappBrowserRouteArgs(val entry: String = "jup.ag")

data class DappBrowserUiState(
        val title: String = "DApp 浏览器",
        val subtitle: String = "DAPP BROWSER",
        val badge: String = "P2 · EXTENDED",
        val summary: String = "DApp 浏览器页提供搜索、收藏与安全评分入口。",
        val primaryActionLabel: String = "查看 WalletConnect 会话",
        val secondaryActionLabel: String? = "返回钱包首页",
        val heroAccent: String = "dapp_browser",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "最近访问", value = "5"),
    FeatureMetric(label = "安全评分", value = "88"),
    FeatureMetric(label = "分类", value = "4 类"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "entry", label = "站点入口", value = "jup.ag", supportingText = "支持 URL、域名或已收藏 DApp"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "DApp 浏览器页提供搜索、收藏与安全评分入口。", trailing = "dapp_browser", badge = "P2 扩展页"),
    FeatureListItem(title = "导航参数", subtitle = "entry", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "站点入口", trailing = "1 项", badge = "Form"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "DApp 浏览器 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 DappBrowserPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "DApp 浏览器 已按 P2 扩展页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
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
