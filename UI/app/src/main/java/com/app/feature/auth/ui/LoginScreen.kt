package com.app.feature.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.common.components.GradientCard
import com.app.common.components.PrimaryButton
import com.app.common.components.SecondaryButton
import com.app.core.ui.AppScaffold
import com.app.feature.auth.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit = {},
    onRegister: () -> Unit = {},
    onResetPassword: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(state.isLoggedIn) { if (state.isLoggedIn) onLoginSuccess() }
    AppScaffold(title = "登录页") { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            GradientCard(title = "欢迎回来", subtitle = "进入你的钱包与 VPN 控制台") {
                OutlinedTextField(value = state.email, onValueChange = viewModel::updateEmail, label = { Text("邮箱") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = state.password, onValueChange = viewModel::updatePassword, label = { Text("密码") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                PrimaryButton(text = if (state.loading) "登录中…" else "登录", onClick = viewModel::login)
                Spacer(Modifier.height(8.dp))
                SecondaryButton(text = "创建账户", onClick = onRegister)
                Spacer(Modifier.height(8.dp))
                SecondaryButton(text = "忘记密码", onClick = onResetPassword)
                if (state.lastMessage.isNotBlank()) Text(state.lastMessage, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
