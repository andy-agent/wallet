package com.app.feature.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.common.components.GradientCard
import com.app.core.ui.AppScaffold
import com.app.feature.auth.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: AuthViewModel = viewModel(),
    onGoLogin: () -> Unit = {},
    onGoHome: () -> Unit = {},
    onForceUpdate: () -> Unit = {},
    onVersionUpdate: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(state.forceUpdate, state.versionUpdate, state.isLoggedIn) {
        delay(900)
        when {
            state.forceUpdate -> onForceUpdate()
            state.versionUpdate && !state.isLoggedIn -> onVersionUpdate()
            state.isLoggedIn -> onGoHome()
            else -> onGoLogin()
        }
    }
    AppScaffold(title = "启动页") { padding: PaddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            GradientCard(title = "CryptoVPN", subtitle = "多链钱包 + v2rayNG 风格 VPN 架构") {
                Text("正在检查版本、权限与本地钱包状态…", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
