package com.v2ray.ang.payment.data.api;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0016\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B7\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0003\u00a2\u0006\u0004\b\t\u0010\nJ\t\u0010\u0012\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003JE\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001c\u001a\u00020\u001dH\u00d6\u0001J\t\u0010\u001e\u001a\u00020\u0003H\u00d6\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\fR\u0016\u0010\u0005\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\fR\u0016\u0010\u0006\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\fR\u0016\u0010\u0007\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\fR\u0016\u0010\b\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\f\u00a8\u0006\u001f"}, d2 = {"Lcom/v2ray/ang/payment/data/api/CommissionSummaryData;", "", "settlementAssetCode", "", "settlementNetworkCode", "availableAmount", "frozenAmount", "withdrawingAmount", "withdrawnTotal", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getSettlementAssetCode", "()Ljava/lang/String;", "getSettlementNetworkCode", "getAvailableAmount", "getFrozenAmount", "getWithdrawingAmount", "getWithdrawnTotal", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "", "other", "hashCode", "", "toString", "app_playstoreDebug"})
public final class CommissionSummaryData {
    @com.google.gson.annotations.SerializedName(value = "settlementAssetCode")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String settlementAssetCode = null;
    @com.google.gson.annotations.SerializedName(value = "settlementNetworkCode")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String settlementNetworkCode = null;
    @com.google.gson.annotations.SerializedName(value = "availableAmount")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String availableAmount = null;
    @com.google.gson.annotations.SerializedName(value = "frozenAmount")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String frozenAmount = null;
    @com.google.gson.annotations.SerializedName(value = "withdrawingAmount")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String withdrawingAmount = null;
    @com.google.gson.annotations.SerializedName(value = "withdrawnTotal")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String withdrawnTotal = null;
    
    public CommissionSummaryData(@org.jetbrains.annotations.NotNull()
    java.lang.String settlementAssetCode, @org.jetbrains.annotations.NotNull()
    java.lang.String settlementNetworkCode, @org.jetbrains.annotations.NotNull()
    java.lang.String availableAmount, @org.jetbrains.annotations.NotNull()
    java.lang.String frozenAmount, @org.jetbrains.annotations.NotNull()
    java.lang.String withdrawingAmount, @org.jetbrains.annotations.NotNull()
    java.lang.String withdrawnTotal) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSettlementAssetCode() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSettlementNetworkCode() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getAvailableAmount() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getFrozenAmount() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getWithdrawingAmount() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getWithdrawnTotal() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.v2ray.ang.payment.data.api.CommissionSummaryData copy(@org.jetbrains.annotations.NotNull()
    java.lang.String settlementAssetCode, @org.jetbrains.annotations.NotNull()
    java.lang.String settlementNetworkCode, @org.jetbrains.annotations.NotNull()
    java.lang.String availableAmount, @org.jetbrains.annotations.NotNull()
    java.lang.String frozenAmount, @org.jetbrains.annotations.NotNull()
    java.lang.String withdrawingAmount, @org.jetbrains.annotations.NotNull()
    java.lang.String withdrawnTotal) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}