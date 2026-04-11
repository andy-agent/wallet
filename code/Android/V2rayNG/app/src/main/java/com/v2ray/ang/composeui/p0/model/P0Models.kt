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
    val progressDetail: String = "读取本地会话、订单缓存和节点索引…",
    val authResolved: Boolean = false,
    val readyToNavigate: Boolean = false,
    val nextRoute: String? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val retryLabel: String = "重试启动检查",
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
    val statusMessage: String? = null,
    val errorMessage: String? = null,
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
    val isLoading: Boolean = false,
    val accountLabel: String = "",
    val statusMessage: String? = null,
    val errorMessage: String? = null,
    val emptyMessage: String? = null,
    val unavailableMessage: String? = null,
    val primaryActionLabel: String? = null,
)

sealed interface WalletOnboardingEvent {
    data class SelectMode(val value: WalletCreationMode) : WalletOnboardingEvent
    data object ContinueClicked : WalletOnboardingEvent
}

data class SubscriptionSummary(
    val planName: String,
    val statusLabel: String,
    val expiresInDays: Int,
    val autoRenew: Boolean,
    val nextBillingLabel: String,
    val sessionLimitLabel: String = "--",
    val canIssueConfig: Boolean = false,
)

data class RegionSpeed(
    val regionCode: String,
    val regionName: String,
    val protocol: String,
    val latencyMs: Int? = null,
    val load: String,
    val statusLabel: String = "",
    val isAllowed: Boolean = false,
    val isOnline: Boolean = false,
)

data class WatchSignal(
    val symbol: String,
    val reason: String,
    val changeText: String,
    val volumeText: String,
    val isPositive: Boolean,
)

data class VpnHomeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val emptyMessage: String? = null,
    val unavailableMessage: String? = null,
    val accountLabel: String = "",
    val connectionStatus: VpnConnectionStatus = VpnConnectionStatus.DISCONNECTED,
    val selectedRegion: RegionSpeed = RegionSpeed(
        regionCode = "",
        regionName = "暂无可用区域",
        protocol = "--",
        latencyMs = null,
        load = "--",
        statusLabel = "未就绪",
        isAllowed = false,
        isOnline = false,
    ),
    val subscription: SubscriptionSummary = SubscriptionSummary(
        planName = "暂无有效订阅",
        statusLabel = "NONE",
        expiresInDays = 18,
        autoRenew = true,
        nextBillingLabel = "Renews 2025-04-27",
        sessionLimitLabel = "--",
        canIssueConfig = false,
    ),
    val autoConnectEnabled: Boolean = true,
    val oneTapLabel: String = "Quick Secure Connect",
    val speedNodes: List<RegionSpeed> = emptyList(),
    val watchSignals: List<WatchSignal> = emptyList(),
    val importedConfigCount: Int = 0,
    val availableRegionCount: Int = 0,
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
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val emptyMessage: String? = null,
    val unavailableMessage: String? = null,
    val totalBalanceText: String = "0 个已绑定地址",
    val selectedChainId: String = "all",
    val chains: List<WalletChainSummary> = emptyList(),
    val assets: List<AssetHolding> = emptyList(),
    val alertBanner: String = "尚未读取真实钱包公开地址",
    val accountLabel: String = "",
    val defaultAddressCount: Int = 0,
    val supportedRailCount: Int = 0,
)

sealed interface WalletHomeEvent {
    data class ChainSelected(val chainId: String) : WalletHomeEvent
    data object Refresh : WalletHomeEvent
}

data class LoginResult(
    val success: Boolean,
    val message: String? = null,
)

data class CodeRequestResult(
    val success: Boolean,
    val message: String? = null,
)

data class SubmitResult(
    val success: Boolean,
    val message: String? = null,
)
