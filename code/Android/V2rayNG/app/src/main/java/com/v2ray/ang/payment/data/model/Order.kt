package com.v2ray.ang.payment.data.model

import com.google.gson.annotations.SerializedName

/**
 * 订单数据模型
 */
data class Order(
    @SerializedName("order_id")
    val orderId: String,
    @SerializedName("order_no")
    val orderNo: String,
    val status: String,
    @SerializedName("status_text")
    val statusText: String,
    val plan: PlanInfo,
    val payment: PaymentInfo,
    @SerializedName("expires_at")
    val expiresAt: String,
    @SerializedName("created_at")
    val createdAt: String,
    val fulfillment: FulfillmentInfo? = null
)

data class PlanInfo(
    val id: String,
    val name: String
)

data class PaymentInfo(
    @SerializedName("asset_code")
    val assetCode: String,
    @SerializedName("amount_crypto")
    val amountCrypto: String,
    @SerializedName("receive_address")
    val receiveAddress: String,
    @SerializedName("qr_text")
    val qrText: String,
    @SerializedName("tx_hash")
    val txHash: String? = null,
    @SerializedName("confirmed_at")
    val confirmedAt: String? = null
)

data class FulfillmentInfo(
    @SerializedName("marzban_username")
    val marzbanUsername: String,
    @SerializedName("client_token")
    val clientToken: String,
    @SerializedName("token_expires_at")
    val tokenExpiresAt: String,
    @SerializedName("subscription_url")
    val subscriptionUrl: String,
    @SerializedName("expired_at")
    val expiredAt: String
)

/**
 * 创建订单请求
 */
data class CreateOrderRequest(
    @SerializedName("plan_id")
    val planId: String,
    @SerializedName("purchase_type")
    val purchaseType: String,
    @SerializedName("asset_code")
    val assetCode: String,
    @SerializedName("client_device_id")
    val clientDeviceId: String,
    @SerializedName("client_version")
    val clientVersion: String,
    @SerializedName("client_token")
    val clientToken: String? = null,
    @SerializedName("client_user_id")
    val clientUserId: String? = null,
    @SerializedName("marzban_username")
    val marzbanUsername: String? = null
)

/**
 * 创建订单响应
 */
data class CreateOrderResponse(
    val code: String,
    val message: String,
    val data: Order?
)

/**
 * 查询订单响应
 */
data class GetOrderResponse(
    val code: String,
    val message: String,
    val data: Order?
)

// ==================== 认证相关数据类 ====================

/**
 * 登录请求
 */
data class LoginRequest(
    val username: String,
    val password: String
)

/**
 * 注册请求
 */
data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String? = null
)

/**
 * 认证响应数据
 */
data class AuthData(
    @SerializedName("user_id")
    val userId: String,
    val username: String,
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("expires_at")
    val expiresAt: String
)

/**
 * 登录响应
 */
data class LoginResponse(
    val code: String,
    val message: String,
    val data: AuthData?
)

/**
 * 注册响应
 */
data class RegisterResponse(
    val code: String,
    val message: String,
    val data: AuthData?
)

// ==================== Token 刷新相关数据类 ====================

/**
 * Token 刷新请求
 * 实际使用 Header 传递 refresh_token，此请求体为空
 */
data class RefreshTokenRequest(
    val dummy: String? = null
)

/**
 * Token 刷新响应数据
 */
data class RefreshTokenData(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("expires_at")
    val expiresAt: String
)

/**
 * Token 刷新响应
 */
data class RefreshTokenResponse(
    val code: String,
    val message: String,
    val data: RefreshTokenData?
)
