package com.v2ray.ang.payment.data.local.database;

/**
 * Room数据库 - 支付模块
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\'\u0018\u0000 \n2\u00020\u0001:\u0001\nB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0006\u001a\u00020\u0007H&J\b\u0010\b\u001a\u00020\tH&\u00a8\u0006\u000b"}, d2 = {"Lcom/v2ray/ang/payment/data/local/database/PaymentDatabase;", "Landroidx/room/RoomDatabase;", "<init>", "()V", "userDao", "Lcom/v2ray/ang/payment/data/local/dao/UserDao;", "orderDao", "Lcom/v2ray/ang/payment/data/local/dao/OrderDao;", "paymentHistoryDao", "Lcom/v2ray/ang/payment/data/local/dao/PaymentHistoryDao;", "Companion", "app_playstoreDebug"})
@androidx.room.Database(entities = {com.v2ray.ang.payment.data.local.entity.UserEntity.class, com.v2ray.ang.payment.data.local.entity.OrderEntity.class, com.v2ray.ang.payment.data.local.entity.PaymentHistoryEntity.class}, version = 1, exportSchema = false)
public abstract class PaymentDatabase extends androidx.room.RoomDatabase {
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.v2ray.ang.payment.data.local.database.PaymentDatabase INSTANCE;
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.payment.data.local.database.PaymentDatabase.Companion Companion = null;
    
    public PaymentDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.v2ray.ang.payment.data.local.dao.UserDao userDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.v2ray.ang.payment.data.local.dao.OrderDao orderDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.v2ray.ang.payment.data.local.dao.PaymentHistoryDao paymentHistoryDao();
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\bR\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/v2ray/ang/payment/data/local/database/PaymentDatabase$Companion;", "", "<init>", "()V", "INSTANCE", "Lcom/v2ray/ang/payment/data/local/database/PaymentDatabase;", "getDatabase", "context", "Landroid/content/Context;", "app_playstoreDebug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.v2ray.ang.payment.data.local.database.PaymentDatabase getDatabase(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
    }
}