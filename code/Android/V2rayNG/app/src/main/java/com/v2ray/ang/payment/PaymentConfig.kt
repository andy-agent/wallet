package com.v2ray.ang.payment

/**
 * 支付模块配置
 */
object PaymentConfig {
    
    /**
     * API 基础 URL
     */
    const val API_BASE_URL = "https://api.residential-agent.com/"
    
    /**
     * 客户端 API 版本
     */
    const val API_VERSION = "api/client/v1"
    
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
        const val LAST_ORDERS_SYNC_AT = "payment_last_orders_sync_at"
        const val LAST_VPN_NODES_SYNC_AT = "payment_last_vpn_nodes_sync_at"
        const val LAST_VPN_REGION_CODE = "payment_last_vpn_region_code"
        const val LAST_VPN_LINE_NAME = "payment_last_vpn_line_name"
        const val LAST_VPN_NODE_ID = "payment_last_vpn_node_id"
        const val LAST_VPN_NODE_NAME = "payment_last_vpn_node_name"
        const val LAST_VPN_SESSION_STATUS = "payment_last_vpn_session_status"
        const val LAST_SUBSCRIPTION_PLAN_CODE = "payment_last_subscription_plan_code"
        const val LAST_SUBSCRIPTION_STATUS = "payment_last_subscription_status"
        const val LAST_SUBSCRIPTION_DAYS_REMAINING = "payment_last_subscription_days_remaining"
        const val LAST_VPN_CONFIG_EXPIRE_AT = "payment_last_vpn_config_expire_at"
        const val PLANS_CACHE_JSON = "payment_plans_cache_json"
        const val PLANS_CACHE_UPDATED_AT = "payment_plans_cache_updated_at"
        const val WALLET_ASSET_CATALOG_CACHE_JSON = "payment_wallet_asset_catalog_cache_json"
        const val WALLET_ASSET_CATALOG_CACHE_UPDATED_AT = "payment_wallet_asset_catalog_cache_updated_at"
        
        // Token 相关存储键
        const val ACCESS_TOKEN = "payment_access_token"
        const val REFRESH_TOKEN = "payment_refresh_token"
        const val AUTH_TOKEN_EXPIRES_AT = "payment_auth_token_expires_at"
    }
    
    /**
     * Token 相关配置
     */
    object TokenConfig {
        // Token 过期前 5 分钟的缓冲时间（毫秒）
        const val TOKEN_REFRESH_BUFFER_MS = 5 * 60 * 1000L
    }
    
    /**
     * 支持的支付资产
     */
    object AssetCode {
        const val SOL = "SOL"
        const val USDT = "USDT"
        const val USDT_TRC20 = USDT
    }

    /**
     * 支持的支付网络
     */
    object NetworkCode {
        const val SOLANA = "SOLANA"
        const val TRON = "TRON"
    }
    
    /**
     * 订单状态
     */
    object OrderStatus {
        const val PENDING_PAYMENT = "AWAITING_PAYMENT"
        const val SEEN_ONCHAIN = "PAYMENT_DETECTED"
        const val CONFIRMING = "CONFIRMING"
        const val PAID_SUCCESS = "PAID"
        const val FULFILLED = "COMPLETED"
        const val EXPIRED = "EXPIRED"
        const val UNDERPAID = "UNDERPAID_REVIEW"
        const val OVERPAID = "OVERPAID_REVIEW"
        const val FAILED = "FAILED"
        const val LATE_PAID = "CANCELED"
    }
    
    /**
     * 购买类型
     */
    object PurchaseType {
        const val NEW = "NEW"
        const val RENEW = "RENEWAL"
    }
}
