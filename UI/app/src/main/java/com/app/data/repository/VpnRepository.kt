package com.app.data.repository

import com.app.data.model.CommissionRecord
import com.app.data.model.Order
import com.app.data.model.Plan
import com.app.data.model.ReferralSummary
import com.app.data.model.Subscription
import com.app.vpncore.model.VpnNode
import com.app.vpncore.model.VpnState
import kotlinx.coroutines.flow.StateFlow

interface VpnRepository {
    val vpnState: StateFlow<VpnState>
    val nodes: StateFlow<List<VpnNode>>
    val selectedNode: StateFlow<VpnNode?>
    val orders: StateFlow<List<Order>>
    val subscription: StateFlow<Subscription>
    val referralSummary: StateFlow<ReferralSummary>
    val commissions: StateFlow<List<CommissionRecord>>

    suspend fun getPlans(): List<Plan>
    suspend fun getOrders(): List<Order>
    suspend fun getOrder(orderId: String): Order?
    suspend fun createOrder(planId: String): Order
    suspend fun confirmOrderPayment(orderId: String, paySymbol: String = "USDT"): Order
    suspend fun refreshSubscription(): Subscription
    suspend fun selectNode(nodeId: String)
    fun connect()
    fun disconnect()
    suspend fun withdrawCommission(amountUsd: Double, address: String): Boolean
}
