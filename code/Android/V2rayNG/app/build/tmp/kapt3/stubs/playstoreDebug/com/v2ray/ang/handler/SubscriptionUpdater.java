package com.v2ray.ang.handler;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\u0004B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0005"}, d2 = {"Lcom/v2ray/ang/handler/SubscriptionUpdater;", "", "<init>", "()V", "UpdateTask", "app_playstoreDebug"})
public final class SubscriptionUpdater {
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.handler.SubscriptionUpdater INSTANCE = null;
    
    private SubscriptionUpdater() {
        super();
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u000e\u0010\r\u001a\u00020\u000eH\u0097@\u00a2\u0006\u0002\u0010\u000fR\u0013\u0010\b\u001a\u00070\t\u00a2\u0006\u0002\b\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0013\u0010\u000b\u001a\u00070\f\u00a2\u0006\u0002\b\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0010"}, d2 = {"Lcom/v2ray/ang/handler/SubscriptionUpdater$UpdateTask;", "Landroidx/work/CoroutineWorker;", "context", "Landroid/content/Context;", "params", "Landroidx/work/WorkerParameters;", "<init>", "(Landroid/content/Context;Landroidx/work/WorkerParameters;)V", "notificationManager", "Landroidx/core/app/NotificationManagerCompat;", "Lorg/jspecify/annotations/NonNull;", "notification", "Landroidx/core/app/NotificationCompat$Builder;", "doWork", "Landroidx/work/ListenableWorker$Result;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_playstoreDebug"})
    public static final class UpdateTask extends androidx.work.CoroutineWorker {
        @org.jetbrains.annotations.NotNull()
        private final androidx.core.app.NotificationManagerCompat notificationManager = null;
        @org.jetbrains.annotations.NotNull()
        private final androidx.core.app.NotificationCompat.Builder notification = null;
        
        public UpdateTask(@org.jetbrains.annotations.NotNull()
        android.content.Context context, @org.jetbrains.annotations.NotNull()
        androidx.work.WorkerParameters params) {
            super(null, null);
        }
        
        /**
         * Performs the subscription update work.
         * @return The result of the work.
         */
        @java.lang.Override()
        @android.annotation.SuppressLint(value = {"MissingPermission"})
        @org.jetbrains.annotations.Nullable()
        public java.lang.Object doWork(@org.jetbrains.annotations.NotNull()
        kotlin.coroutines.Continuation<? super androidx.work.ListenableWorker.Result> $completion) {
            return null;
        }
    }
}