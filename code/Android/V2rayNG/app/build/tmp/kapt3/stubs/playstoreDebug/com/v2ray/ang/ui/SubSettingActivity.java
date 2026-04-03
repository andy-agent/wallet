package com.v2ray.ang.ui;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001:\u0001)B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u0014J\b\u0010 \u001a\u00020\u001dH\u0014J\u0010\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020$H\u0016J\u0010\u0010%\u001a\u00020\"2\u0006\u0010&\u001a\u00020\'H\u0016J\b\u0010(\u001a\u00020\u001dH\u0007R\u001b\u0010\u0004\u001a\u00020\u00058BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007R\u0014\u0010\n\u001a\u00020\u00008BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u000b\u0010\fR\u001b\u0010\r\u001a\u00020\u000e8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0011\u0010\t\u001a\u0004\b\u000f\u0010\u0010R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0014\u001a\u0004\u0018\u00010\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R#\u0010\u0016\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00180\u00178BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001b\u0010\t\u001a\u0004\b\u0019\u0010\u001a\u00a8\u0006*"}, d2 = {"Lcom/v2ray/ang/ui/SubSettingActivity;", "Lcom/v2ray/ang/ui/BaseActivity;", "<init>", "()V", "binding", "Lcom/v2ray/ang/databinding/ActivitySubSettingBinding;", "getBinding", "()Lcom/v2ray/ang/databinding/ActivitySubSettingBinding;", "binding$delegate", "Lkotlin/Lazy;", "ownerActivity", "getOwnerActivity", "()Lcom/v2ray/ang/ui/SubSettingActivity;", "viewModel", "Lcom/v2ray/ang/viewmodel/SubscriptionsViewModel;", "getViewModel", "()Lcom/v2ray/ang/viewmodel/SubscriptionsViewModel;", "viewModel$delegate", "adapter", "Lcom/v2ray/ang/ui/SubSettingRecyclerAdapter;", "mItemTouchHelper", "Landroidx/recyclerview/widget/ItemTouchHelper;", "share_method", "", "", "getShare_method", "()[Ljava/lang/String;", "share_method$delegate", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "onCreateOptionsMenu", "", "menu", "Landroid/view/Menu;", "onOptionsItemSelected", "item", "Landroid/view/MenuItem;", "refreshData", "ActivityAdapterListener", "app_playstoreDebug"})
public final class SubSettingActivity extends com.v2ray.ang.ui.BaseActivity {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy binding$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy viewModel$delegate = null;
    private com.v2ray.ang.ui.SubSettingRecyclerAdapter adapter;
    @org.jetbrains.annotations.Nullable()
    private androidx.recyclerview.widget.ItemTouchHelper mItemTouchHelper;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy share_method$delegate = null;
    
    public SubSettingActivity() {
        super();
    }
    
    private final com.v2ray.ang.databinding.ActivitySubSettingBinding getBinding() {
        return null;
    }
    
    private final com.v2ray.ang.ui.SubSettingActivity getOwnerActivity() {
        return null;
    }
    
    private final com.v2ray.ang.viewmodel.SubscriptionsViewModel getViewModel() {
        return null;
    }
    
    private final java.lang.String[] getShare_method() {
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
    
    @android.annotation.SuppressLint(value = {"NotifyDataSetChanged"})
    public final void refreshData() {
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\b\u0082\u0004\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0016J\u0018\u0010\n\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0016J\u0010\u0010\u000b\u001a\u00020\u00052\u0006\u0010\f\u001a\u00020\u0007H\u0016J\b\u0010\r\u001a\u00020\u0005H\u0016\u00a8\u0006\u000e"}, d2 = {"Lcom/v2ray/ang/ui/SubSettingActivity$ActivityAdapterListener;", "Lcom/v2ray/ang/contracts/BaseAdapterListener;", "<init>", "(Lcom/v2ray/ang/ui/SubSettingActivity;)V", "onEdit", "", "guid", "", "position", "", "onRemove", "onShare", "url", "onRefreshData", "app_playstoreDebug"})
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