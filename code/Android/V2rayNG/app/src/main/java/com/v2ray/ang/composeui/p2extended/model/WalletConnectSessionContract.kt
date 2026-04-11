package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition

data class WalletConnectSessionRouteArgs(val sessionId: String = "session_jupiter")

data class WalletConnectSessionUiState(
    val title: String = "连接会话",
    val subtitle: String = "WALLET CONNECT SESSION",
    val badge: String = "同步中",
    val summary: String = "正在读取会话对象和授权状态；若未接真实 WalletConnect，会显示阻塞说明。",
    val primaryActionLabel: String? = null,
    val secondaryActionLabel: String? = null,
    val heroAccent: String = "wallet_connect_session",
    val metrics: List<FeatureMetric> = listOf(
        FeatureMetric(label = "会话标识", value = "--"),
        FeatureMetric(label = "授权状态", value = "读取中"),
        FeatureMetric(label = "权限范围", value = "读取中"),
    ),
    val fields: List<FeatureField> = emptyList(),
    val highlights: List<FeatureListItem> = listOf(
        FeatureListItem(title = "当前状态", subtitle = "正在读取会话上下文", trailing = "", badge = "LOADING"),
    ),
    val checklist: List<FeatureBullet> = listOf(
        FeatureBullet(title = "说明", detail = "默认态不再展示演示会话数量、权限项和假按钮。"),
    ),
    val note: String = "刷新后会替换为真实状态或明确阻塞说明。",
)

sealed interface WalletConnectSessionEvent {
    data object Refresh : WalletConnectSessionEvent
    data object PrimaryActionClicked : WalletConnectSessionEvent
    data object SecondaryActionClicked : WalletConnectSessionEvent
    data class FieldChanged(
        val key: String,
        val value: String,
    ) : WalletConnectSessionEvent
}

val walletConnectSessionNavigation: RouteDefinition = CryptoVpnRouteSpec.walletConnectSession

fun walletConnectSessionPreviewState(): WalletConnectSessionUiState = WalletConnectSessionUiState()
