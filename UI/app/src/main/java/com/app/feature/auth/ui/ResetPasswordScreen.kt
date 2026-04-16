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
fun ResetPasswordScreen(
    viewModel: AuthViewModel = viewModel(),
    onBack: () -> Unit = {},
    onDone: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    AppScaffold(title = "重置密码", onBack = onBack) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            GradientCard(title = "找回密码", subtitle = "发送重置链接到你的邮箱") {
                OutlinedTextField(value = state.email, onValueChange = viewModel::updateEmail, label = { Text("邮箱") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                PrimaryButton(text = "发送重置邮件", onClick = { viewModel.resetPassword(); onDone() })
                if (state.lastMessage.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(state.lastMessage)
                }
            }
        }
    }
}
