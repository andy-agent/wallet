package com.v2ray.ang.payment.data.repository

import android.content.Context
import com.v2ray.ang.payment.data.local.database.PaymentDatabase
import com.v2ray.ang.payment.data.local.entity.OrderEntity
import com.v2ray.ang.payment.data.local.entity.PaymentHistoryEntity
import com.v2ray.ang.payment.data.local.entity.UserEntity
import com.v2ray.ang.payment.data.local.entity.LocalWalletChainAccountEntity
import com.v2ray.ang.payment.data.local.entity.LocalWalletEntity
import com.v2ray.ang.payment.data.local.entity.VpnNodeCacheEntity
import com.v2ray.ang.payment.data.local.entity.VpnNodeRuntimeEntity
import com.v2ray.ang.payment.data.local.entity.WalletOverviewCacheEntity
import com.v2ray.ang.payment.data.local.entity.WalletPublicAddressCacheEntity
import com.v2ray.ang.payment.data.local.entity.WalletReceiveContextCacheEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 本地支付数据仓库
 * 封装所有Room数据库操作
 */
class LocalPaymentRepository(context: Context) {

    private val database = PaymentDatabase.getDatabase(context)
    private val userDao = database.userDao()
    private val orderDao = database.orderDao()
    private val paymentHistoryDao = database.paymentHistoryDao()
    private val localWalletDao = database.localWalletDao()
    private val localWalletChainAccountDao = database.localWalletChainAccountDao()
    private val vpnNodeCacheDao = database.vpnNodeCacheDao()
    private val vpnNodeRuntimeDao = database.vpnNodeRuntimeDao()
    private val walletPublicAddressCacheDao = database.walletPublicAddressCacheDao()
    private val walletReceiveContextCacheDao = database.walletReceiveContextCacheDao()
    private val walletOverviewCacheDao = database.walletOverviewCacheDao()

    // ==================== 用户相关操作 ====================

    suspend fun saveUser(user: UserEntity) = withContext(Dispatchers.IO) {
        val rowId = userDao.insert(user)
        if (rowId == -1L) {
            userDao.update(user)
        }
    }

    suspend fun updateUser(user: UserEntity) = withContext(Dispatchers.IO) {
        userDao.update(user)
    }

    suspend fun getUserByUsername(username: String): UserEntity? = withContext(Dispatchers.IO) {
        userDao.getByUsername(username)
    }

    suspend fun getUserById(userId: String): UserEntity? = withContext(Dispatchers.IO) {
        userDao.getByUserId(userId)
    }

    suspend fun getCurrentUser(): UserEntity? = withContext(Dispatchers.IO) {
        userDao.getCurrentUser()
    }

    suspend fun deleteUser(user: UserEntity) = withContext(Dispatchers.IO) {
        userDao.delete(user)
    }

    suspend fun clearAllUsers() = withContext(Dispatchers.IO) {
        userDao.deleteAll()
    }

    // ==================== 订单相关操作 ====================

    suspend fun saveOrder(order: OrderEntity) = withContext(Dispatchers.IO) {
        orderDao.insert(order)
    }

    suspend fun updateOrder(order: OrderEntity) = withContext(Dispatchers.IO) {
        orderDao.update(order)
    }

    suspend fun getOrderByOrderNo(orderNo: String): OrderEntity? = withContext(Dispatchers.IO) {
        orderDao.getByOrderNo(orderNo)
    }

    suspend fun getOrdersByUserId(userId: String): List<OrderEntity> = withContext(Dispatchers.IO) {
        orderDao.getAllByUserId(userId)
    }

    suspend fun getActiveOrders(userId: String): List<OrderEntity> = withContext(Dispatchers.IO) {
        orderDao.getActiveOrders(userId)
    }

    suspend fun getExpiringOrders(userId: String, threshold: Long): List<OrderEntity> = withContext(Dispatchers.IO) {
        orderDao.getExpiringOrders(userId, threshold = threshold)
    }

    suspend fun deleteOrderByOrderNo(orderNo: String) = withContext(Dispatchers.IO) {
        orderDao.deleteByOrderNo(orderNo)
    }

    suspend fun deleteOrdersByUserId(userId: String) = withContext(Dispatchers.IO) {
        orderDao.deleteByUserId(userId)
    }

    // ==================== 支付历史相关操作 ====================

    suspend fun savePaymentHistory(paymentHistory: PaymentHistoryEntity) = withContext(Dispatchers.IO) {
        paymentHistoryDao.insert(paymentHistory)
    }

    suspend fun getPaymentHistoryByOrderNo(orderNo: String): List<PaymentHistoryEntity> = withContext(Dispatchers.IO) {
        paymentHistoryDao.getByOrderNo(orderNo)
    }

    suspend fun getAllPaymentHistory(): List<PaymentHistoryEntity> = withContext(Dispatchers.IO) {
        paymentHistoryDao.getAll()
    }

    suspend fun getPaymentHistoryByUserId(userId: String): List<PaymentHistoryEntity> = withContext(Dispatchers.IO) {
        paymentHistoryDao.getAllByUserId(userId)
    }

    suspend fun deletePaymentHistoryByOrderNo(orderNo: String) = withContext(Dispatchers.IO) {
        paymentHistoryDao.deleteByOrderNo(orderNo)
    }

    suspend fun clearAllPaymentHistory() = withContext(Dispatchers.IO) {
        paymentHistoryDao.deleteAll()
    }

    // ==================== 数据同步相关 ====================

    /**
     * 清除所有本地数据（退出登录时调用）
     */
    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        userDao.deleteAll()
        paymentHistoryDao.deleteAll()
        localWalletDao.deleteAll()
        localWalletChainAccountDao.deleteAll()
        vpnNodeCacheDao.deleteAll()
        vpnNodeRuntimeDao.deleteAll()
        walletPublicAddressCacheDao.deleteAll()
        walletReceiveContextCacheDao.deleteAll()
        walletOverviewCacheDao.deleteAll()
        // 订单可以选择保留或删除，这里选择保留以便用户重新登录后仍能看到历史
    }

    /**
     * 同步订单列表到本地
     */
    suspend fun syncOrders(userId: String, orders: List<OrderEntity>) = withContext(Dispatchers.IO) {
        orders.forEach { order ->
            orderDao.insert(order)
        }
    }

    /**
     * 同步支付历史到本地
     */
    suspend fun syncPaymentHistory(paymentHistories: List<PaymentHistoryEntity>) = withContext(Dispatchers.IO) {
        paymentHistories.forEach { history ->
            paymentHistoryDao.insert(history)
        }
    }

    suspend fun getLocalWallets(userId: String): List<LocalWalletEntity> = withContext(Dispatchers.IO) {
        localWalletDao.getByUserId(userId)
    }

    suspend fun getLocalWallet(walletId: String): LocalWalletEntity? = withContext(Dispatchers.IO) {
        localWalletDao.getByWalletId(walletId)
    }

    suspend fun syncLocalWallets(
        userId: String,
        wallets: List<LocalWalletEntity>,
        chainAccounts: List<LocalWalletChainAccountEntity>,
    ) = withContext(Dispatchers.IO) {
        localWalletDao.deleteByUserId(userId)
        localWalletChainAccountDao.deleteByUserId(userId)
        if (wallets.isNotEmpty()) {
            localWalletDao.insertAll(wallets)
        }
        if (chainAccounts.isNotEmpty()) {
            localWalletChainAccountDao.insertAll(chainAccounts)
        }
    }

    suspend fun clearWalletDomainData(userId: String) = withContext(Dispatchers.IO) {
        localWalletDao.deleteByUserId(userId)
        localWalletChainAccountDao.deleteByUserId(userId)
        walletPublicAddressCacheDao.deleteByUserId(userId)
        walletReceiveContextCacheDao.deleteByUserId(userId)
        walletOverviewCacheDao.deleteByUserId(userId)
    }

    suspend fun getLocalWalletChainAccounts(walletId: String): List<LocalWalletChainAccountEntity> = withContext(Dispatchers.IO) {
        localWalletChainAccountDao.getByWalletId(walletId)
    }

    suspend fun getVpnNodeCache(userId: String, lineCode: String? = null): List<VpnNodeCacheEntity> = withContext(Dispatchers.IO) {
        if (lineCode.isNullOrBlank()) {
            vpnNodeCacheDao.getAllByUserId(userId)
        } else {
            vpnNodeCacheDao.getAllByUserIdAndLineCode(userId, lineCode)
        }
    }

    suspend fun getVpnNodeRuntime(userId: String): List<VpnNodeRuntimeEntity> = withContext(Dispatchers.IO) {
        vpnNodeRuntimeDao.getAllByUserId(userId)
    }

    suspend fun syncVpnNodes(
        userId: String,
        nodes: List<VpnNodeCacheEntity>,
        runtimes: List<VpnNodeRuntimeEntity>,
    ) = withContext(Dispatchers.IO) {
        if (nodes.isEmpty()) {
            vpnNodeCacheDao.deleteByUserId(userId)
            vpnNodeRuntimeDao.deleteByUserId(userId)
            return@withContext
        }

        vpnNodeCacheDao.insertAll(nodes)
        vpnNodeRuntimeDao.insertAll(runtimes)
        val nodeIds = nodes.map { it.nodeId }
        vpnNodeCacheDao.deleteMissingByUserId(userId, nodeIds)
        vpnNodeRuntimeDao.deleteMissingByUserId(userId, nodeIds)
    }

    suspend fun markSelectedVpnNode(userId: String, nodeId: String) = withContext(Dispatchers.IO) {
        vpnNodeRuntimeDao.markSelected(userId, nodeId)
    }

    suspend fun getWalletOverviewCache(userId: String): WalletOverviewCacheEntity? = withContext(Dispatchers.IO) {
        walletOverviewCacheDao.getByUserId(userId)
    }

    suspend fun saveWalletOverviewCache(item: WalletOverviewCacheEntity) = withContext(Dispatchers.IO) {
        walletOverviewCacheDao.upsert(item)
    }

    suspend fun getWalletReceiveContextCache(
        userId: String,
        requestNetworkCode: String,
        requestAssetCode: String,
    ): WalletReceiveContextCacheEntity? = withContext(Dispatchers.IO) {
        walletReceiveContextCacheDao.get(userId, requestNetworkCode, requestAssetCode)
    }

    suspend fun saveWalletReceiveContextCache(item: WalletReceiveContextCacheEntity) = withContext(Dispatchers.IO) {
        walletReceiveContextCacheDao.upsert(item)
    }

    suspend fun getWalletPublicAddresses(
        userId: String,
        networkCode: String? = null,
        assetCode: String? = null,
    ): List<WalletPublicAddressCacheEntity> = withContext(Dispatchers.IO) {
        walletPublicAddressCacheDao.getByUserId(userId, networkCode, assetCode)
    }

    suspend fun syncWalletPublicAddresses(
        userId: String,
        networkCode: String,
        assetCode: String,
        items: List<WalletPublicAddressCacheEntity>,
    ) = withContext(Dispatchers.IO) {
        if (items.isEmpty()) {
            walletPublicAddressCacheDao.deleteByScope(userId, networkCode, assetCode)
            return@withContext
        }

        walletPublicAddressCacheDao.insertAll(items)
        walletPublicAddressCacheDao.deleteMissingByScope(
            userId = userId,
            networkCode = networkCode,
            assetCode = assetCode,
            addressIds = items.map { it.addressId },
        )
    }
}
