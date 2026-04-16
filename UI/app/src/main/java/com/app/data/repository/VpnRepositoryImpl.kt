package com.app.data.repository

import com.app.data.local.dao.OrderDao
import com.app.data.local.dao.VpnNodeDao
import com.app.data.local.entity.toEntity
import com.app.data.local.entity.toModel
import com.app.data.model.CommissionRecord
import com.app.data.model.Order
import com.app.data.model.OrderStatus
import com.app.data.model.Plan
import com.app.data.model.ReferralSummary
import com.app.data.model.Subscription
import com.app.data.remote.dto.toModel
import com.app.data.remote.mock.MockVpnDataSource
import com.app.vpncore.manager.VpnManager
import com.app.vpncore.model.VpnNode
import com.app.vpncore.model.VpnState
import com.app.vpncore.storage.VpnStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking

class VpnRepositoryImpl(
    private val orderDao: OrderDao,
    private val vpnNodeDao: VpnNodeDao,
    private val remote: MockVpnDataSource,
    private val vpnManager: VpnManager,
    private val storage: VpnStorage,
) : VpnRepository {
    override val vpnState: StateFlow<VpnState> = vpnManager.state
    override val nodes: StateFlow<List<VpnNode>> = vpnManager.nodes
    override val selectedNode: StateFlow<VpnNode?> = vpnManager.selectedNode

    private val orderState = MutableStateFlow(runBlocking { remote.getOrders() }.map { it.toModel() })
    override val orders: StateFlow<List<Order>> = orderState.asStateFlow()

    private val subscriptionState = MutableStateFlow(runBlocking { remote.getSubscription() }.toModel())
    override val subscription: StateFlow<Subscription> = subscriptionState.asStateFlow()

    private val referralState = MutableStateFlow(runBlocking { remote.getReferral() }.toModel())
    override val referralSummary: StateFlow<ReferralSummary> = referralState.asStateFlow()

    private val commissionState = MutableStateFlow(remote.commissions())
    override val commissions: StateFlow<List<CommissionRecord>> = commissionState.asStateFlow()

    init {
        orderDao.replaceAll(orderState.value.map { it.toEntity() })
        vpnManager.refreshFromSubscription(remote.subscriptionPayload(), subscriptionState.value.url)
        vpnNodeDao.replaceAll(vpnManager.nodes.value.map { it.toEntity() })
    }

    override suspend fun getPlans(): List<Plan> = remote.getPlans().map { it.toModel() }
    override suspend fun getOrders(): List<Order> = orders.value
    override suspend fun getOrder(orderId: String): Order? = orders.value.firstOrNull { it.id == orderId }

    override suspend fun createOrder(planId: String): Order {
        val plan = getPlans().first { it.id == planId }
        val order = Order("ord-${System.currentTimeMillis()}", plan.id, plan.title, plan.priceUsd, OrderStatus.Pending, "USDT", System.currentTimeMillis())
        orderState.value = listOf(order) + orderState.value
        orderDao.upsert(order.toEntity())
        return order
    }

    override suspend fun confirmOrderPayment(orderId: String, paySymbol: String): Order {
        val current = getOrder(orderId) ?: createOrder("plan_quarter")
        val updated = current.copy(status = OrderStatus.Active, paySymbol = paySymbol, activatedAt = System.currentTimeMillis())
        orderState.value = orderState.value.map { if (it.id == updated.id) updated else it }
        orderDao.upsert(updated.toEntity())
        return updated
    }

    override suspend fun refreshSubscription(): Subscription {
        vpnManager.refreshFromSubscription(remote.subscriptionPayload(), subscriptionState.value.url)
        vpnNodeDao.replaceAll(vpnManager.nodes.value.map { it.toEntity() })
        val updated = subscriptionState.value.copy(lastUpdatedAt = System.currentTimeMillis())
        subscriptionState.value = updated
        storage.updateSubscription(updated.url, updated.lastUpdatedAt)
        return updated
    }

    override suspend fun selectNode(nodeId: String) {
        vpnManager.selectNode(nodeId)
    }

    override fun connect() = vpnManager.connect()
    override fun disconnect() = vpnManager.disconnect()
    override suspend fun withdrawCommission(amountUsd: Double, address: String): Boolean = amountUsd > 0 && address.isNotBlank()
}
