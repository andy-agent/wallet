package com.v2ray.ang.helper;

/**
 * PreferenceDataStore implementation that bridges AndroidX Preference framework to MMKV storage.
 * This ensures that all Preference UI operations read/write directly from/to MMKV,
 * avoiding inconsistencies between SharedPreferences and MMKV.
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010#\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u001a\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\b\u001a\u0004\u0018\u00010\u0007H\u0016J\u001c\u0010\t\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\n\u001a\u0004\u0018\u00010\u0007H\u0016J\u0018\u0010\u000b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\fH\u0016J\u0018\u0010\r\u001a\u00020\f2\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\fH\u0016J\u0018\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u000fH\u0016J\u0018\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\u000fH\u0016J\u0018\u0010\u0011\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0012H\u0016J\u0018\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\u0012H\u0016J\u0018\u0010\u0014\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0015H\u0016J\u0018\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\u0015H\u0016J \u0010\u0017\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u000e\u0010\u0018\u001a\n\u0012\u0004\u0012\u00020\u0007\u0018\u00010\u0019H\u0016J(\u0010\u001a\u001a\n\u0012\u0004\u0012\u00020\u0007\u0018\u00010\u00192\u0006\u0010\u0006\u001a\u00020\u00072\u000e\u0010\u001b\u001a\n\u0012\u0004\u0012\u00020\u0007\u0018\u00010\u0019H\u0016J\u0010\u0010\u001c\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0002\u00a8\u0006\u001d"}, d2 = {"Lcom/v2ray/ang/helper/MmkvPreferenceDataStore;", "Landroidx/preference/PreferenceDataStore;", "<init>", "()V", "putString", "", "key", "", "value", "getString", "defaultValue", "putInt", "", "getInt", "putLong", "", "getLong", "putFloat", "", "getFloat", "putBoolean", "", "getBoolean", "putStringSet", "values", "", "getStringSet", "defaultValues", "notifySettingChanged", "app_playstoreDebug"})
public final class MmkvPreferenceDataStore extends androidx.preference.PreferenceDataStore {
    
    public MmkvPreferenceDataStore() {
        super();
    }
    
    @java.lang.Override()
    public void putString(@org.jetbrains.annotations.NotNull()
    java.lang.String key, @org.jetbrains.annotations.Nullable()
    java.lang.String value) {
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.String getString(@org.jetbrains.annotations.NotNull()
    java.lang.String key, @org.jetbrains.annotations.Nullable()
    java.lang.String defaultValue) {
        return null;
    }
    
    @java.lang.Override()
    public void putInt(@org.jetbrains.annotations.NotNull()
    java.lang.String key, int value) {
    }
    
    @java.lang.Override()
    public int getInt(@org.jetbrains.annotations.NotNull()
    java.lang.String key, int defaultValue) {
        return 0;
    }
    
    @java.lang.Override()
    public void putLong(@org.jetbrains.annotations.NotNull()
    java.lang.String key, long value) {
    }
    
    @java.lang.Override()
    public long getLong(@org.jetbrains.annotations.NotNull()
    java.lang.String key, long defaultValue) {
        return 0L;
    }
    
    @java.lang.Override()
    public void putFloat(@org.jetbrains.annotations.NotNull()
    java.lang.String key, float value) {
    }
    
    @java.lang.Override()
    public float getFloat(@org.jetbrains.annotations.NotNull()
    java.lang.String key, float defaultValue) {
        return 0.0F;
    }
    
    @java.lang.Override()
    public void putBoolean(@org.jetbrains.annotations.NotNull()
    java.lang.String key, boolean value) {
    }
    
    @java.lang.Override()
    public boolean getBoolean(@org.jetbrains.annotations.NotNull()
    java.lang.String key, boolean defaultValue) {
        return false;
    }
    
    @java.lang.Override()
    public void putStringSet(@org.jetbrains.annotations.NotNull()
    java.lang.String key, @org.jetbrains.annotations.Nullable()
    java.util.Set<java.lang.String> values) {
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.util.Set<java.lang.String> getStringSet(@org.jetbrains.annotations.NotNull()
    java.lang.String key, @org.jetbrains.annotations.Nullable()
    java.util.Set<java.lang.String> defaultValues) {
        return null;
    }
    
    private final void notifySettingChanged(java.lang.String key) {
    }
}