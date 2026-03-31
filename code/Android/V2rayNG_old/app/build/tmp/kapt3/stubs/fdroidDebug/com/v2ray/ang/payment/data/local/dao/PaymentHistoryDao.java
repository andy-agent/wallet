package com.v2ray.ang.payment.data.local.dao;

/**
 * 支付历史数据访问对象
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u001c\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00050\b2\u0006\u0010\t\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u000bJ\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00050\bH\u00a7@\u00a2\u0006\u0002\u0010\rJ\u001c\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00050\b2\u0006\u0010\u000f\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u000bJ\u0016\u0010\u0010\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0011\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u000bJ\u000e\u0010\u0012\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\r\u00a8\u0006\u0013\u00c0\u0006\u0003"}, d2 = {"Lcom/v2ray/ang/payment/data/local/dao/PaymentHistoryDao;", "", "insert", "", "paymentHistory", "Lcom/v2ray/ang/payment/data/local/entity/PaymentHistoryEntity;", "(Lcom/v2ray/ang/payment/data/local/entity/PaymentHistoryEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getByOrderNo", "", "orderNo", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAll", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllByUserId", "userId", "delete", "deleteByOrderNo", "deleteAll", "app_fdroidDebug"})
@androidx.room.Dao()
public abstract interface PaymentHistoryDao {
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.local.entity.PaymentHistoryEntity paymentHistory, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM payment_history WHERE orderNo = :orderNo ORDER BY paidAt DESC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getByOrderNo(@org.jetbrains.annotations.NotNull()
    java.lang.String orderNo, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.v2ray.ang.payment.data.local.entity.PaymentHistoryEntity>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM payment_history ORDER BY paidAt DESC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.v2ray.ang.payment.data.local.entity.PaymentHistoryEntity>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM payment_history WHERE orderNo IN (SELECT orderNo FROM orders WHERE userId = :userId) ORDER BY paidAt DESC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAllByUserId(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.v2ray.ang.payment.data.local.entity.PaymentHistoryEntity>> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.local.entity.PaymentHistoryEntity paymentHistory, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM payment_history WHERE orderNo = :orderNo")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteByOrderNo(@org.jetbrains.annotations.NotNull()
    java.lang.String orderNo, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM payment_history")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}