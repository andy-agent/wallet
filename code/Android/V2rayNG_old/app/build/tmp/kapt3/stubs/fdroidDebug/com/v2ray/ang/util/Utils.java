package com.v2ray.ang.util;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0019\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0005\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0007\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\nJ#\u0010\u000b\u001a\u00020\f2\u000e\u0010\r\u001a\n\u0012\u0006\b\u0001\u0012\u00020\n0\u000e2\u0006\u0010\u000f\u001a\u00020\n\u00a2\u0006\u0002\u0010\u0010J\u001a\u0010\u0011\u001a\u00020\f2\b\u0010\u0012\u001a\u0004\u0018\u00010\n2\b\b\u0002\u0010\u0013\u001a\u00020\fJ\u000e\u0010\u0014\u001a\u00020\n2\u0006\u0010\u0015\u001a\u00020\u0016J\u0016\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0019\u001a\u00020\nJ\u0010\u0010\u001a\u001a\u00020\n2\b\u0010\t\u001a\u0004\u0018\u00010\nJ\u0012\u0010\u001b\u001a\u0004\u0018\u00010\n2\b\u0010\t\u001a\u0004\u0018\u00010\nJ\u0018\u0010\u001c\u001a\u00020\n2\u0006\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u001d\u001a\u00020\u001eJ\u0010\u0010\u001f\u001a\u00020\u001e2\b\u0010\u000f\u001a\u0004\u0018\u00010\nJ\u000e\u0010 \u001a\u00020\u001e2\u0006\u0010\u000f\u001a\u00020\nJ\u0010\u0010!\u001a\u00020\u001e2\b\u0010\"\u001a\u0004\u0018\u00010\nJ\u0010\u0010#\u001a\u00020\u001e2\u0006\u0010\u000f\u001a\u00020\nH\u0002J\u0010\u0010$\u001a\u00020\u001e2\u0006\u0010\u000f\u001a\u00020\nH\u0002J\u000e\u0010%\u001a\u00020\u001e2\u0006\u0010&\u001a\u00020\nJ\u0010\u0010\'\u001a\u00020\u001e2\b\u0010\u000f\u001a\u0004\u0018\u00010\nJ\u0016\u0010(\u001a\u00020\u00182\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010)\u001a\u00020\nJ\u0006\u0010*\u001a\u00020\nJ\u000e\u0010+\u001a\u00020\n2\u0006\u0010,\u001a\u00020\nJ\u000e\u0010-\u001a\u00020\n2\u0006\u0010,\u001a\u00020\nJ\u000e\u0010.\u001a\u00020\n2\u0006\u0010,\u001a\u00020\nJ\u000e\u0010/\u001a\u00020\n2\u0006\u0010,\u001a\u00020\nJ\u0018\u00100\u001a\u00020\n2\b\u0010\u0015\u001a\u0004\u0018\u00010\u00162\u0006\u00101\u001a\u00020\nJ\u0010\u00102\u001a\u00020\n2\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016J\u0006\u00103\u001a\u00020\nJ\u000e\u00104\u001a\u00020\u001e2\u0006\u0010\u0015\u001a\u00020\u0016J\u0010\u00105\u001a\u00020\n2\b\u00106\u001a\u0004\u0018\u00010\nJ\u0006\u00107\u001a\u000208J\u000e\u00109\u001a\u00020\n2\u0006\u0010\u0012\u001a\u00020\nJ\u0014\u0010:\u001a\u00020\f2\f\u0010;\u001a\b\u0012\u0004\u0012\u00020\f0<J\u0010\u0010=\u001a\u00020\u001e2\b\u0010\u000f\u001a\u0004\u0018\u00010\nJ\u0006\u0010>\u001a\u00020\fJ\u0006\u0010?\u001a\u00020\u001eJ\u0006\u0010@\u001a\u00020\u001eJ\u0010\u0010A\u001a\u00020B2\u0006\u0010C\u001a\u00020DH\u0002J\u0016\u0010E\u001a\u00020\u001e2\u0006\u0010C\u001a\u00020\n2\u0006\u0010F\u001a\u00020\nJ)\u0010G\u001a\u00020\n2\b\u0010H\u001a\u0004\u0018\u00010B2\b\b\u0002\u0010I\u001a\u00020\n2\b\b\u0002\u0010J\u001a\u000208\u00a2\u0006\u0002\u0010KR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006L"}, d2 = {"Lcom/v2ray/ang/util/Utils;", "", "<init>", "()V", "IPV4_REGEX", "Lkotlin/text/Regex;", "IPV6_REGEX", "getEditable", "Landroid/text/Editable;", "text", "", "arrayFind", "", "array", "", "value", "([Ljava/lang/String;Ljava/lang/String;)I", "parseInt", "str", "default", "getClipboard", "context", "Landroid/content/Context;", "setClipboard", "", "content", "decode", "tryDecodeBase64", "encode", "removePadding", "", "isIpAddress", "isPureIpAddress", "isDomainName", "input", "isIpv4Address", "isIpv6Address", "isCoreDNSAddress", "s", "isValidUrl", "openUri", "uriString", "getUuid", "urlDecode", "url", "urlEncode", "decodeURIComponent", "encodeURIComponent", "readTextFromAssets", "fileName", "userAssetPath", "getDeviceIdForXUDPBaseKey", "getDarkModeStatus", "getIpv6Address", "address", "getSysLocale", "Ljava/util/Locale;", "fixIllegalUrl", "findFreePort", "ports", "", "isValidSubUrl", "receiverFlags", "isXray", "isGoogleFlavor", "inetAddressToLong", "", "ip", "Ljava/net/InetAddress;", "isIpInCidr", "cidr", "formatTimestamp", "ts", "pattern", "locale", "(Ljava/lang/Long;Ljava/lang/String;Ljava/util/Locale;)Ljava/lang/String;", "app_fdroidDebug"})
public final class Utils {
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.text.Regex IPV4_REGEX = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.text.Regex IPV6_REGEX = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.util.Utils INSTANCE = null;
    
    private Utils() {
        super();
    }
    
    /**
     * Convert string to editable for Kotlin.
     *
     * @param text The string to convert.
     * @return An Editable instance containing the text.
     */
    @org.jetbrains.annotations.NotNull()
    public final android.text.Editable getEditable(@org.jetbrains.annotations.Nullable()
    java.lang.String text) {
        return null;
    }
    
    /**
     * Find the position of a value in an array.
     *
     * @param array The array to search.
     * @param value The value to find.
     * @return The index of the value in the array, or -1 if not found.
     */
    public final int arrayFind(@org.jetbrains.annotations.NotNull()
    java.lang.String[] array, @org.jetbrains.annotations.NotNull()
    java.lang.String value) {
        return 0;
    }
    
    /**
     * Parse a string to an integer with a default value.
     *
     * @param str The string to parse.
     * @param default The default value if parsing fails.
     * @return The parsed integer, or the default value if parsing fails.
     */
    public final int parseInt(@org.jetbrains.annotations.Nullable()
    java.lang.String str, int p1_772401952) {
        return 0;
    }
    
    /**
     * Get text from the clipboard.
     *
     * @param context The context to use.
     * @return The text from the clipboard, or an empty string if an error occurs.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getClipboard(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    /**
     * Set text to the clipboard.
     *
     * @param context The context to use.
     * @param content The text to set to the clipboard.
     */
    public final void setClipboard(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String content) {
    }
    
    /**
     * Decode a base64 encoded string.
     *
     * @param text The base64 encoded string.
     * @return The decoded string, or an empty string if decoding fails.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String decode(@org.jetbrains.annotations.Nullable()
    java.lang.String text) {
        return null;
    }
    
    /**
     * Try to decode a base64 encoded string.
     *
     * @param text The base64 encoded string.
     * @return The decoded string, or null if decoding fails.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String tryDecodeBase64(@org.jetbrains.annotations.Nullable()
    java.lang.String text) {
        return null;
    }
    
    /**
     * Encode a string to base64.
     *
     * @param text The string to encode.
     * @param removePadding
     * @return The base64 encoded string, or an empty string if encoding fails.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String encode(@org.jetbrains.annotations.NotNull()
    java.lang.String text, boolean removePadding) {
        return null;
    }
    
    /**
     * Check if a string is a valid IP address.
     *
     * @param value The string to check.
     * @return True if the string is a valid IP address, false otherwise.
     */
    public final boolean isIpAddress(@org.jetbrains.annotations.Nullable()
    java.lang.String value) {
        return false;
    }
    
    /**
     * Check if a string is a pure IP address (IPv4 or IPv6).
     *
     * @param value The string to check.
     * @return True if the string is a pure IP address, false otherwise.
     */
    public final boolean isPureIpAddress(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
        return false;
    }
    
    /**
     * Check if a string is a valid domain name.
     *
     * A valid domain name must not be an IP address and must be a valid URL format.
     *
     * @param input The string to check.
     * @return True if the string is a valid domain name, false otherwise.
     */
    public final boolean isDomainName(@org.jetbrains.annotations.Nullable()
    java.lang.String input) {
        return false;
    }
    
    /**
     * Check if a string is a valid IPv4 address.
     *
     * @param value The string to check.
     * @return True if the string is a valid IPv4 address, false otherwise.
     */
    private final boolean isIpv4Address(java.lang.String value) {
        return false;
    }
    
    /**
     * Check if a string is a valid IPv6 address.
     *
     * @param value The string to check.
     * @return True if the string is a valid IPv6 address, false otherwise.
     */
    private final boolean isIpv6Address(java.lang.String value) {
        return false;
    }
    
    /**
     * Check if a string is a CoreDNS address.
     *
     * @param s The string to check.
     * @return True if the string is a CoreDNS address, false otherwise.
     */
    public final boolean isCoreDNSAddress(@org.jetbrains.annotations.NotNull()
    java.lang.String s) {
        return false;
    }
    
    /**
     * Check if a string is a valid URL.
     *
     * @param value The string to check.
     * @return True if the string is a valid URL, false otherwise.
     */
    public final boolean isValidUrl(@org.jetbrains.annotations.Nullable()
    java.lang.String value) {
        return false;
    }
    
    /**
     * Open a URI in a browser.
     *
     * @param context The context to use.
     * @param uriString The URI string to open.
     */
    public final void openUri(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String uriString) {
    }
    
    /**
     * Generate a UUID.
     *
     * @return A UUID string without dashes.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getUuid() {
        return null;
    }
    
    /**
     * Decode a URL-encoded string.
     *
     * @param url The URL-encoded string.
     * @return The decoded string, or the original string if decoding fails.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String urlDecode(@org.jetbrains.annotations.NotNull()
    java.lang.String url) {
        return null;
    }
    
    /**
     * Encode a string to URL-encoded format.
     *
     * @param url The string to encode.
     * @return The URL-encoded string, or the original string if encoding fails.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String urlEncode(@org.jetbrains.annotations.NotNull()
    java.lang.String url) {
        return null;
    }
    
    /**
     * Decode a "encodeURIComponent" string.
     *
     * @param url The "encodeURIComponent" string.
     * @return The decoded string, or the original string if decoding fails.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String decodeURIComponent(@org.jetbrains.annotations.NotNull()
    java.lang.String url) {
        return null;
    }
    
    /**
     * Encode a string to "encodeURIComponent" format.
     *
     * @param url The string to encode.
     * @return The "encodeURIComponent" encoded string, or the original string if encoding fails.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String encodeURIComponent(@org.jetbrains.annotations.NotNull()
    java.lang.String url) {
        return null;
    }
    
    /**
     * Read text from an asset file.
     *
     * @param context The context to use.
     * @param fileName The name of the asset file.
     * @return The content of the asset file as a string.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String readTextFromAssets(@org.jetbrains.annotations.Nullable()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String fileName) {
        return null;
    }
    
    /**
     * Get the path to the user asset directory.
     *
     * @param context The context to use.
     * @return The path to the user asset directory.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String userAssetPath(@org.jetbrains.annotations.Nullable()
    android.content.Context context) {
        return null;
    }
    
    /**
     * Get the device ID for XUDP base key.
     *
     * @return The device ID for XUDP base key.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDeviceIdForXUDPBaseKey() {
        return null;
    }
    
    /**
     * Get the dark mode status.
     *
     * @param context The context to use.
     * @return True if dark mode is enabled, false otherwise.
     */
    public final boolean getDarkModeStatus(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    /**
     * Get the IPv6 address in a formatted string.
     *
     * @param address The IPv6 address.
     * @return The formatted IPv6 address, or the original address if not valid.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getIpv6Address(@org.jetbrains.annotations.Nullable()
    java.lang.String address) {
        return null;
    }
    
    /**
     * Get the system locale.
     *
     * @return The system locale.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.Locale getSysLocale() {
        return null;
    }
    
    /**
     * Fix illegal characters in a URL.
     *
     * @param str The URL string.
     * @return The URL string with illegal characters replaced.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String fixIllegalUrl(@org.jetbrains.annotations.NotNull()
    java.lang.String str) {
        return null;
    }
    
    /**
     * Find a free port from a list of ports.
     *
     * @param ports The list of ports to check.
     * @return The first free port found.
     * @throws IOException If no free port is found.
     */
    public final int findFreePort(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Integer> ports) {
        return 0;
    }
    
    /**
     * Check if a string is a valid subscription URL.
     *
     * @param value The string to check.
     * @return True if the string is a valid subscription URL, false otherwise.
     */
    public final boolean isValidSubUrl(@org.jetbrains.annotations.Nullable()
    java.lang.String value) {
        return false;
    }
    
    /**
     * Get the receiver flags based on the Android version.
     *
     * @return The receiver flags.
     */
    public final int receiverFlags() {
        return 0;
    }
    
    /**
     * Check if the package is Xray.
     *
     * @return True if the package is Xray, false otherwise.
     */
    public final boolean isXray() {
        return false;
    }
    
    /**
     * Check if it is the Google Play version.
     *
     * @return True if the package is Google Play, false otherwise.
     */
    public final boolean isGoogleFlavor() {
        return false;
    }
    
    /**
     * Converts an InetAddress to its long representation
     *
     * @param ip The InetAddress to convert
     * @return The long representation of the IP address
     */
    private final long inetAddressToLong(java.net.InetAddress ip) {
        return 0L;
    }
    
    /**
     * Check if an IP address is within a CIDR range
     *
     * @param ip The IP address to check
     * @param cidr The CIDR notation range (e.g., "192.168.1.0/24")
     * @return True if the IP is within the CIDR range, false otherwise
     */
    public final boolean isIpInCidr(@org.jetbrains.annotations.NotNull()
    java.lang.String ip, @org.jetbrains.annotations.NotNull()
    java.lang.String cidr) {
        return false;
    }
    
    /**
     * Format a timestamp (milliseconds since epoch) into a date string.
     * Returns empty string for null or non-positive timestamps.
     * @param ts timestamp in milliseconds or null
     * @param pattern SimpleDateFormat pattern, default "yyyy-MM-dd HH:mm"
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String formatTimestamp(@org.jetbrains.annotations.Nullable()
    java.lang.Long ts, @org.jetbrains.annotations.NotNull()
    java.lang.String pattern, @org.jetbrains.annotations.NotNull()
    java.util.Locale locale) {
        return null;
    }
}