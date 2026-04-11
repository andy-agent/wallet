package com.v2ray.ang.composeui.pages.p0

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.model.WalletCreationMode
import com.v2ray.ang.composeui.p0.model.WalletOnboardingEvent
import com.v2ray.ang.composeui.p0.model.WalletOnboardingUiState
import com.v2ray.ang.composeui.p0.repository.MockP0Repository
import com.v2ray.ang.composeui.p0.ui.P01Card
import com.v2ray.ang.composeui.p0.ui.P01CardCopy
import com.v2ray.ang.composeui.p0.ui.P01CardHeader
import com.v2ray.ang.composeui.p0.ui.P01Chip
import com.v2ray.ang.composeui.p0.ui.P01Header
import com.v2ray.ang.composeui.p0.ui.P01PhoneScaffold
import com.v2ray.ang.composeui.p0.ui.P01PrimaryButton
import com.v2ray.ang.composeui.p0.ui.P01Tab
import com.v2ray.ang.composeui.p0.ui.P01Orb
import com.v2ray.ang.composeui.p0.viewmodel.WalletOnboardingViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun WalletOnboardingRoute(
    viewModel: WalletOnboardingViewModel,
    onContinue: () -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    WalletOnboardingScreen(
        uiState = uiState,
        onSelectMode = { viewModel.onEvent(WalletOnboardingEvent.SelectMode(it)) },
        onContinue = onContinue,
        onBottomNav = onBottomNav,
    )
}

@Composable
fun WalletOnboardingScreen(
    uiState: WalletOnboardingUiState,
    onSelectMode: (WalletCreationMode) -> Unit,
    onContinue: () -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P01PhoneScaffold(
        statusTime = "18:06",
        currentRoute = CryptoVpnRouteSpec.walletHome.name,
        onBottomNav = onBottomNav,
    ) {
        P01Header(
            eyebrow = "MULTI-CHAIN WALLET SETUP",
            title = "配置你的多链钱包",
            subtitle = uiState.statusMessage ?: "当前钱包能力仍在逐步接入，先按真实状态引导你进入后续页面。",
        )

        P01Card(centered = true) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Box(modifier = Modifier.size(170.dp)) {
                    P01Orb(modifier = Modifier.fillMaxSize())
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            P01Tab(
                text = "主账户",
                selected = uiState.selectedMode == WalletCreationMode.CREATE,
                onClick = { onSelectMode(WalletCreationMode.CREATE) },
            )
            P01Tab(
                text = "硬件钱包",
                selected = false,
                onClick = { onSelectMode(WalletCreationMode.IMPORT) },
            )
            P01Tab(
                text = "观察钱包",
                selected = uiState.selectedMode == WalletCreationMode.IMPORT,
                onClick = { onSelectMode(WalletCreationMode.IMPORT) },
            )
        }

        P01Card(
            modifier = Modifier.clickable { onSelectMode(WalletCreationMode.CREATE) },
        ) {
            P01CardHeader(
                title = "创建新钱包",
                trailing = { P01Chip(text = if (uiState.selectedMode == WalletCreationMode.CREATE) "当前选择" else "未接入") },
            )
            P01CardCopy("当前尚未接入真实钱包创建引擎；本页只保留后续流程入口与状态说明。")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Solana", "TRON", "Ethereum", "Base").forEach { label ->
                    P01Chip(text = label)
                }
            }
        }

        P01Card(
            modifier = Modifier.clickable { onSelectMode(WalletCreationMode.IMPORT) },
        ) {
            P01CardHeader(title = "导入助记词 / 私钥")
            P01CardCopy("导入相关页面当前只暴露阻塞和本地状态，不再伪装成已完成的钱包恢复能力。")
        }

        P01Card(
            modifier = Modifier.clickable { onSelectMode(WalletCreationMode.IMPORT) },
        ) {
            P01CardHeader(title = "仅观察模式")
            P01CardCopy("观察模式仍待真实钱包地址源接入；当前仅保留路线入口。")
        }

        P01Card {
            P01CardHeader(
                title = "安全策略",
                trailing = { P01Chip(text = "本地状态") },
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                P01Card(
                    modifier = Modifier.weight(1f),
                ) {
                    P01CardCopy("Biometric")
                    androidx.compose.material3.Text("待接真实策略")
                }
                P01Card(
                    modifier = Modifier.weight(1f),
                ) {
                    P01CardCopy("Backup")
                    androidx.compose.material3.Text("仅流程说明")
                }
            }
        }

        P01PrimaryButton(
            text = uiState.primaryActionLabel,
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun WalletOnboardingPreview() {
    CryptoVpnTheme {
        WalletOnboardingScreen(
            uiState = WalletOnboardingViewModel(MockP0Repository()).uiState.value,
            onSelectMode = {},
            onContinue = {},
        )
    }
}
