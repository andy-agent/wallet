package com.v2ray.ang.handler;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tJ\u001c\u0010\n\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\t0\fJ\u0010\u0010\r\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\b\u001a\u00020\tJ\u0018\u0010\u000f\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\b\u001a\u0004\u0018\u00010\tJ\u0010\u0010\u0010\u001a\u00020\t2\u0006\u0010\b\u001a\u00020\tH\u0002J,\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0014\u001a\u00020\t2\u0006\u0010\u0015\u001a\u00020\u0016J\u0012\u0010\u0017\u001a\u00020\u00052\b\u0010\u0018\u001a\u0004\u0018\u00010\tH\u0002J\"\u0010\u0019\u001a\u00020\u00052\b\u0010\u0018\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0014\u001a\u00020\t2\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J*\u0010\u001a\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u001c0\u001b2\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001c0\f2\u0006\u0010\u0014\u001a\u00020\tH\u0002J(\u0010\u001e\u001a\u0004\u0018\u00010\t2\u0012\u0010\u001f\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u001c0\u001b2\b\u0010 \u001a\u0004\u0018\u00010\u001cH\u0002J\u001c\u0010!\u001a\u00020\u00162\b\u0010\"\u001a\u0004\u0018\u00010\t2\b\u0010#\u001a\u0004\u0018\u00010\tH\u0002J\"\u0010$\u001a\u00020\u00052\b\u0010\u0013\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0014\u001a\u00020\t2\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J&\u0010%\u001a\u0004\u0018\u00010\u001c2\b\u0010&\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0014\u001a\u00020\t2\b\u0010\'\u001a\u0004\u0018\u00010(H\u0002J\u0006\u0010)\u001a\u00020*J\u000e\u0010+\u001a\u00020*2\u0006\u0010,\u001a\u00020-J\"\u0010.\u001a\u00020\u00052\b\u0010\u0013\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0014\u001a\u00020\t2\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J\u0010\u0010/\u001a\u00020\u00052\u0006\u00100\u001a\u00020\tH\u0002J\u000e\u00101\u001a\u00020\t2\u0006\u00102\u001a\u00020\u001c\u00a8\u00063"}, d2 = {"Lcom/v2ray/ang/handler/AngConfigManager;", "", "<init>", "()V", "share2Clipboard", "", "context", "Landroid/content/Context;", "guid", "", "shareNonCustomConfigsToClipboard", "serverList", "", "share2QRCode", "Landroid/graphics/Bitmap;", "shareFullContent2Clipboard", "shareConfig", "importBatchConfig", "Lkotlin/Pair;", "server", "subid", "append", "", "parseBatchSubscription", "servers", "parseBatchConfig", "batchSaveConfigs", "", "Lcom/v2ray/ang/dto/ProfileItem;", "configs", "findMatchedProfileKey", "keyToProfile", "target", "isSameText", "left", "right", "parseCustomConfigServer", "parseConfig", "str", "subItem", "Lcom/v2ray/ang/dto/SubscriptionItem;", "updateConfigViaSubAll", "Lcom/v2ray/ang/dto/SubscriptionUpdateResult;", "updateConfigViaSub", "it", "Lcom/v2ray/ang/dto/SubscriptionCache;", "parseConfigViaSub", "importUrlAsSubscription", "url", "generateDescription", "profile", "app_fdroidDebug"})
public final class AngConfigManager {
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.handler.AngConfigManager INSTANCE = null;
    
    private AngConfigManager() {
        super();
    }
    
    /**
     * Shares the configuration to the clipboard.
     *
     * @param context The context.
     * @param guid The GUID of the configuration.
     * @return The result code.
     */
    public final int share2Clipboard(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String guid) {
        return 0;
    }
    
    /**
     * Shares non-custom configurations to the clipboard.
     *
     * @param context The context.
     * @param serverList The list of server GUIDs.
     * @return The number of configurations shared.
     */
    public final int shareNonCustomConfigsToClipboard(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> serverList) {
        return 0;
    }
    
    /**
     * Shares the configuration as a QR code.
     *
     * @param guid The GUID of the configuration.
     * @return The QR code bitmap.
     */
    @org.jetbrains.annotations.Nullable()
    public final android.graphics.Bitmap share2QRCode(@org.jetbrains.annotations.NotNull()
    java.lang.String guid) {
        return null;
    }
    
    /**
     * Shares the full content of the configuration to the clipboard.
     *
     * @param context The context.
     * @param guid The GUID of the configuration.
     * @return The result code.
     */
    public final int shareFullContent2Clipboard(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.Nullable()
    java.lang.String guid) {
        return 0;
    }
    
    /**
     * Shares the configuration.
     *
     * @param guid The GUID of the configuration.
     * @return The configuration string.
     */
    private final java.lang.String shareConfig(java.lang.String guid) {
        return null;
    }
    
    /**
     * Imports a batch of configurations.
     *
     * @param server The server string.
     * @param subid The subscription ID.
     * @param append Whether to append the configurations.
     * @return A pair containing the number of configurations and subscriptions imported.
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlin.Pair<java.lang.Integer, java.lang.Integer> importBatchConfig(@org.jetbrains.annotations.Nullable()
    java.lang.String server, @org.jetbrains.annotations.NotNull()
    java.lang.String subid, boolean append) {
        return null;
    }
    
    /**
     * Parses a batch of subscriptions.
     *
     * @param servers The servers string.
     * @return The number of subscriptions parsed.
     */
    private final int parseBatchSubscription(java.lang.String servers) {
        return 0;
    }
    
    /**
     * Parses a batch of configurations.
     *
     * @param servers The servers string.
     * @param subid The subscription ID.
     * @param append Whether to append the configurations.
     * @return The number of configurations parsed.
     */
    private final int parseBatchConfig(java.lang.String servers, java.lang.String subid, boolean append) {
        return 0;
    }
    
    /**
     * Batch save configurations to reduce serverList read/write operations.
     * Reads serverList once, saves all configs, then writes serverList once.
     *
     * @param configs The list of ProfileItem to save.
     * @param subid The subscription ID.
     * @return Map of generated keys to their corresponding ProfileItem.
     */
    private final java.util.Map<java.lang.String, com.v2ray.ang.dto.ProfileItem> batchSaveConfigs(java.util.List<com.v2ray.ang.dto.ProfileItem> configs, java.lang.String subid) {
        return null;
    }
    
    /**
     * Finds a matched profile key from the given key-profile map using multi-level matching.
     * Matching priority (from highest to lowest):
     * 1. Exact match: server + port + password
     * 2. Match by remarks (exact match)
     * 3. Match by server + port
     * 4. Match by server only
     *
     * @param keyToProfile Map of server keys to their ProfileItem
     * @param target Target profile to match
     * @return Matched key or null
     */
    private final java.lang.String findMatchedProfileKey(java.util.Map<java.lang.String, com.v2ray.ang.dto.ProfileItem> keyToProfile, com.v2ray.ang.dto.ProfileItem target) {
        return null;
    }
    
    /**
     * Case-insensitive trimmed string comparison.
     *
     * @param left First string
     * @param right Second string
     * @return True if both are non-empty and equal (case-insensitive, trimmed)
     */
    private final boolean isSameText(java.lang.String left, java.lang.String right) {
        return false;
    }
    
    /**
     * Parses a custom configuration server.
     *
     * @param server The server string.
     * @param subid The subscription ID.
     * @param append Whether to append the configurations.
     * @return The number of configurations parsed.
     */
    private final int parseCustomConfigServer(java.lang.String server, java.lang.String subid, boolean append) {
        return 0;
    }
    
    /**
     * Parses the configuration from a QR code or string.
     * Only parses and returns ProfileItem, does not save.
     *
     * @param str The configuration string.
     * @param subid The subscription ID.
     * @param subItem The subscription item.
     * @return The parsed ProfileItem or null if parsing fails or filtered out.
     */
    private final com.v2ray.ang.dto.ProfileItem parseConfig(java.lang.String str, java.lang.String subid, com.v2ray.ang.dto.SubscriptionItem subItem) {
        return null;
    }
    
    /**
     * Updates the configuration via all subscriptions.
     *
     * @return Detailed result of the subscription update operation.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.v2ray.ang.dto.SubscriptionUpdateResult updateConfigViaSubAll() {
        return null;
    }
    
    /**
     * Updates the configuration via a subscription.
     *
     * @param it The subscription item.
     * @return Subscription update result.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.v2ray.ang.dto.SubscriptionUpdateResult updateConfigViaSub(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.dto.SubscriptionCache it) {
        return null;
    }
    
    /**
     * Parses the configuration via a subscription.
     *
     * @param server The server string.
     * @param subid The subscription ID.
     * @param append Whether to append the configurations.
     * @return The number of configurations parsed.
     */
    private final int parseConfigViaSub(java.lang.String server, java.lang.String subid, boolean append) {
        return 0;
    }
    
    /**
     * Imports a URL as a subscription.
     *
     * @param url The URL.
     * @return The number of subscriptions imported.
     */
    private final int importUrlAsSubscription(java.lang.String url) {
        return 0;
    }
    
    /**
     * Generates a description for the profile.
     *
     * @param profile The profile item.
     * @return The generated description.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String generateDescription(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.dto.ProfileItem profile) {
        return null;
    }
}