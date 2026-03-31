package com.v2ray.ang.handler;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u0010\u0010\b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0002J\"\u0010\t\u001a\n\u0012\u0004\u0012\u00020\u000b\u0018\u00010\n2\u0006\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\f\u001a\u00020\rH\u0002J\u0016\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\f\u001a\u00020\rJ\u0010\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012J\u0016\u0010\u0013\u001a\u00020\u00052\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u000b0\nH\u0002J\u0010\u0010\u0015\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\f\u001a\u00020\rJ\u0018\u0010\u0016\u001a\u00020\u00052\u0006\u0010\f\u001a\u00020\r2\b\u0010\u0017\u001a\u0004\u0018\u00010\u000bJ\u000e\u0010\u0018\u001a\u00020\u00052\u0006\u0010\f\u001a\u00020\rJ\u0006\u0010\u0019\u001a\u00020\u0010J\u0016\u0010\u001a\u001a\u00020\u00052\u0006\u0010\u001b\u001a\u00020\r2\u0006\u0010\u001c\u001a\u00020\rJ\u0016\u0010\u001d\u001a\u00020\u00052\u0006\u0010\u001b\u001a\u00020\r2\u0006\u0010\u001c\u001a\u00020\rJ\u0012\u0010\u001e\u001a\u0004\u0018\u00010\u001f2\b\u0010 \u001a\u0004\u0018\u00010\u0012J\u000e\u0010!\u001a\u00020\u00052\u0006\u0010\"\u001a\u00020\u0012J\u0006\u0010#\u001a\u00020\rJ\u0006\u0010$\u001a\u00020\rJ\u0016\u0010%\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010&\u001a\u00020\'J\f\u0010(\u001a\b\u0012\u0004\u0012\u00020\u00120)J\f\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00120)J\f\u0010+\u001a\b\u0012\u0004\u0012\u00020\u00120)J\u0010\u0010,\u001a\u00020\u00122\b\b\u0002\u0010-\u001a\u00020\u0010J\u0006\u0010.\u001a\u00020/J\u0006\u00100\u001a\u00020\u0005J\u0006\u00101\u001a\u000202J\u0006\u00103\u001a\u00020\rJ\u0006\u00104\u001a\u00020\u0010J\u0006\u00105\u001a\u00020\u0010J\b\u00106\u001a\u00020\u0005H\u0002J\u0018\u00107\u001a\u00020\u00052\u0006\u00108\u001a\u00020\u00122\u0006\u00109\u001a\u00020\u0012H\u0002J\b\u0010:\u001a\u00020\u0005H\u0002J\b\u0010;\u001a\u00020\u0005H\u0002J\b\u0010<\u001a\u00020\u0005H\u0002\u00a8\u0006="}, d2 = {"Lcom/v2ray/ang/handler/SettingsManager;", "", "<init>", "()V", "initApp", "", "context", "Landroid/content/Context;", "initRoutingRulesets", "getPresetRoutingRulesets", "", "Lcom/v2ray/ang/dto/RulesetItem;", "index", "", "resetRoutingRulesetsFromPresets", "resetRoutingRulesets", "", "content", "", "resetRoutingRulesetsCommon", "rulesetList", "getRoutingRuleset", "saveRoutingRuleset", "ruleset", "removeRoutingRuleset", "routingRulesetsBypassLan", "swapRoutingRuleset", "fromPosition", "toPosition", "swapSubscriptions", "getServerViaRemarks", "Lcom/v2ray/ang/dto/ProfileItem;", "remarks", "removeSubscriptionWithDefault", "subid", "getSocksPort", "getHttpPort", "initAssets", "assets", "Landroid/content/res/AssetManager;", "getDomesticDnsServers", "", "getRemoteDnsServers", "getVpnDnsServers", "getDelayTestUrl", "second", "getLocale", "Ljava/util/Locale;", "setNightMode", "getCurrentVpnInterfaceAddressConfig", "Lcom/v2ray/ang/enums/VpnInterfaceAddressConfig;", "getVpnMtu", "isUsingHevTun", "isVpnMode", "ensureDefaultSettings", "ensureDefaultValue", "key", "default", "migrateHysteria2PinSHA256", "migrateServerListToSubscriptions", "ensureDefaultSubscription", "app_fdroidDebug"})
public final class SettingsManager {
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.handler.SettingsManager INSTANCE = null;
    
    private SettingsManager() {
        super();
    }
    
    public final void initApp(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    /**
     * Initialize routing rulesets.
     * @param context The application context.
     */
    private final void initRoutingRulesets(android.content.Context context) {
    }
    
    /**
     * Get preset routing rulesets.
     * @param context The application context.
     * @param index The index of the routing type.
     * @return A mutable list of RulesetItem.
     */
    private final java.util.List<com.v2ray.ang.dto.RulesetItem> getPresetRoutingRulesets(android.content.Context context, int index) {
        return null;
    }
    
    /**
     * Reset routing rulesets from presets.
     * @param context The application context.
     * @param index The index of the routing type.
     */
    public final void resetRoutingRulesetsFromPresets(@org.jetbrains.annotations.NotNull()
    android.content.Context context, int index) {
    }
    
    /**
     * Reset routing rulesets.
     * @param content The content of the rulesets.
     * @return True if successful, false otherwise.
     */
    public final boolean resetRoutingRulesets(@org.jetbrains.annotations.Nullable()
    java.lang.String content) {
        return false;
    }
    
    /**
     * Common method to reset routing rulesets.
     * @param rulesetList The list of rulesets.
     */
    private final void resetRoutingRulesetsCommon(java.util.List<com.v2ray.ang.dto.RulesetItem> rulesetList) {
    }
    
    /**
     * Get a routing ruleset by index.
     * @param index The index of the ruleset.
     * @return The RulesetItem.
     */
    @org.jetbrains.annotations.Nullable()
    public final com.v2ray.ang.dto.RulesetItem getRoutingRuleset(int index) {
        return null;
    }
    
    /**
     * Save a routing ruleset.
     * @param index The index of the ruleset.
     * @param ruleset The RulesetItem to save.
     */
    public final void saveRoutingRuleset(int index, @org.jetbrains.annotations.Nullable()
    com.v2ray.ang.dto.RulesetItem ruleset) {
    }
    
    /**
     * Remove a routing ruleset by index.
     * @param index The index of the ruleset.
     */
    public final void removeRoutingRuleset(int index) {
    }
    
    /**
     * Check if routing rulesets bypass LAN.
     * @return True if bypassing LAN, false otherwise.
     */
    public final boolean routingRulesetsBypassLan() {
        return false;
    }
    
    /**
     * Swap routing rulesets.
     * @param fromPosition The position to swap from.
     * @param toPosition The position to swap to.
     */
    public final void swapRoutingRuleset(int fromPosition, int toPosition) {
    }
    
    /**
     * Swap subscriptions.
     * @param fromPosition The position to swap from.
     * @param toPosition The position to swap to.
     */
    public final void swapSubscriptions(int fromPosition, int toPosition) {
    }
    
    /**
     * Get server via remarks.
     * @param remarks The remarks of the server.
     * @return The ProfileItem.
     */
    @org.jetbrains.annotations.Nullable()
    public final com.v2ray.ang.dto.ProfileItem getServerViaRemarks(@org.jetbrains.annotations.Nullable()
    java.lang.String remarks) {
        return null;
    }
    
    /**
     * Removes the subscription.
     * If there are no remaining subscriptions,
     * it creates a new default subscription to ensure that ungroup
     */
    public final void removeSubscriptionWithDefault(@org.jetbrains.annotations.NotNull()
    java.lang.String subid) {
    }
    
    /**
     * Get the SOCKS port.
     * @return The SOCKS port.
     */
    public final int getSocksPort() {
        return 0;
    }
    
    /**
     * Get the HTTP port.
     * @return The HTTP port.
     */
    public final int getHttpPort() {
        return 0;
    }
    
    /**
     * Initialize assets.
     * @param context The application context.
     * @param assets The AssetManager.
     */
    public final void initAssets(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.content.res.AssetManager assets) {
    }
    
    /**
     * Get domestic DNS servers from preference.
     * @return A list of domestic DNS servers.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getDomesticDnsServers() {
        return null;
    }
    
    /**
     * Get remote DNS servers from preference.
     * @return A list of remote DNS servers.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getRemoteDnsServers() {
        return null;
    }
    
    /**
     * Get VPN DNS servers from preference.
     * @return A list of VPN DNS servers.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getVpnDnsServers() {
        return null;
    }
    
    /**
     * Get delay test URL.
     * @param second Whether to use the second URL.
     * @return The delay test URL.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDelayTestUrl(boolean second) {
        return null;
    }
    
    /**
     * Get the locale.
     * @return The locale.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.Locale getLocale() {
        return null;
    }
    
    /**
     * Set night mode.
     */
    public final void setNightMode() {
    }
    
    /**
     * Retrieves the currently selected VPN interface address configuration.
     * This method reads the user's preference for VPN interface addressing and returns
     * the corresponding configuration containing IPv4 and IPv6 addresses.
     *
     * @return The selected VpnInterfaceAddressConfig instance, or the default configuration
     *        if no valid selection is found or if the stored index is invalid.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.v2ray.ang.enums.VpnInterfaceAddressConfig getCurrentVpnInterfaceAddressConfig() {
        return null;
    }
    
    /**
     * Get the VPN MTU from settings, defaulting to AppConfig.VPN_MTU.
     */
    public final int getVpnMtu() {
        return 0;
    }
    
    /**
     * Check if HEV TUN is being used.
     * @return True if HEV TUN is used, false otherwise.
     */
    public final boolean isUsingHevTun() {
        return false;
    }
    
    /**
     * Check if VPN mode is enabled.
     * @return True if VPN mode is enabled, false otherwise.
     */
    public final boolean isVpnMode() {
        return false;
    }
    
    /**
     * Ensure default settings are present in MMKV.
     */
    private final void ensureDefaultSettings() {
    }
    
    private final void ensureDefaultValue(java.lang.String key, java.lang.String p1_772401952) {
    }
    
    private final void migrateHysteria2PinSHA256() {
    }
    
    /**
     * Migrates server list from legacy KEY_ANG_CONFIGS to subscription-based storage.
     * This method should be called once during app initialization after the storage structure change.
     * Servers are grouped by their subscriptionId into respective subscription's serverList.
     * Servers without subscription are moved to the default subscription.
     * After migration, KEY_ANG_CONFIGS is removed.
     */
    private final void migrateServerListToSubscriptions() {
    }
    
    /**
     * Ensures the default subscription exists for ungrouped servers.
     * This subscription is used internally to store servers without a subscription.
     * Made public for migration in SettingsManager.
     */
    private final void ensureDefaultSubscription() {
    }
}