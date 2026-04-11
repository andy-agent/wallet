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
    val selectedModeCopy = when (uiState.selectedMode) {
        WalletCreationMode.CREATE -> "当前仅保留创建流程的状态说明，真实创建引擎尚未接通。"
        WalletCreationMode.IMPORT -> "当前仅保留导入流程的阻塞说明，真实助记词/私钥导入尚未接通。"
    }
    P01PhoneScaffold(
        statusTime = "18:06",
        currentRoute = CryptoVpnRouteSpec.walletHome.name,
        onBottomNav = onBottomNav,
    ) {
        P01Header(
            eyebrow = "MULTI-CHAIN WALLET SETUP",
            title = "配置你的多链钱包",
            subtitle = uiState.statusMessage ?: "当前钱包能力仍在逐步接入，只展示真实状态和阻塞说明。",
            chips = listOfNotNull(
                uiState.accountLabel.takeIf { it.isNotBlank() }?.let { "账号 $it" },
                when (uiState.selectedMode) {
                    WalletCreationMode.CREATE -> "创建流程"
                    WalletCreationMode.IMPORT -> "导入流程"
                },
            ),
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
            P01Tab(
                text = "观察钱包",
                selected = false,
                onClick = { onSelectMode(WalletCreationMode.IMPORT) },
            )
        }

        uiState.unavailableMessage?.let {
            P01Card {
                P01CardHeader(title = "当前阻塞")
                P01CardCopy(it)
            }
        }

        uiState.errorMessage?.let {
            P01Card {
                P01CardHeader(title = "错误状态")
                P01CardCopy(it)
            }
        }

        uiState.emptyMessage?.let {
            P01Card {
                P01CardHeader(title = "空状态")
                P01CardCopy(it)
            }
        }

        P01Card(
            modifier = Modifier.clickable { onSelectMode(WalletCreationMode.CREATE) },
        ) {
            P01CardHeader(
                title = "创建新钱包",
                trailing = {
                    P01Chip(
                        text = if (uiState.selectedMode == WalletCreationMode.CREATE) {
                            "当前查看"
                        } else {
                            "未激活"
                        },
                    )
                },
            )
            P01CardCopy(
                if (uiState.selectedMode == WalletCreationMode.CREATE) {
                    selectedModeCopy
                } else {
                    "切到该模式后查看创建流程的真实阻塞与状态。"
                },
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Solana", "TRON", "Ethereum", "Base").forEach { label ->
                    P01Chip(text = label)
                }
            }
        }

        P01Card(
            modifier = Modifier.clickable { onSelectMode(WalletCreationMode.IMPORT) },
        ) {
            P01CardHeader(
                title = "导入助记词 / 私钥",
                trailing = {
                    P01Chip(
                        text = if (uiState.selectedMode == WalletCreationMode.IMPORT) {
                            "当前查看"
                        } else {
                            "未激活"
                        },
                    )
                },
            )
            P01CardCopy(
                if (uiState.selectedMode == WalletCreationMode.IMPORT) {
                    selectedModeCopy
                } else {
                    "切到该模式后查看导入流程的真实阻塞与状态。"
                },
            )
        }

        P01Card(
            modifier = Modifier.clickable { onSelectMode(WalletCreationMode.IMPORT) },
        ) {
            P01CardHeader(title = "仅观察模式")
            P01CardCopy("观察模式仍待真实钱包地址源接入；当前不会伪装成可直接添加观察地址。")
        }

        P01Card {
            P01CardHeader(
                title = "当前能力",
                trailing = { P01Chip(text = "真实状态") },
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                P01Card(
                    modifier = Modifier.weight(1f),
                ) {
                    P01CardCopy("账号状态")
                    androidx.compose.material3.Text(if (uiState.accountLabel.isBlank()) "未登录" else "已识别账号")
                }
                P01Card(
                    modifier = Modifier.weight(1f),
                ) {
                    P01CardCopy("流程能力")
                    androidx.compose.material3.Text(if (uiState.primaryActionLabel.isNullOrBlank()) "仅说明页" else "可查看钱包状态")
                }
            }
        }

        if (uiState.primaryActionLabel.isNullOrBlank()) {
            P01CardCopy("当前没有可执行的钱包创建/导入动作，页面只保留真实状态与阻塞说明。")
        } else {
            P01PrimaryButton(
                text = uiState.primaryActionLabel,
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
            )
        }
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
