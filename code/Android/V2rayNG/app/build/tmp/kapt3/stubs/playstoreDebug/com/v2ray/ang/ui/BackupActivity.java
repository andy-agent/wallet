package com.v2ray.ang.ui;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013H\u0014J\u0014\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\f0\u0015H\u0002J\u0010\u0010\u0017\u001a\u00020\u00162\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J\b\u0010\u001a\u001a\u00020\u0011H\u0002J\b\u0010\u001b\u001a\u00020\u0011H\u0002J\b\u0010\u001c\u001a\u00020\u0011H\u0002J\b\u0010\u001d\u001a\u00020\u0011H\u0002J\b\u0010\u001e\u001a\u00020\u0011H\u0002J\b\u0010\u001f\u001a\u00020\u0011H\u0002R\u001b\u0010\u0004\u001a\u00020\u00058BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007R#\u0010\n\u001a\n\u0012\u0006\b\u0001\u0012\u00020\f0\u000b8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000f\u0010\t\u001a\u0004\b\r\u0010\u000e\u00a8\u0006 "}, d2 = {"Lcom/v2ray/ang/ui/BackupActivity;", "Lcom/v2ray/ang/ui/HelperBaseActivity;", "<init>", "()V", "binding", "Lcom/v2ray/ang/databinding/ActivityBackupBinding;", "getBinding", "()Lcom/v2ray/ang/databinding/ActivityBackupBinding;", "binding$delegate", "Lkotlin/Lazy;", "config_backup_options", "", "", "getConfig_backup_options", "()[Ljava/lang/String;", "config_backup_options$delegate", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "backupConfigurationToCache", "Lkotlin/Pair;", "", "restoreConfiguration", "zipFile", "Ljava/io/File;", "showFileChooser", "backupViaLocal", "restoreViaLocal", "backupViaWebDav", "restoreViaWebDav", "showWebDavSettingsDialog", "app_playstoreDebug"})
public final class BackupActivity extends com.v2ray.ang.ui.HelperBaseActivity {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy binding$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy config_backup_options$delegate = null;
    
    public BackupActivity() {
        super();
    }
    
    private final com.v2ray.ang.databinding.ActivityBackupBinding getBinding() {
        return null;
    }
    
    private final java.lang.String[] getConfig_backup_options() {
        return null;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    /**
     * Backup configuration to cache directory
     * Returns Pair<success, zipFilePath>
     */
    private final kotlin.Pair<java.lang.Boolean, java.lang.String> backupConfigurationToCache() {
        return null;
    }
    
    private final boolean restoreConfiguration(java.io.File zipFile) {
        return false;
    }
    
    private final void showFileChooser() {
    }
    
    private final void backupViaLocal() {
    }
    
    private final void restoreViaLocal() {
    }
    
    private final void backupViaWebDav() {
    }
    
    private final void restoreViaWebDav() {
    }
    
    private final void showWebDavSettingsDialog() {
    }
}