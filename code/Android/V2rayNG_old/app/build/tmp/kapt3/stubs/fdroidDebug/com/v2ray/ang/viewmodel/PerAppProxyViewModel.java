package com.v2ray.ang.viewmodel;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010#\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u001e\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0006J\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00060\u000bJ\u000e\u0010\f\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0006J\u000e\u0010\r\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0006J\u000e\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\t\u001a\u00020\u0006J\u0014\u0010\u0010\u001a\u00020\u000f2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00060\u0012J\u0014\u0010\u0013\u001a\u00020\u000f2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00060\u0012J\u0006\u0010\u0014\u001a\u00020\u000fJ\b\u0010\u0015\u001a\u00020\u000fH\u0002R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/v2ray/ang/viewmodel/PerAppProxyViewModel;", "Landroidx/lifecycle/ViewModel;", "<init>", "()V", "blacklist", "", "", "contains", "", "packageName", "getAll", "", "add", "remove", "toggle", "", "addAll", "packages", "", "removeAll", "clear", "save", "app_fdroidDebug"})
public final class PerAppProxyViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final java.util.Set<java.lang.String> blacklist = null;
    
    public PerAppProxyViewModel() {
        super();
    }
    
    public final boolean contains(@org.jetbrains.annotations.NotNull()
    java.lang.String packageName) {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> getAll() {
        return null;
    }
    
    public final boolean add(@org.jetbrains.annotations.NotNull()
    java.lang.String packageName) {
        return false;
    }
    
    public final boolean remove(@org.jetbrains.annotations.NotNull()
    java.lang.String packageName) {
        return false;
    }
    
    public final void toggle(@org.jetbrains.annotations.NotNull()
    java.lang.String packageName) {
    }
    
    public final void addAll(@org.jetbrains.annotations.NotNull()
    java.util.Collection<java.lang.String> packages) {
    }
    
    public final void removeAll(@org.jetbrains.annotations.NotNull()
    java.util.Collection<java.lang.String> packages) {
    }
    
    public final void clear() {
    }
    
    private final void save() {
    }
}