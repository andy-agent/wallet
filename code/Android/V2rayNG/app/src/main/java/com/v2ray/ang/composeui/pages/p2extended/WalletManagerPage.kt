package com.v2ray.ang.composeui.pages.p2extended

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.components.actions.ActionCluster
import com.v2ray.ang.composeui.components.actions.ActionClusterAction
import com.v2ray.ang.composeui.components.app.AppPageBackgroundStyle
import com.v2ray.ang.composeui.components.app.AppPageScaffold
import com.v2ray.ang.composeui.components.buttons.AppButtonVariant
import com.v2ray.ang.composeui.components.chips.AppChip
import com.v2ray.ang.composeui.components.chips.AppChipTone
import com.v2ray.ang.composeui.components.navigation.AppTopBar
import com.v2ray.ang.composeui.components.navigation.AppTopBarMode
import com.v2ray.ang.composeui.components.navigation.CryptoVpnBottomBar
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p2extended.model.WalletManagerEvent
import com.v2ray.ang.composeui.p2extended.model.WalletManagerWalletItemUi
import com.v2ray.ang.composeui.p2extended.model.WalletManagerUiState
import com.v2ray.ang.composeui.p2extended.model.walletManagerPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.WalletManagerViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.theme.tokens.OverviewBaselineTokens

@Composable
fun WalletManagerRoute(
    viewModel: WalletManagerViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    WalletManagerScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                WalletManagerEvent.PrimaryActionClicked -> onPrimaryAction()
                WalletManagerEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                is WalletManagerEvent.WalletSelected -> {
                    viewModel.submitSetDefault(
                        walletId = event.walletId,
                        onError = { message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        },
                    )
                }
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun WalletManagerScreen(
    uiState: WalletManagerUiState,
    onEvent: (WalletManagerEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val baseline = OverviewBaselineTokens.primary
    AppPageScaffold(
        backgroundStyle = AppPageBackgroundStyle.Hero,
        bottomBar = {
            CryptoVpnBottomBar(
                currentRoute = CryptoVpnRouteSpec.walletHome.name,
                onRouteSelected = onBottomNav,
            )
        },
        contentPadding = PaddingValues(
            horizontal = baseline.pageHorizontal,
            vertical = baseline.pageTopSpacing,
        ),
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 680.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AppTopBar(
                title = uiState.title,
                subtitle = uiState.subtitle,
                mode = AppTopBarMode.Hero,
            )
            uiState.metrics.forEach { metric ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(metric.label, style = MaterialTheme.typography.bodyMedium)
                    Text(metric.value, style = MaterialTheme.typography.titleMedium)
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                uiState.wallets.forEach { wallet ->
                    WalletCard(
                        item = wallet,
                        onClick = { onEvent(WalletManagerEvent.WalletSelected(wallet.walletId)) },
                    )
                }
            }

            ActionCluster(
                actions = listOf(
                    ActionClusterAction(
                        label = uiState.secondaryActionLabel ?: "导入观察钱包",
                        onClick = { onEvent(WalletManagerEvent.SecondaryActionClicked) },
                        variant = AppButtonVariant.Secondary,
                    ),
                    ActionClusterAction(
                        label = uiState.primaryActionLabel,
                        onClick = { onEvent(WalletManagerEvent.PrimaryActionClicked) },
                        variant = AppButtonVariant.Primary,
                    ),
                ),
            )
        }
    }
}

@Composable
private fun WalletCard(
    item: WalletManagerWalletItemUi,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        tonalElevation = 1.dp,
        shadowElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(item.walletName, style = MaterialTheme.typography.titleMedium)
                if (item.isDefault) {
                    AppChip(text = "默认", tone = AppChipTone.Brand)
                }
            }
            Text(item.subtitle, style = MaterialTheme.typography.bodySmall)
            Text(item.walletKind, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun WalletManagerPreview() {
    CryptoVpnTheme {
        WalletManagerScreen(
            uiState = walletManagerPreviewState().copy(
                wallets = listOf(
                    WalletManagerWalletItemUi(
                        walletId = "wallet-1",
                        walletName = "Main Wallet",
                        walletKind = "SELF_CUSTODY",
                        isDefault = true,
                        isArchived = false,
                        subtitle = "CREATED · SIGNABLE",
                    ),
                    WalletManagerWalletItemUi(
                        walletId = "wallet-2",
                        walletName = "Watch Wallet",
                        walletKind = "WATCH_ONLY",
                        isDefault = false,
                        isArchived = false,
                        subtitle = "WATCH_IMPORTED · VIEW_ONLY",
                    ),
                ),
            ),
            onEvent = {},
        )
    }
}
