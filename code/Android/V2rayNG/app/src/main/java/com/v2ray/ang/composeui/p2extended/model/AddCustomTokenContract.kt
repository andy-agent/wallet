package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition

data class AddCustomTokenRouteArgs(
    val walletId: String = "primary_wallet",
    val chainId: String = "base",
)

data class AddCustomTokenCandidateUi(
    val tokenAddress: String,
    val name: String,
    val symbol: String,
    val decimals: Int,
    val iconUrl: String? = null,
)

data class AddCustomTokenUiState(
    val title: String = "添加自定义代币",
    val subtitle: String = "ADD CUSTOM TOKEN",
    val summary: String = "搜索或粘贴精确合约地址 / mint 地址，确认后加入当前钱包当前链资产列表。",
    val walletName: String = "",
    val chainLabel: String = "",
    val query: String = "",
    val tokenAddress: String = "",
    val name: String = "",
    val symbol: String = "",
    val decimals: String = "",
    val iconUrl: String? = null,
    val searchResults: List<AddCustomTokenCandidateUi> = emptyList(),
    val selectedTokenAddress: String? = null,
    val note: String = "最终保存对象必须包含精确地址。",
    val errorMessage: String? = null,
    val statusMessage: String? = null,
    val isSearching: Boolean = false,
    val isSaving: Boolean = false,
)

sealed interface AddCustomTokenEvent {
    data object Refresh : AddCustomTokenEvent
    data class QueryChanged(val value: String) : AddCustomTokenEvent
    data class AddressChanged(val value: String) : AddCustomTokenEvent
    data class NameChanged(val value: String) : AddCustomTokenEvent
    data class SymbolChanged(val value: String) : AddCustomTokenEvent
    data class DecimalsChanged(val value: String) : AddCustomTokenEvent
}

val addCustomTokenNavigation: RouteDefinition = CryptoVpnRouteSpec.addCustomToken

fun addCustomTokenPreviewState(): AddCustomTokenUiState = AddCustomTokenUiState(
    walletName = "Main Wallet",
    chainLabel = "Base",
    query = "USDC",
    tokenAddress = "0x833589fCD6EDB6E08f4c7C32D4f71b54bdA02913",
    name = "USD Coin",
    symbol = "USDC",
    decimals = "6",
    searchResults = listOf(
        AddCustomTokenCandidateUi(
            tokenAddress = "0x833589fCD6EDB6E08f4c7C32D4f71b54bdA02913",
            name = "USD Coin",
            symbol = "USDC",
            decimals = 6,
        ),
    ),
)
