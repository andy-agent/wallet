package com.v2ray.ang.viewmodel;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\bJ\u0006\u0010\t\u001a\u00020\nJ\u000e\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eJ\u0016\u0010\u000f\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u0011J\u0016\u0010\u0012\u001a\u00020\n2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0014R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/v2ray/ang/viewmodel/SubscriptionsViewModel;", "Landroidx/lifecycle/ViewModel;", "<init>", "()V", "subscriptions", "", "Lcom/v2ray/ang/dto/SubscriptionCache;", "getAll", "", "reload", "", "remove", "", "subId", "", "update", "item", "Lcom/v2ray/ang/dto/SubscriptionItem;", "swap", "fromPosition", "", "toPosition", "app_fdroidDebug"})
public final class SubscriptionsViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.v2ray.ang.dto.SubscriptionCache> subscriptions = null;
    
    public SubscriptionsViewModel() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.v2ray.ang.dto.SubscriptionCache> getAll() {
        return null;
    }
    
    public final void reload() {
    }
    
    public final boolean remove(@org.jetbrains.annotations.NotNull()
    java.lang.String subId) {
        return false;
    }
    
    public final void update(@org.jetbrains.annotations.NotNull()
    java.lang.String subId, @org.jetbrains.annotations.NotNull()
    com.v2ray.ang.dto.SubscriptionItem item) {
    }
    
    public final void swap(int fromPosition, int toPosition) {
    }
}