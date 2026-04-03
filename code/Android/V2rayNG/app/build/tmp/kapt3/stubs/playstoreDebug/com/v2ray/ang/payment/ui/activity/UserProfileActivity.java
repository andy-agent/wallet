package com.v2ray.ang.payment.ui.activity;

/**
 * 用户中心页面
 * 显示用户信息、到期时间、订单历史
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001:\u0001%B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0014J\b\u0010\u0012\u001a\u00020\u000fH\u0014J\b\u0010\u0013\u001a\u00020\u000fH\u0002J\b\u0010\u0014\u001a\u00020\u000fH\u0002J\b\u0010\u0015\u001a\u00020\u000fH\u0002J\u0010\u0010\u0016\u001a\u00020\u000f2\u0006\u0010\u0017\u001a\u00020\u000bH\u0002J\u0010\u0010\u0018\u001a\u00020\u000f2\u0006\u0010\u0019\u001a\u00020\u001aH\u0002J\b\u0010\u001b\u001a\u00020\u000fH\u0002J\b\u0010\u001c\u001a\u00020\u000fH\u0002J\b\u0010\u001d\u001a\u00020\u000fH\u0002J\u0010\u0010\u001e\u001a\u00020\u000f2\u0006\u0010\u001f\u001a\u00020 H\u0002J\u0010\u0010!\u001a\u00020\u001a2\u0006\u0010\"\u001a\u00020\u001aH\u0002J\u0010\u0010#\u001a\u00020\u000f2\u0006\u0010$\u001a\u00020\u001aH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082.\u00a2\u0006\u0002\n\u0000R\u0012\u0010\b\u001a\u00060\tR\u00020\u0000X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006&"}, d2 = {"Lcom/v2ray/ang/payment/ui/activity/UserProfileActivity;", "Lcom/v2ray/ang/ui/BaseActivity;", "<init>", "()V", "binding", "Lcom/v2ray/ang/databinding/ActivityUserProfileBinding;", "paymentRepository", "Lcom/v2ray/ang/payment/data/repository/PaymentRepository;", "orderAdapter", "Lcom/v2ray/ang/payment/ui/activity/UserProfileActivity$OrderHistoryAdapter;", "currentUser", "Lcom/v2ray/ang/payment/data/local/entity/UserEntity;", "dateFormat", "Ljava/text/SimpleDateFormat;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "setupRecyclerView", "setupClickListeners", "loadUserData", "displayUserInfo", "user", "loadOrderHistory", "userId", "", "refreshData", "showLogoutConfirmDialog", "performLogout", "showOrderDetail", "order", "Lcom/v2ray/ang/payment/data/local/entity/OrderEntity;", "getStatusText", "status", "showError", "message", "OrderHistoryAdapter", "app_playstoreDebug"})
public final class UserProfileActivity extends com.v2ray.ang.ui.BaseActivity {
    private com.v2ray.ang.databinding.ActivityUserProfileBinding binding;
    private com.v2ray.ang.payment.data.repository.PaymentRepository paymentRepository;
    private com.v2ray.ang.payment.ui.activity.UserProfileActivity.OrderHistoryAdapter orderAdapter;
    @org.jetbrains.annotations.Nullable()
    private com.v2ray.ang.payment.data.local.entity.UserEntity currentUser;
    @org.jetbrains.annotations.NotNull()
    private final java.text.SimpleDateFormat dateFormat = null;
    
    public UserProfileActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    protected void onResume() {
    }
    
    private final void setupRecyclerView() {
    }
    
    private final void setupClickListeners() {
    }
    
    /**
     * 加载用户数据
     */
    private final void loadUserData() {
    }
    
    /**
     * 显示用户信息
     */
    private final void displayUserInfo(com.v2ray.ang.payment.data.local.entity.UserEntity user) {
    }
    
    /**
     * 加载订单历史
     */
    private final void loadOrderHistory(java.lang.String userId) {
    }
    
    /**
     * 刷新数据（从服务器同步）
     */
    private final void refreshData() {
    }
    
    /**
     * 显示退出登录确认对话框
     */
    private final void showLogoutConfirmDialog() {
    }
    
    /**
     * 执行退出登录
     */
    private final void performLogout() {
    }
    
    /**
     * 显示订单详情
     */
    private final void showOrderDetail(com.v2ray.ang.payment.data.local.entity.OrderEntity order) {
    }
    
    private final java.lang.String getStatusText(java.lang.String status) {
        return null;
    }
    
    private final void showError(java.lang.String message) {
    }
    
    /**
     * 订单历史适配器
     */
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\b\u0086\u0004\u0018\u00002\u0010\u0012\f\u0012\n0\u0002R\u00060\u0000R\u00020\u00030\u0001:\u0001\u0017B\u001b\u0012\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005\u00a2\u0006\u0004\b\b\u0010\tJ\u0014\u0010\f\u001a\u00020\u00072\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00060\u000bJ \u0010\u000e\u001a\n0\u0002R\u00060\u0000R\u00020\u00032\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0016J \u0010\u0013\u001a\u00020\u00072\u000e\u0010\u0014\u001a\n0\u0002R\u00060\u0000R\u00020\u00032\u0006\u0010\u0015\u001a\u00020\u0012H\u0016J\b\u0010\u0016\u001a\u00020\u0012H\u0016R\u001a\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00060\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2 = {"Lcom/v2ray/ang/payment/ui/activity/UserProfileActivity$OrderHistoryAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/v2ray/ang/payment/ui/activity/UserProfileActivity$OrderHistoryAdapter$OrderViewHolder;", "Lcom/v2ray/ang/payment/ui/activity/UserProfileActivity;", "onItemClick", "Lkotlin/Function1;", "Lcom/v2ray/ang/payment/data/local/entity/OrderEntity;", "", "<init>", "(Lcom/v2ray/ang/payment/ui/activity/UserProfileActivity;Lkotlin/jvm/functions/Function1;)V", "orders", "", "submitList", "newOrders", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "", "onBindViewHolder", "holder", "position", "getItemCount", "OrderViewHolder", "app_playstoreDebug"})
    public final class OrderHistoryAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.v2ray.ang.payment.ui.activity.UserProfileActivity.OrderHistoryAdapter.OrderViewHolder> {
        @org.jetbrains.annotations.NotNull()
        private final kotlin.jvm.functions.Function1<com.v2ray.ang.payment.data.local.entity.OrderEntity, kotlin.Unit> onItemClick = null;
        @org.jetbrains.annotations.NotNull()
        private java.util.List<com.v2ray.ang.payment.data.local.entity.OrderEntity> orders;
        
        public OrderHistoryAdapter(@org.jetbrains.annotations.NotNull()
        kotlin.jvm.functions.Function1<? super com.v2ray.ang.payment.data.local.entity.OrderEntity, kotlin.Unit> onItemClick) {
            super();
        }
        
        public final void submitList(@org.jetbrains.annotations.NotNull()
        java.util.List<com.v2ray.ang.payment.data.local.entity.OrderEntity> newOrders) {
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public com.v2ray.ang.payment.ui.activity.UserProfileActivity.OrderHistoryAdapter.OrderViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
        android.view.ViewGroup parent, int viewType) {
            return null;
        }
        
        @java.lang.Override()
        public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
        com.v2ray.ang.payment.ui.activity.UserProfileActivity.OrderHistoryAdapter.OrderViewHolder holder, int position) {
        }
        
        @java.lang.Override()
        public int getItemCount() {
            return 0;
        }
        
        @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0012"}, d2 = {"Lcom/v2ray/ang/payment/ui/activity/UserProfileActivity$OrderHistoryAdapter$OrderViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "itemView", "Landroid/view/View;", "<init>", "(Lcom/v2ray/ang/payment/ui/activity/UserProfileActivity$OrderHistoryAdapter;Landroid/view/View;)V", "cardView", "Lcom/google/android/material/card/MaterialCardView;", "textPlanName", "Landroid/widget/TextView;", "textOrderNo", "textAmount", "textStatus", "textDate", "bind", "", "order", "Lcom/v2ray/ang/payment/data/local/entity/OrderEntity;", "app_playstoreDebug"})
        public final class OrderViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            @org.jetbrains.annotations.NotNull()
            private final com.google.android.material.card.MaterialCardView cardView = null;
            @org.jetbrains.annotations.NotNull()
            private final android.widget.TextView textPlanName = null;
            @org.jetbrains.annotations.NotNull()
            private final android.widget.TextView textOrderNo = null;
            @org.jetbrains.annotations.NotNull()
            private final android.widget.TextView textAmount = null;
            @org.jetbrains.annotations.NotNull()
            private final android.widget.TextView textStatus = null;
            @org.jetbrains.annotations.NotNull()
            private final android.widget.TextView textDate = null;
            
            public OrderViewHolder(@org.jetbrains.annotations.NotNull()
            android.view.View itemView) {
                super(null);
            }
            
            public final void bind(@org.jetbrains.annotations.NotNull()
            com.v2ray.ang.payment.data.local.entity.OrderEntity order) {
            }
        }
    }
}