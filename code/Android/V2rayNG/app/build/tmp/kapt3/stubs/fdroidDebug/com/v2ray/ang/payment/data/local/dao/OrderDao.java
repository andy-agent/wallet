package com.v2ray.ang.payment.data.local.dao;

/**
 * 订单数据访问对象
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0006\n\u0002\u0010\t\n\u0002\b\b\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0018\u0010\b\u001a\u0004\u0018\u00010\u00052\u0006\u0010\t\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u000bJ\u001c\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00050\r2\u0006\u0010\u000e\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u000bJ$\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00050\r2\u0006\u0010\u000e\u001a\u00020\n2\u0006\u0010\u0010\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u0011J&\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00050\r2\u0006\u0010\u000e\u001a\u00020\n2\b\b\u0002\u0010\u0013\u001a\u00020\u0014H\u00a7@\u00a2\u0006\u0002\u0010\u0015J.\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00050\r2\u0006\u0010\u000e\u001a\u00020\n2\b\b\u0002\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0017\u001a\u00020\u0014H\u00a7@\u00a2\u0006\u0002\u0010\u0018J\u0016\u0010\u0019\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u001a\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u000bJ\u0016\u0010\u001b\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u000b\u00a8\u0006\u001c\u00c0\u0006\u0003"}, d2 = {"Lcom/v2ray/ang/payment/data/local/dao/OrderDao;", "", "insert", "", "order", "Lcom/v2ray/ang/payment/data/local/entity/OrderEntity;", "(Lcom/v2ray/ang/payment/data/local/entity/OrderEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "update", "getByOrderNo", "orderNo", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllByUserId", "", "userId", "getByUserIdAndStatus", "status", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getActiveOrders", "currentTime", "", "(Ljava/lang/String;JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getExpiringOrders", "threshold", "(Ljava/lang/String;JJLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "delete", "deleteByOrderNo", "deleteByUserId", "app_fdroidDebug"})
@androidx.room.Dao()
public abstract interface OrderDao {
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.local.entity.OrderEntity order, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.local.entity.OrderEntity order, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM orders WHERE orderNo = :orderNo LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getByOrderNo(@org.jetbrains.annotations.NotNull()
    java.lang.String orderNo, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.v2ray.ang.payment.data.local.entity.OrderEntity> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAllByUserId(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.v2ray.ang.payment.data.local.entity.OrderEntity>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM orders WHERE userId = :userId AND status = :status ORDER BY createdAt DESC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getByUserIdAndStatus(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    java.lang.String status, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.v2ray.ang.payment.data.local.entity.OrderEntity>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM orders WHERE userId = :userId AND expiredAt > :currentTime ORDER BY expiredAt ASC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getActiveOrders(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, long currentTime, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.v2ray.ang.payment.data.local.entity.OrderEntity>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM orders WHERE userId = :userId AND expiredAt > :currentTime AND expiredAt <= :threshold ORDER BY expiredAt ASC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getExpiringOrders(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, long currentTime, long threshold, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.v2ray.ang.payment.data.local.entity.OrderEntity>> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.local.entity.OrderEntity order, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM orders WHERE orderNo = :orderNo")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteByOrderNo(@org.jetbrains.annotations.NotNull()
    java.lang.String orderNo, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM orders WHERE userId = :userId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteByUserId(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * 订单数据访问对象
     */
    @kotlin.Metadata(mv = {2, 2, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}