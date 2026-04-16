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
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.components.actions.ActionCluster
import com.v2ray.ang.composeui.components.actions.ActionClusterAction
import com.v2ray.ang.composeui.components.app.AppPageBackgroundStyle
import com.v2ray.ang.composeui.components.app.AppPageScaffold
import com.v2ray.ang.composeui.components.buttons.AppButtonVariant
import com.v2ray.ang.composeui.components.cards.MetricCard
import com.v2ray.ang.composeui.components.chips.AppChip
import com.v2ray.ang.composeui.components.chips.AppChipTone
import com.v2ray.ang.composeui.components.feedback.EmptyStateCard
import com.v2ray.ang.composeui.components.listitems.AppListCardItem
import com.v2ray.ang.composeui.components.navigation.AppTopBar
import com.v2ray.ang.composeui.components.navigation.AppTopBarMode
import com.v2ray.ang.composeui.components.navigation.CryptoVpnBottomBar
import com.v2ray.ang.composeui.p2extended.model.ChainManagerEvent
import com.v2ray.ang.composeui.p2extended.model.ChainManagerUiState
import com.v2ray.ang.composeui.p2extended.model.chainManagerPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.ChainManagerViewModel
import com.v2ray.ang.composeui.theme.AppTheme
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.theme.tokens.OverviewBaselineTokens

private val ChainGlowBlue = Color(0x224F7CFF)
private val ChainGlowMint = Color(0x162ED8A3)

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
    val baseline = OverviewBaselineTokens.primary

    AppPageScaffold(
        backgroundStyle = AppPageBackgroundStyle.Hero,
        background = { ChainManagerBackgroundGlow() },
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

            MetricGridRow(
                metrics = uiState.metrics.map { it.label to it.value },
            )

            if (uiState.highlights.isEmpty()) {
                EmptyStateCard(
                    title = "暂无链配置",
                    message = uiState.note.ifBlank { "当前钱包还没有可展示的链配置。" },
                    actionLabel = uiState.primaryActionLabel,
                    onAction = { onEvent(ChainManagerEvent.PrimaryActionClicked) },
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space12),
                ) {
                    uiState.highlights.forEachIndexed { index, item ->
                        AppListCardItem(
                            title = item.title,
                            subtitle = item.subtitle,
                            value = item.trailing,
                            emphasized = index == 0,
                            trailing = {
                                if (item.badge.isNotBlank()) {
                                    AppChip(
                                        text = item.badge,
                                        tone = chainBadgeTone(item),
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
}

@Composable
private fun MetricGridRow(
    metrics: List<Pair<String, String>>,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space12)) {
        metrics.chunked(2).forEach { row ->
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.space12),
            ) {
                row.forEach { (label, value) ->
                    MetricCard(
                        title = label,
                        value = value,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) {
                    Box(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

private fun chainBadgeTone(item: FeatureListItem): AppChipTone = when {
    item.trailing.contains("已配置", ignoreCase = true) -> AppChipTone.Success
    item.trailing.contains("待配置", ignoreCase = true) -> AppChipTone.Warning
    item.badge.equals("REAL", ignoreCase = true) -> AppChipTone.Info
    else -> AppChipTone.Neutral
}

@Composable
private fun ChainManagerBackgroundGlow() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 36.dp),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(220.dp)
                .background(ChainGlowBlue, RoundedCornerShape(999.dp))
                .blur(48.dp),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(top = 320.dp)
                .size(260.dp)
                .background(ChainGlowMint, RoundedCornerShape(999.dp))
                .blur(60.dp),
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
