package com.v2ray.ang.payment.ui;

/**
 * 订单轮询用例
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\u0018\u00002\u00020\u0001:\u0001!B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0006\u0010\u0007J\n\u0010\u000b\u001a\u0004\u0018\u00010\u0005H\u0002J\u000e\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001bJ\u0006\u0010\u001c\u001a\u00020\u0019J\u0006\u0010\u001d\u001a\u00020\u0017J\u0010\u0010\u001e\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001bH\u0002J\u0010\u0010\u001f\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001bH\u0002J\u000e\u0010 \u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010\b\u001a\u0010\u0012\f\u0012\n \n*\u0004\u0018\u00010\u00050\u00050\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\""}, d2 = {"Lcom/v2ray/ang/payment/ui/OrderPollingUseCase;", "", "repository", "Lcom/v2ray/ang/payment/data/repository/PaymentRepository;", "callback", "Lcom/v2ray/ang/payment/ui/OrderPollingUseCase$PollingCallback;", "<init>", "(Lcom/v2ray/ang/payment/data/repository/PaymentRepository;Lcom/v2ray/ang/payment/ui/OrderPollingUseCase$PollingCallback;)V", "callbackRef", "Ljava/lang/ref/WeakReference;", "kotlin.jvm.PlatformType", "getCallback", "coroutineScope", "Lkotlinx/coroutines/CoroutineScope;", "pollingJob", "Lkotlinx/coroutines/Job;", "handler", "Landroid/os/Handler;", "currentIntervalIndex", "", "startTime", "", "isPolling", "", "startPolling", "", "orderId", "", "stopPolling", "isActive", "scheduleNextPoll", "pollOrder", "pollImmediately", "PollingCallback", "app_fdroidDebug"})
public final class OrderPollingUseCase {
    @org.jetbrains.annotations.NotNull()
    private final com.v2ray.ang.payment.data.repository.PaymentRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.ref.WeakReference<com.v2ray.ang.payment.ui.OrderPollingUseCase.PollingCallback> callbackRef = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope coroutineScope = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job pollingJob;
    @org.jetbrains.annotations.NotNull()
    private final android.os.Handler handler = null;
    private int currentIntervalIndex = 0;
    private long startTime = 0L;
    private boolean isPolling = false;
    
    public OrderPollingUseCase(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.repository.PaymentRepository repository, @org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.ui.OrderPollingUseCase.PollingCallback callback) {
        super();
    }
    
    private final com.v2ray.ang.payment.ui.OrderPollingUseCase.PollingCallback getCallback() {
        return null;
    }
    
    /**
     * 开始轮询
     */
    public final void startPolling(@org.jetbrains.annotations.NotNull()
    java.lang.String orderId) {
    }
    
    /**
     * 停止轮询
     */
    public final void stopPolling() {
    }
    
    /**
     * 是否正在轮询
     */
    public final boolean isActive() {
        return false;
    }
    
    private final void scheduleNextPoll(java.lang.String orderId) {
    }
    
    private final void pollOrder(java.lang.String orderId) {
    }
    
    /**
     * 立即查询一次（用于手动刷新）
     */
    public final void pollImmediately(@org.jetbrains.annotations.NotNull()
    java.lang.String orderId) {
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\u0010\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\u0010\u0010\u0007\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\tH&J\b\u0010\n\u001a\u00020\u0003H&J\u0010\u0010\u000b\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\tH&\u00a8\u0006\f\u00c0\u0006\u0003"}, d2 = {"Lcom/v2ray/ang/payment/ui/OrderPollingUseCase$PollingCallback;", "", "onStatusUpdate", "", "order", "Lcom/v2ray/ang/payment/data/model/Order;", "onPaymentSuccess", "onPaymentFailed", "error", "", "onExpired", "onError", "app_fdroidDebug"})
    public static abstract interface PollingCallback {
        
        public abstract void onStatusUpdate(@org.jetbrains.annotations.NotNull()
        com.v2ray.ang.payment.data.model.Order order);
        
        public abstract void onPaymentSuccess(@org.jetbrains.annotations.NotNull()
        com.v2ray.ang.payment.data.model.Order order);
        
        public abstract void onPaymentFailed(@org.jetbrains.annotations.NotNull()
        java.lang.String error);
        
        public abstract void onExpired();
        
        public abstract void onError(@org.jetbrains.annotations.NotNull()
        java.lang.String error);
    }
}