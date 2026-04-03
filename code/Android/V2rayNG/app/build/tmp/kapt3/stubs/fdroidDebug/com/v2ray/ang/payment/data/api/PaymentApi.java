package com.v2ray.ang.payment.data.api;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u00ae\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u001e\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0007J2\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\n\u001a\u00020\u00062\b\b\u0001\u0010\u000b\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\rJ(\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0010\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0011J(\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0010\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0011J\u001e\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00150\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0007J\u001e\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00170\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0007J\u001e\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00190\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0007J(\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u001b0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u000b\u001a\u00020\u001cH\u00a7@\u00a2\u0006\u0002\u0010\u001dJ\u001e\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u001f0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0007J>\u0010 \u001a\b\u0012\u0004\u0012\u00020!0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\n\b\u0003\u0010\"\u001a\u0004\u0018\u00010\u00062\b\b\u0003\u0010#\u001a\u00020$2\b\b\u0003\u0010%\u001a\u00020$H\u00a7@\u00a2\u0006\u0002\u0010&J2\u0010\'\u001a\b\u0012\u0004\u0012\u00020(0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\n\u001a\u00020\u00062\b\b\u0001\u0010\u000b\u001a\u00020)H\u00a7@\u00a2\u0006\u0002\u0010*J>\u0010+\u001a\b\u0012\u0004\u0012\u00020,0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\n\b\u0003\u0010\"\u001a\u0004\u0018\u00010\u00062\b\b\u0003\u0010#\u001a\u00020$2\b\b\u0003\u0010%\u001a\u00020$H\u00a7@\u00a2\u0006\u0002\u0010&J(\u0010-\u001a\b\u0012\u0004\u0012\u00020(0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010.\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0011J\u001e\u0010/\u001a\b\u0012\u0004\u0012\u00020\u001b0\u00032\b\b\u0001\u0010\u000b\u001a\u000200H\u00a7@\u00a2\u0006\u0002\u00101J\u001e\u00102\u001a\b\u0012\u0004\u0012\u0002030\u00032\b\b\u0001\u0010\u000b\u001a\u000204H\u00a7@\u00a2\u0006\u0002\u00105J(\u00106\u001a\b\u0012\u0004\u0012\u0002070\u00032\b\b\u0001\u0010\n\u001a\u00020\u00062\b\b\u0001\u0010\u000b\u001a\u000208H\u00a7@\u00a2\u0006\u0002\u00109J\u001e\u0010:\u001a\b\u0012\u0004\u0012\u00020;0\u00032\b\b\u0001\u0010\u000b\u001a\u00020<H\u00a7@\u00a2\u0006\u0002\u0010=\u00a8\u0006>\u00c0\u0006\u0003"}, d2 = {"Lcom/v2ray/ang/payment/data/api/PaymentApi;", "", "getPlans", "Lretrofit2/Response;", "Lcom/v2ray/ang/payment/data/api/PlansResponse;", "authorization", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "createOrder", "Lcom/v2ray/ang/payment/data/model/CreateOrderResponse;", "idempotencyKey", "request", "Lcom/v2ray/ang/payment/data/model/CreateOrderRequest;", "(Ljava/lang/String;Ljava/lang/String;Lcom/v2ray/ang/payment/data/model/CreateOrderRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getOrder", "Lcom/v2ray/ang/payment/data/model/GetOrderResponse;", "orderNo", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getPaymentTarget", "Lcom/v2ray/ang/payment/data/api/PaymentTargetResponse;", "getSubscription", "Lcom/v2ray/ang/payment/data/api/CurrentSubscriptionResponse;", "getMe", "Lcom/v2ray/ang/payment/data/api/MeResponse;", "getReferralOverview", "Lcom/v2ray/ang/payment/data/api/ReferralOverviewResponse;", "bindReferralCode", "Lcom/v2ray/ang/payment/data/api/OperationResponse;", "Lcom/v2ray/ang/payment/data/api/ReferralBindRequest;", "(Ljava/lang/String;Lcom/v2ray/ang/payment/data/api/ReferralBindRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCommissionSummary", "Lcom/v2ray/ang/payment/data/api/CommissionSummaryResponse;", "getCommissionLedger", "Lcom/v2ray/ang/payment/data/api/CommissionLedgerPageResponse;", "status", "page", "", "pageSize", "(Ljava/lang/String;Ljava/lang/String;IILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "createWithdrawal", "Lcom/v2ray/ang/payment/data/api/WithdrawalResponse;", "Lcom/v2ray/ang/payment/data/api/CreateWithdrawalRequest;", "(Ljava/lang/String;Ljava/lang/String;Lcom/v2ray/ang/payment/data/api/CreateWithdrawalRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getWithdrawals", "Lcom/v2ray/ang/payment/data/api/WithdrawalPageResponse;", "getWithdrawal", "requestNo", "requestRegisterCode", "Lcom/v2ray/ang/payment/data/api/RegisterEmailCodeRequest;", "(Lcom/v2ray/ang/payment/data/api/RegisterEmailCodeRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "login", "Lcom/v2ray/ang/payment/data/model/LoginResponse;", "Lcom/v2ray/ang/payment/data/model/LoginRequest;", "(Lcom/v2ray/ang/payment/data/model/LoginRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "register", "Lcom/v2ray/ang/payment/data/model/RegisterResponse;", "Lcom/v2ray/ang/payment/data/model/RegisterRequest;", "(Ljava/lang/String;Lcom/v2ray/ang/payment/data/model/RegisterRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "refreshToken", "Lcom/v2ray/ang/payment/data/model/RefreshTokenResponse;", "Lcom/v2ray/ang/payment/data/model/RefreshTokenRequest;", "(Lcom/v2ray/ang/payment/data/model/RefreshTokenRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_fdroidDebug"})
public abstract interface PaymentApi {
    
    @retrofit2.http.GET(value = "api/client/v1/plans")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getPlans(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String authorization, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.v2ray.ang.payment.data.api.PlansResponse>> $completion);
    
    @retrofit2.http.POST(value = "api/client/v1/orders")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object createOrder(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String authorization, @retrofit2.http.Header(value = "X-Idempotency-Key")
    @org.jetbrains.annotations.NotNull()
    java.lang.String idempotencyKey, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.model.CreateOrderRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.v2ray.ang.payment.data.model.CreateOrderResponse>> $completion);
    
    @retrofit2.http.GET(value = "api/client/v1/orders/{orderNo}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getOrder(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String authorization, @retrofit2.http.Path(value = "orderNo")
    @org.jetbrains.annotations.NotNull()
    java.lang.String orderNo, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.v2ray.ang.payment.data.model.GetOrderResponse>> $completion);
    
    @retrofit2.http.GET(value = "api/client/v1/orders/{orderNo}/payment-target")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getPaymentTarget(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String authorization, @retrofit2.http.Path(value = "orderNo")
    @org.jetbrains.annotations.NotNull()
    java.lang.String orderNo, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.v2ray.ang.payment.data.api.PaymentTargetResponse>> $completion);
    
    @retrofit2.http.GET(value = "api/client/v1/subscriptions/current")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getSubscription(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String authorization, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.v2ray.ang.payment.data.api.CurrentSubscriptionResponse>> $completion);
    
    @retrofit2.http.GET(value = "api/client/v1/me")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getMe(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String authorization, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.v2ray.ang.payment.data.api.MeResponse>> $completion);
    
    @retrofit2.http.GET(value = "api/client/v1/referral/overview")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getReferralOverview(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String authorization, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.v2ray.ang.payment.data.api.ReferralOverviewResponse>> $completion);
    
    @retrofit2.http.POST(value = "api/client/v1/referral/bind")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object bindReferralCode(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String authorization, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.api.ReferralBindRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.v2ray.ang.payment.data.api.OperationResponse>> $completion);
    
    @retrofit2.http.GET(value = "api/client/v1/commissions/summary")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getCommissionSummary(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String authorization, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.v2ray.ang.payment.data.api.CommissionSummaryResponse>> $completion);
    
    @retrofit2.http.GET(value = "api/client/v1/commissions/ledger")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getCommissionLedger(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String authorization, @retrofit2.http.Query(value = "status")
    @org.jetbrains.annotations.Nullable()
    java.lang.String status, @retrofit2.http.Query(value = "page")
    int page, @retrofit2.http.Query(value = "pageSize")
    int pageSize, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.v2ray.ang.payment.data.api.CommissionLedgerPageResponse>> $completion);
    
    @retrofit2.http.POST(value = "api/client/v1/withdrawals")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object createWithdrawal(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String authorization, @retrofit2.http.Header(value = "X-Idempotency-Key")
    @org.jetbrains.annotations.NotNull()
    java.lang.String idempotencyKey, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.api.CreateWithdrawalRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.v2ray.ang.payment.data.api.WithdrawalResponse>> $completion);
    
    @retrofit2.http.GET(value = "api/client/v1/withdrawals")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getWithdrawals(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String authorization, @retrofit2.http.Query(value = "status")
    @org.jetbrains.annotations.Nullable()
    java.lang.String status, @retrofit2.http.Query(value = "page")
    int page, @retrofit2.http.Query(value = "pageSize")
    int pageSize, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.v2ray.ang.payment.data.api.WithdrawalPageResponse>> $completion);
    
    @retrofit2.http.GET(value = "api/client/v1/withdrawals/{requestNo}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getWithdrawal(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String authorization, @retrofit2.http.Path(value = "requestNo")
    @org.jetbrains.annotations.NotNull()
    java.lang.String requestNo, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.v2ray.ang.payment.data.api.WithdrawalResponse>> $completion);
    
    @retrofit2.http.POST(value = "api/client/v1/auth/register/email/request-code")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object requestRegisterCode(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.api.RegisterEmailCodeRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.v2ray.ang.payment.data.api.OperationResponse>> $completion);
    
    @retrofit2.http.POST(value = "api/client/v1/auth/login/password")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object login(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.model.LoginRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.v2ray.ang.payment.data.model.LoginResponse>> $completion);
    
    @retrofit2.http.POST(value = "api/client/v1/auth/register/email")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object register(@retrofit2.http.Header(value = "X-Idempotency-Key")
    @org.jetbrains.annotations.NotNull()
    java.lang.String idempotencyKey, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.model.RegisterRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.v2ray.ang.payment.data.model.RegisterResponse>> $completion);
    
    @retrofit2.http.POST(value = "api/client/v1/auth/refresh")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object refreshToken(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.v2ray.ang.payment.data.model.RefreshTokenRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.v2ray.ang.payment.data.model.RefreshTokenResponse>> $completion);
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}