package com.v2ray.ang.handler;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016J\u0010\u0010\u0017\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016J\u0006\u0010\u0018\u001a\u00020\u0014J\u0010\u0010\u0019\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016J\b\u0010\u001a\u001a\u00020\u001bH\u0003J\"\u0010\u001c\u001a\u00020\u00142\b\u0010\u001d\u001a\u0004\u0018\u00010\u001b2\u0006\u0010\u001e\u001a\u00020\u000b2\u0006\u0010\u001f\u001a\u00020\u000bH\u0002J\n\u0010 \u001a\u0004\u0018\u00010\u0012H\u0002J.\u0010!\u001a\u00020\u00142\n\u0010\"\u001a\u00060#j\u0002`$2\b\u0010%\u001a\u0004\u0018\u00010\u001b2\u0006\u0010&\u001a\u00020\'2\u0006\u0010(\u001a\u00020\'H\u0002J\n\u0010)\u001a\u0004\u0018\u00010*H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006+"}, d2 = {"Lcom/v2ray/ang/handler/NotificationManager;", "", "<init>", "()V", "NOTIFICATION_ID", "", "NOTIFICATION_PENDING_INTENT_CONTENT", "NOTIFICATION_PENDING_INTENT_STOP_V2RAY", "NOTIFICATION_PENDING_INTENT_RESTART_V2RAY", "NOTIFICATION_ICON_THRESHOLD", "QUERY_INTERVAL_MS", "", "lastQueryTime", "mBuilder", "Landroidx/core/app/NotificationCompat$Builder;", "speedNotificationJob", "Lkotlinx/coroutines/Job;", "mNotificationManager", "Landroid/app/NotificationManager;", "startSpeedNotification", "", "currentConfig", "Lcom/v2ray/ang/dto/ProfileItem;", "showNotification", "cancelNotification", "stopSpeedNotification", "createNotificationChannel", "", "updateNotification", "contentText", "proxyTraffic", "directTraffic", "getNotificationManager", "appendSpeedString", "text", "Ljava/lang/StringBuilder;", "Lkotlin/text/StringBuilder;", "name", "up", "", "down", "getService", "Landroid/app/Service;", "app_playstoreDebug"})
public final class NotificationManager {
    private static final int NOTIFICATION_ID = 1;
    private static final int NOTIFICATION_PENDING_INTENT_CONTENT = 0;
    private static final int NOTIFICATION_PENDING_INTENT_STOP_V2RAY = 1;
    private static final int NOTIFICATION_PENDING_INTENT_RESTART_V2RAY = 2;
    private static final int NOTIFICATION_ICON_THRESHOLD = 3000;
    private static final long QUERY_INTERVAL_MS = 3000L;
    private static long lastQueryTime = 0L;
    @org.jetbrains.annotations.Nullable()
    private static androidx.core.app.NotificationCompat.Builder mBuilder;
    @org.jetbrains.annotations.Nullable()
    private static kotlinx.coroutines.Job speedNotificationJob;
    @org.jetbrains.annotations.Nullable()
    private static android.app.NotificationManager mNotificationManager;
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.handler.NotificationManager INSTANCE = null;
    
    private NotificationManager() {
        super();
    }
    
    /**
     * Starts the speed notification.
     * @param currentConfig The current profile configuration.
     */
    public final void startSpeedNotification(@org.jetbrains.annotations.Nullable()
    com.v2ray.ang.dto.ProfileItem currentConfig) {
    }
    
    /**
     * Shows the notification.
     * @param currentConfig The current profile configuration.
     */
    public final void showNotification(@org.jetbrains.annotations.Nullable()
    com.v2ray.ang.dto.ProfileItem currentConfig) {
    }
    
    /**
     * Cancels the notification.
     */
    public final void cancelNotification() {
    }
    
    /**
     * Stops the speed notification.
     * @param currentConfig The current profile configuration.
     */
    public final void stopSpeedNotification(@org.jetbrains.annotations.Nullable()
    com.v2ray.ang.dto.ProfileItem currentConfig) {
    }
    
    /**
     * Creates a notification channel for Android O and above.
     * @return The channel ID.
     */
    @androidx.annotation.RequiresApi(value = android.os.Build.VERSION_CODES.O)
    private final java.lang.String createNotificationChannel() {
        return null;
    }
    
    /**
     * Updates the notification with the given content text and traffic data.
     * @param contentText The content text.
     * @param proxyTraffic The proxy traffic.
     * @param directTraffic The direct traffic.
     */
    private final void updateNotification(java.lang.String contentText, long proxyTraffic, long directTraffic) {
    }
    
    /**
     * Gets the notification manager.
     * @return The notification manager.
     */
    private final android.app.NotificationManager getNotificationManager() {
        return null;
    }
    
    /**
     * Appends the speed string to the given text.
     * @param text The text to append to.
     * @param name The name of the tag.
     * @param up The uplink speed.
     * @param down The downlink speed.
     */
    private final void appendSpeedString(java.lang.StringBuilder text, java.lang.String name, double up, double down) {
    }
    
    /**
     * Gets the service instance.
     * @return The service instance.
     */
    private final android.app.Service getService() {
        return null;
    }
}