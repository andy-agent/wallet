package com.v2ray.ang.payment.data.model;

/**
 * 订单数据模型
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u001a\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BS\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\u0003\u0012\u0006\u0010\f\u001a\u00020\u0003\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u000e\u00a2\u0006\u0004\b\u000f\u0010\u0010J\t\u0010\u001e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010 \u001a\u00020\u0003H\u00c6\u0003J\t\u0010!\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\"\u001a\u00020\bH\u00c6\u0003J\t\u0010#\u001a\u00020\nH\u00c6\u0003J\t\u0010$\u001a\u00020\u0003H\u00c6\u0003J\t\u0010%\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010&\u001a\u0004\u0018\u00010\u000eH\u00c6\u0003Je\u0010\'\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\u00032\b\b\u0002\u0010\f\u001a\u00020\u00032\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u000eH\u00c6\u0001J\u0013\u0010(\u001a\u00020)2\b\u0010*\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010+\u001a\u00020,H\u00d6\u0001J\t\u0010-\u001a\u00020\u0003H\u00d6\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0012R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0012R\u0016\u0010\u0006\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0012R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0016\u0010\u000b\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0012R\u0016\u0010\f\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0012R\u0013\u0010\r\u001a\u0004\u0018\u00010\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001d\u00a8\u0006."}, d2 = {"Lcom/v2ray/ang/payment/data/model/Order;", "", "orderId", "", "orderNo", "status", "statusText", "plan", "Lcom/v2ray/ang/payment/data/model/PlanInfo;", "payment", "Lcom/v2ray/ang/payment/data/model/PaymentInfo;", "expiresAt", "createdAt", "fulfillment", "Lcom/v2ray/ang/payment/data/model/FulfillmentInfo;", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/v2ray/ang/payment/data/model/PlanInfo;Lcom/v2ray/ang/payment/data/model/PaymentInfo;Ljava/lang/String;Ljava/lang/String;Lcom/v2ray/ang/payment/data/model/FulfillmentInfo;)V", "getOrderId", "()Ljava/lang/String;", "getOrderNo", "getStatus", "getStatusText", "getPlan", "()Lcom/v2ray/ang/payment/data/model/PlanInfo;", "getPayment", "()Lcom/v2ray/ang/payment/data/model/PaymentInfo;", "getExpiresAt", "getCreatedAt", "getFulfillment", "()Lcom/v2ray/ang/payment/data/model/FulfillmentInfo;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "", "other", "hashCode", "", "toString", "app_fdroidDebug"})
public final class Order {
    @com.google.gson.annotations.SerializedName(value = "order_id")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String orderId = null;
    @com.google.gson.annotations.SerializedName(value = "order_no")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String orderNo = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String status = null;
    @com.google.gson.annotations.SerializedName(value = "status_text")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String statusText = null;
    @org.jetbrains.annotations.NotNull()
    private final com.v2ray.ang.payment.data.model.PlanInfo plan = null;
    @org.jetbrains.annotations.NotNull()
    private final com.v2ray.ang.payment.data.model.PaymentInfo payment = null;
    @com.google.gson.annotations.SerializedName(value = "expires_at")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String expiresAt = null;
    @com.google.gson.annotations.SerializedName(value = "created_at")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String createdAt = null;
    @org.jetbrains.annotations.Nullable()
    private final com.v2ray.ang.payment.data.model.FulfillmentInfo fulfillment = null;
    
    public Order(@org.jetbrains.annotations.NotNull()
    java.lang.String orderId, @org.jetbrains.annotations.NotNull()
    java.lang.String orderNo, @org.jetbrains.annotations.NotNull()
    java.lang.String status, @org.jetbrains.annotations.NotNull()
    java.lang.String statusText, @org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.model.PlanInfo plan, @org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.model.PaymentInfo payment, @org.jetbrains.annotations.NotNull()
    java.lang.String expiresAt, @org.jetbrains.annotations.NotNull()
    java.lang.String createdAt, @org.jetbrains.annotations.Nullable()
    com.v2ray.ang.payment.data.model.FulfillmentInfo fulfillment) {
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
    public final java.lang.String getStatus() {
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
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getExpiresAt() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCreatedAt() {
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
    public final com.v2ray.ang.payment.data.model.PlanInfo component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.v2ray.ang.payment.data.model.PaymentInfo component6() {
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
    
    @org.jetbrains.annotations.Nullable()
    public final com.v2ray.ang.payment.data.model.FulfillmentInfo component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.v2ray.ang.payment.data.model.Order copy(@org.jetbrains.annotations.NotNull()
    java.lang.String orderId, @org.jetbrains.annotations.NotNull()
    java.lang.String orderNo, @org.jetbrains.annotations.NotNull()
    java.lang.String status, @org.jetbrains.annotations.NotNull()
    java.lang.String statusText, @org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.model.PlanInfo plan, @org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.model.PaymentInfo payment, @org.jetbrains.annotations.NotNull()
    java.lang.String expiresAt, @org.jetbrains.annotations.NotNull()
    java.lang.String createdAt, @org.jetbrains.annotations.Nullable()
    com.v2ray.ang.payment.data.model.FulfillmentInfo fulfillment) {
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