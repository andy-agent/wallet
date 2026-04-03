package com.v2ray.ang.service;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001:\u0001\rB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\b\u0010\b\u001a\u00020\u0005H\u0016J\b\u0010\t\u001a\u00020\u0005H\u0016J\b\u0010\n\u001a\u00020\u0005H\u0016R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/v2ray/ang/service/QSTileService;", "Landroid/service/quicksettings/TileService;", "<init>", "()V", "setState", "", "state", "", "onStartListening", "onStopListening", "onClick", "mMsgReceive", "Landroid/content/BroadcastReceiver;", "ReceiveMessageHandler", "app_playstoreDebug"})
public final class QSTileService extends android.service.quicksettings.TileService {
    @org.jetbrains.annotations.Nullable()
    private android.content.BroadcastReceiver mMsgReceive;
    
    public QSTileService() {
        super();
    }
    
    /**
     * Sets the state of the tile.
     * @param state The state to set.
     */
    public final void setState(int state) {
    }
    
    /**
     * Refer to the official documentation for [registerReceiver](https://developer.android.com/reference/androidx/core/content/ContextCompat#registerReceiver(android.content.Context,android.content.BroadcastReceiver,android.content.IntentFilter,int):
     * `registerReceiver(Context, BroadcastReceiver, IntentFilter, int)`.
     */
    @java.lang.Override()
    public void onStartListening() {
    }
    
    /**
     * Called when the tile stops listening.
     */
    @java.lang.Override()
    public void onStopListening() {
    }
    
    /**
     * Called when the tile is clicked.
     */
    @java.lang.Override()
    public void onClick() {
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u001c\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0016R \u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000b\u00a8\u0006\u0012"}, d2 = {"Lcom/v2ray/ang/service/QSTileService$ReceiveMessageHandler;", "Landroid/content/BroadcastReceiver;", "context", "Lcom/v2ray/ang/service/QSTileService;", "<init>", "(Lcom/v2ray/ang/service/QSTileService;)V", "mReference", "Ljava/lang/ref/SoftReference;", "getMReference", "()Ljava/lang/ref/SoftReference;", "setMReference", "(Ljava/lang/ref/SoftReference;)V", "onReceive", "", "ctx", "Landroid/content/Context;", "intent", "Landroid/content/Intent;", "app_playstoreDebug"})
    static final class ReceiveMessageHandler extends android.content.BroadcastReceiver {
        @org.jetbrains.annotations.NotNull()
        private java.lang.ref.SoftReference<com.v2ray.ang.service.QSTileService> mReference;
        
        public ReceiveMessageHandler(@org.jetbrains.annotations.NotNull()
        com.v2ray.ang.service.QSTileService context) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.ref.SoftReference<com.v2ray.ang.service.QSTileService> getMReference() {
            return null;
        }
        
        public final void setMReference(@org.jetbrains.annotations.NotNull()
        java.lang.ref.SoftReference<com.v2ray.ang.service.QSTileService> p0) {
        }
        
        @java.lang.Override()
        public void onReceive(@org.jetbrains.annotations.Nullable()
        android.content.Context ctx, @org.jetbrains.annotations.Nullable()
        android.content.Intent intent) {
        }
    }
}