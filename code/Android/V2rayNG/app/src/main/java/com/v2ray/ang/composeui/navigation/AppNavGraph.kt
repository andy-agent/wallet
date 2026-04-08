package com.v2ray.ang.composeui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.v2ray.ang.AppConfig
import com.v2ray.ang.composeui.bridge.auth.ComposeAuthBridge
import com.v2ray.ang.composeui.pages.auth.EmailLoginPage
import com.v2ray.ang.composeui.pages.auth.EmailRegisterPage
import com.v2ray.ang.composeui.pages.auth.ResetPasswordPage
import com.v2ray.ang.composeui.pages.growth.CommissionLedgerPage
import com.v2ray.ang.composeui.pages.growth.InviteCenterPage
import com.v2ray.ang.composeui.pages.growth.WithdrawPage
import com.v2ray.ang.composeui.pages.legal.LegalDocumentDetailPage
import com.v2ray.ang.composeui.pages.legal.LegalDocumentsListPage
import com.v2ray.ang.composeui.pages.profile.ProfilePage
import com.v2ray.ang.composeui.pages.splash.ComposeUpdateBridge
import com.v2ray.ang.composeui.pages.splash.ForceUpdatePage
import com.v2ray.ang.composeui.pages.splash.OptionalUpdateDialog
import com.v2ray.ang.composeui.pages.splash.SplashScreen
import com.v2ray.ang.composeui.pages.vpn.OrderCheckoutPage
import com.v2ray.ang.composeui.pages.vpn.OrderCheckoutState
import com.v2ray.ang.composeui.pages.vpn.OrderCheckoutViewModel
import com.v2ray.ang.composeui.pages.vpn.OrderDetailPage
import com.v2ray.ang.composeui.pages.vpn.OrderListPage
import com.v2ray.ang.composeui.pages.vpn.OrderResultPage
import com.v2ray.ang.composeui.pages.vpn.OrderResultType
import com.v2ray.ang.composeui.pages.vpn.PlansPage
import com.v2ray.ang.composeui.pages.vpn.RegionSelectionPage
import com.v2ray.ang.composeui.pages.vpn.VPNHomePage
import com.v2ray.ang.composeui.pages.vpn.WalletPaymentConfirmPage
import com.v2ray.ang.composeui.pages.wallet.AssetDetailPage
import com.v2ray.ang.composeui.pages.wallet.ReceivePage
import com.v2ray.ang.composeui.pages.wallet.SendPage
import com.v2ray.ang.composeui.pages.wallet.SendResultPage
import com.v2ray.ang.composeui.pages.wallet.SendResultType
import com.v2ray.ang.composeui.pages.wallet.WalletHomePage
import com.v2ray.ang.composeui.pages.wallet.WalletOnboardingPage
import com.v2ray.ang.composeui.pages.wallet.WalletPaymentConfirmPage2
import com.v2ray.ang.ui.compose.BitgetAppShell
import kotlinx.coroutines.launch

enum class LegacyDestination {
    SETTINGS,
    ABOUT,
    SUPPORT,
}

@Composable
fun AppNavGraph(
    authBridge: ComposeAuthBridge,
    updateBridge: ComposeUpdateBridge,
    onOpenUrl: (String) -> Unit,
    onOpenLegacyDestination: (LegacyDestination) -> Unit = {},
    onExitApp: () -> Unit,
    onAuthSuccess: () -> Unit,
    startDestination: String = Routes.SPLASH,
    onOpenSettings: () -> Unit = {},
    onOpenAbout: () -> Unit = {},
    onOpenSupport: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    var backStack by rememberSaveable {
        mutableStateOf(listOf(Routes.normalize(startDestination)))
    }
    var hasActiveSession by rememberSaveable { mutableStateOf(authBridge.hasActiveSession()) }
    var pendingPostAuthRoute by rememberSaveable { mutableStateOf<String?>(null) }
    var showOptionalUpdate by rememberSaveable { mutableStateOf(false) }
    var optionalUpdateVersion by rememberSaveable { mutableStateOf("新版本") }
    var optionalUpdateUrl by rememberSaveable { mutableStateOf<String?>(null) }

    val currentRoute = backStack.lastOrNull() ?: Routes.SPLASH

    fun navigateTo(route: String, clearStack: Boolean = false) {
        val normalized = Routes.normalize(route)
        backStack = when {
            clearStack -> listOf(normalized)
            backStack.lastOrNull() == normalized -> backStack
            else -> backStack + normalized
        }
    }

    fun navigateBack() {
        backStack = if (backStack.size > 1) {
            backStack.dropLast(1)
        } else {
            onExitApp()
            backStack
        }
    }

    fun resolvePostSplashRoute(): String {
        return Routes.appShell()
    }

    fun navigateToShell(
        tab: ShellTab = ShellTab.HOME,
        clearStack: Boolean = false,
    ) {
        navigateTo(Routes.appShell(tab), clearStack = clearStack)
    }

    fun navigateAuthenticated(
        route: String,
        clearStack: Boolean = false,
    ) {
        if (hasActiveSession) {
            pendingPostAuthRoute = null
            navigateTo(route, clearStack = clearStack)
        } else {
            pendingPostAuthRoute = route
            navigateTo(Routes.EMAIL_LOGIN)
        }
    }

    fun navigateAuthenticatedShellTab(
        tab: ShellTab,
        clearStack: Boolean = false,
    ) {
        val route = Routes.appShell(tab)
        if (hasActiveSession) {
            pendingPostAuthRoute = null
            navigateTo(route, clearStack = clearStack)
        } else {
            pendingPostAuthRoute = route
            navigateTo(Routes.EMAIL_LOGIN)
        }
    }

    fun consumePostAuthRoute(): String {
        val route = pendingPostAuthRoute ?: Routes.appShell()
        pendingPostAuthRoute = null
        return route
    }

    BackHandler {
        navigateBack()
    }

    when (val routeMatch = Routes.resolve(currentRoute)) {
        null -> {
            LaunchedEffect(currentRoute) {
                navigateTo(Routes.EMAIL_LOGIN, clearStack = true)
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
            )
        }

        else -> when (routeMatch.pattern) {
            Routes.SPLASH -> {
                SplashScreen(
                    onNavigateToHome = {
                        scope.launch {
                            val decision = updateBridge.check()
                            when {
                                decision.hasForceUpdate -> {
                                    optionalUpdateVersion = decision.latestVersion ?: "新版本"
                                    optionalUpdateUrl = decision.downloadUrl
                                    navigateTo(Routes.FORCE_UPDATE, clearStack = true)
                                }

                                decision.hasOptionalUpdate -> {
                                    optionalUpdateVersion = decision.latestVersion ?: "新版本"
                                    optionalUpdateUrl = decision.downloadUrl
                                    showOptionalUpdate = true
                                }

                                else -> navigateTo(resolvePostSplashRoute(), clearStack = true)
                            }
                        }
                    },
                    onNavigateToLogin = { navigateTo(Routes.EMAIL_LOGIN, clearStack = true) },
                    onShowForceUpdate = { navigateTo(Routes.FORCE_UPDATE, clearStack = true) },
                    onShowOptionalUpdate = { showOptionalUpdate = true },
                )
                if (showOptionalUpdate) {
                    OptionalUpdateDialog(
                        versionInfo = optionalUpdateVersion,
                        onDismiss = {
                            showOptionalUpdate = false
                            navigateTo(resolvePostSplashRoute(), clearStack = true)
                        },
                        onUpdate = {
                            showOptionalUpdate = false
                            optionalUpdateUrl?.let(onOpenUrl)
                            navigateTo(resolvePostSplashRoute(), clearStack = true)
                        },
                    )
                }
            }

            Routes.FORCE_UPDATE -> {
                ForceUpdatePage(
                    versionInfo = optionalUpdateVersion,
                    onUpdateClick = { optionalUpdateUrl?.let(onOpenUrl) },
                    onExitClick = onExitApp,
                )
            }

            Routes.APP_SHELL -> {
                val selectedTab = ShellTab.fromKey(routeMatch.args["tab"])
                BitgetAppShell(
                    selectedTab = selectedTab,
                    isAuthenticated = hasActiveSession,
                    onTabSelected = { tab -> navigateToShell(tab = tab, clearStack = true) },
                    onOpenLogin = { navigateTo(Routes.EMAIL_LOGIN) },
                    onOpenVpnConsole = { navigateAuthenticatedShellTab(ShellTab.VPN, clearStack = true) },
                    onOpenPlans = { navigateTo(Routes.PLANS) },
                    onOpenRegions = { navigateAuthenticated(Routes.REGION_SELECTION) },
                    onOpenOrders = { navigateAuthenticated(Routes.ORDER_LIST) },
                    onOpenWalletHome = { navigateAuthenticatedShellTab(ShellTab.WALLET, clearStack = true) },
                    onOpenReceive = { navigateAuthenticated(Routes.RECEIVE) },
                    onOpenSend = { navigateAuthenticated(Routes.send(symbol = "USDT")) },
                    onOpenAssetDetail = { assetId -> navigateAuthenticated(Routes.assetDetail(assetId)) },
                    onOpenInviteCenter = { navigateAuthenticatedShellTab(ShellTab.DISCOVER, clearStack = true) },
                    onOpenCommission = { navigateAuthenticated(Routes.COMMISSION_LEDGER) },
                    onOpenWithdraw = { navigateAuthenticated(Routes.WITHDRAW) },
                    onOpenProfile = { navigateAuthenticatedShellTab(ShellTab.PROFILE, clearStack = true) },
                    onOpenLegal = { navigateTo(Routes.LEGAL_DOCUMENTS) },
                    onOpenSettings = { onOpenLegacyDestination(LegacyDestination.SETTINGS) },
                    onOpenAbout = { onOpenLegacyDestination(LegacyDestination.ABOUT) },
                    onOpenSupport = { onOpenUrl(AppConfig.APP_ISSUES_URL) },
                    onLogout = {
                        hasActiveSession = false
                        pendingPostAuthRoute = null
                    },
                )
            }

            Routes.EMAIL_LOGIN -> {
                EmailLoginPage(
                    onLoginRequest = { email, password -> authBridge.login(email, password) },
                    onLoginSuccess = {
                        hasActiveSession = true
                        navigateTo(consumePostAuthRoute(), clearStack = true)
                        onAuthSuccess()
                    },
                    onNavigateToRegister = { navigateTo(Routes.EMAIL_REGISTER) },
                    onNavigateToResetPassword = { navigateTo(Routes.RESET_PASSWORD) },
                )
            }

            Routes.EMAIL_REGISTER -> {
                EmailRegisterPage(
                    onRequestCode = { email -> authBridge.requestRegisterCode(email) },
                    onRegisterRequest = { email, code, password -> authBridge.register(email, code, password) },
                    onRegisterSuccess = {
                        hasActiveSession = true
                        navigateTo(consumePostAuthRoute(), clearStack = true)
                        onAuthSuccess()
                    },
                    onNavigateToLogin = { navigateBack() },
                    onNavigateToTerms = { navigateTo(Routes.legalDocumentDetail("terms")) },
                    onNavigateToPrivacy = { navigateTo(Routes.legalDocumentDetail("privacy")) },
                )
            }

            Routes.RESET_PASSWORD -> {
                ResetPasswordPage(
                    onSendCodeRequest = { email -> authBridge.requestRegisterCode(email) },
                    onResetRequest = { email, code, newPassword ->
                        authBridge.resetPassword(email, code, newPassword)
                    },
                    onResetSuccess = { navigateTo(Routes.EMAIL_LOGIN, clearStack = true) },
                    onNavigateBack = { navigateBack() },
                )
            }

            Routes.VPN_HOME -> {
                VPNHomePage(
                    onNavigateToRegions = { navigateTo(Routes.REGION_SELECTION) },
                    onNavigateToPlans = { navigateTo(Routes.PLANS) },
                    onNavigateToProfile = { navigateTo(Routes.PROFILE) },
                    onNavigateToOrders = { navigateTo(Routes.ORDER_LIST) },
                )
            }

            Routes.PLANS -> {
                PlansPage(
                    onNavigateBack = { navigateBack() },
                    onNavigateToCheckout = { planId ->
                        navigateTo(Routes.orderCheckout(planId))
                    },
                )
            }

            Routes.REGION_SELECTION -> {
                RegionSelectionPage(
                    onNavigateBack = { navigateBack() },
                    onRegionSelected = { _ -> navigateBack() },
                )
            }

            Routes.ORDER_CHECKOUT -> {
                val planId = routeMatch.args.getValue("planId")
                val checkoutViewModel: OrderCheckoutViewModel = viewModel()
                val checkoutState by checkoutViewModel.state.collectAsState()

                OrderCheckoutPage(
                    viewModel = checkoutViewModel,
                    planId = planId,
                    onNavigateBack = { navigateBack() },
                    onPayWithWallet = { orderId, amount ->
                        val loaded = checkoutState as? OrderCheckoutState.Loaded
                        navigateTo(
                            Routes.walletPaymentConfirm(
                                orderId = orderId.ifBlank { loaded?.orderId },
                                amount = amount.ifBlank { loaded?.totalAmount },
                            ),
                        )
                    },
                    onPayWithCrypto = { _ ->
                        val loaded = checkoutState as? OrderCheckoutState.Loaded
                        navigateTo(
                            Routes.orderResult(
                                orderId = loaded?.orderId,
                                resultType = OrderResultType.PENDING.name,
                            ),
                        )
                    },
                )
            }

            Routes.WALLET_PAYMENT_CONFIRM -> {
                WalletPaymentConfirmPage(
                    orderId = routeMatch.args["orderId"].orEmpty(),
                    amount = routeMatch.args["amount"] ?: "$26.99",
                    onNavigateBack = { navigateBack() },
                    onPaymentSuccess = { txHash ->
                        navigateTo(
                            Routes.orderResult(
                                orderId = routeMatch.args["orderId"].orEmpty().ifBlank { txHash },
                                resultType = OrderResultType.SUCCESS.name,
                            ),
                            clearStack = true,
                        )
                    },
                )
            }

            Routes.ORDER_RESULT -> {
                OrderResultPage(
                    orderId = routeMatch.args["orderId"].orEmpty(),
                    resultType = routeMatch.args["resultType"]?.toOrderResultType()
                        ?: OrderResultType.PENDING,
                    onNavigateToHome = { navigateToShell(tab = ShellTab.HOME, clearStack = true) },
                    onNavigateToOrders = { navigateTo(Routes.ORDER_LIST) },
                    onRetry = {
                        val orderId = routeMatch.args["orderId"]
                        if (orderId.isNullOrBlank()) {
                            navigateTo(Routes.PLANS)
                        } else {
                            navigateTo(Routes.walletPaymentConfirm(orderId = orderId))
                        }
                    },
                )
            }

            Routes.ORDER_LIST -> {
                OrderListPage(
                    onNavigateBack = { navigateBack() },
                    onOrderClick = { orderId ->
                        navigateTo(Routes.orderDetail(orderId))
                    },
                )
            }

            Routes.ORDER_DETAIL -> {
                val orderId = routeMatch.args.getValue("orderId")
                OrderDetailPage(
                    orderId = orderId,
                    onNavigateBack = { navigateBack() },
                    onPayOrder = { selectedOrderId ->
                        navigateTo(Routes.walletPaymentConfirm(orderId = selectedOrderId))
                    },
                    onContactSupport = { onOpenUrl(AppConfig.APP_ISSUES_URL) },
                )
            }

            Routes.WALLET_ONBOARDING -> {
                WalletOnboardingPage(
                    onNavigateToCreate = { navigateToShell(tab = ShellTab.WALLET, clearStack = true) },
                    onNavigateToImport = { navigateToShell(tab = ShellTab.WALLET, clearStack = true) },
                    onSkip = { navigateToShell(tab = ShellTab.WALLET, clearStack = true) },
                )
            }

            Routes.WALLET_HOME -> {
                WalletHomePage(
                    onNavigateToReceive = { navigateTo(Routes.RECEIVE) },
                    onNavigateToSend = { navigateTo(Routes.send(symbol = "USDT")) },
                    onNavigateToAssetDetail = { assetId ->
                        navigateTo(Routes.assetDetail(assetId))
                    },
                    onNavigateToProfile = { navigateTo(Routes.PROFILE) },
                )
            }

            Routes.ASSET_DETAIL -> {
                val assetId = routeMatch.args.getValue("assetId")
                AssetDetailPage(
                    symbol = assetId,
                    onNavigateBack = { navigateBack() },
                    onNavigateToSend = { symbol ->
                        navigateTo(Routes.send(symbol = symbol))
                    },
                    onNavigateToReceive = { navigateTo(Routes.RECEIVE) },
                    onTransactionClick = { orderId ->
                        if (orderId.isNotBlank()) {
                            navigateTo(Routes.orderDetail(orderId))
                        }
                    },
                )
            }

            Routes.RECEIVE -> {
                ReceivePage(onNavigateBack = { navigateBack() })
            }

            Routes.SEND -> {
                val sendSymbol = routeMatch.args["symbol"] ?: "USDT"
                SendPage(
                    symbol = sendSymbol,
                    onNavigateBack = { navigateBack() },
                    onNavigateToConfirm = { orderId, amount, asset ->
                        navigateTo(
                            Routes.walletPayment(
                                orderId = orderId,
                                planName = asset,
                                amount = amount,
                            ),
                        )
                    },
                    onScanQR = {},
                )
            }

            Routes.WALLET_PAYMENT -> {
                WalletPaymentConfirmPage2(
                    orderId = routeMatch.args["orderId"] ?: "ORD-20240115-001",
                    planName = routeMatch.args["planName"] ?: "订单支付",
                    amount = routeMatch.args["amount"] ?: "$0.00",
                    onNavigateBack = { navigateBack() },
                    onPaymentSuccess = {
                        navigateTo(
                            Routes.sendResult(resultType = SendResultType.SUCCESS.name),
                            clearStack = true,
                        )
                    },
                )
            }

            Routes.SEND_RESULT -> {
                SendResultPage(
                    resultType = routeMatch.args["resultType"]?.toSendResultType()
                        ?: SendResultType.PENDING,
                    onNavigateToHome = { navigateToShell(tab = ShellTab.HOME, clearStack = true) },
                    onNavigateToWallet = { navigateToShell(tab = ShellTab.WALLET, clearStack = true) },
                    onRetry = { navigateTo(Routes.send(symbol = routeMatch.args["symbol"])) },
                    onViewExplorer = {},
                )
            }

            Routes.INVITE_CENTER -> {
                InviteCenterPage(
                    onNavigateBack = { navigateBack() },
                    onNavigateToCommission = { navigateTo(Routes.COMMISSION_LEDGER) },
                    onNavigateToWithdraw = { navigateTo(Routes.WITHDRAW) },
                )
            }

            Routes.COMMISSION_LEDGER -> {
                CommissionLedgerPage(
                    onNavigateBack = { navigateBack() },
                    onNavigateToWithdraw = { navigateTo(Routes.WITHDRAW) },
                )
            }

            Routes.WITHDRAW -> {
                WithdrawPage(
                    onNavigateBack = { navigateBack() },
                    onWithdrawSuccess = { navigateBack() },
                )
            }

            Routes.PROFILE -> {
                ProfilePage(
                    onNavigateToOrders = { navigateTo(Routes.ORDER_LIST) },
                    onNavigateToWallet = { navigateTo(Routes.WALLET_HOME) },
                    onNavigateToInvite = { navigateTo(Routes.INVITE_CENTER) },
                    onNavigateToCommission = { navigateTo(Routes.COMMISSION_LEDGER) },
                    onNavigateToSettings = { onOpenLegacyDestination(LegacyDestination.SETTINGS) },
                    onNavigateToLegal = { navigateTo(Routes.LEGAL_DOCUMENTS) },
                    onNavigateToSupport = {
                        onOpenLegacyDestination(LegacyDestination.SUPPORT)
                        onOpenUrl(AppConfig.APP_ISSUES_URL)
                    },
                    onNavigateToAbout = { onOpenLegacyDestination(LegacyDestination.ABOUT) },
                    onLogout = {
                        hasActiveSession = false
                        pendingPostAuthRoute = null
                        navigateToShell(tab = ShellTab.PROFILE, clearStack = true)
                    },
                )
            }

            Routes.LEGAL_DOCUMENTS -> {
                LegalDocumentsListPage(
                    onNavigateBack = { navigateBack() },
                    onDocumentClick = { documentId ->
                        navigateTo(Routes.legalDocumentDetail(documentId))
                    },
                )
            }

            Routes.LEGAL_DOCUMENT_DETAIL -> {
                LegalDocumentDetailPage(
                    documentId = routeMatch.args.getValue("documentId"),
                    onNavigateBack = { navigateBack() },
                )
            }
        }
    }
}

private fun String.toOrderResultType(): OrderResultType {
    return runCatching { OrderResultType.valueOf(this) }.getOrDefault(OrderResultType.PENDING)
}

private fun String.toSendResultType(): SendResultType {
    return runCatching { SendResultType.valueOf(this) }.getOrDefault(SendResultType.PENDING)
}
