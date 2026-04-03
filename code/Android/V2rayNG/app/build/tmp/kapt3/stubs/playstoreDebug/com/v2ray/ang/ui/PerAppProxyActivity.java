package com.v2ray.ang.ui;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u000e\n\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017H\u0014J\b\u0010\u0018\u001a\u00020\u0015H\u0002J\u0010\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cH\u0016J\u0010\u0010\u001d\u001a\u00020\u001a2\u0006\u0010\u001e\u001a\u00020\u001fH\u0017J\b\u0010 \u001a\u00020\u0015H\u0002J\b\u0010!\u001a\u00020\u0015H\u0002J\b\u0010\"\u001a\u00020\u0015H\u0002J\b\u0010#\u001a\u00020\u0015H\u0002J\b\u0010$\u001a\u00020\u0015H\u0002J\b\u0010%\u001a\u00020\u0015H\u0002J\u0018\u0010&\u001a\u00020\u001a2\u0006\u0010\'\u001a\u00020(2\u0006\u0010)\u001a\u00020\u001aH\u0003J \u0010*\u001a\u00020\u001a2\u0006\u0010+\u001a\u00020(2\u0006\u0010,\u001a\u00020(2\u0006\u0010)\u001a\u00020\u001aH\u0002J\u0010\u0010-\u001a\u00020\u001a2\u0006\u0010\'\u001a\u00020(H\u0002J\b\u0010.\u001a\u00020\u0015H\u0007R\u001b\u0010\u0004\u001a\u00020\u00058BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\f\u001a\n\u0012\u0004\u0012\u00020\u000e\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u000f\u001a\u00020\u00108BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0013\u0010\t\u001a\u0004\b\u0011\u0010\u0012\u00a8\u0006/"}, d2 = {"Lcom/v2ray/ang/ui/PerAppProxyActivity;", "Lcom/v2ray/ang/ui/BaseActivity;", "<init>", "()V", "binding", "Lcom/v2ray/ang/databinding/ActivityBypassListBinding;", "getBinding", "()Lcom/v2ray/ang/databinding/ActivityBypassListBinding;", "binding$delegate", "Lkotlin/Lazy;", "adapter", "Lcom/v2ray/ang/ui/PerAppProxyAdapter;", "appsAll", "", "Lcom/v2ray/ang/dto/AppInfo;", "viewModel", "Lcom/v2ray/ang/viewmodel/PerAppProxyViewModel;", "getViewModel", "()Lcom/v2ray/ang/viewmodel/PerAppProxyViewModel;", "viewModel$delegate", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "initList", "onCreateOptionsMenu", "", "menu", "Landroid/view/Menu;", "onOptionsItemSelected", "item", "Landroid/view/MenuItem;", "selectAllApp", "invertSelection", "selectProxyAppAuto", "importProxyApp", "exportProxyApp", "allowPerAppProxy", "selectProxyApp", "content", "", "force", "inProxyApps", "proxyApps", "packageName", "filterProxyApp", "refreshData", "app_playstoreDebug"})
public final class PerAppProxyActivity extends com.v2ray.ang.ui.BaseActivity {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy binding$delegate = null;
    @org.jetbrains.annotations.Nullable()
    private com.v2ray.ang.ui.PerAppProxyAdapter adapter;
    @org.jetbrains.annotations.Nullable()
    private java.util.List<com.v2ray.ang.dto.AppInfo> appsAll;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy viewModel$delegate = null;
    
    public PerAppProxyActivity() {
        super();
    }
    
    private final com.v2ray.ang.databinding.ActivityBypassListBinding getBinding() {
        return null;
    }
    
    private final com.v2ray.ang.viewmodel.PerAppProxyViewModel getViewModel() {
        return null;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void initList() {
    }
    
    @java.lang.Override()
    public boolean onCreateOptionsMenu(@org.jetbrains.annotations.NotNull()
    android.view.Menu menu) {
        return false;
    }
    
    @java.lang.Override()
    @android.annotation.SuppressLint(value = {"NotifyDataSetChanged"})
    public boolean onOptionsItemSelected(@org.jetbrains.annotations.NotNull()
    android.view.MenuItem item) {
        return false;
    }
    
    private final void selectAllApp() {
    }
    
    private final void invertSelection() {
    }
    
    private final void selectProxyAppAuto() {
    }
    
    private final void importProxyApp() {
    }
    
    private final void exportProxyApp() {
    }
    
    private final void allowPerAppProxy() {
    }
    
    @android.annotation.SuppressLint(value = {"NotifyDataSetChanged"})
    private final boolean selectProxyApp(java.lang.String content, boolean force) {
        return false;
    }
    
    private final boolean inProxyApps(java.lang.String proxyApps, java.lang.String packageName, boolean force) {
        return false;
    }
    
    private final boolean filterProxyApp(java.lang.String content) {
        return false;
    }
    
    @android.annotation.SuppressLint(value = {"NotifyDataSetChanged"})
    public final void refreshData() {
    }
}