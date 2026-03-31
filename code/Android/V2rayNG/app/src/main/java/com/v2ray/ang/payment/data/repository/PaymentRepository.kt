package com.v2ray.ang.payment.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.v2ray.ang.BuildConfig
import com.v2ray.ang.payment.PaymentConfig
import com.v2ray.ang.payment.data.api.PaymentApi
import com.v2ray.ang.payment.data.api.SubscriptionData
import com.v2ray.ang.payment.data.local.entity.OrderEntity
import com.v2ray.ang.payment.data.local.entity.PaymentHistoryEntity
import com.v2ray.ang.payment.data.local.entity.UserEntity
import com.v2ray.ang.payment.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.*


/**
 * 支付仓库类
 * 集成本地数据库缓存
 */
class PaymentRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val localRepository = LocalPaymentRepository(context)

    private val api: PaymentApi by lazy {
        // 创建信任所有证书的 SSL Socket Factory (用于自签名证书)
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
        })
        
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        
        // 创建允许所有主机名的 HostnameVerifier
        val allHostsValid = HostnameVerifier { _, _ -> true }
        
        val client = OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier(allHostsValid)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("User-Agent", "V2rayNG/${BuildConfig.VERSION_NAME} (Android)")
                    .addHeader("Accept", "application/json")
                    .addHeader("X-Client-Version", BuildConfig.VERSION_NAME)
                    .build()
                chain.proceed(request)
            }
            .build()

        Retrofit.Builder()
            .baseUrl(PaymentConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PaymentApi::class.java)
    }

    // In-memory fallback
    private var cachedDeviceId: String? = null

    companion object {
        private const val PREFS_NAME = "payment_prefs"
        private const val KEY_CURRENT_USER_ID = "current_user_id"
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    }

    // ==================== 本地数据库操作 ====================

    /**
     * 获取本地数据仓库
     */
    fun getLocalRepository(): LocalPaymentRepository = localRepository

    /**
     * 缓存当前用户ID
     */
    private fun saveCurrentUserId(userId: String) {
        prefs.edit().putString(KEY_CURRENT_USER_ID, userId).apply()
    }

    /**
     * 获取当前用户ID
     */
    fun getCurrentUserId(): String? {
        return prefs.getString(KEY_CURRENT_USER_ID, null)
    }

    /**
     * 从API UserInfo缓存用户信息
     */
    suspend fun cacheUserInfo(userInfo: com.v2ray.ang.payment.data.api.UserInfo, accessToken: String) = withContext(Dispatchers.IO) {
        val userEntity = UserEntity(
            userId = userInfo.username,
            username = userInfo.username,
            email = null,
            accessToken = accessToken,
            refreshToken = null,
            loginAt = System.currentTimeMillis()
        )
        localRepository.saveUser(userEntity)
        saveCurrentUserId(userInfo.username)
    }

    /**
     * 从订单数据缓存订单信息
     */
    suspend fun cacheOrder(order: Order, userId: String) = withContext(Dispatchers.IO) {
        val orderEntity = OrderEntity(
            orderNo = order.orderNo,
            planName = order.plan.name,
            planId = order.plan.id,
            amount = order.payment.amountCrypto,
            assetCode = order.payment.assetCode,
            status = order.status,
            createdAt = parseDate(order.createdAt) ?: System.currentTimeMillis(),
            paidAt = order.payment.confirmedAt?.let { parseDate(it) },
            fulfilledAt = order.fulfillment?.let { parseDate(order.createdAt) },
            expiredAt = order.fulfillment?.expiredAt?.let { parseDate(it) },
            subscriptionUrl = order.fulfillment?.subscriptionUrl,
            marzbanUsername = order.fulfillment?.marzbanUsername,
            userId = userId
        )
        localRepository.saveOrder(orderEntity)

        // 如果已支付，缓存支付历史
        if (order.status == PaymentConfig.OrderStatus.FULFILLED || order.payment.txHash != null) {
            val paymentHistory = PaymentHistoryEntity(
                orderNo = order.orderNo,
                amount = order.payment.amountCrypto,
                assetCode = order.payment.assetCode,
                txHash = order.payment.txHash,
                paidAt = order.payment.confirmedAt?.let { parseDate(it) } ?: System.currentTimeMillis()
            )
            localRepository.savePaymentHistory(paymentHistory)
        }
    }

    /**
     * 更新订单状态（支付成功后）
     */
    suspend fun updateOrderStatus(order: Order) = withContext(Dispatchers.IO) {
        val existingOrder = localRepository.getOrderByOrderNo(order.orderNo)
        if (existingOrder != null) {
            val updatedOrder = existingOrder.copy(
                status = order.status,
                paidAt = order.payment.confirmedAt?.let { parseDate(it) } ?: existingOrder.paidAt,
                fulfilledAt = if (order.status == PaymentConfig.OrderStatus.FULFILLED) System.currentTimeMillis() else existingOrder.fulfilledAt,
                expiredAt = order.fulfillment?.expiredAt?.let { parseDate(it) } ?: existingOrder.expiredAt,
                subscriptionUrl = order.fulfillment?.subscriptionUrl ?: existingOrder.subscriptionUrl,
                marzbanUsername = order.fulfillment?.marzbanUsername ?: existingOrder.marzbanUsername
            )
            localRepository.updateOrder(updatedOrder)

            // 缓存支付历史
            if (order.payment.txHash != null) {
                val paymentHistory = PaymentHistoryEntity(
                    orderNo = order.orderNo,
                    amount = order.payment.amountCrypto,
                    assetCode = order.payment.assetCode,
                    txHash = order.payment.txHash,
                    paidAt = order.payment.confirmedAt?.let { parseDate(it) } ?: System.currentTimeMillis()
                )
                localRepository.savePaymentHistory(paymentHistory)
            }
        }
    }

    /**
     * 获取本地缓存的订单列表
     */
    suspend fun getCachedOrders(userId: String): List<OrderEntity> = withContext(Dispatchers.IO) {
        localRepository.getOrdersByUserId(userId)
    }

    /**
     * 获取本地缓存的支付历史
     */
    suspend fun getCachedPaymentHistory(userId: String): List<PaymentHistoryEntity> = withContext(Dispatchers.IO) {
        localRepository.getPaymentHistoryByUserId(userId)
    }

    /**
     * 获取当前缓存的用户信息
     */
    suspend fun getCachedCurrentUser(): UserEntity? = withContext(Dispatchers.IO) {
        localRepository.getCurrentUser()
    }

    /**
     * 清除所有本地数据（退出登录）
     */
    suspend fun logout() = withContext(Dispatchers.IO) {
        localRepository.clearAllData()
        prefs.edit()
            .remove(KEY_CURRENT_USER_ID)
            .remove(PaymentConfig.Prefs.CLIENT_TOKEN)
            .remove(PaymentConfig.Prefs.TOKEN_EXPIRES_AT)
            .remove(PaymentConfig.Prefs.SUBSCRIPTION_URL)
            .remove(PaymentConfig.Prefs.MARZBAN_USERNAME)
            .apply()
    }

    private fun parseDate(dateString: String?): Long? {
        return try {
            dateString?.let { dateFormat.parse(it)?.time }
        } catch (e: Exception) {
            null
        }
    }

    // ==================== 原有方法 ====================

    /**
     * 获取设备唯一ID
     */
    fun getDeviceId(): String {
        // Return cached value if available
        cachedDeviceId?.let { return it }

        // Try to get from SharedPreferences
        var deviceId = prefs.getString(PaymentConfig.Prefs.DEVICE_ID, null)

        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString()
            prefs.edit().putString(PaymentConfig.Prefs.DEVICE_ID, deviceId).apply()
        }

        // Cache in memory
        cachedDeviceId = deviceId
        return deviceId
    }

    /**
     * 保存客户端Token
     */
    fun saveClientToken(token: String, expiresAt: String) {
        prefs.edit()
            .putString(PaymentConfig.Prefs.CLIENT_TOKEN, token)
            .putString(PaymentConfig.Prefs.TOKEN_EXPIRES_AT, expiresAt)
            .apply()
    }

    /**
     * 获取客户端Token
     */
    fun getClientToken(): String? {
        return prefs.getString(PaymentConfig.Prefs.CLIENT_TOKEN, null)
    }

    /**
     * 保存当前订单ID
     */
    fun saveCurrentOrderId(orderId: String) {
        prefs.edit().putString(PaymentConfig.Prefs.CURRENT_ORDER_ID, orderId).apply()
    }

    /**
     * 获取当前订单ID
     */
    fun getCurrentOrderId(): String? {
        return prefs.getString(PaymentConfig.Prefs.CURRENT_ORDER_ID, null)
    }

    /**
     * 清除当前订单
     */
    fun clearCurrentOrder() {
        prefs.edit().remove(PaymentConfig.Prefs.CURRENT_ORDER_ID).apply()
    }

    /**
     * 保存订阅信息
     */
    fun saveSubscription(url: String, username: String) {
        prefs.edit()
            .putString(PaymentConfig.Prefs.SUBSCRIPTION_URL, url)
            .putString(PaymentConfig.Prefs.MARZBAN_USERNAME, username)
            .apply()
    }

    /**
     * 获取套餐列表
     */
    suspend fun getPlans(): Result<List<Plan>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getPlans()
            if (response.isSuccessful && response.body()?.code == "SUCCESS") {
                val plans = response.body()?.data?.plans ?: emptyList()
                Result.success(plans)
            } else {
                Result.failure(Exception(response.body()?.message ?: "获取套餐失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 创建订单
     */
    suspend fun createOrder(
        planId: String,
        assetCode: String,
        purchaseType: String = PaymentConfig.PurchaseType.NEW,
        clientToken: String? = null
    ): Result<Order> = withContext(Dispatchers.IO) {
        try {
            val request = CreateOrderRequest(
                planId = planId,
                purchaseType = purchaseType,
                assetCode = assetCode,
                clientDeviceId = getDeviceId(),
                clientVersion = BuildConfig.VERSION_NAME,
                clientToken = clientToken,
                clientUserId = getCurrentUserId(),
                marzbanUsername = prefs.getString(PaymentConfig.Prefs.MARZBAN_USERNAME, null)
            )

            val response = api.createOrder(request)
            if (response.isSuccessful && response.body()?.code == "SUCCESS") {
                val order = response.body()?.data
                if (order != null) {
                    saveCurrentOrderId(order.orderId)
                    // 缓存订单到本地
                    getCurrentUserId()?.let { userId ->
                        cacheOrder(order, userId)
                    }
                    Result.success(order)
                } else {
                    Result.failure(Exception("订单数据为空"))
                }
            } else {
                Result.failure(Exception(response.body()?.message ?: "创建订单失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 查询订单状态
     */
    suspend fun getOrder(orderId: String): Result<Order> = withContext(Dispatchers.IO) {
        try {
            val response = api.getOrder(orderId)
            if (response.isSuccessful && response.body()?.code == "SUCCESS") {
                val order = response.body()?.data
                if (order != null) {
                    // 如果订单已完成，保存token
                    if (order.status == PaymentConfig.OrderStatus.FULFILLED && order.fulfillment != null) {
                        saveClientToken(
                            order.fulfillment.clientToken,
                            order.fulfillment.tokenExpiresAt
                        )
                        saveSubscription(
                            order.fulfillment.subscriptionUrl,
                            order.fulfillment.marzbanUsername
                        )
                        clearCurrentOrder()
                    }
                    // 更新本地订单状态
                    getCurrentUserId()?.let { userId ->
                        updateOrderStatus(order)
                    }
                    Result.success(order)
                } else {
                    Result.failure(Exception("订单数据为空"))
                }
            } else {
                Result.failure(Exception(response.body()?.message ?: "查询订单失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 获取订阅信息
     */
    suspend fun getSubscription(): Result<SubscriptionData> = withContext(Dispatchers.IO) {
        try {
            val token = getClientToken()
                ?: return@withContext Result.failure(Exception("未登录"))

            val response = api.getSubscription("Bearer $token")
            if (response.isSuccessful && response.body()?.code == "SUCCESS") {
                val data = response.body()?.data
                if (data != null) {
                    // 缓存用户信息
                    cacheUserInfo(data.user, token)
                    Result.success(data)
                } else {
                    Result.failure(Exception("订阅数据为空"))
                }
            } else {
                Result.failure(Exception(response.body()?.message ?: "获取订阅失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
