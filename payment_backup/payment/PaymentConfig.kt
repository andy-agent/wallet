package com.v2ray.ang.payment

/**
 * 支付模块配置
 */
object PaymentConfig {
    
    /**
     * API 基础 URL
     * TODO: Configure your actual API domain before production deployment
     * This placeholder must be replaced with your real backend domain.
     */
    const val API_BASE_URL = "https://your-api-domain.com"
    
    /**
     * 客户端 API 版本
     */
    const val API_VERSION = "/client/v1"
    
    /**
     * 完整 API 基础 URL
     */
    const val FULL_API_URL = "$API_BASE_URL$API_VERSION"
    
    /**
     * 订单过期时间（毫秒）
     */
    const val ORDER_EXPIRE_TIME_MS = 15 * 60 * 1000L // 15分钟
    
    /**
     * 轮询间隔配置（毫秒）
     */
    val POLLING_INTERVALS = listOf(3000L, 3000L, 5000L, 5000L, 8000L, 8000L, 10000L)
    
    /**
     * 最大轮询时间（毫秒）
     */
    const val MAX_POLLING_TIME_MS = ORDER_EXPIRE_TIME_MS + 60000L // 订单过期后额外1分钟
    
    /**
     * MMKV 存储键
     */
    object Prefs {
        const val CLIENT_TOKEN = "payment_client_token"
        const val TOKEN_EXPIRES_AT = "payment_token_expires_at"
        const val MARZBAN_USERNAME = "payment_marzban_username"
        const val SUBSCRIPTION_URL = "payment_subscription_url"
        const val CURRENT_ORDER_ID = "payment_current_order_id"
        const val DEVICE_ID = "payment_device_id"
    }
    
    /**
     * 支持的支付资产
     */
    object AssetCode {
        const val SOL = "SOL"
        const val USDT_TRC20 = "USDT_TRC20"
    }
    
    /**
     * 订单状态
     */
    object OrderStatus {
        const val PENDING_PAYMENT = "pending_payment"
        const val SEEN_ONCHAIN = "seen_onchain"
        const val CONFIRMING = "confirming"
        const val PAID_SUCCESS = "paid_success"
        const val FULFILLED = "fulfilled"
        const val EXPIRED = "expired"
        const val UNDERPAID = "underpaid"
        const val OVERPAID = "overpaid"
        const val FAILED = "failed"
        const val LATE_PAID = "late_paid"
    }
    
    /**
     * 购买类型
     */
    object PurchaseType {
        const val NEW = "new"
        const val RENEW = "renew"
    }
}
