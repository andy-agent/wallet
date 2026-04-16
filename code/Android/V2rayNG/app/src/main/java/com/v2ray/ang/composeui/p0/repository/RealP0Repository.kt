package com.v2ray.ang.composeui.p0.repository

import android.content.Context
import com.v2ray.ang.BuildConfig
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.model.AssetHolding
import com.v2ray.ang.composeui.p0.model.LoginResult
import com.v2ray.ang.composeui.p0.model.LoginUiState
import com.v2ray.ang.composeui.p0.model.P0LoadState
import com.v2ray.ang.composeui.p0.model.RegionSpeed
import com.v2ray.ang.composeui.p0.model.resolveVpnOverviewValueText
import com.v2ray.ang.composeui.p0.model.SplashUiState
import com.v2ray.ang.composeui.p0.model.SubscriptionSummary
import com.v2ray.ang.composeui.p0.model.VpnConnectionStatus
import com.v2ray.ang.composeui.p0.model.VpnHomeUiState
import com.v2ray.ang.composeui.p0.model.WalletChainSummary
import com.v2ray.ang.composeui.p0.model.WalletHomeUiState
import com.v2ray.ang.composeui.p0.model.WalletOnboardingUiState
import com.v2ray.ang.composeui.p0.model.WatchSignal
import com.v2ray.ang.composeui.p0.model.hasUsableWallet
import com.v2ray.ang.composeui.p0.model.toWalletHomeUiState
import com.v2ray.ang.composeui.p0.model.walletHomeChainLabel
import com.v2ray.ang.handler.MmkvManager
import com.v2ray.ang.handler.V2RayServiceManager
import com.v2ray.ang.payment.PaymentConfig
import com.v2ray.ang.payment.data.api.CurrentSubscriptionData
import com.v2ray.ang.payment.data.api.MeData
import com.v2ray.ang.payment.data.api.VpnRegionItem
import com.v2ray.ang.payment.data.api.VpnStatusData
import com.v2ray.ang.payment.data.local.entity.UserEntity
import com.v2ray.ang.payment.data.local.entity.VpnNodeCacheEntity
import com.v2ray.ang.payment.data.local.entity.VpnNodeRuntimeEntity
import com.v2ray.ang.payment.data.model.LoginRequest
import com.v2ray.ang.payment.data.model.Order
import com.v2ray.ang.payment.data.repository.PaymentRepository
import com.v2ray.ang.payment.wallet.WalletKeyManager
import com.v2ray.ang.payment.wallet.WalletSecretStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class RealP0Repository(context: Context) : P0Repository {
    private val appContext = context.applicationContext
    private val paymentRepository = PaymentRepository(appContext)
    private val walletSecretStore = WalletSecretStore(appContext)
    private val walletKeyManager = WalletKeyManager(walletSecretStore)

    override suspend fun getCachedVpnHomeState(): VpnHomeUiState? = withContext(Dispatchers.IO) {
        val cachedUser = paymentRepository.getCachedCurrentUser() ?: return@withContext null
        val currentUserId = paymentRepository.getCurrentUserId() ?: return@withContext null
        val cachedNodeSnapshots = buildCachedNodeSnapshots(
            cachedNodes = paymentRepository.getCachedVpnNodes(userId = currentUserId),
            runtimes = paymentRepository.getCachedVpnNodeRuntime(userId = currentUserId),
            selectedNodeId = paymentRepository.getCachedVpnNodeId(),
        )
        val cachedOrders = readLocalOrders(currentUserId)
        val latestOrder = cachedOrders.maxByOrNull { it.createdAt }
        val cachedSubscription = paymentRepository.getCachedSubscriptionStatus()
        val cachedPlanCode = paymentRepository.getCachedSubscriptionPlanCode()
        val cachedDaysRemaining = paymentRepository.getCachedSubscriptionDaysRemaining()
        val cachedLineName = paymentRepository.getCachedVpnLineName()
        val cachedWalletOverview = paymentRepository.getCachedWalletOverview(currentUserId)
        val selectedNode = selectedCachedNode(cachedNodeSnapshots, paymentRepository.getCachedVpnNodeId())
        val hasLocalConfig = !MmkvManager.getSelectServer().isNullOrEmpty()
        val localSubscriptionUrl = latestOrder?.subscriptionUrl ?: paymentRepository.getSavedSubscriptionUrl()
        val cachedSubscriptionData = buildCachedSubscriptionData(
            planCode = cachedPlanCode,
            status = cachedSubscription,
            daysRemaining = cachedDaysRemaining,
            expireAt = paymentRepository.getLastIssuedVpnConfigExpireAt(),
            subscriptionUrl = localSubscriptionUrl,
            marzbanUsername = paymentRepository.getSavedMarzbanUsername(),
        )
        val cachedVpnStatusData = buildCachedVpnStatusData(
            subscriptionStatus = cachedSubscription,
            currentRegionCode = paymentRepository.getLastIssuedVpnRegionCode(),
            selectedLineCode = paymentRepository.getLastIssuedVpnRegionCode(),
            selectedLineName = cachedLineName,
            selectedNodeId = paymentRepository.getCachedVpnNodeId(),
            selectedNodeName = paymentRepository.getCachedVpnNodeName(),
            sessionStatus = paymentRepository.getCachedVpnSessionStatus(),
        )
        val signals = buildVpnSignals(
            subscription = cachedSubscriptionData,
            vpnStatus = cachedVpnStatusData,
            latestOrder = latestOrder,
            hasLocalConfig = hasLocalConfig,
            localNodes = cachedNodeSnapshots,
            localSubscriptionUrl = localSubscriptionUrl,
        )

        return@withContext VpnHomeUiState(
            isLoading = false,
            loadState = if (cachedNodeSnapshots.isNotEmpty() || cachedOrders.isNotEmpty() || !localSubscriptionUrl.isNullOrBlank()) {
                P0LoadState.READY
            } else {
                P0LoadState.EMPTY
            },
            connectionStatus = resolveConnectionStatus(cachedVpnStatusData, hasLocalConfig),
            accountLabel = cachedUser.email ?: cachedUser.username,
            selectedRegion = selectedNode?.toRegionSpeed()
                ?: cachedLineName?.let {
                    RegionSpeed(
                        regionName = it,
                        protocol = paymentRepository.getCachedVpnNodeName() ?: "节点信息未返回",
                        latencyMs = 0,
                        load = "--",
                    )
                }
                ?: RegionSpeed("未获取区域", "节点信息未返回", 0, "--"),
            subscription = cachedSubscriptionData?.toSummary() ?: localSubscriptionSummary(
                latestOrder = latestOrder,
                hasSavedSubscriptionUrl = !localSubscriptionUrl.isNullOrBlank(),
                fallbackExpireAt = paymentRepository.getLastIssuedVpnConfigExpireAt(),
            ),
            autoConnectEnabled = hasLocalConfig,
            oneTapLabel = if (hasLocalConfig) "启动已导入配置" else "导入配置后连接",
            speedNodes = cachedNodeSnapshots.map { it.toRegionSpeed() },
            watchSignals = signals,
            overviewValueText = resolveVpnOverviewValueText(
                walletOverview = cachedWalletOverview,
                ordersSyncUnavailable = false,
            ),
            overviewSummaryText = buildOverviewSummary(
                orders = cachedOrders,
                subscription = cachedSubscriptionData,
                fallbackExpireAt = paymentRepository.getLastIssuedVpnConfigExpireAt(),
                ordersSyncUnavailable = false,
            ),
            alertCount = signals.size,
            nodeHealthPercent = calculateNodeHealthPercent(
                localNodeCount = cachedNodeSnapshots.size,
                allowedRegions = emptyList(),
                cachedNodes = cachedNodeSnapshots,
            ),
            vlessExpiryLabel = resolveVlessExpiryLabel(
                subscription = cachedSubscriptionData,
                lastIssuedConfigExpireAt = paymentRepository.getLastIssuedVpnConfigExpireAt(),
            ),
            vlessRegionLabel = cachedVpnStatusData?.selectedLineName
                ?: selectedNode?.lineName
                ?: paymentRepository.getLastIssuedVpnRegionCode()
                ?: "待同步",
            configStatusLabel = localConfigStatusLabel(
                hasLocalConfig = hasLocalConfig,
                localSubscriptionUrl = localSubscriptionUrl,
                localNodeCount = cachedNodeSnapshots.size,
            ),
            latestOrderLabel = latestOrder?.let { "${it.planName} · ${it.statusText}" }
                ?: if (cachedNodeSnapshots.isNotEmpty()) {
                    "已识别 ${cachedNodeSnapshots.size} 个缓存节点"
                } else {
                    resolveOrdersFallbackLabel(false)
                },
            canConnect = hasLocalConfig,
        )
    }

    override suspend fun getSplashState(): SplashUiState = withContext(Dispatchers.IO) {
        val cachedUser = paymentRepository.getCachedCurrentUser()
        val currentUserId = paymentRepository.getCurrentUserId()
        val cachedOrders = currentUserId?.let { paymentRepository.getCachedOrders(it) }.orEmpty()
        val accessToken = paymentRepository.getAccessToken()
        val refreshToken = paymentRepository.getRefreshToken()
        val hasPersistedAccountContext = cachedUser != null
        val hasPersistedBusinessCache = cachedOrders.isNotEmpty()
        val hasTokenOnlyResidue =
            !currentUserId.isNullOrBlank() ||
                !accessToken.isNullOrBlank() ||
                !refreshToken.isNullOrBlank()
        val meResult = paymentRepository.getMe()

        if (meResult.isSuccess) {
            val me = meResult.getOrThrow()
            paymentRepository.warmSyncAfterLogin(
                userId = me.accountId,
                meSnapshot = me,
            ).getOrElse { error ->
                val failureMessage = error.message ?: "无法完成数据同步。"
                if (failureMessage.contains("未登录") ||
                    failureMessage.contains("UNAUTHORIZED", ignoreCase = true) ||
                    failureMessage.contains("AUTH_REFRESH_INVALID", ignoreCase = true) ||
                    failureMessage.contains("invalid", ignoreCase = true)
                ) {
                    paymentRepository.clearAuth()
                    return@withContext SplashUiState(
                        checkingSecureBoot = false,
                        versionLabel = "v${BuildConfig.VERSION_NAME}",
                        buildStatus = "登录态已失效",
                        progress = 1f,
                        progressHeadline = "进入登录",
                        progressDetail = "检测到本地登录态失效，返回登录页。",
                        authResolved = true,
                        readyToNavigate = true,
                        nextRoute = CryptoVpnRouteSpec.emailLogin.pattern,
                        loadState = P0LoadState.READY,
                        accountLabel = "未登录",
                        subscriptionLabel = "无可用数据",
                    )
                }
                return@withContext SplashUiState(
                    checkingSecureBoot = false,
                    versionLabel = "v${BuildConfig.VERSION_NAME}",
                    buildStatus = me.email,
                    progress = 0.74f,
                    progressHeadline = "账户同步中断",
                    progressDetail = failureMessage,
                    authResolved = false,
                    readyToNavigate = false,
                    loadState = P0LoadState.UNAVAILABLE,
                    accountLabel = me.email,
                    subscriptionLabel = "同步未完成",
                    errorMessage = failureMessage,
                    unavailableMessage = "正在加载订单、订阅、节点和配置同步。",
                )
            }
            val subscription = paymentRepository.getSubscription().getOrNull() ?: me.subscription
            val hasActiveSubscription = subscription?.status.equals("ACTIVE", ignoreCase = true)
            val regionCount = if (hasActiveSubscription) {
                paymentRepository.getVpnRegions().getOrNull()?.count { it.isAllowed } ?: 0
            } else {
                0
            }
            return@withContext SplashUiState(
                checkingSecureBoot = false,
                versionLabel = "v${BuildConfig.VERSION_NAME}",
                buildStatus = if (hasActiveSubscription) {
                    "${me.email} · ${subscription?.status ?: "NONE"} · $regionCount 个可用区域"
                } else {
                    "${me.email} · ${subscription?.status ?: "NONE"}"
                },
                progress = 1f,
                progressHeadline = "准备完成",
                progressDetail = if (hasActiveSubscription) {
                    "账号、订阅和 VPN 区域已同步，正在进入主界面。"
                } else {
                    "已完成账户同步，正在进入主界面。"
                },
                authResolved = true,
                readyToNavigate = true,
                nextRoute = resolvePostAuthRoute(),
                loadState = P0LoadState.READY,
                accountLabel = me.email,
                subscriptionLabel = subscription?.planName ?: subscription?.planCode ?: "暂无有效订阅",
            )
        }

        val failureMessage = meResult.exceptionOrNull()?.message ?: "未识别登录会话"
        if (hasPersistedAccountContext || hasPersistedBusinessCache) {
            return@withContext SplashUiState(
                checkingSecureBoot = false,
                versionLabel = "v${BuildConfig.VERSION_NAME}",
                buildStatus = cachedUser?.email
                    ?: cachedUser?.username
                    ?: currentUserId
                    ?: "已读取缓存账户",
                progress = 1f,
                progressHeadline = "继续进入应用",
                progressDetail = "检测到缓存账户和订单数据，正在进入应用。",
                authResolved = true,
                readyToNavigate = true,
                nextRoute = resolvePostAuthRoute(),
                loadState = P0LoadState.READY,
                accountLabel = cachedUser?.email ?: cachedUser?.username ?: "缓存账户",
                subscriptionLabel = if (cachedOrders.isEmpty()) {
                    "已读取缓存账户数据"
                } else {
                    "${cachedOrders.size} 笔缓存订单"
                },
            )
        }

        if (failureMessage.contains("未登录") || failureMessage.contains("Token 已过期")) {
            return@withContext SplashUiState(
                checkingSecureBoot = false,
                versionLabel = "v${BuildConfig.VERSION_NAME}",
                buildStatus = cachedUser?.email ?: "未识别登录会话",
                progress = 1f,
                progressHeadline = "进入登录",
                progressDetail = "未检测到有效信息，将进入 登录页。",
                authResolved = true,
                readyToNavigate = true,
                nextRoute = CryptoVpnRouteSpec.emailLogin.pattern,
                loadState = P0LoadState.READY,
                accountLabel = cachedUser?.email ?: "未登录",
                subscriptionLabel = if (cachedOrders.isEmpty()) "暂无订单" else "${cachedOrders.size} 笔订单",
            )
        }

        if (hasTokenOnlyResidue) {
            paymentRepository.clearAuth()
            return@withContext SplashUiState(
                checkingSecureBoot = false,
                versionLabel = "v${BuildConfig.VERSION_NAME}",
                buildStatus = "已清理残留会话",
                progress = 1f,
                progressHeadline = "进入登录",
                progressDetail = "检测到失效的数据，清理并进入登录页。",
                authResolved = true,
                readyToNavigate = true,
                nextRoute = CryptoVpnRouteSpec.emailLogin.pattern,
                loadState = P0LoadState.READY,
                accountLabel = "未登录",
                subscriptionLabel = "无本地数据",
            )
        }

        SplashUiState(
            checkingSecureBoot = false,
            versionLabel = "v${BuildConfig.VERSION_NAME}",
            buildStatus = cachedUser?.email ?: "账户同步失败",
            progress = 0.58f,
            progressHeadline = "账户服务不可用",
            progressDetail = "无法完成账号同步，请重试后再进入应用。",
            authResolved = false,
            readyToNavigate = false,
            loadState = P0LoadState.UNAVAILABLE,
            accountLabel = cachedUser?.email ?: "未知账号",
            subscriptionLabel = if (cachedOrders.isEmpty()) "未同步订阅" else "${cachedOrders.size} 笔订单",
            errorMessage = failureMessage,
            unavailableMessage = "当前网络或后端不可用。",
        )
    }

    override suspend fun getLoginSeed(): LoginUiState {
        val cachedUser = paymentRepository.getCachedCurrentUser()
        val currentUserId = paymentRepository.getCurrentUserId()
        val cachedOrders = currentUserId?.let { paymentRepository.getCachedOrders(it) }.orEmpty()
        return LoginUiState(
            email = cachedUser?.email ?: cachedUser?.username.orEmpty(),
            rememberMe = paymentRepository.isTokenValid(),
            helperText = if (cachedUser != null) {
                "当前设备已识别账号 ${cachedUser.email ?: cachedUser.username}，正在刷新订阅、VPN 状态和订单。"
            } else {
                "账号登录后，同步订阅、VPN 区域和支付记录。"
            },
            successMessage = if (cachedOrders.isNotEmpty()) "检测到 ${cachedOrders.size} 笔订单。" else null,
        )
    }

    override suspend fun login(email: String, password: String): LoginResult {
        val normalizedEmail = email.trim()
        if (normalizedEmail.isBlank() || password.isBlank()) {
            return LoginResult(
                success = false,
                errorMessage = "邮箱和密码都必须填写",
            )
        }
        return withContext(Dispatchers.IO) {
            try {
                val response = paymentRepository.api.login(
                    LoginRequest(
                        email = normalizedEmail,
                        password = password,
                        installationId = paymentRepository.getDeviceId(),
                    ),
                )
                val auth = response.body()?.data
                if (response.isSuccessful && response.body()?.code == "OK" && auth != null) {
                    val localWallet = walletSecretStore.getConflictingMnemonicRecord(auth.userId)
                    if (localWallet != null) {
                        return@withContext LoginResult(
                            success = false,
                            errorMessage = "当前设备钱包已绑定其他账号；请使用原账号登录或先手动清除本地钱包。",
                        )
                    }
                    paymentRepository.saveAuthResponse(auth)
                    paymentRepository.saveCurrentUserId(auth.userId)
                    paymentRepository.getLocalRepository().saveUser(
                        com.v2ray.ang.payment.data.local.entity.UserEntity(
                            userId = auth.userId,
                            username = normalizedEmail,
                            email = normalizedEmail,
                            accessToken = auth.accessToken,
                            refreshToken = auth.refreshToken,
                            loginAt = System.currentTimeMillis(),
                        ),
                    )
                    val meResult = paymentRepository.getMe()
                    val warmSyncResult = paymentRepository.warmSyncAfterLogin(
                        userId = meResult.getOrNull()?.accountId ?: auth.userId,
                        meSnapshot = meResult.getOrNull(),
                    )
                    warmSyncResult.fold(
                        onSuccess = {
                            paymentRepository.tryBindPendingReferralCode()
                            LoginResult(
                                success = true,
                                nextRoute = resolvePostAuthRoute(),
                            )
                        },
                        onFailure = { error ->
                            LoginResult(
                                success = false,
                                errorMessage = error.message ?: "登录后同步失败",
                                unavailable = true,
                            )
                        },
                    )
                } else {
                    LoginResult(
                        success = false,
                        errorMessage = mapLoginFailureMessage(
                            response.body()?.message,
                            response.body()?.code,
                        ),
                    )
                }
            } catch (e: Exception) {
                LoginResult(
                    success = false,
                    errorMessage = mapLoginFailureMessage(e.message, null),
                    unavailable = true,
                )
            }
        }
    }

    override suspend fun getWalletOnboardingState(): WalletOnboardingUiState {
        val cachedUser = paymentRepository.getCachedCurrentUser()
        val me = paymentRepository.getMe().getOrNull()
        val subscription = paymentRepository.getSubscription().getOrNull() ?: me?.subscription
        val lifecycle = paymentRepository.getWalletLifecycle().getOrNull()
        val hasUsableWallet = lifecycle?.walletExists == true &&
            !lifecycle.sourceType.equals("LEGACY", ignoreCase = true)
        val accountLabel = me?.email ?: cachedUser?.email ?: cachedUser?.username ?: "未登录"
        if (me == null && cachedUser == null) {
            return WalletOnboardingUiState(
                accountLabel = accountLabel,
                summary = "",
                unavailableMessage = "当前没有可用的账号",
                focusedChains = listOf("SOLANA", "TRON"),
            )
        }

        return WalletOnboardingUiState(
            accountLabel = accountLabel,
            summary = if (hasUsableWallet) {
                ""
            } else if (subscription?.status == "ACTIVE") {
                ""
            } else {
                ""
            },
            warningMessage = if (lifecycle?.walletExists == true && !hasUsableWallet) {
                "检测到历史公开地址记录，仍需完成钱包创建或导入流程。"
            } else if (hasUsableWallet) {
                "检测到服务端已有钱包状态；再次导入会覆盖当前默认钱包入口。"
            } else {
                "已同步钱包状态。"
            },
            focusedChains = listOf("SOLANA", "TRON"),
            primaryActionLabel = if (hasUsableWallet) "进入钱包总览" else "继续到钱包入口",
            walletExists = hasUsableWallet,
            lifecycleStatus = lifecycle?.lifecycleStatus ?: "NOT_CREATED",
            walletId = lifecycle?.walletId,
            walletDisplayName = lifecycle?.displayName,
            walletNextAction = lifecycle?.nextAction ?: "CREATE_OR_IMPORT",
        )
    }

    override suspend fun getVpnHomeState(): VpnHomeUiState {
        val cachedUser = paymentRepository.getCachedCurrentUser()
        val currentUserId = paymentRepository.getCurrentUserId()
        val syncedWalletAddresses = syncDerivedWalletAddressesIfAvailable(currentUserId)
        var cachedNodeSnapshots = currentUserId?.let { userId ->
            buildCachedNodeSnapshots(
                cachedNodes = paymentRepository.getCachedVpnNodes(userId = userId),
                runtimes = paymentRepository.getCachedVpnNodeRuntime(userId = userId),
                selectedNodeId = null,
            )
        }.orEmpty()
        val lastIssuedConfigExpireAt = paymentRepository.getLastIssuedVpnConfigExpireAt()
        val lastIssuedRegionCode = paymentRepository.getLastIssuedVpnRegionCode()
        val currentOrderNo = paymentRepository.getCurrentOrderId()
        val orderSnapshot = loadOrdersSnapshot()
        val orders = orderSnapshot.orders
        var walletOverview = when {
            syncedWalletAddresses && !currentUserId.isNullOrBlank() ->
                paymentRepository.syncWalletOverviewFromServer(force = true, userId = currentUserId).getOrNull()
            else -> paymentRepository.getCachedWalletOverview(currentUserId)
        }
        val latestOrder = orders.maxByOrNull { it.createdAt }
        val activeOrder = currentOrderNo?.let { orderNo ->
            orders.firstOrNull { it.orderNo == orderNo }
        } ?: latestOrder
        var selectedLocalRegion = selectedCachedNode(cachedNodeSnapshots)?.toRegionSpeed()
        var hasLocalConfig = !MmkvManager.getSelectServer().isNullOrEmpty()
        val localSubscriptionUrl = activeOrder?.subscriptionUrl
            ?: paymentRepository.getSavedSubscriptionUrl()

        if (cachedNodeSnapshots.isEmpty() && !currentUserId.isNullOrBlank()) {
            paymentRepository.syncVpnNodesFromServer(force = false, userId = currentUserId)
            cachedNodeSnapshots = buildCachedNodeSnapshots(
                cachedNodes = paymentRepository.getCachedVpnNodes(userId = currentUserId),
                runtimes = paymentRepository.getCachedVpnNodeRuntime(userId = currentUserId),
                selectedNodeId = null,
            )
            selectedLocalRegion = selectedCachedNode(cachedNodeSnapshots)?.toRegionSpeed()
        }

        if (cachedNodeSnapshots.isEmpty() && !localSubscriptionUrl.isNullOrBlank()) {
            paymentRepository.importSubscriptionUrl(
                subscriptionUrl = localSubscriptionUrl,
                remarks = activeOrder?.planName ?: "CryptoVPN Subscription",
            )
            hasLocalConfig = !MmkvManager.getSelectServer().isNullOrEmpty()
        }

        val localSignals = buildLocalVpnSignals(
            latestOrder = activeOrder,
            localNodes = cachedNodeSnapshots,
            localSubscriptionUrl = localSubscriptionUrl,
            hasLocalConfig = hasLocalConfig,
        )
        val meResult = paymentRepository.getMe()
        if (meResult.isFailure) {
            getCachedVpnHomeState()?.let { cachedState ->
                return cachedState.copy(
                    accountLabel = cachedUser?.email ?: cachedUser?.username ?: cachedState.accountLabel,
                    canConnect = hasLocalConfig,
                    autoConnectEnabled = hasLocalConfig,
                    oneTapLabel = if (hasLocalConfig) "启动已导入配置" else "导入已购配置后连接",
                )
            }
            val resolvedConnectionStatus = resolveConnectionStatus(null, hasLocalConfig)
            return VpnHomeUiState(
                isLoading = false,
                loadState = if (cachedUser == null && activeOrder == null && cachedNodeSnapshots.isEmpty()) {
                    P0LoadState.UNAVAILABLE
                } else {
                    P0LoadState.READY
                },
                accountLabel = cachedUser?.email ?: cachedUser?.username ?: "未登录",
                errorMessage = null,
                unavailableMessage = null,
                connectionStatus = resolvedConnectionStatus,
                selectedRegion = selectedLocalRegion ?: RegionSpeed(
                    regionName = "未获取区域",
                    protocol = "配置信息未返回",
                    latencyMs = 0,
                    load = "--",
                ),
                subscription = localSubscriptionSummary(
                    latestOrder = activeOrder,
                    hasSavedSubscriptionUrl = !localSubscriptionUrl.isNullOrBlank(),
                    fallbackExpireAt = lastIssuedConfigExpireAt,
                ),
                autoConnectEnabled = hasLocalConfig,
                oneTapLabel = if (hasLocalConfig) "启动已导入配置" else "导入已购配置后连接",
                speedNodes = cachedNodeSnapshots.map { it.toRegionSpeed() },
                watchSignals = localSignals,
                overviewValueText = resolveVpnOverviewValueText(
                    walletOverview = walletOverview,
                    ordersSyncUnavailable = orderSnapshot.syncUnavailable,
                ),
                overviewSummaryText = buildOverviewSummary(
                    orders = orders,
                    subscription = null,
                    fallbackExpireAt = lastIssuedConfigExpireAt,
                    ordersSyncUnavailable = orderSnapshot.syncUnavailable,
                ),
                alertCount = localSignals.size,
                nodeHealthPercent = calculateNodeHealthPercent(
                    localNodeCount = cachedNodeSnapshots.size,
                    allowedRegions = emptyList(),
                    cachedNodes = cachedNodeSnapshots,
                ),
                vlessExpiryLabel = resolveVlessExpiryLabel(
                    subscription = null,
                    lastIssuedConfigExpireAt = lastIssuedConfigExpireAt,
                ),
                vlessRegionLabel = lastIssuedRegionCode ?: selectedLocalRegion?.regionName ?: "待同步",
                configStatusLabel = localConfigStatusLabel(
                    hasLocalConfig = hasLocalConfig,
                    localSubscriptionUrl = localSubscriptionUrl,
                    localNodeCount = cachedNodeSnapshots.size,
                ),
                latestOrderLabel = activeOrder?.let { latest ->
                    buildString {
                        append(latest.planName)
                        append(" · ")
                        append(latest.statusText)
                        latest.subscriptionUrl?.takeIf { it.isNotBlank() }?.let {
                            append(" · 已签发订阅")
                        }
                    }
                } ?: if (cachedNodeSnapshots.isNotEmpty()) {
                    "已加载 ${cachedNodeSnapshots.size} 个节点"
                } else {
                    resolveOrdersFallbackLabel(orderSnapshot.syncUnavailable)
                },
                canConnect = hasLocalConfig,
            )
        }

        val me = meResult.getOrThrow()
        walletOverview = when {
            syncedWalletAddresses && !currentUserId.isNullOrBlank() -> walletOverview
            else -> paymentRepository.getWalletOverview().getOrNull() ?: walletOverview
        }
        val subscription = paymentRepository.getSubscription().getOrNull() ?: me.subscription
        val hasActiveSubscription = subscription?.status.equals("ACTIVE", ignoreCase = true)
        val vpnStatus = if (hasActiveSubscription) {
            paymentRepository.getVpnStatus().getOrNull()
        } else {
            null
        }
        if (!currentUserId.isNullOrBlank()) {
            paymentRepository.syncVpnNodesFromServer(force = false, userId = currentUserId)
            cachedNodeSnapshots = buildCachedNodeSnapshots(
                cachedNodes = paymentRepository.getCachedVpnNodes(userId = currentUserId),
                runtimes = paymentRepository.getCachedVpnNodeRuntime(userId = currentUserId),
                selectedNodeId = vpnStatus?.selectedNodeId,
            )
            selectedLocalRegion = selectedCachedNode(
                cachedNodeSnapshots,
                vpnStatus?.selectedNodeId,
            )?.toRegionSpeed()
        }
        val connectionStatus = resolveConnectionStatus(vpnStatus, hasLocalConfig)
        val vpnRegions = if (hasActiveSubscription) {
            paymentRepository.getVpnRegions().getOrNull().orEmpty()
        } else {
            emptyList()
        }
        val allowedRegions = vpnRegions.filter { it.isAllowed }
        val selectedLineName = vpnStatus?.selectedLineName?.takeIf { it.isNotBlank() }
        val selectedRegion = selectedLocalRegion ?: resolveSelectedRegion(allowedRegions, vpnStatus)
        val watchSignals = buildVpnSignals(
            subscription = subscription,
            vpnStatus = vpnStatus,
            latestOrder = activeOrder,
            hasLocalConfig = hasLocalConfig,
            localNodes = cachedNodeSnapshots,
            localSubscriptionUrl = localSubscriptionUrl,
        )

        val loadState = when {
            cachedNodeSnapshots.isNotEmpty() || localSubscriptionUrl != null -> P0LoadState.READY
            allowedRegions.isEmpty() && subscription?.status == "ACTIVE" -> P0LoadState.EMPTY
            allowedRegions.isEmpty() -> P0LoadState.EMPTY
            else -> P0LoadState.READY
        }

        return VpnHomeUiState(
            isLoading = false,
            loadState = loadState,
            connectionStatus = connectionStatus,
            accountLabel = me.email,
            selectedRegion = selectedRegion ?: RegionSpeed(
                regionName = "未获取区域",
                protocol = "区域信息未返回",
                latencyMs = 0,
                load = "--",
            ),
            subscription = subscription?.toSummary() ?: SubscriptionSummary(
                planName = "未获取订阅",
                expiresInDays = 0,
                autoRenew = false,
                nextBillingLabel = "未返回",
                status = "NONE",
            ),
            autoConnectEnabled = vpnStatus?.canIssueConfig == true || hasLocalConfig,
            oneTapLabel = when {
                connectionStatus == VpnConnectionStatus.CONNECTED -> "断开并刷新状态"
                connectionStatus == VpnConnectionStatus.CONNECTING -> "连接中"
                hasLocalConfig -> "启动配置"
                vpnStatus?.canIssueConfig == true -> "当前账号可同步配置"
                else -> "购买套餐后连接"
            },
            speedNodes = if (cachedNodeSnapshots.isNotEmpty()) {
                cachedNodeSnapshots.map { it.toRegionSpeed() }
            } else {
                allowedRegions.map { it.toRegionSpeed() }
            },
            watchSignals = watchSignals,
            overviewValueText = resolveVpnOverviewValueText(
                walletOverview = walletOverview,
                ordersSyncUnavailable = orderSnapshot.syncUnavailable,
            ),
            overviewSummaryText = buildOverviewSummary(
                orders = orders,
                subscription = subscription,
                fallbackExpireAt = lastIssuedConfigExpireAt,
                ordersSyncUnavailable = orderSnapshot.syncUnavailable,
            ),
            alertCount = watchSignals.size,
            nodeHealthPercent = calculateNodeHealthPercent(
                localNodeCount = cachedNodeSnapshots.size,
                allowedRegions = allowedRegions,
                cachedNodes = cachedNodeSnapshots,
            ),
            vlessExpiryLabel = resolveVlessExpiryLabel(
                subscription = subscription,
                lastIssuedConfigExpireAt = lastIssuedConfigExpireAt,
            ),
            vlessRegionLabel = selectedLineName
                ?: lastIssuedRegionCode
                ?: selectedRegion?.regionName
                ?: selectedLocalRegion?.regionName
                ?: "待同步",
            configStatusLabel = localConfigStatusLabel(
                hasLocalConfig = hasLocalConfig,
                localSubscriptionUrl = localSubscriptionUrl,
                localNodeCount = cachedNodeSnapshots.size,
                remoteIssuable = vpnStatus?.canIssueConfig == true,
            ),
            latestOrderLabel = activeOrder?.let { latest ->
                buildString {
                    append(latest.planName)
                    append(" · ")
                    append(latest.statusText)
                    latest.subscriptionUrl?.takeIf { it.isNotBlank() }?.let {
                        append(" · 已签发订阅")
                    }
                }
            } ?: resolveOrdersFallbackLabel(orderSnapshot.syncUnavailable),
            canConnect = hasLocalConfig,
            emptyMessage = null,
        )
    }

    override suspend fun getWalletHomeState(): WalletHomeUiState {
        val currentUserId = paymentRepository.getCurrentUserId()
        val syncedWalletAddresses = syncDerivedWalletAddressesIfAvailable(currentUserId)
        val walletOverview = when {
            syncedWalletAddresses && !currentUserId.isNullOrBlank() ->
                paymentRepository.syncWalletOverviewFromServer(force = true, userId = currentUserId).getOrNull()
                    ?: paymentRepository.getWalletOverview().getOrNull()
            else -> paymentRepository.getWalletOverview().getOrNull()
        }
        val lifecycle = paymentRepository.getWalletLifecycle().getOrNull()
        val hasUsableWallet = lifecycle.hasUsableWallet()
        if (walletOverview != null) {
            return walletOverview.toWalletHomeUiState(lifecycle)
        }

        val cachedUser = paymentRepository.getCachedCurrentUser()
        val meResult = paymentRepository.getMe()
        if (meResult.isFailure) {
            val failureMessage = meResult.exceptionOrNull()?.message ?: "获取账号信息失败"
            return WalletHomeUiState(
                isLoading = false,
                loadState = if (cachedUser == null) P0LoadState.UNAVAILABLE else P0LoadState.ERROR,
                accountLabel = cachedUser?.email ?: cachedUser?.username ?: "未登录",
                errorMessage = failureMessage,
                unavailableMessage = if (cachedUser == null) "请先登录后再查看支付记录。" else null,
                totalBalanceText = "--",
                summaryLabel = "无法同步区块数据",
                alertBanner = "请确认",
                walletExists = hasUsableWallet,
                walletLifecycleStatus = lifecycle?.lifecycleStatus ?: "NOT_CREATED",
                walletId = lifecycle?.walletId,
                walletDisplayName = lifecycle?.displayName,
                walletNextAction = lifecycle?.nextAction ?: "CREATE_OR_IMPORT",
            )
        }

        val me = meResult.getOrThrow()
        val subscription = paymentRepository.getSubscription().getOrNull() ?: me.subscription
        val orders = loadOrders()
        val latestOrder = orders.maxByOrNull { it.createdAt }
        if (orders.isEmpty()) {
            return WalletHomeUiState(
                isLoading = false,
                loadState = P0LoadState.EMPTY,
                accountLabel = me.email,
                totalBalanceText = "0 笔订单",
                summaryLabel = subscription?.planName ?: subscription?.planCode ?: "暂无支付记录",
                emptyMessage = "当前账号还没有同步支付记录。",
                alertBanner = " 支付与订单 ",
                walletExists = hasUsableWallet,
                walletLifecycleStatus = lifecycle?.lifecycleStatus ?: "NOT_CREATED",
                walletId = lifecycle?.walletId,
                walletDisplayName = lifecycle?.displayName,
                walletNextAction = lifecycle?.nextAction ?: "CREATE_OR_IMPORT",
            )
        }

        val chainGroups = orders.groupBy { it.quoteNetworkCode.lowercase(Locale.ROOT) }
        val chains = chainGroups.entries
            .sortedByDescending { it.value.size }
            .map { (chainId, items) ->
                WalletChainSummary(
                    chainId = chainId,
                    label = walletHomeChainLabel(chainId),
                    balanceText = "${items.size} 笔订单",
                    accent = "区块支付网络",
                    itemCount = items.size,
                )
            }

        val assets = orders
            .groupBy { "${it.quoteAssetCode}:${it.quoteNetworkCode}" }
            .entries
            .sortedByDescending { (_, items) -> items.size }
            .map { (_, items) ->
                val latest = items.maxByOrNull { it.createdAt } ?: items.first()
                val totalAmount = items.sumOf { it.payment.amountCrypto.toDoubleOrNull() ?: 0.0 }
                AssetHolding(
                    symbol = latest.quoteAssetCode,
                    chainLabel = latest.quoteNetworkCode,
                    balanceText = "${items.size} 笔订单",
                    valueText = "累计 ${formatAssetAmount(totalAmount, latest.quoteAssetCode)}",
                    changeText = latest.statusText,
                    changePositive = latest.status !in setOf("FAILED", "EXPIRED", "CANCELED"),
                    detailText = latest.planName,
                )
            }

        return WalletHomeUiState(
            isLoading = false,
            loadState = P0LoadState.READY,
            accountLabel = me.email,
            totalBalanceText = "${orders.size} 笔支付记录",
            summaryLabel = subscription?.let { "${it.planName ?: it.planCode ?: "订阅"} · ${it.status}" } ?: "暂无有效订阅",
            selectedChainId = chains.firstOrNull()?.chainId ?: "all",
            chains = chains,
            assets = assets,
            alertBanner = latestOrder?.let { "最新订单：${it.planName} · ${it.statusText}" }
                ?: "订单与区块记录",
            walletExists = hasUsableWallet,
            walletLifecycleStatus = lifecycle?.lifecycleStatus ?: "NOT_CREATED",
            walletId = lifecycle?.walletId,
            walletDisplayName = lifecycle?.displayName,
            walletNextAction = lifecycle?.nextAction ?: "CREATE_OR_IMPORT",
        )
    }

    private suspend fun resolvePostAuthRoute(): String {
        val lifecycle = paymentRepository.getWalletLifecycle().getOrNull()
        return when {
            lifecycle?.walletExists != true ||
                lifecycle.sourceType.equals("LEGACY", ignoreCase = true) ->
                CryptoVpnRouteSpec.walletOnboarding.pattern
            lifecycle.nextAction.equals("BACKUP_MNEMONIC", ignoreCase = true) &&
                !lifecycle.walletId.isNullOrBlank() ->
                CryptoVpnRouteSpec.backupMnemonicRoute(lifecycle.walletId)
            lifecycle.nextAction.equals("CONFIRM_MNEMONIC", ignoreCase = true) &&
                !lifecycle.walletId.isNullOrBlank() ->
                CryptoVpnRouteSpec.confirmMnemonicRoute(lifecycle.walletId)
            else -> CryptoVpnRouteSpec.walletHome.pattern
        }
    }

    private suspend fun loadOrders(): List<Order> {
        paymentRepository.getCurrentOrderId()?.let { currentOrderId ->
            paymentRepository.getOrder(currentOrderId)
        }
        val currentUserId = paymentRepository.getCurrentUserId() ?: return emptyList()
        return readLocalOrders(currentUserId)
    }

    private suspend fun loadOrdersSnapshot(): OrdersSnapshot {
        val currentUserId = paymentRepository.getCurrentUserId()
            ?: return OrdersSnapshot(emptyList(), syncUnavailable = false)
        val localOrders = readLocalOrders(currentUserId)
        paymentRepository.getCurrentOrderId()?.let { currentOrderId ->
            paymentRepository.getOrder(currentOrderId)
        }
        val syncResult = paymentRepository.syncOrdersFromServer(force = false, userId = currentUserId)
        val refreshedOrders = readLocalOrders(currentUserId)
        val syncUnavailable = syncResult.exceptionOrNull()?.message?.contains("订单接口待同步") == true
        return OrdersSnapshot(
            orders = if (refreshedOrders.isNotEmpty()) refreshedOrders else localOrders,
            syncUnavailable = syncUnavailable && refreshedOrders.isEmpty() && localOrders.isEmpty(),
        )
    }

    private fun resolveSelectedRegion(
        regions: List<VpnRegionItem>,
        vpnStatus: VpnStatusData?,
    ): RegionSpeed? {
        val selected = regions.firstOrNull { it.regionCode == vpnStatus?.currentRegionCode }
            ?: regions.firstOrNull { it.status.equals("ONLINE", ignoreCase = true) }
            ?: regions.firstOrNull()
        return selected?.toRegionSpeed()
    }

    private fun resolveConnectionStatus(
        vpnStatus: VpnStatusData?,
        hasLocalConfig: Boolean,
    ): VpnConnectionStatus {
        return when {
            V2RayServiceManager.isRunning() -> VpnConnectionStatus.CONNECTED
            vpnStatus?.sessionStatus?.contains("CONNECT", ignoreCase = true) == true -> VpnConnectionStatus.CONNECTING
            hasLocalConfig -> VpnConnectionStatus.DISCONNECTED
            else -> VpnConnectionStatus.DISCONNECTED
        }
    }

    private fun buildVpnSignals(
        subscription: CurrentSubscriptionData?,
        vpnStatus: VpnStatusData?,
        latestOrder: Order?,
        hasLocalConfig: Boolean,
        localNodes: List<CachedVpnNodeSnapshot>,
        localSubscriptionUrl: String?,
    ): List<WatchSignal> {
        val signals = mutableListOf<WatchSignal>()
        signals += WatchSignal(
            symbol = "SUB",
            reason = subscription?.let {
                "${it.planName ?: it.planCode ?: "当前订阅"} · ${it.status}"
            } ?: "暂无有效订阅",
            changeText = subscription?.daysRemaining?.let { "剩余 ${it} 天" } ?: "等待购买",
            volumeText = subscription?.expireAt?.let(::formatDateLabel) ?: "--",
            isPositive = subscription?.status == "ACTIVE",
        )
        signals += WatchSignal(
            symbol = "VPN",
            reason = vpnStatus?.sessionStatus ?: "未同步节点",
            changeText = when {
                hasLocalConfig -> "已有配置"
                vpnStatus?.canIssueConfig == true -> "可签发配置"
                else -> "未就绪"
            },
            volumeText = vpnStatus?.connectionMode ?: "--",
            isPositive = hasLocalConfig || vpnStatus?.canIssueConfig == true,
        )
        latestOrder?.let {
            signals += WatchSignal(
                symbol = it.quoteAssetCode,
                reason = "${it.planName} · ${it.statusText}",
                changeText = it.quoteNetworkCode,
                volumeText = it.payment.amountCrypto,
                isPositive = it.status !in setOf("FAILED", "EXPIRED", "CANCELED"),
            )
        }
        signals += buildLocalVpnSignals(
            latestOrder = latestOrder,
            localNodes = localNodes,
            localSubscriptionUrl = localSubscriptionUrl,
            hasLocalConfig = hasLocalConfig,
        )
        return signals
            .distinctBy { "${it.symbol}:${it.reason}" }
            .take(4)
    }

    private fun VpnRegionItem.toRegionSpeed(): RegionSpeed {
        return RegionSpeed(
            regionName = displayName,
            protocol = "$tier · $status",
            latencyMs = 0,
            load = remark ?: if (status.equals("ONLINE", ignoreCase = true)) "可用" else status,
            regionCode = regionCode,
            isAllowed = isAllowed,
        )
    }

    private fun CurrentSubscriptionData.toSummary(): SubscriptionSummary {
        return SubscriptionSummary(
            planName = planName ?: planCode ?: "待同步订阅",
            expiresInDays = daysRemaining ?: 0,
            autoRenew = false,
            nextBillingLabel = expireAt?.let(::formatDateLabel) ?: "待同步",
            status = status,
        )
    }

    private fun buildCachedSubscriptionData(
        planCode: String?,
        status: String?,
        daysRemaining: Int?,
        expireAt: String?,
        subscriptionUrl: String?,
        marzbanUsername: String?,
    ): CurrentSubscriptionData? {
        if (planCode.isNullOrBlank() && status.isNullOrBlank() && expireAt.isNullOrBlank() && subscriptionUrl.isNullOrBlank()) {
            return null
        }
        return CurrentSubscriptionData(
            planCode = planCode,
            status = status ?: "LOCAL",
            expireAt = expireAt,
            daysRemaining = daysRemaining,
            isUnlimitedTraffic = true,
            maxActiveSessions = 1,
            subscriptionUrl = subscriptionUrl,
            marzbanUsername = marzbanUsername,
        )
    }

    private fun buildCachedVpnStatusData(
        subscriptionStatus: String?,
        currentRegionCode: String?,
        selectedLineCode: String?,
        selectedLineName: String?,
        selectedNodeId: String?,
        selectedNodeName: String?,
        sessionStatus: String?,
    ): VpnStatusData? {
        if (subscriptionStatus.isNullOrBlank() &&
            currentRegionCode.isNullOrBlank() &&
            selectedLineName.isNullOrBlank() &&
            selectedNodeId.isNullOrBlank() &&
            selectedNodeName.isNullOrBlank() &&
            sessionStatus.isNullOrBlank()
        ) {
            return null
        }
        return VpnStatusData(
            subscriptionStatus = subscriptionStatus ?: "NONE",
            currentRegionCode = currentRegionCode,
            selectedLineCode = selectedLineCode,
            selectedLineName = selectedLineName,
            selectedNodeId = selectedNodeId,
            selectedNodeName = selectedNodeName,
            connectionMode = null,
            canIssueConfig = false,
            sessionStatus = sessionStatus ?: "LOCAL",
        )
    }

    private fun inferNetworkCode(assetCode: String): String {
        return if (assetCode.equals(PaymentConfig.AssetCode.SOL, ignoreCase = true)) {
            PaymentConfig.NetworkCode.SOLANA
        } else {
            PaymentConfig.NetworkCode.TRON
        }
    }

    private fun formatAssetAmount(value: Double, assetCode: String): String {
        return String.format(Locale.US, "%.6f %s", value, assetCode)
            .replace(Regex("0+\\s"), " $assetCode")
    }

    private fun formatEpoch(epoch: Long): String =
        Instant.ofEpochMilli(epoch).toString()

    private suspend fun syncDerivedWalletAddressesIfAvailable(accountId: String?): Boolean {
        val resolvedAccountId = accountId?.takeIf { it.isNotBlank() } ?: return false
        if (walletSecretStore.getConflictingMnemonicRecord(resolvedAccountId) != null) {
            return false
        }
        walletSecretStore.getMnemonicRecord(resolvedAccountId) ?: return false
        val addresses = runCatching { walletKeyManager.deriveAddresses(resolvedAccountId) }
            .getOrElse { return false }
        val payload = listOf(
            arrayOf("SOLANA", "SOL", addresses.solanaAddress),
            arrayOf("SOLANA", "USDT", addresses.solanaAddress),
            arrayOf("TRON", "TRX", addresses.tronAddress),
            arrayOf("TRON", "USDT", addresses.tronAddress),
        )
        return payload.all { (networkCode, assetCode, address) ->
            paymentRepository.upsertWalletPublicAddress(
                networkCode = networkCode,
                assetCode = assetCode,
                address = address,
                isDefault = true,
            ).isSuccess
        }
    }

    private suspend fun readLocalOrders(userId: String): List<Order> {
        return paymentRepository.getLocalRepository().getOrdersByUserId(userId).map {
            Order(
                orderId = it.orderNo,
                orderNo = it.orderNo,
                planCode = it.planId,
                planName = it.planName,
                orderType = PaymentConfig.PurchaseType.NEW,
                quoteAssetCode = it.assetCode,
                quoteNetworkCode = it.networkCode.ifBlank { inferNetworkCode(it.assetCode) },
                quoteUsdAmount = it.usdAmount,
                payableAmount = it.amount,
                status = it.status,
                expiresAt = formatEpoch(it.expiredAt ?: it.createdAt),
                confirmedAt = it.paidAt?.let(::formatEpoch),
                completedAt = it.fulfilledAt?.let(::formatEpoch),
                createdAt = formatEpoch(it.createdAt),
                subscriptionUrl = it.subscriptionUrl,
            )
        }
    }

    private fun formatDateLabel(date: String): String =
        runCatching { LocalDate.parse(date.take(10)).format(DateTimeFormatter.ISO_DATE) }.getOrDefault(date)

    private fun localSubscriptionSummary(
        latestOrder: Order?,
        hasSavedSubscriptionUrl: Boolean,
        fallbackExpireAt: String? = null,
    ): SubscriptionSummary? {
        val order = latestOrder
        val expiresInDays = fallbackExpireAt?.let(::remainingDaysFromIso) ?: 0
        return when {
            order != null -> SubscriptionSummary(
                planName = order.planName,
                expiresInDays = expiresInDays,
                autoRenew = false,
                nextBillingLabel = fallbackExpireAt?.let(::formatDateTimeLabel) ?: "待同步",
                status = order.status,
            )
            hasSavedSubscriptionUrl -> SubscriptionSummary(
                planName = "已导入订阅",
                expiresInDays = 0,
                autoRenew = false,
                nextBillingLabel = "已导入订阅",
                status = "LOCAL_CONFIG",
            )
            else -> null
        }
    }

    private fun buildLocalVpnSignals(
        latestOrder: Order?,
        localNodes: List<CachedVpnNodeSnapshot>,
        localSubscriptionUrl: String?,
        hasLocalConfig: Boolean,
    ): List<WatchSignal> {
        val selectedNode = selectedCachedNode(localNodes)
        val signals = mutableListOf<WatchSignal>()
        selectedNode?.let {
            signals += WatchSignal(
                symbol = "NODE",
                reason = it.displayName,
                changeText = it.protocol,
                volumeText = it.latencyMs?.let { latency -> "${latency}ms" } ?: it.description,
                isPositive = true,
            )
        }
        localSubscriptionUrl?.let { url ->
            signals += WatchSignal(
                symbol = "VLESS",
                reason = shortenSensitiveUrl(url),
                changeText = selectedNode?.lineName ?: "已保存订阅",
                volumeText = if (hasLocalConfig) "配置可用" else "待导入节点",
                isPositive = true,
            )
        }
        if (latestOrder != null) {
            signals += WatchSignal(
                symbol = "ORDER",
                reason = "${latestOrder.planName} · ${latestOrder.statusText}",
                changeText = latestOrder.quoteNetworkCode,
                volumeText = latestOrder.payment.amountCrypto,
                isPositive = latestOrder.status !in setOf("FAILED", "EXPIRED", "CANCELED"),
            )
        }
        return signals
    }

    private fun localConfigStatusLabel(
        hasLocalConfig: Boolean,
        localSubscriptionUrl: String?,
        localNodeCount: Int,
        remoteIssuable: Boolean = false,
    ): String {
        return when {
            (localSubscriptionUrl != null || hasLocalConfig) && localNodeCount > 0 -> "已就绪"
            localSubscriptionUrl != null -> "已保存订阅"
            hasLocalConfig -> "已就绪"
            remoteIssuable -> "可签发"
            else -> "待同步"
        }
    }

    private fun buildCachedNodeSnapshots(
        cachedNodes: List<VpnNodeCacheEntity>,
        runtimes: List<VpnNodeRuntimeEntity>,
        selectedNodeId: String?,
    ): List<CachedVpnNodeSnapshot> {
        val runtimeMap = runtimes.associateBy { it.nodeId }
        return cachedNodes.map { node ->
            val runtime = runtimeMap[node.nodeId]
            CachedVpnNodeSnapshot(
                nodeId = node.nodeId,
                displayName = node.nodeName,
                lineName = node.lineName,
                regionName = node.regionName,
                description = listOfNotNull(
                    node.lineName,
                    "${node.host}:${node.port}",
                ).joinToString(" · "),
                protocol = node.source,
                healthStatus = runtime?.healthStatus ?: "UNKNOWN",
                latencyMs = runtime?.pingMs,
                selected = selectedNodeId == node.nodeId || runtime?.selected == true,
            )
        }.sortedWith(
            compareByDescending<CachedVpnNodeSnapshot> { it.selected }
                .thenBy { it.latencyMs == null }
                .thenBy { it.latencyMs ?: Int.MAX_VALUE }
                .thenBy { it.displayName },
        )
    }

    private fun selectedCachedNode(
        nodes: List<CachedVpnNodeSnapshot>,
        selectedNodeId: String? = null,
    ): CachedVpnNodeSnapshot? {
        return nodes.firstOrNull { it.selected || (selectedNodeId != null && it.nodeId == selectedNodeId) }
            ?: nodes.firstOrNull()
    }

    private fun CachedVpnNodeSnapshot.toRegionSpeed(): RegionSpeed {
        return RegionSpeed(
            regionName = lineName,
            protocol = displayName,
            latencyMs = latencyMs ?: 0,
            load = if (selected) "已选中" else description,
            regionCode = nodeId,
            isAllowed = true,
        )
    }

    private fun shortenSensitiveUrl(url: String): String {
        val trimmed = url.trim()
        return if (trimmed.length <= 40) trimmed else "${trimmed.take(18)}...${trimmed.takeLast(14)}"
    }

    private fun buildOverviewSummary(
        orders: List<Order>,
        subscription: CurrentSubscriptionData?,
        fallbackExpireAt: String?,
        ordersSyncUnavailable: Boolean,
    ): String {
        val orderText = when {
            orders.isNotEmpty() -> "${orders.size} 笔订单"
            ordersSyncUnavailable -> "订单待同步"
            else -> "暂无订单"
        }
        val subscriptionText = subscription?.status ?: "订阅待同步"
        val expiryText = resolveVlessExpiryLabel(subscription, fallbackExpireAt)
        return "$orderText · $subscriptionText · VLESS 至 $expiryText"
    }

    private fun resolveOrdersFallbackLabel(ordersSyncUnavailable: Boolean): String {
        return if (ordersSyncUnavailable) "订单待同步" else "暂无订单"
    }

    private fun calculateNodeHealthPercent(
        localNodeCount: Int,
        allowedRegions: List<VpnRegionItem>,
        cachedNodes: List<CachedVpnNodeSnapshot> = emptyList(),
    ): Int {
        if (cachedNodes.isNotEmpty()) {
            val healthy = cachedNodes.count { it.healthStatus.equals("HEALTHY", ignoreCase = true) }
            return (healthy * 100) / cachedNodes.size
        }
        if (allowedRegions.isNotEmpty()) {
            val online = allowedRegions.count {
                it.status.equals("ACTIVE", ignoreCase = true) || it.status.equals("ONLINE", ignoreCase = true)
            }
            return (online * 100) / allowedRegions.size
        }
        return if (localNodeCount > 0) 100 else 0
    }

    private fun resolveVlessExpiryLabel(
        subscription: CurrentSubscriptionData?,
        lastIssuedConfigExpireAt: String?,
    ): String {
        return when {
            subscription?.expireAt != null -> formatDateLabel(subscription.expireAt)
            lastIssuedConfigExpireAt != null -> formatDateTimeLabel(lastIssuedConfigExpireAt)
            else -> "待同步"
        }
    }

    private fun formatUsdAmount(value: Double): String {
        return "$" + String.format(Locale.US, "%,.2f", value)
    }

    private fun formatDateTimeLabel(date: String): String =
        runCatching {
            Instant.parse(date).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("MM-dd HH:mm"))
        }.getOrDefault(formatDateLabel(date))

    private fun remainingDaysFromIso(iso: String): Int {
        return runCatching {
            val localDate = LocalDate.parse(iso.take(10))
            val today = LocalDate.now()
            maxOf(0, localDate.toEpochDay().minus(today.toEpochDay()).toInt())
        }.getOrDefault(0)
    }

    private fun mapLoginFailureMessage(message: String?, code: String?): String {
        val normalized = listOfNotNull(code, message).joinToString(" ").uppercase(Locale.ROOT)
        return when {
            "AUTH_INVALID_CREDENTIALS" in normalized ||
                "INVALID CREDENTIALS" in normalized ||
                "UNAUTHORIZED" in normalized -> "账号或密码错误，请重新输入"
            "NOT FOUND" in normalized -> "账号不存在，请检查邮箱地址"
            message.isNullOrBlank() -> "登录失败，请稍后重试"
            else -> message
        }
    }
}

private data class CachedVpnNodeSnapshot(
    val nodeId: String,
    val displayName: String,
    val lineName: String,
    val regionName: String,
    val description: String,
    val protocol: String,
    val healthStatus: String,
    val latencyMs: Int?,
    val selected: Boolean,
)

private data class OrdersSnapshot(
    val orders: List<Order>,
    val syncUnavailable: Boolean,
)
