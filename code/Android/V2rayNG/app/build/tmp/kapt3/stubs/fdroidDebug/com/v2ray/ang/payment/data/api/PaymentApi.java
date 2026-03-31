package com.v2ray.ang.payment.data.api;

/**
 * 支付 API 接口
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0005J\u001e\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00070\u00032\b\b\u0001\u0010\b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u001e\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u00032\b\b\u0001\u0010\r\u001a\u00020\u000eH\u00a7@\u00a2\u0006\u0002\u0010\u000fJ\u001e\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\u00032\b\b\u0001\u0010\u0012\u001a\u00020\u000eH\u00a7@\u00a2\u0006\u0002\u0010\u000fJ\u001e\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\u00032\b\b\u0001\u0010\b\u001a\u00020\u0015H\u00a7@\u00a2\u0006\u0002\u0010\u0016J\u001e\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00180\u00032\b\b\u0001\u0010\b\u001a\u00020\u0019H\u00a7@\u00a2\u0006\u0002\u0010\u001aJ\u001e\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001c0\u00032\b\b\u0001\u0010\u001d\u001a\u00020\u000eH\u00a7@\u00a2\u0006\u0002\u0010\u000f\u00a8\u0006\u001e\u00c0\u0006\u0003"}, d2 = {"Lcom/v2ray/ang/payment/data/api/PaymentApi;", "", "getPlans", "Lretrofit2/Response;", "Lcom/v2ray/ang/payment/data/model/PlansResponse;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "createOrder", "Lcom/v2ray/ang/payment/data/model/CreateOrderResponse;", "request", "Lcom/v2ray/ang/payment/data/model/CreateOrderRequest;", "(Lcom/v2ray/ang/payment/data/model/CreateOrderRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getOrder", "Lcom/v2ray/ang/payment/data/model/GetOrderResponse;", "orderId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getSubscription", "Lcom/v2ray/ang/payment/data/api/SubscriptionResponse;", "token", "login", "Lcom/v2ray/ang/payment/data/model/LoginResponse;", "Lcom/v2ray/ang/payment/data/model/LoginRequest;", "(Lcom/v2ray/ang/payment/data/model/LoginRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "register", "Lcom/v2ray/ang/payment/data/model/RegisterResponse;", "Lcom/v2ray/ang/payment/data/model/RegisterRequest;", "(Lcom/v2ray/ang/payment/data/model/RegisterRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "refreshToken", "Lcom/v2ray/ang/payment/data/model/RefreshTokenResponse;", "authorization", "app_fdroidDebug"})
public abstract interface PaymentApi {
    
    /**
     * 获取套餐列表
     */
    @retrofit2.http.GET(value = "/client/v1/plans")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getPlans(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.v2ray.ang.payment.data.model.PlansResponse>> $completion);
    
    /**
     * 创建订单
     */
    @retrofit2.http.POST(value = "/client/v1/orders")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object createOrder(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.model.CreateOrderRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.v2ray.ang.payment.data.model.CreateOrderResponse>> $completion);
    
    /**
     * 查询订单状态
     */
    @retrofit2.http.GET(value = "/client/v1/orders/{orderId}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getOrder(@retrofit2.http.Path(value = "orderId")
    @org.jetbrains.annotations.NotNull()
    java.lang.String orderId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.v2ray.ang.payment.data.model.GetOrderResponse>> $completion);
    
    /**
     * 获取订阅信息
     */
    @retrofit2.http.GET(value = "/client/v1/subscription")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getSubscription(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.v2ray.ang.payment.data.api.SubscriptionResponse>> $completion);
    
    /**
     * 用户登录
     */
    @retrofit2.http.POST(value = "/client/v1/auth/login")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object login(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.model.LoginRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.v2ray.ang.payment.data.model.LoginResponse>> $completion);
    
    /**
     * 用户注册
     */
    @retrofit2.http.POST(value = "/client/v1/auth/register")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object register(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.model.RegisterRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.v2ray.ang.payment.data.model.RegisterResponse>> $completion);
    
    /**
     * 刷新 Token
     * 使用 refresh_token 作为 Bearer Token
     */
    @retrofit2.http.POST(value = "/client/v1/auth/refresh")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object refreshToken(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String authorization, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.v2ray.ang.payment.data.model.RefreshTokenResponse>> $completion);
}