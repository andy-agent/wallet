package com.v2ray.ang.payment.data.api

import com.v2ray.ang.payment.PaymentConfig
import com.v2ray.ang.payment.data.model.*
import retrofit2.Response
import com.google.gson.annotations.SerializedName
import retrofit2.http.*

/**
 * 支付 API 接口
 */
interface PaymentApi {

    /**
     * 获取套餐列表
     */
    @GET("${PaymentConfig.API_VERSION}/plans")
    suspend fun getPlans(): Response<PlansResponse>

    /**
     * 创建订单
     */
    @POST("${PaymentConfig.API_VERSION}/orders")
    suspend fun createOrder(
        @Body request: CreateOrderRequest
    ): Response<CreateOrderResponse>

    /**
     * 查询订单状态
     */
    @GET("${PaymentConfig.API_VERSION}/orders/{orderId}")
    suspend fun getOrder(
        @Path("orderId") orderId: String
    ): Response<GetOrderResponse>

    /**
     * 获取订阅信息
     */
    @GET("${PaymentConfig.API_VERSION}/subscription")
    suspend fun getSubscription(
        @Header("Authorization") token: String
    ): Response<SubscriptionResponse>
}

/**
 * 订阅响应
 */
data class SubscriptionResponse(
    val code: String,
    val message: String,
    val data: SubscriptionData?
)

data class SubscriptionData(
    val user: UserInfo,
    val subscription: SubscriptionInfo,
    val servers: List<ServerInfo>
)

data class UserInfo(
    val username: String,
    val status: String,
    @SerializedName("expired_at")
    val expiredAt: String,
    @SerializedName("traffic_total")
    val trafficTotal: Long,
    @SerializedName("traffic_used")
    val trafficUsed: Long,
    @SerializedName("traffic_remaining")
    val trafficRemaining: Long
)

data class SubscriptionInfo(
    val url: String,
    @SerializedName("expires_at")
    val expiresAt: String
)

data class ServerInfo(
    val protocol: String,
    val config: String,
    val remark: String,
    val region: String
)
