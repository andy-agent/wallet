package com.v2ray.ang.composeui.p0.model

enum class P0LoadState {
    LOADING,
    READY,
    EMPTY,
    ERROR,
    UNAVAILABLE,
}

enum class VpnConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
}

data class SplashUiState(
    val checkingSecureBoot: Boolean = true,
    val versionLabel: String = "",
    val buildStatus: String = "正在检查登录状态",
    val progress: Float = 0.08f,
    val progressHeadline: String = "系统正在准备",
    val progressDetail: String = "正在读取账号、订单、节点和钱包状态。",
    val authResolved: Boolean = false,
    val readyToNavigate: Boolean = false,
    val nextRoute: String = "",
    val loadState: P0LoadState = P0LoadState.LOADING,
    val accountLabel: String = "未识别账号",
    val subscriptionLabel: String = "未同步订阅",
    val errorMessage: String? = null,
    val unavailableMessage: String? = null,
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
    val errorMessage: String? = null,
    val unavailableMessage: String? = null,
    val successMessage: String? = null,
    val dialogTitle: String? = null,
    val dialogMessage: String? = null,
)

sealed interface LoginEvent {
    data class EmailChanged(val value: String) : LoginEvent
    data class PasswordChanged(val value: String) : LoginEvent
    data class RememberMeChanged(val value: Boolean) : LoginEvent
    data object LoginClicked : LoginEvent
    data object DialogDismissed : LoginEvent
}

enum class WalletCreationMode {
    CREATE,
    IMPORT,
}

data class WalletOnboardingUiState(
    val selectedMode: WalletCreationMode = WalletCreationMode.CREATE,
    val focusedChains: List<String> = listOf("ETH", "BSC", "Polygon", "Arbitrum", "Base", "Solana", "TRON"),
    val accountLabel: String = "未登录",
    val summary: String = "",
    val warningMessage: String? = null,
    val unavailableMessage: String? = null,
    val primaryActionLabel: String = "继续",
    val walletExists: Boolean = false,
    val lifecycleStatus: String = "NOT_CREATED",
    val walletId: String? = null,
    val walletDisplayName: String? = null,
    val walletNextAction: String = "CREATE_OR_IMPORT",
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
    val status: String = "NONE",
)

data class RegionSpeed(
    val regionName: String,
    val protocol: String,
    val latencyMs: Int,
    val load: String,
    val regionCode: String = "",
    val isAllowed: Boolean = true,
)

data class WatchSignal(
    val symbol: String,
    val reason: String,
    val changeText: String,
    val volumeText: String,
    val isPositive: Boolean,
)

data class VpnHomeUiState(
    val isLoading: Boolean = true,
    val loadState: P0LoadState = P0LoadState.LOADING,
    val errorMessage: String? = null,
    val unavailableMessage: String? = null,
    val emptyMessage: String? = null,
    val connectionStatus: VpnConnectionStatus = VpnConnectionStatus.DISCONNECTED,
    val accountLabel: String = "未登录",
    val selectedRegion: RegionSpeed = RegionSpeed("未获取区域", "节点状态未返回", 0, "--"),
    val subscription: SubscriptionSummary? = null,
    val autoConnectEnabled: Boolean = false,
    val oneTapLabel: String = "等待状态返回",
    val speedNodes: List<RegionSpeed> = emptyList(),
    val watchSignals: List<WatchSignal> = emptyList(),
    val overviewValueText: String = "$0.00",
    val overviewSummaryText: String = "暂无订单与节点信息",
    val alertCount: Int = 0,
    val nodeHealthPercent: Int = 0,
    val vlessExpiryLabel: String = "待同步",
    val vlessRegionLabel: String = "未签发",
    val configStatusLabel: String = "未同步配置状态",
    val latestOrderLabel: String = "暂无订单",
    val canConnect: Boolean = false,
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
    val itemCount: Int = 0,
)

data class WalletHomeChainOption(
    val chainId: String,
    val label: String,
    val address: String,
    val addressSuffix: String,
)

data class WalletHomeWalletOption(
    val walletId: String,
    val walletName: String,
    val walletKind: String,
    val isDefault: Boolean,
    val chainOptions: List<WalletHomeChainOption>,
)

data class AssetHolding(
    val tokenKey: String,
    val symbol: String,
    val chainLabel: String,
    val balanceText: String,
    val valueText: String,
    val changeText: String,
    val changePositive: Boolean,
    val detailText: String = "",
    val customTokenId: String? = null,
    val isCustom: Boolean = false,
    val iconLocalPath: String? = null,
    val iconUrl: String? = null,
)

data class WalletHomeUiState(
    val isLoading: Boolean = true,
    val loadState: P0LoadState = P0LoadState.LOADING,
    val errorMessage: String? = null,
    val unavailableMessage: String? = null,
    val emptyMessage: String? = null,
    val totalBalanceText: String = "--",
    val summaryLabel: String = "支付与账户摘要",
    val selectedChainId: String = "all",
    val selectedWalletId: String? = null,
    val chains: List<WalletChainSummary> = emptyList(),
    val walletOptions: List<WalletHomeWalletOption> = emptyList(),
    val assets: List<AssetHolding> = emptyList(),
    val alertBanner: String = "",
    val accountLabel: String = "未登录",
    val accountSecondaryLabel: String = "",
    val currentWalletLabel: String = "未选择钱包",
    val currentWalletAddress: String = "",
    val currentWalletAddressSuffix: String = "",
    val currentWalletChainLabel: String = "",
    val walletExists: Boolean = false,
    val walletLifecycleStatus: String = "NOT_CREATED",
    val walletId: String? = null,
    val walletDisplayName: String? = null,
    val walletNextAction: String = "CREATE_OR_IMPORT",
)

sealed interface WalletHomeEvent {
    data class ChainSelected(val chainId: String) : WalletHomeEvent
    data class WalletContextSelected(
        val walletId: String,
        val chainId: String,
    ) : WalletHomeEvent
    data object Refresh : WalletHomeEvent
}

data class LoginResult(
    val success: Boolean,
    val errorMessage: String? = null,
    val unavailable: Boolean = false,
    val nextRoute: String? = null,
)

fun splashPreviewState(): SplashUiState = SplashUiState(
    checkingSecureBoot = false,
    versionLabel = "v2.4.0",
    buildStatus = "本地安全模块和缓存已就绪",
    progress = 0.12f,
    progressHeadline = "连接钱包与网络",
    progressDetail = "初始化加密模块、节点探测与资产索引…",
    authResolved = false,
    readyToNavigate = false,
)

fun loginPreviewState(): LoginUiState = LoginUiState(
    helperText = "VPN 订阅、多链钱包与安全切换聚合在同一个壳里。",
)

fun walletOnboardingPreviewState(): WalletOnboardingUiState = WalletOnboardingUiState()

fun vpnHomePreviewState(): VpnHomeUiState = VpnHomeUiState(
    isLoading = false,
    loadState = P0LoadState.READY,
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

fun walletHomePreviewState(): WalletHomeUiState = WalletHomeUiState(
    isLoading = false,
    loadState = P0LoadState.READY,
    selectedWalletId = "wallet-main",
    chains = listOf(
        WalletChainSummary("ethereum", "ETH", "$8,920.22", "Main execution layer"),
        WalletChainSummary("bsc", "BSC", "$2,218.10", "Gas-efficient trading"),
        WalletChainSummary("polygon", "Polygon", "$1,604.12", "Stable consumer flows"),
        WalletChainSummary("arbitrum", "Arbitrum", "$4,412.18", "Fast rollup liquidity"),
        WalletChainSummary("base", "Base", "$2,986.60", "Coinbase ecosystem"),
        WalletChainSummary("solana", "Solana", "$3,905.11", "Fast payment rail"),
        WalletChainSummary("tron", "TRON", "$813.09", "USDT transfer lane"),
    ),
    walletOptions = listOf(
        WalletHomeWalletOption(
            walletId = "wallet-main",
            walletName = "Main Wallet",
            walletKind = "SELF_CUSTODY",
            isDefault = true,
            chainOptions = listOf(
                WalletHomeChainOption("solana", "Solana", "7YttLkHDo1B4ezgm6KPDLJrVN6a8GN28AL5soMgqd7qV", "7qvV"),
                WalletHomeChainOption("tron", "TRON", "TLa2f6VPqDgRE67v1736s7bJ8Ray5wYjU7", "YjU7"),
            ),
        ),
        WalletHomeWalletOption(
            walletId = "wallet-watch",
            walletName = "Watch Wallet",
            walletKind = "WATCH_ONLY",
            isDefault = false,
            chainOptions = listOf(
                WalletHomeChainOption("tron", "TRON", "TLa2f6VPqDgRE67v1736s7bJ8Ray5wYjU7", "YjU7"),
            ),
        ),
    ),
    assets = listOf(
        AssetHolding("ethereum:native:ETH", "ETH", "Ethereum", "2.84 ETH", "$9,214.80", "+2.4%", true),
        AssetHolding("tron:native:USDT", "USDT", "TRON", "12,450 USDT", "$12,450.00", "0.0%", true),
        AssetHolding("polygon:native:MATIC", "MATIC", "Polygon", "1,202 MATIC", "$1,103.24", "+6.1%", true),
        AssetHolding("arbitrum:native:ARB", "ARB", "Arbitrum", "856 ARB", "$1,202.84", "-3.4%", false),
        AssetHolding("solana:native:SOL", "SOL", "Solana", "9.8 SOL", "$1,860.44", "+4.7%", true),
        AssetHolding("bsc:native:BNB", "BNB", "BSC", "1.2 BNB", "$685.10", "+1.3%", true),
    ),
    currentWalletLabel = "Main Wallet",
    currentWalletAddress = "7YttLkHDo1B4ezgm6KPDLJrVN6a8GN28AL5soMgqd7qV",
    currentWalletAddressSuffix = "7qvV",
    currentWalletChainLabel = "Solana",
    accountSecondaryLabel = "system@cnyirui.cn",
)
