package com.v2ray.ang.composeui.p0.model

enum class VpnConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
}

data class SplashUiState(
    val checkingSecureBoot: Boolean = true,
    val versionLabel: String = "v1.0.0",
    val buildStatus: String = "Verifying secure module",
    val progress: Float = 0.08f,
    val progressHeadline: String = "系统正在准备",
    val progressDetail: String = "初始化加密模块、节点探测与资产索引…",
    val authResolved: Boolean = false,
    val readyToNavigate: Boolean = false,
)

sealed interface SplashEvent {
    data object Refresh : SplashEvent
    data object Continue : SplashEvent
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val rememberMe: Boolean = true,
    val helperText: String = "Self-custody wallet + VPN, one secure shell.",
)

sealed interface LoginEvent {
    data class EmailChanged(val value: String) : LoginEvent
    data class PasswordChanged(val value: String) : LoginEvent
    data class RememberMeChanged(val value: Boolean) : LoginEvent
    data object LoginClicked : LoginEvent
}

enum class WalletCreationMode {
    CREATE,
    IMPORT,
}

data class WalletOnboardingUiState(
    val selectedMode: WalletCreationMode = WalletCreationMode.CREATE,
    val focusedChains: List<String> = listOf("ETH", "BSC", "Polygon", "Arbitrum", "Base", "Solana", "TRON"),
)

sealed interface WalletOnboardingEvent {
    data class SelectMode(val value: WalletCreationMode) : WalletOnboardingEvent
    data object ContinueClicked : WalletOnboardingEvent
}

data class SubscriptionSummary(
    val planName: String,
    val expiresInDays: Int,
    val autoRenew: Boolean,
    val nextBillingLabel: String,
)

data class RegionSpeed(
    val regionName: String,
    val protocol: String,
    val latencyMs: Int,
    val load: String,
)

data class WatchSignal(
    val symbol: String,
    val reason: String,
    val changeText: String,
    val volumeText: String,
    val isPositive: Boolean,
)

data class VpnHomeUiState(
    val connectionStatus: VpnConnectionStatus = VpnConnectionStatus.DISCONNECTED,
    val selectedRegion: RegionSpeed = RegionSpeed("Singapore - Premium", "VLESS / Reality", 48, "11% load"),
    val subscription: SubscriptionSummary = SubscriptionSummary(
        planName = "Pro Mesh 30D",
        expiresInDays = 18,
        autoRenew = true,
        nextBillingLabel = "Renews 2025-04-27",
    ),
    val autoConnectEnabled: Boolean = true,
    val oneTapLabel: String = "Quick Secure Connect",
    val speedNodes: List<RegionSpeed> = emptyList(),
    val watchSignals: List<WatchSignal> = emptyList(),
    val walletTotalLabel: String = "$24,860.42",
)

sealed interface VpnHomeEvent {
    data object ToggleConnection : VpnHomeEvent
    data object Refresh : VpnHomeEvent
    data class AutoConnectChanged(val value: Boolean) : VpnHomeEvent
    data class RegionSelected(val value: RegionSpeed) : VpnHomeEvent
}

data class WalletChainSummary(
    val chainId: String,
    val label: String,
    val balanceText: String,
    val accent: String,
)

data class AssetHolding(
    val symbol: String,
    val chainLabel: String,
    val balanceText: String,
    val valueText: String,
    val changeText: String,
    val changePositive: Boolean,
)

data class WalletHomeUiState(
    val totalBalanceText: String = "$24,860.42",
    val selectedChainId: String = "ethereum",
    val chains: List<WalletChainSummary> = emptyList(),
    val assets: List<AssetHolding> = emptyList(),
    val alertBanner: String = "2 approvals need review · 1 backup reminder",
)

sealed interface WalletHomeEvent {
    data class ChainSelected(val chainId: String) : WalletHomeEvent
    data object Refresh : WalletHomeEvent
}

data class LoginResult(
    val success: Boolean,
)
