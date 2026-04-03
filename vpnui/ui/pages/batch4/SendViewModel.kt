package com.cryptovpn.ui.pages.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

/**
 * 发送页ViewModel
 */
@HiltViewModel
class SendViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<SendPageState>(SendPageState.Loading)
    val uiState: StateFlow<SendPageState> = _uiState.asStateFlow()

    // 当前编辑状态
    private var currentEditingState: SendPageState.Editing? = null

    // 可用资产列表
    private val availableAssets = listOf(
        AssetInfo(
            symbol = "USDT",
            name = "Tether USD",
            balance = BigDecimal("1250.50"),
            usdPrice = BigDecimal("1.00"),
            networkColor = androidx.compose.ui.graphics.Color(0xFF26A17B)
        ),
        AssetInfo(
            symbol = "TRX",
            name = "TRON",
            balance = BigDecimal("5000.00"),
            usdPrice = BigDecimal("0.12"),
            networkColor = androidx.compose.ui.graphics.Color(0xFFFF060A)
        ),
        AssetInfo(
            symbol = "SOL",
            name = "Solana",
            balance = BigDecimal("25.75"),
            usdPrice = BigDecimal("145.30"),
            networkColor = androidx.compose.ui.graphics.Color(0xFF9945FF)
        )
    )

    init {
        loadInitialData()
    }

    /**
     * 加载初始数据
     */
    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = SendPageState.Loading
            
            try {
                kotlinx.coroutines.delay(500)
                
                val defaultAsset = availableAssets.first()
                val fee = calculateFee(defaultAsset.symbol)
                
                currentEditingState = SendPageState.Editing(
                    selectedAsset = defaultAsset,
                    fee = fee,
                    availableBalance = defaultAsset.balance
                )
                
                _uiState.value = currentEditingState!!
            } catch (e: Exception) {
                _uiState.value = SendPageState.Error(
                    message = "加载数据失败: ${e.message}",
                    canRetry = true
                )
            }
        }
    }

    /**
     * 选择资产
     */
    fun selectAsset(asset: AssetInfo) {
        currentEditingState?.let { state ->
            val fee = calculateFee(asset.symbol)
            currentEditingState = state.copy(
                selectedAsset = asset,
                fee = fee,
                availableBalance = asset.balance,
                amount = "",
                usdValue = "",
                isAmountValid = false
            )
            _uiState.value = currentEditingState!!
        }
    }

    /**
     * 更新收款地址
     */
    fun updateRecipientAddress(address: String) {
        currentEditingState?.let { state ->
            val isValid = validateAddress(address, state.selectedAsset.symbol)
            currentEditingState = state.copy(
                recipientAddress = address,
                isAddressValid = isValid
            )
            _uiState.value = currentEditingState!!
            checkReadyToSign()
        }
    }

    /**
     * 更新金额
     */
    fun updateAmount(amount: String) {
        currentEditingState?.let { state ->
            // 只允许数字和小数点
            if (amount.isNotEmpty() && !amount.matches(Regex("^\\d*\\.?\\d*$"))) {
                return
            }
            
            val amountDecimal = try {
                if (amount.isEmpty()) BigDecimal.ZERO else BigDecimal(amount)
            } catch (e: Exception) {
                BigDecimal.ZERO
            }
            
            val usdValue = amountDecimal.multiply(state.selectedAsset.usdPrice)
                .setScale(2, RoundingMode.HALF_UP)
                .toString()
            
            val isValid = amountDecimal > BigDecimal.ZERO && 
                         amountDecimal <= state.availableBalance
            
            val errorMessage = when {
                amountDecimal > state.availableBalance -> "余额不足"
                amountDecimal <= BigDecimal.ZERO && amount.isNotEmpty() -> "金额必须大于0"
                else -> null
            }
            
            currentEditingState = state.copy(
                amount = amount,
                usdValue = usdValue,
                isAmountValid = isValid,
                errorMessage = errorMessage
            )
            _uiState.value = currentEditingState!!
            checkReadyToSign()
        }
    }

    /**
     * 设置最大金额
     */
    fun setMaxAmount() {
        currentEditingState?.let { state ->
            val maxAmount = state.availableBalance
            updateAmount(maxAmount.toString())
        }
    }

    /**
     * 检查是否可以签名
     */
    private fun checkReadyToSign() {
        currentEditingState?.let { state ->
            if (state.isAddressValid && state.isAmountValid) {
                val amount = BigDecimal(state.amount)
                _uiState.value = SendPageState.ReadyToSign(
                    selectedAsset = state.selectedAsset,
                    recipientAddress = state.recipientAddress,
                    amount = amount,
                    usdValue = state.usdValue,
                    fee = state.fee
                )
            }
        }
    }

    /**
     * 确认交易
     */
    fun confirmTransaction() {
        val readyState = _uiState.value as? SendPageState.ReadyToSign ?: return
        
        viewModelScope.launch {
            _uiState.value = SendPageState.Broadcasting("正在广播交易...")
            
            try {
                // 模拟广播交易
                kotlinx.coroutines.delay(2000)
                
                val txHash = "0x" + generateRandomTxHash()
                
                _uiState.value = SendPageState.Pending(
                    txHash = txHash,
                    message = "等待区块确认..."
                )
                
                // 模拟等待确认
                kotlinx.coroutines.delay(3000)
                
                _uiState.value = SendPageState.Success(
                    txHash = txHash,
                    amount = readyState.amount,
                    recipientAddress = readyState.recipientAddress
                )
            } catch (e: Exception) {
                _uiState.value = SendPageState.Error(
                    message = "交易失败: ${e.message}",
                    canRetry = true
                )
            }
        }
    }

    /**
     * 取消交易
     */
    fun cancelTransaction() {
        currentEditingState?.let {
            _uiState.value = it
        }
    }

    /**
     * 重试
     */
    fun retry() {
        when (_uiState.value) {
            is SendPageState.Error -> loadInitialData()
            else -> {}
        }
    }

    /**
     * 验证地址
     */
    private fun validateAddress(address: String, symbol: String): Boolean {
        return when (symbol) {
            "USDT", "TRX" -> address.startsWith("T") && address.length == 34
            "SOL" -> address.length in 32..44
            else -> address.length >= 20
        }
    }

    /**
     * 计算手续费
     */
    private fun calculateFee(symbol: String): FeeInfo {
        return when (symbol) {
            "USDT", "TRX" -> FeeInfo(
                amount = BigDecimal("1.5"),
                symbol = "TRX",
                usdValue = "0.18",
                estimatedTime = "3-5 分钟"
            )
            "SOL" -> FeeInfo(
                amount = BigDecimal("0.000005"),
                symbol = "SOL",
                usdValue = "0.0007",
                estimatedTime = "几秒钟"
            )
            else -> FeeInfo(
                amount = BigDecimal("0.001"),
                symbol = symbol,
                usdValue = "0.01",
                estimatedTime = "5-10 分钟"
            )
        }
    }

    /**
     * 生成随机交易哈希
     */
    private fun generateRandomTxHash(): String {
        val chars = "0123456789abcdef"
        return (1..64).map { chars.random() }.joinToString("")
    }
}
