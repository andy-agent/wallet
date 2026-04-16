package com.v2ray.ang.payment.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.v2ray.ang.BuildConfig
import com.v2ray.ang.dto.SubscriptionItem
import com.v2ray.ang.fmt.VlessFmt
import com.v2ray.ang.handler.AngConfigManager
import com.v2ray.ang.handler.MmkvManager
import com.v2ray.ang.payment.PaymentConfig
import com.v2ray.ang.payment.data.api.CommissionLedgerPageData
import com.v2ray.ang.payment.data.api.CommissionSummaryData
import com.v2ray.ang.payment.data.api.CurrentSubscriptionData
import com.v2ray.ang.payment.data.api.IssueVpnConfigRequest
import com.v2ray.ang.payment.data.api.MeData
import com.v2ray.ang.payment.data.api.PaymentApi
import com.v2ray.ang.payment.data.api.RegisterEmailCodeRequest
import com.v2ray.ang.payment.data.api.ReferralBindRequest
import com.v2ray.ang.payment.data.api.ReferralOverviewData
import com.v2ray.ang.payment.data.api.ReferralShareContextData
import com.v2ray.ang.payment.data.api.SelectVpnNodeRequest
import com.v2ray.ang.payment.data.api.VpnConfigIssueData
import com.v2ray.ang.payment.data.api.VpnNodeItem
import com.v2ray.ang.payment.data.api.VpnRegionItem
import com.v2ray.ang.payment.data.api.VpnStatusData
import com.v2ray.ang.payment.data.api.WalletChainItemData
import com.v2ray.ang.payment.data.api.WalletOverviewData
import com.v2ray.ang.payment.data.api.WalletPublicAddressData
import com.v2ray.ang.payment.data.api.WalletPublicAddressUpsertRequest
import com.v2ray.ang.payment.data.api.WalletLifecycleData
import com.v2ray.ang.payment.data.api.WalletLifecycleUpsertRequest
import com.v2ray.ang.payment.data.api.WalletBalancesData
import com.v2ray.ang.payment.data.api.WalletSecretBackupData
import com.v2ray.ang.payment.data.api.WalletSecretBackupMetadataData
import com.v2ray.ang.payment.data.api.WalletSecretBackupExportData
import com.v2ray.ang.payment.data.api.WalletSecretBackupUpsertRequest
import com.v2ray.ang.payment.data.api.WalletTransferBuildData
import com.v2ray.ang.payment.data.api.WalletTransferBuildRequest
import com.v2ray.ang.payment.data.api.WalletTransferPrecheckData
import com.v2ray.ang.payment.data.api.WalletTransferPrecheckRequest
import com.v2ray.ang.payment.data.api.WalletTransferProxyBroadcastData
import com.v2ray.ang.payment.data.api.WalletTransferProxyBroadcastRequest
import com.v2ray.ang.payment.data.api.WalletAssetItemData
import com.v2ray.ang.payment.data.api.WalletReceiveContextData
import com.v2ray.ang.payment.data.api.WithdrawalItem
import com.v2ray.ang.payment.data.api.WithdrawalPageData
import com.v2ray.ang.payment.data.local.entity.OrderEntity
import com.v2ray.ang.payment.data.local.entity.PaymentHistoryEntity
import com.v2ray.ang.payment.data.local.entity.UserEntity
import com.v2ray.ang.payment.data.local.entity.VpnNodeCacheEntity
import com.v2ray.ang.payment.data.local.entity.VpnNodeRuntimeEntity
import com.v2ray.ang.payment.data.local.entity.WalletOverviewCacheEntity
import com.v2ray.ang.payment.data.local.entity.WalletPublicAddressCacheEntity
import com.v2ray.ang.payment.data.local.entity.WalletReceiveContextCacheEntity
import com.v2ray.ang.payment.data.model.*
import com.v2ray.ang.util.Utils
import com.v2ray.ang.service.SessionKeepAliveService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.*


/**
 * 支付仓库类
 * 集成本地数据库缓存
 */
class PaymentRepository(context: Context) {

    private val appContext = context.applicationContext

    private val prefs: SharedPreferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val localRepository = LocalPaymentRepository(appContext)
    private val paymentApiGson: Gson by lazy { createPaymentApiGson() }
    private val cacheSyncScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

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
            .addConverterFactory(GsonConverterFactory.create(paymentApiGson))
            .build()
            .create(PaymentApi::class.java)
    }

    // In-memory fallback
    private var cachedDeviceId: String? = null

    companion object {
        private const val PREFS_NAME = "payment_prefs"
        private const val KEY_CURRENT_USER_ID = "current_user_id"
        private const val KEY_PENDING_REFERRAL_CODE = "pending_referral_code"
        private const val PLAN_CACHE_SYNC_THROTTLE_MS = 5 * 60_000L
        private const val WALLET_ASSET_CATALOG_CACHE_SYNC_THROTTLE_MS = 5 * 60_000L
        private const val ORDER_SYNC_THROTTLE_MS = 60_000L
        private const val VPN_NODE_SYNC_THROTTLE_MS = 60_000L
        private const val WALLET_CACHE_SYNC_THROTTLE_MS = 60_000L
        private const val ORDER_PAGE_SIZE = 100
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
            timeZone = java.util.TimeZone.getTimeZone("UTC")
        }
        private val isoDateFormatWithMs = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
            timeZone = java.util.TimeZone.getTimeZone("UTC")
        }

        internal fun createPaymentApiGson(): Gson {
            return GsonBuilder()
                .registerTypeAdapterFactory(OrderCreatedAtFallbackAdapterFactory())
                .create()
        }

        internal fun normalizeReferralCodeInternal(code: String?): String {
            return code.orEmpty().trim().uppercase(Locale.ROOT)
        }

        internal fun shouldSkipReferralBindFailure(message: String?): Boolean {
            val normalized = message.orEmpty().uppercase(Locale.ROOT)
            return when {
                normalized.isBlank() -> false
                "已绑定邀请码" in normalized -> true
                "REFERRAL_BINDING_LOCKED" in normalized || "ALREADY EXISTS" in normalized -> true
                "邀请码无效" in normalized -> true
                "REFERRAL_CODE_INVALID" in normalized || "INVALID" in normalized -> true
                "不能绑定自己的邀请码" in normalized -> true
                "REFERRAL_SELF_BIND_FORBIDDEN" in normalized || "SELF BIND" in normalized -> true
                "EMPTY" in normalized || "不能为空" in normalized -> true
                else -> false
            }
        }

        /**
         * 解析 ISO 8601 日期字符串（支持带毫秒和不带毫秒格式）
         * 用于单元测试，internal visibility
         */
        internal fun parseIsoDateInternal(dateString: String?): Long? {
            if (dateString.isNullOrBlank()) return null
            return try {
                // 先尝试带毫秒的格式 (e.g., 2026-04-04T22:12:11.788Z)
                isoDateFormatWithMs.parse(dateString)?.time
            } catch (e: Exception) {
                try {
                    // 再尝试不带毫秒的格式 (e.g., 2026-04-04T22:12:11Z)
                    isoDateFormat.parse(dateString)?.time
                } catch (e2: Exception) {
                    null
                }
            }
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

    fun savePendingReferralCode(code: String) {
        val normalizedCode = normalizeReferralCodeInternal(code)
        if (normalizedCode.isBlank()) return
        prefs.edit().putString(KEY_PENDING_REFERRAL_CODE, normalizedCode).apply()
    }

    fun getPendingReferralCode(): String? {
        return prefs.getString(KEY_PENDING_REFERRAL_CODE, null)?.trim()?.takeIf { it.isNotBlank() }
    }

    fun clearPendingReferralCode() {
        prefs.edit().remove(KEY_PENDING_REFERRAL_CODE).apply()
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
            usdAmount = order.quoteUsdAmount,
            assetCode = order.payment.assetCode,
            networkCode = order.quoteNetworkCode,
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
                usdAmount = order.quoteUsdAmount,
                networkCode = order.quoteNetworkCode,
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
    suspend fun getCachedOrders(userId: String): List<OrderEntity> {
        syncOrdersFromServer(force = false, userId = userId)
        return withContext(Dispatchers.IO) {
            localRepository.getOrdersByUserId(userId)
        }
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
        clearAuth()
    }

    private fun parseDate(dateString: String?): Long? {
        return parseIsoDateInternal(dateString) ?: try {
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

    fun getSavedSubscriptionUrl(): String? {
        return prefs.getString(PaymentConfig.Prefs.SUBSCRIPTION_URL, null)
    }

    fun getSavedMarzbanUsername(): String? {
        return prefs.getString(PaymentConfig.Prefs.MARZBAN_USERNAME, null)
    }

    fun getCachedSubscriptionPlanCode(): String? {
        return prefs.getString(PaymentConfig.Prefs.LAST_SUBSCRIPTION_PLAN_CODE, null)
    }

    fun getCachedSubscriptionStatus(): String? {
        return prefs.getString(PaymentConfig.Prefs.LAST_SUBSCRIPTION_STATUS, null)
    }

    fun getCachedSubscriptionDaysRemaining(): Int? {
        return if (prefs.contains(PaymentConfig.Prefs.LAST_SUBSCRIPTION_DAYS_REMAINING)) {
            prefs.getInt(PaymentConfig.Prefs.LAST_SUBSCRIPTION_DAYS_REMAINING, 0)
        } else {
            null
        }
    }

    fun getCachedVpnLineName(): String? {
        return prefs.getString(PaymentConfig.Prefs.LAST_VPN_LINE_NAME, null)
    }

    fun getCachedVpnNodeId(): String? {
        return prefs.getString(PaymentConfig.Prefs.LAST_VPN_NODE_ID, null)
    }

    fun getCachedVpnNodeName(): String? {
        return prefs.getString(PaymentConfig.Prefs.LAST_VPN_NODE_NAME, null)
    }

    fun getCachedVpnSessionStatus(): String? {
        return prefs.getString(PaymentConfig.Prefs.LAST_VPN_SESSION_STATUS, null)
    }

    fun importSubscriptionUrl(
        subscriptionUrl: String,
        remarks: String = "CryptoVPN Subscription",
    ): Boolean {
        if (subscriptionUrl.isBlank()) {
            return false
        }

        val existing = MmkvManager.decodeSubscriptions()
            .firstOrNull { it.subscription.url == subscriptionUrl }
        val subscriptionId = existing?.guid ?: Utils.getUuid()
        val subscriptionItem = existing?.subscription ?: SubscriptionItem()
        subscriptionItem.remarks =
            if (remarks.isBlank()) subscriptionItem.remarks.ifBlank { "CryptoVPN Subscription" } else remarks
        subscriptionItem.url = subscriptionUrl
        subscriptionItem.enabled = true
        subscriptionItem.autoUpdate = true
        subscriptionItem.lastUpdated = System.currentTimeMillis()
        MmkvManager.encodeSubscription(subscriptionId, subscriptionItem)

        val updateResult = AngConfigManager.updateConfigViaSubAll()
        val serverList = MmkvManager.decodeServerList(subscriptionId)
        if (MmkvManager.getSelectServer().isNullOrEmpty()) {
            serverList.firstOrNull()?.let { MmkvManager.setSelectServer(it) }
        }

        return updateResult.configCount > 0 || serverList.isNotEmpty()
    }

    /**
     * 获取套餐列表
     */
    suspend fun getPlans(): Result<List<Plan>> = withContext(Dispatchers.IO) {
        val cached = readCachedPlans()
        if (cached != null) {
            cacheSyncScope.launch {
                syncPlansFromServer(force = true)
            }
            cacheSyncScope.launch {
                syncWalletAssetCatalogFromServer(force = false)
            }
            return@withContext Result.success(cached)
        }
        syncPlansFromServer(force = true)
    }

    suspend fun getCachedPlans(): List<Plan>? = withContext(Dispatchers.IO) {
        readCachedPlans()
    }

    suspend fun syncPlansFromServer(force: Boolean = false): Result<List<Plan>> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val cached = readCachedPlans()
        val lastSyncAt = prefs.getLong(PaymentConfig.Prefs.PLANS_CACHE_UPDATED_AT, 0L)
        if (!force && cached != null && now - lastSyncAt < PLAN_CACHE_SYNC_THROTTLE_MS) {
            return@withContext Result.success(cached)
        }
        try {
            val token = getAccessToken()
                ?: return@withContext cached?.let { Result.success(it) }
                    ?: Result.failure(Exception("未登录"))
            val response = api.getPlans("Bearer $token")
            if (response.isSuccessful && response.body()?.code == "OK") {
                val plans = response.body()?.data?.items ?: emptyList()
                savePlansCache(plans, now)
                cacheSyncScope.launch {
                    syncWalletAssetCatalogFromServer(force = false)
                }
                Result.success(plans)
            } else {
                cached?.let { Result.success(it) }
                    ?: Result.failure(Exception(response.body()?.message ?: "获取套餐失败"))
            }
        } catch (e: Exception) {
            cached?.let { Result.success(it) } ?: Result.failure(e)
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
            val request = CreateOrderRequest(
                planCode = planId,
                orderType = purchaseType,
                quoteAssetCode = assetCode,
                quoteNetworkCode = networkCode
            )

            val (response, token) = executeAuthenticatedRequest {
                api.createOrder(
                    authorization = "Bearer $it",
                    idempotencyKey = UUID.randomUUID().toString(),
                    request = request
                )
            } ?: return@withContext Result.failure(Exception("未登录"))
            if (response.isSuccessful && response.body()?.code == "OK") {
                val order = response.body()?.data
                if (order != null) {
                    val finalOrder = order.copy(
                        paymentTarget = order.paymentTarget ?: fetchPaymentTargetSafe(token, order.orderNo),
                    )
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
                Result.failure(Exception(extractApiErrorMessage(response, "创建订单失败")))
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
            val (response, token) = executeAuthenticatedRequest {
                api.refreshOrderStatus("Bearer $it", orderNo)
            } ?: return@withContext Result.failure(Exception("未登录"))
            if (response.isSuccessful && response.body()?.code == "OK") {
                val order = response.body()?.data?.let { latest ->
                    latest.copy(
                        paymentTarget = latest.paymentTarget ?: fetchPaymentTargetSafe(token, orderNo),
                    )
                }
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
                Result.failure(Exception(extractApiErrorMessage(response, "查询订单失败")))
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
            val (response, _) = executeAuthenticatedRequest {
                api.getSubscription("Bearer $it")
            } ?: return@withContext Result.failure(Exception("未登录"))
            if (response.isSuccessful && response.body()?.code == "OK") {
                val data = response.body()?.data
                if (data != null) {
                    cacheSubscriptionMetadata(data)
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
     * 当前客户端将 access token 视为本地常驻态；只要本地还保存 token，就不主动按时间判过期。
     */
    fun isTokenExpired(): Boolean {
        return getAccessToken().isNullOrBlank()
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
        SessionKeepAliveService.stop(appContext)
        prefs.edit()
            .remove(PaymentConfig.Prefs.ACCESS_TOKEN)
            .remove(PaymentConfig.Prefs.REFRESH_TOKEN)
            .remove(PaymentConfig.Prefs.AUTH_TOKEN_EXPIRES_AT)
            .remove(KEY_CURRENT_USER_ID)
            .remove(PaymentConfig.Prefs.CLIENT_TOKEN)
            .remove(PaymentConfig.Prefs.TOKEN_EXPIRES_AT)
            .remove(PaymentConfig.Prefs.SUBSCRIPTION_URL)
            .remove(PaymentConfig.Prefs.MARZBAN_USERNAME)
            .remove(PaymentConfig.Prefs.CURRENT_ORDER_ID)
            .remove(PaymentConfig.Prefs.LAST_ORDERS_SYNC_AT)
            .remove(PaymentConfig.Prefs.LAST_VPN_NODES_SYNC_AT)
            .remove(PaymentConfig.Prefs.LAST_VPN_LINE_NAME)
            .remove(PaymentConfig.Prefs.LAST_VPN_NODE_ID)
            .remove(PaymentConfig.Prefs.LAST_VPN_NODE_NAME)
            .remove(PaymentConfig.Prefs.LAST_VPN_SESSION_STATUS)
            .remove(PaymentConfig.Prefs.LAST_SUBSCRIPTION_PLAN_CODE)
            .remove(PaymentConfig.Prefs.LAST_SUBSCRIPTION_STATUS)
            .remove(PaymentConfig.Prefs.LAST_SUBSCRIPTION_DAYS_REMAINING)
            .remove(PaymentConfig.Prefs.LAST_VPN_REGION_CODE)
            .remove(PaymentConfig.Prefs.LAST_VPN_CONFIG_EXPIRE_AT)
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

    suspend fun register(
        email: String,
        password: String,
    ): Result<AuthData> = withContext(Dispatchers.IO) {
        try {
            val response = api.register(
                UUID.randomUUID().toString(),
                RegisterRequest(
                    email = email,
                    password = password,
                    installationId = getDeviceId(),
                ),
            )

            if (response.isSuccessful && response.body()?.code == "OK") {
                response.body()?.data?.let { authData ->
                    saveAuthResponse(authData)
                    val userEntity = UserEntity(
                        userId = authData.userId,
                        username = email,
                        email = email,
                        accessToken = authData.accessToken,
                        refreshToken = authData.refreshToken,
                        loginAt = System.currentTimeMillis(),
                    )
                    localRepository.saveUser(userEntity)
                    saveCurrentUserId(authData.userId)
                    Result.success(authData)
                } ?: Result.failure(Exception("注册响应为空"))
            } else {
                Result.failure(Exception(extractApiErrorMessage(response, "注册失败")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMe(): Result<MeData> = withContext(Dispatchers.IO) {
        try {
            val (response, token) = executeAuthenticatedRequest {
                api.getMe("Bearer $it")
            } ?: return@withContext Result.failure(Exception("未登录"))
            if (response.isSuccessful && response.body()?.code == "OK") {
                val data = response.body()?.data
                if (data != null) {
                    val currentUser = UserEntity(
                        userId = data.accountId,
                        username = data.email,
                        email = data.email,
                        accessToken = token,
                        refreshToken = getRefreshToken(),
                        loginAt = System.currentTimeMillis(),
                    )
                    localRepository.saveUser(currentUser)
                    saveCurrentUserId(data.accountId)
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

    suspend fun submitClientTx(
        orderNo: String,
        txHash: String,
        networkCode: String,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (!refreshTokenIfNeeded()) {
                return@withContext Result.failure(Exception("Token 已过期，请重新登录"))
            }
            val token = getAccessToken()
                ?: return@withContext Result.failure(Exception("未登录"))
            val response = api.submitClientTx(
                authorization = "Bearer $token",
                orderNo = orderNo,
                request = com.v2ray.ang.payment.data.api.SubmitClientTxRequest(
                    txHash = txHash,
                    networkCode = networkCode,
                ),
            )
            if (response.isSuccessful && response.body()?.code == "OK") {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "提交交易哈希失败（兜底）"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun fetchPaymentTargetSafe(token: String, orderNo: String): PaymentTarget? {
        return try {
            val paymentTargetResponse = api.getPaymentTarget("Bearer $token", orderNo)
            if (paymentTargetResponse.isSuccessful && paymentTargetResponse.body()?.code == "OK") {
                paymentTargetResponse.body()?.data
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getVpnRegions(): Result<List<VpnRegionItem>> = withContext(Dispatchers.IO) {
        try {
            val (response, _) = executeAuthenticatedRequest {
                api.getVpnRegions("Bearer $it")
            } ?: return@withContext Result.failure(Exception("未登录"))
            if (response.isSuccessful && response.body()?.code == "OK") {
                Result.success(response.body()?.data?.items.orEmpty())
            } else {
                Result.failure(Exception(response.body()?.message ?: "获取 VPN 区域失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVpnNodes(
        lineCode: String? = null,
    ): Result<List<VpnNodeItem>> = withContext(Dispatchers.IO) {
        try {
            val (response, _) = executeAuthenticatedRequest {
                api.getVpnNodes("Bearer $it", lineCode)
            } ?: return@withContext Result.failure(Exception("未登录"))
            if (response.isSuccessful && response.body()?.code == "OK") {
                Result.success(response.body()?.data?.items.orEmpty())
            } else {
                Result.failure(Exception(response.body()?.message ?: "获取 VPN 节点失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCachedVpnNodes(
        userId: String = getCurrentUserId().orEmpty(),
        lineCode: String? = null,
    ): List<VpnNodeCacheEntity> {
        if (userId.isBlank()) return emptyList()
        return localRepository.getVpnNodeCache(userId, lineCode)
    }

    suspend fun getCachedVpnNodeRuntime(
        userId: String = getCurrentUserId().orEmpty(),
    ): List<VpnNodeRuntimeEntity> {
        if (userId.isBlank()) return emptyList()
        return localRepository.getVpnNodeRuntime(userId)
    }

    suspend fun syncVpnNodesFromServer(
        force: Boolean = false,
        userId: String? = getCurrentUserId(),
    ): Result<List<VpnNodeCacheEntity>> = withContext(Dispatchers.IO) {
        val resolvedUserId = userId
            ?: return@withContext Result.failure(Exception("未识别当前用户"))
        val now = System.currentTimeMillis()
        val lastSyncAt = prefs.getLong(PaymentConfig.Prefs.LAST_VPN_NODES_SYNC_AT, 0L)
        val cachedNodes = localRepository.getVpnNodeCache(resolvedUserId)
        if (!force && cachedNodes.isNotEmpty() && now - lastSyncAt < VPN_NODE_SYNC_THROTTLE_MS) {
            return@withContext Result.success(cachedNodes)
        }

        val vpnStatus = getVpnStatus().getOrElse {
            return@withContext Result.failure(it)
        }
        val regions = getVpnRegions().getOrElse {
            return@withContext Result.failure(it)
        }
        val allowedLines = regions.filter { it.isAllowed }
        if (allowedLines.isEmpty()) {
            localRepository.syncVpnNodes(resolvedUserId, emptyList(), emptyList())
            prefs.edit().putLong(PaymentConfig.Prefs.LAST_VPN_NODES_SYNC_AT, now).apply()
            return@withContext Result.success(emptyList())
        }

        val remoteNodes = mutableListOf<VpnNodeItem>()
        allowedLines.forEach { line ->
            val result = getVpnNodes(line.regionCode).getOrElse { error ->
                prefs.edit().remove(PaymentConfig.Prefs.LAST_VPN_NODES_SYNC_AT).apply()
                return@withContext Result.failure(error)
            }
            remoteNodes += result
        }
        val nodeCache = remoteNodes.map { node ->
            VpnNodeCacheEntity(
                userId = resolvedUserId,
                nodeId = node.nodeId,
                nodeName = node.nodeName,
                lineCode = node.lineCode,
                lineName = node.lineName,
                regionCode = node.regionCode,
                regionName = node.regionName,
                host = node.host,
                port = node.port,
                status = node.status,
                source = node.source ?: "backend",
                remark = null,
                updatedAt = now,
            )
        }
        val nodeRuntime = remoteNodes.map { node ->
            VpnNodeRuntimeEntity(
                userId = resolvedUserId,
                nodeId = node.nodeId,
                lineCode = node.lineCode,
                healthStatus = node.healthStatus,
                pingMs = null,
                selected = node.selected || vpnStatus.selectedNodeId == node.nodeId,
                lastSeenAt = now,
            )
        }

        localRepository.syncVpnNodes(resolvedUserId, nodeCache, nodeRuntime)
        prefs.edit().putLong(PaymentConfig.Prefs.LAST_VPN_NODES_SYNC_AT, now).apply()
        Result.success(localRepository.getVpnNodeCache(resolvedUserId))
    }

    suspend fun selectVpnNode(
        lineCode: String,
        nodeId: String,
    ): Result<VpnStatusData> = withContext(Dispatchers.IO) {
        try {
            val (response, _) = executeAuthenticatedRequest { token ->
                api.selectVpnNode(
                    authorization = "Bearer $token",
                    request = SelectVpnNodeRequest(
                        lineCode = lineCode,
                        nodeId = nodeId,
                    ),
                )
            } ?: return@withContext Result.failure(Exception("未登录"))
            if (response.isSuccessful && response.body()?.code == "OK") {
                response.body()?.data?.let {
                    cacheVpnStatusMetadata(it)
                    getCurrentUserId()?.let { userId ->
                        localRepository.markSelectedVpnNode(userId, nodeId)
                    }
                    Result.success(it)
                } ?: Result.failure(Exception("节点选择结果为空"))
            } else {
                Result.failure(Exception(response.body()?.message ?: "保存节点选择失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun issueVpnConfig(
        regionCode: String,
        connectionMode: String = "global",
    ): Result<VpnConfigIssueData> = withContext(Dispatchers.IO) {
        try {
            val (response, _) = executeAuthenticatedRequest { token ->
                api.issueVpnConfig(
                    authorization = "Bearer $token",
                    request = IssueVpnConfigRequest(
                        regionCode = regionCode,
                        connectionMode = connectionMode,
                    ),
                )
            } ?: return@withContext Result.failure(Exception("未登录"))
            if (response.isSuccessful && response.body()?.code == "OK") {
                response.body()?.data?.let {
                    saveIssuedVpnConfigMetadata(it)
                    Result.success(it)
                }
                    ?: Result.failure(Exception("VPN 配置为空"))
            } else {
                Result.failure(Exception(response.body()?.message ?: "签发 VPN 配置失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncOrdersFromServer(
        force: Boolean = false,
        userId: String? = getCurrentUserId(),
    ): Result<List<OrderEntity>> = withContext(Dispatchers.IO) {
        val resolvedUserId = userId
            ?: return@withContext Result.failure(Exception("未识别当前用户"))
        val now = System.currentTimeMillis()
        val lastSyncAt = prefs.getLong(PaymentConfig.Prefs.LAST_ORDERS_SYNC_AT, 0L)
        val cachedOrders = localRepository.getOrdersByUserId(resolvedUserId)
        if (!force && cachedOrders.isNotEmpty() && now - lastSyncAt < ORDER_SYNC_THROTTLE_MS) {
            return@withContext Result.success(cachedOrders)
        }

        try {
            val remoteOrders = mutableListOf<Order>()
            var page = 1
            var total = Int.MAX_VALUE
            while (remoteOrders.size < total) {
                val (response, _) = executeAuthenticatedRequest { token ->
                    api.listOrders(
                        authorization = "Bearer $token",
                        page = page,
                        pageSize = ORDER_PAGE_SIZE,
                    )
                } ?: run {
                    prefs.edit().remove(PaymentConfig.Prefs.LAST_ORDERS_SYNC_AT).apply()
                    return@withContext Result.failure(Exception("未登录"))
                }
                if (!response.isSuccessful || response.body()?.code != "OK") {
                    prefs.edit().remove(PaymentConfig.Prefs.LAST_ORDERS_SYNC_AT).apply()
                    val failureMessage = when {
                        response.code() == 404 -> "订单接口待同步"
                        else -> response.body()?.message ?: "同步订单列表失败"
                    }
                    return@withContext Result.failure(
                        Exception(failureMessage),
                    )
                }
                val data = response.body()?.data
                    ?: run {
                        prefs.edit().remove(PaymentConfig.Prefs.LAST_ORDERS_SYNC_AT).apply()
                        return@withContext Result.failure(Exception("订单列表数据为空"))
                    }
                remoteOrders += data.items
                total = data.page.total
                if (data.items.isEmpty() || remoteOrders.size >= total) {
                    break
                }
                page += 1
            }

            remoteOrders.forEach { order ->
                cacheOrder(order, resolvedUserId)
            }
            val remoteOrderNos = remoteOrders.mapTo(mutableSetOf()) { it.orderNo }
            cachedOrders
                .filterNot { it.orderNo in remoteOrderNos }
                .forEach { staleOrder ->
                    localRepository.deleteOrderByOrderNo(staleOrder.orderNo)
                    localRepository.deletePaymentHistoryByOrderNo(staleOrder.orderNo)
                }

            val latestOrder = remoteOrders.maxByOrNull { parseDate(it.createdAt) ?: 0L }
            latestOrder?.let { saveCurrentOrderId(it.orderNo) }
            prefs.edit().putLong(PaymentConfig.Prefs.LAST_ORDERS_SYNC_AT, now).apply()
            Result.success(localRepository.getOrdersByUserId(resolvedUserId))
        } catch (e: Exception) {
            prefs.edit().remove(PaymentConfig.Prefs.LAST_ORDERS_SYNC_AT).apply()
            Result.failure(e)
        }
    }

    suspend fun getVpnStatus(): Result<VpnStatusData> = withContext(Dispatchers.IO) {
        try {
            val (response, _) = executeAuthenticatedRequest {
                api.getVpnStatus("Bearer $it")
            } ?: return@withContext Result.failure(Exception("未登录"))
            if (response.isSuccessful && response.body()?.code == "OK") {
                response.body()?.data?.let {
                    cacheVpnStatusMetadata(it)
                    Result.success(it)
                }
                    ?: Result.failure(Exception("VPN 状态为空"))
            } else {
                Result.failure(Exception(response.body()?.message ?: "获取 VPN 状态失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun <T> executeAuthenticatedRequest(
        call: suspend (token: String) -> Response<T>,
    ): Pair<Response<T>, String>? {
        if (!refreshTokenIfNeeded()) {
            return null
        }
        var token = getAccessToken() ?: return null
        var response = call(token)
        if (response.code() == 401) {
            val refreshed = forceRefreshToken()
            if (refreshed) {
                token = getAccessToken() ?: return Pair(response, token)
                response = call(token)
            }
            if (response.code() == 401) {
                clearAuth()
            }
        }
        return Pair(response, token)
    }

    private fun <T> extractApiErrorMessage(response: Response<T>, fallback: String): String {
        response.body()?.let { body ->
            val messageField = runCatching {
                paymentApiGson.toJsonTree(body).asJsonObject.get("message")?.asString
            }.getOrNull()
            if (!messageField.isNullOrBlank()) {
                return messageField
            }
        }
        val errorMessage = runCatching {
            response.errorBody()?.string()
                ?.takeIf { it.isNotBlank() }
                ?.let { raw ->
                    val json = paymentApiGson.fromJson(raw, Map::class.java)
                    json["message"]?.toString()
                        ?: json["error"]?.toString()
                        ?: raw
                }
        }.getOrNull()
        return errorMessage?.takeIf { it.isNotBlank() } ?: fallback
    }

    suspend fun warmSyncAfterLogin(
        userId: String? = getCurrentUserId(),
        meSnapshot: MeData? = null,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val me = meSnapshot ?: getMe().getOrElse { return@withContext Result.failure(it) }
        val resolvedUserId = userId ?: me?.accountId ?: getCurrentUserId()
            ?: return@withContext Result.failure(Exception("未识别当前用户"))

        syncOrdersFromServer(force = true, userId = resolvedUserId).getOrElse { error ->
            if (!isNonFatalOrderSyncFailure(error.message)) {
                return@withContext Result.failure(error)
            }
        }

        val subscription = getSubscription().getOrElse {
            me?.subscription ?: return@withContext Result.failure(it)
        }
        cacheSubscriptionMetadata(subscription)

        if (!subscription.status.equals("ACTIVE", ignoreCase = true)) {
            cacheSyncScope.launch {
                syncPlansFromServer(force = true)
            }
            cacheSyncScope.launch {
                syncWalletAssetCatalogFromServer(force = true)
            }
            SessionKeepAliveService.start(appContext)
            return@withContext Result.success(Unit)
        }

        getVpnStatus().getOrNull()?.let { cacheVpnStatusMetadata(it) }
        getVpnRegions().getOrNull()
        cacheSyncScope.launch {
            syncPlansFromServer(force = true)
        }
        cacheSyncScope.launch {
            syncWalletAssetCatalogFromServer(force = true)
        }
        cacheSyncScope.launch {
            syncVpnNodesFromServer(force = true, userId = resolvedUserId)
        }

        val shouldBootstrapConfig = MmkvManager.getSelectServer().isNullOrEmpty()
        if (shouldBootstrapConfig) {
            val subscriptionUrl = subscription?.subscriptionUrl?.takeIf { it.isNotBlank() }
            if (!subscriptionUrl.isNullOrBlank()) {
                importSubscriptionUrl(
                    subscriptionUrl = subscriptionUrl,
                    remarks = subscription.planCode?.takeIf { code -> code.isNotBlank() }
                        ?.let { code -> "Purchase $code" }
                        ?: "CryptoVPN Subscription",
                )
            }
        }

        SessionKeepAliveService.start(appContext)
        Result.success(Unit)
    }

    private fun isNonFatalOrderSyncFailure(message: String?): Boolean {
        return message?.contains("订单接口待同步") == true
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

    suspend fun getReferralShareContext(): Result<ReferralShareContextData> = withContext(Dispatchers.IO) {
        try {
            if (!refreshTokenIfNeeded()) {
                return@withContext Result.failure(Exception("Token 已过期，请重新登录"))
            }
            val token = getAccessToken()
                ?: return@withContext Result.failure(Exception("未登录"))
            val response = api.getReferralShareContext("Bearer $token")
            if (response.isSuccessful && response.body()?.code == "OK") {
                response.body()?.data?.let { Result.success(it) }
                    ?: Result.failure(Exception("推广分享上下文为空"))
            } else {
                Result.failure(Exception(response.body()?.message ?: "获取推广分享上下文失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun bindReferralCode(referralCode: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val normalizedReferralCode = normalizeReferralCodeInternal(referralCode)
            if (normalizedReferralCode.isBlank()) {
                return@withContext Result.failure(Exception("邀请码不能为空"))
            }
            if (!refreshTokenIfNeeded()) {
                return@withContext Result.failure(Exception("Token 已过期，请重新登录"))
            }
            val token = getAccessToken()
                ?: return@withContext Result.failure(Exception("未登录"))
            val response = api.bindReferralCode("Bearer $token", ReferralBindRequest(normalizedReferralCode))
            if (response.isSuccessful && response.body()?.code == "OK") {
                Result.success(Unit)
            } else {
                val message = extractApiErrorMessage(response, "绑定邀请码失败")
                Result.failure(Exception(mapReferralBindErrorMessage(message)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapReferralBindErrorMessage(message: String?): String {
        val normalized = message.orEmpty().uppercase(Locale.ROOT)
        return when {
            "REFERRAL_BINDING_LOCKED" in normalized || "ALREADY EXISTS" in normalized ->
                "当前账号已绑定邀请码，不能重复绑定"
            "REFERRAL_CODE_INVALID" in normalized || "INVALID" in normalized ->
                "邀请码无效，请检查大小写和空格"
            "REFERRAL_SELF_BIND_FORBIDDEN" in normalized || "SELF BIND" in normalized ->
                "不能绑定自己的邀请码"
            message.isNullOrBlank() -> "绑定邀请码失败"
            else -> message
        }
    }

    suspend fun tryBindPendingReferralCode(): Result<Boolean> = withContext(Dispatchers.IO) {
        val pendingCode = getPendingReferralCode()
            ?: return@withContext Result.success(false)
        val bindResult = bindReferralCode(pendingCode)
        if (bindResult.isSuccess) {
            clearPendingReferralCode()
            Result.success(true)
        } else if (shouldSkipReferralBindFailure(bindResult.exceptionOrNull()?.message)) {
            clearPendingReferralCode()
            Result.success(false)
        } else {
            Result.failure(bindResult.exceptionOrNull() ?: Exception("邀请码绑定失败"))
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

    suspend fun getCachedWalletOverview(userId: String? = getCurrentUserId()): WalletOverviewData? = withContext(Dispatchers.IO) {
        val resolvedUserId = userId ?: return@withContext null
        localRepository.getWalletOverviewCache(resolvedUserId)?.toWalletOverviewData()
    }

    suspend fun syncWalletOverviewFromServer(
        force: Boolean = false,
        userId: String? = getCurrentUserId(),
    ): Result<WalletOverviewData> = withContext(Dispatchers.IO) {
        val resolvedUserId = userId
        val cached = resolvedUserId?.let { localRepository.getWalletOverviewCache(it) }
        val now = System.currentTimeMillis()
        if (!force && cached != null && now - cached.updatedAt < WALLET_CACHE_SYNC_THROTTLE_MS) {
            return@withContext Result.success(cached.toWalletOverviewData())
        }

        try {
            if (!refreshTokenIfNeeded()) {
                return@withContext cached?.let { Result.success(it.toWalletOverviewData()) }
                    ?: Result.failure(Exception("Token 已过期，请重新登录"))
            }
            val token = getAccessToken()
                ?: return@withContext cached?.let { Result.success(it.toWalletOverviewData()) }
                    ?: Result.failure(Exception("未登录"))
            val response = api.getWalletOverview("Bearer $token")
            if (response.isSuccessful && response.body()?.code == "OK") {
                response.body()?.data?.let { data ->
                    val cacheUserId = resolvedUserId ?: getCurrentUserId()
                    if (!cacheUserId.isNullOrBlank()) {
                        localRepository.saveWalletOverviewCache(
                            WalletOverviewCacheEntity(
                                userId = cacheUserId,
                                accountId = data.accountId,
                                accountEmail = data.accountEmail,
                                selectedNetworkCode = data.selectedNetworkCode,
                                chainItemsJson = paymentApiGson.toJson(data.chainItems),
                                assetItemsJson = paymentApiGson.toJson(data.assetItems),
                                alertsJson = paymentApiGson.toJson(data.alerts),
                                updatedAt = now,
                            ),
                        )
                    }
                    Result.success(data)
                } ?: cached?.let { Result.success(it.toWalletOverviewData()) }
                    ?: Result.failure(Exception("钱包总览为空"))
            } else {
                cached?.let { Result.success(it.toWalletOverviewData()) }
                    ?: Result.failure(Exception(response.body()?.message ?: "获取钱包总览失败"))
            }
        } catch (e: Exception) {
            cached?.let { Result.success(it.toWalletOverviewData()) } ?: Result.failure(e)
        }
    }

    suspend fun getWalletOverview(): Result<WalletOverviewData> = withContext(Dispatchers.IO) {
        val resolvedUserId = getCurrentUserId()
        val cached = getCachedWalletOverview(resolvedUserId)
        if (cached != null) {
            cacheSyncScope.launch {
                syncWalletOverviewFromServer(force = true, userId = resolvedUserId)
            }
            return@withContext Result.success(cached)
        }
        syncWalletOverviewFromServer(force = true, userId = resolvedUserId)
    }

    suspend fun getWalletBalances(): Result<WalletBalancesData> = withContext(Dispatchers.IO) {
        try {
            if (!refreshTokenIfNeeded()) {
                return@withContext Result.failure(Exception("Token 已过期，请重新登录"))
            }
            val token = getAccessToken()
                ?: return@withContext Result.failure(Exception("未登录"))
            val response = api.getWalletBalances("Bearer $token")
            if (response.isSuccessful && response.body()?.code == "OK") {
                response.body()?.data?.let { Result.success(it) }
                    ?: Result.failure(Exception("钱包余额结果为空"))
            } else {
                Result.failure(Exception(extractApiErrorMessage(response, "获取钱包余额失败")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWalletLifecycle(): Result<WalletLifecycleData> = withContext(Dispatchers.IO) {
        try {
            if (!refreshTokenIfNeeded()) {
                return@withContext Result.failure(Exception("Token 已过期，请重新登录"))
            }
            val token = getAccessToken()
                ?: return@withContext Result.failure(Exception("未登录"))
            val response = api.getWalletLifecycle("Bearer $token")
            if (response.isSuccessful && response.body()?.code == "OK") {
                response.body()?.data?.let { Result.success(it) }
                    ?: Result.failure(Exception("钱包生命周期为空"))
            } else {
                Result.failure(Exception(response.body()?.message ?: "获取钱包生命周期失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun upsertWalletLifecycle(
        action: String,
        displayName: String? = null,
        mnemonic: String? = null,
        mnemonicHash: String? = null,
        mnemonicWordCount: Int? = null,
    ): Result<WalletLifecycleData> = withContext(Dispatchers.IO) {
        try {
            if (!refreshTokenIfNeeded()) {
                return@withContext Result.failure(Exception("Token 已过期，请重新登录"))
            }
            val token = getAccessToken()
                ?: return@withContext Result.failure(Exception("未登录"))
            val response = api.upsertWalletLifecycle(
                authorization = "Bearer $token",
                request = WalletLifecycleUpsertRequest(
                    action = action,
                    displayName = displayName?.trim()?.takeIf { it.isNotBlank() },
                    mnemonic = mnemonic?.trim()?.takeIf { it.isNotBlank() },
                    mnemonicHash = mnemonicHash?.trim()?.takeIf { it.isNotBlank() },
                    mnemonicWordCount = mnemonicWordCount,
                ),
            )
            if (response.isSuccessful && response.body()?.code == "OK") {
                response.body()?.data?.let { Result.success(it) }
                    ?: Result.failure(Exception("钱包生命周期为空"))
            } else {
                Result.failure(Exception(response.body()?.message ?: "更新钱包生命周期失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun upsertWalletSecretBackup(
        request: WalletSecretBackupUpsertRequest,
    ): Result<WalletSecretBackupData> = withContext(Dispatchers.IO) {
        try {
            if (!refreshTokenIfNeeded()) {
                return@withContext Result.failure(Exception("Token 已过期，请重新登录"))
            }
            val token = getAccessToken()
                ?: return@withContext Result.failure(Exception("未登录"))
            val response = api.upsertWalletSecretBackup(
                authorization = "Bearer $token",
                request = request,
            )
            if (response.isSuccessful && response.body()?.code == "OK") {
                response.body()?.data?.let { Result.success(it) }
                    ?: Result.failure(Exception("钱包备份结果为空"))
            } else {
                Result.failure(Exception(response.body()?.message ?: "上传钱包备份失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWalletSecretBackupMetadata(): Result<WalletSecretBackupMetadataData> = withContext(Dispatchers.IO) {
        try {
            if (!refreshTokenIfNeeded()) {
                return@withContext Result.failure(Exception("Token 已过期，请重新登录"))
            }
            val token = getAccessToken()
                ?: return@withContext Result.failure(Exception("未登录"))
            val response = api.getWalletSecretBackupMetadata("Bearer $token")
            if (response.isSuccessful && response.body()?.code == "OK") {
                response.body()?.data?.let { Result.success(it) }
                    ?: Result.failure(Exception("钱包备份状态为空"))
            } else {
                Result.failure(Exception(response.body()?.message ?: "获取钱包备份状态失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWalletSecretBackupExport(): Result<WalletSecretBackupExportData> = withContext(Dispatchers.IO) {
        try {
            if (!refreshTokenIfNeeded()) {
                return@withContext Result.failure(Exception("Token 已过期，请重新登录"))
            }
            val token = getAccessToken()
                ?: return@withContext Result.failure(Exception("未登录"))
            val response = api.getWalletSecretBackupExport("Bearer $token")
            if (response.isSuccessful && response.body()?.code == "OK") {
                response.body()?.data?.let { Result.success(it) }
                    ?: Result.failure(Exception("钱包加密备份为空"))
            } else {
                Result.failure(Exception(extractApiErrorMessage(response, "导出加密备份失败")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun upsertWalletPublicAddress(
        networkCode: String,
        assetCode: String,
        address: String,
        isDefault: Boolean,
    ): Result<WalletPublicAddressData> = withContext(Dispatchers.IO) {
        try {
            if (!refreshTokenIfNeeded()) {
                return@withContext Result.failure(Exception("Token 已过期，请重新登录"))
            }
            val token = getAccessToken()
                ?: return@withContext Result.failure(Exception("未登录"))
            val response = api.upsertWalletPublicAddress(
                authorization = "Bearer $token",
                request = WalletPublicAddressUpsertRequest(
                    networkCode = networkCode,
                    assetCode = assetCode,
                    address = address,
                    isDefault = isDefault,
                ),
            )
            if (response.isSuccessful && response.body()?.code == "OK") {
                response.body()?.data?.let { Result.success(it) }
                    ?: Result.failure(Exception("公开地址结果为空"))
            } else {
                Result.failure(Exception(response.body()?.message ?: "同步公开地址失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun buildWalletTransfer(
        networkCode: String,
        assetCode: String,
        fromAddress: String,
        toAddress: String,
        amount: String,
        orderNo: String? = null,
    ): Result<WalletTransferBuildData> = withContext(Dispatchers.IO) {
        try {
            if (!refreshTokenIfNeeded()) {
                return@withContext Result.failure(Exception("Token 已过期，请重新登录"))
            }
            val token = getAccessToken()
                ?: return@withContext Result.failure(Exception("未登录"))
            val response = api.buildWalletTransfer(
                authorization = "Bearer $token",
                request = WalletTransferBuildRequest(
                    networkCode = networkCode,
                    assetCode = assetCode,
                    fromAddress = fromAddress,
                    toAddress = toAddress,
                    amount = amount,
                    orderNo = orderNo,
                ),
            )
            if (response.isSuccessful && response.body()?.code == "OK") {
                response.body()?.data?.let { Result.success(it) }
                    ?: Result.failure(Exception("构建转账结果为空"))
            } else {
                Result.failure(Exception(response.body()?.message ?: "构建转账失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun precheckWalletTransfer(
        networkCode: String,
        assetCode: String,
        toAddress: String,
        amount: String,
        orderNo: String? = null,
    ): Result<WalletTransferPrecheckData> = withContext(Dispatchers.IO) {
        try {
            if (!refreshTokenIfNeeded()) {
                return@withContext Result.failure(Exception("Token 已过期，请重新登录"))
            }
            val token = getAccessToken()
                ?: return@withContext Result.failure(Exception("未登录"))
            val response = api.precheckWalletTransfer(
                authorization = "Bearer $token",
                request = WalletTransferPrecheckRequest(
                    networkCode = networkCode,
                    assetCode = assetCode,
                    toAddress = toAddress,
                    amount = amount,
                    orderNo = orderNo,
                ),
            )
            if (response.isSuccessful && response.body()?.code == "OK") {
                response.body()?.data?.let { Result.success(it) }
                    ?: Result.failure(Exception("预检查结果为空"))
            } else {
                Result.failure(Exception(response.body()?.message ?: "预检查失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun proxyBroadcastWalletTransfer(
        request: WalletTransferProxyBroadcastRequest,
    ): Result<WalletTransferProxyBroadcastData> = withContext(Dispatchers.IO) {
        try {
            if (!refreshTokenIfNeeded()) {
                return@withContext Result.failure(Exception("Token 已过期，请重新登录"))
            }
            val token = getAccessToken()
                ?: return@withContext Result.failure(Exception("未登录"))
            val response = api.proxyBroadcastWalletTransfer(
                authorization = "Bearer $token",
                request = request,
            )
            if (response.isSuccessful && response.body()?.code == "OK") {
                response.body()?.data?.let { Result.success(it) }
                    ?: Result.failure(Exception("广播结果为空"))
            } else {
                Result.failure(Exception(response.body()?.message ?: "广播失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWalletAssetCatalog(
        networkCode: String? = null,
    ): Result<List<WalletAssetItemData>> = withContext(Dispatchers.IO) {
        val cached = readCachedWalletAssetCatalog()
        if (cached != null) {
            cacheSyncScope.launch {
                syncWalletAssetCatalogFromServer(force = true)
            }
            return@withContext Result.success(filterWalletAssetCatalogByNetwork(cached, networkCode))
        }
        syncWalletAssetCatalogFromServer(force = true, networkCode = networkCode)
    }

    suspend fun getCachedWalletAssetCatalog(
        networkCode: String? = null,
    ): List<WalletAssetItemData>? = withContext(Dispatchers.IO) {
        readCachedWalletAssetCatalog()?.let { filterWalletAssetCatalogByNetwork(it, networkCode) }
    }

    suspend fun syncWalletAssetCatalogFromServer(
        force: Boolean = false,
        networkCode: String? = null,
    ): Result<List<WalletAssetItemData>> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val cached = readCachedWalletAssetCatalog()
        val lastSyncAt = prefs.getLong(PaymentConfig.Prefs.WALLET_ASSET_CATALOG_CACHE_UPDATED_AT, 0L)
        if (!force && cached != null && now - lastSyncAt < WALLET_ASSET_CATALOG_CACHE_SYNC_THROTTLE_MS) {
            return@withContext Result.success(filterWalletAssetCatalogByNetwork(cached, networkCode))
        }
        try {
            val (response, _) = executeAuthenticatedRequest { token ->
                api.getWalletAssetCatalog(
                    authorization = "Bearer $token",
                    networkCode = null,
                )
            } ?: return@withContext cached?.let {
                Result.success(filterWalletAssetCatalogByNetwork(it, networkCode))
            } ?: Result.failure(Exception("未登录"))
            if (response.isSuccessful && response.body()?.code == "OK") {
                val items = response.body()?.data?.items.orEmpty()
                saveWalletAssetCatalogCache(items, now)
                Result.success(filterWalletAssetCatalogByNetwork(items, networkCode))
            } else {
                cached?.let { Result.success(filterWalletAssetCatalogByNetwork(it, networkCode)) }
                    ?: Result.failure(Exception(extractApiErrorMessage(response, "获取钱包资产目录失败")))
            }
        } catch (e: Exception) {
            cached?.let { Result.success(filterWalletAssetCatalogByNetwork(it, networkCode)) }
                ?: Result.failure(e)
        }
    }

    suspend fun getCachedWalletReceiveContext(
        networkCode: String? = null,
        assetCode: String? = null,
        userId: String? = getCurrentUserId(),
    ): WalletReceiveContextData? = withContext(Dispatchers.IO) {
        val resolvedUserId = userId ?: return@withContext null
        val requestNetworkCode = normalizeWalletCacheKey(networkCode)
        val requestAssetCode = normalizeWalletCacheKey(assetCode)
        val context = localRepository.getWalletReceiveContextCache(
            userId = resolvedUserId,
            requestNetworkCode = requestNetworkCode,
            requestAssetCode = requestAssetCode,
        ) ?: return@withContext null
        val addresses = localRepository.getWalletPublicAddresses(
            userId = resolvedUserId,
            networkCode = context.selectedNetworkCode,
            assetCode = context.selectedAssetCode,
        )
        context.toWalletReceiveContextData(addresses)
    }

    suspend fun syncWalletReceiveContextFromServer(
        networkCode: String? = null,
        assetCode: String? = null,
        force: Boolean = false,
        userId: String? = getCurrentUserId(),
    ): Result<WalletReceiveContextData> = withContext(Dispatchers.IO) {
        val resolvedUserId = userId
        val requestNetworkCode = normalizeWalletCacheKey(networkCode)
        val requestAssetCode = normalizeWalletCacheKey(assetCode)
        val cachedEntity = if (!resolvedUserId.isNullOrBlank()) {
            localRepository.getWalletReceiveContextCache(
                userId = resolvedUserId,
                requestNetworkCode = requestNetworkCode,
                requestAssetCode = requestAssetCode,
            )
        } else {
            null
        }
        val cached = if (!resolvedUserId.isNullOrBlank() && cachedEntity != null) {
            cachedEntity.toWalletReceiveContextData(
                localRepository.getWalletPublicAddresses(
                    userId = resolvedUserId,
                    networkCode = cachedEntity.selectedNetworkCode,
                    assetCode = cachedEntity.selectedAssetCode,
                ),
            )
        } else {
            null
        }
        val now = System.currentTimeMillis()
        if (!force && cachedEntity != null && now - cachedEntity.updatedAt < WALLET_CACHE_SYNC_THROTTLE_MS) {
            return@withContext Result.success(cached!!)
        }

        try {
            if (!refreshTokenIfNeeded()) {
                return@withContext cached?.let { Result.success(it) }
                    ?: Result.failure(Exception("Token 已过期，请重新登录"))
            }
            val token = getAccessToken()
                ?: return@withContext cached?.let { Result.success(it) }
                    ?: Result.failure(Exception("未登录"))
            val response = api.getWalletReceiveContext(
                authorization = "Bearer $token",
                networkCode = networkCode,
                assetCode = assetCode,
            )
            if (response.isSuccessful && response.body()?.code == "OK") {
                response.body()?.data?.let { data ->
                    val cacheUserId = resolvedUserId ?: getCurrentUserId()
                    if (!cacheUserId.isNullOrBlank()) {
                        localRepository.syncWalletPublicAddresses(
                            userId = cacheUserId,
                            networkCode = data.selectedNetworkCode,
                            assetCode = data.selectedAssetCode,
                            items = data.addresses.map { address ->
                                WalletPublicAddressCacheEntity(
                                    userId = cacheUserId,
                                    addressId = address.addressId,
                                    accountId = address.accountId,
                                    networkCode = address.networkCode,
                                    assetCode = address.assetCode,
                                    address = address.address,
                                    isDefault = address.isDefault,
                                    createdAt = address.createdAt,
                                    updatedAt = now,
                                )
                            },
                        )
                        localRepository.saveWalletReceiveContextCache(
                            WalletReceiveContextCacheEntity(
                                userId = cacheUserId,
                                requestNetworkCode = requestNetworkCode,
                                requestAssetCode = requestAssetCode,
                                selectedNetworkCode = data.selectedNetworkCode,
                                selectedAssetCode = data.selectedAssetCode,
                                chainItemsJson = paymentApiGson.toJson(data.chainItems),
                                assetItemsJson = paymentApiGson.toJson(data.assetItems),
                                defaultAddress = data.defaultAddress,
                                canShare = data.canShare,
                                walletExists = data.walletExists,
                                receiveState = data.receiveState,
                                status = data.status,
                                note = data.note,
                                shareText = data.shareText,
                                updatedAt = now,
                            ),
                        )
                    }
                    Result.success(data)
                } ?: cached?.let { Result.success(it) }
                    ?: Result.failure(Exception("收款上下文为空"))
            } else {
                cached?.let { Result.success(it) }
                    ?: Result.failure(Exception(response.body()?.message ?: "获取收款上下文失败"))
            }
        } catch (e: Exception) {
            cached?.let { Result.success(it) } ?: Result.failure(e)
        }
    }

    suspend fun getWalletReceiveContext(
        networkCode: String? = null,
        assetCode: String? = null,
    ): Result<WalletReceiveContextData> = withContext(Dispatchers.IO) {
        val resolvedUserId = getCurrentUserId()
        val cached = getCachedWalletReceiveContext(
            networkCode = networkCode,
            assetCode = assetCode,
            userId = resolvedUserId,
        )
        if (cached != null) {
            cacheSyncScope.launch {
                syncWalletReceiveContextFromServer(
                    networkCode = networkCode,
                    assetCode = assetCode,
                    force = true,
                    userId = resolvedUserId,
                )
            }
            return@withContext Result.success(cached)
        }
        syncWalletReceiveContextFromServer(
            networkCode = networkCode,
            assetCode = assetCode,
            force = true,
            userId = resolvedUserId,
        )
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

    fun getLastIssuedVpnConfigExpireAt(): String? {
        return prefs.getString(PaymentConfig.Prefs.LAST_VPN_CONFIG_EXPIRE_AT, null)
    }

    fun getLastIssuedVpnRegionCode(): String? {
        return prefs.getString(PaymentConfig.Prefs.LAST_VPN_REGION_CODE, null)
    }

    private fun cacheSubscriptionMetadata(subscription: CurrentSubscriptionData?) {
        val editor = prefs.edit()
        subscription?.planCode?.takeIf { it.isNotBlank() }?.let {
            editor.putString(PaymentConfig.Prefs.LAST_SUBSCRIPTION_PLAN_CODE, it)
        }
        subscription?.status.takeIf { !it.isNullOrBlank() }?.let {
            editor.putString(PaymentConfig.Prefs.LAST_SUBSCRIPTION_STATUS, it)
        }
        subscription?.daysRemaining?.let {
            editor.putInt(PaymentConfig.Prefs.LAST_SUBSCRIPTION_DAYS_REMAINING, it)
        }
        subscription?.expireAt?.takeIf { it.isNotBlank() }?.let {
            editor.putString(PaymentConfig.Prefs.LAST_VPN_CONFIG_EXPIRE_AT, it)
        }
        subscription?.subscriptionUrl?.takeIf { it.isNotBlank() }?.let {
            editor.putString(PaymentConfig.Prefs.SUBSCRIPTION_URL, it)
        }
        subscription?.marzbanUsername?.takeIf { it.isNotBlank() }?.let {
            editor.putString(PaymentConfig.Prefs.MARZBAN_USERNAME, it)
        }
        editor.apply()
    }

    private fun cacheVpnStatusMetadata(status: VpnStatusData?) {
        val editor = prefs.edit()
        status?.currentRegionCode?.takeIf { it.isNotBlank() }?.let {
            editor.putString(PaymentConfig.Prefs.LAST_VPN_REGION_CODE, it)
        }
        status?.selectedLineName?.takeIf { it.isNotBlank() }?.let {
            editor.putString(PaymentConfig.Prefs.LAST_VPN_LINE_NAME, it)
        }
        status?.selectedNodeId?.takeIf { it.isNotBlank() }?.let {
            editor.putString(PaymentConfig.Prefs.LAST_VPN_NODE_ID, it)
        }
        status?.selectedNodeName?.takeIf { it.isNotBlank() }?.let {
            editor.putString(PaymentConfig.Prefs.LAST_VPN_NODE_NAME, it)
        }
        status?.sessionStatus.takeIf { !it.isNullOrBlank() }?.let {
            editor.putString(PaymentConfig.Prefs.LAST_VPN_SESSION_STATUS, it)
        }
        editor.apply()
    }

    private fun resolveBootstrapRegionCode(
        vpnStatus: VpnStatusData?,
        regions: List<VpnRegionItem>,
    ): String? {
        return vpnStatus?.currentRegionCode?.takeIf { it.isNotBlank() }
            ?: regions.firstOrNull {
                it.isAllowed && (
                    it.status.equals("ACTIVE", ignoreCase = true) ||
                        it.status.equals("ONLINE", ignoreCase = true)
                    )
            }?.regionCode
            ?: regions.firstOrNull { it.isAllowed }?.regionCode
    }

    fun importIssuedVpnConfig(config: VpnConfigIssueData): Boolean {
        val profile = VlessFmt.parse(config.configPayload) ?: return false
        val subscriptionId = Utils.getUuid()
        MmkvManager.encodeSubscription(
            subscriptionId,
            SubscriptionItem(
                remarks = "Purchase ${config.regionCode}",
                url = config.configPayload,
                enabled = true,
                lastUpdated = System.currentTimeMillis(),
                autoUpdate = false,
            ),
        )
        profile.subscriptionId = subscriptionId
        profile.remarks = "Purchase ${config.regionCode}"
        profile.description = AngConfigManager.generateDescription(profile)
        val guid = MmkvManager.encodeServerConfig("", profile)
        MmkvManager.setSelectServer(guid)
        return true
    }

    fun selectLocalServerForNode(node: VpnNodeItem): Boolean {
        val matchedGuid = MmkvManager.decodeAllServerList()
            .firstOrNull { guid ->
                val profile = MmkvManager.decodeServerConfig(guid) ?: return@firstOrNull false
                profile.server == node.host && profile.serverPort == node.port.toString()
            } ?: return false
        MmkvManager.setSelectServer(matchedGuid)
        return true
    }

    private fun saveIssuedVpnConfigMetadata(config: VpnConfigIssueData) {
        prefs.edit()
            .putString(PaymentConfig.Prefs.SUBSCRIPTION_URL, config.configPayload)
            .putString(PaymentConfig.Prefs.LAST_VPN_REGION_CODE, config.regionCode)
            .putString(PaymentConfig.Prefs.LAST_VPN_CONFIG_EXPIRE_AT, config.expireAt)
            .apply()
    }

    /**
     * 解析 ISO 8601 日期字符串
     */
    private fun parseIsoDate(dateString: String): Long? = parseIsoDateInternal(dateString)

    private fun normalizeWalletCacheKey(value: String?): String = value?.trim().orEmpty()

    private fun readCachedPlans(): List<Plan>? {
        val payload = prefs.getString(PaymentConfig.Prefs.PLANS_CACHE_JSON, null)
            ?: return null
        return runCatching {
            paymentApiGson.fromJson<List<Plan>>(
                payload,
                object : TypeToken<List<Plan>>() {}.type,
            )
        }.getOrNull()
    }

    private fun savePlansCache(plans: List<Plan>, updatedAt: Long) {
        prefs.edit()
            .putString(PaymentConfig.Prefs.PLANS_CACHE_JSON, paymentApiGson.toJson(plans))
            .putLong(PaymentConfig.Prefs.PLANS_CACHE_UPDATED_AT, updatedAt)
            .apply()
    }

    private fun readCachedWalletAssetCatalog(): List<WalletAssetItemData>? {
        val payload = prefs.getString(PaymentConfig.Prefs.WALLET_ASSET_CATALOG_CACHE_JSON, null)
            ?: return null
        return runCatching {
            paymentApiGson.fromJson<List<WalletAssetItemData>>(
                payload,
                object : TypeToken<List<WalletAssetItemData>>() {}.type,
            )
        }.getOrNull()
    }

    private fun saveWalletAssetCatalogCache(items: List<WalletAssetItemData>, updatedAt: Long) {
        prefs.edit()
            .putString(PaymentConfig.Prefs.WALLET_ASSET_CATALOG_CACHE_JSON, paymentApiGson.toJson(items))
            .putLong(PaymentConfig.Prefs.WALLET_ASSET_CATALOG_CACHE_UPDATED_AT, updatedAt)
            .apply()
    }

    private fun filterWalletAssetCatalogByNetwork(
        items: List<WalletAssetItemData>,
        networkCode: String?,
    ): List<WalletAssetItemData> {
        if (networkCode.isNullOrBlank()) {
            return items
        }
        return items.filter { it.networkCode.equals(networkCode, ignoreCase = true) }
    }

    private fun WalletOverviewCacheEntity.toWalletOverviewData(): WalletOverviewData {
        return WalletOverviewData(
            accountId = accountId,
            accountEmail = accountEmail,
            selectedNetworkCode = selectedNetworkCode,
            chainItems = paymentApiGson.fromJson(
                chainItemsJson,
                object : TypeToken<List<WalletChainItemData>>() {}.type,
            ),
            assetItems = paymentApiGson.fromJson(
                assetItemsJson,
                object : TypeToken<List<WalletAssetItemData>>() {}.type,
            ),
            alerts = paymentApiGson.fromJson(
                alertsJson,
                object : TypeToken<List<String>>() {}.type,
            ),
        )
    }

    private fun WalletReceiveContextCacheEntity.toWalletReceiveContextData(
        addresses: List<WalletPublicAddressCacheEntity>,
    ): WalletReceiveContextData {
        return WalletReceiveContextData(
            selectedNetworkCode = selectedNetworkCode,
            selectedAssetCode = selectedAssetCode,
            chainItems = paymentApiGson.fromJson(
                chainItemsJson,
                object : TypeToken<List<WalletChainItemData>>() {}.type,
            ),
            assetItems = paymentApiGson.fromJson(
                assetItemsJson,
                object : TypeToken<List<WalletAssetItemData>>() {}.type,
            ),
            addresses = addresses.map { address ->
                WalletPublicAddressData(
                    addressId = address.addressId,
                    accountId = address.accountId,
                    networkCode = address.networkCode,
                    assetCode = address.assetCode,
                    address = address.address,
                    isDefault = address.isDefault,
                    createdAt = address.createdAt,
                )
            },
            defaultAddress = defaultAddress,
            canShare = canShare,
            walletExists = walletExists,
            receiveState = receiveState,
            status = status,
            note = note,
            shareText = shareText,
        )
    }
}

private class OrderCreatedAtFallbackAdapterFactory : TypeAdapterFactory {
    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        if (type.rawType != Order::class.java) {
            return null
        }

        val delegate = gson.getDelegateAdapter(this, type)
        return object : TypeAdapter<T>() {
            override fun write(out: JsonWriter, value: T?) {
                delegate.write(out, value)
            }

            override fun read(reader: JsonReader): T {
                val payload = JsonParser.parseReader(reader)
                if (payload.isJsonObject) {
                    payload.asJsonObject.backfillOrderCreatedAt()
                }
                return delegate.fromJsonTree(payload)
            }
        }
    }
}

private fun JsonObject.backfillOrderCreatedAt() {
    val createdAt = getStringOrNull("createdAt")
    if (!createdAt.isNullOrBlank()) {
        return
    }

    val fallback = getStringOrNull("expiresAt")
    if (!fallback.isNullOrBlank()) {
        addProperty("createdAt", fallback)
    }
}

private fun JsonObject.getStringOrNull(fieldName: String): String? {
    val element = get(fieldName) ?: return null
    if (element.isJsonNull) {
        return null
    }
    return runCatching { element.asString }.getOrNull()
}
