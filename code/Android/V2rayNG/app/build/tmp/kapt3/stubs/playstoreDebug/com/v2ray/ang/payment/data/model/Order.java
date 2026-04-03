package com.v2ray.ang.payment.data.model;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\b\u001a\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0016\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u00b1\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\u0006\u0010\n\u001a\u00020\u0003\u0012\u0006\u0010\u000b\u001a\u00020\u0003\u0012\u0006\u0010\f\u001a\u00020\u0003\u0012\u0006\u0010\r\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\u0012\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u0014\u0012\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0004\b\u0016\u0010\u0017J\t\u0010:\u001a\u00020\u0003H\u00c6\u0003J\t\u0010;\u001a\u00020\u0003H\u00c6\u0003J\t\u0010<\u001a\u00020\u0003H\u00c6\u0003J\t\u0010=\u001a\u00020\u0003H\u00c6\u0003J\t\u0010>\u001a\u00020\u0003H\u00c6\u0003J\t\u0010?\u001a\u00020\u0003H\u00c6\u0003J\t\u0010@\u001a\u00020\u0003H\u00c6\u0003J\t\u0010A\u001a\u00020\u0003H\u00c6\u0003J\t\u0010B\u001a\u00020\u0003H\u00c6\u0003J\t\u0010C\u001a\u00020\u0003H\u00c6\u0003J\t\u0010D\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010E\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010F\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010G\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010H\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010I\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010J\u001a\u0004\u0018\u00010\u0014H\u00c6\u0003J\u000b\u0010K\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u00c9\u0001\u0010L\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u00032\b\b\u0002\u0010\n\u001a\u00020\u00032\b\b\u0002\u0010\u000b\u001a\u00020\u00032\b\b\u0002\u0010\f\u001a\u00020\u00032\b\b\u0002\u0010\r\u001a\u00020\u00032\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0012\u001a\u00020\u00032\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u00142\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\u0003H\u00c6\u0001J\u0013\u0010M\u001a\u00020N2\b\u0010O\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010P\u001a\u00020QH\u00d6\u0001J\t\u0010R\u001a\u00020\u0003H\u00d6\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0019R\u0016\u0010\u0005\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0019R\u0016\u0010\u0006\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0019R\u0016\u0010\u0007\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0019R\u0016\u0010\b\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0019R\u0016\u0010\t\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0019R\u0016\u0010\n\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u0019R\u0016\u0010\u000b\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u0019R\u0011\u0010\f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u0019R\u0016\u0010\r\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u0019R\u0018\u0010\u000e\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u0019R\u0018\u0010\u000f\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010\u0019R\u0018\u0010\u0010\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\u0019R\u0018\u0010\u0011\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010\u0019R\u0016\u0010\u0012\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010\u0019R\u0013\u0010\u0013\u001a\u0004\u0018\u00010\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010*R\u0013\u0010\u0015\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010\u0019R\u0011\u0010,\u001a\u00020\u00038F\u00a2\u0006\u0006\u001a\u0004\b-\u0010\u0019R\u0011\u0010.\u001a\u00020/8F\u00a2\u0006\u0006\u001a\u0004\b0\u00101R\u0011\u00102\u001a\u0002038F\u00a2\u0006\u0006\u001a\u0004\b4\u00105R\u0013\u00106\u001a\u0004\u0018\u0001078F\u00a2\u0006\u0006\u001a\u0004\b8\u00109\u00a8\u0006S"}, d2 = {"Lcom/v2ray/ang/payment/data/model/Order;", "", "orderId", "", "orderNo", "planCode", "planName", "orderType", "quoteAssetCode", "quoteNetworkCode", "quoteUsdAmount", "payableAmount", "status", "expiresAt", "confirmedAt", "completedAt", "failureReason", "submittedClientTxHash", "createdAt", "paymentTarget", "Lcom/v2ray/ang/payment/data/model/PaymentTarget;", "subscriptionUrl", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/v2ray/ang/payment/data/model/PaymentTarget;Ljava/lang/String;)V", "getOrderId", "()Ljava/lang/String;", "getOrderNo", "getPlanCode", "getPlanName", "getOrderType", "getQuoteAssetCode", "getQuoteNetworkCode", "getQuoteUsdAmount", "getPayableAmount", "getStatus", "getExpiresAt", "getConfirmedAt", "getCompletedAt", "getFailureReason", "getSubmittedClientTxHash", "getCreatedAt", "getPaymentTarget", "()Lcom/v2ray/ang/payment/data/model/PaymentTarget;", "getSubscriptionUrl", "statusText", "getStatusText", "plan", "Lcom/v2ray/ang/payment/data/model/PlanInfo;", "getPlan", "()Lcom/v2ray/ang/payment/data/model/PlanInfo;", "payment", "Lcom/v2ray/ang/payment/data/model/PaymentInfo;", "getPayment", "()Lcom/v2ray/ang/payment/data/model/PaymentInfo;", "fulfillment", "Lcom/v2ray/ang/payment/data/model/FulfillmentInfo;", "getFulfillment", "()Lcom/v2ray/ang/payment/data/model/FulfillmentInfo;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "copy", "equals", "", "other", "hashCode", "", "toString", "app_playstoreDebug"})
public final class Order {
    @com.google.gson.annotations.SerializedName(value = "orderId")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String orderId = null;
    @com.google.gson.annotations.SerializedName(value = "orderNo")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String orderNo = null;
    @com.google.gson.annotations.SerializedName(value = "planCode")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String planCode = null;
    @com.google.gson.annotations.SerializedName(value = "planName")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String planName = null;
    @com.google.gson.annotations.SerializedName(value = "orderType")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String orderType = null;
    @com.google.gson.annotations.SerializedName(value = "quoteAssetCode")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String quoteAssetCode = null;
    @com.google.gson.annotations.SerializedName(value = "quoteNetworkCode")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String quoteNetworkCode = null;
    @com.google.gson.annotations.SerializedName(value = "quoteUsdAmount")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String quoteUsdAmount = null;
    @com.google.gson.annotations.SerializedName(value = "payableAmount")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String payableAmount = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String status = null;
    @com.google.gson.annotations.SerializedName(value = "expiresAt")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String expiresAt = null;
    @com.google.gson.annotations.SerializedName(value = "confirmedAt")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String confirmedAt = null;
    @com.google.gson.annotations.SerializedName(value = "completedAt")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String completedAt = null;
    @com.google.gson.annotations.SerializedName(value = "failureReason")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String failureReason = null;
    @com.google.gson.annotations.SerializedName(value = "submittedClientTxHash")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String submittedClientTxHash = null;
    @com.google.gson.annotations.SerializedName(value = "createdAt")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String createdAt = null;
    @org.jetbrains.annotations.Nullable()
    private final com.v2ray.ang.payment.data.model.PaymentTarget paymentTarget = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String subscriptionUrl = null;
    
    public Order(@org.jetbrains.annotations.NotNull()
    java.lang.String orderId, @org.jetbrains.annotations.NotNull()
    java.lang.String orderNo, @org.jetbrains.annotations.NotNull()
    java.lang.String planCode, @org.jetbrains.annotations.NotNull()
    java.lang.String planName, @org.jetbrains.annotations.NotNull()
    java.lang.String orderType, @org.jetbrains.annotations.NotNull()
    java.lang.String quoteAssetCode, @org.jetbrains.annotations.NotNull()
    java.lang.String quoteNetworkCode, @org.jetbrains.annotations.NotNull()
    java.lang.String quoteUsdAmount, @org.jetbrains.annotations.NotNull()
    java.lang.String payableAmount, @org.jetbrains.annotations.NotNull()
    java.lang.String status, @org.jetbrains.annotations.NotNull()
    java.lang.String expiresAt, @org.jetbrains.annotations.Nullable()
    java.lang.String confirmedAt, @org.jetbrains.annotations.Nullable()
    java.lang.String completedAt, @org.jetbrains.annotations.Nullable()
    java.lang.String failureReason, @org.jetbrains.annotations.Nullable()
    java.lang.String submittedClientTxHash, @org.jetbrains.annotations.NotNull()
    java.lang.String createdAt, @org.jetbrains.annotations.Nullable()
    com.v2ray.ang.payment.data.model.PaymentTarget paymentTarget, @org.jetbrains.annotations.Nullable()
    java.lang.String subscriptionUrl) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getOrderId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getOrderNo() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getPlanCode() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getPlanName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getOrderType() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getQuoteAssetCode() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getQuoteNetworkCode() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getQuoteUsdAmount() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getPayableAmount() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getExpiresAt() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getConfirmedAt() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCompletedAt() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getFailureReason() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getSubmittedClientTxHash() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCreatedAt() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.v2ray.ang.payment.data.model.PaymentTarget getPaymentTarget() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getSubscriptionUrl() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getStatusText() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.v2ray.ang.payment.data.model.PlanInfo getPlan() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.v2ray.ang.payment.data.model.PaymentInfo getPayment() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.v2ray.ang.payment.data.model.FulfillmentInfo getFulfillment() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component10() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component11() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component12() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component13() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component14() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component15() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component16() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.v2ray.ang.payment.data.model.PaymentTarget component17() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component18() {
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
    public final java.lang.String component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.v2ray.ang.payment.data.model.Order copy(@org.jetbrains.annotations.NotNull()
    java.lang.String orderId, @org.jetbrains.annotations.NotNull()
    java.lang.String orderNo, @org.jetbrains.annotations.NotNull()
    java.lang.String planCode, @org.jetbrains.annotations.NotNull()
    java.lang.String planName, @org.jetbrains.annotations.NotNull()
    java.lang.String orderType, @org.jetbrains.annotations.NotNull()
    java.lang.String quoteAssetCode, @org.jetbrains.annotations.NotNull()
    java.lang.String quoteNetworkCode, @org.jetbrains.annotations.NotNull()
    java.lang.String quoteUsdAmount, @org.jetbrains.annotations.NotNull()
    java.lang.String payableAmount, @org.jetbrains.annotations.NotNull()
    java.lang.String status, @org.jetbrains.annotations.NotNull()
    java.lang.String expiresAt, @org.jetbrains.annotations.Nullable()
    java.lang.String confirmedAt, @org.jetbrains.annotations.Nullable()
    java.lang.String completedAt, @org.jetbrains.annotations.Nullable()
    java.lang.String failureReason, @org.jetbrains.annotations.Nullable()
    java.lang.String submittedClientTxHash, @org.jetbrains.annotations.NotNull()
    java.lang.String createdAt, @org.jetbrains.annotations.Nullable()
    com.v2ray.ang.payment.data.model.PaymentTarget paymentTarget, @org.jetbrains.annotations.Nullable()
    java.lang.String subscriptionUrl) {
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