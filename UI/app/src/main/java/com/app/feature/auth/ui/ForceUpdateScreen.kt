package com.app.feature.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.common.components.GradientCard
import com.app.common.components.PrimaryButton
import com.app.core.ui.AppScaffold

@Composable
fun ForceUpdateScreen(onUpdate: () -> Unit = {}) {
    AppScaffold(title = "强制更新") { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.Center) {
            GradientCard(title = "需要更新", subtitle = "当前版本过旧，必须更新后继续使用") {
                Text("本次更新包含钱包签名安全修复和 VPN 连接稳定性改进。")
                Spacer(Modifier.height(16.dp))
                PrimaryButton(text = "立即更新", onClick = onUpdate)
            }
        }
    }
}
