package com.v2ray.ang.fmt;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u000e\u0010\b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\u0005J\u0010\u0010\n\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u0010\u0010\u000b\u001a\u0004\u0018\u00010\f2\u0006\u0010\r\u001a\u00020\u0005\u00a8\u0006\u000e"}, d2 = {"Lcom/v2ray/ang/fmt/VmessFmt;", "Lcom/v2ray/ang/fmt/FmtBase;", "<init>", "()V", "parse", "Lcom/v2ray/ang/dto/ProfileItem;", "str", "", "toUri", "config", "parseVmessStd", "toOutbound", "Lcom/v2ray/ang/dto/V2rayConfig$OutboundBean;", "profileItem", "app_fdroidDebug"})
public final class VmessFmt extends com.v2ray.ang.fmt.FmtBase {
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.fmt.VmessFmt INSTANCE = null;
    
    private VmessFmt() {
        super();
    }
    
    /**
     * Parses a Vmess string into a ProfileItem object.
     *
     * @param str the Vmess string to parse
     * @return the parsed ProfileItem object, or null if parsing fails
     */
    @org.jetbrains.annotations.Nullable()
    public final com.v2ray.ang.dto.ProfileItem parse(@org.jetbrains.annotations.NotNull()
    java.lang.String str) {
        return null;
    }
    
    /**
     * Converts a ProfileItem object to a URI string.
     *
     * @param config the ProfileItem object to convert
     * @return the converted URI string
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String toUri(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.dto.ProfileItem config) {
        return null;
    }
    
    /**
     * Parses a standard Vmess URI string into a ProfileItem object.
     *
     * @param str the standard Vmess URI string to parse
     * @return the parsed ProfileItem object, or null if parsing fails
     */
    @org.jetbrains.annotations.Nullable()
    public final com.v2ray.ang.dto.ProfileItem parseVmessStd(@org.jetbrains.annotations.NotNull()
    java.lang.String str) {
        return null;
    }
    
    /**
     * Converts a ProfileItem object to an OutboundBean object.
     *
     * @param profileItem the ProfileItem object to convert
     * @return the converted OutboundBean object, or null if conversion fails
     */
    @org.jetbrains.annotations.Nullable()
    public final com.v2ray.ang.dto.V2rayConfig.OutboundBean toOutbound(@org.jetbrains.annotations.NotNull()
    com.v2ray.ang.dto.ProfileItem profileItem) {
        return null;
    }
}