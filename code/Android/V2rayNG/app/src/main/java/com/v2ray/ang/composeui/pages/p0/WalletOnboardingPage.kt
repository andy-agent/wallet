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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.model.WalletCreationMode
import com.v2ray.ang.composeui.p0.model.WalletOnboardingEvent
import com.v2ray.ang.composeui.p0.model.WalletOnboardingUiState
import com.v2ray.ang.composeui.p0.model.resolveWalletActionLabel
import com.v2ray.ang.composeui.p0.model.walletOnboardingPreviewState
import com.v2ray.ang.composeui.p0.ui.P01Card
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
    onCreateWallet: () -> Unit,
    onImportWallet: () -> Unit,
    onContinue: () -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(
        uiState.walletExists,
        uiState.walletDisplayName,
        uiState.lifecycleStatus,
        uiState.walletNextAction,
        uiState.walletId,
    ) {
        val canEnterWalletHome =
            uiState.walletExists ||
                (
                    !uiState.walletDisplayName.isNullOrBlank() &&
                        uiState.lifecycleStatus.equals("ACTIVE", ignoreCase = true) &&
                        uiState.walletNextAction.equals("READY", ignoreCase = true)
                )
        if (canEnterWalletHome) {
            onContinue()
        }
    }
    WalletOnboardingScreen(
        uiState = uiState,
        onSelectMode = { viewModel.onEvent(WalletOnboardingEvent.SelectMode(it)) },
        onCreateWallet = {
            viewModel.onEvent(WalletOnboardingEvent.SelectMode(WalletCreationMode.CREATE))
            onCreateWallet()
        },
        onImportWallet = {
            viewModel.onEvent(WalletOnboardingEvent.SelectMode(WalletCreationMode.IMPORT))
            onImportWallet()
        },
        onContinue = {
            viewModel.onEvent(WalletOnboardingEvent.ContinueClicked)
            onContinue()
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun WalletOnboardingScreen(
    uiState: WalletOnboardingUiState,
    onSelectMode: (WalletCreationMode) -> Unit,
    onCreateWallet: () -> Unit = {},
    onImportWallet: () -> Unit = {},
    onContinue: () -> Unit = {},
    onBottomNav: (String) -> Unit = {},
) {
    P01PhoneScaffold(
        currentRoute = CryptoVpnRouteSpec.walletHome.name,
        onBottomNav = onBottomNav,
    ) {
        P01Header(
            eyebrow = "MULTI-CHAIN WALLET SETUP",
            title = "配置您的多链钱包",
            subtitle = uiState.summary,
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
                text = "创建钱包",
                selected = uiState.selectedMode == WalletCreationMode.CREATE,
                onClick = { onSelectMode(WalletCreationMode.CREATE) },
            )
            P01Tab(
                text = "导入钱包",
                selected = uiState.selectedMode == WalletCreationMode.IMPORT,
                onClick = { onSelectMode(WalletCreationMode.IMPORT) },
            )
        }

        P01Card(
            modifier = Modifier.clickable { onCreateWallet() },
        ) {
            P01CardHeader(
                title = "创建新钱包",
                trailing = { P01Chip(text = "推荐") },
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.focusedChains.forEach { label ->
                    P01Chip(text = label)
                }
            }
        }

        P01Card(
            modifier = Modifier.clickable { onImportWallet() },
        ) {
            P01CardHeader(title = "导入助记词 / 私钥")
        }

        P01Card {
            P01CardHeader(
                title = uiState.walletDisplayName ?: "当前状态",
                trailing = { P01Chip(text = uiState.lifecycleStatus) },
                subtitle = uiState.accountLabel,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                P01Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onContinue() },
                ) {
                    Text(
                        text = uiState.resolveWalletActionLabel(),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                P01Card(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = if (uiState.walletExists) "已建立" else "未建立",
                        style = MaterialTheme.typography.titleMedium,
                    )
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
            uiState = walletOnboardingPreviewState(),
            onSelectMode = {},
            onCreateWallet = {},
            onImportWallet = {},
            onContinue = {},
        )
    }
}
