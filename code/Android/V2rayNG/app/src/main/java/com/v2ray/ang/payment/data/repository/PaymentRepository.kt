package com.v2ray.ang.payment.data.repository

import android.content.Context
import com.v2ray.ang.BuildConfig
import com.v2ray.ang.payment.PaymentConfig
import com.v2ray.ang.payment.data.api.PaymentApi
import com.v2ray.ang.payment.data.model.*
import com.v2ray.ang.util.MmkvManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

/**
 * 支付仓库类
 */
class PaymentRepository(context: Context) {

    private val api: PaymentApi by lazy {
        // TODO: Replace with your actual API domain and certificate SHA-256 hash.
        // To get the certificate hash, run:
        //   openssl s_client -connect api.yourdomain.com:443 -servername api.yourdomain.com < /dev/null 2>/dev/null | openssl x509 -pubkey -noout | openssl pkey -pubin -outform der | openssl dgst -sha256 -binary | openssl enc -base64
        // Or use: ./gradlew :app:pinCert -Purl=https://api.yourdomain.com
        val certificatePinner = CertificatePinner.Builder()
            .add("api.example.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
            .build()

        val client = OkHttpClient.Builder()
            .certificatePinner(certificatePinner)
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

    private val mmkv = MmkvManager.mmkv
    
    // In-memory fallback when MMKV is unavailable
    private var cachedDeviceId: String? = null

    /**
     * 获取设备唯一ID
     */
    fun getDeviceId(): String {
        // Return cached value if available (handles MMKV null case)
        cachedDeviceId?.let { return it }
        
        // Try to get from MMKV
        var deviceId = mmkv?.decodeString(PaymentConfig.Prefs.DEVICE_ID)
        
        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString()
            mmkv?.encode(PaymentConfig.Prefs.DEVICE_ID, deviceId)
        }
        
        // Cache in memory to avoid regenerating if MMKV is null
        cachedDeviceId = deviceId
        return deviceId
    }

    /**
     * 保存客户端Token
     */
    fun saveClientToken(token: String, expiresAt: String) {
        mmkv?.encode(PaymentConfig.Prefs.CLIENT_TOKEN, token)
        mmkv?.encode(PaymentConfig.Prefs.TOKEN_EXPIRES_AT, expiresAt)
    }

    /**
     * 获取客户端Token
     */
    fun getClientToken(): String? {
        return mmkv?.decodeString(PaymentConfig.Prefs.CLIENT_TOKEN)
    }

    /**
     * 保存当前订单ID
     */
    fun saveCurrentOrderId(orderId: String) {
        mmkv?.encode(PaymentConfig.Prefs.CURRENT_ORDER_ID, orderId)
    }

    /**
     * 获取当前订单ID
     */
    fun getCurrentOrderId(): String? {
        return mmkv?.decodeString(PaymentConfig.Prefs.CURRENT_ORDER_ID)
    }

    /**
     * 清除当前订单
     */
    fun clearCurrentOrder() {
        mmkv?.remove(PaymentConfig.Prefs.CURRENT_ORDER_ID)
    }

    /**
     * 保存订阅信息
     */
    fun saveSubscription(url: String, username: String) {
        mmkv?.encode(PaymentConfig.Prefs.SUBSCRIPTION_URL, url)
        mmkv?.encode(PaymentConfig.Prefs.MARZBAN_USERNAME, username)
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
                clientToken = clientToken
            )
            
            val response = api.createOrder(request)
            if (response.isSuccessful && response.body()?.code == "SUCCESS") {
                val order = response.body()?.data
                if (order != null) {
                    saveCurrentOrderId(order.orderId)
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
