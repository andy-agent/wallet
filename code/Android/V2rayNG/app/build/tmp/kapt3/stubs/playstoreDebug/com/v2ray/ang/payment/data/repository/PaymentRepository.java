package com.v2ray.ang.payment.data.repository;

/**
 * 支付仓库类
 * 集成本地数据库缓存
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u00bc\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 v2\u00020\u0001:\u0001vB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0012\u001a\u00020\tJ\u000e\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0011J\b\u0010\u0016\u001a\u0004\u0018\u00010\u0011J\u001e\u0010\u0017\u001a\u00020\u00142\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0002\u0010\u001bJ\u001e\u0010\u001c\u001a\u00020\u00142\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u0015\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0002\u0010\u001fJ\u0016\u0010 \u001a\u00020\u00142\u0006\u0010\u001d\u001a\u00020\u001eH\u0086@\u00a2\u0006\u0002\u0010!J\u001c\u0010\"\u001a\b\u0012\u0004\u0012\u00020$0#2\u0006\u0010\u0015\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0002\u0010%J\u001c\u0010&\u001a\b\u0012\u0004\u0012\u00020\'0#2\u0006\u0010\u0015\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0002\u0010%J\u0010\u0010(\u001a\u0004\u0018\u00010)H\u0086@\u00a2\u0006\u0002\u0010*J\u000e\u0010+\u001a\u00020\u0014H\u0086@\u00a2\u0006\u0002\u0010*J\u0019\u0010,\u001a\u0004\u0018\u00010-2\b\u0010.\u001a\u0004\u0018\u00010\u0011H\u0002\u00a2\u0006\u0002\u0010/J\u0006\u00100\u001a\u00020\u0011J\u0016\u00101\u001a\u00020\u00142\u0006\u00102\u001a\u00020\u00112\u0006\u00103\u001a\u00020\u0011J\b\u00104\u001a\u0004\u0018\u00010\u0011J\u000e\u00105\u001a\u00020\u00142\u0006\u00106\u001a\u00020\u0011J\b\u00107\u001a\u0004\u0018\u00010\u0011J\u0006\u00108\u001a\u00020\u0014J\u0016\u00109\u001a\u00020\u00142\u0006\u0010:\u001a\u00020\u00112\u0006\u0010;\u001a\u00020\u0011J\u001c\u0010<\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020>0#0=H\u0086@\u00a2\u0006\u0004\b?\u0010*J<\u0010@\u001a\b\u0012\u0004\u0012\u00020\u001e0=2\u0006\u0010A\u001a\u00020\u00112\u0006\u0010B\u001a\u00020\u00112\b\b\u0002\u0010C\u001a\u00020\u00112\n\b\u0002\u0010D\u001a\u0004\u0018\u00010\u0011H\u0086@\u00a2\u0006\u0004\bE\u0010FJ\u001e\u0010G\u001a\b\u0012\u0004\u0012\u00020\u001e0=2\u0006\u00106\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0004\bH\u0010%J\u0016\u0010I\u001a\b\u0012\u0004\u0012\u00020J0=H\u0086@\u00a2\u0006\u0004\bK\u0010*J\u000e\u0010L\u001a\u00020\u00142\u0006\u0010M\u001a\u00020NJ\b\u0010O\u001a\u0004\u0018\u00010\u0011J\b\u0010P\u001a\u0004\u0018\u00010\u0011J\u0006\u0010Q\u001a\u00020RJ\u0006\u0010S\u001a\u00020RJ\u000e\u0010T\u001a\u00020RH\u0086@\u00a2\u0006\u0002\u0010*J\u000e\u0010U\u001a\u00020RH\u0086@\u00a2\u0006\u0002\u0010*J\u000e\u0010V\u001a\u00020\u0014H\u0086@\u00a2\u0006\u0002\u0010*J\u0016\u0010W\u001a\u00020\u00142\u0006\u0010X\u001a\u00020\u0011H\u0082@\u00a2\u0006\u0002\u0010%J\u001e\u0010Y\u001a\b\u0012\u0004\u0012\u00020\u00140=2\u0006\u0010Z\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0004\b[\u0010%J\u0016\u0010\\\u001a\b\u0012\u0004\u0012\u00020]0=H\u0086@\u00a2\u0006\u0004\b^\u0010*J\u0016\u0010_\u001a\b\u0012\u0004\u0012\u00020`0=H\u0086@\u00a2\u0006\u0004\ba\u0010*J\u001e\u0010b\u001a\b\u0012\u0004\u0012\u00020\u00140=2\u0006\u0010c\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0004\bd\u0010%J\u0016\u0010e\u001a\b\u0012\u0004\u0012\u00020f0=H\u0086@\u00a2\u0006\u0004\bg\u0010*J\"\u0010h\u001a\b\u0012\u0004\u0012\u00020i0=2\n\b\u0002\u0010j\u001a\u0004\u0018\u00010\u0011H\u0086@\u00a2\u0006\u0004\bk\u0010%J&\u0010l\u001a\b\u0012\u0004\u0012\u00020m0=2\u0006\u0010n\u001a\u00020\u00112\u0006\u0010o\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0004\bp\u0010qJ\"\u0010r\u001a\b\u0012\u0004\u0012\u00020s0=2\n\b\u0002\u0010j\u001a\u0004\u0018\u00010\u0011H\u0086@\u00a2\u0006\u0004\bt\u0010%J\u0017\u0010u\u001a\u0004\u0018\u00010-2\u0006\u0010.\u001a\u00020\u0011H\u0002\u00a2\u0006\u0002\u0010/R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001b\u0010\n\u001a\u00020\u000b8FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000e\u0010\u000f\u001a\u0004\b\f\u0010\rR\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006w"}, d2 = {"Lcom/v2ray/ang/payment/data/repository/PaymentRepository;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "prefs", "Landroid/content/SharedPreferences;", "localRepository", "Lcom/v2ray/ang/payment/data/repository/LocalPaymentRepository;", "api", "Lcom/v2ray/ang/payment/data/api/PaymentApi;", "getApi", "()Lcom/v2ray/ang/payment/data/api/PaymentApi;", "api$delegate", "Lkotlin/Lazy;", "cachedDeviceId", "", "getLocalRepository", "saveCurrentUserId", "", "userId", "getCurrentUserId", "cacheUserInfo", "userInfo", "Lcom/v2ray/ang/payment/data/api/UserInfo;", "accessToken", "(Lcom/v2ray/ang/payment/data/api/UserInfo;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "cacheOrder", "order", "Lcom/v2ray/ang/payment/data/model/Order;", "(Lcom/v2ray/ang/payment/data/model/Order;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateOrderStatus", "(Lcom/v2ray/ang/payment/data/model/Order;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCachedOrders", "", "Lcom/v2ray/ang/payment/data/local/entity/OrderEntity;", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCachedPaymentHistory", "Lcom/v2ray/ang/payment/data/local/entity/PaymentHistoryEntity;", "getCachedCurrentUser", "Lcom/v2ray/ang/payment/data/local/entity/UserEntity;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "logout", "parseDate", "", "dateString", "(Ljava/lang/String;)Ljava/lang/Long;", "getDeviceId", "saveClientToken", "token", "expiresAt", "getClientToken", "saveCurrentOrderId", "orderId", "getCurrentOrderId", "clearCurrentOrder", "saveSubscription", "url", "username", "getPlans", "Lkotlin/Result;", "Lcom/v2ray/ang/payment/data/model/Plan;", "getPlans-IoAF18A", "createOrder", "planId", "assetCode", "purchaseType", "clientToken", "createOrder-yxL6bBk", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getOrder", "getOrder-gIAlu-s", "getSubscription", "Lcom/v2ray/ang/payment/data/api/CurrentSubscriptionData;", "getSubscription-IoAF18A", "saveAuthResponse", "authData", "Lcom/v2ray/ang/payment/data/model/AuthData;", "getAccessToken", "getRefreshToken", "isTokenExpired", "", "isTokenValid", "refreshTokenIfNeeded", "forceRefreshToken", "clearAuth", "updateCachedUserToken", "newAccessToken", "requestRegisterCode", "email", "requestRegisterCode-gIAlu-s", "getMe", "Lcom/v2ray/ang/payment/data/api/MeData;", "getMe-IoAF18A", "getReferralOverview", "Lcom/v2ray/ang/payment/data/api/ReferralOverviewData;", "getReferralOverview-IoAF18A", "bindReferralCode", "referralCode", "bindReferralCode-gIAlu-s", "getCommissionSummary", "Lcom/v2ray/ang/payment/data/api/CommissionSummaryData;", "getCommissionSummary-IoAF18A", "getCommissionLedger", "Lcom/v2ray/ang/payment/data/api/CommissionLedgerPageData;", "status", "getCommissionLedger-gIAlu-s", "createWithdrawal", "Lcom/v2ray/ang/payment/data/api/WithdrawalItem;", "amount", "payoutAddress", "createWithdrawal-0E7RQCE", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getWithdrawals", "Lcom/v2ray/ang/payment/data/api/WithdrawalPageData;", "getWithdrawals-gIAlu-s", "parseIsoDate", "Companion", "app_playstoreDebug"})
public final class PaymentRepository {
    @org.jetbrains.annotations.NotNull()
    private final android.content.SharedPreferences prefs = null;
    @org.jetbrains.annotations.NotNull()
    private final com.v2ray.ang.payment.data.repository.LocalPaymentRepository localRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy api$delegate = null;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String cachedDeviceId;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS_NAME = "payment_prefs";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_CURRENT_USER_ID = "current_user_id";
    @org.jetbrains.annotations.NotNull()
    private static final java.text.SimpleDateFormat dateFormat = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.text.SimpleDateFormat isoDateFormat = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.payment.data.repository.PaymentRepository.Companion Companion = null;
    
    public PaymentRepository(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.v2ray.ang.payment.data.api.PaymentApi getApi() {
        return null;
    }
    
    /**
     * 获取本地数据仓库
     */
    @org.jetbrains.annotations.NotNull()
    public final com.v2ray.ang.payment.data.repository.LocalPaymentRepository getLocalRepository() {
        return null;
    }
    
    /**
     * 缓存当前用户ID
     */
    public final void saveCurrentUserId(@org.jetbrains.annotations.NotNull()
    java.lang.String userId) {
    }
    
    /**
     * 获取当前用户ID
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCurrentUserId() {
        return null;
    }
    
    /**
     * 从API UserInfo缓存用户信息
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object cacheUserInfo(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.api.UserInfo userInfo, @org.jetbrains.annotations.NotNull()
    java.lang.String accessToken, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * 从订单数据缓存订单信息
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object cacheOrder(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.model.Order order, @org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * 更新订单状态（支付成功后）
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateOrderStatus(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.model.Order order, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * 获取本地缓存的订单列表
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getCachedOrders(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.v2ray.ang.payment.data.local.entity.OrderEntity>> $completion) {
        return null;
    }
    
    /**
     * 获取本地缓存的支付历史
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getCachedPaymentHistory(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.v2ray.ang.payment.data.local.entity.PaymentHistoryEntity>> $completion) {
        return null;
    }
    
    /**
     * 获取当前缓存的用户信息
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getCachedCurrentUser(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.v2ray.ang.payment.data.local.entity.UserEntity> $completion) {
        return null;
    }
    
    /**
     * 清除所有本地数据（退出登录）
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object logout(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Long parseDate(java.lang.String dateString) {
        return null;
    }
    
    /**
     * 获取设备唯一ID
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDeviceId() {
        return null;
    }
    
    /**
     * 保存客户端Token
     */
    public final void saveClientToken(@org.jetbrains.annotations.NotNull()
    java.lang.String token, @org.jetbrains.annotations.NotNull()
    java.lang.String expiresAt) {
    }
    
    /**
     * 获取客户端Token
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getClientToken() {
        return null;
    }
    
    /**
     * 保存当前订单ID
     */
    public final void saveCurrentOrderId(@org.jetbrains.annotations.NotNull()
    java.lang.String orderId) {
    }
    
    /**
     * 获取当前订单ID
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCurrentOrderId() {
        return null;
    }
    
    /**
     * 清除当前订单
     */
    public final void clearCurrentOrder() {
    }
    
    /**
     * 保存订阅信息
     */
    public final void saveSubscription(@org.jetbrains.annotations.NotNull()
    java.lang.String url, @org.jetbrains.annotations.NotNull()
    java.lang.String username) {
    }
    
    /**
     * 保存登录认证响应（包含 access_token 和 refresh_token）
     */
    public final void saveAuthResponse(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.model.AuthData authData) {
    }
    
    /**
     * 获取 Access Token
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getAccessToken() {
        return null;
    }
    
    /**
     * 获取 Refresh Token
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getRefreshToken() {
        return null;
    }
    
    /**
     * 检查 Token 是否过期
     * 在 Token 过期前 5 分钟认为即将过期
     */
    public final boolean isTokenExpired() {
        return false;
    }
    
    /**
     * 检查 Token 是否有效（未过期）
     */
    public final boolean isTokenValid() {
        return false;
    }
    
    /**
     * 自动刷新 Token（如果需要）
     * @return true 表示 Token 有效（无需刷新或刷新成功），false 表示刷新失败
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object refreshTokenIfNeeded(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    /**
     * 强制刷新 Token
     * @return true 表示刷新成功，false 表示刷新失败
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object forceRefreshToken(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    /**
     * 清除认证信息（退出登录）
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object clearAuth(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * 更新缓存用户的 Token
     */
    private final java.lang.Object updateCachedUserToken(java.lang.String newAccessToken, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * 解析 ISO 8601 日期字符串
     */
    private final java.lang.Long parseIsoDate(java.lang.String dateString) {
        return null;
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"Lcom/v2ray/ang/payment/data/repository/PaymentRepository$Companion;", "", "<init>", "()V", "PREFS_NAME", "", "KEY_CURRENT_USER_ID", "dateFormat", "Ljava/text/SimpleDateFormat;", "isoDateFormat", "app_playstoreDebug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}