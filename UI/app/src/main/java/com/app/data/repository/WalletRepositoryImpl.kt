package com.app.data.repository

import com.app.common.model.ChainUiModel
import com.app.data.local.dao.AssetDao
import com.app.data.local.dao.TransactionDao
import com.app.data.local.entity.toEntity
import com.app.data.local.entity.toModel
import com.app.data.model.Asset
import com.app.data.model.TokenPricePoint
import com.app.data.model.Transaction
import com.app.data.model.TransactionDirection
import com.app.data.model.TransactionStatus
import com.app.data.model.UserProfile
import com.app.data.model.WalletSetupOption
import com.app.data.remote.mock.MockWalletDataSource
import com.app.data.local.prefs.WalletPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WalletRepositoryImpl(
    private val assetDao: AssetDao,
    private val transactionDao: TransactionDao,
    private val remote: MockWalletDataSource,
    private val preferences: WalletPreferences,
) : WalletRepository {
    private val profileState = MutableStateFlow(remote.profile())
    override val profile: StateFlow<UserProfile> = profileState.asStateFlow()

    private val assetState = MutableStateFlow(remote.assets())
    override val assets: StateFlow<List<Asset>> = assetState.asStateFlow()

    private val transactionState = MutableStateFlow(remote.transactions())
    override val transactions: StateFlow<List<Transaction>> = transactionState.asStateFlow()

    private val securityFlags = mutableMapOf(
        "bio" to true,
        "lock" to true,
        "phrase" to false,
        "cloud" to false,
    )

    private val chainFlags = remote.chainItems().associate { it.id to it.enabled }.toMutableMap()

    init {
        assetDao.replaceAll(assetState.value.map { it.toEntity() })
        transactionDao.replaceAll(transactionState.value.map { it.toEntity() })
    }

    override suspend fun getAssets(): List<Asset> = assets.value
    override suspend fun getAsset(symbol: String): Asset? = assetState.value.firstOrNull { it.symbol.equals(symbol, true) }
    override suspend fun getTransactions(symbol: String?): List<Transaction> = if (symbol == null) transactionState.value else transactionState.value.filter { it.symbol.equals(symbol, true) }
    override suspend fun getReceiveAddress(symbol: String): String = getAsset(symbol)?.address ?: "0xwallet-$symbol-demo"
    override suspend fun getSetupOptions(): List<WalletSetupOption> = remote.walletSetupOptions()
    override suspend fun getMnemonicWords(): List<String> = remote.mnemonicWords()
    override suspend fun getChains(): List<ChainUiModel> = remote.chainItems().map { it.copy(enabled = chainFlags[it.id] ?: it.enabled) }
    override suspend fun getPriceSeries(symbol: String): List<TokenPricePoint> = remote.priceSeries(symbol)

    override suspend fun createWallet(): Boolean {
        preferences.hasWallet = true
        return true
    }

    override suspend fun importWallet(mnemonic: String): Boolean {
        preferences.hasWallet = mnemonic.trim().split(' ').size >= 12
        return preferences.hasWallet
    }

    override suspend fun addCustomToken(symbol: String, name: String, chainId: String): Boolean {
        if (assetState.value.any { it.symbol.equals(symbol, true) && it.chainId == chainId }) return false
        val updated = assetState.value + Asset("custom-$symbol", chainId, symbol.uppercase(), name, 0.0, 1.0, 0.0, "0xcustom-$symbol")
        assetState.value = updated
        assetDao.replaceAll(updated.map { it.toEntity() })
        return true
    }

    override suspend fun sendToken(symbol: String, address: String, amount: Double): String {
        val txId = "send-${System.currentTimeMillis()}"
        val tx = Transaction(txId, symbol.uppercase(), symbol.uppercase(), amount, amount, TransactionDirection.Send, TransactionStatus.Pending, System.currentTimeMillis(), address, "0x$txId")
        transactionState.value = listOf(tx) + transactionState.value
        transactionDao.prepend(tx.toEntity())
        return txId
    }

    override suspend fun swapToken(fromSymbol: String, toSymbol: String, amount: Double): String {
        val txId = "swap-${System.currentTimeMillis()}"
        val tx = Transaction(txId, "$fromSymbol/$toSymbol", "$fromSymbol->$toSymbol", amount, amount, TransactionDirection.Swap, TransactionStatus.Pending, System.currentTimeMillis(), "Swap Router", "0x$txId")
        transactionState.value = listOf(tx) + transactionState.value
        transactionDao.prepend(tx.toEntity())
        return txId
    }

    override suspend fun bridgeToken(symbol: String, targetChain: String, amount: Double): String {
        val txId = "bridge-${System.currentTimeMillis()}"
        val tx = Transaction(txId, symbol.uppercase(), targetChain, amount, amount, TransactionDirection.Bridge, TransactionStatus.Pending, System.currentTimeMillis(), targetChain, "0x$txId")
        transactionState.value = listOf(tx) + transactionState.value
        transactionDao.prepend(tx.toEntity())
        return txId
    }

    override suspend fun toggleSecurity(itemId: String): Boolean {
        val next = !(securityFlags[itemId] ?: false)
        securityFlags[itemId] = next
        return next
    }

    override suspend fun toggleChain(chainId: String): Boolean {
        val next = !(chainFlags[chainId] ?: false)
        chainFlags[chainId] = next
        return next
    }
}
