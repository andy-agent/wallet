package com.v2ray.ang.plans;

/**
 * 支付页面
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000l\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\n\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u00012\u00020\u0002B\u0007\u00a2\u0006\u0004\b\u0003\u0010\u0004J\u0012\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H\u0014J\u0018\u0010\u0016\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u0018H\u0002J\u0018\u0010\u001a\u001a\u00020\u00132\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001cH\u0002J\u0010\u0010\u001e\u001a\u00020\u00132\u0006\u0010\u001f\u001a\u00020\fH\u0002J\u0010\u0010 \u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001cH\u0002J\u0018\u0010!\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001c2\u0006\u0010\"\u001a\u00020\u001cH\u0002J\u0010\u0010#\u001a\u00020\u00132\u0006\u0010\u001f\u001a\u00020\fH\u0002J\u0010\u0010$\u001a\u00020\u00132\u0006\u0010\u001f\u001a\u00020\fH\u0002J\u0012\u0010%\u001a\u0004\u0018\u00010&2\u0006\u0010\'\u001a\u00020\u001cH\u0002J\u0018\u0010(\u001a\u00020\u00132\u0006\u0010)\u001a\u00020\u001c2\u0006\u0010*\u001a\u00020\u001cH\u0002J\u0010\u0010+\u001a\u00020,2\u0006\u0010-\u001a\u00020\u001cH\u0002J\u0010\u0010.\u001a\u00020\u00132\u0006\u0010\u001f\u001a\u00020\fH\u0016J\u0010\u0010/\u001a\u00020\u00132\u0006\u0010\u001f\u001a\u00020\fH\u0016J\u0010\u00100\u001a\u00020\u00132\u0006\u00101\u001a\u00020\u001cH\u0016J\b\u00102\u001a\u00020\u0013H\u0016J\u0010\u00103\u001a\u00020\u00132\u0006\u00101\u001a\u00020\u001cH\u0016J\b\u00104\u001a\u00020\u0013H\u0014J\u0010\u00105\u001a\u00020\u00182\u0006\u00106\u001a\u000207H\u0016R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u00068"}, d2 = {"Lcom/v2ray/ang/plans/PaymentActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "Lcom/v2ray/ang/payment/ui/OrderPollingUseCase$PollingCallback;", "<init>", "()V", "binding", "Lcom/v2ray/ang/databinding/ActivityPaymentBinding;", "repository", "Lcom/v2ray/ang/payment/data/repository/PaymentRepository;", "pollingUseCase", "Lcom/v2ray/ang/payment/ui/OrderPollingUseCase;", "currentOrder", "Lcom/v2ray/ang/payment/data/model/Order;", "countDownTimer", "Landroid/os/CountDownTimer;", "loginLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "Landroid/content/Intent;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "setupPaymentMethodSelection", "supportsSol", "", "supportsUsdt", "createOrder", "planId", "", "assetCode", "displayOrder", "order", "getPaymentMethodDisplay", "formatAmountDisplay", "amountCrypto", "startCountdown", "startPolling", "generateQRCode", "Landroid/graphics/Bitmap;", "content", "copyToClipboard", "label", "text", "parseIsoDate", "", "dateStr", "onStatusUpdate", "onPaymentSuccess", "onPaymentFailed", "error", "onExpired", "onError", "onDestroy", "onOptionsItemSelected", "item", "Landroid/view/MenuItem;", "app_playstoreDebug"})
public final class PaymentActivity extends androidx.appcompat.app.AppCompatActivity implements com.v2ray.ang.payment.ui.OrderPollingUseCase.PollingCallback {
    private com.v2ray.ang.databinding.ActivityPaymentBinding binding;
    private com.v2ray.ang.payment.data.repository.PaymentRepository repository;
    private com.v2ray.ang.payment.ui.OrderPollingUseCase pollingUseCase;
    @org.jetbrains.annotations.Nullable()
    private com.v2ray.ang.payment.data.model.Order currentOrder;
    @org.jetbrains.annotations.Nullable()
    private android.os.CountDownTimer countDownTimer;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<android.content.Intent> loginLauncher = null;
    
    public PaymentActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setupPaymentMethodSelection(boolean supportsSol, boolean supportsUsdt) {
    }
    
    private final void createOrder(java.lang.String planId, java.lang.String assetCode) {
    }
    
    private final void displayOrder(com.v2ray.ang.payment.data.model.Order order) {
    }
    
    private final java.lang.String getPaymentMethodDisplay(java.lang.String assetCode) {
        return null;
    }
    
    private final java.lang.String formatAmountDisplay(java.lang.String assetCode, java.lang.String amountCrypto) {
        return null;
    }
    
    private final void startCountdown(com.v2ray.ang.payment.data.model.Order order) {
    }
    
    private final void startPolling(com.v2ray.ang.payment.data.model.Order order) {
    }
    
    private final android.graphics.Bitmap generateQRCode(java.lang.String content) {
        return null;
    }
    
    private final void copyToClipboard(java.lang.String label, java.lang.String text) {
    }
    
    private final long parseIsoDate(java.lang.String dateStr) {
        return 0L;
    }
    
    @java.lang.Override()
    public void onStatusUpdate(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.model.Order order) {
    }
    
    @java.lang.Override()
    public void onPaymentSuccess(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.model.Order order) {
    }
    
    @java.lang.Override()
    public void onPaymentFailed(@org.jetbrains.annotations.NotNull()
    java.lang.String error) {
    }
    
    @java.lang.Override()
    public void onExpired() {
    }
    
    @java.lang.Override()
    public void onError(@org.jetbrains.annotations.NotNull()
    java.lang.String error) {
    }
    
    @java.lang.Override()
    protected void onDestroy() {
    }
    
    @java.lang.Override()
    public boolean onOptionsItemSelected(@org.jetbrains.annotations.NotNull()
    android.view.MenuItem item) {
        return false;
    }
}