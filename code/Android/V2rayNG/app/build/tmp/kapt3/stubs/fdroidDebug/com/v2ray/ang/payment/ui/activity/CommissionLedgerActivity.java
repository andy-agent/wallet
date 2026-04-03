package com.v2ray.ang.payment.ui.activity;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\u0018\u00002\u00020\u0001:\u0001\u001cB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u0014J\b\u0010\u000e\u001a\u00020\u000bH\u0014J\b\u0010\u000f\u001a\u00020\u000bH\u0002J\b\u0010\u0010\u001a\u00020\u000bH\u0002J\b\u0010\u0011\u001a\u00020\u000bH\u0002J\u0010\u0010\u0012\u001a\u00020\u000b2\u0006\u0010\u0013\u001a\u00020\u0014H\u0002J\u0016\u0010\u0015\u001a\u00020\u000b2\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00180\u0017H\u0002J\u0010\u0010\u0019\u001a\u00020\u000b2\u0006\u0010\u001a\u001a\u00020\u001bH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082.\u00a2\u0006\u0002\n\u0000R\u0012\u0010\b\u001a\u00060\tR\u00020\u0000X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001d"}, d2 = {"Lcom/v2ray/ang/payment/ui/activity/CommissionLedgerActivity;", "Lcom/v2ray/ang/ui/BaseActivity;", "<init>", "()V", "binding", "Lcom/v2ray/ang/databinding/ActivityCommissionLedgerBinding;", "paymentRepository", "Lcom/v2ray/ang/payment/data/repository/PaymentRepository;", "ledgerAdapter", "Lcom/v2ray/ang/payment/ui/activity/CommissionLedgerActivity$CommissionLedgerAdapter;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "setupRecyclerView", "setupClickListeners", "loadData", "displaySummary", "data", "Lcom/v2ray/ang/payment/data/api/CommissionSummaryData;", "displayLedger", "items", "", "Lcom/v2ray/ang/payment/data/api/CommissionLedgerItem;", "showError", "message", "", "CommissionLedgerAdapter", "app_fdroidDebug"})
public final class CommissionLedgerActivity extends com.v2ray.ang.ui.BaseActivity {
    private com.v2ray.ang.databinding.ActivityCommissionLedgerBinding binding;
    private com.v2ray.ang.payment.data.repository.PaymentRepository paymentRepository;
    private com.v2ray.ang.payment.ui.activity.CommissionLedgerActivity.CommissionLedgerAdapter ledgerAdapter;
    
    public CommissionLedgerActivity() {
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
    
    private final void loadData() {
    }
    
    private final void displaySummary(com.v2ray.ang.payment.data.api.CommissionSummaryData data) {
    }
    
    private final void displayLedger(java.util.List<com.v2ray.ang.payment.data.api.CommissionLedgerItem> items) {
    }
    
    private final void showError(java.lang.String message) {
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\b\u0086\u0004\u0018\u00002\u0010\u0012\f\u0012\n0\u0002R\u00060\u0000R\u00020\u00030\u0001:\u0001\u0015B\u0007\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0014\u0010\t\u001a\u00020\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\b0\u0007J \u0010\f\u001a\n0\u0002R\u00060\u0000R\u00020\u00032\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0016J \u0010\u0011\u001a\u00020\n2\u000e\u0010\u0012\u001a\n0\u0002R\u00060\u0000R\u00020\u00032\u0006\u0010\u0013\u001a\u00020\u0010H\u0016J\b\u0010\u0014\u001a\u00020\u0010H\u0016R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/v2ray/ang/payment/ui/activity/CommissionLedgerActivity$CommissionLedgerAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/v2ray/ang/payment/ui/activity/CommissionLedgerActivity$CommissionLedgerAdapter$ViewHolder;", "Lcom/v2ray/ang/payment/ui/activity/CommissionLedgerActivity;", "<init>", "(Lcom/v2ray/ang/payment/ui/activity/CommissionLedgerActivity;)V", "items", "", "Lcom/v2ray/ang/payment/data/api/CommissionLedgerItem;", "submitList", "", "newItems", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "", "onBindViewHolder", "holder", "position", "getItemCount", "ViewHolder", "app_fdroidDebug"})
    public final class CommissionLedgerAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.v2ray.ang.payment.ui.activity.CommissionLedgerActivity.CommissionLedgerAdapter.ViewHolder> {
        @org.jetbrains.annotations.NotNull()
        private java.util.List<com.v2ray.ang.payment.data.api.CommissionLedgerItem> items;
        
        public CommissionLedgerAdapter() {
            super();
        }
        
        public final void submitList(@org.jetbrains.annotations.NotNull()
        java.util.List<com.v2ray.ang.payment.data.api.CommissionLedgerItem> newItems) {
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public com.v2ray.ang.payment.ui.activity.CommissionLedgerActivity.CommissionLedgerAdapter.ViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
        android.view.ViewGroup parent, int viewType) {
            return null;
        }
        
        @java.lang.Override()
        public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
        com.v2ray.ang.payment.ui.activity.CommissionLedgerActivity.CommissionLedgerAdapter.ViewHolder holder, int position) {
        }
        
        @java.lang.Override()
        public int getItemCount() {
            return 0;
        }
        
        @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010J\u0010\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0012H\u0002R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/v2ray/ang/payment/ui/activity/CommissionLedgerActivity$CommissionLedgerAdapter$ViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "itemView", "Landroid/view/View;", "<init>", "(Lcom/v2ray/ang/payment/ui/activity/CommissionLedgerActivity$CommissionLedgerAdapter;Landroid/view/View;)V", "textEntryNo", "Landroid/widget/TextView;", "textSourceAccount", "textCommissionLevel", "textAmount", "textStatus", "textDate", "bind", "", "item", "Lcom/v2ray/ang/payment/data/api/CommissionLedgerItem;", "getStatusText", "", "status", "app_fdroidDebug"})
        public final class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            @org.jetbrains.annotations.NotNull()
            private final android.widget.TextView textEntryNo = null;
            @org.jetbrains.annotations.NotNull()
            private final android.widget.TextView textSourceAccount = null;
            @org.jetbrains.annotations.NotNull()
            private final android.widget.TextView textCommissionLevel = null;
            @org.jetbrains.annotations.NotNull()
            private final android.widget.TextView textAmount = null;
            @org.jetbrains.annotations.NotNull()
            private final android.widget.TextView textStatus = null;
            @org.jetbrains.annotations.NotNull()
            private final android.widget.TextView textDate = null;
            
            public ViewHolder(@org.jetbrains.annotations.NotNull()
            android.view.View itemView) {
                super(null);
            }
            
            public final void bind(@org.jetbrains.annotations.NotNull()
            com.v2ray.ang.payment.data.api.CommissionLedgerItem item) {
            }
            
            private final java.lang.String getStatusText(java.lang.String status) {
                return null;
            }
        }
    }
}