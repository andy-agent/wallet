package com.v2ray.ang.ui;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u0015B\'\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0016\b\u0002\u0010\u0005\u001a\u0010\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b\u0018\u00010\u0006\u00a2\u0006\u0004\b\t\u0010\nJ\b\u0010\u000b\u001a\u00020\fH\u0016J\u0018\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00022\u0006\u0010\u0010\u001a\u00020\fH\u0016J\u0018\u0010\u0011\u001a\u00020\u00022\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\fH\u0016R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u0005\u001a\u0010\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b\u0018\u00010\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/v2ray/ang/ui/LogcatRecyclerAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/v2ray/ang/ui/LogcatRecyclerAdapter$MainViewHolder;", "viewModel", "Lcom/v2ray/ang/viewmodel/LogcatViewModel;", "onLongClick", "Lkotlin/Function1;", "", "", "<init>", "(Lcom/v2ray/ang/viewmodel/LogcatViewModel;Lkotlin/jvm/functions/Function1;)V", "getItemCount", "", "onBindViewHolder", "", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "MainViewHolder", "app_playstoreDebug"})
public final class LogcatRecyclerAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.v2ray.ang.ui.LogcatRecyclerAdapter.MainViewHolder> {
    @org.jetbrains.annotations.NotNull()
    private final com.v2ray.ang.viewmodel.LogcatViewModel viewModel = null;
    @org.jetbrains.annotations.Nullable()
    private final kotlin.jvm.functions.Function1<java.lang.String, java.lang.Boolean> onLongClick = null;
    
    public LogcatRecyclerAdapter(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.viewmodel.LogcatViewModel viewModel, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function1<? super java.lang.String, java.lang.Boolean> onLongClick) {
        super();
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.ui.LogcatRecyclerAdapter.MainViewHolder holder, int position) {
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.v2ray.ang.ui.LogcatRecyclerAdapter.MainViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\b"}, d2 = {"Lcom/v2ray/ang/ui/LogcatRecyclerAdapter$MainViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "itemSubSettingBinding", "Lcom/v2ray/ang/databinding/ItemRecyclerLogcatBinding;", "<init>", "(Lcom/v2ray/ang/databinding/ItemRecyclerLogcatBinding;)V", "getItemSubSettingBinding", "()Lcom/v2ray/ang/databinding/ItemRecyclerLogcatBinding;", "app_playstoreDebug"})
    public static final class MainViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @org.jetbrains.annotations.NotNull()
        private final com.v2ray.ang.databinding.ItemRecyclerLogcatBinding itemSubSettingBinding = null;
        
        public MainViewHolder(@org.jetbrains.annotations.NotNull()
        com.v2ray.ang.databinding.ItemRecyclerLogcatBinding itemSubSettingBinding) {
            super(null);
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.v2ray.ang.databinding.ItemRecyclerLogcatBinding getItemSubSettingBinding() {
            return null;
        }
    }
}