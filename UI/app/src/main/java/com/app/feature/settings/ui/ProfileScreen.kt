package com.app.feature.settings.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.common.components.*
import com.app.core.theme.AppDimens
import com.app.core.ui.AppScaffold
import com.app.feature.settings.viewmodel.SettingsViewModel

import com.app.common.widgets.BalanceHeader

@Composable
fun ProfileScreen(
    viewModel: SettingsViewModel = viewModel(),
    onOpenLegalDocs: () -> Unit = {},
    onLogout: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    val profile = state.profile ?: return
    AppScaffold(title = "我的") { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { BalanceHeader(profile) }
            item { GradientCard(title = "账户中心", subtitle = profile.levelLabel) { InfoRow("邮箱", profile.email); InfoRow("邀请码", profile.inviteCode) } }
            item { PrimaryButton(text = "法务文档", onClick = onOpenLegalDocs) }
            item { SecondaryButton(text = "退出登录", onClick = { viewModel.logout(); onLogout() }) }
        }
    }
}
