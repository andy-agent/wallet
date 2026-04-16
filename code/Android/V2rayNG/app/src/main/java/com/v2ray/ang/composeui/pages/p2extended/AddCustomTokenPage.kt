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
import com.v2ray.ang.composeui.components.wallet.chain.CustomTokenFormSection
import com.v2ray.ang.composeui.components.feedback.EmptyStateCard
import com.v2ray.ang.composeui.p2extended.model.AddCustomTokenEvent
import com.v2ray.ang.composeui.p2extended.model.AddCustomTokenUiState
import com.v2ray.ang.composeui.p2extended.model.addCustomTokenPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.AddCustomTokenViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun AddCustomTokenRoute(
    viewModel: AddCustomTokenViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    AddCustomTokenScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                AddCustomTokenEvent.PrimaryActionClicked -> onPrimaryAction()
                AddCustomTokenEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun AddCustomTokenScreen(
    uiState: AddCustomTokenUiState,
    onEvent: (AddCustomTokenEvent) -> Unit,
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

        if (uiState.fields.isEmpty()) {
            EmptyStateCard(
                title = "暂无可录入字段",
                message = uiState.note.ifBlank { "当前链不支持手动添加代币。" },
            )
        } else {
            CustomTokenFormSection(
                fields = uiState.fields,
                onFieldChanged = { key, value ->
                    onEvent(AddCustomTokenEvent.FieldChanged(key, value))
                },
            )
        }

        if (uiState.highlights.isNotEmpty()) {
            ChainFeatureSection(
                items = uiState.highlights,
                emptyTitle = "暂无链侧说明",
                emptyMessage = uiState.note,
            )
        }

        ActionCluster(
            actions = listOfNotNull(
                ActionClusterAction(
                    label = uiState.primaryActionLabel,
                    onClick = { onEvent(AddCustomTokenEvent.PrimaryActionClicked) },
                    variant = AppButtonVariant.Primary,
                ),
                uiState.secondaryActionLabel?.takeIf { it.isNotBlank() }?.let {
                    ActionClusterAction(
                        label = it,
                        onClick = { onEvent(AddCustomTokenEvent.SecondaryActionClicked) },
                        variant = AppButtonVariant.Secondary,
                    )
                },
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun AddCustomTokenPreview() {
    CryptoVpnTheme {
        Surface {
            AddCustomTokenScreen(
                uiState = addCustomTokenPreviewState(),
                onEvent = {},
            )
        }
    }
}
