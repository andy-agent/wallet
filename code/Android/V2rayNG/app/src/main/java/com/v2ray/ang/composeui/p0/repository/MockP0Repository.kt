package com.v2ray.ang.composeui.p0.repository

import com.v2ray.ang.composeui.p0.model.AssetHolding
import com.v2ray.ang.composeui.p0.model.CodeRequestResult
import com.v2ray.ang.composeui.p0.model.LoginResult
import com.v2ray.ang.composeui.p0.model.LoginUiState
import com.v2ray.ang.composeui.p0.model.RegionSpeed
import com.v2ray.ang.composeui.p0.model.SplashUiState
import com.v2ray.ang.composeui.p0.model.SubmitResult
import com.v2ray.ang.composeui.p0.model.SubscriptionSummary
import com.v2ray.ang.composeui.p0.model.VpnConnectionStatus
import com.v2ray.ang.composeui.p0.model.VpnHomeUiState
import com.v2ray.ang.composeui.p0.model.WalletCreationMode
import com.v2ray.ang.composeui.p0.model.WalletChainSummary
import com.v2ray.ang.composeui.p0.model.WalletHomeUiState
import com.v2ray.ang.composeui.p0.model.WalletOnboardingUiState
import com.v2ray.ang.composeui.p0.model.WatchSignal
import kotlinx.coroutines.delay

class MockP0Repository : P0Repository {
    override suspend fun getSplashState(): SplashUiState {
        delay(600)
        return SplashUiState(
            checkingSecureBoot = false,
            versionLabel = "v2.4.0",
            buildStatus = "本地安全模块和演示缓存已就绪",
            progress = 0.12f,
            progressHeadline = "连接钱包与网络",
            progressDetail = "初始化加密模块、节点探测与资产索引…",
            authResolved = false,
            readyToNavigate = false,
        )
    }

    override suspend fun getLoginSeed(): LoginUiState {
        return LoginUiState(
            helperText = "VPN subscriptions, multichain wallet, and secure switching in one shell.",
        )
    }

    override suspend fun login(email: String, password: String): LoginResult {
        delay(800)
        return LoginResult(
            success = email.isNotBlank() && password.length >= 6,
            message = if (email.isNotBlank() && password.length >= 6) "模拟登录成功" else "模拟登录失败",
        )
    }

    override suspend fun requestRegisterCode(email: String): CodeRequestResult {
        delay(400)
        return CodeRequestResult(success = email.isNotBlank(), message = if (email.isNotBlank()) "模拟验证码已发送" else "邮箱不能为空")
    }

    override suspend fun register(email: String, password: String, code: String): SubmitResult {
        delay(800)
        return SubmitResult(
            success = email.isNotBlank() && password.length >= 6 && code.isNotBlank(),
            message = if (email.isNotBlank() && password.length >= 6 && code.isNotBlank()) "模拟注册成功" else "模拟注册失败",
        )
    }

    override suspend fun requestResetCode(email: String): CodeRequestResult {
        delay(400)
        return CodeRequestResult(success = email.isNotBlank(), message = if (email.isNotBlank()) "模拟重置验证码已发送" else "邮箱不能为空")
    }

    override suspend fun resetPassword(email: String, code: String, newPassword: String): SubmitResult {
        delay(800)
        return SubmitResult(
            success = email.isNotBlank() && code.isNotBlank() && newPassword.length >= 6,
            message = if (email.isNotBlank() && code.isNotBlank() && newPassword.length >= 6) "模拟密码重置成功" else "模拟密码重置失败",
        )
    }

    override suspend fun getWalletOnboardingState(selectedMode: WalletCreationMode?): WalletOnboardingUiState {
        return WalletOnboardingUiState(selectedMode = selectedMode ?: WalletCreationMode.CREATE)
    }

    override suspend fun getVpnHomeState(selectedRegionCode: String?): VpnHomeUiState {
        val regions = listOf(
            RegionSpeed("sg", "Singapore - Premium", "VLESS / Reality", 48, "11% load", "ONLINE", true, true),
            RegionSpeed("jp", "Tokyo - Ultra", "XTLS / Vision", 61, "18% load", "ONLINE", true, true),
            RegionSpeed("de", "Frankfurt - Mesh", "VLESS / TCP", 118, "27% load", "ONLINE", true, true),
        )
        val selected = regions.firstOrNull { it.regionCode == selectedRegionCode } ?: regions.first()
        return VpnHomeUiState(
            isLoading = false,
            accountLabel = "mock-user",
            connectionStatus = VpnConnectionStatus.CONNECTED,
            selectedRegion = selected,
            subscription = SubscriptionSummary(
                planName = "Mock Plan",
                statusLabel = "ACTIVE",
                expiresInDays = 30,
                autoRenew = false,
                nextBillingLabel = "2026-05-11",
                sessionLimitLabel = "1",
                canIssueConfig = true,
            ),
            speedNodes = regions,
            watchSignals = listOf(
                WatchSignal("ENJ", "Unusual inflow on tracked pairs", "+44.1%", "$246M", true),
                WatchSignal("SOL", "Fast volume rotation before pullback", "-12.3%", "$310M", false),
                WatchSignal("ARB", "Volatility spike on perp books", "+18.6%", "$132M", true),
            ),
            importedConfigCount = 3,
            availableRegionCount = regions.count { it.isAllowed },
        )
    }

    override suspend fun getWalletHomeState(selectedChainId: String?): WalletHomeUiState {
        val chains = listOf(
            WalletChainSummary("ethereum", "ETH", "$8,920.22", "Main execution layer"),
            WalletChainSummary("bsc", "BSC", "$2,218.10", "Gas-efficient trading"),
            WalletChainSummary("polygon", "Polygon", "$1,604.12", "Stable consumer flows"),
            WalletChainSummary("arbitrum", "Arbitrum", "$4,412.18", "Fast rollup liquidity"),
            WalletChainSummary("base", "Base", "$2,986.60", "Coinbase ecosystem"),
            WalletChainSummary("solana", "Solana", "$3,905.11", "Fast payment rail"),
            WalletChainSummary("tron", "TRON", "$813.09", "USDT transfer lane"),
        )
        return WalletHomeUiState(
            isLoading = false,
            selectedChainId = selectedChainId ?: "all",
            chains = chains,
            assets = listOf(
                AssetHolding("ETH", "Ethereum", "2.84 ETH", "$9,214.80", "+2.4%", true),
                AssetHolding("USDT", "TRON", "12,450 USDT", "$12,450.00", "0.0%", true),
                AssetHolding("MATIC", "Polygon", "1,202 MATIC", "$1,103.24", "+6.1%", true),
                AssetHolding("ARB", "Arbitrum", "856 ARB", "$1,202.84", "-3.4%", false),
                AssetHolding("SOL", "Solana", "9.8 SOL", "$1,860.44", "+4.7%", true),
                AssetHolding("BNB", "BSC", "1.2 BNB", "$685.10", "+1.3%", true),
            ),
            accountLabel = "mock-user",
            defaultAddressCount = 1,
            supportedRailCount = chains.size,
        )
    }
}
