package com.v2ray.ang.composeui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.v2ray.ang.composeui.bridge.auth.ComposeAuthBridge
import com.v2ray.ang.composeui.pages.auth.EmailLoginPage
import com.v2ray.ang.composeui.pages.auth.EmailRegisterPage
import com.v2ray.ang.composeui.pages.auth.ResetPasswordPage
import com.v2ray.ang.composeui.pages.splash.ComposeUpdateBridge
import com.v2ray.ang.composeui.pages.splash.ForceUpdatePage
import com.v2ray.ang.composeui.pages.splash.OptionalUpdateDialog
import com.v2ray.ang.composeui.pages.splash.SplashScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
    authBridge: ComposeAuthBridge,
    updateBridge: ComposeUpdateBridge,
    onOpenUrl: (String) -> Unit,
    onExitApp: () -> Unit,
    onAuthSuccess: () -> Unit,
    startDestination: String = Routes.SPLASH,
) {
    val scope = rememberCoroutineScope()
    var currentRoute by rememberSaveable { mutableStateOf(startDestination) }
    var showOptionalUpdate by rememberSaveable { mutableStateOf(false) }
    var optionalUpdateVersion by rememberSaveable { mutableStateOf("新版本") }
    var optionalUpdateUrl by rememberSaveable { mutableStateOf<String?>(null) }

    when (currentRoute) {
        Routes.SPLASH -> {
            SplashScreen(
                onNavigateToHome = {
                    scope.launch {
                        val decision = updateBridge.check()
                        when {
                            decision.hasForceUpdate -> {
                                optionalUpdateVersion = decision.latestVersion ?: "新版本"
                                optionalUpdateUrl = decision.downloadUrl
                                currentRoute = Routes.FORCE_UPDATE
                            }
                            decision.hasOptionalUpdate -> {
                                optionalUpdateVersion = decision.latestVersion ?: "新版本"
                                optionalUpdateUrl = decision.downloadUrl
                                showOptionalUpdate = true
                            }
                            else -> {
                                currentRoute = Routes.EMAIL_LOGIN
                            }
                        }
                    }
                },
                onNavigateToLogin = { currentRoute = Routes.EMAIL_LOGIN },
                onShowForceUpdate = { currentRoute = Routes.FORCE_UPDATE },
                onShowOptionalUpdate = { showOptionalUpdate = true },
            )
            if (showOptionalUpdate) {
                OptionalUpdateDialog(
                    versionInfo = optionalUpdateVersion,
                    onDismiss = {
                        showOptionalUpdate = false
                        currentRoute = Routes.EMAIL_LOGIN
                    },
                    onUpdate = {
                        showOptionalUpdate = false
                        optionalUpdateUrl?.let(onOpenUrl)
                        currentRoute = Routes.EMAIL_LOGIN
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
        Routes.EMAIL_LOGIN -> {
            EmailLoginPage(
                onLoginRequest = { email, password -> authBridge.login(email, password) },
                onLoginSuccess = onAuthSuccess,
                onNavigateToRegister = { currentRoute = Routes.EMAIL_REGISTER },
                onNavigateToResetPassword = { currentRoute = Routes.RESET_PASSWORD },
            )
        }
        Routes.EMAIL_REGISTER -> {
            EmailRegisterPage(
                onRequestCode = { email -> authBridge.requestRegisterCode(email) },
                onRegisterRequest = { email, code, password -> authBridge.register(email, code, password) },
                onRegisterSuccess = onAuthSuccess,
                onNavigateToLogin = { currentRoute = Routes.EMAIL_LOGIN },
            )
        }
        Routes.RESET_PASSWORD -> {
            ResetPasswordPage(
                onSendCodeRequest = { email -> authBridge.requestRegisterCode(email) },
                onResetRequest = { email, code, newPassword -> authBridge.resetPassword(email, code, newPassword) },
                onResetSuccess = { currentRoute = Routes.EMAIL_LOGIN },
                onNavigateBack = { currentRoute = Routes.EMAIL_LOGIN },
            )
        }
        else -> {
            LaunchedEffect(currentRoute) {
                currentRoute = Routes.EMAIL_LOGIN
            }
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
            )
        }
    }
}
