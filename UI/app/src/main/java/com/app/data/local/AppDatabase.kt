package com.app.data.local

import com.app.data.local.dao.AssetDao
import com.app.data.local.dao.MarketDao
import com.app.data.local.dao.OrderDao
import com.app.data.local.dao.TransactionDao
import com.app.data.local.dao.VpnNodeDao
import com.app.data.local.entity.AssetEntity
import com.app.data.local.entity.MarketTickerEntity
import com.app.data.local.entity.OrderEntity
import com.app.data.local.entity.TransactionEntity
import com.app.data.local.entity.VpnNodeEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppDatabase private constructor() {
    val assetDao: AssetDao = InMemoryAssetDao()
    val marketDao: MarketDao = InMemoryMarketDao()
    val orderDao: OrderDao = InMemoryOrderDao()
    val transactionDao: TransactionDao = InMemoryTransactionDao()
    val vpnNodeDao: VpnNodeDao = InMemoryVpnNodeDao()

    companion object {
        fun create(): AppDatabase = AppDatabase()
    }
}

private class InMemoryAssetDao : AssetDao {
    private val state = MutableStateFlow<List<AssetEntity>>(emptyList())
    override val items: StateFlow<List<AssetEntity>> = state.asStateFlow()
    override fun replaceAll(items: List<AssetEntity>) { state.value = items }
    override fun getAll(): List<AssetEntity> = state.value
    override fun findBySymbol(symbol: String): AssetEntity? = state.value.firstOrNull { it.symbol.equals(symbol, true) }
}

private class InMemoryMarketDao : MarketDao {
    private val state = MutableStateFlow<List<MarketTickerEntity>>(emptyList())
    override val items: StateFlow<List<MarketTickerEntity>> = state.asStateFlow()
    override fun replaceAll(items: List<MarketTickerEntity>) { state.value = items }
    override fun getAll(): List<MarketTickerEntity> = state.value
    override fun find(symbol: String): MarketTickerEntity? = state.value.firstOrNull { it.symbol.equals(symbol, true) }
}

private class InMemoryOrderDao : OrderDao {
    private val state = MutableStateFlow<List<OrderEntity>>(emptyList())
    override val items: StateFlow<List<OrderEntity>> = state.asStateFlow()
    override fun replaceAll(items: List<OrderEntity>) { state.value = items }
    override fun upsert(item: OrderEntity) {
        state.value = state.value.filterNot { it.id == item.id } + item
    }
    override fun find(id: String): OrderEntity? = state.value.firstOrNull { it.id == id }
}

private class InMemoryTransactionDao : TransactionDao {
    private val state = MutableStateFlow<List<TransactionEntity>>(emptyList())
    override val items: StateFlow<List<TransactionEntity>> = state.asStateFlow()
    override fun replaceAll(items: List<TransactionEntity>) { state.value = items }
    override fun prepend(item: TransactionEntity) { state.value = listOf(item) + state.value }
    override fun find(id: String): TransactionEntity? = state.value.firstOrNull { it.id == id }
}

private class InMemoryVpnNodeDao : VpnNodeDao {
    private val state = MutableStateFlow<List<VpnNodeEntity>>(emptyList())
    override val items: StateFlow<List<VpnNodeEntity>> = state.asStateFlow()
    override fun replaceAll(items: List<VpnNodeEntity>) { state.value = items }
    override fun find(id: String): VpnNodeEntity? = state.value.firstOrNull { it.id == id }
}
