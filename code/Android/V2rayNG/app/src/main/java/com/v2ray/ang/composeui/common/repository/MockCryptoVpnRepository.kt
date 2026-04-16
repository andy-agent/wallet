package com.v2ray.ang.composeui.common.repository

import com.v2ray.ang.composeui.global.session.*
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.model.*
import com.v2ray.ang.composeui.p1.model.*
import com.v2ray.ang.composeui.p2.model.*
import com.v2ray.ang.composeui.p2extended.model.*
import com.v2ray.ang.payment.data.api.WalletLifecycleData

class MockCryptoVpnRepository : CryptoVpnRepository {
    override suspend fun getForceUpdateState(): ForceUpdateUiState {
        return forceUpdatePreviewState()
    }

    override suspend fun getOptionalUpdateState(): OptionalUpdateUiState {
        return optionalUpdatePreviewState()
    }

    override suspend fun getEmailRegisterState(): EmailRegisterUiState {
        return emailRegisterPreviewState()
    }

    override suspend fun requestEmailRegisterCode(email: String): EmailRegisterActionResult {
        return EmailRegisterActionResult(
            success = true,
            successMessage = "Preview: 验证码已发送到 $email",
        )
    }

    override suspend fun registerEmail(
        email: String,
        password: String,
        inviteCode: String,
    ): EmailRegisterActionResult {
        return EmailRegisterActionResult(
            success = true,
            successMessage = "Preview: 账户 $email 已创建",
            nextRoute = CryptoVpnRouteSpec.walletOnboarding.pattern,
        )
    }

    override suspend fun getResetPasswordState(): ResetPasswordUiState {
        return resetPasswordPreviewState()
    }

    override suspend fun requestResetPasswordCode(email: String): ResetPasswordActionResult {
        return ResetPasswordActionResult(
            success = true,
            successMessage = "Preview: 重置验证码已发送到 $email",
        )
    }

    override suspend fun resetPassword(
        email: String,
        code: String,
        password: String,
    ): ResetPasswordActionResult {
        return ResetPasswordActionResult(
            success = true,
            successMessage = "Preview: 密码已重置",
        )
    }

    override suspend fun getPlansState(): PlansUiState {
        return plansPreviewState()
    }

    override suspend fun getRegionSelectionState(): RegionSelectionUiState {
        return regionSelectionPreviewState()
    }

    override suspend fun selectVpnNode(lineCode: String, nodeId: String): RegionSelectionUiState {
        return regionSelectionPreviewState().copy(
            selectedLineCode = lineCode,
            selectedNodeId = nodeId,
            selectionApplied = true,
        )
    }

    override suspend fun prepareOrderCheckoutState(args: OrderCheckoutRouteArgs): OrderCheckoutUiState {
        return orderCheckoutPreviewState().copy(
            summary = "Preview: 导航参数 ${args.planId}",
            note = "Preview only",
            orderNo = null,
            collectionAddress = "",
            qrText = "",
        )
    }

    override suspend fun getOrderCheckoutState(args: OrderCheckoutRouteArgs): OrderCheckoutUiState {
        return orderCheckoutPreviewState().copy(
            summary = "Preview: 导航参数 ${args.planId}",
            note = "Preview only",
        )
    }

    override suspend fun getWalletPaymentConfirmState(args: WalletPaymentConfirmRouteArgs): WalletPaymentConfirmUiState {
        return walletPaymentConfirmPreviewState().copy(
            summary = "Preview: 导航参数 ${args.orderId}",
            note = "Preview only",
        )
    }

    override suspend fun getOrderResultState(args: OrderResultRouteArgs): OrderResultUiState {
        return orderResultPreviewState().copy(
            summary = "Preview: 导航参数 ${args.orderId}",
            note = "Preview only",
        )
    }

    override suspend fun getOrderListState(): OrderListUiState {
        return orderListPreviewState()
    }

    override suspend fun getOrderDetailState(args: OrderDetailRouteArgs): OrderDetailUiState {
        return orderDetailPreviewState().copy(
            summary = "Preview: 导航参数 ${args.orderId}",
            note = "Preview only",
        )
    }

    override suspend fun getWalletPaymentState(): WalletPaymentUiState {
        return walletPaymentPreviewState()
    }

    override suspend fun getAssetDetailState(args: AssetDetailRouteArgs): AssetDetailUiState {
        return assetDetailPreviewState().copy(
            summary = "Preview: 导航参数 ${args.assetId}，${args.chainId}",
            note = "Preview only",
        )
    }

    override suspend fun getReceiveState(args: ReceiveRouteArgs): ReceiveUiState {
        return receivePreviewState().copy(
            summary = "Preview: 导航参数 ${args.assetId}，${args.chainId}",
            note = "Preview only",
        )
    }

    override suspend fun getSendState(args: SendRouteArgs): SendUiState {
        return sendPreviewState().copy(
            summary = "Preview: 导航参数 ${args.assetId}，${args.chainId}",
            note = "Preview only",
        )
    }

    override suspend fun getSendResultState(args: SendResultRouteArgs): SendResultUiState {
        return sendResultPreviewState().copy(
            summary = "Preview: 导航参数 ${args.txId}",
            note = "Preview only",
        )
    }

    override suspend fun getInviteCenterState(): InviteCenterUiState {
        return inviteCenterPreviewState()
    }

    override suspend fun getInviteShareState(): InviteShareUiState {
        return inviteSharePreviewState()
    }

    override suspend fun getCommissionLedgerState(): CommissionLedgerUiState {
        return commissionLedgerPreviewState()
    }

    override suspend fun getWithdrawState(): WithdrawUiState {
        return withdrawPreviewState()
    }

    override suspend fun getProfileState(): ProfileUiState {
        return profilePreviewState()
    }

    override suspend fun getLegalDocumentsState(): LegalDocumentsUiState {
        return legalDocumentsPreviewState()
    }

    override suspend fun getAboutAppState(): AboutAppUiState {
        return aboutAppPreviewState()
    }

    override suspend fun getLegalDocumentDetailState(args: LegalDocumentDetailRouteArgs): LegalDocumentDetailUiState {
        return legalDocumentDetailPreviewState().copy(
            summary = "Preview: 导航参数 ${args.documentId}",
            note = "Preview only",
        )
    }

    override suspend fun getSubscriptionDetailState(args: SubscriptionDetailRouteArgs): SubscriptionDetailUiState {
        return subscriptionDetailPreviewState().copy(
            summary = "Preview: 导航参数 ${args.subscriptionId}",
            note = "Preview only",
        )
    }

    override suspend fun getExpiryReminderState(args: ExpiryReminderRouteArgs): ExpiryReminderUiState {
        return expiryReminderPreviewState().copy(
            summary = "Preview: 导航参数 ${args.daysLeft}",
            note = "Preview only",
        )
    }

    override suspend fun getNodeSpeedTestState(args: NodeSpeedTestRouteArgs): NodeSpeedTestUiState {
        return nodeSpeedTestPreviewState().copy(
            summary = "Preview: 导航参数 ${args.nodeGroupId}",
            note = "Preview only",
        )
    }

    override suspend fun getAutoConnectRulesState(): AutoConnectRulesUiState {
        return autoConnectRulesPreviewState()
    }

    override suspend fun getCreateWalletState(args: CreateWalletRouteArgs): CreateWalletUiState {
        return createWalletPreviewState().copy(
            summary = "Preview: 导航参数 ${args.mode}",
            note = "Preview only",
        )
    }

    override suspend fun createWallet(displayName: String): WalletLifecycleMutationResult {
        return WalletLifecycleMutationResult(
            success = displayName.isNotBlank(),
            walletId = "mock-wallet",
            errorMessage = if (displayName.isBlank()) "请输入钱包名称" else null,
        )
    }

    override suspend fun acknowledgeWalletBackup(): Result<WalletLifecycleData> {
        return Result.success(
            WalletLifecycleData(
                accountId = "mock-account",
                walletExists = true,
                receiveState = "NO_ADDRESS",
                lifecycleStatus = "CREATED",
                sourceType = "CREATE",
                walletId = "mock-wallet",
                displayName = "Mock Wallet",
                configuredAddressCount = 0,
            ),
        )
    }

    override suspend fun confirmWalletBackup(): Result<WalletLifecycleData> {
        return Result.success(
            WalletLifecycleData(
                accountId = "mock-account",
                walletExists = true,
                receiveState = "NO_ADDRESS",
                lifecycleStatus = "ACTIVE",
                sourceType = "CREATE",
                walletId = "mock-wallet",
                displayName = "Mock Wallet",
                configuredAddressCount = 0,
            ),
        )
    }

    override suspend fun getImportWalletMethodState(): ImportWalletMethodUiState {
        return importWalletMethodPreviewState()
    }

    override suspend fun getImportMnemonicState(args: ImportMnemonicRouteArgs): ImportMnemonicUiState {
        return importMnemonicPreviewState().copy(
            summary = "Preview: 导航参数 ${args.source}",
            note = "Preview only",
        )
    }

    override suspend fun importWalletFromMnemonic(
        source: String,
        mnemonic: String,
        walletName: String,
    ): WalletLifecycleMutationResult {
        return WalletLifecycleMutationResult(
            success = mnemonic.isNotBlank() && walletName.isNotBlank(),
            walletId = "mock-wallet",
            errorMessage = if (mnemonic.isBlank() || walletName.isBlank()) "请填写助记词和钱包名称" else null,
        )
    }

    override suspend fun getImportPrivateKeyState(args: ImportPrivateKeyRouteArgs): ImportPrivateKeyUiState {
        return importPrivateKeyPreviewState().copy(
            summary = "Preview: 导航参数 ${args.chainId}",
            note = "Preview only",
        )
    }

    override suspend fun getBackupMnemonicState(args: BackupMnemonicRouteArgs): BackupMnemonicUiState {
        return backupMnemonicPreviewState().copy(
            summary = "Preview: 导航参数 ${args.walletId}",
            note = "Preview only",
        )
    }

    override suspend fun getConfirmMnemonicState(args: ConfirmMnemonicRouteArgs): ConfirmMnemonicUiState {
        return confirmMnemonicPreviewState().copy(
            summary = "Preview: 导航参数 ${args.walletId}",
            note = "Preview only",
        )
    }

    override suspend fun getSecurityCenterState(): SecurityCenterUiState {
        return securityCenterPreviewState()
    }

    override suspend fun exportLocalWallet(): LocalWalletActionResult {
        return LocalWalletActionResult(
            success = true,
            walletId = "preview_wallet",
            exportFileName = "cryptovpn-wallet-backup-preview.json",
            exportContent = """{"version":"cryptovpn-wallet-backup-v1","ciphertext":"preview"}""",
        )
    }

    override suspend fun clearLocalWallet(): LocalWalletActionResult {
        return LocalWalletActionResult(success = true)
    }

    override suspend fun logoutSession(): LogoutResult {
        return LogoutResult(success = true)
    }

    override suspend fun getChainManagerState(args: ChainManagerRouteArgs): ChainManagerUiState {
        return chainManagerPreviewState().copy(
            summary = "Preview: 导航参数 ${args.walletId}",
            note = "Preview only",
        )
    }

    override suspend fun getAddCustomTokenState(args: AddCustomTokenRouteArgs): AddCustomTokenUiState {
        return addCustomTokenPreviewState().copy(
            summary = "Preview: 导航参数 ${args.chainId}",
            note = "Preview only",
        )
    }

    override suspend fun getWalletManagerState(args: WalletManagerRouteArgs): WalletManagerUiState {
        return walletManagerPreviewState().copy(
            summary = "Preview: 导航参数 ${args.walletId}",
            note = "Preview only",
        )
    }

    override suspend fun getAddressBookState(args: AddressBookRouteArgs): AddressBookUiState {
        return addressBookPreviewState().copy(
            summary = "Preview: 导航参数 ${args.mode}",
            note = "Preview only",
        )
    }

    override suspend fun getGasSettingsState(args: GasSettingsRouteArgs): GasSettingsUiState {
        return gasSettingsPreviewState().copy(
            summary = "Preview: 导航参数 ${args.chainId}",
            note = "Preview only",
        )
    }

    override suspend fun getSwapState(args: SwapRouteArgs): SwapUiState {
        return swapPreviewState().copy(
            summary = "Preview: 导航参数 ${args.fromAsset}，${args.toAsset}",
            note = "Preview only",
        )
    }

    override suspend fun getBridgeState(args: BridgeRouteArgs): BridgeUiState {
        return bridgePreviewState().copy(
            summary = "Preview: 导航参数 ${args.fromChainId}，${args.toChainId}",
            note = "Preview only",
        )
    }

    override suspend fun getDappBrowserState(args: DappBrowserRouteArgs): DappBrowserUiState {
        return dappBrowserPreviewState().copy(
            summary = "Preview: 导航参数 ${args.entry}",
            note = "Preview only",
        )
    }

    override suspend fun getWalletConnectSessionState(args: WalletConnectSessionRouteArgs): WalletConnectSessionUiState {
        return walletConnectSessionPreviewState().copy(
            summary = "Preview: 导航参数 ${args.sessionId}",
            note = "Preview only",
        )
    }

    override suspend fun getSignMessageConfirmState(args: SignMessageConfirmRouteArgs): SignMessageConfirmUiState {
        return signMessageConfirmPreviewState().copy(
            summary = "Preview: 导航参数 ${args.requestId}",
            note = "Preview only",
        )
    }

    override suspend fun getRiskAuthorizationsState(): RiskAuthorizationsUiState {
        return riskAuthorizationsPreviewState()
    }

    override suspend fun getNftGalleryState(): NftGalleryUiState {
        return nftGalleryPreviewState()
    }

    override suspend fun getStakingEarnState(): StakingEarnUiState {
        return stakingEarnPreviewState()
    }

    override suspend fun getSessionEvictedDialogState(): SessionEvictedDialogUiState {
        return sessionEvictedDialogPreviewState()
    }
}
