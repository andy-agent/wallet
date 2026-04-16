package com.v2ray.ang.composeui.pages.p2extended

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.components.actions.ActionCluster
import com.v2ray.ang.composeui.components.actions.ActionClusterAction
import com.v2ray.ang.composeui.components.buttons.AppButtonVariant
import com.v2ray.ang.composeui.components.wallet.chain.ChainFeatureSection
import com.v2ray.ang.composeui.components.wallet.chain.ChainManagementScaffold
import com.v2ray.ang.composeui.components.wallet.chain.ChainMetricSection
import com.v2ray.ang.composeui.p2extended.model.ChainManagerEvent
import com.v2ray.ang.composeui.p2extended.model.ChainManagerUiState
import com.v2ray.ang.composeui.p2extended.model.chainManagerPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.ChainManagerViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun ChainManagerRoute(
    viewModel: ChainManagerViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    ChainManagerScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                ChainManagerEvent.PrimaryActionClicked -> onPrimaryAction()
                ChainManagerEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun ChainManagerScreen(
    uiState: ChainManagerUiState,
    onEvent: (ChainManagerEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    ChainManagementScaffold(
        title = uiState.title,
        subtitle = uiState.subtitle,
        badge = uiState.badge,
        summary = uiState.summary,
        currentRoute = "wallet_home",
        onBottomNav = onBottomNav,
    ) {
        ChainMetricSection(metrics = uiState.metrics)

        ChainFeatureSection(
            items = uiState.highlights,
            emptyTitle = "暂无链配置",
            emptyMessage = uiState.note.ifBlank { "当前钱包还没有可展示的链配置。" },
        )

        ActionCluster(
            actions = listOfNotNull(
                ActionClusterAction(
                    label = uiState.primaryActionLabel,
                    onClick = { onEvent(ChainManagerEvent.PrimaryActionClicked) },
                    variant = AppButtonVariant.Primary,
                ),
                uiState.secondaryActionLabel?.takeIf { it.isNotBlank() }?.let {
                    ActionClusterAction(
                        label = it,
                        onClick = { onEvent(ChainManagerEvent.SecondaryActionClicked) },
                        variant = AppButtonVariant.Secondary,
                    )
                },
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ChainManagerPreview() {
    CryptoVpnTheme {
        Surface {
            ChainManagerScreen(
                uiState = chainManagerPreviewState(),
                onEvent = {},
            )
        }
    }
}
