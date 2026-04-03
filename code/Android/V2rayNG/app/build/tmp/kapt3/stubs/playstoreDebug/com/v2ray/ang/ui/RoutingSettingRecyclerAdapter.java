package com.v2ray.ang.ui;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0007\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u00012\u00020\u0003:\u0002\u001a\u001bB\u0019\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\u0004\b\b\u0010\tJ\b\u0010\n\u001a\u00020\u000bH\u0016J\u0018\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u00022\u0006\u0010\u000f\u001a\u00020\u000bH\u0016J\u0018\u0010\u0010\u001a\u00020\u00022\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u000bH\u0016J\u0018\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u000b2\u0006\u0010\u0017\u001a\u00020\u000bH\u0016J\b\u0010\u0018\u001a\u00020\rH\u0016J\u0010\u0010\u0019\u001a\u00020\r2\u0006\u0010\u000f\u001a\u00020\u000bH\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001c"}, d2 = {"Lcom/v2ray/ang/ui/RoutingSettingRecyclerAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/v2ray/ang/ui/RoutingSettingRecyclerAdapter$MainViewHolder;", "Lcom/v2ray/ang/helper/ItemTouchHelperAdapter;", "viewModel", "Lcom/v2ray/ang/viewmodel/RoutingSettingsViewModel;", "adapterListener", "Lcom/v2ray/ang/contracts/BaseAdapterListener;", "<init>", "(Lcom/v2ray/ang/viewmodel/RoutingSettingsViewModel;Lcom/v2ray/ang/contracts/BaseAdapterListener;)V", "getItemCount", "", "onBindViewHolder", "", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "onItemMove", "", "fromPosition", "toPosition", "onItemMoveCompleted", "onItemDismiss", "MainViewHolder", "BaseViewHolder", "app_playstoreDebug"})
public final class RoutingSettingRecyclerAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.v2ray.ang.ui.RoutingSettingRecyclerAdapter.MainViewHolder> implements com.v2ray.ang.helper.ItemTouchHelperAdapter {
    @org.jetbrains.annotations.NotNull()
    private final com.v2ray.ang.viewmodel.RoutingSettingsViewModel viewModel = null;
    @org.jetbrains.annotations.Nullable()
    private final com.v2ray.ang.contracts.BaseAdapterListener adapterListener = null;
    
    public RoutingSettingRecyclerAdapter(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.viewmodel.RoutingSettingsViewModel viewModel, @org.jetbrains.annotations.Nullable()
    com.v2ray.ang.contracts.BaseAdapterListener adapterListener) {
        super();
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.ui.RoutingSettingRecyclerAdapter.MainViewHolder holder, int position) {
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.v2ray.ang.ui.RoutingSettingRecyclerAdapter.MainViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }
    
    @java.lang.Override()
    public void onItemMoveCompleted() {
    }
    
    @java.lang.Override()
    public void onItemDismiss(int position) {
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0016\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007J\u0006\u0010\b\u001a\u00020\u0007\u00a8\u0006\t"}, d2 = {"Lcom/v2ray/ang/ui/RoutingSettingRecyclerAdapter$BaseViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "itemView", "Landroid/view/View;", "<init>", "(Landroid/view/View;)V", "onItemSelected", "", "onItemClear", "app_playstoreDebug"})
    public static class BaseViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        
        public BaseViewHolder(@org.jetbrains.annotations.NotNull()
        android.view.View itemView) {
            super(null);
        }
        
        public final void onItemSelected() {
        }
        
        public final void onItemClear() {
        }
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u00012\u00020\u0002B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0006R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b\u00a8\u0006\t"}, d2 = {"Lcom/v2ray/ang/ui/RoutingSettingRecyclerAdapter$MainViewHolder;", "Lcom/v2ray/ang/ui/RoutingSettingRecyclerAdapter$BaseViewHolder;", "Lcom/v2ray/ang/helper/ItemTouchHelperViewHolder;", "itemRoutingSettingBinding", "Lcom/v2ray/ang/databinding/ItemRecyclerRoutingSettingBinding;", "<init>", "(Lcom/v2ray/ang/databinding/ItemRecyclerRoutingSettingBinding;)V", "getItemRoutingSettingBinding", "()Lcom/v2ray/ang/databinding/ItemRecyclerRoutingSettingBinding;", "app_playstoreDebug"})
    public static final class MainViewHolder extends com.v2ray.ang.ui.RoutingSettingRecyclerAdapter.BaseViewHolder implements com.v2ray.ang.helper.ItemTouchHelperViewHolder {
        @org.jetbrains.annotations.NotNull()
        private final com.v2ray.ang.databinding.ItemRecyclerRoutingSettingBinding itemRoutingSettingBinding = null;
        
        public MainViewHolder(@org.jetbrains.annotations.NotNull()
        com.v2ray.ang.databinding.ItemRecyclerRoutingSettingBinding itemRoutingSettingBinding) {
            super(null);
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.v2ray.ang.databinding.ItemRecyclerRoutingSettingBinding getItemRoutingSettingBinding() {
            return null;
        }
    }
}