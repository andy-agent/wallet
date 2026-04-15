package com.v2ray.ang.composeui.pages.p0

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.model.LoginEvent
import com.v2ray.ang.composeui.p0.model.LoginUiState
import com.v2ray.ang.composeui.p0.model.loginPreviewState
import com.v2ray.ang.composeui.p0.ui.P01ButtonRow
import com.v2ray.ang.composeui.p0.ui.P01Card
import com.v2ray.ang.composeui.p0.ui.P01CardCopy
import com.v2ray.ang.composeui.p0.ui.P01CardHeader
import com.v2ray.ang.composeui.p0.ui.P01Header
import com.v2ray.ang.composeui.p0.ui.P01InputField
import com.v2ray.ang.composeui.p0.ui.P01List
import com.v2ray.ang.composeui.p0.ui.P01ListRow
import com.v2ray.ang.composeui.p0.ui.P01PhoneScaffold
import com.v2ray.ang.composeui.p0.viewmodel.LoginViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun EmailLoginRoute(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onForgotPassword: () -> Unit,
    onRegister: () -> Unit,
    onWalletOnboarding: () -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    EmailLoginScreen(
        uiState = uiState,
        onEmailChange = { viewModel.onEvent(LoginEvent.EmailChanged(it)) },
        onPasswordChange = { viewModel.onEvent(LoginEvent.PasswordChanged(it)) },
        onPrimary = { viewModel.onEvent(LoginEvent.LoginClicked, onLoginSuccess) },
        onDismissDialog = { viewModel.onEvent(LoginEvent.DialogDismissed) },
        onRegister = onRegister,
        onWalletImport = onWalletOnboarding,
        onBottomNav = onBottomNav,
        onForgotPassword = onForgotPassword,
    )
}

@Composable
fun EmailLoginScreen(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPrimary: () -> Unit,
    onDismissDialog: () -> Unit,
    onRegister: () -> Unit,
    onWalletImport: () -> Unit,
    onBottomNav: (String) -> Unit = {},
    onForgotPassword: () -> Unit = {},
) {
    uiState.dialogMessage?.takeIf { it.isNotBlank() }?.let { message ->
        AlertDialog(
            onDismissRequest = onDismissDialog,
            title = { Text(uiState.dialogTitle ?: "登录失败") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = onDismissDialog) {
                    Text("知道了")
                }
            },
        )
    }

    P01PhoneScaffold(
        currentRoute = CryptoVpnRouteSpec.vpnHome.name,
        onBottomNav = onBottomNav,
    ) {
        P01Header(
            eyebrow = "WELCOME BACK",
            title = "欢迎回来",
            subtitle = "",
        )

        P01Card {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                P01InputField(
                    label = "邮箱地址",
                    value = uiState.email,
                    onValueChange = onEmailChange,
                )
                P01InputField(
                    label = "登录密码",
                    value = uiState.password,
                    onValueChange = onPasswordChange,
                    password = true,
                    trailingText = "找回密码",
                    onTrailingClick = onForgotPassword,
                )
                P01ButtonRow(
                    primaryLabel = if (uiState.isLoading) "登录中..." else "登录并同步账户",
                    onPrimaryClick = onPrimary,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    com.v2ray.ang.composeui.p0.ui.P01SecondaryButton(
                        text = "创建账户",
                        onClick = onRegister,
                        modifier = Modifier.weight(1f),
                    )
                    com.v2ray.ang.composeui.p0.ui.P01SecondaryButton(
                        text = "注册后导入",
                        onClick = onWalletImport,
                        modifier = Modifier.weight(1f),
                    )
                }
                uiState.successMessage?.takeIf { it.isNotBlank() }?.let { message ->
                    P01CardCopy(message)
                }
            }
        }

    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun EmailLoginPreview() {
    CryptoVpnTheme {
        EmailLoginScreen(
            uiState = loginPreviewState(),
            onEmailChange = {},
            onPasswordChange = {},
            onPrimary = {},
            onDismissDialog = {},
            onRegister = {},
            onWalletImport = {},
        )
    }
}
