package com.v2ray.ang.handler;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0094\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0019\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0007\n\u0002\u0010#\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\b\u0010)\u001a\u0004\u0018\u00010\u0005J\b\u0010*\u001a\u0004\u0018\u00010\u0005J\u000e\u0010+\u001a\u00020,2\u0006\u0010-\u001a\u00020\u0005J\u001c\u0010.\u001a\u00020,2\f\u0010/\u001a\b\u0012\u0004\u0012\u00020\u0005002\u0006\u00101\u001a\u00020\u0005J\u0014\u00102\u001a\b\u0012\u0004\u0012\u00020\u0005002\u0006\u00101\u001a\u00020\u0005J\f\u00103\u001a\b\u0012\u0004\u0012\u00020\u000500J\u0010\u00104\u001a\u0004\u0018\u0001052\u0006\u0010-\u001a\u00020\u0005J\u0016\u00106\u001a\u00020\u00052\u0006\u0010-\u001a\u00020\u00052\u0006\u00107\u001a\u000205J\u0016\u00108\u001a\u00020,2\u0006\u00109\u001a\u00020\u00052\u0006\u0010:\u001a\u00020\u0005J\u000e\u0010;\u001a\u00020,2\u0006\u0010-\u001a\u00020\u0005J\u0010\u0010<\u001a\u00020,2\b\u00101\u001a\u0004\u0018\u00010\u0005J\u0010\u0010=\u001a\u0004\u0018\u00010>2\u0006\u0010-\u001a\u00020\u0005J\u0016\u0010?\u001a\u00020,2\u0006\u0010-\u001a\u00020\u00052\u0006\u0010@\u001a\u00020AJ\u0016\u0010B\u001a\u00020,2\u000e\u0010C\u001a\n\u0012\u0004\u0012\u00020\u0005\u0018\u00010DJ\u0006\u0010E\u001a\u00020FJ\u000e\u0010G\u001a\u00020F2\u0006\u0010-\u001a\u00020\u0005J\u0016\u0010H\u001a\u00020,2\u0006\u0010-\u001a\u00020\u00052\u0006\u00107\u001a\u00020\u0005J\u0010\u0010I\u001a\u0004\u0018\u00010\u00052\u0006\u0010-\u001a\u00020\u0005J\u0012\u0010J\u001a\u00020\u00052\b\u00101\u001a\u0004\u0018\u00010\u0005H\u0002J\b\u0010K\u001a\u00020,H\u0002J\f\u0010L\u001a\b\u0012\u0004\u0012\u00020M0DJ\u000e\u0010N\u001a\u00020,2\u0006\u0010O\u001a\u00020\u0005J\u0016\u0010P\u001a\u00020,2\u0006\u0010-\u001a\u00020\u00052\u0006\u0010Q\u001a\u00020RJ\u0010\u0010S\u001a\u0004\u0018\u00010R2\u0006\u00101\u001a\u00020\u0005J\u0014\u0010T\u001a\u00020,2\f\u0010U\u001a\b\u0012\u0004\u0012\u00020\u000500J\f\u0010V\u001a\b\u0012\u0004\u0012\u00020\u000500J\f\u0010W\u001a\b\u0012\u0004\u0012\u00020X0DJ\u000e\u0010Y\u001a\u00020,2\u0006\u0010Z\u001a\u00020\u0005J\u0016\u0010[\u001a\u00020,2\u0006\u0010Z\u001a\u00020\u00052\u0006\u0010\\\u001a\u00020]J\u0010\u0010^\u001a\u0004\u0018\u00010]2\u0006\u0010Z\u001a\u00020\u0005J\u000e\u0010_\u001a\n\u0012\u0004\u0012\u00020`\u0018\u000100J\u0016\u0010a\u001a\u00020,2\u000e\u0010b\u001a\n\u0012\u0004\u0012\u00020`\u0018\u000100J\u0018\u0010c\u001a\u00020d2\u0006\u00109\u001a\u00020\u00052\b\u0010e\u001a\u0004\u0018\u00010\u0005J\u0016\u0010c\u001a\u00020d2\u0006\u00109\u001a\u00020\u00052\u0006\u0010e\u001a\u00020FJ\u0016\u0010c\u001a\u00020d2\u0006\u00109\u001a\u00020\u00052\u0006\u0010e\u001a\u00020AJ\u0016\u0010c\u001a\u00020d2\u0006\u00109\u001a\u00020\u00052\u0006\u0010e\u001a\u00020fJ\u0016\u0010c\u001a\u00020d2\u0006\u00109\u001a\u00020\u00052\u0006\u0010e\u001a\u00020dJ\u001c\u0010c\u001a\u00020d2\u0006\u00109\u001a\u00020\u00052\f\u0010e\u001a\b\u0012\u0004\u0012\u00020\u00050gJ\u0010\u0010h\u001a\u0004\u0018\u00010\u00052\u0006\u00109\u001a\u00020\u0005J\u001a\u0010h\u001a\u0004\u0018\u00010\u00052\u0006\u00109\u001a\u00020\u00052\b\u0010i\u001a\u0004\u0018\u00010\u0005J\u0016\u0010j\u001a\u00020F2\u0006\u00109\u001a\u00020\u00052\u0006\u0010i\u001a\u00020FJ\u0016\u0010k\u001a\u00020A2\u0006\u00109\u001a\u00020\u00052\u0006\u0010i\u001a\u00020AJ\u0016\u0010l\u001a\u00020f2\u0006\u00109\u001a\u00020\u00052\u0006\u0010i\u001a\u00020fJ\u000e\u0010m\u001a\u00020d2\u0006\u00109\u001a\u00020\u0005J\u0016\u0010m\u001a\u00020d2\u0006\u00109\u001a\u00020\u00052\u0006\u0010i\u001a\u00020dJ\u0016\u0010n\u001a\n\u0012\u0004\u0012\u00020\u0005\u0018\u00010g2\u0006\u00109\u001a\u00020\u0005J\u000e\u0010o\u001a\u00020,2\u0006\u0010p\u001a\u00020dJ\u0006\u0010q\u001a\u00020dJ\u000e\u0010r\u001a\u00020d2\u0006\u00107\u001a\u00020sJ\b\u0010t\u001a\u0004\u0018\u00010sR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0011\u001a\u00020\u00128BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0015\u0010\u0016\u001a\u0004\b\u0013\u0010\u0014R\u001b\u0010\u0017\u001a\u00020\u00128BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0019\u0010\u0016\u001a\u0004\b\u0018\u0010\u0014R\u001b\u0010\u001a\u001a\u00020\u00128BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001c\u0010\u0016\u001a\u0004\b\u001b\u0010\u0014R\u001b\u0010\u001d\u001a\u00020\u00128BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001f\u0010\u0016\u001a\u0004\b\u001e\u0010\u0014R\u001b\u0010 \u001a\u00020\u00128BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\"\u0010\u0016\u001a\u0004\b!\u0010\u0014R\u001b\u0010#\u001a\u00020\u00128BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b%\u0010\u0016\u001a\u0004\b$\u0010\u0014R\u001b\u0010&\u001a\u00020\u00128BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b(\u0010\u0016\u001a\u0004\b\'\u0010\u0014\u00a8\u0006u"}, d2 = {"Lcom/v2ray/ang/handler/MmkvManager;", "", "<init>", "()V", "ID_MAIN", "", "ID_PROFILE_FULL_CONFIG", "ID_SERVER_RAW", "ID_SERVER_AFF", "ID_SUB", "ID_ASSET", "ID_SETTING", "KEY_SELECTED_SERVER", "KEY_ANG_CONFIGS", "KEY_SUB_SERVER_PREFIX", "KEY_SUB_IDS", "KEY_WEBDAV_CONFIG", "mainStorage", "Lcom/tencent/mmkv/MMKV;", "getMainStorage", "()Lcom/tencent/mmkv/MMKV;", "mainStorage$delegate", "Lkotlin/Lazy;", "profileFullStorage", "getProfileFullStorage", "profileFullStorage$delegate", "serverRawStorage", "getServerRawStorage", "serverRawStorage$delegate", "serverAffStorage", "getServerAffStorage", "serverAffStorage$delegate", "subStorage", "getSubStorage", "subStorage$delegate", "assetStorage", "getAssetStorage", "assetStorage$delegate", "settingsStorage", "getSettingsStorage", "settingsStorage$delegate", "readLegacyServerList", "getSelectServer", "setSelectServer", "", "guid", "encodeServerList", "serverList", "", "subscriptionId", "decodeServerList", "decodeAllServerList", "decodeServerConfig", "Lcom/v2ray/ang/dto/ProfileItem;", "encodeServerConfig", "config", "encodeProfileDirect", "key", "configJson", "removeServer", "removeServerViaSubid", "decodeServerAffiliationInfo", "Lcom/v2ray/ang/dto/ServerAffiliationInfo;", "encodeServerTestDelayMillis", "testResult", "", "clearAllTestDelayResults", "keys", "", "removeAllServer", "", "removeInvalidServer", "encodeServerRaw", "decodeServerRaw", "getSubscriptionId", "initSubsList", "decodeSubscriptions", "Lcom/v2ray/ang/dto/SubscriptionCache;", "removeSubscription", "subid", "encodeSubscription", "subItem", "Lcom/v2ray/ang/dto/SubscriptionItem;", "decodeSubscription", "encodeSubsList", "subsList", "decodeSubsList", "decodeAssetUrls", "Lcom/v2ray/ang/dto/AssetUrlCache;", "removeAssetUrl", "assetid", "encodeAsset", "assetItem", "Lcom/v2ray/ang/dto/AssetUrlItem;", "decodeAsset", "decodeRoutingRulesets", "Lcom/v2ray/ang/dto/RulesetItem;", "encodeRoutingRulesets", "rulesetList", "encodeSettings", "", "value", "", "", "decodeSettingsString", "defaultValue", "decodeSettingsInt", "decodeSettingsLong", "decodeSettingsFloat", "decodeSettingsBool", "decodeSettingsStringSet", "encodeStartOnBoot", "startOnBoot", "decodeStartOnBoot", "encodeWebDavConfig", "Lcom/v2ray/ang/dto/WebDavConfig;", "decodeWebDavConfig", "app_playstoreDebug"})
public final class MmkvManager {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String ID_MAIN = "MAIN";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String ID_PROFILE_FULL_CONFIG = "PROFILE_FULL_CONFIG";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String ID_SERVER_RAW = "SERVER_RAW";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String ID_SERVER_AFF = "SERVER_AFF";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String ID_SUB = "SUB";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String ID_ASSET = "ASSET";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String ID_SETTING = "SETTING";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_SELECTED_SERVER = "SELECTED_SERVER";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_ANG_CONFIGS = "ANG_CONFIGS";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_SUB_SERVER_PREFIX = "SUB_SERVERS_";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_SUB_IDS = "SUB_IDS";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_WEBDAV_CONFIG = "WEBDAV_CONFIG";
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.Lazy mainStorage$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.Lazy profileFullStorage$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.Lazy serverRawStorage$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.Lazy serverAffStorage$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.Lazy subStorage$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.Lazy assetStorage$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.Lazy settingsStorage$delegate = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.handler.MmkvManager INSTANCE = null;
    
    private MmkvManager() {
        super();
    }
    
    private final com.tencent.mmkv.MMKV getMainStorage() {
        return null;
    }
    
    private final com.tencent.mmkv.MMKV getProfileFullStorage() {
        return null;
    }
    
    private final com.tencent.mmkv.MMKV getServerRawStorage() {
        return null;
    }
    
    private final com.tencent.mmkv.MMKV getServerAffStorage() {
        return null;
    }
    
    private final com.tencent.mmkv.MMKV getSubStorage() {
        return null;
    }
    
    private final com.tencent.mmkv.MMKV getAssetStorage() {
        return null;
    }
    
    private final com.tencent.mmkv.MMKV getSettingsStorage() {
        return null;
    }
    
    /**
     * Reads the legacy server list from KEY_ANG_CONFIGS for migration.
     * This method is for migration purposes only.
     *
     * @return The JSON string of legacy server list, or null if not exists.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String readLegacyServerList() {
        return null;
    }
    
    /**
     * Gets the selected server GUID.
     *
     * @return The selected server GUID.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getSelectServer() {
        return null;
    }
    
    /**
     * Sets the selected server GUID.
     *
     * @param guid The server GUID.
     */
    public final void setSelectServer(@org.jetbrains.annotations.NotNull()
    java.lang.String guid) {
    }
    
    /**
     * Encodes the server list for a given subscription.
     * Saves to the subscription's serverList (including default subscription for ungrouped servers).
     *
     * @param serverList The list of server GUIDs.
     * @param subscriptionId The subscription ID.
     */
    public final void encodeServerList(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> serverList, @org.jetbrains.annotations.NotNull()
    java.lang.String subscriptionId) {
    }
    
    /**
     * Decodes the server list for a given subscription.
     * If subscriptionId is empty, returns ungrouped servers.
     * Otherwise, returns servers from the specified subscription's serverList.
     *
     * @param subscriptionId The subscription ID.
     * @return The list of server GUIDs.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> decodeServerList(@org.jetbrains.annotations.NotNull()
    java.lang.String subscriptionId) {
        return null;
    }
    
    /**
     * Decodes all server list (merged from all subscriptions including default subscription).
     * Use this when you need the complete server list.
     *
     * @return The list of all server GUIDs.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> decodeAllServerList() {
        return null;
    }
    
    /**
     * Decodes the server configuration.
     *
     * @param guid The server GUID.
     * @return The server configuration.
     */
    @org.jetbrains.annotations.Nullable()
    public final com.v2ray.ang.dto.ProfileItem decodeServerConfig(@org.jetbrains.annotations.NotNull()
    java.lang.String guid) {
        return null;
    }
    
    /**
     * Encodes the server configuration.
     *
     * @param guid The server GUID.
     * @param config The server configuration.
     * @return The server GUID.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String encodeServerConfig(@org.jetbrains.annotations.NotNull()
    java.lang.String guid, @org.jetbrains.annotations.NotNull()
    com.v2ray.ang.dto.ProfileItem config) {
        return null;
    }
    
    /**
     * Encodes the server configuration directly without updating serverList.
     *
     * @param key The server GUID.
     * @param configJson The server configuration JSON string.
     */
    public final void encodeProfileDirect(@org.jetbrains.annotations.NotNull()
    java.lang.String key, @org.jetbrains.annotations.NotNull()
    java.lang.String configJson) {
    }
    
    /**
     * Removes the server configuration.
     *
     * @param guid The server GUID.
     */
    public final void removeServer(@org.jetbrains.annotations.NotNull()
    java.lang.String guid) {
    }
    
    /**
     * Removes the server configurations via subscription ID.
     *
     * @param subscriptionId The subscription ID.
     */
    public final void removeServerViaSubid(@org.jetbrains.annotations.Nullable()
    java.lang.String subscriptionId) {
    }
    
    /**
     * Decodes the server affiliation information.
     *
     * @param guid The server GUID.
     * @return The server affiliation information.
     */
    @org.jetbrains.annotations.Nullable()
    public final com.v2ray.ang.dto.ServerAffiliationInfo decodeServerAffiliationInfo(@org.jetbrains.annotations.NotNull()
    java.lang.String guid) {
        return null;
    }
    
    /**
     * Encodes the server test delay in milliseconds.
     *
     * @param guid The server GUID.
     * @param testResult The test delay in milliseconds.
     */
    public final void encodeServerTestDelayMillis(@org.jetbrains.annotations.NotNull()
    java.lang.String guid, long testResult) {
    }
    
    /**
     * Clears all test delay results.
     *
     * @param keys The list of server GUIDs.
     */
    public final void clearAllTestDelayResults(@org.jetbrains.annotations.Nullable()
    java.util.List<java.lang.String> keys) {
    }
    
    /**
     * Removes all server configurations.
     *
     * @return The number of server configurations removed.
     */
    public final int removeAllServer() {
        return 0;
    }
    
    /**
     * Removes invalid server configurations.
     *
     * @param guid The server GUID.
     * @return The number of server configurations removed.
     */
    public final int removeInvalidServer(@org.jetbrains.annotations.NotNull()
    java.lang.String guid) {
        return 0;
    }
    
    /**
     * Encodes the raw server configuration.
     *
     * @param guid The server GUID.
     * @param config The raw server configuration.
     */
    public final void encodeServerRaw(@org.jetbrains.annotations.NotNull()
    java.lang.String guid, @org.jetbrains.annotations.NotNull()
    java.lang.String config) {
    }
    
    /**
     * Decodes the raw server configuration.
     *
     * @param guid The server GUID.
     * @return The raw server configuration.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String decodeServerRaw(@org.jetbrains.annotations.NotNull()
    java.lang.String guid) {
        return null;
    }
    
    private final java.lang.String getSubscriptionId(java.lang.String subscriptionId) {
        return null;
    }
    
    /**
     * Initializes the subscription list.
     */
    private final void initSubsList() {
    }
    
    /**
     * Decodes the subscriptions.
     *
     * @return The list of subscriptions.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.v2ray.ang.dto.SubscriptionCache> decodeSubscriptions() {
        return null;
    }
    
    /**
     * Removes the subscription.
     *
     * @param subid The subscription ID.
     */
    public final void removeSubscription(@org.jetbrains.annotations.NotNull()
    java.lang.String subid) {
    }
    
    /**
     * Encodes the subscription.
     *
     * @param guid The subscription GUID.
     * @param subItem The subscription item.
     */
    public final void encodeSubscription(@org.jetbrains.annotations.NotNull()
    java.lang.String guid, @org.jetbrains.annotations.NotNull()
    com.v2ray.ang.dto.SubscriptionItem subItem) {
    }
    
    /**
     * Decodes the subscription.
     *
     * @param subscriptionId The subscription ID.
     * @return The subscription item.
     */
    @org.jetbrains.annotations.Nullable()
    public final com.v2ray.ang.dto.SubscriptionItem decodeSubscription(@org.jetbrains.annotations.NotNull()
    java.lang.String subscriptionId) {
        return null;
    }
    
    /**
     * Encodes the subscription list.
     *
     * @param subsList The list of subscription IDs.
     */
    public final void encodeSubsList(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> subsList) {
    }
    
    /**
     * Decodes the subscription list.
     *
     * @return The list of subscription IDs.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> decodeSubsList() {
        return null;
    }
    
    /**
     * Decodes the asset URLs.
     *
     * @return The list of asset URLs.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.v2ray.ang.dto.AssetUrlCache> decodeAssetUrls() {
        return null;
    }
    
    /**
     * Removes the asset URL.
     *
     * @param assetid The asset ID.
     */
    public final void removeAssetUrl(@org.jetbrains.annotations.NotNull()
    java.lang.String assetid) {
    }
    
    /**
     * Encodes the asset.
     *
     * @param assetid The asset ID.
     * @param assetItem The asset item.
     */
    public final void encodeAsset(@org.jetbrains.annotations.NotNull()
    java.lang.String assetid, @org.jetbrains.annotations.NotNull()
    com.v2ray.ang.dto.AssetUrlItem assetItem) {
    }
    
    /**
     * Decodes the asset.
     *
     * @param assetid The asset ID.
     * @return The asset item.
     */
    @org.jetbrains.annotations.Nullable()
    public final com.v2ray.ang.dto.AssetUrlItem decodeAsset(@org.jetbrains.annotations.NotNull()
    java.lang.String assetid) {
        return null;
    }
    
    /**
     * Decodes the routing rulesets.
     *
     * @return The list of routing rulesets.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<com.v2ray.ang.dto.RulesetItem> decodeRoutingRulesets() {
        return null;
    }
    
    /**
     * Encodes the routing rulesets.
     *
     * @param rulesetList The list of routing rulesets.
     */
    public final void encodeRoutingRulesets(@org.jetbrains.annotations.Nullable()
    java.util.List<com.v2ray.ang.dto.RulesetItem> rulesetList) {
    }
    
    /**
     * Encodes the settings.
     *
     * @param key The settings key.
     * @param value The settings value.
     * @return Whether the encoding was successful.
     */
    public final boolean encodeSettings(@org.jetbrains.annotations.NotNull()
    java.lang.String key, @org.jetbrains.annotations.Nullable()
    java.lang.String value) {
        return false;
    }
    
    /**
     * Encodes the settings.
     *
     * @param key The settings key.
     * @param value The settings value.
     * @return Whether the encoding was successful.
     */
    public final boolean encodeSettings(@org.jetbrains.annotations.NotNull()
    java.lang.String key, int value) {
        return false;
    }
    
    /**
     * Encodes the settings.
     *
     * @param key The settings key.
     * @param value The settings value.
     * @return Whether the encoding was successful.
     */
    public final boolean encodeSettings(@org.jetbrains.annotations.NotNull()
    java.lang.String key, long value) {
        return false;
    }
    
    /**
     * Encodes the settings.
     *
     * @param key The settings key.
     * @param value The settings value.
     * @return Whether the encoding was successful.
     */
    public final boolean encodeSettings(@org.jetbrains.annotations.NotNull()
    java.lang.String key, float value) {
        return false;
    }
    
    /**
     * Encodes the settings.
     *
     * @param key The settings key.
     * @param value The settings value.
     * @return Whether the encoding was successful.
     */
    public final boolean encodeSettings(@org.jetbrains.annotations.NotNull()
    java.lang.String key, boolean value) {
        return false;
    }
    
    /**
     * Encodes the settings.
     *
     * @param key The settings key.
     * @param value The settings value.
     * @return Whether the encoding was successful.
     */
    public final boolean encodeSettings(@org.jetbrains.annotations.NotNull()
    java.lang.String key, @org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> value) {
        return false;
    }
    
    /**
     * Decodes the settings string.
     *
     * @param key The settings key.
     * @return The settings value.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String decodeSettingsString(@org.jetbrains.annotations.NotNull()
    java.lang.String key) {
        return null;
    }
    
    /**
     * Decodes the settings string.
     *
     * @param key The settings key.
     * @param defaultValue The default value.
     * @return The settings value.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String decodeSettingsString(@org.jetbrains.annotations.NotNull()
    java.lang.String key, @org.jetbrains.annotations.Nullable()
    java.lang.String defaultValue) {
        return null;
    }
    
    /**
     * Decodes the settings integer.
     *
     * @param key The settings key.
     * @param defaultValue The default value.
     * @return The settings value.
     */
    public final int decodeSettingsInt(@org.jetbrains.annotations.NotNull()
    java.lang.String key, int defaultValue) {
        return 0;
    }
    
    /**
     * Decodes the settings long.
     *
     * @param key The settings key.
     * @param defaultValue The default value.
     * @return The settings value.
     */
    public final long decodeSettingsLong(@org.jetbrains.annotations.NotNull()
    java.lang.String key, long defaultValue) {
        return 0L;
    }
    
    /**
     * Decodes the settings float.
     *
     * @param key The settings key.
     * @param defaultValue The default value.
     * @return The settings value.
     */
    public final float decodeSettingsFloat(@org.jetbrains.annotations.NotNull()
    java.lang.String key, float defaultValue) {
        return 0.0F;
    }
    
    /**
     * Decodes the settings boolean.
     *
     * @param key The settings key.
     * @return The settings value.
     */
    public final boolean decodeSettingsBool(@org.jetbrains.annotations.NotNull()
    java.lang.String key) {
        return false;
    }
    
    /**
     * Decodes the settings boolean.
     *
     * @param key The settings key.
     * @param defaultValue The default value.
     * @return The settings value.
     */
    public final boolean decodeSettingsBool(@org.jetbrains.annotations.NotNull()
    java.lang.String key, boolean defaultValue) {
        return false;
    }
    
    /**
     * Decodes the settings string set.
     *
     * @param key The settings key.
     * @return The settings value.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.util.Set<java.lang.String> decodeSettingsStringSet(@org.jetbrains.annotations.NotNull()
    java.lang.String key) {
        return null;
    }
    
    /**
     * Encodes the start on boot setting.
     *
     * @param startOnBoot Whether to start on boot.
     */
    public final void encodeStartOnBoot(boolean startOnBoot) {
    }
    
    /**
     * Decodes the start on boot setting.
     *
     * @return Whether to start on boot.
     */
    public final boolean decodeStartOnBoot() {
        return false;
    }
    
    /**
     * Encodes the WebDAV config as JSON into storage.
     */
    public final boolean encodeWebDavConfig(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.dto.WebDavConfig config) {
        return false;
    }
    
    /**
     * Decodes the WebDAV config from storage.
     */
    @org.jetbrains.annotations.Nullable()
    public final com.v2ray.ang.dto.WebDavConfig decodeWebDavConfig() {
        return null;
    }
}