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
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.model.LoginEvent
import com.v2ray.ang.composeui.p0.model.LoginUiState
import com.v2ray.ang.composeui.p0.repository.MockP0Repository
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
    onRegister: () -> Unit,
    onWalletImport: () -> Unit,
    onBottomNav: (String) -> Unit = {},
    onForgotPassword: () -> Unit = {},
) {
    P01PhoneScaffold(
        statusTime = "18:05",
        currentRoute = CryptoVpnRouteSpec.vpnHome.name,
        onBottomNav = onBottomNav,
    ) {
        P01Header(
            eyebrow = "WELCOME BACK",
            title = "欢迎回来",
            subtitle = "继续访问你的多链资产、订阅状态与全球隐私节点。",
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
                uiState.statusMessage?.let {
                    P01CardCopy(it)
                }
                uiState.errorMessage?.let {
                    P01CardCopy(it)
                }
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
                        text = "导入钱包",
                        onClick = onWalletImport,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        P01Card {
            P01CardHeader(
                title = "本次会话将同步",
                trailing = {
                    com.v2ray.ang.composeui.p0.ui.P01Chip(text = "端到端安全")
                },
            )
            P01List {
                P01ListRow(
                    title = "VPN订阅与节点偏好",
                    copy = "同步当前账号可见的订阅状态、本地节点缓存与最近连接记录。",
                )
                P01ListRow(
                    title = "多链资产与支付能力",
                    copy = "SOL、USDT-SOL、USDT-TRON 可直接支付套餐。",
                )
                P01ListRow(
                    title = "高风险状态预警",
                    copy = uiState.helperText.ifBlank { "交易确认、节点波动、会话异常同时提醒。" },
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun EmailLoginPreview() {
    CryptoVpnTheme {
        EmailLoginScreen(
            uiState = LoginViewModel(MockP0Repository()).uiState.value,
            onEmailChange = {},
            onPasswordChange = {},
            onPrimary = {},
            onRegister = {},
            onWalletImport = {},
        )
    }
}
