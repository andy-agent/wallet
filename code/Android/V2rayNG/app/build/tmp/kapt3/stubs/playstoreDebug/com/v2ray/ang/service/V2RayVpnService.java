package com.v2ray.ang.service;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000h\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0007\u0018\u00002\u00020\u00012\u00020\u0002B\u0007\u00a2\u0006\u0004\b\u0003\u0010\u0004J\b\u0010\u001c\u001a\u00020\u001dH\u0016J\b\u0010\u001e\u001a\u00020\u001dH\u0016J\b\u0010\u001f\u001a\u00020\u001dH\u0016J\"\u0010 \u001a\u00020!2\b\u0010\"\u001a\u0004\u0018\u00010#2\u0006\u0010$\u001a\u00020!2\u0006\u0010%\u001a\u00020!H\u0016J\b\u0010&\u001a\u00020\'H\u0016J\b\u0010(\u001a\u00020\u001dH\u0016J\b\u0010)\u001a\u00020\u001dH\u0016J\u0010\u0010*\u001a\u00020\b2\u0006\u0010+\u001a\u00020!H\u0016J\u0012\u0010,\u001a\u00020\u001d2\b\u0010-\u001a\u0004\u0018\u00010.H\u0014J\b\u0010/\u001a\u00020\u001dH\u0002J\b\u00100\u001a\u00020\bH\u0002J\u0014\u00101\u001a\u00020\u001d2\n\u00102\u001a\u000603R\u00020\u0001H\u0002J\u0014\u00104\u001a\u00020\u001d2\n\u00102\u001a\u000603R\u00020\u0001H\u0002J\u0014\u00105\u001a\u00020\u001d2\n\u00102\u001a\u000603R\u00020\u0001H\u0002J\b\u00106\u001a\u00020\u001dH\u0002J\u0012\u00107\u001a\u00020\u001d2\b\b\u0002\u00108\u001a\u00020\bH\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R#\u0010\u000b\u001a\n \r*\u0004\u0018\u00010\f0\f8BX\u0083\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0010\u0010\u0011\u001a\u0004\b\u000e\u0010\u000fR\u001b\u0010\u0012\u001a\u00020\u00138BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0016\u0010\u0011\u001a\u0004\b\u0014\u0010\u0015R\u001b\u0010\u0017\u001a\u00020\u00188BX\u0083\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001b\u0010\u0011\u001a\u0004\b\u0019\u0010\u001a\u00a8\u00069"}, d2 = {"Lcom/v2ray/ang/service/V2RayVpnService;", "Landroid/net/VpnService;", "Lcom/v2ray/ang/contracts/ServiceControl;", "<init>", "()V", "mInterface", "Landroid/os/ParcelFileDescriptor;", "isRunning", "", "tun2SocksService", "Lcom/v2ray/ang/contracts/Tun2SocksControl;", "defaultNetworkRequest", "Landroid/net/NetworkRequest;", "kotlin.jvm.PlatformType", "getDefaultNetworkRequest", "()Landroid/net/NetworkRequest;", "defaultNetworkRequest$delegate", "Lkotlin/Lazy;", "connectivity", "Landroid/net/ConnectivityManager;", "getConnectivity", "()Landroid/net/ConnectivityManager;", "connectivity$delegate", "defaultNetworkCallback", "Landroid/net/ConnectivityManager$NetworkCallback;", "getDefaultNetworkCallback", "()Landroid/net/ConnectivityManager$NetworkCallback;", "defaultNetworkCallback$delegate", "onCreate", "", "onRevoke", "onDestroy", "onStartCommand", "", "intent", "Landroid/content/Intent;", "flags", "startId", "getService", "Landroid/app/Service;", "startService", "stopService", "vpnProtect", "socket", "attachBaseContext", "newBase", "Landroid/content/Context;", "setupVpnService", "configureVpnService", "configureNetworkSettings", "builder", "Landroid/net/VpnService$Builder;", "configurePlatformFeatures", "configurePerAppProxy", "runTun2socks", "stopAllService", "isForced", "app_playstoreDebug"})
@android.annotation.SuppressLint(value = {"VpnServicePolicy"})
public final class V2RayVpnService extends android.net.VpnService implements com.v2ray.ang.contracts.ServiceControl {
    private android.os.ParcelFileDescriptor mInterface;
    private boolean isRunning = false;
    @org.jetbrains.annotations.Nullable()
    private com.v2ray.ang.contracts.Tun2SocksControl tun2SocksService;
    
    /**
     * destroy
     * Unfortunately registerDefaultNetworkCallback is going to return our VPN interface: https://android.googlesource.com/platform/frameworks/base/+/dda156ab0c5d66ad82bdcf76cda07cbc0a9c8a2e
     *
     * This makes doing a requestNetwork with REQUEST necessary so that we don't get ALL possible networks that
     * satisfies default network capabilities but only THE default network. Unfortunately we need to have
     * android.permission.CHANGE_NETWORK_STATE to be able to call requestNetwork.
     *
     * Source: https://android.googlesource.com/platform/frameworks/base/+/2df4c7d/services/core/java/com/android/server/ConnectivityService.java#887
     */
    @androidx.annotation.RequiresApi(value = android.os.Build.VERSION_CODES.P)
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy defaultNetworkRequest$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy connectivity$delegate = null;
    @androidx.annotation.RequiresApi(value = android.os.Build.VERSION_CODES.P)
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy defaultNetworkCallback$delegate = null;
    
    public V2RayVpnService() {
        super();
    }
    
    /**
     * destroy
     * Unfortunately registerDefaultNetworkCallback is going to return our VPN interface: https://android.googlesource.com/platform/frameworks/base/+/dda156ab0c5d66ad82bdcf76cda07cbc0a9c8a2e
     *
     * This makes doing a requestNetwork with REQUEST necessary so that we don't get ALL possible networks that
     * satisfies default network capabilities but only THE default network. Unfortunately we need to have
     * android.permission.CHANGE_NETWORK_STATE to be able to call requestNetwork.
     *
     * Source: https://android.googlesource.com/platform/frameworks/base/+/2df4c7d/services/core/java/com/android/server/ConnectivityService.java#887
     */
    private final android.net.NetworkRequest getDefaultNetworkRequest() {
        return null;
    }
    
    private final android.net.ConnectivityManager getConnectivity() {
        return null;
    }
    
    private final android.net.ConnectivityManager.NetworkCallback getDefaultNetworkCallback() {
        return null;
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
    
    @java.lang.Override()
    public void onRevoke() {
    }
    
    @java.lang.Override()
    public void onDestroy() {
    }
    
    @java.lang.Override()
    public int onStartCommand(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent, int flags, int startId) {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public android.app.Service getService() {
        return null;
    }
    
    @java.lang.Override()
    public void startService() {
    }
    
    @java.lang.Override()
    public void stopService() {
    }
    
    @java.lang.Override()
    public boolean vpnProtect(int socket) {
        return false;
    }
    
    @java.lang.Override()
    protected void attachBaseContext(@org.jetbrains.annotations.Nullable()
    android.content.Context newBase) {
    }
    
    /**
     * Sets up the VPN service.
     * Prepares the VPN and configures it if preparation is successful.
     */
    private final void setupVpnService() {
    }
    
    /**
     * Configures the VPN service.
     * @return True if the VPN service was configured successfully, false otherwise.
     */
    private final boolean configureVpnService() {
        return false;
    }
    
    /**
     * Configures the basic network settings for the VPN.
     * This includes IP addresses, routing rules, and DNS servers.
     *
     * @param builder The VPN Builder to configure
     */
    private final void configureNetworkSettings(android.net.VpnService.Builder builder) {
    }
    
    /**
     * Configures platform-specific VPN features for different Android versions.
     *
     * @param builder The VPN Builder to configure
     */
    private final void configurePlatformFeatures(android.net.VpnService.Builder builder) {
    }
    
    /**
     * Configures per-app proxy rules for the VPN builder.
     *
     * - If per-app proxy is not enabled, disallow the VPN service's own package.
     * - If no apps are selected, disallow the VPN service's own package.
     * - If bypass mode is enabled, disallow all selected apps (including self).
     * - If proxy mode is enabled, only allow the selected apps (excluding self).
     *
     * @param builder The VPN Builder to configure.
     */
    private final void configurePerAppProxy(android.net.VpnService.Builder builder) {
    }
    
    /**
     * Runs the tun2socks process.
     * Starts the tun2socks process with the appropriate parameters.
     */
    private final void runTun2socks() {
    }
    
    private final void stopAllService(boolean isForced) {
    }
}