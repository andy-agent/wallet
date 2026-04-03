package com.v2ray.ang.handler;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000r\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0005J\u0016\u0010\f\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0005J \u0010\r\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u000fH\u0002J \u0010\u0010\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u000fH\u0002J \u0010\u0011\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u000fH\u0002J(\u0010\u0012\u001a\u0004\u0018\u00010\u00132\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\u000f2\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0015H\u0002J \u0010\u0016\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u000fH\u0002J\u0012\u0010\u0017\u001a\u0004\u0018\u00010\u00132\u0006\u0010\t\u001a\u00020\nH\u0002J\b\u0010\u0018\u001a\u00020\u0019H\u0002J\u0010\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\u0013H\u0002J\u0010\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001b\u001a\u00020\u0013H\u0002J\u0010\u0010\u001e\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\u0013H\u0002J\u001a\u0010\u001f\u001a\u00020\u001d2\b\u0010 \u001a\u0004\u0018\u00010!2\u0006\u0010\u001b\u001a\u00020\u0013H\u0002J \u0010\"\u001a\u0012\u0012\u0004\u0012\u00020\u00050#j\b\u0012\u0004\u0012\u00020\u0005`$2\u0006\u0010%\u001a\u00020\u0005H\u0002J\u0010\u0010&\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\u0013H\u0002J\u0010\u0010\'\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\u0013H\u0002J\u001f\u0010(\u001a\u0004\u0018\u00010\u00192\u0006\u0010\u001b\u001a\u00020\u00132\u0006\u0010\u000e\u001a\u00020\u000fH\u0002\u00a2\u0006\u0002\u0010)J\u0018\u0010*\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\u00132\u0006\u0010+\u001a\u00020\u0005H\u0002J\u0010\u0010,\u001a\u00020\u00192\u0006\u0010-\u001a\u00020.H\u0002J\u0018\u0010/\u001a\u00020\u001d2\u0006\u0010\u001b\u001a\u00020\u00132\u0006\u0010\u000e\u001a\u00020\u000fH\u0002J\u0010\u00100\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\u0013H\u0002J\u0010\u00101\u001a\u00020\u001d2\u0006\u0010\u001b\u001a\u00020\u0013H\u0002J\u0012\u00102\u001a\u0004\u0018\u00010.2\u0006\u00103\u001a\u00020\u000fH\u0002J\u0010\u00104\u001a\u0004\u0018\u00010.2\u0006\u00105\u001a\u000206J\u0018\u00107\u001a\u0004\u0018\u00010\u00052\u0006\u00108\u001a\u0002092\u0006\u00103\u001a\u00020\u000fJ \u0010:\u001a\u00020\u001d2\u0006\u00108\u001a\u0002092\u0006\u00103\u001a\u00020\u000f2\b\u0010;\u001a\u0004\u0018\u00010\u0005R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006<"}, d2 = {"Lcom/v2ray/ang/handler/V2rayConfigManager;", "", "<init>", "()V", "initConfigCache", "", "initConfigCacheWithTun", "getV2rayConfig", "Lcom/v2ray/ang/dto/ConfigResult;", "context", "Landroid/content/Context;", "guid", "getV2rayConfig4Speedtest", "getV2rayCustomConfig", "config", "Lcom/v2ray/ang/dto/ProfileItem;", "getV2rayGroupConfig", "getV2rayNormalConfig", "getV2rayMultipleConfig", "Lcom/v2ray/ang/dto/V2rayConfig;", "configList", "", "getV2rayNormalConfig4Speedtest", "initV2rayConfig", "needTun", "", "getInbounds", "v2rayConfig", "getFakeDns", "", "getRouting", "getRoutingUserRule", "item", "Lcom/v2ray/ang/dto/RulesetItem;", "getUserRule2Domain", "Ljava/util/ArrayList;", "Lkotlin/collections/ArrayList;", "tag", "getCustomLocalDns", "getDns", "getOutbounds", "(Lcom/v2ray/ang/dto/V2rayConfig;Lcom/v2ray/ang/dto/ProfileItem;)Ljava/lang/Boolean;", "getMoreOutbounds", "subscriptionId", "updateOutboundWithGlobalSettings", "outbound", "Lcom/v2ray/ang/dto/V2rayConfig$OutboundBean;", "getBalance", "updateOutboundFragment", "resolveOutboundDomainsToHosts", "convertProfile2Outbound", "profileItem", "createInitOutbound", "configType", "Lcom/v2ray/ang/enums/EConfigType;", "populateTransportSettings", "streamSettings", "Lcom/v2ray/ang/dto/V2rayConfig$OutboundBean$StreamSettingsBean;", "populateTlsSettings", "sniExt", "app_playstoreDebug"})
public final class V2rayConfigManager {
    @org.jetbrains.annotations.Nullable()
    private static java.lang.String initConfigCache;
    @org.jetbrains.annotations.Nullable()
    private static java.lang.String initConfigCacheWithTun;
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.handler.V2rayConfigManager INSTANCE = null;
    
    private V2rayConfigManager() {
        super();
    }
    
    /**
     * Retrieves the V2ray configuration for the given GUID.
     *
     * @param context The context of the caller.
     * @param guid The unique identifier for the V2ray configuration.
     * @return A ConfigResult object containing the configuration details or indicating failure.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.v2ray.ang.dto.ConfigResult getV2rayConfig(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String guid) {
        return null;
    }
    
    /**
     * Retrieves the speedtest V2ray configuration for the given GUID.
     *
     * @param context The context of the caller.
     * @param guid The unique identifier for the V2ray configuration.
     * @return A ConfigResult object containing the configuration details or indicating failure.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.v2ray.ang.dto.ConfigResult getV2rayConfig4Speedtest(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String guid) {
        return null;
    }
    
    /**
     * Retrieves the custom V2ray configuration.
     *
     * @param guid The unique identifier for the V2ray configuration.
     * @param config The profile item containing the configuration details.
     * @return A ConfigResult object containing the result of the configuration retrieval.
     */
    private final com.v2ray.ang.dto.ConfigResult getV2rayCustomConfig(android.content.Context context, java.lang.String guid, com.v2ray.ang.dto.ProfileItem config) {
        return null;
    }
    
    /**
     * Retrieves the group V2ray configuration.
     *
     * @param context The context in which the function is called.
     * @param guid The unique identifier for the V2ray configuration.
     * @param config The profile item containing the configuration details.
     * @return A ConfigResult object containing the result of the configuration retrieval.
     */
    private final com.v2ray.ang.dto.ConfigResult getV2rayGroupConfig(android.content.Context context, java.lang.String guid, com.v2ray.ang.dto.ProfileItem config) {
        return null;
    }
    
    /**
     * Retrieves the normal V2ray configuration.
     *
     * @param context The context in which the function is called.
     * @param guid The unique identifier for the V2ray configuration.
     * @param config The profile item containing the configuration details.
     * @return A ConfigResult object containing the result of the configuration retrieval.
     */
    private final com.v2ray.ang.dto.ConfigResult getV2rayNormalConfig(android.content.Context context, java.lang.String guid, com.v2ray.ang.dto.ProfileItem config) {
        return null;
    }
    
    private final com.v2ray.ang.dto.V2rayConfig getV2rayMultipleConfig(android.content.Context context, com.v2ray.ang.dto.ProfileItem config, java.util.List<com.v2ray.ang.dto.ProfileItem> configList) {
        return null;
    }
    
    /**
     * Retrieves the normal V2ray configuration for speedtest.
     *
     * @param context The context in which the function is called.
     * @param guid The unique identifier for the V2ray configuration.
     * @param config The profile item containing the configuration details.
     * @return A ConfigResult object containing the result of the configuration retrieval.
     */
    private final com.v2ray.ang.dto.ConfigResult getV2rayNormalConfig4Speedtest(android.content.Context context, java.lang.String guid, com.v2ray.ang.dto.ProfileItem config) {
        return null;
    }
    
    /**
     * Initializes V2ray configuration.
     *
     * This function loads the V2ray configuration from assets or from a cached value.
     * It first attempts to use the cached configuration if available, otherwise reads
     * the configuration from the "v2ray_config.json" asset file.
     *
     * @param context Android context used to access application assets
     * @return V2rayConfig object parsed from the JSON configuration, or null if the configuration is empty
     */
    private final com.v2ray.ang.dto.V2rayConfig initV2rayConfig(android.content.Context context) {
        return null;
    }
    
    private final boolean needTun() {
        return false;
    }
    
    /**
     * Configures the inbound settings for V2ray.
     *
     * This function sets up the listening ports, sniffing options, and other inbound-related configurations.
     *
     * @param v2rayConfig The V2ray configuration object to be modified
     * @return true if inbound configuration was successful, false otherwise
     */
    private final boolean getInbounds(com.v2ray.ang.dto.V2rayConfig v2rayConfig) {
        return false;
    }
    
    /**
     * Configures the fake DNS settings if enabled.
     *
     * Adds FakeDNS configuration to v2rayConfig if both local DNS and fake DNS are enabled.
     *
     * @param v2rayConfig The V2ray configuration object to be modified
     */
    private final void getFakeDns(com.v2ray.ang.dto.V2rayConfig v2rayConfig) {
    }
    
    /**
     * Configures routing settings for V2ray.
     *
     * Sets up the domain strategy and adds routing rules from saved rulesets.
     *
     * @param v2rayConfig The V2ray configuration object to be modified
     * @return true if routing configuration was successful, false otherwise
     */
    private final boolean getRouting(com.v2ray.ang.dto.V2rayConfig v2rayConfig) {
        return false;
    }
    
    /**
     * Adds a specific ruleset item to the routing configuration.
     *
     * @param item The ruleset item to add
     * @param v2rayConfig The V2ray configuration object to be modified
     */
    private final void getRoutingUserRule(com.v2ray.ang.dto.RulesetItem item, com.v2ray.ang.dto.V2rayConfig v2rayConfig) {
    }
    
    /**
     * Retrieves domain rules for a specific outbound tag.
     *
     * Searches through all rulesets to find domains targeting the specified tag.
     *
     * @param tag The outbound tag to search for
     * @return ArrayList of domain rules matching the tag
     */
    private final java.util.ArrayList<java.lang.String> getUserRule2Domain(java.lang.String tag) {
        return null;
    }
    
    /**
     * Configures custom local DNS settings.
     *
     * Sets up DNS inbound, outbound, and routing rules for local DNS resolution.
     *
     * @param v2rayConfig The V2ray configuration object to be modified
     * @return true if custom local DNS configuration was successful, false otherwise
     */
    private final boolean getCustomLocalDns(com.v2ray.ang.dto.V2rayConfig v2rayConfig) {
        return false;
    }
    
    /**
     * Configures the DNS settings for V2ray.
     *
     * Sets up DNS servers, hosts, and routing rules for DNS resolution.
     *
     * @param v2rayConfig The V2ray configuration object to be modified
     * @return true if DNS configuration was successful, false otherwise
     */
    private final boolean getDns(com.v2ray.ang.dto.V2rayConfig v2rayConfig) {
        return false;
    }
    
    /**
     * Configures the primary outbound connection.
     *
     * Converts the profile to an outbound configuration and applies global settings.
     *
     * @param v2rayConfig The V2ray configuration object to be modified
     * @param config The profile item containing connection details
     * @return true if outbound configuration was successful, null if there was an error
     */
    private final java.lang.Boolean getOutbounds(com.v2ray.ang.dto.V2rayConfig v2rayConfig, com.v2ray.ang.dto.ProfileItem config) {
        return null;
    }
    
    /**
     * Configures additional outbound connections for proxy chaining.
     *
     * Sets up previous and next proxies in a subscription for advanced routing capabilities.
     *
     * @param v2rayConfig The V2ray configuration object to be modified
     * @param subscriptionId The subscription ID to look up related proxies
     * @return true if additional outbounds were configured successfully, false otherwise
     */
    private final boolean getMoreOutbounds(com.v2ray.ang.dto.V2rayConfig v2rayConfig, java.lang.String subscriptionId) {
        return false;
    }
    
    /**
     * Updates outbound settings based on global preferences.
     *
     * Applies multiplexing and protocol-specific settings to an outbound connection.
     *
     * @param outbound The outbound connection to update
     * @return true if the update was successful, false otherwise
     */
    private final boolean updateOutboundWithGlobalSettings(com.v2ray.ang.dto.V2rayConfig.OutboundBean outbound) {
        return false;
    }
    
    /**
     * Configures load balancing settings for the V2ray configuration.
     *
     * @param v2rayConfig The V2ray configuration object to be modified with balancing settings
     * @param config The profile item containing policy group settings
     */
    private final void getBalance(com.v2ray.ang.dto.V2rayConfig v2rayConfig, com.v2ray.ang.dto.ProfileItem config) {
    }
    
    /**
     * Updates the outbound with fragment settings for traffic optimization.
     *
     * Configures packet fragmentation for TLS and REALITY protocols if enabled.
     *
     * @param v2rayConfig The V2ray configuration object to be modified
     * @return true if fragment configuration was successful, false otherwise
     */
    private final boolean updateOutboundFragment(com.v2ray.ang.dto.V2rayConfig v2rayConfig) {
        return false;
    }
    
    /**
     * Resolves domain names to IP addresses in outbound connections.
     *
     * Pre-resolves domains to improve connection speed and reliability.
     *
     * @param v2rayConfig The V2ray configuration object to be modified
     */
    private final void resolveOutboundDomainsToHosts(com.v2ray.ang.dto.V2rayConfig v2rayConfig) {
    }
    
    /**
     * Converts a profile item to an outbound configuration.
     *
     * Creates appropriate outbound settings based on the protocol type.
     *
     * @param profileItem The profile item to convert
     * @return OutboundBean configuration for the profile, or null if not supported
     */
    private final com.v2ray.ang.dto.V2rayConfig.OutboundBean convertProfile2Outbound(com.v2ray.ang.dto.ProfileItem profileItem) {
        return null;
    }
    
    /**
     * Creates an initial outbound configuration for a specific protocol type.
     *
     * Provides a template configuration for different protocol types.
     *
     * @param configType The type of configuration to create
     * @return An initial OutboundBean for the specified configuration type, or null for custom types
     */
    @org.jetbrains.annotations.Nullable()
    public final com.v2ray.ang.dto.V2rayConfig.OutboundBean createInitOutbound(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.enums.EConfigType configType) {
        return null;
    }
    
    /**
     * Configures transport settings for an outbound connection.
     *
     * Sets up protocol-specific transport options based on the profile settings.
     *
     * @param streamSettings The stream settings to configure
     * @param profileItem The profile containing transport configuration
     * @return The Server Name Indication (SNI) value to use, or null if not applicable
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String populateTransportSettings(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.dto.V2rayConfig.OutboundBean.StreamSettingsBean streamSettings, @org.jetbrains.annotations.NotNull()
    com.v2ray.ang.dto.ProfileItem profileItem) {
        return null;
    }
    
    /**
     * Configures TLS or REALITY security settings for an outbound connection.
     *
     * Sets up security-related parameters like certificates, fingerprints, and SNI.
     *
     * @param streamSettings The stream settings to configure
     * @param profileItem The profile containing security configuration
     * @param sniExt An external SNI value to use if the profile doesn't specify one
     */
    public final void populateTlsSettings(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.dto.V2rayConfig.OutboundBean.StreamSettingsBean streamSettings, @org.jetbrains.annotations.NotNull()
    com.v2ray.ang.dto.ProfileItem profileItem, @org.jetbrains.annotations.Nullable()
    java.lang.String sniExt) {
    }
}