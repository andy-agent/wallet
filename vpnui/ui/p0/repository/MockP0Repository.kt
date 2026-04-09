package com.cryptovpn.ui.p0.repository

import com.cryptovpn.ui.p0.model.AssetHolding
import com.cryptovpn.ui.p0.model.LoginResult
import com.cryptovpn.ui.p0.model.LoginUiState
import com.cryptovpn.ui.p0.model.RegionSpeed
import com.cryptovpn.ui.p0.model.SplashUiState
import com.cryptovpn.ui.p0.model.VpnConnectionStatus
import com.cryptovpn.ui.p0.model.VpnHomeUiState
import com.cryptovpn.ui.p0.model.WalletChainSummary
import com.cryptovpn.ui.p0.model.WalletHomeUiState
import com.cryptovpn.ui.p0.model.WalletOnboardingUiState
import com.cryptovpn.ui.p0.model.WatchSignal
import kotlinx.coroutines.delay

class MockP0Repository : P0Repository {
    override suspend fun getSplashState(): SplashUiState {
        delay(600)
        return SplashUiState(
            checkingSecureBoot = false,
            versionLabel = "v2.4.0",
            buildStatus = "White-tech shell ready · secure modules online",
            authResolved = true,
        )
    }

    override suspend fun getLoginSeed(): LoginUiState {
        return LoginUiState(
            helperText = "VPN subscriptions, multichain wallet, and secure switching in one shell.",
        )
    }

    override suspend fun login(email: String, password: String): LoginResult {
        delay(800)
        return LoginResult(success = email.isNotBlank() && password.length >= 6)
    }

    override suspend fun getWalletOnboardingState(): WalletOnboardingUiState {
        return WalletOnboardingUiState()
    }

    override suspend fun getVpnHomeState(): VpnHomeUiState {
        return VpnHomeUiState(
            connectionStatus = VpnConnectionStatus.CONNECTED,
            speedNodes = listOf(
                RegionSpeed("Singapore - Premium", "VLESS / Reality", 48, "11% load"),
                RegionSpeed("Tokyo - Ultra", "XTLS / Vision", 61, "18% load"),
                RegionSpeed("Frankfurt - Mesh", "VLESS / TCP", 118, "27% load"),
            ),
            watchSignals = listOf(
                WatchSignal("ENJ", "Unusual inflow on tracked pairs", "+44.1%", "$246M", true),
                WatchSignal("SOL", "Fast volume rotation before pullback", "-12.3%", "$310M", false),
                WatchSignal("ARB", "Volatility spike on perp books", "+18.6%", "$132M", true),
            ),
        )
    }

    override suspend fun getWalletHomeState(): WalletHomeUiState {
        return WalletHomeUiState(
            chains = listOf(
                WalletChainSummary("ethereum", "ETH", "$8,920.22", "Main execution layer"),
                WalletChainSummary("bsc", "BSC", "$2,218.10", "Gas-efficient trading"),
                WalletChainSummary("polygon", "Polygon", "$1,604.12", "Stable consumer flows"),
                WalletChainSummary("arbitrum", "Arbitrum", "$4,412.18", "Fast rollup liquidity"),
                WalletChainSummary("base", "Base", "$2,986.60", "Coinbase ecosystem"),
                WalletChainSummary("solana", "Solana", "$3,905.11", "Fast payment rail"),
                WalletChainSummary("tron", "TRON", "$813.09", "USDT transfer lane"),
            ),
            assets = listOf(
                AssetHolding("ETH", "Ethereum", "2.84 ETH", "$9,214.80", "+2.4%", true),
                AssetHolding("USDT", "TRON", "12,450 USDT", "$12,450.00", "0.0%", true),
                AssetHolding("MATIC", "Polygon", "1,202 MATIC", "$1,103.24", "+6.1%", true),
                AssetHolding("ARB", "Arbitrum", "856 ARB", "$1,202.84", "-3.4%", false),
                AssetHolding("SOL", "Solana", "9.8 SOL", "$1,860.44", "+4.7%", true),
                AssetHolding("BNB", "BSC", "1.2 BNB", "$685.10", "+1.3%", true),
            ),
        )
    }
}
