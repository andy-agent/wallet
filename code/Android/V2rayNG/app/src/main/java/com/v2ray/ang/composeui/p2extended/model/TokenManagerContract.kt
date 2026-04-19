package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition

data class TokenManagerRouteArgs(
    val walletId: String = "primary_wallet",
    val chainId: String = "solana",
)

data class TokenManagerUiState(
    val title: String = "代币管理",
    val subtitle: String = "TOKEN MANAGER",
    val badge: String = "",
    val summary: String = "管理当前钱包当前链下的代币显示、自定义代币与垃圾币处理。",
    val primaryActionLabel: String = "添加自定义代币",
    val secondaryActionLabel: String? = "返回钱包首页",
    val heroAccent: String = "token_manager",
    val metrics: List<FeatureMetric> = emptyList(),
    val fields: List<FeatureField> = emptyList(),
    val highlights: List<FeatureListItem> = emptyList(),
    val checklist: List<FeatureBullet> = emptyList(),
    val note: String = "",
)

sealed interface TokenManagerEvent {
    data object Refresh : TokenManagerEvent
    data object PrimaryActionClicked : TokenManagerEvent
    data object SecondaryActionClicked : TokenManagerEvent
    data class FieldChanged(
        val key: String,
        val value: String,
    ) : TokenManagerEvent
}

val tokenManagerNavigation: RouteDefinition = CryptoVpnRouteSpec.tokenManager

fun tokenManagerPreviewState(): TokenManagerUiState = TokenManagerUiState(
    metrics = listOf(
        FeatureMetric("当前链", "Solana"),
        FeatureMetric("代币数量", "3"),
        FeatureMetric("可见资产", "3"),
    ),
    highlights = listOf(
        FeatureListItem("SOL", "Solana", "9.80", "REAL"),
        FeatureListItem("USDC", "USD Coin", "20.50", "REAL"),
        FeatureListItem("USDT", "Tether USD", "8.00", "REAL"),
    ),
)
