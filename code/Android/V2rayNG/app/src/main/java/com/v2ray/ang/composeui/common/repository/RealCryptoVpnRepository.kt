package com.v2ray.ang.composeui.common.repository

import android.content.Context
import com.v2ray.ang.AppConfig
import com.v2ray.ang.BuildConfig
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.global.session.SessionEvictedDialogUiState
import com.v2ray.ang.composeui.global.session.sessionEvictedDialogPreviewState
import com.v2ray.ang.composeui.p0.model.EmailRegisterActionResult
import com.v2ray.ang.composeui.p0.model.EmailRegisterUiState
import com.v2ray.ang.composeui.p0.model.ForceUpdateUiState
import com.v2ray.ang.composeui.p0.model.OptionalUpdateUiState
import com.v2ray.ang.composeui.p0.model.ResetPasswordActionResult
import com.v2ray.ang.composeui.p0.model.ResetPasswordUiState
import com.v2ray.ang.composeui.p0.model.emailRegisterPreviewState
import com.v2ray.ang.composeui.p0.model.optionalUpdatePreviewState
import com.v2ray.ang.composeui.p0.model.resetPasswordPreviewState
import com.v2ray.ang.composeui.p1.model.OrderCheckoutRouteArgs
import com.v2ray.ang.composeui.p1.model.OrderCheckoutUiState
import com.v2ray.ang.composeui.p1.model.OrderDetailRowUi
import com.v2ray.ang.composeui.p1.model.OrderDetailRouteArgs
import com.v2ray.ang.composeui.p1.model.OrderDetailUiState
import com.v2ray.ang.composeui.p1.model.CheckoutPaymentOptionUi
import com.v2ray.ang.composeui.p1.model.OrderListItemUi
import com.v2ray.ang.composeui.p1.model.OrderListUiState
import com.v2ray.ang.composeui.p1.model.OrderResultRouteArgs
import com.v2ray.ang.composeui.p1.model.OrderResultUiState
import com.v2ray.ang.composeui.p1.model.P1ScreenState
import com.v2ray.ang.composeui.p1.model.PlanOptionUi
import com.v2ray.ang.composeui.p1.model.PlansUiState
import com.v2ray.ang.composeui.p1.model.RegionOptionUi
import com.v2ray.ang.composeui.p1.model.RegionSelectionUiState
import com.v2ray.ang.composeui.p1.model.WalletPaymentConfirmRouteArgs
import com.v2ray.ang.composeui.p1.model.WalletPaymentConfirmUiState
import com.v2ray.ang.composeui.p1.model.WalletPaymentUiState
import com.v2ray.ang.composeui.p1.model.checkoutPaymentLabel
import com.v2ray.ang.composeui.p1.model.orderCheckoutPreviewState
import com.v2ray.ang.composeui.p1.model.orderDetailPreviewState
import com.v2ray.ang.composeui.p1.model.orderListPreviewState
import com.v2ray.ang.composeui.p1.model.orderResultPreviewState
import com.v2ray.ang.composeui.p1.model.plansPreviewState
import com.v2ray.ang.composeui.p1.model.regionSelectionPreviewState
import com.v2ray.ang.composeui.p1.model.walletPaymentPreviewState
import com.v2ray.ang.composeui.p2.model.AssetDetailRouteArgs
import com.v2ray.ang.composeui.p2.model.AssetDetailUiState
import com.v2ray.ang.composeui.p2.model.AboutAppUiState
import com.v2ray.ang.composeui.p2.model.CommissionLedgerUiState
import com.v2ray.ang.composeui.p2.model.InviteCenterUiState
import com.v2ray.ang.composeui.p2.model.InviteShareUiState
import com.v2ray.ang.composeui.p2.model.LegalDocumentDetailRouteArgs
import com.v2ray.ang.composeui.p2.model.LegalDocumentDetailUiState
import com.v2ray.ang.composeui.p2.model.LegalDocumentsUiState
import com.v2ray.ang.composeui.p2.model.ProfileUiState
import com.v2ray.ang.composeui.p2.model.ReceiveRouteArgs
import com.v2ray.ang.composeui.p2.model.ReceiveUiState
import com.v2ray.ang.composeui.p2.model.SendResultRouteArgs
import com.v2ray.ang.composeui.p2.model.SendResultUiState
import com.v2ray.ang.composeui.p2.model.SendRouteArgs
import com.v2ray.ang.composeui.p2.model.SendUiState
import com.v2ray.ang.composeui.p2.model.WithdrawUiState
import com.v2ray.ang.composeui.p2.model.aboutAppPreviewState
import com.v2ray.ang.composeui.p2.model.commissionLedgerPreviewState
import com.v2ray.ang.composeui.p2.model.inviteCenterPreviewState
import com.v2ray.ang.composeui.p2.model.inviteSharePreviewState
import com.v2ray.ang.composeui.p2.model.legalDocumentDetailPreviewState
import com.v2ray.ang.composeui.p2.model.legalDocumentsPreviewState
import com.v2ray.ang.composeui.p2.model.profilePreviewState
import com.v2ray.ang.composeui.p2.model.receivePreviewState
import com.v2ray.ang.composeui.p2.model.sendResultPreviewState
import com.v2ray.ang.composeui.p2.model.withdrawPreviewState
import com.v2ray.ang.composeui.p2extended.model.AddCustomTokenRouteArgs
import com.v2ray.ang.composeui.p2extended.model.AddCustomTokenUiState
import com.v2ray.ang.composeui.p2extended.model.AddressBookRouteArgs
import com.v2ray.ang.composeui.p2extended.model.AddressBookUiState
import com.v2ray.ang.composeui.p2extended.model.AutoConnectRulesUiState
import com.v2ray.ang.composeui.p2extended.model.BackupMnemonicRouteArgs
import com.v2ray.ang.composeui.p2extended.model.BackupMnemonicUiState
import com.v2ray.ang.composeui.p2extended.model.BridgeRouteArgs
import com.v2ray.ang.composeui.p2extended.model.BridgeUiState
import com.v2ray.ang.composeui.p2extended.model.ChainManagerRouteArgs
import com.v2ray.ang.composeui.p2extended.model.ChainManagerUiState
import com.v2ray.ang.composeui.p2extended.model.ConfirmMnemonicRouteArgs
import com.v2ray.ang.composeui.p2extended.model.ConfirmMnemonicUiState
import com.v2ray.ang.composeui.p2extended.model.CreateWalletRouteArgs
import com.v2ray.ang.composeui.p2extended.model.CreateWalletUiState
import com.v2ray.ang.composeui.p2extended.model.DappBrowserRouteArgs
import com.v2ray.ang.composeui.p2extended.model.DappBrowserUiState
import com.v2ray.ang.composeui.p2extended.model.ExpiryReminderRouteArgs
import com.v2ray.ang.composeui.p2extended.model.ExpiryReminderUiState
import com.v2ray.ang.composeui.p2extended.model.GasSettingsRouteArgs
import com.v2ray.ang.composeui.p2extended.model.GasSettingsUiState
import com.v2ray.ang.composeui.p2extended.model.ImportMnemonicRouteArgs
import com.v2ray.ang.composeui.p2extended.model.ImportMnemonicUiState
import com.v2ray.ang.composeui.p2extended.model.ImportPrivateKeyRouteArgs
import com.v2ray.ang.composeui.p2extended.model.ImportPrivateKeyUiState
import com.v2ray.ang.composeui.p2extended.model.ImportWalletMethodUiState
import com.v2ray.ang.composeui.p2extended.model.NftGalleryUiState
import com.v2ray.ang.composeui.p2extended.model.NodeSpeedTestRouteArgs
import com.v2ray.ang.composeui.p2extended.model.NodeSpeedTestUiState
import com.v2ray.ang.composeui.p2extended.model.RiskAuthorizationsUiState
import com.v2ray.ang.composeui.p2extended.model.SecurityCenterUiState
import com.v2ray.ang.composeui.p2extended.model.SignMessageConfirmRouteArgs
import com.v2ray.ang.composeui.p2extended.model.SignMessageConfirmUiState
import com.v2ray.ang.composeui.p2extended.model.StakingEarnUiState
import com.v2ray.ang.composeui.p2extended.model.SubscriptionDetailRouteArgs
import com.v2ray.ang.composeui.p2extended.model.SubscriptionDetailUiState
import com.v2ray.ang.composeui.p2extended.model.SwapRouteArgs
import com.v2ray.ang.composeui.p2extended.model.SwapUiState
import com.v2ray.ang.composeui.p2extended.model.WalletConnectSessionRouteArgs
import com.v2ray.ang.composeui.p2extended.model.WalletConnectSessionUiState
import com.v2ray.ang.composeui.p2extended.model.WalletManagerRouteArgs
import com.v2ray.ang.composeui.p2extended.model.WalletManagerUiState
import com.v2ray.ang.composeui.p2extended.model.addCustomTokenPreviewState
import com.v2ray.ang.composeui.p2extended.model.addressBookPreviewState
import com.v2ray.ang.composeui.p2extended.model.autoConnectRulesPreviewState
import com.v2ray.ang.composeui.p2extended.model.backupMnemonicPreviewState
import com.v2ray.ang.composeui.p2extended.model.bridgePreviewState
import com.v2ray.ang.composeui.p2extended.model.chainManagerPreviewState
import com.v2ray.ang.composeui.p2extended.model.confirmMnemonicPreviewState
import com.v2ray.ang.composeui.p2extended.model.createWalletPreviewState
import com.v2ray.ang.composeui.p2extended.model.dappBrowserPreviewState
import com.v2ray.ang.composeui.p2extended.model.expiryReminderPreviewState
import com.v2ray.ang.composeui.p2extended.model.gasSettingsPreviewState
import com.v2ray.ang.composeui.p2extended.model.importMnemonicPreviewState
import com.v2ray.ang.composeui.p2extended.model.importPrivateKeyPreviewState
import com.v2ray.ang.composeui.p2extended.model.importWalletMethodPreviewState
import com.v2ray.ang.composeui.p2extended.model.nftGalleryPreviewState
import com.v2ray.ang.composeui.p2extended.model.nodeSpeedTestPreviewState
import com.v2ray.ang.composeui.p2extended.model.riskAuthorizationsPreviewState
import com.v2ray.ang.composeui.p2extended.model.securityCenterPreviewState
import com.v2ray.ang.composeui.p2extended.model.signMessageConfirmPreviewState
import com.v2ray.ang.composeui.p2extended.model.stakingEarnPreviewState
import com.v2ray.ang.composeui.p2extended.model.subscriptionDetailPreviewState
import com.v2ray.ang.composeui.p2extended.model.swapPreviewState
import com.v2ray.ang.composeui.p2extended.model.walletConnectSessionPreviewState
import com.v2ray.ang.composeui.p2extended.model.walletManagerPreviewState
import com.v2ray.ang.payment.PaymentConfig
import com.v2ray.ang.payment.data.api.CommissionLedgerItem
import com.v2ray.ang.payment.data.api.CommissionSummaryData
import com.v2ray.ang.payment.data.api.CurrentSubscriptionData
import com.v2ray.ang.payment.data.api.MeData
import com.v2ray.ang.payment.data.api.PasswordResetCodeRequest
import com.v2ray.ang.payment.data.api.PasswordResetRequest
import com.v2ray.ang.payment.data.api.ReferralOverviewData
import com.v2ray.ang.payment.data.api.WalletLifecycleData
import com.v2ray.ang.payment.data.api.WithdrawalItem
import com.v2ray.ang.payment.data.local.entity.OrderEntity
import com.v2ray.ang.payment.data.local.entity.UserEntity
import com.v2ray.ang.payment.data.local.entity.VpnNodeCacheEntity
import com.v2ray.ang.payment.data.local.entity.VpnNodeRuntimeEntity
import com.v2ray.ang.payment.data.model.Order
import com.v2ray.ang.payment.data.model.Plan
import com.v2ray.ang.payment.data.repository.PaymentRepository
import com.v2ray.ang.payment.data.api.WalletSecretBackupPublicAddressRequest
import com.v2ray.ang.payment.data.api.WalletSecretBackupUpsertRequest
import com.v2ray.ang.payment.data.api.WalletTransferProxyBroadcastRequest
import com.v2ray.ang.payment.wallet.WalletMnemonicGenerator
import com.v2ray.ang.payment.wallet.WalletKeyManager
import com.v2ray.ang.payment.wallet.WalletSecretStore
import com.v2ray.ang.handler.MmkvManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

class RealCryptoVpnRepository(context: Context) : CryptoVpnRepository {
    private val paymentRepository = PaymentRepository(context.applicationContext)
    private val walletSecretStore = WalletSecretStore(context.applicationContext)
    private val walletKeyManager = WalletKeyManager(walletSecretStore)

    override suspend fun getForceUpdateState(): ForceUpdateUiState {
        return ForceUpdateUiState(
            metrics = listOf(
                FeatureMetric("当前版本", BuildConfig.VERSION_NAME),
                FeatureMetric("分发渠道", BuildConfig.DISTRIBUTION),
                FeatureMetric("更新服务", "未接入"),
            ),
            note = "当前仅展示已安装版本信息；强制升级检查接口尚未接入。",
        )
    }

    override suspend fun getOptionalUpdateState(): OptionalUpdateUiState {
        return OptionalUpdateUiState(
            metrics = listOf(
                FeatureMetric("当前版本", BuildConfig.VERSION_NAME),
                FeatureMetric("分发渠道", BuildConfig.DISTRIBUTION),
                FeatureMetric("更新服务", "未接入"),
            ),
            note = "当前仅展示已安装版本信息；可选升级检查接口尚未接入。",
        )
    }

    override suspend fun getEmailRegisterState(): EmailRegisterUiState {
        val cached = paymentRepository.getCachedCurrentUser()
        return EmailRegisterUiState(
            fields = listOf(
                FeatureField("email", "邮箱", cached?.email ?: "", "将作为登录与找回凭据"),
                FeatureField("code", "验证码", "", "先点击发送验证码，再填写收到的 6 位验证码"),
                FeatureField("password", "登录密码", "", "至少 8 位，建议混合字母和数字"),
                FeatureField("invite", "邀请码", "", "选填，注册成功后会尝试真实绑定"),
            ),
            summary = "完成 注册和邀请码绑定。",
            note = " 只有成功后才进入主界面。",
        )
    }

    override suspend fun getResetPasswordState(): ResetPasswordUiState {
        val cached = paymentRepository.getCachedCurrentUser()
        return ResetPasswordUiState(
            fields = listOf(
                FeatureField("email", "邮箱", cached?.email ?: "", "会向该邮箱发送验证码"),
                FeatureField("code", "验证码", "", "先发送验证码，再填写收到的 6 位验证码"),
                FeatureField("password", "新密码", "", "重置后立即使用新密码登录"),
                FeatureField("confirm", "确认密码", "", "保持两次输入一致"),
            ),
            summary = "通过重置恢复当前账号访问权限。",
            note = "密码重置确认成功后返回登录页。",
        )
    }

    override suspend fun requestEmailRegisterCode(email: String): EmailRegisterActionResult = withContext(Dispatchers.IO) {
        val normalizedEmail = email.trim()
        if (normalizedEmail.isBlank()) {
            return@withContext EmailRegisterActionResult(
                success = false,
                errorMessage = "请先填写邮箱地址",
            )
        }
        val result = paymentRepository.requestRegisterCode(normalizedEmail)
        if (result.isSuccess) {
            EmailRegisterActionResult(
                success = true,
                successMessage = "验证码已发送。",
            )
        } else {
            val message = result.exceptionOrNull()?.message ?: "发送验证码失败"
            EmailRegisterActionResult(
                success = false,
                errorMessage = message,
                unavailable = true,
            )
        }
    }

    override suspend fun registerEmail(
        email: String,
        password: String,
        code: String,
        inviteCode: String,
    ): EmailRegisterActionResult = withContext(Dispatchers.IO) {
        val normalizedEmail = email.trim()
        val normalizedCode = code.trim()
        val normalizedInvite = inviteCode.trim()
        if (normalizedEmail.isBlank() || normalizedCode.isBlank() || password.isBlank()) {
            return@withContext EmailRegisterActionResult(
                success = false,
                errorMessage = "邮箱、验证码和密码都必须填写",
            )
        }

        val registerResult = paymentRepository.register(
            email = normalizedEmail,
            password = password,
            code = normalizedCode,
        )
        if (registerResult.isFailure) {
            return@withContext EmailRegisterActionResult(
                success = false,
                errorMessage = registerResult.exceptionOrNull()?.message ?: "注册失败",
                unavailable = true,
            )
        }

        val successMessage = when {
            normalizedInvite.isNotBlank() -> {
                val bindResult = paymentRepository.bindReferralCode(normalizedInvite)
                if (bindResult.isSuccess) {
                    "账户已创建，并已绑定邀请码。"
                } else {
                    "账户已创建，但邀请码绑定失败：${bindResult.exceptionOrNull()?.message ?: "未知错误"}"
                }
            }
            else -> {
                val bindPendingResult = paymentRepository.tryBindPendingReferralCode()
                if (bindPendingResult.getOrDefault(false)) {
                    "账户已创建，并已自动绑定邀请关系。"
                } else if (bindPendingResult.isFailure) {
                    "账户已创建，但邀请码绑定失败：${bindPendingResult.exceptionOrNull()?.message ?: "未知错误"}"
                } else {
                    "账户已创建并已登录。"
                }
            }
        }

        EmailRegisterActionResult(
            success = true,
            successMessage = successMessage,
            completed = true,
            nextRoute = CryptoVpnRouteSpec.walletOnboarding.pattern,
        )
    }

    override suspend fun requestResetPasswordCode(email: String): ResetPasswordActionResult = withContext(Dispatchers.IO) {
        val normalizedEmail = email.trim()
        if (normalizedEmail.isBlank()) {
            return@withContext ResetPasswordActionResult(
                success = false,
                errorMessage = "请先填写邮箱地址",
            )
        }
        try {
            val response = paymentRepository.api.requestPasswordResetCode(
                PasswordResetCodeRequest(email = normalizedEmail),
            )
            if (response.isSuccessful && response.body()?.code == "OK") {
                ResetPasswordActionResult(
                    success = true,
                    successMessage = "重置验证码已发送，请检查邮箱。",
                )
            } else {
                ResetPasswordActionResult(
                    success = false,
                    errorMessage = response.body()?.message ?: "发送重置验证码失败",
                    unavailable = true,
                )
            }
        } catch (e: Exception) {
            ResetPasswordActionResult(
                success = false,
                errorMessage = e.message ?: "发送重置验证码失败",
                unavailable = true,
            )
        }
    }

    override suspend fun resetPassword(
        email: String,
        code: String,
        password: String,
    ): ResetPasswordActionResult = withContext(Dispatchers.IO) {
        val normalizedEmail = email.trim()
        val normalizedCode = code.trim()
        if (normalizedEmail.isBlank() || normalizedCode.isBlank() || password.isBlank()) {
            return@withContext ResetPasswordActionResult(
                success = false,
                errorMessage = "邮箱、验证码和新密码都必须填写",
            )
        }
        try {
            val response = paymentRepository.api.resetPassword(
                idempotencyKey = UUID.randomUUID().toString(),
                request = PasswordResetRequest(
                    email = normalizedEmail,
                    code = normalizedCode,
                    password = password,
                ),
            )
            if (response.isSuccessful && response.body()?.code == "OK") {
                ResetPasswordActionResult(
                    success = true,
                    successMessage = "密码已重置，请使用新密码重新登录。",
                    completed = true,
                )
            } else {
                ResetPasswordActionResult(
                    success = false,
                    errorMessage = response.body()?.message ?: "密码重置失败",
                    unavailable = true,
                )
            }
        } catch (e: Exception) {
            ResetPasswordActionResult(
                success = false,
                errorMessage = e.message ?: "密码重置失败",
                unavailable = true,
            )
        }
    }

    override suspend fun getPlansState(): PlansUiState {
        val plansResult = paymentRepository.getPlans()
        val plans = plansResult.getOrNull()
        if (plansResult.isFailure) {
            val failureMessage = plansResult.exceptionOrNull()?.message ?: "套餐服务不可用"
            return PlansUiState(
                summary = "套餐页无法加载。",
                screenState = P1ScreenState(
                    errorMessage = failureMessage,
                    unavailableMessage = if (failureMessage.contains("未登录")) {
                        "请登录。"
                    } else {
                        null
                    },
                ),
                note = " 错误 ",
            )
        }
        if (plans.isNullOrEmpty()) {
            return PlansUiState(
                summary = "当前没有可售的套餐。",
                screenState = P1ScreenState(emptyMessage = "套餐接口返回空。"),
                note = " 错误 ",
            )
        }

        val sortedPlans = plans.sortedBy { it.displayOrder }
        val currentPlanStatus = resolveCurrentPlanStatus(
            subscription = currentSubscription(),
            plans = sortedPlans,
            cachedOrders = loadCachedOrders(),
        )
        return PlansUiState(
            summary = "套餐接口拉取 ${sortedPlans.size} 个计划。",
            screenState = P1ScreenState(),
            plans = sortedPlans.map {
                PlanOptionUi(
                    planCode = it.planCode,
                    title = normalizePlanDisplayName(it.name),
                    description = it.description ?: it.regionAccessPolicy,
                    priceText = "$${it.priceUsd}",
                    durationText = it.getDurationDisplay(),
                    maxSessionsText = "${it.maxActiveSessions} 台设备",
                    badge = it.badge,
                    paymentMethods = buildList {
                        if (it.supportsUsdtTrc20()) add("USDT-TRON")
                        add("USDT-SOL")
                        if (it.supportsSol()) add("SOL")
                    },
                )
            },
            selectedPlanCode = sortedPlans.firstOrNull()?.planCode,
            currentPlanName = currentPlanStatus.planName,
            currentPlanDescription = currentPlanStatus.planDescription,
            currentPlanStatusText = currentPlanStatus.statusText,
            note = "套餐数据来自 PaymentRepository.getPlans()。",
        )
    }

    override suspend fun getRegionSelectionState(): RegionSelectionUiState {
        val vpnStatus = paymentRepository.getVpnStatus().getOrNull()
        val syncResult = paymentRepository.syncVpnNodesFromServer(force = false)
        val cached = buildRegionSelectionStateFromCache(vpnStatus)
        return if (syncResult.isSuccess) {
            (buildRegionSelectionStateFromCache(vpnStatus) ?: cached ?: RegionSelectionUiState(
                summary = "当前没有可用节点。",
                screenState = P1ScreenState(emptyMessage = "服务器当前没有返回任何节点。"),
            )).copy(
                note = "节点数据来自本地缓存，并已按服务器结果覆盖同步。",
            )
        } else {
            val failureMessage = syncResult.exceptionOrNull()?.message ?: "节点服务不可用"
            cached?.copy(
                screenState = P1ScreenState(errorMessage = failureMessage),
                note = "远端同步失败。",
            ) ?: RegionSelectionUiState(
                summary = "区域选择页无法完成加载。",
                screenState = P1ScreenState(
                    errorMessage = failureMessage,
                    unavailableMessage = if (failureMessage.contains("未登录")) {
                        "登录后才能加载节点。"
                    } else {
                        null
                    },
                ),
                note = "当前无本地缓存也无法从服务器同步。",
            )
        }
    }

    override suspend fun getCachedRegionSelectionState(): RegionSelectionUiState? {
        return buildRegionSelectionStateFromCache(paymentRepository.getVpnStatus().getOrNull())
    }

    override suspend fun selectVpnNode(lineCode: String, nodeId: String): RegionSelectionUiState {
        val selection = paymentRepository.selectVpnNode(lineCode, nodeId)
        if (selection.isFailure) {
            return getRegionSelectionState().copy(
                screenState = P1ScreenState(
                    errorMessage = selection.exceptionOrNull()?.message ?: "保存节点选择失败",
                ),
            )
        }

        paymentRepository.getVpnNodes(lineCode).getOrNull()
            ?.firstOrNull { it.nodeId == nodeId }
            ?.let { paymentRepository.selectLocalServerForNode(it) }

        return getRegionSelectionState().copy(selectionApplied = true)
    }

    private suspend fun buildRegionSelectionStateFromCache(
        vpnStatus: com.v2ray.ang.payment.data.api.VpnStatusData?,
    ): RegionSelectionUiState? {
        val userId = paymentRepository.getCurrentUserId() ?: return null
        val cachedNodes = paymentRepository.getCachedVpnNodes(userId = userId)
        if (cachedNodes.isEmpty()) {
            return null
        }
        val runtimes = paymentRepository.getCachedVpnNodeRuntime(userId)
            .associateBy { it.nodeId }
        val selectedNodeId =
            vpnStatus?.selectedNodeId ?: runtimes.values.firstOrNull { it.selected }?.nodeId
        val selectedLineCode =
            vpnStatus?.selectedLineCode ?: runtimes.values.firstOrNull { it.selected }?.lineCode
        val regions = cachedNodes.map { cache ->
            val runtime = runtimes[cache.nodeId]
            cache.toRegionOption(runtime, selectedNodeId)
        }
        return RegionSelectionUiState(
            summary = "已从本地缓存加载 ${regions.size} 个节点。",
            screenState = P1ScreenState(),
            regions = regions,
            selectedRegionCode = vpnStatus?.selectedRegionCode ?: regions.firstOrNull()?.regionCode,
            selectedLineCode = selectedLineCode ?: regions.firstOrNull()?.lineCode,
            selectedNodeId = selectedNodeId ?: regions.firstOrNull()?.nodeId,
            note = "节点目录先读本地缓存，再由服务器覆盖同步。",
        )
    }

    override suspend fun prepareOrderCheckoutState(args: OrderCheckoutRouteArgs): OrderCheckoutUiState {
        return buildOrderCheckoutState(args, createOrder = false)
    }

    override suspend fun getOrderCheckoutState(args: OrderCheckoutRouteArgs): OrderCheckoutUiState {
        if (args.planId.isBlank()) {
            return OrderCheckoutUiState(
                summary = "无法创建订单。",
                screenState = P1ScreenState(errorMessage = "planId 不能为空"),
                note = "",
            )
        }
        return buildOrderCheckoutState(args, createOrder = true)
    }

    private suspend fun buildOrderCheckoutState(
        args: OrderCheckoutRouteArgs,
        createOrder: Boolean,
    ): OrderCheckoutUiState {
        val paymentOptions = paymentRepository.getWalletAssetCatalog().getOrNull()
            .orEmpty()
            .filter { it.orderPayable }
            .map {
                CheckoutPaymentOptionUi(
                    assetCode = it.assetCode,
                    networkCode = it.networkCode,
                    label = checkoutPaymentLabel(it.assetCode, it.networkCode),
                    selected = false,
                )
            }
            .distinctBy { "${it.assetCode}:${it.networkCode}" }
        val selectedOption = paymentOptions.firstOrNull {
            it.assetCode == args.assetCode && it.networkCode == args.networkCode
        } ?: paymentOptions.firstOrNull()
        val normalizedOptions = paymentOptions.map {
            it.copy(selected = selectedOption?.assetCode == it.assetCode && selectedOption.networkCode == it.networkCode)
        }
        val plan = findPlanByCode(args.planId)
        val selectedRegionCode = paymentRepository.getLastIssuedVpnRegionCode().orEmpty()
        val selectedRegionLabel = resolveCheckoutRegionLabel(selectedRegionCode)
        val cachedEmail = paymentRepository.getCachedCurrentUser()?.email

        val reusableOrder = selectedOption?.let {
            findReusableCheckoutOrder(
                planId = args.planId,
                assetCode = it.assetCode,
                networkCode = it.networkCode,
            )
        }
        if (reusableOrder != null) {
            return reusableOrder.toCheckoutUiState(
                paymentOptions = normalizedOptions,
                selectedRegionCode = selectedRegionCode,
                selectedRegionLabel = selectedRegionLabel,
                invoiceEmail = cachedEmail,
            )
        }

        if (!createOrder || selectedOption == null) {
            return OrderCheckoutUiState(
                summary = when {
                    normalizedOptions.isEmpty() -> "当前没有可创建订单的支付网络。"
                    selectedRegionLabel.isBlank() -> "当前没有可选节点区域，可先选择支付网络并创建订单，支付完成后再补选节点。"
                    else -> "请先确认节点区域和支付网络，再创建订单。"
                },
                screenState = if (normalizedOptions.isEmpty()) {
                    P1ScreenState(emptyMessage = "当前环境没有可创建订单的支付网络。")
                } else {
                    P1ScreenState()
                },
                planCode = args.planId,
                planTitle = plan?.name.orEmpty(),
                selectedRegionCode = selectedRegionCode,
                selectedRegionLabel = selectedRegionLabel,
                assetCode = selectedOption?.assetCode.orEmpty(),
                networkCode = selectedOption?.networkCode.orEmpty(),
                invoiceEmail = cachedEmail,
                paymentOptions = normalizedOptions,
                note = if (selectedRegionLabel.isBlank()) {
                    "未发现已选节点区域，允许先支付再补选节点。"
                } else {
                    "节点区域与支付网络确认后，再手动创建订单。"
                },
            )
        }

        val orderResult = resolveCheckoutOrder(
            planId = args.planId,
            assetCode = selectedOption.assetCode,
            networkCode = selectedOption.networkCode,
        )
        val order = orderResult.getOrNull()
        if (order != null) {
            return order.toCheckoutUiState(
                paymentOptions = normalizedOptions,
                selectedRegionCode = selectedRegionCode,
                selectedRegionLabel = selectedRegionLabel,
                invoiceEmail = cachedEmail,
            )
        }

        val errorMessage = orderResult.exceptionOrNull()?.message?.takeIf { it.isNotBlank() }
            ?: "订单服务未返回可用错误详情"
        val capabilityUnavailableMessage = checkoutCapabilityUnavailableMessage(errorMessage)
        val sessionUnavailableMessage = sessionUnavailableMessage(errorMessage)
        return OrderCheckoutUiState(
            summary = sessionUnavailableMessage
                ?: capabilityUnavailableMessage
                ?: "当前未能生成有效订单。",
            screenState = when {
                sessionUnavailableMessage != null -> P1ScreenState(unavailableMessage = sessionUnavailableMessage)
                capabilityUnavailableMessage != null -> P1ScreenState(unavailableMessage = capabilityUnavailableMessage)
                else -> P1ScreenState(errorMessage = errorMessage)
            },
            planCode = args.planId,
            planTitle = plan?.name.orEmpty(),
            selectedRegionCode = selectedRegionCode,
            selectedRegionLabel = selectedRegionLabel,
            assetCode = selectedOption.assetCode,
            networkCode = selectedOption.networkCode,
            invoiceEmail = cachedEmail,
            paymentOptions = normalizedOptions,
            note = sessionUnavailableMessage ?: capabilityUnavailableMessage ?: errorMessage,
        )
    }

    private suspend fun findReusableCheckoutOrder(
        planId: String,
        assetCode: String,
        networkCode: String,
    ): Order? {
        val currentOrderNo = paymentRepository.getCurrentOrderId() ?: return null
        val currentOrder = paymentRepository.getOrder(currentOrderNo).getOrNull() ?: return null
        return currentOrder.takeIf {
            it.planCode == planId &&
                it.status == PaymentConfig.OrderStatus.PENDING_PAYMENT &&
                it.quoteAssetCode == assetCode &&
                it.quoteNetworkCode == networkCode
        }
    }

    private fun resolveCheckoutRegionLabel(regionCode: String): String {
        val lineName = paymentRepository.getCachedVpnLineName().orEmpty()
        val nodeName = paymentRepository.getCachedVpnNodeName().orEmpty()
        return when {
            lineName.isNotBlank() && nodeName.isNotBlank() -> "$lineName / $nodeName"
            nodeName.isNotBlank() -> nodeName
            lineName.isNotBlank() -> lineName
            regionCode.isNotBlank() -> regionCode
            else -> ""
        }
    }

    private fun Order.toCheckoutUiState(
        paymentOptions: List<CheckoutPaymentOptionUi>,
        selectedRegionCode: String,
        selectedRegionLabel: String,
        invoiceEmail: String?,
    ): OrderCheckoutUiState {
        return OrderCheckoutUiState(
            summary = "订单已创建，请支付确认。",
            screenState = P1ScreenState(),
            planCode = planCode,
            planTitle = planName,
            selectedRegionCode = selectedRegionCode,
            selectedRegionLabel = selectedRegionLabel,
            orderNo = orderNo,
            orderStatus = status,
            assetCode = paymentTarget?.assetCode ?: quoteAssetCode,
            networkCode = paymentTarget?.networkCode ?: quoteNetworkCode,
            payableAmount = payment.amountCrypto,
            baseAmount = paymentTarget?.baseAmount ?: baseAmount,
            uniqueAmountDelta = paymentTarget?.uniqueAmountDelta ?: uniqueAmountDelta,
            collectionAddress = payment.receiveAddress,
            qrText = payment.qrText,
            expiresAt = paymentTarget?.expiresAt ?: expiresAt,
            invoiceEmail = invoiceEmail,
            serviceEnabled = paymentTarget?.serviceEnabled == true,
            paymentOptions = paymentOptions,
            note = "订单数据来自 PaymentRepository.createOrder()/getOrder()。",
        )
    }

    private fun checkoutCapabilityUnavailableMessage(message: String?): String? {
        if (message.isNullOrBlank()) {
            return null
        }
        return when {
            message.contains("Solana collection address is not configured", ignoreCase = true) ->
                "Solana 支付暂未就绪，请切换到 USDT.tron 或稍后再试。"
            else -> null
        }
    }

    private fun VpnNodeCacheEntity.toRegionOption(
        runtime: VpnNodeRuntimeEntity?,
        selectedNodeId: String?,
    ): RegionOptionUi {
        return RegionOptionUi(
            nodeId = nodeId,
            nodeName = nodeName,
            lineCode = lineCode,
            lineName = lineName,
            regionCode = regionCode,
            regionName = regionName,
            tier = lineCode.substringAfter('_', "STANDARD"),
            status = status,
            healthStatus = runtime?.healthStatus ?: "UNKNOWN",
            host = host,
            port = port,
            isAllowed = true,
            isSelected = selectedNodeId == nodeId,
            remark = remark,
        )
    }

    override suspend fun getWalletPaymentConfirmState(args: WalletPaymentConfirmRouteArgs): WalletPaymentConfirmUiState {
        val order = paymentRepository.getOrder(args.orderId).getOrNull()
        return if (order != null) {
            WalletPaymentConfirmUiState(
                summary = "支付确认页已绑定订单对象。",
                screenState = P1ScreenState(),
                orderNo = order.orderNo,
                planCode = order.planCode,
                planTitle = order.planName,
                status = order.status,
                statusText = order.statusText,
                assetCode = order.paymentTarget?.assetCode ?: order.quoteAssetCode,
                networkCode = order.paymentTarget?.networkCode ?: order.quoteNetworkCode,
                payableAmount = order.payment.amountCrypto,
                baseAmount = order.paymentTarget?.baseAmount ?: order.baseAmount,
                uniqueAmountDelta = order.paymentTarget?.uniqueAmountDelta ?: order.uniqueAmountDelta,
                collectionAddress = order.payment.receiveAddress,
                qrText = order.payment.qrText,
                expiresAt = order.paymentTarget?.expiresAt ?: order.expiresAt,
                txHash = order.payment.txHash,
                note = "订单",
            )
        } else {
            WalletPaymentConfirmUiState(
                summary = "当前未查询到支付确认单。",
                screenState = P1ScreenState(errorMessage = "未查询到订单 ${args.orderId}"),
                orderNo = args.orderId,
                note = "未查询到支付确认单",
            )
        }
    }

    override suspend fun getOrderResultState(args: OrderResultRouteArgs): OrderResultUiState {
        val order = paymentRepository.getOrder(args.orderId).getOrNull()
        return if (order != null) {
            OrderResultUiState(
                summary = "订单状态",
                screenState = P1ScreenState(),
                orderNo = order.orderNo,
                planCode = order.planCode,
                planTitle = order.planName,
                status = order.status,
                statusText = order.statusText,
                payableAmount = order.payment.amountCrypto,
                assetCode = order.paymentTarget?.assetCode ?: order.quoteAssetCode,
                networkCode = order.paymentTarget?.networkCode ?: order.quoteNetworkCode,
                txHash = order.payment.txHash,
                paymentMatchedAt = order.paymentMatchedAt,
                subscriptionUrl = order.subscriptionUrl,
                expiresAt = order.expiresAt,
                completedAt = order.completedAt,
                failureReason = order.failureReason,
                note = "使用 PaymentRepository.getOrder(orderId) 填充。",
            )
        } else {
            OrderResultUiState(
                summary = "当前未查询到订单。",
                screenState = P1ScreenState(errorMessage = "未查询到订单 ${args.orderId}"),
                orderNo = args.orderId,
                note = "当前未查询到订单。",
            )
        }
    }

    override suspend fun getOrderListState(): OrderListUiState {
        val orders = loadCachedOrders()
        if (orders.isEmpty()) {
            return OrderListUiState(
                summary = "当前账号暂无订单。",
                screenState = P1ScreenState(emptyMessage = "当前账号没有真实订单缓存。"),
                note = "当前未查询到订单",
            )
        }
        return OrderListUiState(
            summary = "订单列表。",
            screenState = P1ScreenState(),
            orders = orders.sortedByDescending { it.createdAt }.map {
                OrderListItemUi(
                    orderNo = it.orderNo,
                    planTitle = it.planName,
                    status = it.status,
                    statusText = it.toOrder().statusText,
                    amountText = "${it.amount} ${it.assetCode}",
                    createdAt = formatEpoch(it.createdAt),
                )
            },
            note = "使用 LocalPaymentRepository 中的订单缓存。",
        )
    }

    override suspend fun getOrderDetailState(args: OrderDetailRouteArgs): OrderDetailUiState {
        val order = paymentRepository.getOrder(args.orderId).getOrNull()
            ?: loadCachedOrders().firstOrNull { it.orderNo == args.orderId }?.toOrder()
        return if (order != null) {
            OrderDetailUiState(
                summary = "订单详情",
                screenState = P1ScreenState(),
                orderNo = order.orderNo,
                planCode = order.planCode,
                planTitle = order.planName,
                status = order.status,
                statusText = order.statusText,
                rows = listOf(
                    OrderDetailRowUi("订单号", order.orderNo),
                    OrderDetailRowUi("套餐", order.planName),
                    OrderDetailRowUi("金额", "${order.payment.amountCrypto} ${order.payment.assetCode}"),
                    OrderDetailRowUi("网络", order.paymentTarget?.networkCode ?: order.quoteNetworkCode),
                    OrderDetailRowUi("收款地址", order.payment.receiveAddress.ifBlank { "--" }),
                    OrderDetailRowUi("链上交易", order.payment.txHash ?: "--"),
                    OrderDetailRowUi("创建时间", order.createdAt),
                    OrderDetailRowUi("到期时间", order.expiresAt),
                    OrderDetailRowUi("订阅链接", order.subscriptionUrl ?: "待开通"),
                ),
                note = "订单详情优先使用 PaymentRepository.getOrder(orderNo)。",
            )
        } else {
            OrderDetailUiState(
                summary = "当前未查询到订单详情。",
                screenState = P1ScreenState(errorMessage = "未查询到订单 ${args.orderId}"),
                orderNo = args.orderId,
                note = "订单详情页未再回退到 Mock 仓库；保持真实订单空态。",
            )
        }
    }

    override suspend fun getWalletPaymentState(): WalletPaymentUiState {
        val currentOrder = paymentRepository.getCurrentOrderId()?.let { paymentRepository.getOrder(it).getOrNull() }
        val currentUser = paymentRepository.getCachedCurrentUser()
        return WalletPaymentUiState(
            metrics = listOf(
                FeatureMetric("可用余额", currentOrder?.quoteUsdAmount ?: "--"),
                FeatureMetric("默认网络", currentOrder?.quoteNetworkCode ?: "TRON"),
                FeatureMetric("预估手续费", currentOrder?.paymentTarget?.uniqueAmountDelta ?: "0"),
            ),
            fields = listOf(
                FeatureField("source", "扣款钱包", walletAddress(currentUser), "来自缓存信息"),
                FeatureField("memo", "支付备注", currentOrder?.orderNo ?: "", "订单号"),
            ),
            note = "钱包支付",
        )
    }

    override suspend fun getWalletLifecycleState(): Result<WalletLifecycleData> {
        return paymentRepository.getWalletLifecycle()
    }

    override suspend fun getAssetDetailState(args: AssetDetailRouteArgs): AssetDetailUiState {
        val normalizedNetworkCode = routeChainIdToNetworkCode(args.chainId)
        val overview = paymentRepository.getWalletOverview().getOrNull()
        val assetItem = overview?.assetItems?.firstOrNull {
            it.assetCode.equals(args.assetId, ignoreCase = true) &&
                it.networkCode.equals(normalizedNetworkCode, ignoreCase = true)
        } ?: overview?.assetItems?.firstOrNull {
            it.assetCode.equals(args.assetId, ignoreCase = true)
        }
        val relatedOrders = loadCachedOrders().filter {
            it.assetCode.equals(args.assetId, ignoreCase = true) &&
                it.networkCode.equals(normalizedNetworkCode, ignoreCase = true)
        }
        val totalPayableAmount = assetItem?.totalPayableAmount?.takeIf { it.isNotBlank() }
        val computedBalance = relatedOrders.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
        val balanceText = totalPayableAmount
            ?: if (computedBalance > 0) String.format(Locale.US, "%.6f", computedBalance) else "0"
        val latestOrderStatus = assetItem?.lastOrderStatus?.takeIf { it.isNotBlank() } ?: "--"
        val lastOrderAt = assetItem?.lastOrderAt?.takeIf { it.isNotBlank() } ?: "暂无记录"
        return AssetDetailUiState(
            metrics = listOf(
                FeatureMetric("资产", args.assetId),
                FeatureMetric("余额", "$balanceText ${assetItem?.assetCode ?: args.assetId}"),
                FeatureMetric("今日", latestOrderStatus),
            ),
            highlights = relatedOrders.take(3).map {
                FeatureListItem(it.planName, it.orderNo, "${it.amount} ${it.assetCode}", it.status)
            }.ifEmpty {
                listOf(
                    FeatureListItem("链路状态", "当前资产暂无最近交易记录", "", "EMPTY"),
                    FeatureListItem("最近订单", lastOrderAt, latestOrderStatus, "REAL"),
                    FeatureListItem("地址状态", "${assetItem?.publicAddressCount ?: 0} 个公开地址", "", "ADDR"),
                )
            },
            checklist = listOf(
                FeatureBullet("网络", displayChainLabel(assetItem?.networkCode ?: normalizedNetworkCode)),
                FeatureBullet("支付订单数", (assetItem?.orderCount ?: relatedOrders.size).toString()),
                FeatureBullet("公开地址数", (assetItem?.publicAddressCount ?: 0).toString()),
                FeatureBullet("数据源", if (assetItem != null) "wallet/overview" else "orders-cache"),
            ),
            note = if (assetItem != null) {
                ""
            } else {
                "当前未获取到该资产详情，保持真实空态。"
            },
            summary = if (assetItem != null) "" else "当前未获取到资产详情。",
        )
    }

    override suspend fun getCachedReceiveState(args: ReceiveRouteArgs): ReceiveUiState? {
        val context = paymentRepository.getCachedWalletReceiveContext(
            networkCode = routeChainIdToNetworkCode(args.chainId),
            assetCode = args.assetId.takeIf { it.isNotBlank() },
        ) ?: return null
        return buildReceiveUiState(context)
    }

    override suspend fun getReceiveState(args: ReceiveRouteArgs): ReceiveUiState {
        val lifecycle = paymentRepository.getWalletLifecycle().getOrNull()
        if (lifecycle?.receiveState == "NO_WALLET") {
            return ReceiveUiState(
                badge = "",
                summary = "未接入",
                metrics = listOf(
                    FeatureMetric("钱包状态", lifecycle.lifecycleStatus),
                    FeatureMetric("收款状态", lifecycle.receiveState),
                    FeatureMetric("地址数", lifecycle.configuredAddressCount.toString()),
                ),
                fields = listOf(
                    FeatureField("address", "收款地址", "--", "创建或导入钱包后才能继续"),
                ),
                note = "",
                canShare = false,
                shareText = "",
                redirectRoute = CryptoVpnRouteSpec.walletOnboarding.pattern,
            )
        }
        val context = paymentRepository.getWalletReceiveContext(
            networkCode = routeChainIdToNetworkCode(args.chainId),
            assetCode = args.assetId.takeIf { it.isNotBlank() },
        ).getOrNull()
        if (context != null) {
            return buildReceiveUiState(context)
        }

        val user = paymentRepository.getCachedCurrentUser()
        return ReceiveUiState(
            metrics = listOf(
                FeatureMetric("默认链", args.chainId.uppercase(Locale.ROOT)),
                FeatureMetric("支持网络", "1"),
                FeatureMetric("校验状态", if (user != null) "已登录" else "未登录"),
            ),
            fields = listOf(
                FeatureField("label", "地址标签", walletAddress(user), "服务端不可用"),
            ),
            summary = "暂无数据",
            note = "",
            canShare = false,
            shareText = "",
            walletExists = false,
            receiveState = "NO_WALLET",
        )
    }

    private fun buildReceiveUiState(context: com.v2ray.ang.payment.data.api.WalletReceiveContextData): ReceiveUiState {
        return context.toReceiveUiState()
    }

    override suspend fun getSendState(args: SendRouteArgs): SendUiState {
        val overview = paymentRepository.getWalletOverview().getOrNull()
        val currentOrderId = paymentRepository.getCurrentOrderId()
        if (overview != null) {
            return overview.toSendUiState(args, currentOrderId)
        }
        val networkLabel = displayChainLabel(routeChainIdToNetworkCode(args.chainId))
        return SendUiState(
            badge = "",
            subtitle = "",
            summary = "",
            availableBalance = "--",
            balanceSupportingText = "",
            fields = buildList {
                add(FeatureField("to", "收款地址", "", "粘贴或扫码 $networkLabel 地址"))
                add(FeatureField("amount", "发送数量", "", "输入数量"))
                currentOrderId
                    ?.takeIf { it.isNotBlank() }
                    ?.let { add(FeatureField("memo", "备注", it, "订单号 / 对账")) }
            },
            highlights = emptyList(),
            checklist = emptyList(),
            currentRoute = args,
            note = "",
        )
    }

    override suspend fun submitSend(
        args: SendRouteArgs,
        toAddress: String,
        amount: String,
        memo: String,
    ): SendSubmissionResult {
        val accountId = paymentRepository.getCurrentUserId()
            ?: return SendSubmissionResult(success = false, errorMessage = "未登录")
        val normalizedNetworkCode = sendChainIdToNetworkCode(args.chainId)
        val fromAddress = when (normalizedNetworkCode) {
            "SOLANA" -> walletKeyManager.deriveAddresses(accountId).solanaAddress
            else -> walletKeyManager.deriveAddresses(accountId).tronAddress
        }
        val precheck = paymentRepository.precheckWalletTransfer(
            networkCode = normalizedNetworkCode,
            assetCode = args.assetId,
            toAddress = toAddress,
            amount = amount,
            orderNo = memo.takeIf { it.isNotBlank() },
        ).getOrElse { error ->
            return SendSubmissionResult(success = false, errorMessage = error.message ?: "预检查失败")
        }
        val build = paymentRepository.buildWalletTransfer(
            networkCode = normalizedNetworkCode,
            assetCode = args.assetId,
            fromAddress = fromAddress,
            toAddress = precheck.toAddressNormalized,
            amount = amount,
            orderNo = memo.takeIf { it.isNotBlank() },
        ).getOrElse { error ->
            return SendSubmissionResult(success = false, errorMessage = error.message ?: "构建转账失败")
        }

        val signature = when (build.networkCode) {
            "SOLANA" -> walletKeyManager.signSolanaMessage(accountId, build.signingPayload)
            "TRON" -> walletKeyManager.signTronTransactionId(accountId, build.signingPayload)
            else -> return SendSubmissionResult(success = false, errorMessage = "暂不支持该链发送")
        }

        val broadcast = paymentRepository.proxyBroadcastWalletTransfer(
            WalletTransferProxyBroadcastRequest(
                networkCode = build.networkCode,
                assetCode = build.assetCode,
                toAddress = build.toAddress,
                unsignedPayload = build.unsignedPayload,
                signature = signature,
            ),
        ).getOrElse { error ->
            return SendSubmissionResult(success = false, errorMessage = error.message ?: "广播失败")
        }

        return SendSubmissionResult(
            success = broadcast.broadcasted,
            txHash = broadcast.txHash,
            errorMessage = if (broadcast.broadcasted) null else (broadcast.note ?: "广播失败"),
        )
    }

    override suspend fun getSendResultState(args: SendResultRouteArgs): SendResultUiState {
        val currentOrder = paymentRepository.getCurrentOrderId()?.let { paymentRepository.getOrder(it).getOrNull() }
        return SendResultUiState(
            metrics = listOf(
                FeatureMetric("交易状态", currentOrder?.statusText ?: "已提交"),
                FeatureMetric("Tx Hash", args.txId),
                FeatureMetric("手续费", currentOrder?.paymentTarget?.uniqueAmountDelta ?: "0"),
            ),
            summary = "",
            note = "",
        )
    }

    override suspend fun getInviteCenterState(): InviteCenterUiState {
        val overview = paymentRepository.getReferralOverview().getOrNull()
        val share = paymentRepository.getReferralShareContext().getOrNull()
        return if (overview != null) {
            val referralCode = share?.referralCode ?: overview.referralCode
            val shareLink = normalizeInviteShareLink(share?.shareLink.orEmpty())
            val shareMessage = normalizeInviteShareMessage(
                share?.shareMessage?.takeIf { it.isNotBlank() } ?: shareLink,
            )
            InviteCenterUiState(
                inviteCode = referralCode,
                shareLink = shareLink,
                shareMessage = shareMessage,
                metrics = listOf(
                    FeatureMetric("累计佣金", "$${overview.availableAmountUsdt}"),
                    FeatureMetric("邀请人数", (overview.level1InviteCount + overview.level2InviteCount).toString()),
                    FeatureMetric("转化率", if (overview.level1InviteCount > 0) "${overview.level2InviteCount * 100 / overview.level1InviteCount}%" else "0%"),
                ),
                highlights = listOf(
                    FeatureListItem(referralCode, "", "", "LIVE"),
                    FeatureListItem("一级邀请", overview.level1InviteCount.toString(), overview.level1IncomeUsdt, "L1"),
                    FeatureListItem("二级邀请", overview.level2InviteCount.toString(), overview.level2IncomeUsdt, "L2"),
                ),
                summary = "",
                note = "",
            )
        } else {
            InviteCenterUiState(
                inviteCode = share?.referralCode ?: "--",
                shareLink = share?.shareLink.orEmpty(),
                shareMessage = share?.shareMessage?.takeIf { it.isNotBlank() } ?: share?.shareLink.orEmpty(),
                metrics = listOf(
                    FeatureMetric("邀请人数", "0"),
                    FeatureMetric("累计佣金", "0"),
                    FeatureMetric("数据源", "PaymentRepository"),
                ),
                highlights = emptyList(),
                summary = "暂无数据",
                note = "",
            )
        }
    }

    override suspend fun getInviteShareState(): InviteShareUiState {
        val share = paymentRepository.getReferralShareContext().getOrNull()
        return if (share != null) {
            InviteShareUiState(
                metrics = listOf(
                    FeatureMetric("链接", normalizeInviteShareLink(share.shareLink)),
                    FeatureMetric("邀请码", share.referralCode),
                    FeatureMetric("渠道", "系统分享"),
                ),
                highlights = listOf(
                    FeatureListItem("推广链接", normalizeInviteShareLink(share.shareLink), "复制", "LIVE"),
                    FeatureListItem("邀请码", share.referralCode, "复制", "CODE"),
                    FeatureListItem("可用佣金", "${share.availableAmountUsdt} USDT", "冻结 ${share.frozenAmountUsdt}", "BAL"),
                ),
                summary = "",
                note = "",
            )
        } else {
            InviteShareUiState(
                metrics = listOf(
                    FeatureMetric("链接", "--"),
                    FeatureMetric("邀请码", "--"),
                    FeatureMetric("渠道", "系统分享"),
                ),
                highlights = emptyList(),
                summary = "暂无数据",
                note = "",
            )
        }
    }

    private fun normalizeInviteShareLink(link: String): String {
        if (link.isBlank()) return link
        return link.replace(
            "https://vpn.residential-agent.com/invite",
            "https://api.residential-agent.com/invite",
        )
    }

    private fun normalizeInviteShareMessage(message: String): String {
        if (message.isBlank()) return message
        return message.replace(
            "https://vpn.residential-agent.com/invite",
            "https://api.residential-agent.com/invite",
        )
    }

    override suspend fun getCommissionLedgerState(): CommissionLedgerUiState {
        val summary = paymentRepository.getCommissionSummary().getOrNull()
        val ledger = paymentRepository.getCommissionLedger().getOrNull()?.items.orEmpty()
        return if (summary != null) {
            CommissionLedgerUiState(
                metrics = ledgerMetrics(summary),
                highlights = ledger.take(4).map {
                    FeatureListItem(
                        title = it.sourceOrderNo,
                        subtitle = it.sourceAccountMasked,
                        trailing = "${it.settlementAmountUsdt} ${summary.settlementAssetCode}",
                        badge = it.status,
                    )
                },
                summary = "",
                note = "",
            )
        } else {
            CommissionLedgerUiState(
                metrics = listOf(
                    FeatureMetric("可用佣金", "0"),
                    FeatureMetric("流水条数", ledger.size.toString()),
                    FeatureMetric("数据源", "PaymentRepository"),
                ),
                highlights = emptyList(),
                summary = "暂无数据",
                note = "",
            )
        }
    }

    override suspend fun getWithdrawState(): WithdrawUiState {
        val summary = paymentRepository.getCommissionSummary().getOrNull()
        val lastWithdrawal = paymentRepository.getWithdrawals().getOrNull()?.items?.firstOrNull()
        return if (summary != null) {
            WithdrawUiState(
                metrics = listOf(
                    FeatureMetric("可提佣金", "${summary.availableAmount} ${summary.settlementAssetCode}"),
                    FeatureMetric("最小提现", "50 ${summary.settlementAssetCode}"),
                    FeatureMetric("网络", summary.settlementNetworkCode),
                ),
                fields = listOf(
                    FeatureField("address", "提现地址", lastWithdrawal?.payoutAddress ?: "", "最近一次提现地址或留空"),
                    FeatureField("amount", "提现金额", summary.availableAmount, "默认取当前可提现金额"),
                ),
                highlights = listOf(
                    FeatureListItem("冻结金额", summary.frozenAmount, "", "FROZEN"),
                    FeatureListItem("提现中", summary.withdrawingAmount, "", "PENDING"),
                    FeatureListItem("累计提现", summary.withdrawnTotal, "", "DONE"),
                ),
                summary = "",
                note = "",
            )
        } else {
            WithdrawUiState(
                metrics = listOf(
                    FeatureMetric("可提佣金", "0"),
                    FeatureMetric("最近记录", lastWithdrawal?.requestNo ?: "--"),
                    FeatureMetric("数据源", "PaymentRepository"),
                ),
                highlights = emptyList(),
                summary = "暂无数据",
                note = "",
            )
        }
    }

    override suspend fun getProfileState(): ProfileUiState {
        val user = paymentRepository.getCachedCurrentUser()
        val me = paymentRepository.getMe().getOrNull()
        val orders = user?.userId?.let { paymentRepository.getCachedOrders(it) }.orEmpty()
        return ProfileUiState(
            metrics = listOf(
                FeatureMetric("当前套餐", me?.subscription?.planName ?: me?.subscription?.planCode ?: "未订阅"),
                FeatureMetric("设备数量", me?.subscription?.maxActiveSessions?.toString() ?: "0"),
                FeatureMetric("安全评分", if (paymentRepository.isTokenValid()) "A" else "C"),
            ),
            highlights = listOf(
                FeatureListItem("账户", user?.username ?: me?.email ?: "--", user?.userId ?: me?.accountId ?: "--", "LIVE"),
                FeatureListItem("订单数", orders.size.toString(), orders.firstOrNull()?.orderNo ?: "", "ORDER"),
                FeatureListItem("订阅到期", me?.subscription?.expireAt ?: "--", me?.status ?: "--", "SUB"),
            ),
            summary = "",
            note = "",
        )
    }

    override suspend fun getLegalDocumentsState(): LegalDocumentsUiState {
        val docs = localLegalDocs()
        return LegalDocumentsUiState(
            metrics = listOf(
                FeatureMetric("文档总数", docs.size.toString()),
                FeatureMetric("最近更新", docs.maxOfOrNull { it.lastUpdated } ?: "--"),
                FeatureMetric("来源", "本地资源/配置"),
            ),
            fields = emptyList(),
            highlights = docs.take(4).map {
                FeatureListItem(it.title, it.description, it.lastUpdated, "DOC")
            },
            summary = "法务文档页已切换到本地资源与配置驱动的数据源。",
            note = "文档入口来自 AppConfig 与本地应用资源，不再写死在仓库方法内。",
        )
    }

    override suspend fun getAboutAppState(): AboutAppUiState {
        val cachedUser = paymentRepository.getCachedCurrentUser()
        val versionLabel = "v${BuildConfig.VERSION_NAME}"

        return AboutAppUiState(
            metrics = listOf(
                FeatureMetric("应用", "v2rayNG"),
                FeatureMetric("版本", versionLabel),
                FeatureMetric("渠道", BuildConfig.DISTRIBUTION),
            ),
            highlights = listOf(
                FeatureListItem("当前版本", "$versionLabel · ${BuildConfig.DISTRIBUTION}", "最新", "LIVE"),
                FeatureListItem("源码仓库", AppConfig.APP_URL, "查看", "SRC"),
                FeatureListItem("帮助与支持", AppConfig.APP_ISSUES_URL, "进入", "SUPPORT"),
            ),
            note = cachedUser?.let { "当前账号：${it.username}" } ?: "当前未缓存登录账号。",
        )
    }

    override suspend fun getLegalDocumentDetailState(args: LegalDocumentDetailRouteArgs): LegalDocumentDetailUiState {
        val doc = localLegalDocs().firstOrNull {
            it.id == args.documentId || normalizeDocId(it.id) == args.documentId
        }
        return if (doc != null) {
            LegalDocumentDetailUiState(
                title = doc.title,
                summary = doc.description,
                metrics = listOf(
                    FeatureMetric("文档版本", doc.lastUpdated),
                    FeatureMetric("文档标识", doc.id),
                    FeatureMetric("来源", "本地资源/配置"),
                ),
                highlights = listOf(
                    FeatureListItem("文档标识", doc.id, doc.lastUpdated, "DOC"),
                    FeatureListItem("说明", doc.content, "", "TEXT"),
                    FeatureListItem("来源链接", doc.link, "打开外部原文", "LINK"),
                ),
                note = "法务详情已切到本地资源与配置驱动的数据源。",
            )
        } else {
            LegalDocumentDetailUiState(
                title = "文档不存在",
                summary = "未找到请求的法务文档。",
                metrics = listOf(
                    FeatureMetric("文档标识", args.documentId),
                    FeatureMetric("状态", "未找到"),
                    FeatureMetric("数据源", "本地资源/配置"),
                ),
                highlights = emptyList(),
                note = "法务详情页未再回退到 Mock 仓库；保持真实空态。",
            )
        }
    }

    override suspend fun getSubscriptionDetailState(args: SubscriptionDetailRouteArgs): SubscriptionDetailUiState {
        val subscription = currentSubscription()
        val planCode = subscription?.planCode
        val plan = if (planCode != null) findPlanByCode(planCode) else null
        val displayId = subscription?.subscriptionId ?: args.subscriptionId
        val expireAt = subscription?.expireAt ?: "--"
        val daysRemaining = subscription?.daysRemaining?.let { "$it 天" } ?: "未知"

        return SubscriptionDetailUiState(
            metrics = listOf(
                FeatureMetric("当前计划", plan?.name ?: subscription?.planName ?: subscription?.planCode ?: "未订阅"),
                FeatureMetric("剩余时间", daysRemaining),
                FeatureMetric("自动续费", "未接入"),
            ),
            highlights = listOf(
                FeatureListItem("订阅标识", displayId, subscription?.status ?: "NONE", "LIVE"),
                FeatureListItem("到期时间", expireAt, subscription?.planName ?: subscription?.planCode ?: "--", "SUB"),
                FeatureListItem("并发设备", subscription?.maxActiveSessions?.toString() ?: "0", "", "DEVICE"),
            ),
            summary = if (subscription != null) "" else "暂无数据",
            note = "",
        )
    }

    override suspend fun getExpiryReminderState(args: ExpiryReminderRouteArgs): ExpiryReminderUiState {
        val subscription = currentSubscription()
        val planCode = subscription?.planCode
        val plan = if (planCode != null) findPlanByCode(planCode) else null
        val daysLeft = subscription?.daysRemaining?.toString() ?: args.daysLeft
        val renewAmount = plan?.priceUsd?.let { "US$$it" } ?: "待定"

        return ExpiryReminderUiState(
            metrics = listOf(
                FeatureMetric("剩余天数", daysLeft),
                FeatureMetric("续费金额", renewAmount),
                FeatureMetric("自动续费", "未接入"),
            ),
            highlights = listOf(
                FeatureListItem("当前计划", plan?.name ?: subscription?.planName ?: subscription?.planCode ?: "未订阅", daysLeft, "LIVE"),
                FeatureListItem("到期时间", subscription?.expireAt ?: "--", subscription?.status ?: "NONE", "SUB"),
                FeatureListItem("数据源", "真实订阅上下文", "", "REAL"),
            ),
            summary = if (subscription != null) "" else "暂无数据",
            note = "",
        )
    }

    override suspend fun getNodeSpeedTestState(args: NodeSpeedTestRouteArgs): NodeSpeedTestUiState {
        val nodes = localServerSnapshots()
        val bestLatency = nodes.mapNotNull { it.latencyMs }.minOrNull()?.let { "$it ms" } ?: "--"

        return NodeSpeedTestUiState(
            metrics = listOf(
                FeatureMetric("已测速", nodes.count { it.latencyMs != null }.toString()),
                FeatureMetric("最佳延迟", bestLatency),
                FeatureMetric("节点分组", args.nodeGroupId),
            ),
            highlights = nodes.map {
                FeatureListItem(
                    title = it.displayName,
                    subtitle = it.description,
                    trailing = it.latencyMs?.let { latency -> "$latency ms" } ?: "--",
                    badge = it.protocol,
                )
            },
            summary = "",
            note = "未接入",
        )
    }

    override suspend fun getAutoConnectRulesState(): AutoConnectRulesUiState {
        val tokenValid = paymentRepository.isTokenValid()
        val orders = loadCachedOrders()

        return AutoConnectRulesUiState(
            metrics = listOf(
                FeatureMetric("规则数量", if (tokenValid) "1" else "0"),
                FeatureMetric("会话状态", if (tokenValid) "有效" else "未登录"),
                FeatureMetric("订单记录", orders.size.toString()),
            ),
            fields = listOf(
                FeatureField(
                    key = "rule",
                    label = "默认规则",
                    value = if (tokenValid) "不安全网络自动提醒" else "",
                    supportingText = "当前仅展示本机规则状态，未与服务端规则集同步。",
                ),
            ),
            highlights = listOf(
                FeatureListItem("默认策略", if (tokenValid) "当前仅记录本机规则状态" else "未配置", "", "LOCAL"),
                FeatureListItem("会话状态", if (tokenValid) "已登录" else "需登录后配置", "", "SESSION"),
                FeatureListItem("数据源", "设备本机状态", "", "LOCAL"),
            ),
            note = "未接入",
        )
    }

    override suspend fun getCreateWalletState(args: CreateWalletRouteArgs): CreateWalletUiState {
        val user = paymentRepository.getCachedCurrentUser()
        val lifecycle = paymentRepository.getWalletLifecycle().getOrNull()
        val defaultName = user?.username?.let { "$it Wallet" } ?: "Primary Wallet"

        return CreateWalletUiState(
            metrics = listOf(
                FeatureMetric("模式", args.mode),
                FeatureMetric("账户状态", if (user != null) "已绑定" else "未绑定"),
                FeatureMetric("数据源", "wallet/lifecycle"),
            ),
            fields = listOf(
                FeatureField(
                    key = "name",
                    label = "钱包名称",
                    value = lifecycle?.displayName ?: defaultName,
                    supportingText = "创建动作会把钱包存在态写入服务端生命周期状态。",
                ),
            ),
            highlights = listOf(
                FeatureListItem("创建模式", args.mode, lifecycle?.lifecycleStatus ?: "NOT_CREATED", "LIVE"),
                FeatureListItem("账户标签", user?.username ?: "--", user?.userId ?: "--", "ACCOUNT"),
                FeatureListItem("数据源", "wallet/lifecycle", "", "REAL"),
            ),
            note = "",
        )
    }

    override suspend fun createWallet(displayName: String): WalletLifecycleMutationResult {
        val accountId = paymentRepository.getCurrentUserId()
            ?: return WalletLifecycleMutationResult(
                success = false,
                errorMessage = "未登录",
            )
        val mnemonic = WalletMnemonicGenerator.generate12WordMnemonic()
        val normalizedMnemonic = mnemonic.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        val normalizedMnemonicText = normalizedMnemonic.joinToString(" ")
        val mnemonicHash = createMnemonicHash(normalizedMnemonicText)
        val result = paymentRepository.upsertWalletLifecycle(
            action = "CREATE",
            displayName = displayName,
            mnemonicHash = mnemonicHash,
            mnemonicWordCount = normalizedMnemonic.size,
        )
        val lifecycle = result.getOrNull()
        return if (lifecycle?.walletExists == true) {
            val timestamp = Instant.now().toString()
            walletSecretStore.upsertMnemonic(
                accountId = accountId,
                walletId = lifecycle.walletId.orEmpty(),
                mnemonic = normalizedMnemonicText,
                mnemonicHash = mnemonicHash,
                mnemonicWordCount = normalizedMnemonic.size,
                sourceType = "CREATE",
                timestampIso = timestamp,
            )
            val publicAddresses = syncDerivedPublicAddresses(accountId)
            uploadWalletSecretBackup(
                walletId = lifecycle.walletId.orEmpty(),
                mnemonic = normalizedMnemonicText,
                mnemonicHash = mnemonicHash,
                mnemonicWordCount = normalizedMnemonic.size,
                walletName = lifecycle.displayName ?: displayName,
                sourceType = "CREATE",
                publicAddresses = publicAddresses,
            )
            WalletLifecycleMutationResult(success = true, walletId = lifecycle.walletId)
        } else {
            WalletLifecycleMutationResult(
                success = false,
                errorMessage = result.exceptionOrNull()?.message ?: "创建钱包失败",
            )
        }
    }

    override suspend fun acknowledgeWalletBackup(): Result<WalletLifecycleData> {
        return paymentRepository.upsertWalletLifecycle(action = "ACKNOWLEDGE_BACKUP")
    }

    override suspend fun confirmWalletBackup(): Result<WalletLifecycleData> {
        return paymentRepository.upsertWalletLifecycle(action = "CONFIRM_BACKUP")
    }

    override suspend fun getImportWalletMethodState(): ImportWalletMethodUiState {
        val user = paymentRepository.getCachedCurrentUser()
        val orders = loadCachedOrders()
        val lifecycle = paymentRepository.getWalletLifecycle().getOrNull()
        return ImportWalletMethodUiState(
            metrics = listOf(
                FeatureMetric("当前账户", if (user != null) "已登录" else "未登录"),
                FeatureMetric("订单记录", orders.size.toString()),
                FeatureMetric("恢复入口", "助记词"),
            ),
            highlights = listOf(
                FeatureListItem("默认方式", "优先使用助记词恢复多链钱包", "助记词", "LIVE"),
                FeatureListItem("账户标签", user?.username ?: "--", user?.userId ?: "--", "ACCOUNT"),
                FeatureListItem("当前钱包", lifecycle?.displayName ?: "未创建", lifecycle?.lifecycleStatus ?: "NOT_CREATED", "REAL"),
            ),
            note = "",
        )
    }

    override suspend fun getImportMnemonicState(args: ImportMnemonicRouteArgs): ImportMnemonicUiState {
        val user = paymentRepository.getCachedCurrentUser()
        return ImportMnemonicUiState(
            metrics = listOf(
                FeatureMetric("导入来源", args.source),
                FeatureMetric("账户状态", if (user != null) "已登录" else "未登录"),
                FeatureMetric("恢复能力", "wallet/lifecycle"),
            ),
            fields = listOf(
                FeatureField(
                    key = "mnemonic",
                    label = "助记词",
                    value = "",
                    supportingText = "请输入 12 或 24 个单词，使用空格分隔。",
                ),
                FeatureField(
                    key = "walletName",
                    label = "恢复后钱包名",
                    value = user?.username?.let { "$it Wallet" } ?: "Imported Wallet",
                    supportingText = "恢复后将作为默认钱包名称展示。",
                ),
            ),
            highlights = listOf(
                FeatureListItem("恢复来源", args.source, "服务状态", "REAL"),
                FeatureListItem("账户标签", user?.username ?: "--", user?.userId ?: "--", "ACCOUNT"),
                FeatureListItem("数据来源", "wallet/lifecycle", "", "REAL"),
            ),
            note = "",
        )
    }

    override suspend fun importWalletFromMnemonic(
        source: String,
        mnemonic: String,
        walletName: String,
    ): WalletLifecycleMutationResult {
        val normalizedMnemonic = mnemonic.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        if (normalizedMnemonic.size !in setOf(12, 24)) {
            return WalletLifecycleMutationResult(
                success = false,
                errorMessage = "助记词词数必须是 12 或 24 个",
            )
        }
        val accountId = paymentRepository.getCurrentUserId()
            ?: return WalletLifecycleMutationResult(
                success = false,
                errorMessage = "未登录",
            )
        val normalizedMnemonicText = normalizedMnemonic.joinToString(" ")
        val mnemonicHash = createMnemonicHash(normalizedMnemonicText)
        val result = paymentRepository.upsertWalletLifecycle(
            action = "IMPORT",
            displayName = walletName.ifBlank { "Imported Wallet" },
            mnemonicHash = mnemonicHash,
            mnemonicWordCount = normalizedMnemonic.size,
        )
        val lifecycle = result.getOrNull()
        return if (lifecycle?.walletExists == true) {
            val timestamp = Instant.now().toString()
            walletSecretStore.upsertMnemonic(
                accountId = accountId,
                walletId = lifecycle.walletId.orEmpty(),
                mnemonic = normalizedMnemonicText,
                mnemonicHash = mnemonicHash,
                mnemonicWordCount = normalizedMnemonic.size,
                sourceType = "IMPORT",
                timestampIso = timestamp,
            )
            val publicAddresses = syncDerivedPublicAddresses(accountId)
            uploadWalletSecretBackup(
                walletId = lifecycle.walletId.orEmpty(),
                mnemonic = normalizedMnemonicText,
                mnemonicHash = mnemonicHash,
                mnemonicWordCount = normalizedMnemonic.size,
                walletName = lifecycle.displayName ?: walletName,
                sourceType = source.ifBlank { "IMPORT" },
                publicAddresses = publicAddresses,
            )
            WalletLifecycleMutationResult(success = true, walletId = lifecycle.walletId)
        } else {
            WalletLifecycleMutationResult(
                success = false,
                errorMessage = result.exceptionOrNull()?.message ?: "导入钱包失败",
            )
        }
    }

    override suspend fun getImportPrivateKeyState(args: ImportPrivateKeyRouteArgs): ImportPrivateKeyUiState =
        ImportPrivateKeyUiState(
            metrics = listOf(
                FeatureMetric("目标链", args.chainId),
                FeatureMetric("校验状态", "BLOCKED"),
                FeatureMetric("导入模式", "私钥"),
            ),
            note = "未接入",
        )

    override suspend fun getBackupMnemonicState(args: BackupMnemonicRouteArgs): BackupMnemonicUiState {
        val user = paymentRepository.getCachedCurrentUser()
        val lifecycle = paymentRepository.getWalletLifecycle().getOrNull()
        val localSecret = paymentRepository.getCurrentUserId()?.let { walletSecretStore.getMnemonicRecord(it) }
        val mnemonicWords = localSecret?.mnemonic?.split(Regex("\\s+"))?.filter { it.isNotBlank() }.orEmpty()
        return BackupMnemonicUiState(
            metrics = listOf(
                FeatureMetric("词数", mnemonicWords.size.takeIf { it > 0 }?.toString() ?: "待返回"),
                FeatureMetric("账户状态", if (user != null) "已绑定" else "未绑定"),
                FeatureMetric("当前阶段", lifecycle?.status ?: lifecycle?.lifecycleStatus ?: "CREATED_PENDING_BACKUP"),
            ),
            fields = mnemonicWords.chunked(3).mapIndexed { index, chunk ->
                FeatureField(
                    key = "mnemonic_row_$index",
                    label = "助记词 ${index + 1}",
                    value = chunk.joinToString("  "),
                    supportingText = "请线下抄写并妥善保存",
                )
            },
            highlights = listOf(
                FeatureListItem("钱包标识", args.walletId, lifecycle?.walletName ?: lifecycle?.displayName ?: "Primary Wallet", "REAL"),
                FeatureListItem("账户标签", user?.username ?: "--", user?.userId ?: "--", "ACCOUNT"),
                FeatureListItem("数据来源", if (mnemonicWords.isNotEmpty()) "device-keystore" else "wallet/lifecycle", "", "REAL"),
            ),
            note = if (mnemonicWords.isNotEmpty()) "助记词已保存在本机加密仓库；请完成线下备份。" else "",
        )
    }

    override suspend fun getConfirmMnemonicState(args: ConfirmMnemonicRouteArgs): ConfirmMnemonicUiState =
        paymentRepository.getWalletLifecycle().getOrNull().let { lifecycle ->
            ConfirmMnemonicUiState(
                metrics = listOf(
                    FeatureMetric("钱包标识", args.walletId),
                    FeatureMetric("当前阶段", lifecycle?.nextAction ?: "CONFIRM_MNEMONIC"),
                    FeatureMetric("状态", lifecycle?.status ?: lifecycle?.lifecycleStatus ?: "BACKUP_PENDING_CONFIRMATION"),
                ),
                highlights = listOf(
                    FeatureListItem("钱包名称", lifecycle?.walletName ?: lifecycle?.displayName ?: "Primary Wallet", "", "REAL"),
                    FeatureListItem("生命周期", lifecycle?.status ?: lifecycle?.lifecycleStatus ?: "BACKUP_PENDING_CONFIRMATION", lifecycle?.origin ?: "", "STATE"),
                ),
                note = "",
            )
        }

    override suspend fun getSecurityCenterState(): SecurityCenterUiState {
        val user = paymentRepository.getCachedCurrentUser()
        val orders = loadCachedOrders()
        return SecurityCenterUiState(
            metrics = listOf(
                FeatureMetric("会话状态", if (paymentRepository.isTokenValid()) "有效" else "失效"),
                FeatureMetric("账户", user?.username ?: "--"),
                FeatureMetric("订单记录", orders.size.toString()),
            ),
            highlights = listOf(
                FeatureListItem("助记词备份", "本地安全流程已启用", "检查", "SAFE"),
                FeatureListItem("设备会话", if (paymentRepository.isTokenValid()) "当前登录有效" else "需要重登", "", "SESSION"),
                FeatureListItem("数据来源", "真实账户 + 本地状态", "", "REAL"),
            ),
            note = "",
        )
    }

    override suspend fun getChainManagerState(args: ChainManagerRouteArgs): ChainManagerUiState =
        paymentRepository.getWalletOverview().getOrNull().let { overview ->
            ChainManagerUiState(
                metrics = listOf(
                    FeatureMetric("钱包标识", args.walletId),
                    FeatureMetric("启用链", overview?.chainItems?.size?.toString() ?: "0"),
                    FeatureMetric("默认链", overview?.selectedNetworkCode ?: "--"),
                ),
                highlights = overview?.chainItems.orEmpty().map {
                    FeatureListItem(
                        title = it.displayName,
                        subtitle = it.networkCode,
                        trailing = if (it.hasConfiguredAddress == true) "已配置地址" else "待配置地址",
                        badge = "REAL",
                    )
                },
                note = if (overview != null) "" else "暂无数据",
            )
        }

    override suspend fun getAddCustomTokenState(args: AddCustomTokenRouteArgs): AddCustomTokenUiState =
        AddCustomTokenUiState(
            metrics = listOf(
                FeatureMetric("目标链", args.chainId),
                FeatureMetric("录入模式", "手动"),
                FeatureMetric("校验", "BLOCKED"),
            ),
            note = "未接入",
        )

    override suspend fun getWalletManagerState(args: WalletManagerRouteArgs): WalletManagerUiState {
        val user = paymentRepository.getCachedCurrentUser()
        val orders = loadCachedOrders()
        val lifecycle = paymentRepository.getWalletLifecycle().getOrNull()

        return WalletManagerUiState(
            metrics = listOf(
                FeatureMetric("钱包数量", if (lifecycle?.walletExists == true) "1" else "0"),
                FeatureMetric("默认钱包", lifecycle?.walletName ?: lifecycle?.displayName ?: args.walletId),
                FeatureMetric("关联订单", orders.size.toString()),
            ),
            highlights = listOf(
                FeatureListItem("默认钱包", lifecycle?.walletName ?: lifecycle?.displayName ?: args.walletId, lifecycle?.lifecycleStatus ?: "NOT_CREATED", "REAL"),
                FeatureListItem("账户标签", user?.username ?: "--", user?.userId ?: "--", "ACCOUNT"),
                FeatureListItem("数据源", "wallet/lifecycle", lifecycle?.sourceType ?: lifecycle?.origin ?: "--", "REAL"),
            ),
            summary = if (lifecycle?.walletExists == true) "" else "暂无数据",
            note = "",
        )
    }

    override suspend fun getAddressBookState(args: AddressBookRouteArgs): AddressBookUiState =
        AddressBookUiState(
            metrics = listOf(
                FeatureMetric("已保存", "0"),
                FeatureMetric("最近使用", "0"),
                FeatureMetric("模式", args.mode),
            ),
            fields = listOf(
                FeatureField(
                    key = "name",
                    label = "联系人名称",
                    value = "",
                    supportingText = "当前地址簿尚未接入持久化数据。",
                ),
                FeatureField(
                    key = "address",
                    label = "钱包地址",
                    value = "",
                    supportingText = "保持明确空态，不再回退到 Mock 仓库。",
                ),
            ),
            highlights = listOf(
                FeatureListItem("地址簿状态", "暂无本地地址记录", args.mode, "EMPTY"),
                FeatureListItem("数据源", "等待地址簿持久化", "", "BLOCKED"),
            ),
            summary = "暂无数据",
            note = "未接入",
        )

    override suspend fun getGasSettingsState(args: GasSettingsRouteArgs): GasSettingsUiState =
        GasSettingsUiState(
            metrics = listOf(
                FeatureMetric("目标链", args.chainId),
                FeatureMetric("估算状态", "未接入"),
                FeatureMetric("数据源", "本地设置"),
            ),
            fields = listOf(
                FeatureField(
                    key = "maxFee",
                    label = "Max Fee",
                    value = "",
                    supportingText = "当前未接链上估算，保持空态。",
                ),
                FeatureField(
                    key = "priorityFee",
                    label = "Priority Fee",
                    value = "",
                    supportingText = "不再回退到 Mock 仓库。",
                ),
            ),
            highlights = listOf(
                FeatureListItem("Gas 状态", "等待真实链上估算", args.chainId, "PENDING"),
                FeatureListItem("数据源", "等待 gas estimator", "", "BLOCKED"),
            ),
            summary = "暂无数据",
            note = "未接入",
        )

    override suspend fun getSwapState(args: SwapRouteArgs): SwapUiState =
        SwapUiState(
            metrics = listOf(
                FeatureMetric("源资产", args.fromAsset),
                FeatureMetric("目标资产", args.toAsset),
                FeatureMetric("数据来源", "BLOCKED"),
            ),
            note = "未接入",
        )

    override suspend fun getBridgeState(args: BridgeRouteArgs): BridgeUiState =
        BridgeUiState(
            metrics = listOf(
                FeatureMetric("起始链", args.fromChainId),
                FeatureMetric("目标链", args.toChainId),
                FeatureMetric("状态", "BLOCKED"),
            ),
            note = "未接入",
        )

    override suspend fun getDappBrowserState(args: DappBrowserRouteArgs): DappBrowserUiState =
        DappBrowserUiState(
            metrics = listOf(
                FeatureMetric("入口", args.entry),
                FeatureMetric("会话状态", if (paymentRepository.isTokenValid()) "已认证" else "匿名"),
                FeatureMetric("安全模式", "BLOCKED"),
            ),
            note = "未接入",
        )

    override suspend fun getWalletConnectSessionState(args: WalletConnectSessionRouteArgs): WalletConnectSessionUiState =
        WalletConnectSessionUiState(
            metrics = listOf(
                FeatureMetric("会话标识", args.sessionId),
                FeatureMetric("认证状态", if (paymentRepository.isTokenValid()) "有效" else "失效"),
                FeatureMetric("数据来源", "BLOCKED"),
            ),
            note = "未接入",
        )

    override suspend fun getSignMessageConfirmState(args: SignMessageConfirmRouteArgs): SignMessageConfirmUiState =
        SignMessageConfirmUiState(
            metrics = listOf(
                FeatureMetric("请求标识", args.requestId),
                FeatureMetric("账户状态", if (paymentRepository.isTokenValid()) "已登录" else "未登录"),
                FeatureMetric("校验", "BLOCKED"),
            ),
            note = "未接入",
        )

    override suspend fun getRiskAuthorizationsState(): RiskAuthorizationsUiState =
        RiskAuthorizationsUiState(
            metrics = listOf(
                FeatureMetric("授权总数", "0"),
                FeatureMetric("高风险", "0"),
                FeatureMetric("会话状态", if (paymentRepository.isTokenValid()) "已登录" else "未登录"),
            ),
            highlights = listOf(
                FeatureListItem("授权状态", "当前未接入真实授权列表", "", "EMPTY"),
                FeatureListItem("数据源", "等待授权记录源", "", "BLOCKED"),
            ),
            summary = "暂无数据",
            note = "未接入",
        )

    override suspend fun getNftGalleryState(): NftGalleryUiState =
        NftGalleryUiState(
            metrics = listOf(
                FeatureMetric("收藏系列", "0"),
                FeatureMetric("地板价", "--"),
                FeatureMetric("在售数量", "0"),
            ),
            highlights = listOf(
                FeatureListItem("NFT 状态", "当前未接入真实 NFT 数据源", "", "EMPTY"),
                FeatureListItem("数据源", "等待 NFT 索引", "", "BLOCKED"),
            ),
            summary = "暂无数据",
            note = "未接入",
        )

    override suspend fun getStakingEarnState(): StakingEarnUiState =
        StakingEarnUiState(
            metrics = listOf(
                FeatureMetric("APR", "--"),
                FeatureMetric("已质押", "0"),
                FeatureMetric("待领取", "0"),
            ),
            highlights = listOf(
                FeatureListItem("质押状态", "当前未接入真实质押数据源", "", "EMPTY"),
                FeatureListItem("数据源", "等待质押域能力", "", "BLOCKED"),
            ),
            summary = "暂无数据",
            note = "未接入",
        )

    override suspend fun getSessionEvictedDialogState(): SessionEvictedDialogUiState {
        return SessionEvictedDialogUiState(
            title = if (paymentRepository.isTokenValid()) "会话安全提醒" else "会话已失效",
            message = if (paymentRepository.isTokenValid()) {
                "当前账号已登录，但建议定期校验设备与授权状态。"
            } else {
                "当前账号 access token 已失效，请重新登录后继续访问 VPN 与钱包能力。"
            },
        )
    }

    private suspend fun resolveCheckoutOrder(
        planId: String,
        assetCode: String,
        networkCode: String,
    ): Result<Order> {
        val currentOrderNo = paymentRepository.getCurrentOrderId()
        val currentOrderResult = currentOrderNo?.let { paymentRepository.getOrder(it) }
        val currentOrder = currentOrderResult?.getOrNull()
        if (currentOrder != null &&
            currentOrder.planCode == planId &&
            currentOrder.status == PaymentConfig.OrderStatus.PENDING_PAYMENT &&
            currentOrder.quoteAssetCode == assetCode &&
            currentOrder.quoteNetworkCode == networkCode
        ) {
            return Result.success(currentOrder)
        }
        return paymentRepository.createOrder(
            planId = planId,
            assetCode = assetCode,
            networkCode = networkCode,
        )
    }

    private suspend fun loadCachedOrders(): List<OrderEntity> {
        val userId = paymentRepository.getCurrentUserId() ?: return emptyList()
        return paymentRepository.getCachedOrders(userId)
    }

    private suspend fun currentSubscription(): CurrentSubscriptionData? {
        return paymentRepository.getSubscription().getOrNull()
            ?: paymentRepository.getMe().getOrNull()?.subscription
    }

    private suspend fun findPlanByCode(planCode: String): Plan? {
        return paymentRepository.getPlans().getOrNull()?.firstOrNull {
            it.planCode == planCode
        }
    }

    private fun ledgerMetrics(summary: CommissionSummaryData): List<FeatureMetric> = listOf(
        FeatureMetric("可用佣金", "${summary.availableAmount} ${summary.settlementAssetCode}"),
        FeatureMetric("待结算", "${summary.frozenAmount} ${summary.settlementAssetCode}"),
        FeatureMetric("已结算", "${summary.withdrawnTotal} ${summary.settlementAssetCode}"),
    )

    private fun displayChainLabel(networkCode: String): String = receiveDisplayChainLabel(networkCode)

    private fun createMnemonicHash(mnemonic: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(mnemonic.toByteArray(Charsets.UTF_8))
            .joinToString("") { byte -> "%02x".format(byte) }
    }

    private suspend fun uploadWalletSecretBackup(
        walletId: String,
        mnemonic: String,
        mnemonicHash: String,
        mnemonicWordCount: Int,
        walletName: String?,
        sourceType: String,
        publicAddresses: List<WalletSecretBackupPublicAddressRequest>,
    ) {
        paymentRepository.upsertWalletSecretBackup(
            WalletSecretBackupUpsertRequest(
                walletId = walletId.takeIf { it.isNotBlank() },
                mnemonic = mnemonic,
                mnemonicHash = mnemonicHash,
                mnemonicWordCount = mnemonicWordCount,
                walletName = walletName?.trim()?.takeIf { it.isNotBlank() },
                sourceType = sourceType,
                publicAddresses = publicAddresses,
            ),
        )
    }

    private suspend fun syncDerivedPublicAddresses(
        accountId: String,
    ): List<WalletSecretBackupPublicAddressRequest> {
        val addresses = walletKeyManager.deriveAddresses(accountId)
        val payload = listOf(
            WalletSecretBackupPublicAddressRequest(
                networkCode = "SOLANA",
                assetCode = "SOL",
                address = addresses.solanaAddress,
                isDefault = true,
            ),
            WalletSecretBackupPublicAddressRequest(
                networkCode = "SOLANA",
                assetCode = "USDT",
                address = addresses.solanaAddress,
                isDefault = true,
            ),
            WalletSecretBackupPublicAddressRequest(
                networkCode = "TRON",
                assetCode = "TRX",
                address = addresses.tronAddress,
                isDefault = true,
            ),
            WalletSecretBackupPublicAddressRequest(
                networkCode = "TRON",
                assetCode = "USDT",
                address = addresses.tronAddress,
                isDefault = true,
            ),
        )
        payload.forEach { item ->
            paymentRepository.upsertWalletPublicAddress(
                networkCode = item.networkCode,
                assetCode = item.assetCode,
                address = item.address,
                isDefault = item.isDefault,
            )
        }
        return payload
    }

    private fun sessionUnavailableMessage(message: String?): String? {
        if (message.isNullOrBlank()) {
            return null
        }
        return if (
            message.contains("session evicted", ignoreCase = true) ||
            message.contains("auth_session_evicted", ignoreCase = true) ||
            message.contains("401") ||
            message.contains("unauthorized", ignoreCase = true) ||
            message.contains("未授权") ||
            message.contains("未登录") ||
            message.contains("token 已过期")
        ) {
            "登录会话已失效，请重新登录。"
        } else {
            null
        }
    }

    private fun routeChainIdToNetworkCode(chainId: String): String = when (chainId.lowercase(Locale.ROOT)) {
        "sol", "solana" -> "SOLANA"
        else -> "TRON"
    }

    private fun walletAddress(user: UserEntity?): String {
        if (user == null) return "--"
        return "acct:${user.userId.take(6)}...${user.userId.takeLast(4)}"
    }

    private fun localServerSnapshots(): List<LocalServerSnapshot> {
        return MmkvManager.decodeAllServerList().mapNotNull { guid ->
            val profile = MmkvManager.decodeServerConfig(guid) ?: return@mapNotNull null
            val subscriptionRemarks = profile.subscriptionId
                .takeIf { it.isNotBlank() }
                ?.let { MmkvManager.decodeSubscription(it)?.remarks }
                .orEmpty()
            val latency = MmkvManager.decodeServerAffiliationInfo(guid)
                ?.testDelayMillis
                ?.takeIf { it > 0L }
                ?.toInt()
            val displayName = profile.remarks.ifBlank {
                subscriptionRemarks.ifBlank { profile.server ?: guid.take(8) }
            }
            val description = listOfNotNull(
                subscriptionRemarks.takeIf { it.isNotBlank() },
                profile.server,
                profile.serverPort?.let { "端口 $it" },
            ).joinToString(" · ").ifBlank { "本地节点配置" }

            LocalServerSnapshot(
                guid = guid,
                displayName = displayName,
                description = description,
                protocol = profile.configType.name,
                latencyMs = latency,
            )
        }.sortedWith(
            compareBy<LocalServerSnapshot> { it.latencyMs == null }
                .thenBy { it.latencyMs ?: Int.MAX_VALUE }
                .thenBy { it.displayName },
        )
    }

    private fun localLegalDocs(): List<RealLegalDoc> = listOf(
        RealLegalDoc(
            id = "terms_of_service",
            title = "服务协议",
            description = "应用与服务使用说明",
            lastUpdated = BuildConfig.VERSION_NAME,
            content = "通过项目主页查看最新服务说明与发行信息。",
            link = AppConfig.APP_URL,
        ),
        RealLegalDoc(
            id = "privacy_policy",
            title = "隐私政策",
            description = "查看当前隐私政策原文链接",
            lastUpdated = BuildConfig.VERSION_NAME,
            content = "隐私政策由应用配置提供，点击链接查看原文。",
            link = AppConfig.APP_PRIVACY_POLICY,
        ),
        RealLegalDoc(
            id = "vpn_service_notice",
            title = "VPN 服务说明",
            description = "节点、线路与模式说明",
            lastUpdated = BuildConfig.VERSION_NAME,
            content = "查看模式说明与线路使用指引。",
            link = AppConfig.APP_WIKI_MODE,
        ),
    )

    private fun normalizeDocId(id: String): String = when (id) {
        "terms" -> "terms_of_service"
        "privacy" -> "privacy_policy"
        else -> id
    }

    private fun OrderEntity.toOrder(): Order = Order(
        orderId = orderNo,
        orderNo = orderNo,
        planCode = planId,
        planName = planName,
        orderType = PaymentConfig.PurchaseType.NEW,
        quoteAssetCode = assetCode,
        quoteNetworkCode = networkCode.ifBlank {
            if (assetCode.equals(PaymentConfig.AssetCode.SOL, true)) {
                PaymentConfig.NetworkCode.SOLANA
            } else {
                PaymentConfig.NetworkCode.TRON
            }
        },
        quoteUsdAmount = usdAmount,
        payableAmount = amount,
        status = status,
        expiresAt = expiredAt?.let(::formatEpoch) ?: formatEpoch(createdAt),
        confirmedAt = paidAt?.let(::formatEpoch),
        completedAt = fulfilledAt?.let(::formatEpoch),
        createdAt = formatEpoch(createdAt),
        subscriptionUrl = subscriptionUrl,
    )

    private fun formatEpoch(epoch: Long): String =
        Instant.ofEpochMilli(epoch).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

    private val liveStatuses = setOf(
        PaymentConfig.OrderStatus.PAID_SUCCESS,
        PaymentConfig.OrderStatus.FULFILLED,
        PaymentConfig.OrderStatus.PENDING_PAYMENT,
    )
}

private data class RealLegalDoc(
    val id: String,
    val title: String,
    val description: String,
    val lastUpdated: String,
    val content: String,
    val link: String,
)

private data class LocalServerSnapshot(
    val guid: String,
    val displayName: String,
    val description: String,
    val protocol: String,
    val latencyMs: Int?,
)
