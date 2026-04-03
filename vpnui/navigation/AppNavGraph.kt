package com.cryptovpn.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cryptovpn.ui.pages.*
import com.cryptovpn.ui.pages.batch1.*
import com.cryptovpn.ui.pages.batch2.*
import com.cryptovpn.ui.pages.batch2b.*
import com.cryptovpn.ui.pages.batch3.*
import com.cryptovpn.ui.pages.batch4.*
import com.cryptovpn.ui.pages.batch5.*

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Routes.SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Splash & Version
        composable(Routes.SPLASH) {
            val viewModel: SplashViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            SplashScreen(
                state = state,
                onNavigateToLogin = {
                    navController.navigate(Routes.EMAIL_LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Routes.VPN_HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToForceUpdate = {
                    navController.navigate(Routes.FORCE_UPDATE)
                },
                onShowOptionalUpdate = {
                    // Show optional update dialog
                }
            )
        }

        composable(Routes.FORCE_UPDATE) {
            ForceUpdatePage(
                onUpdateClick = { /* Open download link */ },
                onExitClick = { /* Exit app */ }
            )
        }

        // Auth
        composable(Routes.EMAIL_LOGIN) {
            val viewModel: LoginViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            EmailLoginPage(
                state = state,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onPasswordVisibilityToggle = viewModel::togglePasswordVisibility,
                onLoginClick = viewModel::login,
                onNavigateToRegister = {
                    navController.navigate(Routes.EMAIL_REGISTER)
                },
                onNavigateToResetPassword = {
                    navController.navigate(Routes.RESET_PASSWORD)
                },
                onLoginSuccess = {
                    navController.navigate(Routes.VPN_HOME) {
                        popUpTo(Routes.EMAIL_LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.EMAIL_REGISTER) {
            val viewModel: RegisterViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            EmailRegisterPage(
                state = state,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
                onRegisterClick = viewModel::register,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Routes.EMAIL_LOGIN) {
                        popUpTo(Routes.EMAIL_REGISTER) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.RESET_PASSWORD) {
            val viewModel: ResetPasswordViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            ResetPasswordPage(
                state = state,
                onEmailChange = viewModel::onEmailChange,
                onCodeChange = viewModel::onCodeChange,
                onNewPasswordChange = viewModel::onNewPasswordChange,
                onSendCodeClick = viewModel::sendVerificationCode,
                onConfirmClick = viewModel::resetPassword,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onResetSuccess = {
                    navController.navigate(Routes.EMAIL_LOGIN) {
                        popUpTo(Routes.RESET_PASSWORD) { inclusive = true }
                    }
                }
            )
        }

        // VPN
        composable(Routes.VPN_HOME) {
            val viewModel: VPNHomeViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            VPNHomePage(
                state = state,
                onMenuClick = { /* Open drawer */ },
                onSettingsClick = { /* Navigate to settings */ },
                onProfileClick = {
                    navController.navigate(Routes.PROFILE)
                },
                onConnectClick = viewModel::toggleConnection,
                onModeChange = viewModel::changeMode,
                onNavigateToRegions = {
                    navController.navigate(Routes.REGION_SELECTION)
                },
                onNavigateToPlans = {
                    navController.navigate(Routes.PLANS)
                },
                onRenewClick = {
                    navController.navigate(Routes.PLANS)
                }
            )
        }

        composable(Routes.PLANS) {
            val viewModel: PlansViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            PlansPage(
                state = state,
                onBackClick = { navController.popBackStack() },
                onPlanSelect = { planId ->
                    navController.navigate(Routes.orderCheckout(planId))
                }
            )
        }

        composable(Routes.REGION_SELECTION) {
            val viewModel: RegionSelectionViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            RegionSelectionPage(
                state = state,
                onBackClick = { navController.popBackStack() },
                onRegionSelect = viewModel::selectRegion,
                onConfirmClick = {
                    navController.popBackStack()
                },
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onContinentFilterChange = viewModel::onContinentFilterChange
            )
        }

        composable(
            route = Routes.ORDER_CHECKOUT,
            arguments = listOf(
                androidx.navigation.navArgument("planId") {
                    type = androidx.navigation.NavType.StringType
                }
            )
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString("planId") ?: ""
            val viewModel: OrderCheckoutViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            OrderCheckoutPage(
                state = state,
                onBackClick = { navController.popBackStack() },
                onCopyAddressClick = viewModel::copyAddress,
                onUseWalletClick = {
                    navController.navigate(Routes.WALLET_PAYMENT_CONFIRM)
                },
                onPaidClick = viewModel::checkPayment,
                onRefreshStatusClick = viewModel::refreshStatus
            )
        }

        composable(Routes.ORDER_RESULT) {
            val viewModel: OrderResultViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            OrderResultPage(
                state = state,
                onCloseClick = {
                    navController.navigate(Routes.VPN_HOME) {
                        popUpTo(Routes.ORDER_RESULT) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Routes.VPN_HOME) {
                        popUpTo(Routes.ORDER_RESULT) { inclusive = true }
                    }
                },
                onNavigateToOrders = {
                    navController.navigate(Routes.ORDER_LIST)
                },
                onReorderClick = {
                    navController.navigate(Routes.PLANS)
                }
            )
        }

        composable(Routes.ORDER_LIST) {
            val viewModel: OrderListViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            OrderListPage(
                state = state,
                onBackClick = { navController.popBackStack() },
                onOrderClick = { orderId ->
                    navController.navigate(Routes.orderDetail(orderId))
                }
            )
        }

        composable(
            route = Routes.ORDER_DETAIL,
            arguments = listOf(
                androidx.navigation.navArgument("orderId") {
                    type = androidx.navigation.NavType.StringType
                }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            val viewModel: OrderDetailViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            OrderDetailPage(
                state = state,
                orderId = orderId,
                onBackClick = { navController.popBackStack() },
                onReorderClick = {
                    navController.navigate(Routes.PLANS)
                }
            )
        }

        // Wallet
        composable(Routes.WALLET_ONBOARDING) {
            val viewModel: WalletOnboardingViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            WalletOnboardingPage(
                state = state,
                onCreateWalletClick = viewModel::createWallet,
                onImportWalletClick = viewModel::importWallet,
                onNavigateToWalletHome = {
                    navController.navigate(Routes.WALLET_HOME) {
                        popUpTo(Routes.WALLET_ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.WALLET_HOME) {
            val viewModel: WalletHomeViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            WalletHomePage(
                state = state,
                onBackClick = { navController.popBackStack() },
                onSettingsClick = { /* Navigate to wallet settings */ },
                onChainSelect = viewModel::selectChain,
                onAssetClick = { assetId ->
                    navController.navigate(Routes.assetDetail(assetId))
                },
                onReceiveClick = {
                    navController.navigate(Routes.RECEIVE)
                },
                onSendClick = {
                    navController.navigate(Routes.SEND)
                }
            )
        }

        composable(
            route = Routes.ASSET_DETAIL,
            arguments = listOf(
                androidx.navigation.navArgument("assetId") {
                    type = androidx.navigation.NavType.StringType
                }
            )
        ) { backStackEntry ->
            val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
            val viewModel: AssetDetailViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            AssetDetailPage(
                state = state,
                assetId = assetId,
                onBackClick = { navController.popBackStack() },
                onReceiveClick = {
                    navController.navigate(Routes.RECEIVE)
                },
                onSendClick = {
                    navController.navigate(Routes.SEND)
                }
            )
        }

        composable(Routes.RECEIVE) {
            val viewModel: ReceiveViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            ReceivePage(
                state = state,
                onBackClick = { navController.popBackStack() },
                onCopyAddressClick = viewModel::copyAddress,
                onShareAddressClick = viewModel::shareAddress
            )
        }

        composable(Routes.SEND) {
            val viewModel: SendViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            SendPage(
                state = state,
                onBackClick = { navController.popBackStack() },
                onCancelClick = { navController.popBackStack() },
                onAssetSelect = { /* Show asset selector */ },
                onAddressChange = viewModel::onAddressChange,
                onAmountChange = viewModel::onAmountChange,
                onScanClick = viewModel::scanQRCode,
                onMaxClick = viewModel::setMaxAmount,
                onFeeSpeedChange = viewModel::changeFeeSpeed,
                onConfirmClick = {
                    navController.navigate(Routes.SEND_RESULT)
                }
            )
        }

        composable(Routes.SEND_RESULT) {
            val viewModel: SendResultViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            SendResultPage(
                state = state,
                onBackToWalletClick = {
                    navController.navigate(Routes.WALLET_HOME) {
                        popUpTo(Routes.SEND_RESULT) { inclusive = true }
                    }
                },
                onViewTransactionClick = { txHash ->
                    // Open blockchain explorer
                },
                onRetryClick = {
                    navController.navigate(Routes.SEND)
                }
            )
        }

        composable(Routes.WALLET_PAYMENT_CONFIRM) {
            val viewModel: WalletPaymentConfirmViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            WalletPaymentConfirmPage(
                state = state,
                onBackClick = { navController.popBackStack() },
                onConfirmClick = {
                    navController.navigate(Routes.ORDER_RESULT)
                },
                onCancelClick = { navController.popBackStack() }
            )
        }

        // Growth
        composable(Routes.INVITE_CENTER) {
            val viewModel: InviteCenterViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            InviteCenterPage(
                state = state,
                onBackClick = { navController.popBackStack() },
                onShareClick = viewModel::shareInvite,
                onCopyCodeClick = viewModel::copyInviteCode,
                onNavigateToWithdraw = {
                    navController.navigate(Routes.WITHDRAW)
                },
                onNavigateToLedger = {
                    navController.navigate(Routes.COMMISSION_LEDGER)
                }
            )
        }

        composable(Routes.COMMISSION_LEDGER) {
            val viewModel: CommissionLedgerViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            CommissionLedgerPage(
                state = state,
                onBackClick = { navController.popBackStack() },
                onFilterChange = viewModel::changeFilter,
                onLoadMore = viewModel::loadMore
            )
        }

        composable(Routes.WITHDRAW) {
            val viewModel: WithdrawViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            WithdrawPage(
                state = state,
                onBackClick = { navController.popBackStack() },
                onAmountChange = viewModel::onAmountChange,
                onAddressChange = viewModel::onAddressChange,
                onPasteAddressClick = viewModel::pasteAddress,
                onNetworkChange = viewModel::changeNetwork,
                onMaxClick = viewModel::setMaxAmount,
                onConfirmClick = viewModel::submitWithdraw,
                onViewHistoryClick = {
                    // Navigate to withdraw history
                }
            )
        }

        // Profile & Legal
        composable(Routes.PROFILE) {
            val viewModel: ProfileViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            ProfilePage(
                state = state,
                onEditProfileClick = { /* Navigate to edit profile */ },
                onManageSubscriptionClick = { /* Navigate to subscription management */ },
                onNavigateToOrders = {
                    navController.navigate(Routes.ORDER_LIST)
                },
                onNavigateToLegal = {
                    navController.navigate(Routes.LEGAL_DOCUMENTS)
                },
                onNavigateToAbout = { /* Navigate to about page */ },
                onLogoutClick = {
                    navController.navigate(Routes.EMAIL_LOGIN) {
                        popUpTo(Routes.PROFILE) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LEGAL_DOCUMENTS) {
            val viewModel: LegalDocumentsViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            LegalDocumentsListPage(
                state = state,
                onBackClick = { navController.popBackStack() },
                onDocumentClick = { documentId ->
                    navController.navigate(Routes.legalDocumentDetail(documentId))
                }
            )
        }

        composable(
            route = Routes.LEGAL_DOCUMENT_DETAIL,
            arguments = listOf(
                androidx.navigation.navArgument("documentId") {
                    type = androidx.navigation.NavType.StringType
                }
            )
        ) { backStackEntry ->
            val documentId = backStackEntry.arguments?.getString("documentId") ?: ""
            val viewModel: LegalDocumentDetailViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            LegalDocumentDetailPage(
                state = state,
                documentId = documentId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}