package com.app.feature.vpn.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.AppGraph
import com.app.data.model.CommissionRecord
import com.app.data.model.Order
import com.app.data.model.Plan
import com.app.data.model.ReferralSummary
import com.app.data.model.Subscription
import com.app.data.repository.VpnRepository
import com.app.vpncore.model.VpnNode
import com.app.vpncore.model.VpnState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class VpnUiState(
    val vpnState: VpnState = VpnState.Disconnected,
    val nodes: List<VpnNode> = emptyList(),
    val selectedNode: VpnNode? = null,
    val orders: List<Order> = emptyList(),
    val subscription: Subscription? = null,
    val referralSummary: ReferralSummary? = null,
    val commissions: List<CommissionRecord> = emptyList(),
    val plans: List<Plan> = emptyList(),
)

class VpnViewModel(
    private val repository: VpnRepository = AppGraph.vpnRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(VpnUiState())
    val uiState: StateFlow<VpnUiState> = _uiState.asStateFlow()
    private var cachedPlans: List<Plan> = emptyList()

    init {
        viewModelScope.launch {
            cachedPlans = repository.getPlans()
            refreshSnapshot()
        }
        viewModelScope.launch {
            repository.vpnState.collect { refreshSnapshot() }
        }
        viewModelScope.launch {
            repository.nodes.collect { refreshSnapshot() }
        }
        viewModelScope.launch {
            repository.selectedNode.collect { refreshSnapshot() }
        }
        viewModelScope.launch {
            repository.orders.collect { refreshSnapshot() }
        }
        viewModelScope.launch {
            repository.subscription.collect { refreshSnapshot() }
        }
        viewModelScope.launch {
            repository.referralSummary.collect { refreshSnapshot() }
        }
        viewModelScope.launch {
            repository.commissions.collect { refreshSnapshot() }
        }
    }

    private fun refreshSnapshot() {
        _uiState.value = VpnUiState(
            vpnState = repository.vpnState.value,
            nodes = repository.nodes.value,
            selectedNode = repository.selectedNode.value,
            orders = repository.orders.value,
            subscription = repository.subscription.value,
            referralSummary = repository.referralSummary.value,
            commissions = repository.commissions.value,
            plans = cachedPlans,
        )
    }

    fun plan(planId: String): Plan? = uiState.value.plans.firstOrNull { it.id == planId }
    fun order(orderId: String): Order? = uiState.value.orders.firstOrNull { it.id == orderId }
    fun node(nodeId: String): VpnNode? = uiState.value.nodes.firstOrNull { it.id == nodeId }

    fun refreshSubscription() { viewModelScope.launch { repository.refreshSubscription() } }
    fun selectNode(nodeId: String) { viewModelScope.launch { repository.selectNode(nodeId) } }
    fun connectOrDisconnect() { when (uiState.value.vpnState) { is VpnState.Connected, is VpnState.Connecting -> repository.disconnect() else -> repository.connect() } }
    fun createOrder(planId: String, onDone: (Order) -> Unit) { viewModelScope.launch { onDone(repository.createOrder(planId)) } }
    fun confirmPayment(orderId: String, paySymbol: String = "USDT", onDone: (Order) -> Unit = {}) { viewModelScope.launch { onDone(repository.confirmOrderPayment(orderId, paySymbol)) } }
    fun withdraw(amount: Double, address: String, onDone: (Boolean) -> Unit = {}) { viewModelScope.launch { onDone(repository.withdrawCommission(amount, address)) } }
}
