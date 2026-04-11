package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition

data class DappBrowserRouteArgs(val entry: String = "jup.ag")

data class DappBrowserUiState(
    val title: String = "DApp 浏览器",
    val subtitle: String = "DAPP BROWSER",
    val badge: String = "同步中",
    val summary: String = "正在读取浏览器入口和会话状态；若容器能力未接通，将显示阻塞说明。",
    val primaryActionLabel: String? = null,
    val secondaryActionLabel: String? = null,
    val heroAccent: String = "dapp_browser",
    val metrics: List<FeatureMetric> = listOf(
        FeatureMetric(label = "入口", value = "--"),
        FeatureMetric(label = "会话状态", value = "读取中"),
        FeatureMetric(label = "浏览器容器", value = "读取中"),
    ),
    val fields: List<FeatureField> = listOf(
        FeatureField(key = "entry", label = "站点入口", value = "", supportingText = "等待真实浏览器容器或阻塞说明。"),
    ),
    val highlights: List<FeatureListItem> = listOf(
        FeatureListItem(title = "当前状态", subtitle = "正在读取浏览器能力", trailing = "", badge = "LOADING"),
    ),
    val checklist: List<FeatureBullet> = listOf(
        FeatureBullet(title = "说明", detail = "默认态不再展示演示站点评分、收藏数量和假入口。"),
    ),
    val note: String = "刷新后会替换为真实状态或明确阻塞说明。",
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
