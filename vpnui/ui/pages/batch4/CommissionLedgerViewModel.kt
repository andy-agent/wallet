package com.cryptovpn.ui.pages.growth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

/**
 * 佣金账本ViewModel
 */
@HiltViewModel
class CommissionLedgerViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<CommissionLedgerState>(CommissionLedgerState.Loading)
    val uiState: StateFlow<CommissionLedgerState> = _uiState.asStateFlow()

    // 所有交易记录
    private var allTransactions: List<CommissionTransaction> = emptyList()

    // 当前筛选
    private var currentFilter: CommissionFilter = CommissionFilter.ALL

    init {
        loadLedgerData()
    }

    /**
     * 加载账本数据
     */
    private fun loadLedgerData() {
        viewModelScope.launch {
            _uiState.value = CommissionLedgerState.Loading
            
            try {
                // 模拟网络请求
                kotlinx.coroutines.delay(600)
                
                // 模拟获取交易记录
                allTransactions = generateMockTransactions()
                
                // 计算收益统计
                val currentMonthEarnings = calculateCurrentMonthEarnings(allTransactions)
                val totalEarnings = allTransactions
                    .filter { it.status != TransactionStatus.WITHDRAWN }
                    .fold(BigDecimal.ZERO) { acc, transaction -> acc.add(transaction.amount) }
                
                val filteredTransactions = filterTransactions(allTransactions, currentFilter)
                
                if (filteredTransactions.isEmpty()) {
                    _uiState.value = CommissionLedgerState.Empty("暂无佣金记录")
                } else {
                    _uiState.value = CommissionLedgerState.Loaded(
                        currentMonthEarnings = currentMonthEarnings,
                        totalEarnings = totalEarnings,
                        selectedFilter = currentFilter,
                        transactions = filteredTransactions
                    )
                }
            } catch (e: Exception) {
                _uiState.value = CommissionLedgerState.Error(
                    message = "加载数据失败: ${e.message}",
                    canRetry = true
                )
            }
        }
    }

    /**
     * 设置筛选条件
     */
    fun setFilter(filter: CommissionFilter) {
        currentFilter = filter
        
        val currentState = _uiState.value as? CommissionLedgerState.Loaded ?: return
        
        val filteredTransactions = filterTransactions(allTransactions, filter)
        
        _uiState.value = currentState.copy(
            selectedFilter = filter,
            transactions = filteredTransactions
        )
    }

    /**
     * 筛选交易记录
     */
    private fun filterTransactions(
        transactions: List<CommissionTransaction>,
        filter: CommissionFilter
    ): List<CommissionTransaction> {
        return when (filter) {
            CommissionFilter.ALL -> transactions
            CommissionFilter.LEVEL1 -> transactions.filter { it.level == CommissionLevel.LEVEL1 }
            CommissionFilter.LEVEL2 -> transactions.filter { it.level == CommissionLevel.LEVEL2 }
            CommissionFilter.FROZEN -> transactions.filter { it.status == TransactionStatus.FROZEN }
        }
    }

    /**
     * 计算本月收益
     */
    private fun calculateCurrentMonthEarnings(transactions: List<CommissionTransaction>): BigDecimal {
        val calendar = java.util.Calendar.getInstance()
        val currentMonth = calendar.get(java.util.Calendar.MONTH)
        val currentYear = calendar.get(java.util.Calendar.YEAR)
        
        return transactions
            .filter { transaction ->
                val transactionCal = java.util.Calendar.getInstance().apply {
                    timeInMillis = transaction.timestamp
                }
                transactionCal.get(java.util.Calendar.MONTH) == currentMonth &&
                transactionCal.get(java.util.Calendar.YEAR) == currentYear &&
                transaction.status == TransactionStatus.CONFIRMED
            }
            .fold(BigDecimal.ZERO) { acc, transaction -> acc.add(transaction.amount) }
    }

    /**
     * 刷新数据
     */
    fun refreshData() {
        loadLedgerData()
    }

    /**
     * 重试
     */
    fun retry() {
        loadLedgerData()
    }

    /**
     * 生成模拟交易数据
     */
    private fun generateMockTransactions(): List<CommissionTransaction> {
        val now = System.currentTimeMillis()
        val day = 24 * 60 * 60 * 1000L
        
        return listOf(
            CommissionTransaction(
                id = "TXN${(1000..9999).random()}",
                level = CommissionLevel.LEVEL1,
                amount = BigDecimal("10.00"),
                source = "User${(100..999).random()}***",
                status = TransactionStatus.CONFIRMED,
                timestamp = now,
                description = "充值佣金"
            ),
            CommissionTransaction(
                id = "TXN${(1000..9999).random()}",
                level = CommissionLevel.LEVEL2,
                amount = BigDecimal("5.00"),
                source = "User${(100..999).random()}***",
                status = TransactionStatus.FROZEN,
                timestamp = now - day,
                description = "充值佣金"
            ),
            CommissionTransaction(
                id = "TXN${(1000..9999).random()}",
                level = CommissionLevel.LEVEL1,
                amount = BigDecimal("20.00"),
                source = "User${(100..999).random()}***",
                status = TransactionStatus.WITHDRAWN,
                timestamp = now - 2 * day
            ),
            CommissionTransaction(
                id = "TXN${(1000..9999).random()}",
                level = CommissionLevel.LEVEL1,
                amount = BigDecimal("15.00"),
                source = "User${(100..999).random()}***",
                status = TransactionStatus.CONFIRMED,
                timestamp = now - 3 * day,
                description = "续费佣金"
            ),
            CommissionTransaction(
                id = "TXN${(1000..9999).random()}",
                level = CommissionLevel.LEVEL2,
                amount = BigDecimal("7.50"),
                source = "User${(100..999).random()}***",
                status = TransactionStatus.CONFIRMED,
                timestamp = now - 5 * day
            ),
            CommissionTransaction(
                id = "TXN${(1000..9999).random()}",
                level = CommissionLevel.LEVEL1,
                amount = BigDecimal("30.00"),
                source = "User${(100..999).random()}***",
                status = TransactionStatus.PENDING,
                timestamp = now - 6 * day,
                description = "充值佣金"
            ),
            CommissionTransaction(
                id = "TXN${(1000..9999).random()}",
                level = CommissionLevel.LEVEL2,
                amount = BigDecimal("3.00"),
                source = "User${(100..999).random()}***",
                status = TransactionStatus.FROZEN,
                timestamp = now - 7 * day
            ),
            CommissionTransaction(
                id = "TXN${(1000..9999).random()}",
                level = CommissionLevel.LEVEL1,
                amount = BigDecimal("12.00"),
                source = "User${(100..999).random()}***",
                status = TransactionStatus.CONFIRMED,
                timestamp = now - 10 * day
            )
        )
    }
}
