package com.v2ray.ang.util;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u001e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bJ\u001e\u0010\f\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bJ\u0016\u0010\r\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u000e\u001a\u00020\u000fJ(\u0010\u0010\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0002\u00a8\u0006\u0013"}, d2 = {"Lcom/v2ray/ang/util/MessageUtil;", "", "<init>", "()V", "sendMsg2Service", "", "ctx", "Landroid/content/Context;", "what", "", "content", "Ljava/io/Serializable;", "sendMsg2UI", "sendMsg2TestService", "message", "Lcom/v2ray/ang/dto/TestServiceMessage;", "sendMsg", "action", "", "app_fdroidDebug"})
public final class MessageUtil {
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.util.MessageUtil INSTANCE = null;
    
    private MessageUtil() {
        super();
    }
    
    /**
     * Sends a message to the service.
     *
     * @param ctx The context.
     * @param what The message identifier.
     * @param content The message content.
     */
    public final void sendMsg2Service(@org.jetbrains.annotations.NotNull()
    android.content.Context ctx, int what, @org.jetbrains.annotations.NotNull()
    java.io.Serializable content) {
    }
    
    /**
     * Sends a message to the UI.
     *
     * @param ctx The context.
     * @param what The message identifier.
     * @param content The message content.
     */
    public final void sendMsg2UI(@org.jetbrains.annotations.NotNull()
    android.content.Context ctx, int what, @org.jetbrains.annotations.NotNull()
    java.io.Serializable content) {
    }
    
    /**
     * Sends a message to the test service.
     *
     * @param ctx The context.
     * @param message The test service message containing key, subscriptionId, and serverGuids.
     */
    public final void sendMsg2TestService(@org.jetbrains.annotations.NotNull()
    android.content.Context ctx, @org.jetbrains.annotations.NotNull()
    com.v2ray.ang.dto.TestServiceMessage message) {
    }
    
    /**
     * Sends a message with the specified action.
     *
     * @param ctx The context.
     * @param action The action string.
     * @param what The message identifier.
     * @param content The message content.
     */
    private final void sendMsg(android.content.Context ctx, java.lang.String action, int what, java.io.Serializable content) {
    }
}