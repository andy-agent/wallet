package com.v2ray.ang.ui;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\t\u0018\u00002\u00020\u0001:\u00013B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u001cH\u0014J\b\u0010\u001d\u001a\u00020\u001aH\u0014J\u0010\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!H\u0016J\u0010\u0010\"\u001a\u00020\u001f2\u0006\u0010#\u001a\u00020$H\u0016J\b\u0010%\u001a\u00020&H\u0002J\b\u0010\'\u001a\u00020\u001aH\u0002J\b\u0010(\u001a\u00020\u001aH\u0002J\u0010\u0010)\u001a\u00020&2\u0006\u0010*\u001a\u00020+H\u0002J\u0012\u0010,\u001a\u0004\u0018\u00010&2\u0006\u0010*\u001a\u00020+H\u0002J\b\u0010-\u001a\u00020\u001fH\u0002J\u0012\u0010.\u001a\u00020\u001f2\b\u0010/\u001a\u0004\u0018\u00010&H\u0002J\b\u00100\u001a\u00020\u001aH\u0002J\u0006\u00101\u001a\u00020\u001aJ\b\u00102\u001a\u00020\u001aH\u0007R\u001b\u0010\u0004\u001a\u00020\u00058BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007R\u0014\u0010\n\u001a\u00020\u00008BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u000b\u0010\fR\u001b\u0010\r\u001a\u00020\u000e8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0011\u0010\t\u001a\u0004\b\u000f\u0010\u0010R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082.\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0014\u001a\u00020\u00158FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0018\u0010\t\u001a\u0004\b\u0016\u0010\u0017\u00a8\u00064"}, d2 = {"Lcom/v2ray/ang/ui/UserAssetActivity;", "Lcom/v2ray/ang/ui/HelperBaseActivity;", "<init>", "()V", "binding", "Lcom/v2ray/ang/databinding/ActivityUserAssetBinding;", "getBinding", "()Lcom/v2ray/ang/databinding/ActivityUserAssetBinding;", "binding$delegate", "Lkotlin/Lazy;", "ownerActivity", "getOwnerActivity", "()Lcom/v2ray/ang/ui/UserAssetActivity;", "viewModel", "Lcom/v2ray/ang/viewmodel/UserAssetViewModel;", "getViewModel", "()Lcom/v2ray/ang/viewmodel/UserAssetViewModel;", "viewModel$delegate", "adapter", "Lcom/v2ray/ang/ui/UserAssetAdapter;", "extDir", "Ljava/io/File;", "getExtDir", "()Ljava/io/File;", "extDir$delegate", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "onCreateOptionsMenu", "", "menu", "Landroid/view/Menu;", "onOptionsItemSelected", "item", "Landroid/view/MenuItem;", "getGeoFilesSources", "", "setGeoFilesSources", "showFileChooser", "copyFile", "uri", "Landroid/net/Uri;", "getCursorName", "importAssetFromQRcode", "importAsset", "url", "downloadGeoFiles", "initAssets", "refreshData", "ActivityAdapterListener", "app_fdroidDebug"})
public final class UserAssetActivity extends com.v2ray.ang.ui.HelperBaseActivity {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy binding$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy viewModel$delegate = null;
    private com.v2ray.ang.ui.UserAssetAdapter adapter;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy extDir$delegate = null;
    
    public UserAssetActivity() {
        super();
    }
    
    private final com.v2ray.ang.databinding.ActivityUserAssetBinding getBinding() {
        return null;
    }
    
    private final com.v2ray.ang.ui.UserAssetActivity getOwnerActivity() {
        return null;
    }
    
    private final com.v2ray.ang.viewmodel.UserAssetViewModel getViewModel() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.io.File getExtDir() {
        return null;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    protected void onResume() {
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
    
    private final java.lang.String getGeoFilesSources() {
        return null;
    }
    
    private final void setGeoFilesSources() {
    }
    
    private final void showFileChooser() {
    }
    
    private final java.lang.String copyFile(android.net.Uri uri) {
        return null;
    }
    
    private final java.lang.String getCursorName(android.net.Uri uri) {
        return null;
    }
    
    private final boolean importAssetFromQRcode() {
        return false;
    }
    
    private final boolean importAsset(java.lang.String url) {
        return false;
    }
    
    private final void downloadGeoFiles() {
    }
    
    public final void initAssets() {
    }
    
    @android.annotation.SuppressLint(value = {"NotifyDataSetChanged"})
    public final void refreshData() {
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\b\u0082\u0004\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0016J\u0018\u0010\n\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0016J\u0010\u0010\u000b\u001a\u00020\u00052\u0006\u0010\f\u001a\u00020\u0007H\u0016J\b\u0010\r\u001a\u00020\u0005H\u0016\u00a8\u0006\u000e"}, d2 = {"Lcom/v2ray/ang/ui/UserAssetActivity$ActivityAdapterListener;", "Lcom/v2ray/ang/contracts/BaseAdapterListener;", "<init>", "(Lcom/v2ray/ang/ui/UserAssetActivity;)V", "onEdit", "", "guid", "", "position", "", "onRemove", "onShare", "url", "onRefreshData", "app_fdroidDebug"})
    final class ActivityAdapterListener implements com.v2ray.ang.contracts.BaseAdapterListener {
        
        public ActivityAdapterListener() {
            super();
        }
        
        @java.lang.Override()
        public void onEdit(@org.jetbrains.annotations.NotNull()
        java.lang.String guid, int position) {
        }
        
        @java.lang.Override()
        public void onRemove(@org.jetbrains.annotations.NotNull()
        java.lang.String guid, int position) {
        }
        
        @java.lang.Override()
        public void onShare(@org.jetbrains.annotations.NotNull()
        java.lang.String url) {
        }
        
        @java.lang.Override()
        public void onRefreshData() {
        }
    }
}