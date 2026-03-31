package com.v2ray.ang.enums;

/**
 * Permission types used in the app, handling API level differences.
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0007\u001a\u00020\bH&J\u0006\u0010\t\u001a\u00020\bj\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\n"}, d2 = {"Lcom/v2ray/ang/enums/PermissionType;", "", "<init>", "(Ljava/lang/String;I)V", "CAMERA", "READ_STORAGE", "POST_NOTIFICATIONS", "getPermission", "", "getLabel", "app_fdroidDebug"})
public enum PermissionType {
    /*public static final*/ CAMERA /* = new @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00ca\u0001\u0018\u00002\u00020\u0000J\b\u0010\u0001\u001a\u00020\u0002H\u0016\u00a8\u0006\u0003"}, d2 = {"Lcom/v2ray/ang/enums/PermissionType;", "getPermission", "", "app_fdroidDebug"}) CAMERA(){
} */,
    /*public static final*/ READ_STORAGE /* = new @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00ca\u0001\u0018\u00002\u00020\u0000J\b\u0010\u0001\u001a\u00020\u0002H\u0016\u00a8\u0006\u0003"}, d2 = {"Lcom/v2ray/ang/enums/PermissionType;", "getPermission", "", "app_fdroidDebug"}) READ_STORAGE(){
} */,
    /*public static final*/ POST_NOTIFICATIONS /* = new @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00ca\u0001\u0018\u00002\u00020\u0000J\b\u0010\u0001\u001a\u00020\u0002H\u0017\u00a8\u0006\u0003"}, d2 = {"Lcom/v2ray/ang/enums/PermissionType;", "getPermission", "", "app_fdroidDebug"}) POST_NOTIFICATIONS(){
} */;
    
    PermissionType() {
    }
    
    /**
     * Return the actual Android permission string
     */
    @org.jetbrains.annotations.NotNull()
    public abstract java.lang.String getPermission();
    
    /**
     * Return a human-readable label for the permission
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getLabel() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.v2ray.ang.enums.PermissionType> getEntries() {
        return null;
    }
}