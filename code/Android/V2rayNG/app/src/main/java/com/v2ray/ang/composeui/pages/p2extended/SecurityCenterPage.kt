package com.v2ray.ang.composeui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.p2extended.model.SecurityCenterEvent
import com.v2ray.ang.composeui.p2extended.model.SecurityCenterUiState
import com.v2ray.ang.composeui.p2extended.model.securityCenterPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.SecurityCenterViewModel

@Composable
fun SecurityCenterRoute(
    viewModel: SecurityCenterViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    SecurityCenterScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                SecurityCenterEvent.PrimaryActionClicked -> onPrimaryAction()
                SecurityCenterEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun SecurityCenterScreen(
    uiState: SecurityCenterUiState,
    onEvent: (SecurityCenterEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P2ExtendedPageScaffold(
        kicker = "Security Center",
        title = "安全中心",
        subtitle = "管理助记词、设备、签名授权与高风险地址提醒。",
        hubLabel = "A级",
        onHubClick = { onEvent(SecurityCenterEvent.Refresh) },
    ) {
        KpiRow(
            listOf(
                "助记词状态" to "已备份",
                "双重验证" to "已开启",
                "风险地址" to "2 条",
            ),
        )
        Spacer(modifier = Modifier.height(14.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ListRow("管理助记词", "查看备份状态、重新校验")
            ListRow("已登录设备", "Android · Pixel 8 / Web")
            ListRow("签名授权记录", "已连接 6 个 DApp")
            ListRow("风险地址提醒", "自动拦截诈骗地址")
            ListRow("生物识别支付", "大额交易需指纹验证")
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun SecurityCenterPreview() {
    CryptoVpnTheme {
        SecurityCenterScreen(
            uiState = securityCenterPreviewState(),
            onEvent = {},
        )
    }
}
