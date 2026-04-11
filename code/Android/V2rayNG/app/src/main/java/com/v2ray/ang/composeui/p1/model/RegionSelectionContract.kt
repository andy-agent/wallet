package com.v2ray.ang.composeui.p1.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class RegionSelectionUiState(
    val title: String = "选择可用区域",
    val subtitle: String = "REGION SELECTION",
    val badge: String = "P1 · REAL",
    val summary: String = "区域页直接读取真实 VPN 区域列表，不再展示本地样本节点。",
    val primaryActionLabel: String = "返回首页继续连接",
    val secondaryActionLabel: String? = "刷新区域列表",
    val heroAccent: String = "region_selection",
    val stateInfo: P1StateInfo = P1StateInfo(),
    val metrics: List<FeatureMetric> = emptyList(),
    val searchField: FeatureField = FeatureField("search", "搜索区域", ""),
    val fields: List<FeatureField> = listOf(searchField),
    val highlights: List<FeatureListItem> = emptyList(),
    val regions: List<P1RegionOption> = emptyList(),
    val selectedRegionCode: String? = null,
    val checklist: List<FeatureBullet> = emptyList(),
    val note: String = "",
)

    sealed interface RegionSelectionEvent {
        data object Refresh : RegionSelectionEvent
        data object PrimaryActionClicked : RegionSelectionEvent
        data object SecondaryActionClicked : RegionSelectionEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : RegionSelectionEvent
    }

    val regionSelectionNavigation: RouteDefinition = CryptoVpnRouteSpec.regionSelection

    fun regionSelectionPreviewState(): RegionSelectionUiState = RegionSelectionUiState(
        stateInfo = P1StateInfo(P1ScreenState.Content),
        metrics = listOf(
            FeatureMetric("可用区域", "2"),
            FeatureMetric("允许访问", "2"),
            FeatureMetric("状态", "PREVIEW"),
        ),
        regions = listOf(
            P1RegionOption("JP", "Japan", "Tokyo optimized", "ONLINE", "CORE", "ONLINE", true),
            P1RegionOption("SG", "Singapore", "Low latency", "ONLINE", "CORE", "ONLINE", true),
        ),
        selectedRegionCode = "JP",
    )
