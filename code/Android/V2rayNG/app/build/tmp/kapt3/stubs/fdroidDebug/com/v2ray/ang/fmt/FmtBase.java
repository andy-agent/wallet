package com.v2ray.ang.fmt;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0016\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J@\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\b\u001a\u0004\u0018\u00010\u00052&\u0010\t\u001a\"\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u0005\u0018\u00010\nj\u0010\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u0005\u0018\u0001`\u000bJ\u001a\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\r2\u0006\u0010\u000e\u001a\u00020\u000fJ*\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0006\u001a\u00020\u00072\u0012\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\r2\u0006\u0010\u0013\u001a\u00020\u0014J*\u0010\u0015\u001a\u001e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\nj\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u0005`\u000b2\u0006\u0010\u0006\u001a\u00020\u0007J\u000e\u0010\u0016\u001a\u00020\u00052\u0006\u0010\u0017\u001a\u00020\u0007\u00a8\u0006\u0018"}, d2 = {"Lcom/v2ray/ang/fmt/FmtBase;", "", "<init>", "()V", "toUri", "", "config", "Lcom/v2ray/ang/dto/ProfileItem;", "userInfo", "dicQuery", "Ljava/util/HashMap;", "Lkotlin/collections/HashMap;", "getQueryParam", "", "uri", "Ljava/net/URI;", "getItemFormQuery", "", "queryParam", "allowInsecure", "", "getQueryDic", "getServerAddress", "profileItem", "app_fdroidDebug"})
public class FmtBase {
    
    public FmtBase() {
        super();
    }
    
    /**
     * Converts a ProfileItem object to a URI string.
     *
     * @param config the ProfileItem object to convert
     * @param userInfo the user information to include in the URI
     * @param dicQuery the query parameters to include in the URI
     * @return the converted URI string
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String toUri(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.dto.ProfileItem config, @org.jetbrains.annotations.Nullable()
    java.lang.String userInfo, @org.jetbrains.annotations.Nullable()
    java.util.HashMap<java.lang.String, java.lang.String> dicQuery) {
        return null;
    }
    
    /**
     * Extracts query parameters from a URI.
     *
     * @param uri the URI to extract query parameters from
     * @return a map of query parameters
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, java.lang.String> getQueryParam(@org.jetbrains.annotations.NotNull()
    java.net.URI uri) {
        return null;
    }
    
    /**
     * Populates a ProfileItem object with values from query parameters.
     *
     * @param config the ProfileItem object to populate
     * @param queryParam the query parameters to use for populating the ProfileItem
     * @param allowInsecure whether to allow insecure connections
     */
    public final void getItemFormQuery(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.dto.ProfileItem config, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.String> queryParam, boolean allowInsecure) {
    }
    
    /**
     * Creates a map of query parameters from a ProfileItem object.
     *
     * @param config the ProfileItem object to create query parameters from
     * @return a map of query parameters
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.HashMap<java.lang.String, java.lang.String> getQueryDic(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.dto.ProfileItem config) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getServerAddress(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.dto.ProfileItem profileItem) {
        return null;
    }
}