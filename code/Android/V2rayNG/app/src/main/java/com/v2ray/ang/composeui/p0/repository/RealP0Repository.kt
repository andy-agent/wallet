package com.v2ray.ang.composeui.p0.repository

import android.content.Context
import com.v2ray.ang.BuildConfig
import com.v2ray.ang.composeui.p0.model.AssetHolding
import com.v2ray.ang.composeui.p0.model.LoginResult
import com.v2ray.ang.composeui.p0.model.LoginUiState
import com.v2ray.ang.composeui.p0.model.RegionSpeed
import com.v2ray.ang.composeui.p0.model.SplashUiState
import com.v2ray.ang.composeui.p0.model.SubscriptionSummary
import com.v2ray.ang.composeui.p0.model.VpnConnectionStatus
import com.v2ray.ang.composeui.p0.model.VpnHomeUiState
import com.v2ray.ang.composeui.p0.model.WalletChainSummary
import com.v2ray.ang.composeui.p0.model.WalletHomeUiState
import com.v2ray.ang.composeui.p0.model.WalletOnboardingUiState
import com.v2ray.ang.composeui.p0.model.WatchSignal
import com.v2ray.ang.handler.V2RayServiceManager
import com.v2ray.ang.handler.MmkvManager
import com.v2ray.ang.payment.PaymentConfig
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

    override suspend fun getSplashState(): SplashUiState {
        val cachedUser = paymentRepository.getCachedCurrentUser()
        val currentUserId = paymentRepository.getCurrentUserId()
        val cachedOrders = currentUserId?.let { paymentRepository.getCachedOrders(it) }.orEmpty()
        val sessionLabel = if (paymentRepository.isTokenValid()) "已识别登录会话" else "未登录"
        return SplashUiState(
            checkingSecureBoot = false,
            versionLabel = "v${BuildConfig.VERSION_NAME}",
            buildStatus = buildString {
                append(sessionLabel)
                cachedUser?.let {
                    append(" · ")
                    append(it.username)
                }
                if (cachedOrders.isNotEmpty()) {
                    append(" · ")
                    append(cachedOrders.size)
                    append(" 条本地订单缓存")
                }
            },
            progress = 0.12f,
            progressHeadline = "连接钱包与网络",
            progressDetail = "初始化加密模块、节点探测与资产索引…",
            authResolved = false,
            readyToNavigate = false,
        )
    }

    override suspend fun getLoginSeed(): LoginUiState {
        val cachedUser = paymentRepository.getCachedCurrentUser()
        return LoginUiState(
            email = cachedUser?.email ?: cachedUser?.username.orEmpty(),
            rememberMe = paymentRepository.isTokenValid(),
            helperText = if (cachedUser != null) {
                "Detected cached account ${cachedUser.username}. Continue to refresh VPN subscription and wallet shell."
            } else {
                "Use your real CryptoVPN account to unlock subscription, orders, wallet and invite data."
            },
        )
    }

    override suspend fun login(email: String, password: String): LoginResult {
        if (email.isBlank() || password.isBlank()) {
            return LoginResult(success = false)
        }
        return withContext(Dispatchers.IO) {
            val response = paymentRepository.api.login(
                com.v2ray.ang.payment.data.model.LoginRequest(
                    email = email,
                    password = password,
                ),
            )
            val auth = response.body()?.data
            if (response.isSuccessful && response.body()?.code == "OK" && auth != null) {
                paymentRepository.saveAuthResponse(auth)
                paymentRepository.saveCurrentUserId(auth.userId)
                paymentRepository.getLocalRepository().saveUser(
                    com.v2ray.ang.payment.data.local.entity.UserEntity(
                        userId = auth.userId,
                        username = email,
                        email = email,
                        accessToken = auth.accessToken,
                        refreshToken = auth.refreshToken,
                        loginAt = System.currentTimeMillis(),
                    ),
                )
                LoginResult(success = true)
            } else {
                LoginResult(success = false)
            }
        }
    }

    override suspend fun getWalletOnboardingState(): WalletOnboardingUiState {
        return WalletOnboardingUiState()
    }

    override suspend fun getVpnHomeState(): VpnHomeUiState {
        val me = paymentRepository.getMe().getOrNull()
        val subscription = paymentRepository.getSubscription().getOrNull() ?: me?.subscription
        val orders = loadOrders()
        val latestOrder = orders.maxByOrNull { it.createdAt }
        val regions = localRegions()
        val selectedRegion = regions.firstOrNull()
            ?: RegionSpeed(
                regionName = "暂无本地节点",
                protocol = "等待导入或订阅同步",
                latencyMs = 0,
                load = "空态",
            )

        return VpnHomeUiState(
            connectionStatus = if (V2RayServiceManager.isRunning()) {
                VpnConnectionStatus.CONNECTED
            } else {
                VpnConnectionStatus.DISCONNECTED
            },
            selectedRegion = selectedRegion,
            subscription = SubscriptionSummary(
                planName = subscription?.planCode ?: latestOrder?.planName ?: "暂无有效订阅",
                expiresInDays = subscription?.daysRemaining ?: 0,
                autoRenew = false,
                nextBillingLabel = subscription?.expireAt?.let(::formatDateLabel) ?: "等待续费",
            ),
            autoConnectEnabled = paymentRepository.isTokenValid(),
            oneTapLabel = if (V2RayServiceManager.isRunning()) "VPN tunnel active" else "Connect and secure",
            speedNodes = regions,
            watchSignals = buildWatchSignals(orders),
            walletTotalLabel = orders.sumOf { it.payment.amountCrypto.toDoubleOrNull() ?: 0.0 }
                .let { "$" + String.format(Locale.US, "%.2f", it) },
        )
    }

    override suspend fun getWalletHomeState(): WalletHomeUiState {
        val orders = loadOrders()
        val currentUser = paymentRepository.getCachedCurrentUser()
        val assetCodeTotals = orders.groupBy { it.quoteAssetCode }
            .mapValues { (_, items) -> items.sumOf { it.payment.amountCrypto.toDoubleOrNull() ?: 0.0 } }

        val chainTotals = orders.groupBy { it.quoteNetworkCode.lowercase(Locale.ROOT) }
            .mapValues { (_, items) -> items.sumOf { it.payment.amountCrypto.toDoubleOrNull() ?: 0.0 } }

        val chains = chainTotals.entries
            .sortedByDescending { it.value }
            .map { (chainId, total) ->
                WalletChainSummary(
                    chainId = chainId,
                    label = chainLabel(chainId),
                    balanceText = formatMoney(total),
                    accent = chainAccent(chainId),
                )
            }

        val assets = assetCodeTotals.entries.sortedByDescending { it.value }.map { (asset, amount) ->
            AssetHolding(
                symbol = asset,
                chainLabel = orders.firstOrNull { it.quoteAssetCode == asset }?.quoteNetworkCode ?: "--",
                balanceText = String.format(Locale.US, "%.2f %s", amount, asset),
                valueText = formatMoney(amount),
                changeText = orders.count { it.quoteAssetCode == asset }.toString() + " orders",
                changePositive = true,
            )
        }

        return WalletHomeUiState(
            totalBalanceText = formatMoney(assetCodeTotals.values.sum()),
            selectedChainId = chains.firstOrNull()?.chainId ?: "",
            chains = chains,
            assets = assets,
            alertBanner = currentUser?.let { "Account ${it.username} · ${orders.size} 笔订单缓存" }
                ?: "当前未缓存账号",
        )
    }

    private suspend fun loadOrders(): List<Order> {
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
                quoteNetworkCode = inferNetworkCode(it.assetCode),
                quoteUsdAmount = it.amount,
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

    private fun localRegions(): List<RegionSpeed> {
        return MmkvManager.decodeAllServerList().mapNotNull { guid ->
            val profile = MmkvManager.decodeServerConfig(guid) ?: return@mapNotNull null
            val latency = MmkvManager.decodeServerAffiliationInfo(guid)
                ?.testDelayMillis
                ?.takeIf { it > 0L }
                ?.toInt()
                ?: 0
            RegionSpeed(
                regionName = profile.remarks.ifBlank { profile.server ?: guid.take(8) },
                protocol = profile.configType.name,
                latencyMs = latency,
                load = if (latency > 0) "${latency}ms" else "未测速",
            )
        }.sortedBy { if (it.latencyMs > 0) it.latencyMs else Int.MAX_VALUE }
    }

    private fun buildWatchSignals(orders: List<Order>): List<WatchSignal> {
        if (orders.isEmpty()) {
            return listOf(
                WatchSignal("USDT", "No real wallet flow yet", "0.0%", "$0", true),
            )
        }
        return orders.take(3).mapIndexed { index, order ->
            WatchSignal(
                symbol = order.quoteAssetCode,
                reason = "${order.planName} · ${order.statusText}",
                changeText = if (index == 0) "+1.0%" else "0.0%",
                volumeText = order.payment.amountCrypto,
                isPositive = order.status !in setOf("FAILED", "EXPIRED", "CANCELED"),
            )
        }
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

    private fun chainAccent(chainId: String): String = when (chainId.lowercase(Locale.ROOT)) {
        "tron" -> "来自真实订单支付网络"
        "solana" -> "来自真实订单支付网络"
        else -> "来自本地资产统计"
    }

    private fun formatMoney(value: Double): String = "$" + String.format(Locale.US, "%.2f", value)

    private fun formatEpoch(epoch: Long): String =
        Instant.ofEpochMilli(epoch).toString()

    private fun formatDateLabel(date: String): String =
        runCatching { LocalDate.parse(date.take(10)).format(DateTimeFormatter.ISO_DATE) }.getOrDefault(date)
}
