package com.v2ray.ang.service;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u00012\u00020\u0002B\u0007\u00a2\u0006\u0004\b\u0003\u0010\u0004J\b\u0010\u0005\u001a\u00020\u0006H\u0016J\"\u0010\u0007\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u000b\u001a\u00020\b2\u0006\u0010\f\u001a\u00020\bH\u0016J\b\u0010\r\u001a\u00020\u0006H\u0016J\b\u0010\u000e\u001a\u00020\u0001H\u0016J\b\u0010\u000f\u001a\u00020\u0006H\u0016J\b\u0010\u0010\u001a\u00020\u0006H\u0016J\u0010\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\bH\u0016J\u0014\u0010\u0014\u001a\u0004\u0018\u00010\u00152\b\u0010\t\u001a\u0004\u0018\u00010\nH\u0016J\u0012\u0010\u0016\u001a\u00020\u00062\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018H\u0014\u00a8\u0006\u0019"}, d2 = {"Lcom/v2ray/ang/service/V2RayProxyOnlyService;", "Landroid/app/Service;", "Lcom/v2ray/ang/contracts/ServiceControl;", "<init>", "()V", "onCreate", "", "onStartCommand", "", "intent", "Landroid/content/Intent;", "flags", "startId", "onDestroy", "getService", "startService", "stopService", "vpnProtect", "", "socket", "onBind", "Landroid/os/IBinder;", "attachBaseContext", "newBase", "Landroid/content/Context;", "app_playstoreDebug"})
public final class V2RayProxyOnlyService extends android.app.Service implements com.v2ray.ang.contracts.ServiceControl {
    
    public V2RayProxyOnlyService() {
        super();
    }
    
    /**
     * Initializes the service.
     */
    @java.lang.Override()
    public void onCreate() {
    }
    
    /**
     * Handles the start command for the service.
     * @param intent The intent.
     * @param flags The flags.
     * @param startId The start ID.
     * @return The start mode.
     */
    @java.lang.Override()
    public int onStartCommand(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent, int flags, int startId) {
        return 0;
    }
    
    /**
     * Destroys the service.
     */
    @java.lang.Override()
    public void onDestroy() {
    }
    
    /**
     * Gets the service instance.
     * @return The service instance.
     */
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public android.app.Service getService() {
        return null;
    }
    
    /**
     * Starts the service.
     */
    @java.lang.Override()
    public void startService() {
    }
    
    /**
     * Stops the service.
     */
    @java.lang.Override()
    public void stopService() {
    }
    
    /**
     * Protects the VPN socket.
     * @param socket The socket to protect.
     * @return True if the socket is protected, false otherwise.
     */
    @java.lang.Override()
    public boolean vpnProtect(int socket) {
        return false;
    }
    
    /**
     * Binds the service.
     * @param intent The intent.
     * @return The binder.
     */
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public android.os.IBinder onBind(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent) {
        return null;
    }
    
    /**
     * Attaches the base context to the service.
     * @param newBase The new base context.
     */
    @java.lang.Override()
    protected void attachBaseContext(@org.jetbrains.annotations.Nullable()
    android.content.Context newBase) {
    }
}