package com.v2ray.ang.service;

/**
 * Manages the tun2socks process that handles VPN traffic
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0003\u0018\u0000 \u00112\u00020\u0001:\u0001\u0011B3\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0007\u00a2\u0006\u0004\b\u000b\u0010\fJ\b\u0010\r\u001a\u00020\nH\u0016J\b\u0010\u000e\u001a\u00020\u000fH\u0002J\b\u0010\u0010\u001a\u00020\nH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0012"}, d2 = {"Lcom/v2ray/ang/service/TProxyService;", "Lcom/v2ray/ang/contracts/Tun2SocksControl;", "context", "Landroid/content/Context;", "vpnInterface", "Landroid/os/ParcelFileDescriptor;", "isRunningProvider", "Lkotlin/Function0;", "", "restartCallback", "", "<init>", "(Landroid/content/Context;Landroid/os/ParcelFileDescriptor;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;)V", "startTun2Socks", "buildConfig", "", "stopTun2Socks", "Companion", "app_fdroidDebug"})
public final class TProxyService implements com.v2ray.ang.contracts.Tun2SocksControl {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final android.os.ParcelFileDescriptor vpnInterface = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.jvm.functions.Function0<java.lang.Boolean> isRunningProvider = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.jvm.functions.Function0<kotlin.Unit> restartCallback = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.service.TProxyService.Companion Companion = null;
    
    public TProxyService(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.os.ParcelFileDescriptor vpnInterface, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<java.lang.Boolean> isRunningProvider, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> restartCallback) {
        super();
    }
    
    @kotlin.jvm.JvmStatic()
    @kotlin.Suppress(names = {"FunctionName"})
    private static final native void TProxyStartService(java.lang.String configPath, int fd) {
    }
    
    @kotlin.jvm.JvmStatic()
    @kotlin.Suppress(names = {"FunctionName"})
    private static final native void TProxyStopService() {
    }
    
    @kotlin.jvm.JvmStatic()
    @kotlin.Suppress(names = {"FunctionName"})
    private static final native long[] TProxyGetStats() {
        return null;
    }
    
    /**
     * Starts the tun2socks process with the appropriate parameters.
     */
    @java.lang.Override()
    public void startTun2Socks() {
    }
    
    private final java.lang.String buildConfig() {
        return null;
    }
    
    /**
     * Stops the tun2socks process
     */
    @java.lang.Override()
    public void stopTun2Socks() {
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0016\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0019\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0083 J\t\u0010\n\u001a\u00020\u0005H\u0083 J\u000b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0083 \u00a8\u0006\r"}, d2 = {"Lcom/v2ray/ang/service/TProxyService$Companion;", "", "<init>", "()V", "TProxyStartService", "", "configPath", "", "fd", "", "TProxyStopService", "TProxyGetStats", "", "app_fdroidDebug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @kotlin.jvm.JvmStatic()
        @kotlin.Suppress(names = {"FunctionName"})
        private final void TProxyStartService(java.lang.String configPath, int fd) {
        }
        
        @kotlin.jvm.JvmStatic()
        @kotlin.Suppress(names = {"FunctionName"})
        private final void TProxyStopService() {
        }
        
        @kotlin.jvm.JvmStatic()
        @kotlin.Suppress(names = {"FunctionName"})
        private final long[] TProxyGetStats() {
            return null;
        }
    }
}