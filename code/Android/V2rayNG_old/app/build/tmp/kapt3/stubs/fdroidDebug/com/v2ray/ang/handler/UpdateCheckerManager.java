package com.v2ray.ang.handler;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u0086@\u00a2\u0006\u0002\u0010\bJ \u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\u000fJ\u0018\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u000e2\u0006\u0010\u0013\u001a\u00020\u000eH\u0002J\u0018\u0010\u0014\u001a\u00020\u000e2\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u000eH\u0002\u00a8\u0006\u0018"}, d2 = {"Lcom/v2ray/ang/handler/UpdateCheckerManager;", "", "<init>", "()V", "checkForUpdate", "Lcom/v2ray/ang/dto/CheckUpdateResult;", "includePreRelease", "", "(ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "downloadApk", "Ljava/io/File;", "context", "Landroid/content/Context;", "downloadUrl", "", "(Landroid/content/Context;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "compareVersions", "", "version1", "version2", "getDownloadUrl", "release", "Lcom/v2ray/ang/dto/GitHubRelease;", "abi", "app_fdroidDebug"})
public final class UpdateCheckerManager {
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.handler.UpdateCheckerManager INSTANCE = null;
    
    private UpdateCheckerManager() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object checkForUpdate(boolean includePreRelease, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.v2ray.ang.dto.CheckUpdateResult> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object downloadApk(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String downloadUrl, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.io.File> $completion) {
        return null;
    }
    
    private final int compareVersions(java.lang.String version1, java.lang.String version2) {
        return 0;
    }
    
    private final java.lang.String getDownloadUrl(com.v2ray.ang.dto.GitHubRelease release, java.lang.String abi) {
        return null;
    }
}