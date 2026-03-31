package com.v2ray.ang.helper;

/**
 * Helper for choosing and creating files using Android Storage Access Framework.
 * Supports both file selection (ACTION_GET_CONTENT) and file creation (CreateDocument).
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J&\u0010\u0010\u001a\u00020\t2\b\b\u0002\u0010\u0011\u001a\u00020\u000f2\u0014\u0010\u0012\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010\b\u0012\u0004\u0012\u00020\t0\u0007J$\u0010\u0013\u001a\u00020\t2\u0006\u0010\u0014\u001a\u00020\u000f2\u0014\u0010\u0012\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010\b\u0012\u0004\u0012\u00020\t0\u0007R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u0006\u001a\u0012\u0012\u0006\u0012\u0004\u0018\u00010\b\u0012\u0004\u0012\u00020\t\u0018\u00010\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001e\u0010\n\u001a\u0012\u0012\u0006\u0012\u0004\u0018\u00010\b\u0012\u0004\u0012\u00020\t\u0018\u00010\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lcom/v2ray/ang/helper/FileChooserHelper;", "", "activity", "Landroidx/appcompat/app/AppCompatActivity;", "<init>", "(Landroidx/appcompat/app/AppCompatActivity;)V", "fileChooserCallback", "Lkotlin/Function1;", "Landroid/net/Uri;", "", "documentCreateCallback", "fileChooserLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "Landroid/content/Intent;", "documentCreateLauncher", "", "launch", "mimeType", "onResult", "createDocument", "fileName", "app_fdroidDebug"})
public final class FileChooserHelper {
    @org.jetbrains.annotations.NotNull()
    private final androidx.appcompat.app.AppCompatActivity activity = null;
    @org.jetbrains.annotations.Nullable()
    private kotlin.jvm.functions.Function1<? super android.net.Uri, kotlin.Unit> fileChooserCallback;
    @org.jetbrains.annotations.Nullable()
    private kotlin.jvm.functions.Function1<? super android.net.Uri, kotlin.Unit> documentCreateCallback;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<android.content.Intent> fileChooserLauncher = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<java.lang.String> documentCreateLauncher = null;
    
    public FileChooserHelper(@org.jetbrains.annotations.NotNull()
    androidx.appcompat.app.AppCompatActivity activity) {
        super();
    }
    
    /**
     * Launch file chooser with ACTION_GET_CONTENT intent to select an existing file.
     *
     * @param mimeType MIME type filter for files
     * @param onResult Callback invoked with the selected file URI (null if cancelled)
     */
    public final void launch(@org.jetbrains.annotations.NotNull()
    java.lang.String mimeType, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super android.net.Uri, kotlin.Unit> onResult) {
    }
    
    /**
     * Launch document creator to create a new file at user-selected location.
     *
     * @param fileName Default file name for the new document
     * @param onResult Callback invoked with the created file URI (null if cancelled)
     */
    public final void createDocument(@org.jetbrains.annotations.NotNull()
    java.lang.String fileName, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super android.net.Uri, kotlin.Unit> onResult) {
    }
}