package com.v2ray.ang.contracts;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\b\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0006\u001a\u00020\u0005H&J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH&\u00a8\u0006\u000b\u00c0\u0006\u0003"}, d2 = {"Lcom/v2ray/ang/contracts/ServiceControl;", "", "getService", "Landroid/app/Service;", "startService", "", "stopService", "vpnProtect", "", "socket", "", "app_fdroidDebug"})
public abstract interface ServiceControl {
    
    /**
     * Gets the service instance.
     * @return The service instance.
     */
    @org.jetbrains.annotations.NotNull()
    public abstract android.app.Service getService();
    
    /**
     * Starts the service.
     */
    public abstract void startService();
    
    /**
     * Stops the service.
     */
    public abstract void stopService();
    
    /**
     * Protects the VPN socket.
     * @param socket The socket to protect.
     * @return True if the socket is protected, false otherwise.
     */
    public abstract boolean vpnProtect(int socket);
}