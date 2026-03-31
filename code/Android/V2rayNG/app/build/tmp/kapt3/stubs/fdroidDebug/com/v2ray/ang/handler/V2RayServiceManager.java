package com.v2ray.ang.handler;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0002)*B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015J\u001a\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0014\u001a\u00020\u00152\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\u0019J\u000e\u0010\u001a\u001a\u00020\u00172\u0006\u0010\u0014\u001a\u00020\u0015J\u0006\u0010\u001b\u001a\u00020\u0013J\u0006\u0010\u001c\u001a\u00020\u0019J\u0010\u0010\u001d\u001a\u00020\u00172\u0006\u0010\u0014\u001a\u00020\u0015H\u0002J\u0010\u0010\u001e\u001a\u00020\u00132\b\u0010\u001f\u001a\u0004\u0018\u00010 J\u0006\u0010!\u001a\u00020\u0013J\u0016\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020\u00192\u0006\u0010%\u001a\u00020\u0019J\b\u0010&\u001a\u00020\u0017H\u0002J\n\u0010\'\u001a\u0004\u0018\u00010(H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R4\u0010\r\u001a\n\u0012\u0004\u0012\u00020\f\u0018\u00010\u000b2\u000e\u0010\n\u001a\n\u0012\u0004\u0012\u00020\f\u0018\u00010\u000b@FX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011\u00a8\u0006+"}, d2 = {"Lcom/v2ray/ang/handler/V2RayServiceManager;", "", "<init>", "()V", "coreController", "Llibv2ray/CoreController;", "mMsgReceive", "Lcom/v2ray/ang/handler/V2RayServiceManager$ReceiveMessageHandler;", "currentConfig", "Lcom/v2ray/ang/dto/ProfileItem;", "value", "Ljava/lang/ref/SoftReference;", "Lcom/v2ray/ang/contracts/ServiceControl;", "serviceControl", "getServiceControl", "()Ljava/lang/ref/SoftReference;", "setServiceControl", "(Ljava/lang/ref/SoftReference;)V", "startVServiceFromToggle", "", "context", "Landroid/content/Context;", "startVService", "", "guid", "", "stopVService", "isRunning", "getRunningServerName", "startContextService", "startCoreLoop", "vpnInterface", "Landroid/os/ParcelFileDescriptor;", "stopCoreLoop", "queryStats", "", "tag", "link", "measureV2rayDelay", "getService", "Landroid/app/Service;", "CoreCallback", "ReceiveMessageHandler", "app_fdroidDebug"})
public final class V2RayServiceManager {
    @org.jetbrains.annotations.NotNull()
    private static final libv2ray.CoreController coreController = null;
    @org.jetbrains.annotations.NotNull()
    private static final com.v2ray.ang.handler.V2RayServiceManager.ReceiveMessageHandler mMsgReceive = null;
    @org.jetbrains.annotations.Nullable()
    private static com.v2ray.ang.dto.ProfileItem currentConfig;
    @org.jetbrains.annotations.Nullable()
    private static java.lang.ref.SoftReference<com.v2ray.ang.contracts.ServiceControl> serviceControl;
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.handler.V2RayServiceManager INSTANCE = null;
    
    private V2RayServiceManager() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.ref.SoftReference<com.v2ray.ang.contracts.ServiceControl> getServiceControl() {
        return null;
    }
    
    public final void setServiceControl(@org.jetbrains.annotations.Nullable()
    java.lang.ref.SoftReference<com.v2ray.ang.contracts.ServiceControl> value) {
    }
    
    /**
     * Starts the V2Ray service from a toggle action.
     * @param context The context from which the service is started.
     * @return True if the service was started successfully, false otherwise.
     */
    public final boolean startVServiceFromToggle(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    /**
     * Starts the V2Ray service.
     * @param context The context from which the service is started.
     * @param guid The GUID of the server configuration to use (optional).
     */
    public final void startVService(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.Nullable()
    java.lang.String guid) {
    }
    
    /**
     * Stops the V2Ray service.
     * @param context The context from which the service is stopped.
     */
    public final void stopVService(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    /**
     * Checks if the V2Ray service is running.
     * @return True if the service is running, false otherwise.
     */
    public final boolean isRunning() {
        return false;
    }
    
    /**
     * Gets the name of the currently running server.
     * @return The name of the running server.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getRunningServerName() {
        return null;
    }
    
    /**
     * Starts the context service for V2Ray.
     * Chooses between VPN service or Proxy-only service based on user settings.
     * @param context The context from which the service is started.
     */
    private final void startContextService(android.content.Context context) {
    }
    
    /**
     * Refer to the official documentation for [registerReceiver](https://developer.android.com/reference/androidx/core/content/ContextCompat#registerReceiver(android.content.Context,android.content.BroadcastReceiver,android.content.IntentFilter,int):
     * `registerReceiver(Context, BroadcastReceiver, IntentFilter, int)`.
     * Starts the V2Ray core service.
     */
    public final boolean startCoreLoop(@org.jetbrains.annotations.Nullable()
    android.os.ParcelFileDescriptor vpnInterface) {
        return false;
    }
    
    /**
     * Stops the V2Ray core service.
     * Unregisters broadcast receivers, stops notifications, and shuts down plugins.
     * @return True if the core was stopped successfully, false otherwise.
     */
    public final boolean stopCoreLoop() {
        return false;
    }
    
    /**
     * Queries the statistics for a given tag and link.
     * @param tag The tag to query.
     * @param link The link to query.
     * @return The statistics value.
     */
    public final long queryStats(@org.jetbrains.annotations.NotNull()
    java.lang.String tag, @org.jetbrains.annotations.NotNull()
    java.lang.String link) {
        return 0L;
    }
    
    /**
     * Measures the connection delay for the current V2Ray configuration.
     * Tests with primary URL first, then falls back to alternative URL if needed.
     * Also fetches remote IP information if the delay test was successful.
     */
    private final void measureV2rayDelay() {
    }
    
    /**
     * Gets the current service instance.
     * @return The current service instance, or null if not available.
     */
    private final android.app.Service getService() {
        return null;
    }
    
    /**
     * Core callback handler implementation for handling V2Ray core events.
     * Handles startup, shutdown, socket protection, and status emission.
     */
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H\u0016J\b\u0010\u0006\u001a\u00020\u0005H\u0016J\u001a\u0010\u0007\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\u00052\b\u0010\t\u001a\u0004\u0018\u00010\nH\u0016\u00a8\u0006\u000b"}, d2 = {"Lcom/v2ray/ang/handler/V2RayServiceManager$CoreCallback;", "Llibv2ray/CoreCallbackHandler;", "<init>", "()V", "startup", "", "shutdown", "onEmitStatus", "l", "s", "", "app_fdroidDebug"})
    static final class CoreCallback implements libv2ray.CoreCallbackHandler {
        
        public CoreCallback() {
            super();
        }
        
        /**
         * Called when V2Ray core starts up.
         * @return 0 for success, any other value for failure.
         */
        @java.lang.Override()
        public long startup() {
            return 0L;
        }
        
        /**
         * Called when V2Ray core shuts down.
         * @return 0 for success, any other value for failure.
         */
        @java.lang.Override()
        public long shutdown() {
            return 0L;
        }
        
        /**
         * Called when V2Ray core emits status information.
         * @param l Status code.
         * @param s Status message.
         * @return Always returns 0.
         */
        @java.lang.Override()
        public long onEmitStatus(long l, @org.jetbrains.annotations.Nullable()
        java.lang.String s) {
            return 0L;
        }
    }
    
    /**
     * Broadcast receiver for handling messages sent to the service.
     * Handles registration, service control, and screen events.
     */
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u001c\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\b\u0010\b\u001a\u0004\u0018\u00010\tH\u0016\u00a8\u0006\n"}, d2 = {"Lcom/v2ray/ang/handler/V2RayServiceManager$ReceiveMessageHandler;", "Landroid/content/BroadcastReceiver;", "<init>", "()V", "onReceive", "", "ctx", "Landroid/content/Context;", "intent", "Landroid/content/Intent;", "app_fdroidDebug"})
    static final class ReceiveMessageHandler extends android.content.BroadcastReceiver {
        
        public ReceiveMessageHandler() {
            super();
        }
        
        /**
         * Handles received broadcast messages.
         * Processes service control messages and screen state changes.
         * @param ctx The context in which the receiver is running.
         * @param intent The intent being received.
         */
        @java.lang.Override()
        public void onReceive(@org.jetbrains.annotations.Nullable()
        android.content.Context ctx, @org.jetbrains.annotations.Nullable()
        android.content.Intent intent) {
        }
    }
}