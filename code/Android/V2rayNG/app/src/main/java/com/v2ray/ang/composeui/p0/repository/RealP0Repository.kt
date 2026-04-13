package com.v2ray.ang.composeui.p0.repository

import android.content.Context
import com.v2ray.ang.BuildConfig
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.model.AssetHolding
import com.v2ray.ang.composeui.p0.model.LoginResult
import com.v2ray.ang.composeui.p0.model.LoginUiState
import com.v2ray.ang.composeui.p0.model.P0LoadState
import com.v2ray.ang.composeui.p0.model.RegionSpeed
import com.v2ray.ang.composeui.p0.model.SplashUiState
import com.v2ray.ang.composeui.p0.model.SubscriptionSummary
import com.v2ray.ang.composeui.p0.model.VpnConnectionStatus
import com.v2ray.ang.composeui.p0.model.VpnHomeUiState
import com.v2ray.ang.composeui.p0.model.WalletChainSummary
import com.v2ray.ang.composeui.p0.model.WalletHomeUiState
import com.v2ray.ang.composeui.p0.model.WalletOnboardingUiState
import com.v2ray.ang.composeui.p0.model.WatchSignal
import com.v2ray.ang.dto.ProfileItem
import com.v2ray.ang.dto.SubscriptionItem
import com.v2ray.ang.handler.MmkvManager
import com.v2ray.ang.handler.V2RayServiceManager
import com.v2ray.ang.payment.PaymentConfig
import com.v2ray.ang.payment.data.api.CurrentSubscriptionData
import com.v2ray.ang.payment.data.api.MeData
import com.v2ray.ang.payment.data.api.VpnRegionItem
import com.v2ray.ang.payment.data.api.VpnStatusData
import com.v2ray.ang.payment.data.local.entity.UserEntity
import com.v2ray.ang.payment.data.model.LoginRequest
import com.v2ray.ang.payment.data.model.Order
import com.v2ray.ang.payment.data.repository.PaymentRepository
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
                val failureMessage = error.message ?: "无法完成登录后的真实数据预同步。"
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
                        progressDetail = "检测到本地登录态失效，已清理并返回登录页。",
                        authResolved = true,
                        readyToNavigate = true,
                        nextRoute = CryptoVpnRouteSpec.emailLogin.pattern,
                        loadState = P0LoadState.READY,
                        accountLabel = "未登录",
                        subscriptionLabel = "无本地缓存",
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
                    unavailableMessage = "启动前必须完成订单、订阅、节点和配置同步。",
                )
            }
            val subscription = paymentRepository.getSubscription().getOrNull() ?: me.subscription
            val regionCount = paymentRepository.getVpnRegions().getOrNull()?.count { it.isAllowed } ?: 0
            return@withContext SplashUiState(
                checkingSecureBoot = false,
                versionLabel = "v${BuildConfig.VERSION_NAME}",
                buildStatus = "${me.email} · ${subscription?.status ?: "NONE"} · $regionCount 个可用区域",
                progress = 1f,
                progressHeadline = "准备完成",
                progressDetail = "真实账号、订阅和 VPN 区域已同步，正在进入主界面。",
                authResolved = true,
                readyToNavigate = true,
                nextRoute = CryptoVpnRouteSpec.vpnHome.pattern,
                loadState = P0LoadState.READY,
                accountLabel = me.email,
                subscriptionLabel = subscription?.planCode ?: "暂无有效订阅",
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
                    ?: "已识别本地账户数据",
                progress = 1f,
                progressHeadline = "继续进入应用",
                progressDetail = "检测到本地账户数据，将直接进入应用并在后台恢复真实会话。",
                authResolved = true,
                readyToNavigate = true,
                nextRoute = CryptoVpnRouteSpec.vpnHome.pattern,
                loadState = P0LoadState.READY,
                accountLabel = cachedUser?.email ?: cachedUser?.username ?: "本地账户",
                subscriptionLabel = if (cachedOrders.isEmpty()) {
                    "已恢复本地账户数据"
                } else {
                    "${cachedOrders.size} 笔本地订单缓存"
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
                progressDetail = "未检测到有效认证信息，将进入真实登录页。",
                authResolved = true,
                readyToNavigate = true,
                nextRoute = CryptoVpnRouteSpec.emailLogin.pattern,
                loadState = P0LoadState.READY,
                accountLabel = cachedUser?.email ?: "未登录",
                subscriptionLabel = if (cachedOrders.isEmpty()) "暂无本地订单缓存" else "${cachedOrders.size} 笔本地订单缓存",
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
                progressDetail = "检测到失效的本地 token 残留，已清理并进入登录页。",
                authResolved = true,
                readyToNavigate = true,
                nextRoute = CryptoVpnRouteSpec.emailLogin.pattern,
                loadState = P0LoadState.READY,
                accountLabel = "未登录",
                subscriptionLabel = "无本地缓存",
            )
        }

        SplashUiState(
            checkingSecureBoot = false,
            versionLabel = "v${BuildConfig.VERSION_NAME}",
            buildStatus = cachedUser?.email ?: "账户同步失败",
            progress = 0.58f,
            progressHeadline = "账户服务不可用",
            progressDetail = "无法完成真实账号同步，请重试后再进入应用。",
            authResolved = false,
            readyToNavigate = false,
            loadState = P0LoadState.UNAVAILABLE,
            accountLabel = cachedUser?.email ?: "未知账号",
            subscriptionLabel = if (cachedOrders.isEmpty()) "未同步订阅" else "${cachedOrders.size} 笔本地订单缓存",
            errorMessage = failureMessage,
            unavailableMessage = "启动必须依赖真实账户状态；当前网络或后端不可用。",
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
                "当前设备已识别账号 ${cachedUser.email ?: cachedUser.username}，登录后会刷新订阅、VPN 状态和订单缓存。"
            } else {
                "使用真实 CryptoVPN 账号登录后，当前 Compose 页会同步订阅、VPN 区域和支付记录。"
            },
            successMessage = if (cachedOrders.isNotEmpty()) "检测到 ${cachedOrders.size} 笔真实订单缓存。" else null,
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
                        onSuccess = { LoginResult(success = true) },
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
        val accountLabel = me?.email ?: cachedUser?.email ?: cachedUser?.username ?: "未登录"
        if (me == null && cachedUser == null) {
            return WalletOnboardingUiState(
                accountLabel = accountLabel,
                summary = "钱包入口依赖真实登录态，请先登录再继续。",
                unavailableMessage = "当前没有可用的真实账号上下文。",
                focusedChains = listOf("SOLANA", "TRON"),
            )
        }

        return WalletOnboardingUiState(
            accountLabel = accountLabel,
            summary = if (subscription?.status == "ACTIVE") {
                "账号已开通 ${subscription.planCode ?: "订阅"}，可继续选择创建或导入钱包入口。"
            } else {
                "账号已登录，但链上钱包资料仍未建立；请选择创建或导入路径。"
            },
            warningMessage = "本页已切到真实账号上下文；后续 create/import 页仍处于钱包域建设中。",
            focusedChains = listOf("SOLANA", "TRON"),
            primaryActionLabel = "继续到钱包入口",
        )
    }

    override suspend fun getVpnHomeState(): VpnHomeUiState {
        val cachedUser = paymentRepository.getCachedCurrentUser()
        var localNodes = localServerSnapshots()
        var localSubscription = selectedLocalSubscription(localNodes)
        val lastIssuedConfigExpireAt = paymentRepository.getLastIssuedVpnConfigExpireAt()
        val lastIssuedRegionCode = paymentRepository.getLastIssuedVpnRegionCode()
        val currentOrderNo = paymentRepository.getCurrentOrderId()
        val orderSnapshot = loadOrdersSnapshot()
        val orders = orderSnapshot.orders
        val latestOrder = orders.maxByOrNull { it.createdAt }
        val activeOrder = currentOrderNo?.let { orderNo ->
            orders.firstOrNull { it.orderNo == orderNo }
        } ?: latestOrder
        var selectedLocalRegion = selectedLocalNode(localNodes)?.toRegionSpeed()
        var hasLocalConfig = !MmkvManager.getSelectServer().isNullOrEmpty()
        val localSubscriptionUrl = activeOrder?.subscriptionUrl
            ?: localSubscription?.url
            ?: paymentRepository.getSavedSubscriptionUrl()

        if (localNodes.isEmpty() && !localSubscriptionUrl.isNullOrBlank()) {
            paymentRepository.importSubscriptionUrl(
                subscriptionUrl = localSubscriptionUrl,
                remarks = activeOrder?.planName ?: "CryptoVPN Subscription",
            )
            localNodes = localServerSnapshots()
            localSubscription = selectedLocalSubscription(localNodes)
            selectedLocalRegion = selectedLocalNode(localNodes)?.toRegionSpeed()
            hasLocalConfig = !MmkvManager.getSelectServer().isNullOrEmpty()
        }

        val localSignals = buildLocalVpnSignals(
            latestOrder = activeOrder,
            localNodes = localNodes,
            localSubscription = localSubscription,
            localSubscriptionUrl = localSubscriptionUrl,
            hasLocalConfig = hasLocalConfig,
        )
        val meResult = paymentRepository.getMe()
        if (meResult.isFailure) {
            val resolvedConnectionStatus = resolveConnectionStatus(null, hasLocalConfig)
            return VpnHomeUiState(
                isLoading = false,
                loadState = if (cachedUser == null && activeOrder == null && localNodes.isEmpty()) {
                    P0LoadState.UNAVAILABLE
                } else {
                    P0LoadState.READY
                },
                accountLabel = cachedUser?.email ?: cachedUser?.username ?: "未登录",
                errorMessage = null,
                unavailableMessage = null,
                connectionStatus = resolvedConnectionStatus,
                selectedRegion = selectedLocalRegion ?: RegionSpeed(
                    regionName = "待同步",
                    protocol = "配置待同步",
                    latencyMs = 0,
                    load = "--",
                ),
                subscription = localSubscriptionSummary(
                    latestOrder = activeOrder,
                    localSubscription = localSubscription,
                    fallbackExpireAt = lastIssuedConfigExpireAt,
                ),
                autoConnectEnabled = hasLocalConfig,
                oneTapLabel = if (hasLocalConfig) "启动已导入配置" else "导入已购配置后连接",
                speedNodes = localNodes.map { it.toRegionSpeed() },
                watchSignals = localSignals,
                overviewValueText = resolveOverviewValueText(orders, orderSnapshot.syncUnavailable),
                overviewSummaryText = buildOverviewSummary(
                    orders = orders,
                    subscription = null,
                    fallbackExpireAt = lastIssuedConfigExpireAt,
                    ordersSyncUnavailable = orderSnapshot.syncUnavailable,
                ),
                alertCount = localSignals.size,
                nodeHealthPercent = calculateNodeHealthPercent(
                    localNodeCount = localNodes.size,
                    allowedRegions = emptyList(),
                ),
                vlessExpiryLabel = resolveVlessExpiryLabel(
                    subscription = null,
                    lastIssuedConfigExpireAt = lastIssuedConfigExpireAt,
                ),
                vlessRegionLabel = lastIssuedRegionCode ?: selectedLocalRegion?.regionName ?: "待同步",
                configStatusLabel = localConfigStatusLabel(
                    hasLocalConfig = hasLocalConfig,
                    localSubscriptionUrl = localSubscriptionUrl,
                    localNodeCount = localNodes.size,
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
                } ?: if (localNodes.isNotEmpty()) {
                    "已识别 ${localNodes.size} 个本地节点"
                } else {
                    resolveOrdersFallbackLabel(orderSnapshot.syncUnavailable)
                },
                canConnect = hasLocalConfig,
            )
        }

        val me = meResult.getOrThrow()
        val subscription = paymentRepository.getSubscription().getOrNull() ?: me.subscription
        val vpnStatus = paymentRepository.getVpnStatus().getOrNull()
        val connectionStatus = resolveConnectionStatus(vpnStatus, hasLocalConfig)
        val vpnRegions = paymentRepository.getVpnRegions().getOrNull().orEmpty()
        val allowedRegions = vpnRegions.filter { it.isAllowed }
        val selectedRegion = selectedLocalRegion ?: resolveSelectedRegion(allowedRegions, vpnStatus)
        val watchSignals = buildVpnSignals(
            subscription = subscription,
            vpnStatus = vpnStatus,
            latestOrder = activeOrder,
            hasLocalConfig = hasLocalConfig,
            localNodes = localNodes,
            localSubscription = localSubscription,
            localSubscriptionUrl = localSubscriptionUrl,
        )

        val loadState = when {
            localNodes.isNotEmpty() || localSubscriptionUrl != null -> P0LoadState.READY
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
                regionName = "待同步",
                protocol = "区域待同步",
                latencyMs = 0,
                load = "--",
            ),
            subscription = subscription?.toSummary() ?: SubscriptionSummary(
                planName = "待同步订阅",
                expiresInDays = 0,
                autoRenew = false,
                nextBillingLabel = "待同步",
                status = "NONE",
            ),
            autoConnectEnabled = vpnStatus?.canIssueConfig == true || hasLocalConfig,
            oneTapLabel = when {
                connectionStatus == VpnConnectionStatus.CONNECTED -> "断开并刷新状态"
                connectionStatus == VpnConnectionStatus.CONNECTING -> "连接中"
                hasLocalConfig -> "启动已导入配置"
                vpnStatus?.canIssueConfig == true -> "当前账号可签发配置"
                else -> "购买套餐后连接"
            },
            speedNodes = if (localNodes.isNotEmpty()) {
                localNodes.map { it.toRegionSpeed() }
            } else {
                allowedRegions.map { it.toRegionSpeed() }
            },
            watchSignals = watchSignals,
            overviewValueText = resolveOverviewValueText(orders, orderSnapshot.syncUnavailable),
            overviewSummaryText = buildOverviewSummary(
                orders = orders,
                subscription = subscription,
                fallbackExpireAt = lastIssuedConfigExpireAt,
                ordersSyncUnavailable = orderSnapshot.syncUnavailable,
            ),
            alertCount = watchSignals.size,
            nodeHealthPercent = calculateNodeHealthPercent(
                localNodeCount = localNodes.size,
                allowedRegions = allowedRegions,
            ),
            vlessExpiryLabel = resolveVlessExpiryLabel(
                subscription = subscription,
                lastIssuedConfigExpireAt = lastIssuedConfigExpireAt,
            ),
            vlessRegionLabel = lastIssuedRegionCode
                ?: selectedRegion?.regionName
                ?: selectedLocalRegion?.regionName
                ?: "待同步",
            configStatusLabel = localConfigStatusLabel(
                hasLocalConfig = hasLocalConfig,
                localSubscriptionUrl = localSubscriptionUrl,
                localNodeCount = localNodes.size,
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
        val cachedUser = paymentRepository.getCachedCurrentUser()
        val meResult = paymentRepository.getMe()
        if (meResult.isFailure) {
            val failureMessage = meResult.exceptionOrNull()?.message ?: "获取账号信息失败"
            return WalletHomeUiState(
                isLoading = false,
                loadState = if (cachedUser == null) P0LoadState.UNAVAILABLE else P0LoadState.ERROR,
                accountLabel = cachedUser?.email ?: cachedUser?.username ?: "未登录",
                errorMessage = failureMessage,
                unavailableMessage = if (cachedUser == null) "请先登录后再查看真实支付记录。" else null,
                totalBalanceText = "--",
                summaryLabel = "无法同步真实支付数据",
                alertBanner = "当前页面不再展示假余额；同步失败时只保留真实错误信息。",
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
                totalBalanceText = "0 笔真实订单",
                summaryLabel = subscription?.planCode ?: "暂无支付记录",
                emptyMessage = "当前账号还没有同步到真实支付记录。创建订单或完成支付后，这里会展示真实订单摘要。",
                alertBanner = "当前页面展示的是支付与订单数据，不再冒充链上钱包余额。",
            )
        }

        val chainGroups = orders.groupBy { it.quoteNetworkCode.lowercase(Locale.ROOT) }
        val chains = chainGroups.entries
            .sortedByDescending { it.value.size }
            .map { (chainId, items) ->
                WalletChainSummary(
                    chainId = chainId,
                    label = chainLabel(chainId),
                    balanceText = "${items.size} 笔订单",
                    accent = "真实支付网络",
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
            totalBalanceText = "${orders.size} 笔真实支付记录",
            summaryLabel = subscription?.let { "${it.planCode ?: "订阅"} · ${it.status}" } ?: "暂无有效订阅",
            selectedChainId = chains.firstOrNull()?.chainId ?: "all",
            chains = chains,
            assets = assets,
            alertBanner = latestOrder?.let { "最新订单：${it.planName} · ${it.statusText}" }
                ?: "当前页面展示的是当前账号的真实订单与支付网络摘要。",
        )
    }

    private suspend fun loadOrders(): List<Order> {
        paymentRepository.getCurrentOrderId()?.let { currentOrderId ->
            paymentRepository.getOrder(currentOrderId)
        }
        val currentUserId = paymentRepository.getCurrentUserId() ?: return emptyList()
        val cached = paymentRepository.getCachedOrders(currentUserId)
        return cached.map {
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

    private suspend fun loadOrdersSnapshot(): OrdersSnapshot {
        paymentRepository.getCurrentOrderId()?.let { currentOrderId ->
            paymentRepository.getOrder(currentOrderId)
        }
        val currentUserId = paymentRepository.getCurrentUserId()
            ?: return OrdersSnapshot(emptyList(), syncUnavailable = false)
        val syncResult = paymentRepository.syncOrdersFromServer(force = false, userId = currentUserId)
        val cached = paymentRepository.getLocalRepository().getOrdersByUserId(currentUserId).map {
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
        val syncUnavailable = syncResult.exceptionOrNull()?.message?.contains("订单接口待同步") == true
        return OrdersSnapshot(
            orders = cached,
            syncUnavailable = syncUnavailable && cached.isEmpty(),
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
        localNodes: List<LocalServerSnapshot>,
        localSubscription: SubscriptionItem?,
        localSubscriptionUrl: String?,
    ): List<WatchSignal> {
        val signals = mutableListOf<WatchSignal>()
        signals += WatchSignal(
            symbol = "SUB",
            reason = subscription?.let {
                "${it.planCode ?: "当前订阅"} · ${it.status}"
            } ?: "暂无有效订阅",
            changeText = subscription?.daysRemaining?.let { "剩余 ${it} 天" } ?: "等待购买",
            volumeText = subscription?.expireAt?.let(::formatDateLabel) ?: "--",
            isPositive = subscription?.status == "ACTIVE",
        )
        signals += WatchSignal(
            symbol = "VPN",
            reason = vpnStatus?.sessionStatus ?: "未同步 VPN 会话",
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
            localSubscription = localSubscription,
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
            planName = planCode ?: "待同步订阅",
            expiresInDays = daysRemaining ?: 0,
            autoRenew = false,
            nextBillingLabel = expireAt?.let(::formatDateLabel) ?: "待同步",
            status = status,
        )
    }

    private fun inferNetworkCode(assetCode: String): String {
        return if (assetCode.equals(PaymentConfig.AssetCode.SOL, ignoreCase = true)) {
            PaymentConfig.NetworkCode.SOLANA
        } else {
            PaymentConfig.NetworkCode.TRON
        }
    }

    private fun chainLabel(chainId: String): String = when (chainId.lowercase(Locale.ROOT)) {
        "tron" -> "TRON"
        "solana" -> "Solana"
        "ethereum" -> "Ethereum"
        "base" -> "Base"
        else -> chainId.uppercase(Locale.ROOT)
    }

    private fun formatAssetAmount(value: Double, assetCode: String): String {
        return String.format(Locale.US, "%.6f %s", value, assetCode)
            .replace(Regex("0+\\s"), " $assetCode")
    }

    private fun formatEpoch(epoch: Long): String =
        Instant.ofEpochMilli(epoch).toString()

    private fun formatDateLabel(date: String): String =
        runCatching { LocalDate.parse(date.take(10)).format(DateTimeFormatter.ISO_DATE) }.getOrDefault(date)

    private fun localSubscriptionSummary(
        latestOrder: Order?,
        localSubscription: SubscriptionItem?,
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
            localSubscription != null -> SubscriptionSummary(
                planName = localSubscription.remarks.ifBlank { "本地已导入订阅" },
                expiresInDays = 0,
                autoRenew = false,
                nextBillingLabel = "已导入本地订阅",
                status = "LOCAL_CONFIG",
            )
            else -> null
        }
    }

    private fun buildLocalVpnSignals(
        latestOrder: Order?,
        localNodes: List<LocalServerSnapshot>,
        localSubscription: SubscriptionItem?,
        localSubscriptionUrl: String?,
        hasLocalConfig: Boolean,
    ): List<WatchSignal> {
        val selectedNode = selectedLocalNode(localNodes)
        val signals = mutableListOf<WatchSignal>()
        selectedNode?.let {
            signals += WatchSignal(
                symbol = "NODE",
                reason = it.displayName,
                changeText = it.protocol,
                volumeText = it.latencyMs?.let { latency -> "${latency}ms" } ?: "未测速",
                isPositive = true,
            )
        }
        localSubscriptionUrl?.let { url ->
            signals += WatchSignal(
                symbol = "VLESS",
                reason = shortenSensitiveUrl(url),
                changeText = localSubscription?.remarks?.ifBlank { "已保存订阅配置" } ?: "已保存订阅配置",
                volumeText = if (hasLocalConfig) "本地配置可用" else "待导入节点",
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
            localSubscriptionUrl != null -> "已保存订阅配置"
            hasLocalConfig -> "已就绪"
            remoteIssuable -> "可签发配置"
            else -> "配置待同步"
        }
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
            LocalServerSnapshot(
                guid = guid,
                displayName = profile.remarks.ifBlank {
                    subscriptionRemarks.ifBlank { profile.server ?: guid.take(8) }
                },
                description = listOfNotNull(
                    subscriptionRemarks.takeIf { it.isNotBlank() },
                    profile.server,
                    profile.serverPort?.let { "端口 $it" },
                ).joinToString(" · ").ifBlank { "本地节点配置" },
                protocol = profile.configType.name,
                latencyMs = latency,
                selected = MmkvManager.getSelectServer() == guid,
                subscriptionId = profile.subscriptionId,
            )
        }.sortedWith(
            compareByDescending<LocalServerSnapshot> { it.selected }
                .thenBy { it.latencyMs == null }
                .thenBy { it.latencyMs ?: Int.MAX_VALUE }
                .thenBy { it.displayName },
        )
    }

    private fun selectedLocalNode(nodes: List<LocalServerSnapshot>): LocalServerSnapshot? {
        return nodes.firstOrNull { it.selected } ?: nodes.firstOrNull()
    }

    private fun selectedLocalSubscription(nodes: List<LocalServerSnapshot>): SubscriptionItem? {
        val subscriptionId = selectedLocalNode(nodes)?.subscriptionId
            ?.takeIf { it.isNotBlank() }
            ?: return null
        return MmkvManager.decodeSubscription(subscriptionId)
    }

    private fun LocalServerSnapshot.toRegionSpeed(): RegionSpeed {
        return RegionSpeed(
            regionName = displayName,
            protocol = protocol,
            latencyMs = latencyMs ?: 0,
            load = if (selected) "已选中" else description,
            regionCode = guid,
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

    private fun resolveOverviewValueText(
        orders: List<Order>,
        ordersSyncUnavailable: Boolean,
    ): String {
        return if (orders.isEmpty() && ordersSyncUnavailable) {
            "--"
        } else {
            formatUsdAmount(orders.sumOf { it.quoteUsdAmount.toDoubleOrNull() ?: 0.0 })
        }
    }

    private fun resolveOrdersFallbackLabel(ordersSyncUnavailable: Boolean): String {
        return if (ordersSyncUnavailable) "订单接口待同步" else "暂无订单"
    }

    private fun calculateNodeHealthPercent(
        localNodeCount: Int,
        allowedRegions: List<VpnRegionItem>,
    ): Int {
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

private data class LocalServerSnapshot(
    val guid: String,
    val displayName: String,
    val description: String,
    val protocol: String,
    val latencyMs: Int?,
    val selected: Boolean,
    val subscriptionId: String,
)

private data class OrdersSnapshot(
    val orders: List<Order>,
    val syncUnavailable: Boolean,
)
