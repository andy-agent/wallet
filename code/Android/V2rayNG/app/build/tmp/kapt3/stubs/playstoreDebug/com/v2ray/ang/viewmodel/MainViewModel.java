package com.v2ray.ang.viewmodel;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\'\u001a\u00020(J\b\u0010)\u001a\u00020(H\u0014J\u0006\u0010*\u001a\u00020(J\u000e\u0010+\u001a\u00020(2\u0006\u0010,\u001a\u00020\bJ\u0016\u0010-\u001a\u00020(2\u0006\u0010.\u001a\u00020\u001c2\u0006\u0010/\u001a\u00020\u001cJ\u0006\u00100\u001a\u00020(J\u0006\u00101\u001a\u000202J\u0006\u00103\u001a\u00020\u001cJ\u0006\u00104\u001a\u00020(J\u0006\u00105\u001a\u00020(J\u0006\u00106\u001a\u00020(J\u000e\u00107\u001a\u00020(2\u0006\u00108\u001a\u00020\bJ\u0014\u00109\u001a\b\u0012\u0004\u0012\u00020;0:2\u0006\u0010<\u001a\u00020=J\u000e\u0010>\u001a\u00020\u001c2\u0006\u0010,\u001a\u00020\bJ\u0006\u0010?\u001a\u00020\u001cJ\u0006\u0010@\u001a\u00020\u001cJ\u0006\u0010A\u001a\u00020\u001cJ\u0006\u0010B\u001a\u00020(J\u0010\u0010C\u001a\u00020(2\u0006\u0010D\u001a\u00020\bH\u0002J\u000e\u0010E\u001a\u00020(2\u0006\u0010F\u001a\u00020GJ\u000e\u0010H\u001a\u00020(2\u0006\u0010I\u001a\u00020\bJ\b\u0010J\u001a\u0004\u0018\u00010\bJ\u0006\u0010K\u001a\u00020(R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010\t\u001a\u00020\bX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\rR\u001a\u0010\u000e\u001a\u00020\bX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u000b\"\u0004\b\u0010\u0010\rR\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R!\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00170\u00168FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0019\u0010\u001a\u001a\u0004\b\u0015\u0010\u0018R!\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001c0\u00168FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001e\u0010\u001a\u001a\u0004\b\u001d\u0010\u0018R!\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\b0\u00168FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b!\u0010\u001a\u001a\u0004\b \u0010\u0018R\u001b\u0010\"\u001a\u00020#8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b&\u0010\u001a\u001a\u0004\b$\u0010%R\u0010\u0010L\u001a\u00020MX\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010N\u00a8\u0006O"}, d2 = {"Lcom/v2ray/ang/viewmodel/MainViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "<init>", "(Landroid/app/Application;)V", "serverList", "", "", "subscriptionId", "getSubscriptionId", "()Ljava/lang/String;", "setSubscriptionId", "(Ljava/lang/String;)V", "keywordFilter", "getKeywordFilter", "setKeywordFilter", "serversCache", "Lcom/v2ray/ang/dto/ServersCache;", "getServersCache", "()Ljava/util/List;", "isRunning", "Landroidx/lifecycle/MutableLiveData;", "", "()Landroidx/lifecycle/MutableLiveData;", "isRunning$delegate", "Lkotlin/Lazy;", "updateListAction", "", "getUpdateListAction", "updateListAction$delegate", "updateTestResultAction", "getUpdateTestResultAction", "updateTestResultAction$delegate", "tcpingTestScope", "Lkotlinx/coroutines/CoroutineScope;", "getTcpingTestScope", "()Lkotlinx/coroutines/CoroutineScope;", "tcpingTestScope$delegate", "startListenBroadcast", "", "onCleared", "reloadServerList", "removeServer", "guid", "swapServer", "fromPosition", "toPosition", "updateCache", "updateConfigViaSubAll", "Lcom/v2ray/ang/dto/SubscriptionUpdateResult;", "exportAllServer", "testAllTcping", "testAllRealPing", "testCurrentServerRealPing", "subscriptionIdChanged", "id", "getSubscriptions", "", "Lcom/v2ray/ang/dto/GroupMapItem;", "context", "Landroid/content/Context;", "getPosition", "removeDuplicateServer", "removeAllServer", "removeInvalidServer", "sortByTestResults", "sortByTestResultsForSub", "subId", "initAssets", "assets", "Landroid/content/res/AssetManager;", "filterConfig", "keyword", "findSubscriptionIdBySelect", "onTestsFinished", "mMsgReceiver", "Landroid/content/BroadcastReceiver;", "Landroid/content/BroadcastReceiver;", "app_playstoreDebug"})
public final class MainViewModel extends androidx.lifecycle.AndroidViewModel {
    @org.jetbrains.annotations.NotNull()
    private java.util.List<java.lang.String> serverList;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String subscriptionId;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String keywordFilter = "";
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.v2ray.ang.dto.ServersCache> serversCache = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy isRunning$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy updateListAction$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy updateTestResultAction$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy tcpingTestScope$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final android.content.BroadcastReceiver mMsgReceiver = null;
    
    public MainViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSubscriptionId() {
        return null;
    }
    
    public final void setSubscriptionId(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getKeywordFilter() {
        return null;
    }
    
    public final void setKeywordFilter(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.v2ray.ang.dto.ServersCache> getServersCache() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.lang.Boolean> isRunning() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.lang.Integer> getUpdateListAction() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.lang.String> getUpdateTestResultAction() {
        return null;
    }
    
    private final kotlinx.coroutines.CoroutineScope getTcpingTestScope() {
        return null;
    }
    
    /**
     * Refer to the official documentation for [registerReceiver](https://developer.android.com/reference/androidx/core/content/ContextCompat#registerReceiver(android.content.Context,android.content.BroadcastReceiver,android.content.IntentFilter,int):
     * `registerReceiver(Context, BroadcastReceiver, IntentFilter, int)`.
     */
    public final void startListenBroadcast() {
    }
    
    /**
     * Called when the ViewModel is cleared.
     */
    @java.lang.Override()
    protected void onCleared() {
    }
    
    /**
     * Reloads the server list based on current subscription filter.
     */
    public final void reloadServerList() {
    }
    
    /**
     * Removes a server by its GUID.
     * @param guid The GUID of the server to remove.
     */
    public final void removeServer(@org.jetbrains.annotations.NotNull()
    java.lang.String guid) {
    }
    
    /**
     * Swaps the positions of two servers.
     * @param fromPosition The initial position of the server.
     * @param toPosition The target position of the server.
     */
    public final void swapServer(int fromPosition, int toPosition) {
    }
    
    /**
     * Updates the cache of servers.
     */
    @kotlin.jvm.Synchronized()
    public final synchronized void updateCache() {
    }
    
    /**
     * Updates the configuration via subscription for all servers.
     * @return Detailed result of the subscription update operation.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.v2ray.ang.dto.SubscriptionUpdateResult updateConfigViaSubAll() {
        return null;
    }
    
    /**
     * Exports all servers.
     * @return The number of exported servers.
     */
    public final int exportAllServer() {
        return 0;
    }
    
    /**
     * Tests the TCP ping for all servers.
     */
    public final void testAllTcping() {
    }
    
    /**
     * Tests the real ping for all servers.
     */
    public final void testAllRealPing() {
    }
    
    /**
     * Tests the real ping for the current server.
     */
    public final void testCurrentServerRealPing() {
    }
    
    /**
     * Changes the subscription ID.
     * @param id The new subscription ID.
     */
    public final void subscriptionIdChanged(@org.jetbrains.annotations.NotNull()
    java.lang.String id) {
    }
    
    /**
     * Gets the subscriptions.
     * @param context The context.
     * @return A pair of lists containing the subscription IDs and remarks.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.v2ray.ang.dto.GroupMapItem> getSubscriptions(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    /**
     * Gets the position of a server by its GUID.
     * @param guid The GUID of the server.
     * @return The position of the server.
     */
    public final int getPosition(@org.jetbrains.annotations.NotNull()
    java.lang.String guid) {
        return 0;
    }
    
    /**
     * Removes duplicate servers.
     * @return The number of removed servers.
     */
    public final int removeDuplicateServer() {
        return 0;
    }
    
    /**
     * Removes all servers.
     * @return The number of removed servers.
     */
    public final int removeAllServer() {
        return 0;
    }
    
    /**
     * Removes invalid servers.
     * @return The number of removed servers.
     */
    public final int removeInvalidServer() {
        return 0;
    }
    
    /**
     * Sorts servers by their test results.
     */
    public final void sortByTestResults() {
    }
    
    /**
     * Sorts servers by their test results for a specific subscription.
     * @param subId The subscription ID to sort servers for.
     */
    private final void sortByTestResultsForSub(java.lang.String subId) {
    }
    
    /**
     * Initializes assets.
     * @param assets The asset manager.
     */
    public final void initAssets(@org.jetbrains.annotations.NotNull()
    android.content.res.AssetManager assets) {
    }
    
    /**
     * Filters the configuration by a keyword.
     * @param keyword The keyword to filter by.
     */
    public final void filterConfig(@org.jetbrains.annotations.NotNull()
    java.lang.String keyword) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String findSubscriptionIdBySelect() {
        return null;
    }
    
    public final void onTestsFinished() {
    }
}