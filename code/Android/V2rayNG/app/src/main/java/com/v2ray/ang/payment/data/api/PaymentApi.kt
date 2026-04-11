package com.v2ray.ang.payment.data.api

import com.google.gson.annotations.SerializedName
import com.v2ray.ang.payment.PaymentConfig
import com.v2ray.ang.payment.data.model.AuthData
import com.v2ray.ang.payment.data.model.CreateOrderRequest
import com.v2ray.ang.payment.data.model.CreateOrderResponse
import com.v2ray.ang.payment.data.model.GetOrderResponse
import com.v2ray.ang.payment.data.model.LoginRequest
import com.v2ray.ang.payment.data.model.LoginResponse
import com.v2ray.ang.payment.data.model.PaymentTarget
import com.v2ray.ang.payment.data.model.Plan
import com.v2ray.ang.payment.data.model.RefreshTokenRequest
import com.v2ray.ang.payment.data.model.RefreshTokenResponse
import com.v2ray.ang.payment.data.model.RegisterRequest
import com.v2ray.ang.payment.data.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PaymentApi {
    @GET("${PaymentConfig.API_VERSION}/plans")
    suspend fun getPlans(
        @Header("Authorization") authorization: String
    ): Response<PlansResponse>

    @POST("${PaymentConfig.API_VERSION}/orders")
    suspend fun createOrder(
        @Header("Authorization") authorization: String,
        @Header("X-Idempotency-Key") idempotencyKey: String,
        @Body request: CreateOrderRequest
    ): Response<CreateOrderResponse>

    @GET("${PaymentConfig.API_VERSION}/orders/{orderNo}")
    suspend fun getOrder(
        @Header("Authorization") authorization: String,
        @Path("orderNo") orderNo: String
    ): Response<GetOrderResponse>

    @GET("${PaymentConfig.API_VERSION}/orders/{orderNo}/payment-target")
    suspend fun getPaymentTarget(
        @Header("Authorization") authorization: String,
        @Path("orderNo") orderNo: String
    ): Response<PaymentTargetResponse>

    @POST("${PaymentConfig.API_VERSION}/orders/{orderNo}/submit-client-tx")
    suspend fun submitClientTx(
        @Header("Authorization") authorization: String,
        @Path("orderNo") orderNo: String,
        @Body request: SubmitClientTxRequest
    ): Response<OperationResponse>

    @POST("${PaymentConfig.API_VERSION}/orders/{orderNo}/refresh-status")
    suspend fun refreshOrderStatus(
        @Header("Authorization") authorization: String,
        @Path("orderNo") orderNo: String,
        @Body request: RefreshOrderStatusRequest = RefreshOrderStatusRequest()
    ): Response<GetOrderResponse>

    @GET("${PaymentConfig.API_VERSION}/vpn/regions")
    suspend fun getVpnRegions(
        @Header("Authorization") authorization: String
    ): Response<VpnRegionsResponse>

    @POST("${PaymentConfig.API_VERSION}/vpn/config/issue")
    suspend fun issueVpnConfig(
        @Header("Authorization") authorization: String,
        @Body request: IssueVpnConfigRequest
    ): Response<IssueVpnConfigResponse>

    @GET("${PaymentConfig.API_VERSION}/vpn/status")
    suspend fun getVpnStatus(
        @Header("Authorization") authorization: String
    ): Response<VpnStatusResponse>

    @GET("${PaymentConfig.API_VERSION}/subscriptions/current")
    suspend fun getSubscription(
        @Header("Authorization") authorization: String
    ): Response<CurrentSubscriptionResponse>

    @GET("${PaymentConfig.API_VERSION}/me")
    suspend fun getMe(
        @Header("Authorization") authorization: String
    ): Response<MeResponse>

    @GET("${PaymentConfig.API_VERSION}/referral/overview")
    suspend fun getReferralOverview(
        @Header("Authorization") authorization: String
    ): Response<ReferralOverviewResponse>

    @POST("${PaymentConfig.API_VERSION}/referral/bind")
    suspend fun bindReferralCode(
        @Header("Authorization") authorization: String,
        @Body request: ReferralBindRequest
    ): Response<OperationResponse>

    @GET("${PaymentConfig.API_VERSION}/commissions/summary")
    suspend fun getCommissionSummary(
        @Header("Authorization") authorization: String
    ): Response<CommissionSummaryResponse>

    @GET("${PaymentConfig.API_VERSION}/commissions/ledger")
    suspend fun getCommissionLedger(
        @Header("Authorization") authorization: String,
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<CommissionLedgerPageResponse>

    @POST("${PaymentConfig.API_VERSION}/withdrawals")
    suspend fun createWithdrawal(
        @Header("Authorization") authorization: String,
        @Header("X-Idempotency-Key") idempotencyKey: String,
        @Body request: CreateWithdrawalRequest
    ): Response<WithdrawalResponse>

    @GET("${PaymentConfig.API_VERSION}/withdrawals")
    suspend fun getWithdrawals(
        @Header("Authorization") authorization: String,
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<WithdrawalPageResponse>

    @GET("${PaymentConfig.API_VERSION}/withdrawals/{requestNo}")
    suspend fun getWithdrawal(
        @Header("Authorization") authorization: String,
        @Path("requestNo") requestNo: String
    ): Response<WithdrawalResponse>

    @POST("${PaymentConfig.API_VERSION}/auth/register/email/request-code")
    suspend fun requestRegisterCode(
        @Body request: RegisterEmailCodeRequest
    ): Response<OperationResponse>

    @POST("${PaymentConfig.API_VERSION}/auth/login/password")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("${PaymentConfig.API_VERSION}/auth/register/email")
    suspend fun register(
        @Header("X-Idempotency-Key") idempotencyKey: String,
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @POST("${PaymentConfig.API_VERSION}/auth/password/forgot/request-code")
    suspend fun requestResetCode(
        @Body request: PasswordResetCodeRequest
    ): Response<OperationResponse>

    @POST("${PaymentConfig.API_VERSION}/auth/password/reset")
    suspend fun resetPassword(
        @Header("X-Idempotency-Key") idempotencyKey: String,
        @Body request: PasswordResetRequest
    ): Response<OperationResponse>

    @GET("${PaymentConfig.API_VERSION}/wallet/chains")
    suspend fun getWalletChains(
        @Header("Authorization") authorization: String
    ): Response<WalletChainsResponse>

    @GET("${PaymentConfig.API_VERSION}/wallet/public-addresses")
    suspend fun getWalletPublicAddresses(
        @Header("Authorization") authorization: String,
        @Query("networkCode") networkCode: String? = null,
        @Query("assetCode") assetCode: String? = null
    ): Response<WalletPublicAddressesResponse>

    @POST("${PaymentConfig.API_VERSION}/auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<RefreshTokenResponse>
}

data class OperationResponse(
    val code: String,
    val message: String,
    val data: EmptyData?
)

class EmptyData

data class RegisterEmailCodeRequest(
    val email: String
)

data class PasswordResetCodeRequest(
    val email: String
)

data class PasswordResetRequest(
    val email: String,
    val code: String,
    @SerializedName("newPassword")
    val newPassword: String
)

data class PlansResponse(
    val code: String,
    val message: String,
    val data: PlansData?
)

data class PlansData(
    val items: List<Plan>
)

data class PaymentTargetResponse(
    val code: String,
    val message: String,
    val data: PaymentTarget?
)

data class SubmitClientTxRequest(
    val txHash: String,
    @SerializedName("networkCode")
    val networkCode: String,
    @SerializedName("signedAt")
    val signedAt: String? = null
)

data class RefreshOrderStatusRequest(
    @SerializedName("clientObservedStatus")
    val clientObservedStatus: String? = null
)

data class IssueVpnConfigRequest(
    @SerializedName("regionCode")
    val regionCode: String,
    @SerializedName("connectionMode")
    val connectionMode: String = "global"
)

data class VpnRegionsResponse(
    val code: String,
    val message: String,
    val data: VpnRegionsData?
)

data class VpnRegionsData(
    val items: List<VpnRegionItem>
)

data class VpnRegionItem(
    @SerializedName("regionId")
    val regionId: String,
    @SerializedName("regionCode")
    val regionCode: String,
    @SerializedName("displayName")
    val displayName: String,
    val tier: String,
    val status: String,
    @SerializedName("isAllowed")
    val isAllowed: Boolean,
    val remark: String? = null,
)

data class IssueVpnConfigResponse(
    val code: String,
    val message: String,
    val data: VpnConfigIssueData?
)

data class VpnConfigIssueData(
    @SerializedName("regionCode")
    val regionCode: String,
    @SerializedName("connectionMode")
    val connectionMode: String,
    @SerializedName("configPayload")
    val configPayload: String,
    @SerializedName("issuedAt")
    val issuedAt: String,
    @SerializedName("expireAt")
    val expireAt: String,
)

data class VpnStatusResponse(
    val code: String,
    val message: String,
    val data: VpnStatusData?
)

data class VpnStatusData(
    @SerializedName("subscriptionStatus")
    val subscriptionStatus: String,
    @SerializedName("currentRegionCode")
    val currentRegionCode: String? = null,
    @SerializedName("connectionMode")
    val connectionMode: String? = null,
    @SerializedName("canIssueConfig")
    val canIssueConfig: Boolean,
    @SerializedName("sessionStatus")
    val sessionStatus: String,
)

data class CurrentSubscriptionResponse(
    val code: String,
    val message: String,
    val data: CurrentSubscriptionData?
)

data class CurrentSubscriptionData(
    @SerializedName("subscriptionId")
    val subscriptionId: String? = null,
    @SerializedName("planCode")
    val planCode: String? = null,
    val status: String,
    @SerializedName("startedAt")
    val startedAt: String? = null,
    @SerializedName("expireAt")
    val expireAt: String? = null,
    @SerializedName("daysRemaining")
    val daysRemaining: Int? = null,
    @SerializedName("isUnlimitedTraffic")
    val isUnlimitedTraffic: Boolean,
    @SerializedName("maxActiveSessions")
    val maxActiveSessions: Int
)

data class MeResponse(
    val code: String,
    val message: String,
    val data: MeData?
)

data class MeData(
    @SerializedName("accountId")
    val accountId: String,
    val email: String,
    val status: String,
    @SerializedName("referralCode")
    val referralCode: String,
    val subscription: CurrentSubscriptionData? = null
)

data class ReferralOverviewResponse(
    val code: String,
    val message: String,
    val data: ReferralOverviewData?
)

data class ReferralOverviewData(
    @SerializedName("accountId")
    val accountId: String,
    @SerializedName("referralCode")
    val referralCode: String,
    @SerializedName("hasBinding")
    val hasBinding: Boolean,
    @SerializedName("level1InviteCount")
    val level1InviteCount: Int,
    @SerializedName("level2InviteCount")
    val level2InviteCount: Int,
    @SerializedName("level1IncomeUsdt")
    val level1IncomeUsdt: String,
    @SerializedName("level2IncomeUsdt")
    val level2IncomeUsdt: String,
    @SerializedName("availableAmountUsdt")
    val availableAmountUsdt: String,
    @SerializedName("frozenAmountUsdt")
    val frozenAmountUsdt: String,
    @SerializedName("minWithdrawAmountUsdt")
    val minWithdrawAmountUsdt: String
)

data class ReferralBindRequest(
    @SerializedName("referralCode")
    val referralCode: String
)

data class WalletChainsResponse(
    val code: String,
    val message: String,
    val data: WalletChainsData?
)

data class WalletChainsData(
    val items: List<WalletChainItem>
)

data class WalletChainItem(
    @SerializedName("networkCode")
    val networkCode: String,
    @SerializedName("displayName")
    val displayName: String,
    @SerializedName("nativeAssetCode")
    val nativeAssetCode: String,
    @SerializedName("directBroadcastEnabled")
    val directBroadcastEnabled: Boolean,
    @SerializedName("proxyBroadcastEnabled")
    val proxyBroadcastEnabled: Boolean,
    @SerializedName("requiredConfirmations")
    val requiredConfirmations: Int,
    @SerializedName("publicRpcUrl")
    val publicRpcUrl: String,
)

data class WalletPublicAddressesResponse(
    val code: String,
    val message: String,
    val data: WalletPublicAddressesData?
)

data class WalletPublicAddressesData(
    val items: List<WalletPublicAddressItem>
)

data class WalletPublicAddressItem(
    @SerializedName("addressId")
    val addressId: String,
    @SerializedName("networkCode")
    val networkCode: String,
    @SerializedName("assetCode")
    val assetCode: String,
    val address: String,
    @SerializedName("isDefault")
    val isDefault: Boolean,
    @SerializedName("createdAt")
    val createdAt: String,
)

data class CreateWithdrawalRequest(
    val amount: String,
    @SerializedName("payoutAddress")
    val payoutAddress: String,
    @SerializedName("assetCode")
    val assetCode: String,
    @SerializedName("networkCode")
    val networkCode: String
)

data class CommissionSummaryResponse(
    val code: String,
    val message: String,
    val data: CommissionSummaryData?
)

data class CommissionSummaryData(
    @SerializedName("settlementAssetCode")
    val settlementAssetCode: String,
    @SerializedName("settlementNetworkCode")
    val settlementNetworkCode: String,
    @SerializedName("availableAmount")
    val availableAmount: String,
    @SerializedName("frozenAmount")
    val frozenAmount: String,
    @SerializedName("withdrawingAmount")
    val withdrawingAmount: String,
    @SerializedName("withdrawnTotal")
    val withdrawnTotal: String
)

data class CommissionLedgerPageResponse(
    val code: String,
    val message: String,
    val data: CommissionLedgerPageData?
)

data class CommissionLedgerPageData(
    val items: List<CommissionLedgerItem>,
    val page: PageMeta
)

data class CommissionLedgerItem(
    @SerializedName("entryNo")
    val entryNo: String,
    @SerializedName("sourceOrderNo")
    val sourceOrderNo: String,
    @SerializedName("sourceAccountMasked")
    val sourceAccountMasked: String,
    @SerializedName("commissionLevel")
    val commissionLevel: String,
    @SerializedName("sourceAssetCode")
    val sourceAssetCode: String,
    @SerializedName("sourceAmount")
    val sourceAmount: String,
    @SerializedName("fxRateSnapshot")
    val fxRateSnapshot: String,
    @SerializedName("settlementAmountUsdt")
    val settlementAmountUsdt: String,
    val status: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("availableAt")
    val availableAt: String? = null
)

data class PageMeta(
    val page: Int,
    val pageSize: Int,
    val total: Int
)

data class WithdrawalResponse(
    val code: String,
    val message: String,
    val data: WithdrawalItem?
)

data class WithdrawalPageResponse(
    val code: String,
    val message: String,
    val data: WithdrawalPageData?
)

data class WithdrawalPageData(
    val items: List<WithdrawalItem>,
    val page: PageMeta
)

data class WithdrawalItem(
    @SerializedName("requestNo")
    val requestNo: String,
    val amount: String,
    @SerializedName("assetCode")
    val assetCode: String,
    @SerializedName("networkCode")
    val networkCode: String,
    @SerializedName("payoutAddress")
    val payoutAddress: String,
    val status: String,
    @SerializedName("failReason")
    val failReason: String? = null,
    @SerializedName("txHash")
    val txHash: String? = null,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("reviewedAt")
    val reviewedAt: String? = null,
    @SerializedName("completedAt")
    val completedAt: String? = null
)

data class UserInfo(
    val username: String,
    val status: String,
    val expiredAt: String,
    val trafficTotal: Long,
    val trafficUsed: Long,
    val trafficRemaining: Long
)
