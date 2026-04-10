package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class WalletConnectSessionRouteArgs(val sessionId: String = "session_jupiter")

data class WalletConnectSessionUiState(
        val title: String = "连接会话",
        val subtitle: String = "WALLET CONNECT SESSION",
        val badge: String = "P2 · EXTENDED",
        val summary: String = "WalletConnect 会话页展示活跃连接、权限范围与失效时间。",
        val primaryActionLabel: String = "查看签名确认",
        val secondaryActionLabel: String? = "断开后返回钱包",
        val heroAccent: String = "wallet_connect_session",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "活跃会话", value = "4"),
    FeatureMetric(label = "过期时间", value = "24h"),
    FeatureMetric(label = "权限项", value = "3"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "WalletConnect 会话页展示活跃连接、权限范围与失效时间。", trailing = "wallet_connect_session", badge = "P2 扩展页"),
    FeatureListItem(title = "导航参数", subtitle = "sessionId", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "当前页面以信息展示与确认动作为主", trailing = "0 项", badge = "Info"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "连接会话 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 WalletConnectSessionPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "连接会话 已按 P2 扩展页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
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
