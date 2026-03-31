package com.v2ray.ang.payment;

/**
 * 支付模块配置
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0010 \n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0004\u000f\u0010\u0011\u0012B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0086T\u00a2\u0006\u0002\n\u0000R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u000e\u0010\u000e\u001a\u00020\tX\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0013"}, d2 = {"Lcom/v2ray/ang/payment/PaymentConfig;", "", "<init>", "()V", "API_BASE_URL", "", "API_VERSION", "FULL_API_URL", "ORDER_EXPIRE_TIME_MS", "", "POLLING_INTERVALS", "", "getPOLLING_INTERVALS", "()Ljava/util/List;", "MAX_POLLING_TIME_MS", "Prefs", "AssetCode", "OrderStatus", "PurchaseType", "app_fdroidDebug"})
public final class PaymentConfig {
    
    /**
     * API 基础 URL
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String API_BASE_URL = "https://154.36.173.184:8080";
    
    /**
     * 客户端 API 版本
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String API_VERSION = "/client/v1";
    
    /**
     * 完整 API 基础 URL
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String FULL_API_URL = "https://154.36.173.184:8080/client/v1";
    
    /**
     * 订单过期时间（毫秒）
     */
    public static final long ORDER_EXPIRE_TIME_MS = 900000L;
    
    /**
     * 轮询间隔配置（毫秒）
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.Long> POLLING_INTERVALS = null;
    
    /**
     * 最大轮询时间（毫秒）
     */
    public static final long MAX_POLLING_TIME_MS = 960000L;
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.payment.PaymentConfig INSTANCE = null;
    
    private PaymentConfig() {
        super();
    }
    
    /**
     * 轮询间隔配置（毫秒）
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.Long> getPOLLING_INTERVALS() {
        return null;
    }
    
    /**
     * 支持的支付资产
     */
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/v2ray/ang/payment/PaymentConfig$AssetCode;", "", "<init>", "()V", "SOL", "", "USDT_TRC20", "app_fdroidDebug"})
    public static final class AssetCode {
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String SOL = "SOL";
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String USDT_TRC20 = "USDT_TRC20";
        @org.jetbrains.annotations.NotNull()
        public static final com.v2ray.ang.payment.PaymentConfig.AssetCode INSTANCE = null;
        
        private AssetCode() {
            super();
        }
    }
    
    /**
     * 订单状态
     */
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\n\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/v2ray/ang/payment/PaymentConfig$OrderStatus;", "", "<init>", "()V", "PENDING_PAYMENT", "", "SEEN_ONCHAIN", "CONFIRMING", "PAID_SUCCESS", "FULFILLED", "EXPIRED", "UNDERPAID", "OVERPAID", "FAILED", "LATE_PAID", "app_fdroidDebug"})
    public static final class OrderStatus {
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String PENDING_PAYMENT = "pending_payment";
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String SEEN_ONCHAIN = "seen_onchain";
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String CONFIRMING = "confirming";
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String PAID_SUCCESS = "paid_success";
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String FULFILLED = "fulfilled";
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String EXPIRED = "expired";
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String UNDERPAID = "underpaid";
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String OVERPAID = "overpaid";
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String FAILED = "failed";
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String LATE_PAID = "late_paid";
        @org.jetbrains.annotations.NotNull()
        public static final com.v2ray.ang.payment.PaymentConfig.OrderStatus INSTANCE = null;
        
        private OrderStatus() {
            super();
        }
    }
    
    /**
     * MMKV 存储键
     */
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0006\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/v2ray/ang/payment/PaymentConfig$Prefs;", "", "<init>", "()V", "CLIENT_TOKEN", "", "TOKEN_EXPIRES_AT", "MARZBAN_USERNAME", "SUBSCRIPTION_URL", "CURRENT_ORDER_ID", "DEVICE_ID", "app_fdroidDebug"})
    public static final class Prefs {
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String CLIENT_TOKEN = "payment_client_token";
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String TOKEN_EXPIRES_AT = "payment_token_expires_at";
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String MARZBAN_USERNAME = "payment_marzban_username";
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String SUBSCRIPTION_URL = "payment_subscription_url";
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String CURRENT_ORDER_ID = "payment_current_order_id";
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String DEVICE_ID = "payment_device_id";
        @org.jetbrains.annotations.NotNull()
        public static final com.v2ray.ang.payment.PaymentConfig.Prefs INSTANCE = null;
        
        private Prefs() {
            super();
        }
    }
    
    /**
     * 购买类型
     */
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/v2ray/ang/payment/PaymentConfig$PurchaseType;", "", "<init>", "()V", "NEW", "", "RENEW", "app_fdroidDebug"})
    public static final class PurchaseType {
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String NEW = "new";
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String RENEW = "renew";
        @org.jetbrains.annotations.NotNull()
        public static final com.v2ray.ang.payment.PaymentConfig.PurchaseType INSTANCE = null;
        
        private PurchaseType() {
            super();
        }
    }
}