package com.v2ray.ang.composeui.p1.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition

data class RegionOptionUi(
    val nodeId: String,
    val nodeName: String,
    val lineCode: String,
    val lineName: String,
    val regionCode: String,
    val regionName: String,
    val tier: String,
    val status: String,
    val healthStatus: String = "UNKNOWN",
    val host: String,
    val port: Int,
    val isAllowed: Boolean,
    val isSelected: Boolean = false,
    val remark: String? = null,
)

data class RegionSelectionUiState(
    val title: String = "选择地区",
    val subtitle: String = "REGION SELECTION",
    val badge: String = "P1 · LIVE",
    val summary: String = "加载真实地区列表中…",
    val primaryActionLabel: String = "返回首页继续连接",
    val secondaryActionLabel: String? = null,
    val heroAccent: String = "region_selection",
    val screenState: P1ScreenState = P1ScreenState(isLoading = true),
    val searchQuery: String = "",
    val regions: List<RegionOptionUi> = emptyList(),
    val selectedRegionCode: String? = null,
    val selectedLineCode: String? = null,
    val selectedNodeId: String? = null,
    val selectionApplied: Boolean = false,
    val note: String = "",
)

sealed interface RegionSelectionEvent {
    data object Refresh : RegionSelectionEvent
    data object PrimaryActionClicked : RegionSelectionEvent
    data object SecondaryActionClicked : RegionSelectionEvent
    data object SelectionNavigated : RegionSelectionEvent
    data class NodeSelected(
        val lineCode: String,
        val nodeId: String,
    ) : RegionSelectionEvent
    data class FieldChanged(
        val key: String,
        val value: String,
    ) : RegionSelectionEvent
}

val regionSelectionNavigation: RouteDefinition = CryptoVpnRouteSpec.regionSelection

fun regionSelectionPreviewState(): RegionSelectionUiState = RegionSelectionUiState(
    summary = "真实地区列表已载入 2 个可用区。",
    screenState = P1ScreenState(),
    regions = listOf(
        RegionOptionUi(
            nodeId = "node-jp-01",
            nodeName = "Tokyo 01",
            lineCode = "JP_BASIC",
            lineName = "日本基础",
            regionCode = "JP",
            regionName = "日本",
            tier = "STANDARD",
            status = "ONLINE",
            healthStatus = "HEALTHY",
            host = "jp.example.com",
            port = 443,
            isAllowed = true,
            remark = "Tokyo",
        ),
        RegionOptionUi(
            nodeId = "node-us-01",
            nodeName = "Los Angeles 01",
            lineCode = "US_PREMIUM",
            lineName = "美国高级",
            regionCode = "US",
            regionName = "美国",
            tier = "ADVANCED",
            status = "ONLINE",
            healthStatus = "DEGRADED",
            host = "us.example.com",
            port = 443,
            isAllowed = false,
            remark = "Advanced tier required",
        ),
    ),
    selectedRegionCode = "JP",
    selectedLineCode = "JP_BASIC",
    selectedNodeId = "node-jp-01",
    note = "Preview only.",
)
