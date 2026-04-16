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
    val summary: String = "加载地区列表中…",
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
    summary = "",
    screenState = P1ScreenState(),
    regions = listOf(
        RegionOptionUi(
            nodeId = "",
            nodeName = "",
            lineCode = "",
            lineName = "",
            regionCode = "",
            regionName = "",
            tier = "STANDARD",
            status = "UNKNOWN",
            healthStatus = "UNKNOWN",
            host = "",
            port = 0,
            isAllowed = true,
            remark = "待接口返回",
        ),
        RegionOptionUi(
            nodeId = "",
            nodeName = "",
            lineCode = "",
            lineName = "",
            regionCode = "",
            regionName = "",
            tier = "BLOCKED",
            status = "BLOCKED",
            healthStatus = "UNKNOWN",
            host = "",
            port = 0,
            isAllowed = false,
            remark = "待接口返回",
        ),
    ),
    selectedRegionCode = null,
    selectedLineCode = null,
    selectedNodeId = null,
    note = "",
)
