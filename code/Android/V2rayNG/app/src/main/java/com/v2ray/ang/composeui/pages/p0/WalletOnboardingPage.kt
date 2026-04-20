package com.v2ray.ang.composeui.pages.p0

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.model.WalletCreationMode
import com.v2ray.ang.composeui.p0.model.WalletOnboardingEvent
import com.v2ray.ang.composeui.p0.model.WalletOnboardingUiState
import com.v2ray.ang.composeui.p0.model.walletOnboardingPreviewState
import com.v2ray.ang.composeui.p0.ui.P01Card
import com.v2ray.ang.composeui.p0.ui.P01CardHeader
import com.v2ray.ang.composeui.p0.ui.P01Chip
import com.v2ray.ang.composeui.p0.ui.P01Header
import com.v2ray.ang.composeui.p0.ui.P01PhoneScaffold
import com.v2ray.ang.composeui.p0.ui.P01PrimaryButton
import com.v2ray.ang.composeui.p0.ui.P01Tab
import com.v2ray.ang.composeui.p0.viewmodel.WalletOnboardingViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun WalletOnboardingRoute(
    viewModel: WalletOnboardingViewModel,
    onCreateWallet: () -> Unit,
    onImportWallet: () -> Unit,
    onImportWatchWallet: () -> Unit,
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
        onImportWatchWallet = onImportWatchWallet,
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
    onImportWatchWallet: () -> Unit = {},
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

        WalletOnboardingActionCard(
            title = "创建新钱包",
            subtitle = null,
            trailing = { P01Chip(text = "推荐") },
            onClick = onCreateWallet,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.focusedChains.forEach { label ->
                    P01Chip(text = label)
                }
            }
        }

        WalletOnboardingActionCard(
            title = "导入助记词 / 私钥",
            subtitle = null,
            onClick = onImportWallet,
        )

        WalletOnboardingActionCard(
            title = "观察钱包",
            subtitle = "导入只读地址用于查看资产",
            onClick = onImportWatchWallet,
        )

        P01PrimaryButton(
            text = uiState.primaryActionLabel,
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun WalletOnboardingActionCard(
    title: String,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
    content: @Composable (() -> Unit)? = null,
) {
    P01Card(
        modifier = Modifier.clickable { onClick() },
    ) {
        P01CardHeader(
            title = title,
            trailing = trailing,
            subtitle = subtitle,
        )
        content?.invoke()
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
            onImportWatchWallet = {},
            onContinue = {},
        )
    }
}
