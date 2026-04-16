package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class WalletConnectSessionRouteArgs(val sessionId: String = "")

data class WalletConnectSessionUiState(
        val title: String = "连接会话",
        val subtitle: String = "WALLET CONNECT SESSION",
        val badge: String = "P2 · EXTENDED",
        val summary: String = "WalletConnect 会话页仅展示真实连接状态与权限范围，不再预置固定会话。",
        val primaryActionLabel: String = "查看签名确认",
        val secondaryActionLabel: String? = "断开后返回钱包",
        val heroAccent: String = "wallet_connect_session",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "活跃会话", value = "待同步"),
    FeatureMetric(label = "过期时间", value = "待同步"),
    FeatureMetric(label = "权限项", value = "待同步"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "session_id", label = "会话 ID", value = "", supportingText = "由真实 WalletConnect 会话返回"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "已连接会话", subtitle = "真实会话名称与权限范围待同步。", trailing = "待同步", badge = "状态"),
    FeatureListItem(title = "高风险会话", subtitle = "未知域名或高权限连接需显式标记。", trailing = "待审计", badge = "风控"),
    FeatureListItem(title = "断开能力", subtitle = "批量断开未接入前保持阻塞态。", trailing = "阻塞", badge = "操作"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "会话同步", detail = "仅展示真实连接，不再预置默认 DApp。"),
    FeatureBullet(title = "权限审计", detail = "授权范围需以后端会话详情为准。"),
    FeatureBullet(title = "断开会话", detail = "未接入前不展示成功态或数量。"),
),
        val note: String = "当前不再预置 WalletConnect 固定会话；仅展示真实连接或阻塞态。",
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
