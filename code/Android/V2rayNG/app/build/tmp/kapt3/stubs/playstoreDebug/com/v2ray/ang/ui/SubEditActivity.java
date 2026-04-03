package com.v2ray.ang.ui;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H\u0014J\u0010\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J\b\u0010\u001a\u001a\u00020\u0017H\u0002J\b\u0010\u001b\u001a\u00020\u0017H\u0002J\b\u0010\u001c\u001a\u00020\u0017H\u0002J\u0010\u0010\u001d\u001a\u00020\u00172\u0006\u0010\u001e\u001a\u00020\u001fH\u0016J\u0010\u0010 \u001a\u00020\u00172\u0006\u0010!\u001a\u00020\u000bH\u0016R\u001b\u0010\u0004\u001a\u00020\u00058BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\r\u001a\u00020\u000e8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0011\u0010\t\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\""}, d2 = {"Lcom/v2ray/ang/ui/SubEditActivity;", "Lcom/v2ray/ang/ui/BaseActivity;", "<init>", "()V", "binding", "Lcom/v2ray/ang/databinding/ActivitySubEditBinding;", "getBinding", "()Lcom/v2ray/ang/databinding/ActivitySubEditBinding;", "binding$delegate", "Lkotlin/Lazy;", "del_config", "Landroid/view/MenuItem;", "save_config", "editSubId", "", "getEditSubId", "()Ljava/lang/String;", "editSubId$delegate", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "bindingServer", "", "subItem", "Lcom/v2ray/ang/dto/SubscriptionItem;", "clearServer", "saveServer", "deleteServer", "onCreateOptionsMenu", "menu", "Landroid/view/Menu;", "onOptionsItemSelected", "item", "app_playstoreDebug"})
public final class SubEditActivity extends com.v2ray.ang.ui.BaseActivity {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy binding$delegate = null;
    @org.jetbrains.annotations.Nullable()
    private android.view.MenuItem del_config;
    @org.jetbrains.annotations.Nullable()
    private android.view.MenuItem save_config;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy editSubId$delegate = null;
    
    public SubEditActivity() {
        super();
    }
    
    private final com.v2ray.ang.databinding.ActivitySubEditBinding getBinding() {
        return null;
    }
    
    private final java.lang.String getEditSubId() {
        return null;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    /**
     * binding selected server config
     */
    private final boolean bindingServer(com.v2ray.ang.dto.SubscriptionItem subItem) {
        return false;
    }
    
    /**
     * clear or init server config
     */
    private final boolean clearServer() {
        return false;
    }
    
    /**
     * save server config
     */
    private final boolean saveServer() {
        return false;
    }
    
    /**
     * save server config
     */
    private final boolean deleteServer() {
        return false;
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
}