package com.v2ray.ang.contracts;

/**
 * Interface that defines the control operations for tun2socks implementations.
 *
 * This interface is implemented by different tunnel solutions like:
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\b\u0010\u0004\u001a\u00020\u0003H&\u00a8\u0006\u0005\u00c0\u0006\u0003"}, d2 = {"Lcom/v2ray/ang/contracts/Tun2SocksControl;", "", "startTun2Socks", "", "stopTun2Socks", "app_playstoreDebug"})
public abstract interface Tun2SocksControl {
    
    /**
     * Starts the tun2socks process with the appropriate parameters.
     * This initializes the VPN tunnel and connects it to the SOCKS proxy.
     */
    public abstract void startTun2Socks();
    
    /**
     * Stops the tun2socks process and cleans up resources.
     */
    public abstract void stopTun2Socks();
}