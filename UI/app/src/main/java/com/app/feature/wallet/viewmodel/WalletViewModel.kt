package com.app.feature.wallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.AppGraph
import com.app.common.model.ChainUiModel
import com.app.data.model.Asset
import com.app.data.model.Order
import com.app.data.model.OrderStatus
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

data class WalletNetworkContext(
    val chainId: String,
    val chainName: String,
    val feeAmount: Double,
    val settlementMinutes: Int,
    val memoTagLabel: String? = null,
    val memoTagHint: String? = null,
)

data class WalletSendDraft(
    val asset: Asset,
    val network: WalletNetworkContext,
    val address: String,
    val amount: Double,
    val fiatAmount: Double,
    val feeAmount: Double,
    val totalDeduction: Double,
    val remainingBalance: Double,
    val addressError: String?,
    val amountError: String?,
    val balanceError: String?,
    val canContinue: Boolean,
)

data class WalletReceiveDetails(
    val asset: Asset,
    val network: WalletNetworkContext,
    val address: String,
    val shareText: String,
    val memoGuidance: String,
    val networkGuidance: String,
)

data class WalletPaymentDraft(
    val order: Order,
    val asset: Asset,
    val network: WalletNetworkContext,
    val merchantAddress: String,
    val tokenAmount: Double,
    val feeAmount: Double,
    val totalDeduction: Double,
    val remainingBalance: Double,
    val balanceError: String?,
    val alreadySettled: Boolean,
    val canConfirm: Boolean,
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

    fun networkContext(symbol: String): WalletNetworkContext? = token(symbol)?.toNetworkContext()

    fun evaluateSend(symbol: String, address: String, amountInput: String): WalletSendDraft? {
        val asset = token(symbol) ?: return null
        val network = asset.toNetworkContext()
        val normalizedAddress = address.trim()
        val parsedAmount = amountInput.trim().toDoubleOrNull()
        val addressError = when {
            normalizedAddress.isBlank() -> "请输入收款地址"
            isValidAddress(asset.chainId, normalizedAddress) -> null
            else -> "地址格式与 ${network.chainName} 网络不匹配"
        }
        val amountError = when {
            amountInput.isBlank() -> "请输入发送数量"
            parsedAmount == null -> "请输入有效数字"
            parsedAmount <= 0.0 -> "发送数量必须大于 0"
            else -> null
        }
        val amount = parsedAmount?.coerceAtLeast(0.0) ?: 0.0
        val feeAmount = network.feeAmount
        val totalDeduction = amount + feeAmount
        val balanceError = when {
            amountError != null -> null
            totalDeduction > asset.balance -> "余额不足，当前可用 ${formatWalletAmount(asset.balance, asset.symbol)}"
            else -> null
        }
        return WalletSendDraft(
            asset = asset,
            network = network,
            address = normalizedAddress,
            amount = amount,
            fiatAmount = amount * asset.priceUsd,
            feeAmount = feeAmount,
            totalDeduction = totalDeduction,
            remainingBalance = (asset.balance - totalDeduction).coerceAtLeast(0.0),
            addressError = addressError,
            amountError = amountError,
            balanceError = balanceError,
            canContinue = addressError == null && amountError == null && balanceError == null,
        )
    }

    fun receiveDetails(symbol: String, address: String): WalletReceiveDetails? {
        val asset = token(symbol) ?: return null
        val network = asset.toNetworkContext()
        val memoGuidance = network.memoTagLabel?.let { label ->
            "当前网络通常还需要填写 $label。${network.memoTagHint.orEmpty()}"
        } ?: "当前地址通常无需 Memo/Tag，但从交易所或托管平台提币前仍需确认对方是否要求附加备注。"
        return WalletReceiveDetails(
            asset = asset,
            network = network,
            address = address,
            shareText = "请向我的 ${asset.symbol} ${network.chainName} 地址转账：$address",
            memoGuidance = memoGuidance,
            networkGuidance = "仅接收 ${network.chainName} 网络上的 ${asset.symbol}，向错误网络转入将导致资产丢失。",
        )
    }

    fun evaluatePayment(order: Order?): WalletPaymentDraft? {
        val safeOrder = order ?: return null
        val asset = token(safeOrder.paySymbol) ?: token("USDT") ?: uiState.value.assets.firstOrNull() ?: return null
        val network = asset.toNetworkContext()
        val tokenAmount = safeOrder.amountUsd / asset.priceUsd
        val totalDeduction = tokenAmount + network.feeAmount
        val alreadySettled = safeOrder.status != OrderStatus.Pending
        val balanceError = if (totalDeduction > asset.balance) {
            "余额不足，当前可用 ${formatWalletAmount(asset.balance, asset.symbol)}"
        } else {
            null
        }
        return WalletPaymentDraft(
            order = safeOrder,
            asset = asset,
            network = network,
            merchantAddress = merchantAddress(asset.chainId, safeOrder.id),
            tokenAmount = tokenAmount,
            feeAmount = network.feeAmount,
            totalDeduction = totalDeduction,
            remainingBalance = (asset.balance - totalDeduction).coerceAtLeast(0.0),
            balanceError = balanceError,
            alreadySettled = alreadySettled,
            canConfirm = !alreadySettled && balanceError == null,
        )
    }

    fun receiveAddress(symbol: String, onResult: (String) -> Unit) {
        viewModelScope.launch { onResult(repository.getReceiveAddress(symbol)) }
    }

    fun priceSeries(symbol: String, onResult: (List<TokenPricePoint>) -> Unit) {
        viewModelScope.launch { onResult(repository.getPriceSeries(symbol)) }
    }

    fun createWallet(onDone: (Boolean) -> Unit = {}) {
        viewModelScope.launch { onDone(repository.createWallet()) }
    }

    fun importWallet(mnemonic: String, onDone: (Boolean) -> Unit = {}) {
        viewModelScope.launch { onDone(repository.importWallet(mnemonic)) }
    }

    fun send(symbol: String, address: String, amount: Double, onDone: (String) -> Unit) {
        viewModelScope.launch {
            val txId = repository.sendToken(symbol, address, amount)
            internalState.update { it.copy(lastTxId = txId) }
            onDone(txId)
        }
    }

    fun payOrder(
        orderId: String,
        paySymbol: String,
        amountUsd: Double,
        merchantAddress: String,
        onDone: (txId: String?, error: String?) -> Unit = { _, _ -> },
    ) {
        viewModelScope.launch {
            runCatching { repository.payOrder(orderId, paySymbol, amountUsd, merchantAddress) }
                .onSuccess { txId ->
                    internalState.update { it.copy(lastTxId = txId) }
                    onDone(txId, null)
                }
                .onFailure { error ->
                    onDone(null, error.message ?: "支付模拟失败")
                }
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

    private fun Asset.toNetworkContext(): WalletNetworkContext {
        val chainName = uiState.value.chains.firstOrNull { it.id.equals(chainId, true) }?.name ?: chainDisplayName(chainId)
        val memo = memoTagConfig(chainId)
        return WalletNetworkContext(
            chainId = chainId,
            chainName = chainName,
            feeAmount = estimatedFee(chainId),
            settlementMinutes = settlementMinutes(chainId),
            memoTagLabel = memo?.first,
            memoTagHint = memo?.second,
        )
    }

    private fun chainDisplayName(chainId: String): String = when (chainId.lowercase()) {
        "ethereum" -> "Ethereum"
        "tron" -> "TRON"
        "solana" -> "Solana"
        "bitcoin" -> "Bitcoin"
        "base" -> "Base"
        else -> chainId.replaceFirstChar { it.uppercase() }
    }

    private fun estimatedFee(chainId: String): Double = when (chainId.lowercase()) {
        "ethereum" -> 0.0042
        "tron" -> 1.15
        "solana" -> 0.0005
        "bitcoin" -> 0.00018
        "base" -> 0.0006
        else -> 0.001
    }

    private fun settlementMinutes(chainId: String): Int = when (chainId.lowercase()) {
        "bitcoin" -> 15
        "ethereum" -> 3
        else -> 1
    }

    private fun memoTagConfig(chainId: String): Pair<String, String>? = when (chainId.lowercase()) {
        "xrp" -> "Destination Tag" to "从交易所转入时请同步填写 Tag，否则可能无法自动入账。"
        "stellar" -> "Memo" to "部分托管平台会要求 Memo，用于区分同一归集地址下的用户。"
        else -> null
    }

    private fun isValidAddress(chainId: String, address: String): Boolean = when (chainId.lowercase()) {
        "ethereum", "base" -> address.startsWith("0x") && address.length >= 12
        "tron" -> address.startsWith("T") && address.length >= 12
        "solana" -> address.length >= 12 && address.none { it.isWhitespace() }
        "bitcoin" -> (address.startsWith("bc1") || address.startsWith("1") || address.startsWith("3")) && address.length >= 12
        else -> address.length >= 8
    }

    private fun merchantAddress(chainId: String, orderId: String): String = when (chainId.lowercase()) {
        "tron" -> "TVpnVault${orderId.takeLast(5)}Mock"
        "solana" -> "VpnVault${orderId.takeLast(4)}So1Demo"
        "bitcoin" -> "bc1qvpn${orderId.takeLast(6).lowercase()}merchant"
        else -> "0xvpnmerchant${orderId.takeLast(8).lowercase()}"
    }
}

fun formatWalletAmount(value: Double, symbol: String): String {
    val decimals = when {
        value >= 1000 -> 2
        value >= 1 -> 4
        else -> 6
    }
    return "%.${decimals}f %s".format(value, symbol)
}

