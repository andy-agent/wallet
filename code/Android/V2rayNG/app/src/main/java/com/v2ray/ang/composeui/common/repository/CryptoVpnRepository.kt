package com.v2ray.ang.composeui.common.repository

import com.v2ray.ang.composeui.global.session.*
import com.v2ray.ang.composeui.p0.model.*
import com.v2ray.ang.composeui.p1.model.*
import com.v2ray.ang.composeui.p2.model.*
import com.v2ray.ang.composeui.p2extended.model.*
import com.v2ray.ang.payment.data.api.WalletLifecycleData

data class WalletLifecycleMutationResult(
    val success: Boolean,
    val walletId: String? = null,
    val errorMessage: String? = null,
)

data class WalletCreationProgress(
    val stageLabel: String,
    val progress: Float,
    val etaLabel: String = "预计约 10 秒",
)

data class SendSubmissionResult(
    val success: Boolean,
    val txHash: String? = null,
    val errorMessage: String? = null,
)

data class LogoutResult(
    val success: Boolean,
    val errorMessage: String? = null,
)

data class LocalWalletActionResult(
    val success: Boolean,
    val walletId: String? = null,
    val exportContent: String? = null,
    val exportFileName: String? = null,
    val exportMimeType: String = "application/json",
    val message: String? = null,
    val errorMessage: String? = null,
)

interface CryptoVpnRepository {
    suspend fun getForceUpdateState(): ForceUpdateUiState
    suspend fun getOptionalUpdateState(): OptionalUpdateUiState
    suspend fun getEmailRegisterState(): EmailRegisterUiState
    suspend fun requestEmailRegisterCode(email: String): EmailRegisterActionResult
    suspend fun registerEmail(
        email: String,
        password: String,
        inviteCode: String,
    ): EmailRegisterActionResult
    suspend fun getResetPasswordState(): ResetPasswordUiState
    suspend fun requestResetPasswordCode(email: String): ResetPasswordActionResult
    suspend fun resetPassword(
        email: String,
        code: String,
        password: String,
    ): ResetPasswordActionResult
    suspend fun getCachedPlansState(): PlansUiState? = null
    suspend fun getPlansState(): PlansUiState
    suspend fun getRegionSelectionState(): RegionSelectionUiState
    suspend fun getCachedRegionSelectionState(): RegionSelectionUiState? = null
    suspend fun selectVpnNode(lineCode: String, nodeId: String): RegionSelectionUiState
    suspend fun getCachedOrderCheckoutState(args: OrderCheckoutRouteArgs): OrderCheckoutUiState? = null
    suspend fun prepareOrderCheckoutState(args: OrderCheckoutRouteArgs): OrderCheckoutUiState
    suspend fun refreshOrderCheckoutState(args: OrderCheckoutRouteArgs): OrderCheckoutUiState =
        prepareOrderCheckoutState(args)
    suspend fun getOrderCheckoutState(args: OrderCheckoutRouteArgs): OrderCheckoutUiState
    suspend fun getWalletPaymentConfirmState(args: WalletPaymentConfirmRouteArgs): WalletPaymentConfirmUiState
    suspend fun submitWalletOrderPayment(
        orderNo: String,
        walletId: String,
        chainAccountId: String,
    ): SendSubmissionResult = SendSubmissionResult(success = false, errorMessage = "Wallet order payment unavailable")
    suspend fun getOrderResultState(args: OrderResultRouteArgs): OrderResultUiState
    suspend fun getOrderListState(): OrderListUiState
    suspend fun getOrderDetailState(args: OrderDetailRouteArgs): OrderDetailUiState
    suspend fun getWalletPaymentState(): WalletPaymentUiState
    suspend fun getWalletLifecycleState(): Result<WalletLifecycleData> =
        Result.failure(IllegalStateException("Wallet lifecycle unavailable"))
    suspend fun createWalletProfile(displayName: String): Result<WalletLifecycleData> =
        Result.failure(IllegalStateException("Wallet create unavailable"))
    suspend fun importWalletProfile(displayName: String, mnemonic: String): Result<WalletLifecycleData> =
        Result.failure(IllegalStateException("Wallet import unavailable"))
    suspend fun acknowledgeWalletBackup(): Result<WalletLifecycleData> =
        Result.failure(IllegalStateException("Wallet backup acknowledgement unavailable"))
    suspend fun confirmWalletBackup(): Result<WalletLifecycleData> =
        Result.failure(IllegalStateException("Wallet backup confirmation unavailable"))
    suspend fun getAssetDetailState(args: AssetDetailRouteArgs): AssetDetailUiState
    suspend fun getCachedReceiveState(args: ReceiveRouteArgs): ReceiveUiState? = null
    suspend fun getReceiveState(args: ReceiveRouteArgs): ReceiveUiState
    suspend fun getSendState(args: SendRouteArgs): SendUiState
    suspend fun submitSend(args: SendRouteArgs, toAddress: String, amount: String, memo: String = ""): SendSubmissionResult =
        SendSubmissionResult(success = false, errorMessage = "Send unavailable")
    suspend fun getSendResultState(args: SendResultRouteArgs): SendResultUiState
    suspend fun getInviteCenterState(): InviteCenterUiState
    suspend fun getInviteShareState(): InviteShareUiState
    suspend fun getCommissionLedgerState(): CommissionLedgerUiState
    suspend fun getWithdrawState(): WithdrawUiState
    suspend fun getProfileState(): ProfileUiState
    suspend fun getLegalDocumentsState(): LegalDocumentsUiState
    suspend fun getAboutAppState(): AboutAppUiState
    suspend fun getLegalDocumentDetailState(args: LegalDocumentDetailRouteArgs): LegalDocumentDetailUiState
    suspend fun getSubscriptionDetailState(args: SubscriptionDetailRouteArgs): SubscriptionDetailUiState
    suspend fun getExpiryReminderState(args: ExpiryReminderRouteArgs): ExpiryReminderUiState
    suspend fun getNodeSpeedTestState(args: NodeSpeedTestRouteArgs): NodeSpeedTestUiState
    suspend fun getAutoConnectRulesState(): AutoConnectRulesUiState
    suspend fun getCreateWalletState(args: CreateWalletRouteArgs): CreateWalletUiState
    suspend fun createWallet(
        displayName: String,
        onProgress: (WalletCreationProgress) -> Unit = {},
    ): WalletLifecycleMutationResult
    suspend fun getImportWalletMethodState(): ImportWalletMethodUiState
    suspend fun getImportMnemonicState(args: ImportMnemonicRouteArgs): ImportMnemonicUiState
    suspend fun importWalletFromMnemonic(
        source: String,
        mnemonic: String,
        walletName: String,
    ): WalletLifecycleMutationResult
    suspend fun getImportWatchWalletState(): ImportWatchWalletUiState
    suspend fun importWatchOnlyWallet(
        walletName: String,
        networkCode: String,
        address: String,
    ): WalletLifecycleMutationResult = WalletLifecycleMutationResult(
        success = false,
        errorMessage = "Watch-only import unavailable",
    )
    suspend fun getImportPrivateKeyState(args: ImportPrivateKeyRouteArgs): ImportPrivateKeyUiState
    suspend fun getBackupMnemonicState(args: BackupMnemonicRouteArgs): BackupMnemonicUiState
    suspend fun getConfirmMnemonicState(args: ConfirmMnemonicRouteArgs): ConfirmMnemonicUiState
    suspend fun getSecurityCenterState(): SecurityCenterUiState
    suspend fun exportLocalWallet(): LocalWalletActionResult =
        LocalWalletActionResult(success = false, errorMessage = "Wallet export unavailable")
    suspend fun clearLocalWallet(): LocalWalletActionResult =
        LocalWalletActionResult(success = false, errorMessage = "Wallet clear unavailable")
    suspend fun logoutSession(): LogoutResult = LogoutResult(success = false, errorMessage = "Logout unavailable")
    suspend fun getChainManagerState(args: ChainManagerRouteArgs): ChainManagerUiState
    suspend fun getAddCustomTokenState(args: AddCustomTokenRouteArgs): AddCustomTokenUiState
    suspend fun getCachedWalletManagerState(args: WalletManagerRouteArgs): WalletManagerUiState? = null
    suspend fun getWalletManagerState(args: WalletManagerRouteArgs): WalletManagerUiState
    suspend fun setDefaultWallet(walletId: String): WalletLifecycleMutationResult =
        WalletLifecycleMutationResult(success = false, errorMessage = "Set default wallet unavailable")
    suspend fun getAddressBookState(args: AddressBookRouteArgs): AddressBookUiState
    suspend fun getGasSettingsState(args: GasSettingsRouteArgs): GasSettingsUiState
    suspend fun getSwapState(args: SwapRouteArgs): SwapUiState
    suspend fun getBridgeState(args: BridgeRouteArgs): BridgeUiState
    suspend fun getDappBrowserState(args: DappBrowserRouteArgs): DappBrowserUiState
    suspend fun getWalletConnectSessionState(args: WalletConnectSessionRouteArgs): WalletConnectSessionUiState
    suspend fun getSignMessageConfirmState(args: SignMessageConfirmRouteArgs): SignMessageConfirmUiState
    suspend fun getRiskAuthorizationsState(): RiskAuthorizationsUiState
    suspend fun getNftGalleryState(): NftGalleryUiState
    suspend fun getStakingEarnState(): StakingEarnUiState
    suspend fun getSessionEvictedDialogState(): SessionEvictedDialogUiState
}
