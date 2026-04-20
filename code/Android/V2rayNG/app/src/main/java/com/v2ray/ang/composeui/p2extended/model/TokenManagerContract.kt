package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition

data class TokenManagerRouteArgs(
    val walletId: String = "primary_wallet",
    val chainId: String = "solana",
)

data class ManagedTokenUi(
    val tokenKey: String,
    val symbol: String,
    val name: String,
    val balanceText: String,
    val unitPriceText: String,
    val valueText: String,
    val changeText: String,
    val changePositive: Boolean,
    val statusText: String,
    val chainLabel: String,
    val iconChainId: String,
    val iconLocalPath: String? = null,
    val iconUrl: String? = null,
    val customTokenId: String? = null,
    val isCustom: Boolean = false,
)

data class TokenManagerUiState(
    val title: String = "代币管理",
    val subtitle: String = "TOKEN MANAGER",
    val summary: String = "管理当前钱包当前链下的代币显示、自定义代币与垃圾币处理。",
    val walletName: String = "",
    val chainLabel: String = "",
    val visibleTokens: List<ManagedTokenUi> = emptyList(),
    val hiddenTokens: List<ManagedTokenUi> = emptyList(),
    val spamTokens: List<ManagedTokenUi> = emptyList(),
    val note: String = "",
    val isRefreshing: Boolean = false,
    val actionMessage: String? = null,
    val errorMessage: String? = null,
)

sealed interface TokenVisibilityAction {
    data object Hide : TokenVisibilityAction
    data object Spam : TokenVisibilityAction
    data object Restore : TokenVisibilityAction
    data object DeleteCustom : TokenVisibilityAction
}

val tokenManagerNavigation: RouteDefinition = CryptoVpnRouteSpec.tokenManager

fun tokenManagerPreviewState(): TokenManagerUiState = TokenManagerUiState(
    walletName = "Main Wallet",
    chainLabel = "Solana",
    visibleTokens = listOf(
        ManagedTokenUi(
            tokenKey = "solana:native:SOL",
            symbol = "SOL",
            name = "Solana",
            balanceText = "9.80 SOL",
            unitPriceText = "$189.84",
            valueText = "$1,860.44",
            changeText = "+4.70%",
            changePositive = true,
            statusText = "真实持仓",
            chainLabel = "Solana",
            iconChainId = "solana",
        ),
        ManagedTokenUi(
            tokenKey = "epjfwdd5aufqssqe...",
            symbol = "USDC",
            name = "USD Coin",
            balanceText = "20.50 USDC",
            unitPriceText = "$1.00",
            valueText = "$20.50",
            changeText = "-0.08%",
            changePositive = false,
            statusText = "真实持仓",
            chainLabel = "Solana",
            iconChainId = "solana",
        ),
    ),
)
