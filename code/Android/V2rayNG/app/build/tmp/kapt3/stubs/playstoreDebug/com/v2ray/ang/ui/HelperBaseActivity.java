package com.v2ray.ang.ui;

/**
 * HelperBaseActivity extends BaseActivity and provides additional helpers for
 * activities that need file chooser, permission requesting, or QR code scanning functionality.
 *
 * Activities that don't need these features should extend BaseActivity directly.
 * Activities that need file selection, permissions, or QR code scanning should extend this class.
 *
 * Additional Responsibilities:
 * - Provide file chooser helpers for selecting and creating files.
 * - Provide permission request helpers with callbacks.
 * - Provide QR code scanning helpers with camera permission handling.
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b&\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u0014J\u001e\u0010\u000e\u001a\u00020\u000b2\u0006\u0010\u000f\u001a\u00020\u00102\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0012H\u0004J(\u0010\u0013\u001a\u00020\u000b2\b\b\u0002\u0010\u0014\u001a\u00020\u00152\u0014\u0010\u0016\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010\u0018\u0012\u0004\u0012\u00020\u000b0\u0017H\u0004J&\u0010\u0019\u001a\u00020\u000b2\u0006\u0010\u001a\u001a\u00020\u00152\u0014\u0010\u0016\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010\u0018\u0012\u0004\u0012\u00020\u000b0\u0017H\u0004J\u001e\u0010\u001b\u001a\u00020\u000b2\u0014\u0010\u0016\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010\u0015\u0012\u0004\u0012\u00020\u000b0\u0017H\u0004R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001c"}, d2 = {"Lcom/v2ray/ang/ui/HelperBaseActivity;", "Lcom/v2ray/ang/ui/BaseActivity;", "<init>", "()V", "fileChooser", "Lcom/v2ray/ang/helper/FileChooserHelper;", "permissionRequester", "Lcom/v2ray/ang/helper/PermissionHelper;", "qrCodeScanner", "Lcom/v2ray/ang/helper/QRCodeScannerHelper;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "checkAndRequestPermission", "permissionType", "Lcom/v2ray/ang/enums/PermissionType;", "onGranted", "Lkotlin/Function0;", "launchFileChooser", "mimeType", "", "onResult", "Lkotlin/Function1;", "Landroid/net/Uri;", "launchCreateDocument", "fileName", "launchQRCodeScanner", "app_playstoreDebug"})
public abstract class HelperBaseActivity extends com.v2ray.ang.ui.BaseActivity {
    private com.v2ray.ang.helper.FileChooserHelper fileChooser;
    private com.v2ray.ang.helper.PermissionHelper permissionRequester;
    private com.v2ray.ang.helper.QRCodeScannerHelper qrCodeScanner;
    
    public HelperBaseActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    /**
     * Check if permission is granted and request it if not.
     * Convenience method that delegates to permissionRequester.
     *
     * @param permissionType The type of permission to check and request
     * @param onGranted Callback to execute when permission is granted
     */
    protected final void checkAndRequestPermission(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.enums.PermissionType permissionType, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onGranted) {
    }
    
    /**
     * Launch file chooser with ACTION_GET_CONTENT intent.
     * Convenience method that delegates to fileChooser helper.
     *
     * @param mimeType MIME type filter for files
     * @param onResult Callback invoked with the selected file URI (null if cancelled)
     */
    protected final void launchFileChooser(@org.jetbrains.annotations.NotNull()
    java.lang.String mimeType, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super android.net.Uri, kotlin.Unit> onResult) {
    }
    
    /**
     * Launch document creator to create a new file at user-selected location.
     * Convenience method that delegates to fileChooser helper.
     * Note: No permission check needed as CreateDocument uses Storage Access Framework.
     *
     * @param fileName Default file name for the new document
     * @param onResult Callback invoked with the created file URI (null if cancelled)
     */
    protected final void launchCreateDocument(@org.jetbrains.annotations.NotNull()
    java.lang.String fileName, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super android.net.Uri, kotlin.Unit> onResult) {
    }
    
    /**
     * Launch QR code scanner with camera permission check.
     * Convenience method that delegates to qrCodeScanner helper.
     *
     * @param onResult Callback invoked with the scan result string (null if cancelled or failed)
     */
    protected final void launchQRCodeScanner(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onResult) {
    }
}