package com.v2ray.ang.util;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005J\u000e\u0010\u0007\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\u0005J \u0010\t\u001a\n\u0012\u0004\u0012\u00020\u0005\u0018\u00010\n2\u0006\u0010\u000b\u001a\u00020\u00052\b\b\u0002\u0010\f\u001a\u00020\rJ\"\u0010\u000e\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u000f\u001a\u00020\u00052\u0006\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u0011J.\u0010\u0013\u001a\u00020\u00052\b\u0010\u000f\u001a\u0004\u0018\u00010\u00052\b\u0010\u0014\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u0011J6\u0010\u0015\u001a\u0004\u0018\u00010\u00162\u0006\u0010\u0017\u001a\u00020\u00052\u0006\u0010\u0018\u001a\u00020\u00112\b\b\u0002\u0010\u0019\u001a\u00020\u00112\b\b\u0002\u0010\u001a\u001a\u00020\u00112\b\b\u0002\u0010\u001b\u001a\u00020\rJ\u0010\u0010\u001c\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u001d\u001a\u00020\u0016\u00a8\u0006\u001e"}, d2 = {"Lcom/v2ray/ang/util/HttpUtil;", "", "<init>", "()V", "toIdnUrl", "", "str", "toIdnDomain", "domain", "resolveHostToIP", "", "host", "ipv6Preferred", "", "getUrlContent", "url", "timeout", "", "httpPort", "getUrlContentWithUserAgent", "userAgent", "createProxyConnection", "Ljava/net/HttpURLConnection;", "urlStr", "port", "connectTimeout", "readTimeout", "needStream", "resolveLocation", "conn", "app_fdroidDebug"})
public final class HttpUtil {
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.util.HttpUtil INSTANCE = null;
    
    private HttpUtil() {
        super();
    }
    
    /**
     * Converts the domain part of a URL string to its IDN (Punycode, ASCII Compatible Encoding) format.
     *
     * For example, a URL like "https://例子.中国/path" will be converted to "https://xn--fsqu00a.xn--fiqs8s/path".
     *
     * @param str The URL string to convert (can contain non-ASCII characters in the domain).
     * @return The URL string with the domain part converted to ASCII-compatible (Punycode) format.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String toIdnUrl(@org.jetbrains.annotations.NotNull()
    java.lang.String str) {
        return null;
    }
    
    /**
     * Converts a Unicode domain name to its IDN (Punycode, ASCII Compatible Encoding) format.
     * If the input is an IP address or already an ASCII domain, returns the original string.
     *
     * @param domain The domain string to convert (can include non-ASCII internationalized characters).
     * @return The domain in ASCII-compatible (Punycode) format, or the original string if input is an IP or already ASCII.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String toIdnDomain(@org.jetbrains.annotations.NotNull()
    java.lang.String domain) {
        return null;
    }
    
    /**
     * Resolves a hostname to an IP address, returns original input if it's already an IP
     *
     * @param host The hostname or IP address to resolve
     * @param ipv6Preferred Whether to prefer IPv6 addresses, defaults to false
     * @return The resolved IP address or the original input (if it's already an IP or resolution fails)
     */
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<java.lang.String> resolveHostToIP(@org.jetbrains.annotations.NotNull()
    java.lang.String host, boolean ipv6Preferred) {
        return null;
    }
    
    /**
     * Retrieves the content of a URL as a string.
     *
     * @param url The URL to fetch content from.
     * @param timeout The timeout value in milliseconds.
     * @param httpPort The HTTP port to use.
     * @return The content of the URL as a string.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getUrlContent(@org.jetbrains.annotations.NotNull()
    java.lang.String url, int timeout, int httpPort) {
        return null;
    }
    
    /**
     * Retrieves the content of a URL as a string with a custom User-Agent header.
     *
     * @param url The URL to fetch content from.
     * @param timeout The timeout value in milliseconds.
     * @param httpPort The HTTP port to use.
     * @return The content of the URL as a string.
     * @throws IOException If an I/O error occurs.
     */
    @kotlin.jvm.Throws(exceptionClasses = {java.io.IOException.class})
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getUrlContentWithUserAgent(@org.jetbrains.annotations.Nullable()
    java.lang.String url, @org.jetbrains.annotations.Nullable()
    java.lang.String userAgent, int timeout, int httpPort) throws java.io.IOException {
        return null;
    }
    
    /**
     * Creates an HttpURLConnection object connected through a proxy.
     *
     * @param urlStr The target URL address.
     * @param port The port of the proxy server.
     * @param connectTimeout The connection timeout in milliseconds (default is 15000 ms).
     * @param readTimeout The read timeout in milliseconds (default is 15000 ms).
     * @param needStream Whether the connection needs to support streaming.
     * @return Returns a configured HttpURLConnection object, or null if it fails.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.net.HttpURLConnection createProxyConnection(@org.jetbrains.annotations.NotNull()
    java.lang.String urlStr, int port, int connectTimeout, int readTimeout, boolean needStream) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String resolveLocation(@org.jetbrains.annotations.NotNull()
    java.net.HttpURLConnection conn) {
        return null;
    }
}