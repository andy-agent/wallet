package com.v2ray.ang.payment.service;

/**
 * 订阅到期提醒Worker
 * 定期检查即将到期的订阅并发送通知
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\u0018\u0000 \u00172\u00020\u0001:\u0001\u0017B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u000e\u0010\n\u001a\u00020\u000bH\u0096@\u00a2\u0006\u0002\u0010\fJ\u000e\u0010\r\u001a\u00020\u000eH\u0082@\u00a2\u0006\u0002\u0010\fJ(\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0015\u001a\u00020\u0011H\u0002J\b\u0010\u0016\u001a\u00020\u000eH\u0002R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2 = {"Lcom/v2ray/ang/payment/service/SubscriptionReminderWorker;", "Landroidx/work/CoroutineWorker;", "context", "Landroid/content/Context;", "params", "Landroidx/work/WorkerParameters;", "<init>", "(Landroid/content/Context;Landroidx/work/WorkerParameters;)V", "localRepository", "Lcom/v2ray/ang/payment/data/repository/LocalPaymentRepository;", "doWork", "Landroidx/work/ListenableWorker$Result;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "checkAndNotifyExpiringSubscriptions", "", "sendNotification", "notificationId", "", "title", "", "content", "priority", "createNotificationChannel", "Companion", "app_fdroidDebug"})
public final class SubscriptionReminderWorker extends androidx.work.CoroutineWorker {
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String WORK_NAME = "subscription_reminder_work";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String CHANNEL_ID = "subscription_reminder_channel";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String CHANNEL_NAME = "\u8ba2\u9605\u5230\u671f\u63d0\u9192";
    public static final int NOTIFICATION_ID_3_DAYS = 1001;
    public static final int NOTIFICATION_ID_1_DAY = 1002;
    public static final int NOTIFICATION_ID_EXPIRED = 1003;
    public static final long REMIND_3_DAYS = 259200000L;
    public static final long REMIND_1_DAY = 86400000L;
    @org.jetbrains.annotations.NotNull()
    private final com.v2ray.ang.payment.data.repository.LocalPaymentRepository localRepository = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.payment.service.SubscriptionReminderWorker.Companion Companion = null;
    
    public SubscriptionReminderWorker(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    androidx.work.WorkerParameters params) {
        super(null, null);
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object doWork(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super androidx.work.ListenableWorker.Result> $completion) {
        return null;
    }
    
    /**
     * 检查即将到期的订阅并发送通知
     */
    private final java.lang.Object checkAndNotifyExpiringSubscriptions(kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * 发送通知
     */
    private final void sendNotification(int notificationId, java.lang.String title, java.lang.String content, int priority) {
    }
    
    /**
     * 创建通知渠道（Android 8.0+）
     */
    private final void createNotificationChannel() {
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012J\u000e\u0010\u0013\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\tX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\tX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\rX\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/v2ray/ang/payment/service/SubscriptionReminderWorker$Companion;", "", "<init>", "()V", "WORK_NAME", "", "CHANNEL_ID", "CHANNEL_NAME", "NOTIFICATION_ID_3_DAYS", "", "NOTIFICATION_ID_1_DAY", "NOTIFICATION_ID_EXPIRED", "REMIND_3_DAYS", "", "REMIND_1_DAY", "startReminderWork", "", "context", "Landroid/content/Context;", "stopReminderWork", "app_fdroidDebug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        /**
         * 启动定期提醒任务
         */
        public final void startReminderWork(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
        }
        
        /**
         * 停止提醒任务
         */
        public final void stopReminderWork(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
        }
    }
}