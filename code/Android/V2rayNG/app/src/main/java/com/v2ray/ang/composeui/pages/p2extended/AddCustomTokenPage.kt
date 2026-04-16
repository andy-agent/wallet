package com.v2ray.ang.composeui.pages.p2extended

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.components.actions.ActionCluster
import com.v2ray.ang.composeui.components.actions.ActionClusterAction
import com.v2ray.ang.composeui.components.app.AppPageBackgroundStyle
import com.v2ray.ang.composeui.components.app.AppPageScaffold
import com.v2ray.ang.composeui.components.buttons.AppButtonVariant
import com.v2ray.ang.composeui.components.cards.AppCard
import com.v2ray.ang.composeui.components.cards.MetricCard
import com.v2ray.ang.composeui.components.chips.AppChip
import com.v2ray.ang.composeui.components.chips.AppChipTone
import com.v2ray.ang.composeui.components.feedback.EmptyStateCard
import com.v2ray.ang.composeui.components.inputs.AppTextField
import com.v2ray.ang.composeui.components.listitems.AppListCardItem
import com.v2ray.ang.composeui.components.navigation.AppTopBar
import com.v2ray.ang.composeui.components.navigation.AppTopBarMode
import com.v2ray.ang.composeui.components.navigation.CryptoVpnBottomBar
import com.v2ray.ang.composeui.p2extended.model.AddCustomTokenEvent
import com.v2ray.ang.composeui.p2extended.model.AddCustomTokenUiState
import com.v2ray.ang.composeui.p2extended.model.addCustomTokenPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.AddCustomTokenViewModel
import com.v2ray.ang.composeui.theme.AppTheme
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.theme.tokens.OverviewBaselineTokens

private val AddTokenGlowBlue = Color(0x224F7CFF)
private val AddTokenGlowPurple = Color(0x148C7CFF)

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
    val baseline = OverviewBaselineTokens.primary

    AppPageScaffold(
        backgroundStyle = AppPageBackgroundStyle.Hero,
        background = { AddCustomTokenBackgroundGlow() },
        bottomBar = {
            CryptoVpnBottomBar(
                currentRoute = "wallet_home",
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
            verticalArrangement = Arrangement.spacedBy(baseline.sectionGap),
        ) {
            AppTopBar(
                title = uiState.title,
                subtitle = uiState.subtitle,
                mode = AppTopBarMode.Hero,
                actions = {
                    uiState.badge.takeIf { it.isNotBlank() }?.let {
                        AppChip(text = it, tone = AppChipTone.Info)
                    }
                },
            )

            androidx.compose.foundation.layout.Row(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.space12),
            ) {
                uiState.metrics.chunked(2).firstOrNull().orEmpty().forEach { metric ->
                    MetricCard(
                        title = metric.label,
                        value = metric.value,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (uiState.metrics.size == 1) {
                    Box(modifier = Modifier.weight(1f))
                }
            }
            if (uiState.metrics.size > 2) {
                androidx.compose.foundation.layout.Row(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.space12),
                ) {
                    uiState.metrics.drop(2).take(2).forEach { metric ->
                        MetricCard(
                            title = metric.label,
                            value = metric.value,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (uiState.metrics.drop(2).size == 1) {
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }

            if (uiState.fields.isEmpty()) {
                EmptyStateCard(
                    title = "暂无可录入字段",
                    message = uiState.note.ifBlank { "当前链不支持手动添加代币。" },
                )
            } else {
                AppCard {
                    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space12)) {
                        uiState.fields.forEach { field ->
                            AppTextField(
                                value = field.value,
                                label = field.label,
                                onValueChange = { onEvent(AddCustomTokenEvent.FieldChanged(field.key, it)) },
                                placeholder = field.placeholder,
                                supportingText = field.supportingText,
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                            )
                        }
                    }
                }
            }

            if (uiState.highlights.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space12)) {
                    uiState.highlights.forEach { item ->
                        AppListCardItem(
                            title = item.title,
                            subtitle = item.subtitle,
                            value = item.trailing,
                            trailing = {
                                if (item.badge.isNotBlank()) {
                                    AppChip(
                                        text = item.badge,
                                        tone = when {
                                            item.trailing.contains("阻塞") || item.trailing.contains("待接入") -> AppChipTone.Warning
                                            else -> AppChipTone.Info
                                        },
                                    )
                                }
                            },
                        )
                    }
                }
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
}

@Composable
private fun AddCustomTokenBackgroundGlow() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 36.dp),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(220.dp)
                .background(AddTokenGlowBlue, RoundedCornerShape(999.dp))
                .blur(48.dp),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(top = 320.dp)
                .size(260.dp)
                .background(AddTokenGlowPurple, RoundedCornerShape(999.dp))
                .blur(60.dp),
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
