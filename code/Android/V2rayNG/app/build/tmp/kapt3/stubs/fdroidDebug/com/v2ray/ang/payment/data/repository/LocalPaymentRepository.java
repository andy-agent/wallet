package com.v2ray.ang.payment.data.repository;

/**
 * 本地支付数据仓库
 * 封装所有Room数据库操作
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u000e\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0016\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0002\u0010\u0012J\u0016\u0010\u0013\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0002\u0010\u0012J\u0018\u0010\u0014\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u0015\u001a\u00020\u0016H\u0086@\u00a2\u0006\u0002\u0010\u0017J\u0018\u0010\u0018\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u0019\u001a\u00020\u0016H\u0086@\u00a2\u0006\u0002\u0010\u0017J\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u0011H\u0086@\u00a2\u0006\u0002\u0010\u001bJ\u0016\u0010\u001c\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0002\u0010\u0012J\u000e\u0010\u001d\u001a\u00020\u000fH\u0086@\u00a2\u0006\u0002\u0010\u001bJ\u0016\u0010\u001e\u001a\u00020\u000f2\u0006\u0010\u001f\u001a\u00020 H\u0086@\u00a2\u0006\u0002\u0010!J\u0016\u0010\"\u001a\u00020\u000f2\u0006\u0010\u001f\u001a\u00020 H\u0086@\u00a2\u0006\u0002\u0010!J\u0018\u0010#\u001a\u0004\u0018\u00010 2\u0006\u0010$\u001a\u00020\u0016H\u0086@\u00a2\u0006\u0002\u0010\u0017J\u001c\u0010%\u001a\b\u0012\u0004\u0012\u00020 0&2\u0006\u0010\u0019\u001a\u00020\u0016H\u0086@\u00a2\u0006\u0002\u0010\u0017J\u001c\u0010\'\u001a\b\u0012\u0004\u0012\u00020 0&2\u0006\u0010\u0019\u001a\u00020\u0016H\u0086@\u00a2\u0006\u0002\u0010\u0017J$\u0010(\u001a\b\u0012\u0004\u0012\u00020 0&2\u0006\u0010\u0019\u001a\u00020\u00162\u0006\u0010)\u001a\u00020*H\u0086@\u00a2\u0006\u0002\u0010+J\u0016\u0010,\u001a\u00020\u000f2\u0006\u0010$\u001a\u00020\u0016H\u0086@\u00a2\u0006\u0002\u0010\u0017J\u0016\u0010-\u001a\u00020\u000f2\u0006\u0010\u0019\u001a\u00020\u0016H\u0086@\u00a2\u0006\u0002\u0010\u0017J\u0016\u0010.\u001a\u00020\u000f2\u0006\u0010/\u001a\u000200H\u0086@\u00a2\u0006\u0002\u00101J\u001c\u00102\u001a\b\u0012\u0004\u0012\u0002000&2\u0006\u0010$\u001a\u00020\u0016H\u0086@\u00a2\u0006\u0002\u0010\u0017J\u0014\u00103\u001a\b\u0012\u0004\u0012\u0002000&H\u0086@\u00a2\u0006\u0002\u0010\u001bJ\u001c\u00104\u001a\b\u0012\u0004\u0012\u0002000&2\u0006\u0010\u0019\u001a\u00020\u0016H\u0086@\u00a2\u0006\u0002\u0010\u0017J\u0016\u00105\u001a\u00020\u000f2\u0006\u0010$\u001a\u00020\u0016H\u0086@\u00a2\u0006\u0002\u0010\u0017J\u000e\u00106\u001a\u00020\u000fH\u0086@\u00a2\u0006\u0002\u0010\u001bJ\u000e\u00107\u001a\u00020\u000fH\u0086@\u00a2\u0006\u0002\u0010\u001bJ$\u00108\u001a\u00020\u000f2\u0006\u0010\u0019\u001a\u00020\u00162\f\u00109\u001a\b\u0012\u0004\u0012\u00020 0&H\u0086@\u00a2\u0006\u0002\u0010:J\u001c\u0010;\u001a\u00020\u000f2\f\u0010<\u001a\b\u0012\u0004\u0012\u0002000&H\u0086@\u00a2\u0006\u0002\u0010=R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006>"}, d2 = {"Lcom/v2ray/ang/payment/data/repository/LocalPaymentRepository;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "database", "Lcom/v2ray/ang/payment/data/local/database/PaymentDatabase;", "userDao", "Lcom/v2ray/ang/payment/data/local/dao/UserDao;", "orderDao", "Lcom/v2ray/ang/payment/data/local/dao/OrderDao;", "paymentHistoryDao", "Lcom/v2ray/ang/payment/data/local/dao/PaymentHistoryDao;", "saveUser", "", "user", "Lcom/v2ray/ang/payment/data/local/entity/UserEntity;", "(Lcom/v2ray/ang/payment/data/local/entity/UserEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateUser", "getUserByUsername", "username", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getUserById", "userId", "getCurrentUser", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteUser", "clearAllUsers", "saveOrder", "order", "Lcom/v2ray/ang/payment/data/local/entity/OrderEntity;", "(Lcom/v2ray/ang/payment/data/local/entity/OrderEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateOrder", "getOrderByOrderNo", "orderNo", "getOrdersByUserId", "", "getActiveOrders", "getExpiringOrders", "threshold", "", "(Ljava/lang/String;JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteOrderByOrderNo", "deleteOrdersByUserId", "savePaymentHistory", "paymentHistory", "Lcom/v2ray/ang/payment/data/local/entity/PaymentHistoryEntity;", "(Lcom/v2ray/ang/payment/data/local/entity/PaymentHistoryEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getPaymentHistoryByOrderNo", "getAllPaymentHistory", "getPaymentHistoryByUserId", "deletePaymentHistoryByOrderNo", "clearAllPaymentHistory", "clearAllData", "syncOrders", "orders", "(Ljava/lang/String;Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "syncPaymentHistory", "paymentHistories", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_fdroidDebug"})
public final class LocalPaymentRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.v2ray.ang.payment.data.local.database.PaymentDatabase database = null;
    @org.jetbrains.annotations.NotNull()
    private final com.v2ray.ang.payment.data.local.dao.UserDao userDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.v2ray.ang.payment.data.local.dao.OrderDao orderDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.v2ray.ang.payment.data.local.dao.PaymentHistoryDao paymentHistoryDao = null;
    
    public LocalPaymentRepository(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object saveUser(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.local.entity.UserEntity user, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateUser(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.local.entity.UserEntity user, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getUserByUsername(@org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.v2ray.ang.payment.data.local.entity.UserEntity> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getUserById(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.v2ray.ang.payment.data.local.entity.UserEntity> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getCurrentUser(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.v2ray.ang.payment.data.local.entity.UserEntity> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteUser(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.local.entity.UserEntity user, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object clearAllUsers(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object saveOrder(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.local.entity.OrderEntity order, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateOrder(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.local.entity.OrderEntity order, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getOrderByOrderNo(@org.jetbrains.annotations.NotNull()
    java.lang.String orderNo, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.v2ray.ang.payment.data.local.entity.OrderEntity> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getOrdersByUserId(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.v2ray.ang.payment.data.local.entity.OrderEntity>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getActiveOrders(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.v2ray.ang.payment.data.local.entity.OrderEntity>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getExpiringOrders(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, long threshold, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.v2ray.ang.payment.data.local.entity.OrderEntity>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteOrderByOrderNo(@org.jetbrains.annotations.NotNull()
    java.lang.String orderNo, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteOrdersByUserId(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object savePaymentHistory(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.local.entity.PaymentHistoryEntity paymentHistory, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getPaymentHistoryByOrderNo(@org.jetbrains.annotations.NotNull()
    java.lang.String orderNo, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.v2ray.ang.payment.data.local.entity.PaymentHistoryEntity>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAllPaymentHistory(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.v2ray.ang.payment.data.local.entity.PaymentHistoryEntity>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getPaymentHistoryByUserId(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.v2ray.ang.payment.data.local.entity.PaymentHistoryEntity>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deletePaymentHistoryByOrderNo(@org.jetbrains.annotations.NotNull()
    java.lang.String orderNo, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object clearAllPaymentHistory(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * 清除所有本地数据（退出登录时调用）
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object clearAllData(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * 同步订单列表到本地
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object syncOrders(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    java.util.List<com.v2ray.ang.payment.data.local.entity.OrderEntity> orders, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * 同步支付历史到本地
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object syncPaymentHistory(@org.jetbrains.annotations.NotNull()
    java.util.List<com.v2ray.ang.payment.data.local.entity.PaymentHistoryEntity> paymentHistories, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}