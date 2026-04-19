package com.v2ray.ang.payment.data.api

import com.google.gson.annotations.SerializedName
import com.v2ray.ang.payment.PaymentConfig
import com.v2ray.ang.payment.data.model.AuthData
import com.v2ray.ang.payment.data.model.CreateOrderRequest
import com.v2ray.ang.payment.data.model.CreateOrderResponse
import com.v2ray.ang.payment.data.model.GetOrderResponse
import com.v2ray.ang.payment.data.model.LoginRequest
import com.v2ray.ang.payment.data.model.LoginResponse
import com.v2ray.ang.payment.data.model.OrderPageResponse
import com.v2ray.ang.payment.data.model.PaymentTarget
import com.v2ray.ang.payment.data.model.Plan
import com.v2ray.ang.payment.data.model.RefreshTokenRequest
import com.v2ray.ang.payment.data.model.RefreshTokenResponse
import com.v2ray.ang.payment.data.model.RegisterRequest
import com.v2ray.ang.payment.data.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
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

    @GET("${PaymentConfig.API_VERSION}/orders")
    suspend fun listOrders(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 100,
        @Query("orderNo") orderNo: String? = null,
        @Query("status") status: String? = null
    ): Response<OrderPageResponse>

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

    @GET("${PaymentConfig.API_VERSION}/vpn/nodes")
    suspend fun getVpnNodes(
        @Header("Authorization") authorization: String,
        @Query("lineCode") lineCode: String? = null
    ): Response<VpnNodesResponse>

    @POST("${PaymentConfig.API_VERSION}/vpn/config/issue")
    suspend fun issueVpnConfig(
        @Header("Authorization") authorization: String,
        @Body request: IssueVpnConfigRequest
    ): Response<IssueVpnConfigResponse>

    @POST("${PaymentConfig.API_VERSION}/vpn/selection")
    suspend fun selectVpnNode(
        @Header("Authorization") authorization: String,
        @Body request: SelectVpnNodeRequest
    ): Response<VpnStatusResponse>

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

    @GET("${PaymentConfig.API_VERSION}/wallet/chains")
    suspend fun getWalletChains(
        @Header("Authorization") authorization: String
    ): Response<WalletChainsResponse>

    @GET("${PaymentConfig.API_VERSION}/wallet/assets/catalog")
    suspend fun getWalletAssetCatalog(
        @Header("Authorization") authorization: String,
        @Query("networkCode") networkCode: String? = null
    ): Response<WalletAssetCatalogResponse>

    @GET("${PaymentConfig.API_VERSION}/wallet/public-addresses")
    suspend fun getWalletPublicAddresses(
        @Header("Authorization") authorization: String,
        @Query("networkCode") networkCode: String? = null,
        @Query("assetCode") assetCode: String? = null
    ): Response<WalletPublicAddressesResponse>

    @POST("${PaymentConfig.API_VERSION}/wallet/public-addresses")
    suspend fun upsertWalletPublicAddress(
        @Header("Authorization") authorization: String,
        @Body request: WalletPublicAddressUpsertRequest
    ): Response<WalletPublicAddressUpsertResponse>

    @GET("${PaymentConfig.API_VERSION}/wallet/overview")
    suspend fun getWalletOverview(
        @Header("Authorization") authorization: String
    ): Response<WalletOverviewResponse>

    @GET("${PaymentConfig.API_VERSION}/wallet/balances")
    suspend fun getWalletBalances(
        @Header("Authorization") authorization: String
    ): Response<WalletBalancesResponse>

    @GET("${PaymentConfig.API_VERSION}/wallet/lifecycle")
    suspend fun getWalletLifecycle(
        @Header("Authorization") authorization: String
    ): Response<WalletLifecycleResponse>

    @POST("${PaymentConfig.API_VERSION}/wallet/lifecycle")
    suspend fun upsertWalletLifecycle(
        @Header("Authorization") authorization: String,
        @Body request: WalletLifecycleUpsertRequest
    ): Response<WalletLifecycleResponse>

    @GET("${PaymentConfig.API_VERSION}/wallet/receive-context")
    suspend fun getWalletReceiveContext(
        @Header("Authorization") authorization: String,
        @Query("networkCode") networkCode: String? = null,
        @Query("assetCode") assetCode: String? = null
    ): Response<WalletReceiveContextResponse>

    @POST("${PaymentConfig.API_VERSION}/wallet/secret-backups")
    suspend fun upsertWalletSecretBackup(
        @Header("Authorization") authorization: String,
        @Body request: WalletSecretBackupUpsertRequest
    ): Response<WalletSecretBackupResponse>

    @GET("${PaymentConfig.API_VERSION}/wallet/secret-backups")
    suspend fun getWalletSecretBackupMetadata(
        @Header("Authorization") authorization: String
    ): Response<WalletSecretBackupMetadataResponse>

    @GET("${PaymentConfig.API_VERSION}/wallet/secret-backups/export")
    suspend fun getWalletSecretBackupExport(
        @Header("Authorization") authorization: String
    ): Response<WalletSecretBackupExportResponse>

    @GET("${PaymentConfig.API_VERSION}/wallets")
    suspend fun listWallets(
        @Header("Authorization") authorization: String
    ): Response<WalletsResponse>

    @GET("${PaymentConfig.API_VERSION}/wallets/{walletId}")
    suspend fun getWallet(
        @Header("Authorization") authorization: String,
        @Path("walletId") walletId: String
    ): Response<WalletDetailResponse>

    @GET("${PaymentConfig.API_VERSION}/wallets/{walletId}/chain-accounts")
    suspend fun getWalletChainAccounts(
        @Header("Authorization") authorization: String,
        @Path("walletId") walletId: String
    ): Response<WalletChainAccountsResponse>

    @POST("${PaymentConfig.API_VERSION}/wallets/create-mnemonic")
    suspend fun createMnemonicWallet(
        @Header("Authorization") authorization: String,
        @Body request: CreateMnemonicWalletRequest
    ): Response<WalletDetailResponse>

    @POST("${PaymentConfig.API_VERSION}/wallets/import/mnemonic")
    suspend fun importMnemonicWallet(
        @Header("Authorization") authorization: String,
        @Body request: CreateMnemonicWalletRequest
    ): Response<WalletDetailResponse>

    @POST("${PaymentConfig.API_VERSION}/wallets/import/watch-only")
    suspend fun importWatchOnlyWallet(
        @Header("Authorization") authorization: String,
        @Body request: ImportWatchWalletRequest
    ): Response<WalletDetailResponse>

    @PATCH("${PaymentConfig.API_VERSION}/wallets/{walletId}")
    suspend fun updateWallet(
        @Header("Authorization") authorization: String,
        @Path("walletId") walletId: String,
        @Body request: UpdateWalletRequest
    ): Response<WalletDetailResponse>

    @POST("${PaymentConfig.API_VERSION}/wallets/{walletId}/set-default")
    suspend fun setDefaultWallet(
        @Header("Authorization") authorization: String,
        @Path("walletId") walletId: String
    ): Response<WalletDetailResponse>

    @POST("${PaymentConfig.API_VERSION}/wallets/{walletId}/secret-backup")
    suspend fun upsertWalletSecretBackupV2(
        @Header("Authorization") authorization: String,
        @Path("walletId") walletId: String,
        @Body request: WalletSecretBackupUpsertRequest
    ): Response<WalletSecretBackupResponse>

    @GET("${PaymentConfig.API_VERSION}/wallets/{walletId}/secret-backup")
    suspend fun getWalletSecretBackupMetadataV2(
        @Header("Authorization") authorization: String,
        @Path("walletId") walletId: String
    ): Response<WalletSecretBackupMetadataResponse>

    @POST("${PaymentConfig.API_VERSION}/wallet/transfer/build")
    suspend fun buildWalletTransfer(
        @Header("Authorization") authorization: String,
        @Body request: WalletTransferBuildRequest
    ): Response<WalletTransferBuildResponse>

    @POST("${PaymentConfig.API_VERSION}/wallet/transfer/precheck")
    suspend fun precheckWalletTransfer(
        @Header("Authorization") authorization: String,
        @Body request: WalletTransferPrecheckRequest
    ): Response<WalletTransferPrecheckResponse>

    @POST("${PaymentConfig.API_VERSION}/wallet/transfer/proxy-broadcast")
    suspend fun proxyBroadcastWalletTransfer(
        @Header("Authorization") authorization: String,
        @Body request: WalletTransferProxyBroadcastRequest
    ): Response<WalletTransferProxyBroadcastResponse>

    @GET("${PaymentConfig.API_VERSION}/referral/overview")
    suspend fun getReferralOverview(
        @Header("Authorization") authorization: String
    ): Response<ReferralOverviewResponse>

    @GET("${PaymentConfig.API_VERSION}/referral/share-context")
    suspend fun getReferralShareContext(
        @Header("Authorization") authorization: String
    ): Response<ReferralShareContextResponse>

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
    suspend fun requestPasswordResetCode(
        @Body request: PasswordResetCodeRequest
    ): Response<OperationResponse>

    @POST("${PaymentConfig.API_VERSION}/auth/password/reset")
    suspend fun resetPassword(
        @Header("X-Idempotency-Key") idempotencyKey: String,
        @Body request: PasswordResetRequest
    ): Response<OperationResponse>

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
    val password: String
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
    val signedAt: String? = null,
    @SerializedName("payerWalletId")
    val payerWalletId: String? = null,
    @SerializedName("payerChainAccountId")
    val payerChainAccountId: String? = null,
    @SerializedName("submittedFromAddress")
    val submittedFromAddress: String? = null,
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

data class VpnNodesResponse(
    val code: String,
    val message: String,
    val data: VpnNodesData?
)

data class VpnNodesData(
    val items: List<VpnNodeItem>
)

data class VpnNodeItem(
    @SerializedName("nodeId")
    val nodeId: String,
    @SerializedName("nodeName")
    val nodeName: String,
    @SerializedName("lineCode")
    val lineCode: String,
    @SerializedName("lineName")
    val lineName: String,
    @SerializedName("regionCode")
    val regionCode: String,
    @SerializedName("regionName")
    val regionName: String,
    val host: String,
    val port: Int,
    val status: String,
    @SerializedName("healthStatus")
    val healthStatus: String,
    val selected: Boolean = false,
    val source: String? = null,
)

data class SelectVpnNodeRequest(
    @SerializedName("lineCode")
    val lineCode: String,
    @SerializedName("nodeId")
    val nodeId: String,
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
    @SerializedName("selectedRegionCode")
    val selectedRegionCode: String? = null,
    @SerializedName("selectedRegionName")
    val selectedRegionName: String? = null,
    @SerializedName("selectedLineCode")
    val selectedLineCode: String? = null,
    @SerializedName("selectedLineName")
    val selectedLineName: String? = null,
    @SerializedName("selectedNodeId")
    val selectedNodeId: String? = null,
    @SerializedName("selectedNodeName")
    val selectedNodeName: String? = null,
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
    @SerializedName("planName")
    val planName: String? = null,
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
    val maxActiveSessions: Int,
    @SerializedName("subscriptionUrl")
    val subscriptionUrl: String? = null,
    @SerializedName("marzbanUsername")
    val marzbanUsername: String? = null,
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

data class WalletChainsResponse(
    val code: String,
    val message: String,
    val data: WalletChainsData?
)

data class WalletChainsData(
    val items: List<WalletChainItemData>
)

data class WalletChainItemData(
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
    val publicRpcUrl: String? = null,
    @SerializedName("assetCount")
    val assetCount: Int? = null,
    @SerializedName("orderCount")
    val orderCount: Int? = null,
    @SerializedName("publicAddressCount")
    val publicAddressCount: Int? = null,
    @SerializedName("lastOrderAt")
    val lastOrderAt: String? = null,
    @SerializedName("hasConfiguredAddress")
    val hasConfiguredAddress: Boolean? = null,
    @SerializedName("selected")
    val selected: Boolean? = null,
)

data class WalletBalancesResponse(
    val code: String,
    val message: String,
    val data: WalletBalancesData?,
)

data class WalletBalancesData(
    @SerializedName("accountId")
    val accountId: String,
    @SerializedName("accountEmail")
    val accountEmail: String,
    val items: List<WalletBalanceItemData>,
)

data class WalletBalanceItemData(
    @SerializedName("assetId")
    val assetId: String,
    @SerializedName("networkCode")
    val networkCode: String,
    @SerializedName("assetCode")
    val assetCode: String,
    @SerializedName("displayName")
    val displayName: String,
    val symbol: String,
    val decimals: Int,
    val address: String? = null,
    @SerializedName("availableBalanceMinor")
    val availableBalanceMinor: String? = null,
    @SerializedName("availableBalanceUiAmount")
    val availableBalanceUiAmount: String? = null,
    @SerializedName("availableBalanceStatus")
    val availableBalanceStatus: String? = null,
)

data class WalletAssetCatalogResponse(
    val code: String,
    val message: String,
    val data: WalletAssetCatalogData?
)

data class WalletAssetCatalogData(
    val items: List<WalletAssetItemData>
)

data class WalletAssetItemData(
    @SerializedName("assetId")
    val assetId: String,
    @SerializedName("networkCode")
    val networkCode: String,
    @SerializedName("assetCode")
    val assetCode: String,
    @SerializedName("displayName")
    val displayName: String,
    val symbol: String,
    val decimals: Int,
    @SerializedName("isNative")
    val isNative: Boolean,
    @SerializedName("contractAddress")
    val contractAddress: String? = null,
    @SerializedName("walletVisible")
    val walletVisible: Boolean,
    @SerializedName("orderPayable")
    val orderPayable: Boolean,
    @SerializedName("publicAddressCount")
    val publicAddressCount: Int? = null,
    @SerializedName("orderCount")
    val orderCount: Int? = null,
    @SerializedName("totalPayableAmount")
    val totalPayableAmount: String? = null,
    @SerializedName("availableBalanceMinor")
    val availableBalanceMinor: String? = null,
    @SerializedName("availableBalanceUiAmount")
    val availableBalanceUiAmount: String? = null,
    @SerializedName("availableBalanceStatus")
    val availableBalanceStatus: String? = null,
    @SerializedName("balanceAddress")
    val balanceAddress: String? = null,
    @SerializedName("lastOrderAt")
    val lastOrderAt: String? = null,
    @SerializedName("lastOrderStatus")
    val lastOrderStatus: String? = null,
    @SerializedName("selected")
    val selected: Boolean? = null,
)

data class WalletPublicAddressesResponse(
    val code: String,
    val message: String,
    val data: WalletPublicAddressesData?
)

data class WalletPublicAddressesData(
    val items: List<WalletPublicAddressData>
)

data class WalletPublicAddressUpsertRequest(
    @SerializedName("networkCode")
    val networkCode: String,
    @SerializedName("assetCode")
    val assetCode: String,
    val address: String,
    @SerializedName("isDefault")
    val isDefault: Boolean,
)

data class WalletPublicAddressUpsertResponse(
    val code: String,
    val message: String,
    val data: WalletPublicAddressData?,
)

data class WalletPublicAddressData(
    @SerializedName("addressId")
    val addressId: String,
    @SerializedName("accountId")
    val accountId: String,
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

data class WalletOverviewResponse(
    val code: String,
    val message: String,
    val data: WalletOverviewData?
)

data class WalletOverviewData(
    @SerializedName("accountId")
    val accountId: String,
    @SerializedName("accountEmail")
    val accountEmail: String,
    @SerializedName("selectedNetworkCode")
    val selectedNetworkCode: String,
    @SerializedName("chainItems")
    val chainItems: List<WalletChainItemData>,
    @SerializedName("assetItems")
    val assetItems: List<WalletAssetItemData>,
    val alerts: List<String> = emptyList(),
)

data class WalletLifecycleResponse(
    val code: String,
    val message: String,
    val data: WalletLifecycleData?
)

data class WalletLifecycleData(
    @SerializedName("accountId")
    val accountId: String,
    @SerializedName("walletExists")
    val walletExists: Boolean,
    @SerializedName("receiveState")
    val receiveState: String,
    @SerializedName("lifecycleStatus")
    val lifecycleStatus: String,
    @SerializedName("sourceType")
    val sourceType: String? = null,
    @SerializedName("walletId")
    val walletId: String? = null,
    @SerializedName("displayName")
    val displayName: String? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("origin")
    val origin: String? = null,
    @SerializedName("nextAction")
    val nextAction: String? = null,
    @SerializedName("walletName")
    val walletName: String? = null,
    @SerializedName("configuredAddressCount")
    val configuredAddressCount: Int = 0,
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("updatedAt")
    val updatedAt: String? = null,
    @SerializedName("backupAcknowledgedAt")
    val backupAcknowledgedAt: String? = null,
    @SerializedName("activatedAt")
    val activatedAt: String? = null,
)

data class WalletLifecycleUpsertRequest(
    @SerializedName("action")
    val action: String,
    @SerializedName("displayName")
    val displayName: String? = null,
    @SerializedName("mnemonic")
    val mnemonic: String? = null,
    @SerializedName("mnemonicHash")
    val mnemonicHash: String? = null,
    @SerializedName("mnemonicWordCount")
    val mnemonicWordCount: Int? = null,
)

data class WalletSecretBackupPublicAddressRequest(
    @SerializedName("networkCode")
    val networkCode: String,
    @SerializedName("assetCode")
    val assetCode: String,
    val address: String,
    @SerializedName("isDefault")
    val isDefault: Boolean,
)

data class WalletSecretBackupUpsertRequest(
    @SerializedName("walletId")
    val walletId: String? = null,
    @SerializedName("secretType")
    val secretType: String = "MNEMONIC",
    @SerializedName("mnemonic")
    val mnemonic: String,
    @SerializedName("mnemonicHash")
    val mnemonicHash: String,
    @SerializedName("mnemonicWordCount")
    val mnemonicWordCount: Int,
    @SerializedName("walletName")
    val walletName: String? = null,
    @SerializedName("sourceType")
    val sourceType: String? = null,
    @SerializedName("publicAddresses")
    val publicAddresses: List<WalletSecretBackupPublicAddressRequest> = emptyList(),
)

data class WalletSecretBackupResponse(
    val code: String,
    val message: String,
    val data: WalletSecretBackupData?,
)

data class WalletSecretBackupMetadataResponse(
    val code: String,
    val message: String,
    val data: WalletSecretBackupMetadataData?,
)

data class WalletSecretBackupExportResponse(
    val code: String,
    val message: String,
    val data: WalletSecretBackupExportData?,
)

data class WalletSecretBackupData(
    @SerializedName("backupId")
    val backupId: String,
    @SerializedName("accountId")
    val accountId: String,
    @SerializedName("walletId")
    val walletId: String,
    @SerializedName("secretType")
    val secretType: String,
    @SerializedName("encryptionScheme")
    val encryptionScheme: String,
    @SerializedName("recoveryKeyVersion")
    val recoveryKeyVersion: String,
    @SerializedName("recipientFingerprint")
    val recipientFingerprint: String,
    @SerializedName("replicatedToBackupServer")
    val replicatedToBackupServer: Boolean,
    @SerializedName("backupServerReference")
    val backupServerReference: String? = null,
    @SerializedName("lastReplicationError")
    val lastReplicationError: String? = null,
    @SerializedName("updatedAt")
    val updatedAt: String,
)

data class WalletSecretBackupMetadataData(
    @SerializedName("exists")
    val exists: Boolean,
    @SerializedName("backupId")
    val backupId: String? = null,
    @SerializedName("accountId")
    val accountId: String? = null,
    @SerializedName("walletId")
    val walletId: String? = null,
    @SerializedName("secretType")
    val secretType: String? = null,
    @SerializedName("encryptionScheme")
    val encryptionScheme: String? = null,
    @SerializedName("recoveryKeyVersion")
    val recoveryKeyVersion: String? = null,
    @SerializedName("recipientFingerprint")
    val recipientFingerprint: String? = null,
    @SerializedName("replicatedToBackupServer")
    val replicatedToBackupServer: Boolean? = null,
    @SerializedName("backupServerReference")
    val backupServerReference: String? = null,
    @SerializedName("lastReplicationError")
    val lastReplicationError: String? = null,
    @SerializedName("updatedAt")
    val updatedAt: String? = null,
)

data class WalletSecretBackupExportData(
    @SerializedName("exists")
    val exists: Boolean,
    @SerializedName("fileName")
    val fileName: String? = null,
    @SerializedName("payload")
    val payload: WalletSecretBackupExportPayloadData? = null,
)

data class WalletSecretBackupExportPayloadData(
    @SerializedName("version")
    val version: String,
    @SerializedName("backupId")
    val backupId: String,
    @SerializedName("accountId")
    val accountId: String,
    @SerializedName("walletId")
    val walletId: String,
    @SerializedName("secretType")
    val secretType: String,
    @SerializedName("encryptionScheme")
    val encryptionScheme: String,
    @SerializedName("recoveryKeyVersion")
    val recoveryKeyVersion: String,
    @SerializedName("recipientFingerprint")
    val recipientFingerprint: String,
    @SerializedName("ciphertext")
    val ciphertext: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
)

data class WalletsResponse(
    val code: String,
    val message: String,
    val data: WalletsData?
)

data class WalletsData(
    val items: List<WalletSummaryData>
)

data class WalletDetailResponse(
    val code: String,
    val message: String,
    val data: WalletDetailData?
)

data class WalletChainAccountsResponse(
    val code: String,
    val message: String,
    val data: WalletChainAccountsData?
)

data class WalletChainAccountsData(
    val items: List<WalletChainAccountData>
)

data class WalletSummaryData(
    @SerializedName("walletId")
    val walletId: String,
    @SerializedName("walletName")
    val walletName: String,
    @SerializedName("walletKind")
    val walletKind: String,
    @SerializedName("sourceType")
    val sourceType: String,
    @SerializedName("isDefault")
    val isDefault: Boolean,
    @SerializedName("isArchived")
    val isArchived: Boolean,
    @SerializedName("deviceCapabilitySummary")
    val deviceCapabilitySummary: String? = null,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
)

data class WalletKeySlotData(
    @SerializedName("keySlotId")
    val keySlotId: String,
    @SerializedName("walletId")
    val walletId: String,
    @SerializedName("slotCode")
    val slotCode: String,
    @SerializedName("chainFamily")
    val chainFamily: String,
    @SerializedName("derivationType")
    val derivationType: String,
    @SerializedName("derivationPath")
    val derivationPath: String? = null,
)

data class WalletChainAccountData(
    @SerializedName("chainAccountId")
    val chainAccountId: String,
    @SerializedName("walletId")
    val walletId: String,
    @SerializedName("keySlotId")
    val keySlotId: String? = null,
    @SerializedName("chainFamily")
    val chainFamily: String,
    @SerializedName("networkCode")
    val networkCode: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("capability")
    val capability: String,
    @SerializedName("isEnabled")
    val isEnabled: Boolean,
    @SerializedName("isDefaultReceive")
    val isDefaultReceive: Boolean,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
)

data class WalletDetailData(
    @SerializedName("wallet")
    val wallet: WalletSummaryData,
    @SerializedName("keySlots")
    val keySlots: List<WalletKeySlotData>,
    @SerializedName("chainAccounts")
    val chainAccounts: List<WalletChainAccountData>,
    @SerializedName("backup")
    val backup: WalletSecretBackupMetadataData? = null,
)

data class CreateMnemonicWalletKeySlotRequest(
    @SerializedName("slotCode")
    val slotCode: String,
    @SerializedName("chainFamily")
    val chainFamily: String,
    @SerializedName("derivationType")
    val derivationType: String,
    @SerializedName("derivationPath")
    val derivationPath: String? = null,
)

data class CreateMnemonicWalletChainAccountRequest(
    @SerializedName("slotCode")
    val slotCode: String,
    @SerializedName("chainFamily")
    val chainFamily: String,
    @SerializedName("networkCode")
    val networkCode: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("isEnabled")
    val isEnabled: Boolean = true,
    @SerializedName("isDefaultReceive")
    val isDefaultReceive: Boolean = false,
)

data class CreateMnemonicWalletRequest(
    @SerializedName("walletName")
    val walletName: String,
    @SerializedName("keySlots")
    val keySlots: List<CreateMnemonicWalletKeySlotRequest>,
    @SerializedName("chainAccounts")
    val chainAccounts: List<CreateMnemonicWalletChainAccountRequest>,
)

data class ImportWatchWalletRequest(
    @SerializedName("walletName")
    val walletName: String,
    @SerializedName("chainFamily")
    val chainFamily: String,
    @SerializedName("networkCode")
    val networkCode: String,
    @SerializedName("address")
    val address: String,
)

data class UpdateWalletRequest(
    @SerializedName("walletName")
    val walletName: String? = null,
    @SerializedName("isArchived")
    val isArchived: Boolean? = null,
)

data class WalletTransferBuildRequest(
    @SerializedName("networkCode")
    val networkCode: String,
    @SerializedName("assetCode")
    val assetCode: String,
    @SerializedName("fromAddress")
    val fromAddress: String,
    @SerializedName("toAddress")
    val toAddress: String,
    val amount: String,
    @SerializedName("orderNo")
    val orderNo: String? = null,
)

data class WalletTransferBuildResponse(
    val code: String,
    val message: String,
    val data: WalletTransferBuildData?,
)

data class WalletTransferBuildData(
    @SerializedName("networkCode")
    val networkCode: String,
    @SerializedName("assetCode")
    val assetCode: String,
    @SerializedName("fromAddress")
    val fromAddress: String,
    @SerializedName("toAddress")
    val toAddress: String,
    val amount: String,
    @SerializedName("signingKind")
    val signingKind: String,
    @SerializedName("signingPayload")
    val signingPayload: String,
    @SerializedName("unsignedPayload")
    val unsignedPayload: String,
    @SerializedName("estimatedFee")
    val estimatedFee: String,
)

data class WalletTransferPrecheckRequest(
    @SerializedName("networkCode")
    val networkCode: String,
    @SerializedName("assetCode")
    val assetCode: String,
    @SerializedName("toAddress")
    val toAddress: String,
    val amount: String,
    @SerializedName("orderNo")
    val orderNo: String? = null,
)

data class WalletTransferPrecheckResponse(
    val code: String,
    val message: String,
    val data: WalletTransferPrecheckData?,
)

data class WalletTransferPrecheckData(
    @SerializedName("networkCode")
    val networkCode: String,
    @SerializedName("assetCode")
    val assetCode: String,
    @SerializedName("toAddressNormalized")
    val toAddressNormalized: String,
    val amount: String,
    @SerializedName("estimatedFee")
    val estimatedFee: String,
    @SerializedName("directBroadcastEnabled")
    val directBroadcastEnabled: Boolean,
    @SerializedName("proxyBroadcastEnabled")
    val proxyBroadcastEnabled: Boolean,
    val warnings: List<String> = emptyList(),
    @SerializedName("serviceEnabled")
    val serviceEnabled: Boolean,
)

data class WalletTransferProxyBroadcastRequest(
    @SerializedName("networkCode")
    val networkCode: String,
    @SerializedName("assetCode")
    val assetCode: String,
    @SerializedName("signedPayload")
    val signedPayload: String = "",
    @SerializedName("toAddress")
    val toAddress: String? = null,
    @SerializedName("serializedTx")
    val serializedTx: String? = null,
    @SerializedName("unsignedPayload")
    val unsignedPayload: String? = null,
    @SerializedName("signature")
    val signature: String? = null,
    @SerializedName("clientTxHash")
    val clientTxHash: String? = null,
)

data class WalletTransferProxyBroadcastResponse(
    val code: String,
    val message: String,
    val data: WalletTransferProxyBroadcastData?,
)

data class WalletTransferProxyBroadcastData(
    @SerializedName("networkCode")
    val networkCode: String,
    @SerializedName("broadcasted")
    val broadcasted: Boolean,
    @SerializedName("txHash")
    val txHash: String,
    @SerializedName("acceptedAt")
    val acceptedAt: String,
    @SerializedName("serviceEnabled")
    val serviceEnabled: Boolean? = null,
    @SerializedName("note")
    val note: String? = null,
)

data class WalletReceiveContextResponse(
    val code: String,
    val message: String,
    val data: WalletReceiveContextData?
)

data class WalletReceiveContextData(
    @SerializedName("selectedNetworkCode")
    val selectedNetworkCode: String,
    @SerializedName("selectedAssetCode")
    val selectedAssetCode: String,
    @SerializedName("chainItems")
    val chainItems: List<WalletChainItemData>,
    @SerializedName("assetItems")
    val assetItems: List<WalletAssetItemData>,
    val addresses: List<WalletPublicAddressData>,
    @SerializedName("defaultAddress")
    val defaultAddress: String? = null,
    @SerializedName("canShare")
    val canShare: Boolean,
    @SerializedName("walletExists")
    val walletExists: Boolean = false,
    @SerializedName("receiveState")
    val receiveState: String? = null,
    val status: String,
    val note: String,
    @SerializedName("shareText")
    val shareText: String? = null,
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

data class ReferralShareContextResponse(
    val code: String,
    val message: String,
    val data: ReferralShareContextData?
)

data class ReferralShareContextData(
    @SerializedName("referralCode")
    val referralCode: String,
    @SerializedName("shareLink")
    val shareLink: String,
    @SerializedName("shareTitle")
    val shareTitle: String,
    @SerializedName("shareMessage")
    val shareMessage: String,
    @SerializedName("level1InviteCount")
    val level1InviteCount: Int,
    @SerializedName("level2InviteCount")
    val level2InviteCount: Int,
    @SerializedName("availableAmountUsdt")
    val availableAmountUsdt: String,
    @SerializedName("frozenAmountUsdt")
    val frozenAmountUsdt: String,
    @SerializedName("hasBinding")
    val hasBinding: Boolean,
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
