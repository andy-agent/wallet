package com.v2ray.ang.ui;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u0016J\b\u0010\u0011\u001a\u00020\u000eH\u0002J\u0010\u0010\u0012\u001a\u00020\u000e2\u0006\u0010\u0013\u001a\u00020\u0014H\u0002J\u0010\u0010\u0015\u001a\u00020\u000e2\u0006\u0010\u0016\u001a\u00020\u0017H\u0002J\u0010\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001bH\u0016J\u0010\u0010\u001c\u001a\u00020\u00192\u0006\u0010\u001d\u001a\u00020\u001eH\u0016J\b\u0010\u001f\u001a\u00020\u000eH\u0002R\u001b\u0010\u0004\u001a\u00020\u00058BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006 "}, d2 = {"Lcom/v2ray/ang/ui/ScannerActivity;", "Lcom/v2ray/ang/ui/HelperBaseActivity;", "<init>", "()V", "binding", "Lcom/v2ray/ang/databinding/ActivityNoneBinding;", "getBinding", "()Lcom/v2ray/ang/databinding/ActivityNoneBinding;", "binding$delegate", "Lkotlin/Lazy;", "scanQrCode", "Landroidx/activity/result/ActivityResultLauncher;", "Lio/github/g00fy2/quickie/config/ScannerConfig;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "launchScan", "handleResult", "result", "Lio/github/g00fy2/quickie/QRResult;", "finished", "text", "", "onCreateOptionsMenu", "", "menu", "Landroid/view/Menu;", "onOptionsItemSelected", "item", "Landroid/view/MenuItem;", "showFileChooser", "app_fdroidDebug"})
public final class ScannerActivity extends com.v2ray.ang.ui.HelperBaseActivity {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy binding$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<io.github.g00fy2.quickie.config.ScannerConfig> scanQrCode = null;
    
    public ScannerActivity() {
        super();
    }
    
    private final com.v2ray.ang.databinding.ActivityNoneBinding getBinding() {
        return null;
    }
    
    @java.lang.Override()
    public void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void launchScan() {
    }
    
    private final void handleResult(io.github.g00fy2.quickie.QRResult result) {
    }
    
    private final void finished(java.lang.String text) {
    }
    
    @java.lang.Override()
    public boolean onCreateOptionsMenu(@org.jetbrains.annotations.NotNull()
    android.view.Menu menu) {
        return false;
    }
    
    @java.lang.Override()
    public boolean onOptionsItemSelected(@org.jetbrains.annotations.NotNull()
    android.view.MenuItem item) {
        return false;
    }
    
    private final void showFileChooser() {
    }
}