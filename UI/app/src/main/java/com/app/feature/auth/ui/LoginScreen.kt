package com.app.feature.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.common.components.GradientCard
import com.app.common.components.PrimaryButton
import com.app.common.components.SecondaryButton
import com.app.common.components.StatusChip
import com.app.core.theme.CardGlassStrong
import com.app.core.theme.TextSecondary
import com.app.core.ui.AppScaffold
import com.app.feature.auth.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit = {},
    onRegister: () -> Unit = {},
    onResetPassword: () -> Unit = {},
    onOpenEffectLab: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(state.isLoggedIn) { if (state.isLoggedIn) onLoginSuccess() }
    AppScaffold(title = "", showTopBar = false) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                StatusChip(text = "P0 登录")
                StatusChip(text = "CryptoVPN")
            }
            Spacer(Modifier.height(12.dp))
            Text("欢迎回来", style = MaterialTheme.typography.headlineLarge)
            Text("登录你的多链钱包与 VPN 控制台", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
            GradientCard(title = "安全登录", subtitle = "围绕账户、钱包和订阅同步的一体化入口") {
                OutlinedTextField(
                    value = state.email,
                    onValueChange = viewModel::updateEmail,
                    label = { Text("邮箱地址") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = CardGlassStrong.copy(alpha = 0.76f),
                        focusedContainerColor = CardGlassStrong,
                    ),
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.password,
                    onValueChange = viewModel::updatePassword,
                    label = { Text("登录密码") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = CardGlassStrong.copy(alpha = 0.76f),
                        focusedContainerColor = CardGlassStrong,
                    ),
                )
                Spacer(Modifier.height(12.dp))
                PrimaryButton(text = if (state.loading) "登录中…" else "登录并同步账户", onClick = viewModel::login)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SecondaryButton(text = "创建账户", onClick = onRegister, modifier = Modifier.weight(1f))
                    SecondaryButton(text = "忘记密码", onClick = onResetPassword, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                SecondaryButton(text = "动效实验室", onClick = onOpenEffectLab)
                if (state.lastMessage.isNotBlank()) Text(state.lastMessage, style = MaterialTheme.typography.bodyMedium)
            }
            GradientCard(title = "账户说明", subtitle = "当前为原生 Compose 高保真重建中") {
                Text("下一阶段会继续把旧 composeui 的顶部结构、按钮、卡片和输入风格迁到全部 P0 页面。", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
