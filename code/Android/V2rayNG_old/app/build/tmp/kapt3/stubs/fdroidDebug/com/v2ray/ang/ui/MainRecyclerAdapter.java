package com.v2ray.ang.ui;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\n\u0018\u0000 )2\b\u0012\u0004\u0012\u00020\u00020\u00012\u00020\u0003:\u0004)*+,B\u0019\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\u0004\b\b\u0010\tJ\"\u0010\u000f\u001a\u00020\u00102\u000e\u0010\u0011\u001a\n\u0012\u0004\u0012\u00020\u000e\u0018\u00010\r2\b\b\u0002\u0010\u0012\u001a\u00020\u0013H\u0007J\b\u0010\u0014\u001a\u00020\u0013H\u0016J\u0018\u0010\u0015\u001a\u00020\u00102\u0006\u0010\u0016\u001a\u00020\u00022\u0006\u0010\u0012\u001a\u00020\u0013H\u0016J\u0010\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001aH\u0002J\u0010\u0010\u001b\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001aH\u0002J\u0016\u0010\u001c\u001a\u00020\u00102\u0006\u0010\u001d\u001a\u00020\u00182\u0006\u0010\u0012\u001a\u00020\u0013J\u0016\u0010\u001e\u001a\u00020\u00102\u0006\u0010\u001f\u001a\u00020\u00132\u0006\u0010 \u001a\u00020\u0013J\u0018\u0010!\u001a\u00020\u00022\u0006\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020\u0013H\u0016J\u0010\u0010%\u001a\u00020\u00132\u0006\u0010\u0012\u001a\u00020\u0013H\u0016J\u0018\u0010&\u001a\u00020\u000b2\u0006\u0010\u001f\u001a\u00020\u00132\u0006\u0010 \u001a\u00020\u0013H\u0016J\b\u0010\'\u001a\u00020\u0010H\u0016J\u0010\u0010(\u001a\u00020\u00102\u0006\u0010\u0012\u001a\u00020\u0013H\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006-"}, d2 = {"Lcom/v2ray/ang/ui/MainRecyclerAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/v2ray/ang/ui/MainRecyclerAdapter$BaseViewHolder;", "Lcom/v2ray/ang/helper/ItemTouchHelperAdapter;", "mainViewModel", "Lcom/v2ray/ang/viewmodel/MainViewModel;", "adapterListener", "Lcom/v2ray/ang/contracts/MainAdapterListener;", "<init>", "(Lcom/v2ray/ang/viewmodel/MainViewModel;Lcom/v2ray/ang/contracts/MainAdapterListener;)V", "doubleColumnDisplay", "", "data", "", "Lcom/v2ray/ang/dto/ServersCache;", "setData", "", "newData", "position", "", "getItemCount", "onBindViewHolder", "holder", "getAddress", "", "profile", "Lcom/v2ray/ang/dto/ProfileItem;", "getSubscriptionRemarks", "removeServerSub", "guid", "setSelectServer", "fromPosition", "toPosition", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "getItemViewType", "onItemMove", "onItemMoveCompleted", "onItemDismiss", "Companion", "BaseViewHolder", "MainViewHolder", "FooterViewHolder", "app_fdroidDebug"})
public final class MainRecyclerAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.v2ray.ang.ui.MainRecyclerAdapter.BaseViewHolder> implements com.v2ray.ang.helper.ItemTouchHelperAdapter {
    @org.jetbrains.annotations.NotNull()
    private final com.v2ray.ang.viewmodel.MainViewModel mainViewModel = null;
    @org.jetbrains.annotations.Nullable()
    private final com.v2ray.ang.contracts.MainAdapterListener adapterListener = null;
    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_FOOTER = 2;
    private final boolean doubleColumnDisplay = false;
    @org.jetbrains.annotations.NotNull()
    private java.util.List<com.v2ray.ang.dto.ServersCache> data;
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.ui.MainRecyclerAdapter.Companion Companion = null;
    
    public MainRecyclerAdapter(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.viewmodel.MainViewModel mainViewModel, @org.jetbrains.annotations.Nullable()
    com.v2ray.ang.contracts.MainAdapterListener adapterListener) {
        super();
    }
    
    @android.annotation.SuppressLint(value = {"NotifyDataSetChanged"})
    public final void setData(@org.jetbrains.annotations.Nullable()
    java.util.List<com.v2ray.ang.dto.ServersCache> newData, int position) {
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.ui.MainRecyclerAdapter.BaseViewHolder holder, int position) {
    }
    
    /**
     * Gets the server address information
     * Hides part of IP or domain information for privacy protection
     * @param profile The server configuration
     * @return Formatted address string
     */
    private final java.lang.String getAddress(com.v2ray.ang.dto.ProfileItem profile) {
        return null;
    }
    
    /**
     * Gets the subscription remarks information
     * @param profile The server configuration
     * @return Subscription remarks string, or empty string if none
     */
    private final java.lang.String getSubscriptionRemarks(com.v2ray.ang.dto.ProfileItem profile) {
        return null;
    }
    
    public final void removeServerSub(@org.jetbrains.annotations.NotNull()
    java.lang.String guid, int position) {
    }
    
    public final void setSelectServer(int fromPosition, int toPosition) {
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.v2ray.ang.ui.MainRecyclerAdapter.BaseViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public int getItemViewType(int position) {
        return 0;
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
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0016\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007J\u0006\u0010\b\u001a\u00020\u0007\u00a8\u0006\t"}, d2 = {"Lcom/v2ray/ang/ui/MainRecyclerAdapter$BaseViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "itemView", "Landroid/view/View;", "<init>", "(Landroid/view/View;)V", "onItemSelected", "", "onItemClear", "app_fdroidDebug"})
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
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/v2ray/ang/ui/MainRecyclerAdapter$Companion;", "", "<init>", "()V", "VIEW_TYPE_ITEM", "", "VIEW_TYPE_FOOTER", "app_fdroidDebug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\b"}, d2 = {"Lcom/v2ray/ang/ui/MainRecyclerAdapter$FooterViewHolder;", "Lcom/v2ray/ang/ui/MainRecyclerAdapter$BaseViewHolder;", "itemFooterBinding", "Lcom/v2ray/ang/databinding/ItemRecyclerFooterBinding;", "<init>", "(Lcom/v2ray/ang/databinding/ItemRecyclerFooterBinding;)V", "getItemFooterBinding", "()Lcom/v2ray/ang/databinding/ItemRecyclerFooterBinding;", "app_fdroidDebug"})
    public static final class FooterViewHolder extends com.v2ray.ang.ui.MainRecyclerAdapter.BaseViewHolder {
        @org.jetbrains.annotations.NotNull()
        private final com.v2ray.ang.databinding.ItemRecyclerFooterBinding itemFooterBinding = null;
        
        public FooterViewHolder(@org.jetbrains.annotations.NotNull()
        com.v2ray.ang.databinding.ItemRecyclerFooterBinding itemFooterBinding) {
            super(null);
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.v2ray.ang.databinding.ItemRecyclerFooterBinding getItemFooterBinding() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u00012\u00020\u0002B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0006R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b\u00a8\u0006\t"}, d2 = {"Lcom/v2ray/ang/ui/MainRecyclerAdapter$MainViewHolder;", "Lcom/v2ray/ang/ui/MainRecyclerAdapter$BaseViewHolder;", "Lcom/v2ray/ang/helper/ItemTouchHelperViewHolder;", "itemMainBinding", "Lcom/v2ray/ang/databinding/ItemRecyclerMainBinding;", "<init>", "(Lcom/v2ray/ang/databinding/ItemRecyclerMainBinding;)V", "getItemMainBinding", "()Lcom/v2ray/ang/databinding/ItemRecyclerMainBinding;", "app_fdroidDebug"})
    public static final class MainViewHolder extends com.v2ray.ang.ui.MainRecyclerAdapter.BaseViewHolder implements com.v2ray.ang.helper.ItemTouchHelperViewHolder {
        @org.jetbrains.annotations.NotNull()
        private final com.v2ray.ang.databinding.ItemRecyclerMainBinding itemMainBinding = null;
        
        public MainViewHolder(@org.jetbrains.annotations.NotNull()
        com.v2ray.ang.databinding.ItemRecyclerMainBinding itemMainBinding) {
            super(null);
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.v2ray.ang.databinding.ItemRecyclerMainBinding getItemMainBinding() {
            return null;
        }
    }
}