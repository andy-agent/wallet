package com.app.feature.wallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.AppGraph
import com.app.common.model.ChainUiModel
import com.app.data.model.Asset
import com.app.data.model.TokenPricePoint
import com.app.data.model.Transaction
import com.app.data.model.UserProfile
import com.app.data.model.WalletSetupOption
import com.app.data.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WalletUiState(
    val profile: UserProfile? = null,
    val assets: List<Asset> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val setupOptions: List<WalletSetupOption> = emptyList(),
    val mnemonicWords: List<String> = emptyList(),
    val chains: List<ChainUiModel> = emptyList(),
    val securityItems: Map<String, Boolean> = mapOf("bio" to true, "lock" to true, "phrase" to false, "cloud" to false),
    val lastTxId: String? = null,
)

class WalletViewModel(
    private val repository: WalletRepository = AppGraph.walletRepository,
) : ViewModel() {
    private val internalState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = internalState.asStateFlow()

    init {
        viewModelScope.launch {
            val setupOptions = repository.getSetupOptions()
            val mnemonic = repository.getMnemonicWords()
            val chains = repository.getChains()
            combine(repository.profile, repository.assets, repository.transactions) { profile, assets, transactions ->
                WalletUiState(
                    profile = profile,
                    assets = assets,
                    transactions = transactions,
                    setupOptions = setupOptions,
                    mnemonicWords = mnemonic,
                    chains = chains,
                    securityItems = uiState.value.securityItems,
                    lastTxId = uiState.value.lastTxId,
                )
            }.collect { internalState.value = it }
        }
    }

    fun token(symbol: String): Asset? = uiState.value.assets.firstOrNull { it.symbol.equals(symbol, true) }
    fun transactionsOf(symbol: String): List<Transaction> = uiState.value.transactions.filter { it.symbol.contains(symbol, true) }

    fun receiveAddress(symbol: String, onResult: (String) -> Unit) {
        viewModelScope.launch { onResult(repository.getReceiveAddress(symbol)) }
    }

    fun priceSeries(symbol: String, onResult: (List<TokenPricePoint>) -> Unit) {
        viewModelScope.launch { onResult(repository.getPriceSeries(symbol)) }
    }

    fun createWallet(onDone: (Boolean) -> Unit = {}) { viewModelScope.launch { onDone(repository.createWallet()) } }
    fun importWallet(mnemonic: String, onDone: (Boolean) -> Unit = {}) { viewModelScope.launch { onDone(repository.importWallet(mnemonic)) } }

    fun send(symbol: String, address: String, amount: Double, onDone: (String) -> Unit) {
        viewModelScope.launch {
            val txId = repository.sendToken(symbol, address, amount)
            internalState.update { it.copy(lastTxId = txId) }
            onDone(txId)
        }
    }

    fun swap(fromSymbol: String, toSymbol: String, amount: Double, onDone: (String) -> Unit = {}) {
        viewModelScope.launch { onDone(repository.swapToken(fromSymbol, toSymbol, amount)) }
    }

    fun bridge(symbol: String, targetChain: String, amount: Double, onDone: (String) -> Unit = {}) {
        viewModelScope.launch { onDone(repository.bridgeToken(symbol, targetChain, amount)) }
    }

    fun addCustomToken(symbol: String, name: String, chainId: String, onDone: (Boolean) -> Unit = {}) {
        viewModelScope.launch { onDone(repository.addCustomToken(symbol, name, chainId)) }
    }

    fun toggleSecurity(itemId: String) {
        viewModelScope.launch {
            val next = repository.toggleSecurity(itemId)
            internalState.update { it.copy(securityItems = it.securityItems + (itemId to next)) }
        }
    }

    fun toggleChain(chainId: String) {
        viewModelScope.launch {
            val next = repository.toggleChain(chainId)
            internalState.update {
                it.copy(chains = it.chains.map { item -> if (item.id == chainId) item.copy(enabled = next) else item })
            }
        }
    }
}
