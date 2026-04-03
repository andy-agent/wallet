package com.v2ray.ang.handler;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0005J\u001e\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0086@\u00a2\u0006\u0002\u0010\u0011J\u001e\u0010\u0012\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0013\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\u0014J\u0010\u0010\u0015\u001a\u00020\u00102\u0006\u0010\u000f\u001a\u00020\u0010H\u0002J\u0010\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0017H\u0002J\u0010\u0010\u0019\u001a\u00020\t2\u0006\u0010\u001a\u001a\u00020\u0010H\u0002R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001b"}, d2 = {"Lcom/v2ray/ang/handler/WebDavManager;", "", "<init>", "()V", "cfg", "Lcom/v2ray/ang/dto/WebDavConfig;", "client", "Lokhttp3/OkHttpClient;", "init", "", "config", "uploadFile", "", "localFile", "Ljava/io/File;", "remoteFileName", "", "(Ljava/io/File;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "downloadFile", "destFile", "(Ljava/lang/String;Ljava/io/File;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "buildRemoteUrl", "applyAuth", "Lokhttp3/Request$Builder;", "builder", "ensureRemoteDirs", "dirUrl", "app_playstoreDebug"})
public final class WebDavManager {
    @org.jetbrains.annotations.Nullable()
    private static com.v2ray.ang.dto.WebDavConfig cfg;
    @org.jetbrains.annotations.Nullable()
    private static okhttp3.OkHttpClient client;
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.handler.WebDavManager INSTANCE = null;
    
    private WebDavManager() {
        super();
    }
    
    /**
     * Initialize the WebDAV manager with a configuration and build an OkHttp client.
     *
     * @param config WebDavConfig containing baseUrl, credentials, remoteBasePath and timeoutSeconds.
     */
    public final void init(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.dto.WebDavConfig config) {
    }
    
    /**
     * Upload a local file to a remote file name under the configured remoteBasePath.
     * The provided `remoteFileName` should be a file name (e.g. "backup_ng.zip").
     * The method will attempt to create parent directories via MKCOL before PUT.
     *
     * @param localFile File to upload.
     * @param remoteFileName Remote file name relative to configured remoteBasePath.
     * @return true if upload succeeded (HTTP 2xx), false otherwise.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object uploadFile(@org.jetbrains.annotations.NotNull()
    java.io.File localFile, @org.jetbrains.annotations.NotNull()
    java.lang.String remoteFileName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    /**
     * Download a remote file (relative to configured remoteBasePath) into a local file.
     *
     * @param remoteFileName Remote file name relative to configured remoteBasePath.
     * @param destFile Local destination file to write to.
     * @return true if download and write succeeded, false otherwise.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object downloadFile(@org.jetbrains.annotations.NotNull()
    java.lang.String remoteFileName, @org.jetbrains.annotations.NotNull()
    java.io.File destFile, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    /**
     * Build a full remote URL by combining the configured base URL, the configured
     * remote base path and a file name provided by the caller.
     *
     * Example: baseUrl="https://example.com/remote.php/dav", remoteBasePath="backups",
     * remoteFileName="backup_ng.zip" => "https://example.com/remote.php/dav/backups/backup_ng.zip"
     *
     * @param remoteFileName A file name relative to the configured remoteBasePath (no leading slash required).
     * @return Full URL string used for HTTP operations.
     */
    private final java.lang.String buildRemoteUrl(java.lang.String remoteFileName) {
        return null;
    }
    
    /**
     * Apply HTTP Basic authentication headers to the given request builder when
     * username is configured in `cfg`.
     *
     * @param builder OkHttp Request.Builder to modify.
     * @return The same builder instance with Authorization header applied if credentials exist.
     */
    private final okhttp3.Request.Builder applyAuth(okhttp3.Request.Builder builder) {
        return null;
    }
    
    /**
     * Ensure that each directory segment in the given directory URL exists on the
     * WebDAV server. This issues MKCOL requests for each segment in a best-effort
     * manner and ignores errors for segments that already exist.
     *
     * @param dirUrl Absolute URL to the directory that should exist (e.g. https://.../backups)
     */
    private final void ensureRemoteDirs(java.lang.String dirUrl) {
    }
}