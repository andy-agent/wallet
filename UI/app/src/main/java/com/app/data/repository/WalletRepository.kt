package com.app.data.repository

import com.app.common.model.ChainUiModel
import com.app.data.model.Asset
import com.app.data.model.TokenPricePoint
import com.app.data.model.Transaction
import com.app.data.model.UserProfile
import com.app.data.model.WalletSetupOption
import kotlinx.coroutines.flow.StateFlow

interface WalletRepository {
    val profile: StateFlow<UserProfile>
    val assets: StateFlow<List<Asset>>
    val transactions: StateFlow<List<Transaction>>
    suspend fun getAssets(): List<Asset>
    suspend fun getAsset(symbol: String): Asset?
    suspend fun getTransactions(symbol: String? = null): List<Transaction>
    suspend fun getReceiveAddress(symbol: String): String
    suspend fun getSetupOptions(): List<WalletSetupOption>
    suspend fun getMnemonicWords(): List<String>
    suspend fun getChains(): List<ChainUiModel>
    suspend fun getPriceSeries(symbol: String): List<TokenPricePoint>
    suspend fun createWallet(): Boolean
    suspend fun importWallet(mnemonic: String): Boolean
    suspend fun addCustomToken(symbol: String, name: String, chainId: String): Boolean
    suspend fun sendToken(symbol: String, address: String, amount: Double): String
    suspend fun swapToken(fromSymbol: String, toSymbol: String, amount: Double): String
    suspend fun bridgeToken(symbol: String, targetChain: String, amount: Double): String
    suspend fun toggleSecurity(itemId: String): Boolean
    suspend fun toggleChain(chainId: String): Boolean
}
