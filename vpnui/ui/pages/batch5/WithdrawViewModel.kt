package com.cryptovpn.ui.pages.withdraw

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 提现网络类型
 */
enum class WithdrawNetwork(
    val displayName: String,
    val description: String,
    val symbol: String,
    val color: androidx.compose.ui.graphics.Color,
    val fee: Double
) {
    SOLANA(
        displayName = "USDT on Solana",
        description = "Solana网络 - 快速、低手续费",
        symbol = "S",
        color = androidx.compose.ui.graphics.Color(0xFF9945FF),
        fee = 0.5
    ),
    TRON(
        displayName = "USDT on Tron",
        description = "Tron网络 - 稳定、广泛使用",
        symbol = "T",
        color = androidx.compose.ui.graphics.Color(0xFFFF060A),
        fee = 1.0
    ),
    BSC(
        displayName = "USDT on BSC",
        description = "币安智能链 - 兼容性强",
        symbol = "B",
        color = androidx.compose.ui.graphics.Color(0xFFF3BA2F),
        fee = 0.8
    )
}

/**
 * 提现页面状态
 */
sealed class WithdrawState {
    object Loading : WithdrawState()
    
    data class Idle(
        val balanceUsdt: Double = 1000.0,
        val balanceCny: Double = 7200.0,
        val amount: String = "",
        val address: String = "",
        val selectedNetwork: WithdrawNetwork = WithdrawNetwork.SOLANA,
        val amountError: String? = null,
        val addressError: String? = null
    ) : WithdrawState() {
        val networkFee: Double get() = selectedNetwork.fee
        val willReceive: Double get() = (amount.toDoubleOrNull() ?: 0.0) - networkFee
        val isFormValid: Boolean get() = 
            amount.isNotBlank() && 
            address.isNotBlank() && 
            amountError == null && 
            addressError == null &&
            (amount.toDoubleOrNull() ?: 0.0) > networkFee
    }
    
    data class Validating(
        val balanceUsdt: Double = 1000.0,
        val balanceCny: Double = 7200.0,
        val amount: String = "",
        val address: String = "",
        val selectedNetwork: WithdrawNetwork = WithdrawNetwork.SOLANA
    ) : WithdrawState() {
        val networkFee: Double get() = selectedNetwork.fee
        val willReceive: Double get() = (amount.toDoubleOrNull() ?: 0.0) - networkFee
    }
    
    data class Submitting(
        val balanceUsdt: Double = 1000.0,
        val balanceCny: Double = 7200.0,
        val amount: String = "",
        val address: String = "",
        val selectedNetwork: WithdrawNetwork = WithdrawNetwork.SOLANA
    ) : WithdrawState()
    
    object Submitted : WithdrawState()
    object UnderReview : WithdrawState()
    object Completed : WithdrawState()
    data class Failed(val errorMessage: String) : WithdrawState()
    data class InsufficientBalance(val required: Double, val available: Double) : WithdrawState()
    data class InvalidAddress(val address: String) : WithdrawState()
}

/**
 * 提现ViewModel
 */
@HiltViewModel
class WithdrawViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<WithdrawState>(WithdrawState.Idle())
    val state: StateFlow<WithdrawState> = _state.asStateFlow()

    private val solanaAddressPattern = Regex("^[1-9A-HJ-NP-Za-km-z]{32,44}$")

    init {
        loadBalance()
    }

    private fun loadBalance() {
        viewModelScope.launch {
            _state.value = WithdrawState.Loading
            delay(500) // 模拟网络请求
            _state.value = WithdrawState.Idle()
        }
    }

    fun onAmountChange(amount: String) {
        val currentState = _state.value as? WithdrawState.Idle ?: return
        
        val amountError = validateAmount(amount, currentState.balanceUsdt)
        
        _state.update {
            currentState.copy(
                amount = amount,
                amountError = amountError
            )
        }
    }

    fun onAddressChange(address: String) {
        val currentState = _state.value as? WithdrawState.Idle ?: return
        
        val addressError = validateAddress(address)
        
        _state.update {
            currentState.copy(
                address = address,
                addressError = addressError
            )
        }
    }

    fun onNetworkChange(network: WithdrawNetwork) {
        val currentState = _state.value as? WithdrawState.Idle ?: return
        _state.update {
            currentState.copy(selectedNetwork = network)
        }
    }

    fun onWithdrawAll() {
        val currentState = _state.value as? WithdrawState.Idle ?: return
        val maxAmount = currentState.balanceUsdt - currentState.selectedNetwork.fee
        
        if (maxAmount > 0) {
            _state.update {
                currentState.copy(
                    amount = String.format("%.6f", maxAmount).trimEnd('0').trimEnd('.'),
                    amountError = null
                )
            }
        }
    }

    fun onPasteAddress() {
        // 实际应用中从剪贴板获取地址
        // 这里模拟粘贴一个地址
        val currentState = _state.value as? WithdrawState.Idle ?: return
        val pastedAddress = "7xKXtg2CW87d97TXJSDpbD5jBkheTqA83TZRuJosgAsU"
        
        _state.update {
            currentState.copy(
                address = pastedAddress,
                addressError = validateAddress(pastedAddress)
            )
        }
    }

    fun onSubmit() {
        val currentState = _state.value as? WithdrawState.Idle ?: return
        
        // 最终验证
        val amountError = validateAmount(currentState.amount, currentState.balanceUsdt)
        val addressError = validateAddress(currentState.address)
        
        if (amountError != null || addressError != null) {
            _state.update {
                currentState.copy(
                    amountError = amountError,
                    addressError = addressError
                )
            }
            return
        }
        
        val amount = currentState.amount.toDoubleOrNull() ?: 0.0
        
        // 检查余额
        if (amount > currentState.balanceUsdt) {
            _state.value = WithdrawState.InsufficientBalance(
                required = amount,
                available = currentState.balanceUsdt
            )
            return
        }
        
        // 提交提现
        viewModelScope.launch {
            _state.value = WithdrawState.Submitting(
                balanceUsdt = currentState.balanceUsdt,
                balanceCny = currentState.balanceCny,
                amount = currentState.amount,
                address = currentState.address,
                selectedNetwork = currentState.selectedNetwork
            )
            
            delay(2000) // 模拟网络请求
            
            // 模拟随机结果
            val result = (0..3).random()
            _state.value = when (result) {
                0 -> WithdrawState.Submitted
                1 -> WithdrawState.UnderReview
                2 -> WithdrawState.Completed
                else -> WithdrawState.Failed("网络异常，请稍后重试")
            }
        }
    }

    private fun validateAmount(amount: String, balance: Double): String? {
        if (amount.isBlank()) {
            return null // 空值不显示错误，只是未填写
        }
        
        val amountValue = amount.toDoubleOrNull()
        
        if (amountValue == null || amountValue <= 0) {
            return "请输入有效的提现金额"
        }
        
        if (amountValue < 10) {
            return "最小提现金额为 10 USDT"
        }
        
        if (amountValue > balance) {
            return "提现金额不能超过可用余额"
        }
        
        return null
    }

    private fun validateAddress(address: String): String? {
        if (address.isBlank()) {
            return null // 空值不显示错误
        }
        
        val currentState = _state.value as? WithdrawState.Idle
            ?: _state.value as? WithdrawState.Validating
            ?: return null
        
        return when (currentState.selectedNetwork) {
            WithdrawNetwork.SOLANA -> {
                if (!solanaAddressPattern.matches(address)) {
                    "请输入有效的Solana地址"
                } else null
            }
            WithdrawNetwork.TRON -> {
                if (!address.startsWith("T") || address.length != 34) {
                    "请输入有效的Tron地址"
                } else null
            }
            WithdrawNetwork.BSC -> {
                if (!address.startsWith("0x") || address.length != 42) {
                    "请输入有效的BSC地址"
                } else null
            }
        }
    }

    fun resetState() {
        _state.value = WithdrawState.Idle()
    }
}
