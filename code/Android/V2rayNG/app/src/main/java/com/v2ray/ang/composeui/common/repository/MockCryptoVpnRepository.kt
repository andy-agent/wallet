package com.v2ray.ang.composeui.common.repository

import com.v2ray.ang.composeui.global.session.*
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
        code: String,
        inviteCode: String,
    ): EmailRegisterActionResult {
        return EmailRegisterActionResult(
            success = true,
            successMessage = "Preview: 账户 $email 已创建",
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
            summary = "先确认节点区域和支付网络，再生成订单。" + " · " + "导航参数" + "：" + args.planId,
            note = "Mock repository 已切换为手动创建订单流程。",
            orderNo = null,
            collectionAddress = "",
            qrText = "",
        )
    }

    override suspend fun getOrderCheckoutState(args: OrderCheckoutRouteArgs): OrderCheckoutUiState {
        return orderCheckoutPreviewState().copy(
            summary = "订单确认与支付前置页，承接套餐、支付方式与开票信息确认。" + " · " + "导航参数" + "：" + args.planId,
            note = "Mock repository 已回填真实创建订单结果。",
        )
    }

    override suspend fun getWalletPaymentConfirmState(args: WalletPaymentConfirmRouteArgs): WalletPaymentConfirmUiState {
        return walletPaymentConfirmPreviewState().copy(
            summary = "针对 VPN 购买流的独立确认页，校验订单、金额与风险提示后再发起支付。" + " · " + "导航参数" + "：" + args.orderId,
            note = "Mock repository 已回填 钱包支付确认 的路由参数，可继续替换为真实仓储实现。",
        )
    }

    override suspend fun getOrderResultState(args: OrderResultRouteArgs): OrderResultUiState {
        return orderResultPreviewState().copy(
            summary = "订单结果页用于展示开通结果、到期时间与下一步跳转入口。" + " · " + "导航参数" + "：" + args.orderId,
            note = "Mock repository 已回填 订单已生效 的路由参数，可继续替换为真实仓储实现。",
        )
    }

    override suspend fun getOrderListState(): OrderListUiState {
        return orderListPreviewState()
    }

    override suspend fun getOrderDetailState(args: OrderDetailRouteArgs): OrderDetailUiState {
        return orderDetailPreviewState().copy(
            summary = "展示单笔订单的支付信息、计划权益、开通时间与后续操作入口。" + " · " + "导航参数" + "：" + args.orderId,
            note = "Mock repository 已回填 订单详情 的路由参数，可继续替换为真实仓储实现。",
        )
    }

    override suspend fun getWalletPaymentState(): WalletPaymentUiState {
        return walletPaymentPreviewState()
    }

    override suspend fun getAssetDetailState(args: AssetDetailRouteArgs): AssetDetailUiState {
        return assetDetailPreviewState().copy(
            summary = "资产详情页承接行情、持仓、最近交易与快捷发送/收款操作。" + " · " + "导航参数" + "：" + args.assetId + "，" + args.chainId,
            note = "Mock repository 已回填 资产详情 的路由参数，可继续替换为真实仓储实现。",
        )
    }

    override suspend fun getReceiveState(args: ReceiveRouteArgs): ReceiveUiState {
        return receivePreviewState().copy(
            summary = "收款页展示当前地址、二维码与可切换网络，方便分享给转账方。" + " · " + "导航参数" + "：" + args.assetId + "，" + args.chainId,
            note = "Mock repository 已回填 收款资产 的路由参数，可继续替换为真实仓储实现。",
        )
    }

    override suspend fun getSendState(args: SendRouteArgs): SendUiState {
        return sendPreviewState().copy(
            summary = "发送页校验目标地址、金额、手续费与安全风险后发起链上转账。" + " · " + "导航参数" + "：" + args.assetId + "，" + args.chainId,
            note = "Mock repository 已回填 发送资产 的路由参数，可继续替换为真实仓储实现。",
        )
    }

    override suspend fun getSendResultState(args: SendResultRouteArgs): SendResultUiState {
        return sendResultPreviewState().copy(
            summary = "展示链上发送结果、交易哈希与后续返回入口。" + " · " + "导航参数" + "：" + args.txId,
            note = "Mock repository 已回填 发送完成 的路由参数，可继续替换为真实仓储实现。",
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
            summary = "法务详情页展示选中文档的条款正文与确认操作。" + " · " + "导航参数" + "：" + args.documentId,
            note = "Mock repository 已回填 文档详情 的路由参数，可继续替换为真实仓储实现。",
        )
    }

    override suspend fun getSubscriptionDetailState(args: SubscriptionDetailRouteArgs): SubscriptionDetailUiState {
        return subscriptionDetailPreviewState().copy(
            summary = "订阅详情页展示当前计划、剩余时长、自动续费与节点权益。" + " · " + "导航参数" + "：" + args.subscriptionId,
            note = "Mock repository 已回填 订阅详情 的路由参数，可继续替换为真实仓储实现。",
        )
    }

    override suspend fun getExpiryReminderState(args: ExpiryReminderRouteArgs): ExpiryReminderUiState {
        return expiryReminderPreviewState().copy(
            summary = "到期提醒页用于在续费前提示用户剩余时长、价格与自动续费状态。" + " · " + "导航参数" + "：" + args.daysLeft,
            note = "Mock repository 已回填 到期提醒 的路由参数，可继续替换为真实仓储实现。",
        )
    }

    override suspend fun getNodeSpeedTestState(args: NodeSpeedTestRouteArgs): NodeSpeedTestUiState {
        return nodeSpeedTestPreviewState().copy(
            summary = "节点测速页展示多节点测速结果、抖动与丢包率，辅助用户选择最佳线路。" + " · " + "导航参数" + "：" + args.nodeGroupId,
            note = "Mock repository 已回填 节点测速 的路由参数，可继续替换为真实仓储实现。",
        )
    }

    override suspend fun getAutoConnectRulesState(): AutoConnectRulesUiState {
        return autoConnectRulesPreviewState()
    }

    override suspend fun getCreateWalletState(args: CreateWalletRouteArgs): CreateWalletUiState {
        return createWalletPreviewState().copy(
            summary = "创建钱包页承接钱包命名、多链初始化与备份前校验。" + " · " + "导航参数" + "：" + args.mode,
            note = "Mock repository 已回填 创建钱包 的路由参数，可继续替换为真实仓储实现。",
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
            summary = "助记词导入页提供文本输入、词数校验与恢复后的链列表预估。" + " · " + "导航参数" + "：" + args.source,
            note = "Mock repository 已回填 输入助记词 的路由参数，可继续替换为真实仓储实现。",
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
            summary = "私钥导入页展示链选择、私钥输入与本地加密提示。" + " · " + "导航参数" + "：" + args.chainId,
            note = "Mock repository 已回填 输入私钥 的路由参数，可继续替换为真实仓储实现。",
        )
    }

    override suspend fun getBackupMnemonicState(args: BackupMnemonicRouteArgs): BackupMnemonicUiState {
        return backupMnemonicPreviewState().copy(
            summary = "备份助记词页展示助记词分组、风险说明与下一步确认入口。" + " · " + "导航参数" + "：" + args.walletId,
            note = "Mock repository 已回填 备份助记词 的路由参数，可继续替换为真实仓储实现。",
        )
    }

    override suspend fun getConfirmMnemonicState(args: ConfirmMnemonicRouteArgs): ConfirmMnemonicUiState {
        return confirmMnemonicPreviewState().copy(
            summary = "确认助记词页通过抽查顺序验证用户是否完成备份。" + " · " + "导航参数" + "：" + args.walletId,
            note = "Mock repository 已回填 确认助记词 的路由参数，可继续替换为真实仓储实现。",
        )
    }

    override suspend fun getSecurityCenterState(): SecurityCenterUiState {
        return securityCenterPreviewState()
    }

    override suspend fun getChainManagerState(args: ChainManagerRouteArgs): ChainManagerUiState {
        return chainManagerPreviewState().copy(
            summary = "链管理页用于启停链、排序、查看 RPC 状态与添加扩展网络。" + " · " + "导航参数" + "：" + args.walletId,
            note = "Mock repository 已回填 链管理 的路由参数，可继续替换为真实仓储实现。",
        )
    }

    override suspend fun getAddCustomTokenState(args: AddCustomTokenRouteArgs): AddCustomTokenUiState {
        return addCustomTokenPreviewState().copy(
            summary = "自定义代币页填写合约、符号与精度，补齐链上资产扩展能力。" + " · " + "导航参数" + "：" + args.chainId,
            note = "Mock repository 已回填 添加自定义代币 的路由参数，可继续替换为真实仓储实现。",
        )
    }

    override suspend fun getWalletManagerState(args: WalletManagerRouteArgs): WalletManagerUiState {
        return walletManagerPreviewState().copy(
            summary = "钱包管理页用于切换默认钱包、重命名、归档与导出安全操作。" + " · " + "导航参数" + "：" + args.walletId,
            note = "Mock repository 已回填 钱包管理 的路由参数，可继续替换为真实仓储实现。",
        )
    }

    override suspend fun getAddressBookState(args: AddressBookRouteArgs): AddressBookUiState {
        return addressBookPreviewState().copy(
            summary = "地址簿页管理常用地址、标签、白名单与快捷发送入口。" + " · " + "导航参数" + "：" + args.mode,
            note = "Mock repository 已回填 地址簿 的路由参数，可继续替换为真实仓储实现。",
        )
    }

    override suspend fun getGasSettingsState(args: GasSettingsRouteArgs): GasSettingsUiState {
        return gasSettingsPreviewState().copy(
            summary = "Gas 设置页支持慢 / 中 / 快档位与高级自定义参数。" + " · " + "导航参数" + "：" + args.chainId,
            note = "Mock repository 已回填 Gas 设置 的路由参数，可继续替换为真实仓储实现。",
        )
    }

    override suspend fun getSwapState(args: SwapRouteArgs): SwapUiState {
        return swapPreviewState().copy(
            summary = "Swap 页展示源币、目标币、滑点与预估接收数量。" + " · " + "导航参数" + "：" + args.fromAsset + "，" + args.toAsset,
            note = "Mock repository 已回填 币币兑换 的路由参数，可继续替换为真实仓储实现。",
        )
    }

    override suspend fun getBridgeState(args: BridgeRouteArgs): BridgeUiState {
        return bridgePreviewState().copy(
            summary = "Bridge 页补齐跨链资产搬运、目标地址与预计到账时间。" + " · " + "导航参数" + "：" + args.fromChainId + "，" + args.toChainId,
            note = "Mock repository 已回填 跨链桥接 的路由参数，可继续替换为真实仓储实现。",
        )
    }

    override suspend fun getDappBrowserState(args: DappBrowserRouteArgs): DappBrowserUiState {
        return dappBrowserPreviewState().copy(
            summary = "DApp 浏览器页提供搜索、收藏与安全评分入口。" + " · " + "导航参数" + "：" + args.entry,
            note = "Mock repository 已回填 DApp 浏览器 的路由参数，可继续替换为真实仓储实现。",
        )
    }

    override suspend fun getWalletConnectSessionState(args: WalletConnectSessionRouteArgs): WalletConnectSessionUiState {
        return walletConnectSessionPreviewState().copy(
            summary = "WalletConnect 会话页展示活跃连接、权限范围与失效时间。" + " · " + "导航参数" + "：" + args.sessionId,
            note = "Mock repository 已回填 连接会话 的路由参数，可继续替换为真实仓储实现。",
        )
    }

    override suspend fun getSignMessageConfirmState(args: SignMessageConfirmRouteArgs): SignMessageConfirmUiState {
        return signMessageConfirmPreviewState().copy(
            summary = "签名确认页展示 DApp 请求、链、金额与风险提示。" + " · " + "导航参数" + "：" + args.requestId,
            note = "Mock repository 已回填 签名确认 的路由参数，可继续替换为真实仓储实现。",
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
