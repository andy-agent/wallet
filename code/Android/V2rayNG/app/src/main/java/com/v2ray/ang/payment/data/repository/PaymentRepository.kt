package com.v2ray.ang.payment.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.v2ray.ang.BuildConfig
import com.v2ray.ang.payment.PaymentConfig
import com.v2ray.ang.payment.data.api.CommissionLedgerPageData
import com.v2ray.ang.payment.data.api.CommissionSummaryData
import com.v2ray.ang.payment.data.api.CurrentSubscriptionData
import com.v2ray.ang.payment.data.api.MeData
import com.v2ray.ang.payment.data.api.PaymentApi
import com.v2ray.ang.payment.data.api.RegisterEmailCodeRequest
import com.v2ray.ang.payment.data.api.ReferralBindRequest
import com.v2ray.ang.payment.data.api.ReferralOverviewData
import com.v2ray.ang.payment.data.api.WithdrawalItem
import com.v2ray.ang.payment.data.api.WithdrawalPageData
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

    val api: PaymentApi by lazy {
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
        private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
            timeZone = java.util.TimeZone.getTimeZone("UTC")
        }
    }

    // ==================== 本地数据库操作 ====================

    /**
     * 获取本地数据仓库
     */
    fun getLocalRepository(): LocalPaymentRepository = localRepository

    /**
     * 缓存当前用户ID
     */
    fun saveCurrentUserId(userId: String) {
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
            fulfilledAt = order.completedAt?.let { parseDate(it) },
            expiredAt = parseDate(order.expiresAt),
            subscriptionUrl = order.subscriptionUrl,
            marzbanUsername = null,
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
                fulfilledAt = order.completedAt?.let { parseDate(it) } ?: existingOrder.fulfilledAt,
                expiredAt = parseDate(order.expiresAt) ?: existingOrder.expiredAt,
                subscriptionUrl = order.subscriptionUrl ?: existingOrder.subscriptionUrl,
                marzbanUsername = existingOrder.marzbanUsername
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
            val token = getAccessToken()
                ?: return@withContext Result.failure(Exception("未登录"))
            val response = api.getPlans("Bearer $token")
            if (response.isSuccessful && response.body()?.code == "OK") {
                val plans = response.body()?.data?.items ?: emptyList()
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
        networkCode: String = if (assetCode == PaymentConfig.AssetCode.SOL) {
            PaymentConfig.NetworkCode.SOLANA
        } else {
            PaymentConfig.NetworkCode.TRON
        },
        purchaseType: String = PaymentConfig.PurchaseType.NEW,
        clientToken: String? = null
    ): Result<Order> = withContext(Dispatchers.IO) {
        try {
            val token = getAccessToken()
                ?: return@withContext Result.failure(Exception("未登录"))
            val request = CreateOrderRequest(
                planCode = planId,
                orderType = purchaseType,
                quoteAssetCode = assetCode,
                quoteNetworkCode = networkCode
            )

            val response = api.createOrder(
                authorization = "Bearer $token",
                idempotencyKey = UUID.randomUUID().toString(),
                request = request
            )
            if (response.isSuccessful && response.body()?.code == "OK") {
                val order = response.body()?.data
                if (order != null) {
                    val paymentTarget = api.getPaymentTarget("Bearer $token", order.orderNo)
                    val finalOrder = order.copy(paymentTarget = paymentTarget.body()?.data)
                    saveCurrentOrderId(finalOrder.orderNo)
                    // 缓存订单到本地
                    getCurrentUserId()?.let { userId ->
                        cacheOrder(finalOrder, userId)
                    }
                    Result.success(finalOrder)
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
    suspend fun getOrder(orderNo: String): Result<Order> = withContext(Dispatchers.IO) {
        try {
            val token = getAccessToken()
                ?: return@withContext Result.failure(Exception("未登录"))
            val response = api.refreshOrderStatus("Bearer $token", orderNo)
            if (response.isSuccessful && response.body()?.code == "OK") {
                val order = response.body()?.data?.copy(
                    paymentTarget = api.getPaymentTarget("Bearer $token", orderNo).body()?.data
                )
                if (order != null) {
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
    suspend fun getSubscription(): Result<CurrentSubscriptionData> = withContext(Dispatchers.IO) {
        try {
            // 自动刷新 Token（如果需要）
            if (!refreshTokenIfNeeded()) {
                return@withContext Result.failure(Exception("Token 已过期，请重新登录"))
            }
            
            val token = getAccessToken()
                ?: return@withContext Result.failure(Exception("未登录"))

            val response = api.getSubscription("Bearer $token")
            if (response.isSuccessful && response.body()?.code == "OK") {
                val data = response.body()?.data
                if (data != null) {
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

    // ==================== Token 管理方法 ====================

    /**
     * 保存登录认证响应（包含 access_token 和 refresh_token）
     */
    fun saveAuthResponse(authData: AuthData) {
        prefs.edit()
            .putString(PaymentConfig.Prefs.ACCESS_TOKEN, authData.accessToken)
            .putString(PaymentConfig.Prefs.REFRESH_TOKEN, authData.refreshToken)
            .putString(PaymentConfig.Prefs.AUTH_TOKEN_EXPIRES_AT, authData.expiresAt)
            .apply()
    }

    /**
     * 获取 Access Token
     */
    fun getAccessToken(): String? = prefs.getString(PaymentConfig.Prefs.ACCESS_TOKEN, null)

    /**
     * 获取 Refresh Token
     */
    fun getRefreshToken(): String? = prefs.getString(PaymentConfig.Prefs.REFRESH_TOKEN, null)

    /**
     * 检查 Token 是否过期
     * 在 Token 过期前 5 分钟认为即将过期
     */
    fun isTokenExpired(): Boolean {
        val expiresAtStr = prefs.getString(PaymentConfig.Prefs.AUTH_TOKEN_EXPIRES_AT, null) ?: return true
        val expiresAt = parseIsoDate(expiresAtStr)
        return if (expiresAt != null) {
            System.currentTimeMillis() + PaymentConfig.TokenConfig.TOKEN_REFRESH_BUFFER_MS >= expiresAt
        } else {
            true
        }
    }

    /**
     * 检查 Token 是否有效（未过期）
     */
    fun isTokenValid(): Boolean {
        return getAccessToken() != null && !isTokenExpired()
    }

    /**
     * 自动刷新 Token（如果需要）
     * @return true 表示 Token 有效（无需刷新或刷新成功），false 表示刷新失败
     */
    suspend fun refreshTokenIfNeeded(): Boolean {
        // Token 未过期，无需刷新
        if (!isTokenExpired()) return true

        val refreshToken = getRefreshToken() ?: return false

        return try {
            val response = api.refreshToken(
                RefreshTokenRequest(
                    refreshToken = refreshToken,
                    installationId = getDeviceId()
                )
            )
            if (response.isSuccessful && response.body()?.code == "OK") {
                response.body()?.data?.let { data ->
                    // 保存新的 access_token，保留原有的 refresh_token
                    prefs.edit()
                        .putString(PaymentConfig.Prefs.ACCESS_TOKEN, data.accessToken)
                        .putString(PaymentConfig.Prefs.AUTH_TOKEN_EXPIRES_AT, data.expiresAt)
                        .apply()
                    
                    // 同时更新 Room 数据库中的 token
                    updateCachedUserToken(data.accessToken)
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 强制刷新 Token
     * @return true 表示刷新成功，false 表示刷新失败
     */
    suspend fun forceRefreshToken(): Boolean {
        val refreshToken = getRefreshToken() ?: return false

        return try {
            val response = api.refreshToken(
                RefreshTokenRequest(
                    refreshToken = refreshToken,
                    installationId = getDeviceId()
                )
            )
            if (response.isSuccessful && response.body()?.code == "OK") {
                response.body()?.data?.let { data ->
                    prefs.edit()
                        .putString(PaymentConfig.Prefs.ACCESS_TOKEN, data.accessToken)
                        .putString(PaymentConfig.Prefs.AUTH_TOKEN_EXPIRES_AT, data.expiresAt)
                        .apply()
                    
                    updateCachedUserToken(data.accessToken)
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 清除认证信息（退出登录）
     */
    suspend fun clearAuth() = withContext(Dispatchers.IO) {
        prefs.edit()
            .remove(PaymentConfig.Prefs.ACCESS_TOKEN)
            .remove(PaymentConfig.Prefs.REFRESH_TOKEN)
            .remove(PaymentConfig.Prefs.AUTH_TOKEN_EXPIRES_AT)
            .remove(KEY_CURRENT_USER_ID)
            .remove(PaymentConfig.Prefs.CLIENT_TOKEN)
            .remove(PaymentConfig.Prefs.TOKEN_EXPIRES_AT)
            .remove(PaymentConfig.Prefs.SUBSCRIPTION_URL)
            .remove(PaymentConfig.Prefs.MARZBAN_USERNAME)
            .apply()
        
        // 清除 Room 数据库中的用户数据
        localRepository.clearAllData()
    }

    /**
     * 更新缓存用户的 Token
     */
    private suspend fun updateCachedUserToken(newAccessToken: String) = withContext(Dispatchers.IO) {
        val currentUser = localRepository.getCurrentUser()
        if (currentUser != null) {
            val updatedUser = currentUser.copy(
                accessToken = newAccessToken
            )
            localRepository.saveUser(updatedUser)
        }
    }

    suspend fun requestRegisterCode(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.requestRegisterCode(RegisterEmailCodeRequest(email))
            if (response.isSuccessful && response.body()?.code == "OK") {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "发送验证码失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMe(): Result<MeData> = withContext(Dispatchers.IO) {
        try {
            if (!refreshTokenIfNeeded()) {
                return@withContext Result.failure(Exception("Token 已过期，请重新登录"))
            }
            val token = getAccessToken()
                ?: return@withContext Result.failure(Exception("未登录"))
            val response = api.getMe("Bearer $token")
            if (response.isSuccessful && response.body()?.code == "OK") {
                val data = response.body()?.data
                if (data != null) {
                    Result.success(data)
                } else {
                    Result.failure(Exception("用户数据为空"))
                }
            } else {
                Result.failure(Exception(response.body()?.message ?: "获取用户信息失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReferralOverview(): Result<ReferralOverviewData> = withContext(Dispatchers.IO) {
        try {
            if (!refreshTokenIfNeeded()) {
                return@withContext Result.failure(Exception("Token 已过期，请重新登录"))
            }
            val token = getAccessToken()
                ?: return@withContext Result.failure(Exception("未登录"))
            val response = api.getReferralOverview("Bearer $token")
            if (response.isSuccessful && response.body()?.code == "OK") {
                response.body()?.data?.let { Result.success(it) }
                    ?: Result.failure(Exception("邀请概览为空"))
            } else {
                Result.failure(Exception(response.body()?.message ?: "获取邀请概览失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun bindReferralCode(referralCode: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (!refreshTokenIfNeeded()) {
                return@withContext Result.failure(Exception("Token 已过期，请重新登录"))
            }
            val token = getAccessToken()
                ?: return@withContext Result.failure(Exception("未登录"))
            val response = api.bindReferralCode("Bearer $token", ReferralBindRequest(referralCode))
            if (response.isSuccessful && response.body()?.code == "OK") {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "绑定邀请码失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCommissionSummary(): Result<CommissionSummaryData> = withContext(Dispatchers.IO) {
        try {
            if (!refreshTokenIfNeeded()) {
                return@withContext Result.failure(Exception("Token 已过期，请重新登录"))
            }
            val token = getAccessToken()
                ?: return@withContext Result.failure(Exception("未登录"))
            val response = api.getCommissionSummary("Bearer $token")
            if (response.isSuccessful && response.body()?.code == "OK") {
                response.body()?.data?.let { Result.success(it) }
                    ?: Result.failure(Exception("佣金概览为空"))
            } else {
                Result.failure(Exception(response.body()?.message ?: "获取佣金概览失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCommissionLedger(status: String? = null): Result<CommissionLedgerPageData> = withContext(Dispatchers.IO) {
        try {
            if (!refreshTokenIfNeeded()) {
                return@withContext Result.failure(Exception("Token 已过期，请重新登录"))
            }
            val token = getAccessToken()
                ?: return@withContext Result.failure(Exception("未登录"))
            val response = api.getCommissionLedger("Bearer $token", status = status)
            if (response.isSuccessful && response.body()?.code == "OK") {
                response.body()?.data?.let { Result.success(it) }
                    ?: Result.failure(Exception("佣金账本为空"))
            } else {
                Result.failure(Exception(response.body()?.message ?: "获取佣金账本失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createWithdrawal(
        amount: String,
        payoutAddress: String
    ): Result<WithdrawalItem> = withContext(Dispatchers.IO) {
        try {
            if (!refreshTokenIfNeeded()) {
                return@withContext Result.failure(Exception("Token 已过期，请重新登录"))
            }
            val token = getAccessToken()
                ?: return@withContext Result.failure(Exception("未登录"))
            val response = api.createWithdrawal(
                authorization = "Bearer $token",
                idempotencyKey = UUID.randomUUID().toString(),
                request = com.v2ray.ang.payment.data.api.CreateWithdrawalRequest(
                    amount = amount,
                    payoutAddress = payoutAddress,
                    assetCode = "USDT",
                    networkCode = "SOLANA"
                )
            )
            if (response.isSuccessful && response.body()?.code == "OK") {
                response.body()?.data?.let { Result.success(it) }
                    ?: Result.failure(Exception("提现结果为空"))
            } else {
                Result.failure(Exception(response.body()?.message ?: "提交提现失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWithdrawals(status: String? = null): Result<WithdrawalPageData> = withContext(Dispatchers.IO) {
        try {
            if (!refreshTokenIfNeeded()) {
                return@withContext Result.failure(Exception("Token 已过期，请重新登录"))
            }
            val token = getAccessToken()
                ?: return@withContext Result.failure(Exception("未登录"))
            val response = api.getWithdrawals("Bearer $token", status = status)
            if (response.isSuccessful && response.body()?.code == "OK") {
                response.body()?.data?.let { Result.success(it) }
                    ?: Result.failure(Exception("提现列表为空"))
            } else {
                Result.failure(Exception(response.body()?.message ?: "获取提现列表失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 解析 ISO 8601 日期字符串
     */
    private fun parseIsoDate(dateString: String): Long? {
        return try {
            isoDateFormat.parse(dateString)?.time
        } catch (e: Exception) {
            null
        }
    }
}
