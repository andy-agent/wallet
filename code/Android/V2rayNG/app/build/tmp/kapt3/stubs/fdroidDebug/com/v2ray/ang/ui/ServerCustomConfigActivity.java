package com.v2ray.ang.ui;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016H\u0014J\u0010\u0010\u0017\u001a\u00020\u00102\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J\b\u0010\u001a\u001a\u00020\u0010H\u0002J\b\u0010\u001b\u001a\u00020\u0010H\u0002J\b\u0010\u001c\u001a\u00020\u0010H\u0002J\u0010\u0010\u001d\u001a\u00020\u00102\u0006\u0010\u001e\u001a\u00020\u001fH\u0016J\u0010\u0010 \u001a\u00020\u00102\u0006\u0010!\u001a\u00020\"H\u0016R\u001b\u0010\u0004\u001a\u00020\u00058BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007R\u001b\u0010\n\u001a\u00020\u000b8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000e\u0010\t\u001a\u0004\b\f\u0010\rR\u001b\u0010\u000f\u001a\u00020\u00108BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0012\u0010\t\u001a\u0004\b\u000f\u0010\u0011\u00a8\u0006#"}, d2 = {"Lcom/v2ray/ang/ui/ServerCustomConfigActivity;", "Lcom/v2ray/ang/ui/BaseActivity;", "<init>", "()V", "binding", "Lcom/v2ray/ang/databinding/ActivityServerCustomConfigBinding;", "getBinding", "()Lcom/v2ray/ang/databinding/ActivityServerCustomConfigBinding;", "binding$delegate", "Lkotlin/Lazy;", "editGuid", "", "getEditGuid", "()Ljava/lang/String;", "editGuid$delegate", "isRunning", "", "()Z", "isRunning$delegate", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "bindingServer", "config", "Lcom/v2ray/ang/dto/ProfileItem;", "clearServer", "saveServer", "deleteServer", "onCreateOptionsMenu", "menu", "Landroid/view/Menu;", "onOptionsItemSelected", "item", "Landroid/view/MenuItem;", "app_fdroidDebug"})
public final class ServerCustomConfigActivity extends com.v2ray.ang.ui.BaseActivity {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy binding$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy editGuid$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy isRunning$delegate = null;
    
    public ServerCustomConfigActivity() {
        super();
    }
    
    private final com.v2ray.ang.databinding.ActivityServerCustomConfigBinding getBinding() {
        return null;
    }
    
    private final java.lang.String getEditGuid() {
        return null;
    }
    
    private final boolean isRunning() {
        return false;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    /**
     * Binding selected server config
     */
    private final boolean bindingServer(com.v2ray.ang.dto.ProfileItem config) {
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