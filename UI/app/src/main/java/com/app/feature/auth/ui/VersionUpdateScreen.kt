package com.app.feature.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.common.components.GradientCard
import com.app.common.components.PrimaryButton
import com.app.common.components.SecondaryButton
import com.app.core.ui.AppScaffold

@Composable
fun VersionUpdateScreen(
    onSkip: () -> Unit = {},
    onUpdate: () -> Unit = {},
) {
    AppScaffold(title = "发现新版本") { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.Center) {
            GradientCard(title = "v1.1 已准备就绪", subtitle = "新增市场 AI 诊断与订单流优化") {
                Text("你可以稍后更新，或现在升级到最新体验。")
                Spacer(Modifier.height(16.dp))
                PrimaryButton(text = "立即更新", onClick = onUpdate)
                Spacer(Modifier.height(8.dp))
                SecondaryButton(text = "稍后再说", onClick = onSkip)
            }
        }
    }
}
