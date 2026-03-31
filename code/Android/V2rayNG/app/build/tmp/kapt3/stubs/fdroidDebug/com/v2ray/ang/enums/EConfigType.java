package com.v2ray.ang.enums;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0013\b\u0086\u0081\u0002\u0018\u0000 \u00172\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\u0017B\u0019\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011j\u0002\b\u0012j\u0002\b\u0013j\u0002\b\u0014j\u0002\b\u0015j\u0002\b\u0016\u00a8\u0006\u0018"}, d2 = {"Lcom/v2ray/ang/enums/EConfigType;", "", "value", "", "protocolScheme", "", "<init>", "(Ljava/lang/String;IILjava/lang/String;)V", "getValue", "()I", "getProtocolScheme", "()Ljava/lang/String;", "VMESS", "CUSTOM", "SHADOWSOCKS", "SOCKS", "VLESS", "TROJAN", "WIREGUARD", "HYSTERIA2", "HYSTERIA", "HTTP", "POLICYGROUP", "Companion", "app_fdroidDebug"})
public enum EConfigType {
    /*public static final*/ VMESS /* = new VMESS(0, null) */,
    /*public static final*/ CUSTOM /* = new CUSTOM(0, null) */,
    /*public static final*/ SHADOWSOCKS /* = new SHADOWSOCKS(0, null) */,
    /*public static final*/ SOCKS /* = new SOCKS(0, null) */,
    /*public static final*/ VLESS /* = new VLESS(0, null) */,
    /*public static final*/ TROJAN /* = new TROJAN(0, null) */,
    /*public static final*/ WIREGUARD /* = new WIREGUARD(0, null) */,
    /*public static final*/ HYSTERIA2 /* = new HYSTERIA2(0, null) */,
    /*public static final*/ HYSTERIA /* = new HYSTERIA(0, null) */,
    /*public static final*/ HTTP /* = new HTTP(0, null) */,
    /*public static final*/ POLICYGROUP /* = new POLICYGROUP(0, null) */;
    private final int value = 0;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String protocolScheme = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.enums.EConfigType.Companion Companion = null;
    
    EConfigType(int value, java.lang.String protocolScheme) {
    }
    
    public final int getValue() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getProtocolScheme() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.v2ray.ang.enums.EConfigType> getEntries() {
        return null;
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u0007\u00a8\u0006\b"}, d2 = {"Lcom/v2ray/ang/enums/EConfigType$Companion;", "", "<init>", "()V", "fromInt", "Lcom/v2ray/ang/enums/EConfigType;", "value", "", "app_fdroidDebug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.Nullable()
        public final com.v2ray.ang.enums.EConfigType fromInt(int value) {
            return null;
        }
    }
}