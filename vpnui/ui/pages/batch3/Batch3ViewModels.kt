package com.cryptovpn.ui.pages.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ==================== Wallet Payment Confirm ViewModel ====================

data class WalletPaymentConfirmUiState(
    val tokenSymbol: String = "USDT",
    val tokenName: String = "Tether USD",
    val paymentAmount: String = "29.99",
    val networkFee: String = "0.50",
    val totalAmount: String = "30.49",
    val recipientAddress: String = "0x742d35Cc6634C0532925a3b844Bc9e7595f0bEb",
    val networkName: String = "TRC20",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isConfirmed: Boolean = false
)

@HiltViewModel
class WalletPaymentConfirmViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(WalletPaymentConfirmUiState())
    val uiState: StateFlow<WalletPaymentConfirmUiState> = _uiState.asStateFlow()

    fun updatePaymentDetails(
        amount: String,
        fee: String,
        total: String,
        address: String
    ) {
        _uiState.value = _uiState.value.copy(
            paymentAmount = amount,
            networkFee = fee,
            totalAmount = total,
            recipientAddress = address
        )
    }

    fun confirmPayment() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // Simulate payment processing
            kotlinx.coroutines.delay(2000)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isConfirmed = true
            )
        }
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

// ==================== Order Result ViewModel ====================

enum class OrderResultStatus {
    COMPLETED, FAILED, EXPIRED, REVIEW_PENDING
}

data class OrderResultUiState(
    val status: OrderResultStatus = OrderResultStatus.COMPLETED,
    val orderId: String = "",
    val planName: String = "",
    val amount: String = "",
    val currency: String = "USDT",
    val transactionHash: String? = null,
    val message: String? = null
)

@HiltViewModel
class OrderResultViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(OrderResultUiState())
    val uiState: StateFlow<OrderResultUiState> = _uiState.asStateFlow()

    fun setOrderResult(
        status: OrderResultStatus,
        orderId: String,
        planName: String,
        amount: String,
        txHash: String? = null
    ) {
        _uiState.value = OrderResultUiState(
            status = status,
            orderId = orderId,
            planName = planName,
            amount = amount,
            transactionHash = txHash,
            message = when (status) {
                OrderResultStatus.COMPLETED -> "支付成功"
                OrderResultStatus.FAILED -> "支付失败，请重试"
                OrderResultStatus.EXPIRED -> "订单已过期"
                OrderResultStatus.REVIEW_PENDING -> "正在审核中"
            }
        )
    }

    fun retryPayment() {
        // Navigate to payment retry
    }
}

// ==================== Order List ViewModel ====================

enum class OrderStatus {
    PENDING, PAID, COMPLETED, FAILED, REFUNDED, CANCELLED
}

data class OrderItemModel(
    val orderId: String,
    val planName: String,
    val status: OrderStatus,
    val amount: String,
    val currency: String,
    val createdAt: Long,
    val duration: String
)

data class OrderListUiState(
    val orders: List<OrderItemModel> = emptyList(),
    val isLoading: Boolean = false,
    val hasMore: Boolean = false,
    val selectedFilter: OrderStatus? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class OrderListViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(OrderListUiState())
    val uiState: StateFlow<OrderListUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    private fun loadOrders() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // Simulate API call
            kotlinx.coroutines.delay(500)
            val sampleOrders = listOf(
                OrderItemModel(
                    orderId = "ORD-2024-001234",
                    planName = "年度高级套餐",
                    status = OrderStatus.COMPLETED,
                    amount = "99.99",
                    currency = "USDT",
                    createdAt = System.currentTimeMillis(),
                    duration = "365天"
                ),
                OrderItemModel(
                    orderId = "ORD-2024-001233",
                    planName = "月度基础套餐",
                    status = OrderStatus.PENDING,
                    amount = "9.99",
                    currency = "USDT",
                    createdAt = System.currentTimeMillis() - 86400000,
                    duration = "30天"
                ),
                OrderItemModel(
                    orderId = "ORD-2024-001232",
                    planName = "季度标准套餐",
                    status = OrderStatus.FAILED,
                    amount = "29.99",
                    currency = "USDT",
                    createdAt = System.currentTimeMillis() - 172800000,
                    duration = "90天"
                )
            )
            _uiState.value = _uiState.value.copy(
                orders = sampleOrders,
                isLoading = false
            )
        }
    }

    fun onFilterSelected(status: OrderStatus?) {
        _uiState.value = _uiState.value.copy(selectedFilter = status)
        // Apply filter
    }

    fun loadMore() {
        if (_uiState.value.hasMore && !_uiState.value.isLoading) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true)
                // Load more orders
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun refresh() {
        loadOrders()
    }
}

// ==================== Order Detail ViewModel ====================

data class TimelineEventModel(
    val status: String,
    val description: String,
    val timestamp: Long,
    val isCompleted: Boolean,
    val isCurrent: Boolean = false
)

data class OrderDetailUiState(
    val orderId: String = "",
    val planName: String = "",
    val planDescription: String = "",
    val status: OrderStatus = OrderStatus.PENDING,
    val amount: String = "",
    val currency: String = "USDT",
    val originalPrice: String = "",
    val discount: String = "",
    val createdAt: Long = 0,
    val paidAt: Long? = null,
    val completedAt: Long? = null,
    val transactionHash: String? = null,
    val paymentMethod: String = "",
    val duration: String = "",
    val expiryDate: String = "",
    val timeline: List<TimelineEventModel> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class OrderDetailViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(OrderDetailUiState())
    val uiState: StateFlow<OrderDetailUiState> = _uiState.asStateFlow()

    fun loadOrderDetail(orderId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // Simulate API call
            kotlinx.coroutines.delay(500)
            val now = System.currentTimeMillis()
            _uiState.value = OrderDetailUiState(
                orderId = orderId,
                planName = "年度高级套餐",
                planDescription = "全球服务器访问 + 无限制流量",
                status = OrderStatus.COMPLETED,
                amount = "99.99",
                currency = "USDT",
                originalPrice = "129.99",
                discount = "30.00",
                createdAt = now - 86400000,
                paidAt = now - 86000000,
                completedAt = now - 85000000,
                transactionHash = "0x742d35Cc6634C0532925a3b844Bc9e7595f0bEb",
                paymentMethod = "USDT-TRC20",
                duration = "365天",
                expiryDate = "2025-01-15",
                timeline = listOf(
                    TimelineEventModel("订单创建", "订单已创建", now - 86400000, true),
                    TimelineEventModel("支付成功", "区块链交易已确认", now - 86000000, true),
                    TimelineEventModel("订单完成", "VPN服务已激活", now - 85000000, true, true)
                ),
                isLoading = false
            )
        }
    }

    fun copyTransactionHash() {
        _uiState.value.transactionHash?.let {
            // Copy to clipboard
        }
    }
}

// ==================== Wallet Onboarding ViewModel ====================

enum class WalletOnboardingStatus {
    EMPTY, CREATING, IMPORTING, READY, ERROR
}

data class WalletFeatureModel(
    val iconRes: String,
    val title: String,
    val description: String
)

data class WalletOnboardingUiState(
    val status: WalletOnboardingStatus = WalletOnboardingStatus.EMPTY,
    val errorMessage: String? = null,
    val mnemonic: List<String>? = null
)

@HiltViewModel
class WalletOnboardingViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(WalletOnboardingUiState())
    val uiState: StateFlow<WalletOnboardingUiState> = _uiState.asStateFlow()

    fun createWallet() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(status = WalletOnboardingStatus.CREATING)
            // Simulate wallet creation
            kotlinx.coroutines.delay(2000)
            _uiState.value = _uiState.value.copy(
                status = WalletOnboardingStatus.READY,
                mnemonic = generateMnemonic()
            )
        }
    }

    fun importWallet(mnemonic: List<String>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(status = WalletOnboardingStatus.IMPORTING)
            // Validate and import
            kotlinx.coroutines.delay(1500)
            _uiState.value = _uiState.value.copy(status = WalletOnboardingStatus.READY)
        }
    }

    private fun generateMnemonic(): List<String> {
        return listOf(
            "abandon", "ability", "able", "about", "above", "absent",
            "absorb", "abstract", "absurd", "abuse", "access", "accident"
        )
    }

    fun reset() {
        _uiState.value = WalletOnboardingUiState()
    }
}

// ==================== Wallet Home ViewModel ====================

enum class BlockchainChain {
    SOLANA, TRON
}

data class AssetItemModel(
    val symbol: String,
    val name: String,
    val balance: String,
    val usdValue: String,
    val priceChange: String,
    val isPositive: Boolean
)

data class WalletHomeUiState(
    val selectedChain: BlockchainChain = BlockchainChain.SOLANA,
    val totalUsdValue: String = "0.00",
    val totalBtcValue: String = "0.0000",
    val assets: List<AssetItemModel> = emptyList(),
    val walletAddress: String = "",
    val isAddressCopied: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class WalletHomeViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(WalletHomeUiState())
    val uiState: StateFlow<WalletHomeUiState> = _uiState.asStateFlow()

    init {
        loadWalletData()
    }

    private fun loadWalletData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // Load wallet data
            _uiState.value = WalletHomeUiState(
                totalUsdValue = "1,234.56",
                totalBtcValue = "0.0284",
                walletAddress = "0x742d...5f0bEb",
                assets = listOf(
                    AssetItemModel("SOL", "Solana", "45.23", "4,523.00", "+5.2%", true),
                    AssetItemModel("USDC", "USD Coin", "500.00", "500.00", "0.0%", true),
                    AssetItemModel("USDT", "Tether", "300.00", "300.00", "0.0%", true)
                ),
                isLoading = false
            )
        }
    }

    fun switchChain(chain: BlockchainChain) {
        _uiState.value = _uiState.value.copy(selectedChain = chain)
        // Reload assets for selected chain
    }

    fun copyAddress() {
        _uiState.value = _uiState.value.copy(isAddressCopied = true)
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000)
            _uiState.value = _uiState.value.copy(isAddressCopied = false)
        }
    }
}

// ==================== Asset Detail ViewModel ====================

enum class TransactionType {
    SEND, RECEIVE, SWAP
}

enum class TransactionStatus {
    PENDING, CONFIRMED, FAILED
}

data class TransactionItemModel(
    val id: String,
    val type: TransactionType,
    val amount: String,
    val symbol: String,
    val usdValue: String,
    val address: String,
    val timestamp: Long,
    val status: TransactionStatus
)

data class AssetDetailUiState(
    val symbol: String = "",
    val name: String = "",
    val balance: String = "0",
    val usdValue: String = "0.00",
    val priceChange24h: String = "0%",
    val isPositiveChange: Boolean = true,
    val walletAddress: String = "",
    val transactions: List<TransactionItemModel> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class AssetDetailViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(AssetDetailUiState())
    val uiState: StateFlow<AssetDetailUiState> = _uiState.asStateFlow()

    fun loadAssetDetail(symbol: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // Load asset data
            val now = System.currentTimeMillis()
            _uiState.value = AssetDetailUiState(
                symbol = symbol,
                name = when (symbol) {
                    "SOL" -> "Solana"
                    "USDC" -> "USD Coin"
                    "USDT" -> "Tether"
                    else -> symbol
                },
                balance = "45.2345",
                usdValue = "4,523.45",
                priceChange24h = "+5.2%",
                isPositiveChange = true,
                walletAddress = "HN7cABqLq46Es1jh92dQQisAq662SmxELLLsHHe4YWrH",
                transactions = listOf(
                    TransactionItemModel(
                        "tx1", TransactionType.RECEIVE, "10.5", symbol, "1,050.00",
                        "HN7c...YWrH", now - 3600000, TransactionStatus.CONFIRMED
                    ),
                    TransactionItemModel(
                        "tx2", TransactionType.SEND, "5.0", symbol, "500.00",
                        "8xDi...3kLmN", now - 86400000, TransactionStatus.CONFIRMED
                    )
                ),
                isLoading = false
            )
        }
    }

    fun refreshTransactions() {
        loadAssetDetail(_uiState.value.symbol)
    }
}