package com.v2ray.ang.handler;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u001e\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0086@\u00a2\u0006\u0002\u0010\u000eJ\u0016\u0010\u000f\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rJ\u0006\u0010\u0010\u001a\u00020\u0011J\"\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u000b0\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\f\u001a\u00020\rJ\b\u0010\u0016\u001a\u0004\u0018\u00010\u000bR\"\u0010\u0004\u001a\u0016\u0012\u0006\u0012\u0004\u0018\u00010\u00060\u0005j\n\u0012\u0006\u0012\u0004\u0018\u00010\u0006`\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Lcom/v2ray/ang/handler/SpeedtestManager;", "", "<init>", "()V", "tcpTestingSockets", "Ljava/util/ArrayList;", "Ljava/net/Socket;", "Lkotlin/collections/ArrayList;", "tcping", "", "url", "", "port", "", "(Ljava/lang/String;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "socketConnectTime", "closeAllTcpSockets", "", "testConnection", "Lkotlin/Pair;", "context", "Landroid/content/Context;", "getRemoteIPInfo", "app_fdroidDebug"})
public final class SpeedtestManager {
    @org.jetbrains.annotations.NotNull()
    private static final java.util.ArrayList<java.net.Socket> tcpTestingSockets = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.handler.SpeedtestManager INSTANCE = null;
    
    private SpeedtestManager() {
        super();
    }
    
    /**
     * Measures the TCP connection time to a given URL and port.
     *
     * @param url The URL to connect to.
     * @param port The port to connect to.
     * @return The connection time in milliseconds, or -1 if the connection failed.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object tcping(@org.jetbrains.annotations.NotNull()
    java.lang.String url, int port, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    /**
     * Measures the time taken to establish a TCP connection to a given URL and port.
     *
     * @param url The URL to connect to.
     * @param port The port to connect to.
     * @return The connection time in milliseconds, or -1 if the connection failed.
     */
    public final long socketConnectTime(@org.jetbrains.annotations.NotNull()
    java.lang.String url, int port) {
        return 0L;
    }
    
    /**
     * Closes all TCP sockets that are currently being tested.
     */
    public final void closeAllTcpSockets() {
    }
    
    /**
     * Tests the connection to a given URL and port.
     *
     * @param context The Context in which the test is running.
     * @param port The port to connect to.
     * @return A pair containing the elapsed time in milliseconds and the result message.
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlin.Pair<java.lang.Long, java.lang.String> testConnection(@org.jetbrains.annotations.NotNull()
    android.content.Context context, int port) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getRemoteIPInfo() {
        return null;
    }
}