package com.app.feature.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.common.components.GradientCard
import com.app.common.components.PrimaryButton
import com.app.core.ui.AppScaffold
import com.app.feature.auth.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel = viewModel(),
    onBack: () -> Unit = {},
    onRegistered: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(state.isLoggedIn) { if (state.isLoggedIn) onRegistered() }
    AppScaffold(title = "创建账户", onBack = onBack) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            GradientCard(title = "注册 vpn01 账户", subtitle = "创建你的钱包身份与邀请关系") {
                OutlinedTextField(value = state.email, onValueChange = viewModel::updateEmail, label = { Text("邮箱") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = state.password, onValueChange = viewModel::updatePassword, label = { Text("密码") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = state.inviteCode, onValueChange = viewModel::updateInviteCode, label = { Text("邀请码") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                PrimaryButton(text = "创建账户", onClick = viewModel::register)
            }
        }
    }
}
