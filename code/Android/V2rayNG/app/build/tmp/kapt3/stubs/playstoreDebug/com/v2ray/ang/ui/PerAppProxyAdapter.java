package com.v2ray.ang.ui;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u0000 \u00192\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0003\u0019\u001a\u001bB\u001d\u0012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0004\b\b\u0010\tJ\u0018\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00022\u0006\u0010\u0011\u001a\u00020\u0012H\u0016J\b\u0010\u0013\u001a\u00020\u0012H\u0016J\u0018\u0010\u0014\u001a\u00020\u00022\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0012H\u0016J\u0010\u0010\u0018\u001a\u00020\u00122\u0006\u0010\u0011\u001a\u00020\u0012H\u0016R\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r\u00a8\u0006\u001c"}, d2 = {"Lcom/v2ray/ang/ui/PerAppProxyAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/v2ray/ang/ui/PerAppProxyAdapter$BaseViewHolder;", "apps", "", "Lcom/v2ray/ang/dto/AppInfo;", "viewModel", "Lcom/v2ray/ang/viewmodel/PerAppProxyViewModel;", "<init>", "(Ljava/util/List;Lcom/v2ray/ang/viewmodel/PerAppProxyViewModel;)V", "getApps", "()Ljava/util/List;", "getViewModel", "()Lcom/v2ray/ang/viewmodel/PerAppProxyViewModel;", "onBindViewHolder", "", "holder", "position", "", "getItemCount", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "getItemViewType", "Companion", "BaseViewHolder", "AppViewHolder", "app_playstoreDebug"})
public final class PerAppProxyAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.v2ray.ang.ui.PerAppProxyAdapter.BaseViewHolder> {
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.v2ray.ang.dto.AppInfo> apps = null;
    @org.jetbrains.annotations.NotNull()
    private final com.v2ray.ang.viewmodel.PerAppProxyViewModel viewModel = null;
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.ui.PerAppProxyAdapter.Companion Companion = null;
    
    public PerAppProxyAdapter(@org.jetbrains.annotations.NotNull()
    java.util.List<com.v2ray.ang.dto.AppInfo> apps, @org.jetbrains.annotations.NotNull()
    com.v2ray.ang.viewmodel.PerAppProxyViewModel viewModel) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.v2ray.ang.dto.AppInfo> getApps() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.v2ray.ang.viewmodel.PerAppProxyViewModel getViewModel() {
        return null;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.ui.PerAppProxyAdapter.BaseViewHolder holder, int position) {
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.v2ray.ang.ui.PerAppProxyAdapter.BaseViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public int getItemViewType(int position) {
        return 0;
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u00012\u00020\u0002B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0006J\u000e\u0010\t\u001a\u00020\n2\u0006\u0010\u0007\u001a\u00020\bJ\u0012\u0010\u000b\u001a\u00020\n2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u0016R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/v2ray/ang/ui/PerAppProxyAdapter$AppViewHolder;", "Lcom/v2ray/ang/ui/PerAppProxyAdapter$BaseViewHolder;", "Landroid/view/View$OnClickListener;", "itemBypassBinding", "Lcom/v2ray/ang/databinding/ItemRecyclerBypassListBinding;", "<init>", "(Lcom/v2ray/ang/ui/PerAppProxyAdapter;Lcom/v2ray/ang/databinding/ItemRecyclerBypassListBinding;)V", "appInfo", "Lcom/v2ray/ang/dto/AppInfo;", "bind", "", "onClick", "v", "Landroid/view/View;", "app_playstoreDebug"})
    public final class AppViewHolder extends com.v2ray.ang.ui.PerAppProxyAdapter.BaseViewHolder implements android.view.View.OnClickListener {
        @org.jetbrains.annotations.NotNull()
        private final com.v2ray.ang.databinding.ItemRecyclerBypassListBinding itemBypassBinding = null;
        private com.v2ray.ang.dto.AppInfo appInfo;
        
        public AppViewHolder(@org.jetbrains.annotations.NotNull()
        com.v2ray.ang.databinding.ItemRecyclerBypassListBinding itemBypassBinding) {
            super(null);
        }
        
        public final void bind(@org.jetbrains.annotations.NotNull()
        com.v2ray.ang.dto.AppInfo appInfo) {
        }
        
        @java.lang.Override()
        public void onClick(@org.jetbrains.annotations.Nullable()
        android.view.View v) {
        }
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0016\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005\u00a8\u0006\u0006"}, d2 = {"Lcom/v2ray/ang/ui/PerAppProxyAdapter$BaseViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "itemView", "Landroid/view/View;", "<init>", "(Landroid/view/View;)V", "app_playstoreDebug"})
    public static class BaseViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        
        public BaseViewHolder(@org.jetbrains.annotations.NotNull()
        android.view.View itemView) {
            super(null);
        }
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/v2ray/ang/ui/PerAppProxyAdapter$Companion;", "", "<init>", "()V", "VIEW_TYPE_HEADER", "", "VIEW_TYPE_ITEM", "app_playstoreDebug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}