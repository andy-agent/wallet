package com.v2ray.ang.ui;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u0015B!\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\b\u00a2\u0006\u0004\b\t\u0010\nJ\b\u0010\u000b\u001a\u00020\fH\u0016J\u0018\u0010\r\u001a\u00020\u00022\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\fH\u0016J\u0018\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00022\u0006\u0010\u0014\u001a\u00020\fH\u0017R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/v2ray/ang/ui/UserAssetAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/v2ray/ang/ui/UserAssetAdapter$UserAssetViewHolder;", "viewModel", "Lcom/v2ray/ang/viewmodel/UserAssetViewModel;", "extDir", "Ljava/io/File;", "adapterListener", "Lcom/v2ray/ang/contracts/BaseAdapterListener;", "<init>", "(Lcom/v2ray/ang/viewmodel/UserAssetViewModel;Ljava/io/File;Lcom/v2ray/ang/contracts/BaseAdapterListener;)V", "getItemCount", "", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "onBindViewHolder", "", "holder", "position", "UserAssetViewHolder", "app_fdroidDebug"})
public final class UserAssetAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.v2ray.ang.ui.UserAssetAdapter.UserAssetViewHolder> {
    @org.jetbrains.annotations.NotNull()
    private final com.v2ray.ang.viewmodel.UserAssetViewModel viewModel = null;
    @org.jetbrains.annotations.NotNull()
    private final java.io.File extDir = null;
    @org.jetbrains.annotations.Nullable()
    private final com.v2ray.ang.contracts.BaseAdapterListener adapterListener = null;
    
    public UserAssetAdapter(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.viewmodel.UserAssetViewModel viewModel, @org.jetbrains.annotations.NotNull()
    java.io.File extDir, @org.jetbrains.annotations.Nullable()
    com.v2ray.ang.contracts.BaseAdapterListener adapterListener) {
        super();
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.v2ray.ang.ui.UserAssetAdapter.UserAssetViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    @android.annotation.SuppressLint(value = {"SetTextI18n"})
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.ui.UserAssetAdapter.UserAssetViewHolder holder, int position) {
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\b"}, d2 = {"Lcom/v2ray/ang/ui/UserAssetAdapter$UserAssetViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "itemUserAssetBinding", "Lcom/v2ray/ang/databinding/ItemRecyclerUserAssetBinding;", "<init>", "(Lcom/v2ray/ang/databinding/ItemRecyclerUserAssetBinding;)V", "getItemUserAssetBinding", "()Lcom/v2ray/ang/databinding/ItemRecyclerUserAssetBinding;", "app_fdroidDebug"})
    public static final class UserAssetViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @org.jetbrains.annotations.NotNull()
        private final com.v2ray.ang.databinding.ItemRecyclerUserAssetBinding itemUserAssetBinding = null;
        
        public UserAssetViewHolder(@org.jetbrains.annotations.NotNull()
        com.v2ray.ang.databinding.ItemRecyclerUserAssetBinding itemUserAssetBinding) {
            super(null);
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.v2ray.ang.databinding.ItemRecyclerUserAssetBinding getItemUserAssetBinding() {
            return null;
        }
    }
}