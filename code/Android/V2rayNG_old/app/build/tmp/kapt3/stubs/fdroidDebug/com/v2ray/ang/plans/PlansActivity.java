package com.v2ray.ang.plans;

/**
 * 套餐列表页面
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u0014J\b\u0010\u000e\u001a\u00020\u000bH\u0002J\b\u0010\u000f\u001a\u00020\u000bH\u0002J\u0010\u0010\u0010\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\u0010\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0016H\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Lcom/v2ray/ang/plans/PlansActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "<init>", "()V", "binding", "Lcom/v2ray/ang/databinding/ActivityPlansBinding;", "repository", "Lcom/v2ray/ang/payment/data/repository/PaymentRepository;", "adapter", "Lcom/v2ray/ang/plans/PlansAdapter;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "setupRecyclerView", "loadPlans", "onPlanSelected", "plan", "Lcom/v2ray/ang/payment/data/model/Plan;", "onOptionsItemSelected", "", "item", "Landroid/view/MenuItem;", "app_fdroidDebug"})
public final class PlansActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.v2ray.ang.databinding.ActivityPlansBinding binding;
    private com.v2ray.ang.payment.data.repository.PaymentRepository repository;
    private com.v2ray.ang.plans.PlansAdapter adapter;
    
    public PlansActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setupRecyclerView() {
    }
    
    private final void loadPlans() {
    }
    
    private final void onPlanSelected(com.v2ray.ang.payment.data.model.Plan plan) {
    }
    
    @java.lang.Override()
    public boolean onOptionsItemSelected(@org.jetbrains.annotations.NotNull()
    android.view.MenuItem item) {
        return false;
    }
}