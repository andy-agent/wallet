package com.v2ray.ang.payment.data.model;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u001c\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BG\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\u0006\u0010\n\u001a\u00020\u0003\u00a2\u0006\u0004\b\u000b\u0010\fJ\t\u0010\u0016\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0003H\u00c6\u0003JY\u0010\u001e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u00032\b\b\u0002\u0010\n\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u001f\u001a\u00020 2\b\u0010!\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\"\u001a\u00020#H\u00d6\u0001J\t\u0010$\u001a\u00020\u0003H\u00d6\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000eR\u0016\u0010\u0005\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000eR\u0016\u0010\u0006\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000eR\u0016\u0010\u0007\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u000eR\u0016\u0010\b\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u000eR\u0016\u0010\t\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u000eR\u0016\u0010\n\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u000e\u00a8\u0006%"}, d2 = {"Lcom/v2ray/ang/payment/data/model/PaymentTarget;", "", "orderNo", "", "networkCode", "assetCode", "collectionAddress", "payableAmount", "uniqueAmountDelta", "qrText", "expiresAt", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getOrderNo", "()Ljava/lang/String;", "getNetworkCode", "getAssetCode", "getCollectionAddress", "getPayableAmount", "getUniqueAmountDelta", "getQrText", "getExpiresAt", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "", "other", "hashCode", "", "toString", "app_playstoreDebug"})
public final class PaymentTarget {
    @com.google.gson.annotations.SerializedName(value = "orderNo")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String orderNo = null;
    @com.google.gson.annotations.SerializedName(value = "networkCode")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String networkCode = null;
    @com.google.gson.annotations.SerializedName(value = "assetCode")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String assetCode = null;
    @com.google.gson.annotations.SerializedName(value = "collectionAddress")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String collectionAddress = null;
    @com.google.gson.annotations.SerializedName(value = "payableAmount")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String payableAmount = null;
    @com.google.gson.annotations.SerializedName(value = "uniqueAmountDelta")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String uniqueAmountDelta = null;
    @com.google.gson.annotations.SerializedName(value = "qrText")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String qrText = null;
    @com.google.gson.annotations.SerializedName(value = "expiresAt")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String expiresAt = null;
    
    public PaymentTarget(@org.jetbrains.annotations.NotNull()
    java.lang.String orderNo, @org.jetbrains.annotations.NotNull()
    java.lang.String networkCode, @org.jetbrains.annotations.NotNull()
    java.lang.String assetCode, @org.jetbrains.annotations.NotNull()
    java.lang.String collectionAddress, @org.jetbrains.annotations.NotNull()
    java.lang.String payableAmount, @org.jetbrains.annotations.NotNull()
    java.lang.String uniqueAmountDelta, @org.jetbrains.annotations.NotNull()
    java.lang.String qrText, @org.jetbrains.annotations.NotNull()
    java.lang.String expiresAt) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getOrderNo() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getNetworkCode() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getAssetCode() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCollectionAddress() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getPayableAmount() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getUniqueAmountDelta() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getQrText() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getExpiresAt() {
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
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.v2ray.ang.payment.data.model.PaymentTarget copy(@org.jetbrains.annotations.NotNull()
    java.lang.String orderNo, @org.jetbrains.annotations.NotNull()
    java.lang.String networkCode, @org.jetbrains.annotations.NotNull()
    java.lang.String assetCode, @org.jetbrains.annotations.NotNull()
    java.lang.String collectionAddress, @org.jetbrains.annotations.NotNull()
    java.lang.String payableAmount, @org.jetbrains.annotations.NotNull()
    java.lang.String uniqueAmountDelta, @org.jetbrains.annotations.NotNull()
    java.lang.String qrText, @org.jetbrains.annotations.NotNull()
    java.lang.String expiresAt) {
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