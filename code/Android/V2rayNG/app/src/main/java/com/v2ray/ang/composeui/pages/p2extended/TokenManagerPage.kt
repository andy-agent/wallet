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
import com.v2ray.ang.composeui.p2extended.model.TokenManagerEvent
import com.v2ray.ang.composeui.p2extended.model.TokenManagerUiState
import com.v2ray.ang.composeui.p2extended.model.tokenManagerPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.TokenManagerViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun TokenManagerRoute(
    viewModel: TokenManagerViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    TokenManagerScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                TokenManagerEvent.PrimaryActionClicked -> onPrimaryAction()
                TokenManagerEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun TokenManagerScreen(
    uiState: TokenManagerUiState,
    onEvent: (TokenManagerEvent) -> Unit,
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
            emptyTitle = "暂无可管理代币",
            emptyMessage = uiState.note.ifBlank { "当前钱包当前链下没有可显示的代币。" },
        )

        ActionCluster(
            actions = listOfNotNull(
                ActionClusterAction(
                    label = uiState.primaryActionLabel,
                    onClick = { onEvent(TokenManagerEvent.PrimaryActionClicked) },
                    variant = AppButtonVariant.Primary,
                ),
                uiState.secondaryActionLabel?.takeIf { it.isNotBlank() }?.let {
                    ActionClusterAction(
                        label = it,
                        onClick = { onEvent(TokenManagerEvent.SecondaryActionClicked) },
                        variant = AppButtonVariant.Secondary,
                    )
                },
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun TokenManagerPreview() {
    CryptoVpnTheme {
        Surface {
            TokenManagerScreen(
                uiState = tokenManagerPreviewState(),
                onEvent = {},
            )
        }
    }
}
