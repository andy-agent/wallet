package com.v2ray.ang.composeui.pages.p0

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.model.OptionalUpdateEvent
import com.v2ray.ang.composeui.p0.model.OptionalUpdateUiState
import com.v2ray.ang.composeui.p0.model.optionalUpdatePreviewState
import com.v2ray.ang.composeui.p0.ui.P01ButtonRow
import com.v2ray.ang.composeui.p0.ui.P01Card
import com.v2ray.ang.composeui.p0.ui.P01CardCopy
import com.v2ray.ang.composeui.p0.ui.P01CardHeader
import com.v2ray.ang.composeui.p0.ui.P01Chip
import com.v2ray.ang.composeui.p0.ui.P01MetricCell
import com.v2ray.ang.composeui.p0.ui.P01MetricGrid
import com.v2ray.ang.composeui.p0.ui.P01PhoneScaffold
import com.v2ray.ang.composeui.p0.ui.P01SuccessBadge
import com.v2ray.ang.composeui.p0.viewmodel.OptionalUpdateViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun OptionalUpdateRoute(
    viewModel: OptionalUpdateViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    OptionalUpdateScreen(
        uiState = uiState,
        onPrimaryAction = {
            viewModel.onEvent(OptionalUpdateEvent.PrimaryActionClicked)
            onPrimaryAction()
        },
        onSecondaryAction = {
            viewModel.onEvent(OptionalUpdateEvent.SecondaryActionClicked)
            onSecondaryAction?.invoke()
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun OptionalUpdateScreen(
    uiState: OptionalUpdateUiState,
    onPrimaryAction: () -> Unit,
    onSecondaryAction: () -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P01PhoneScaffold(
        statusTime = "18:16",
        currentRoute = CryptoVpnRouteSpec.optionalUpdate.name,
        onBottomNav = onBottomNav,
    ) {
        P01Card(centered = true) {
            P01SuccessBadge(symbol = "v", tint = Color(0xFF49D89B))
            P01CardHeader(
                title = uiState.title,
                subtitle = uiState.subtitle,
                trailing = { P01Chip(text = uiState.badge) },
            )
            P01CardCopy(uiState.summary)
            if (uiState.metrics.isNotEmpty()) {
                P01MetricGrid(
                    items = uiState.metrics.take(4).map { P01MetricCell(it.label, it.value) },
                )
            }
            if (uiState.highlights.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    uiState.highlights.take(2).forEach { item ->
                        P01Chip(text = "${item.title} · ${item.trailing}")
                    }
                }
            }
            P01CardCopy(uiState.note)
            if (!uiState.primaryActionLabel.isNullOrBlank() || !uiState.secondaryActionLabel.isNullOrBlank()) {
                P01ButtonRow(
                    primaryLabel = uiState.primaryActionLabel ?: "",
                    onPrimaryClick = onPrimaryAction,
                    secondaryLabel = uiState.secondaryActionLabel,
                    onSecondaryClick = onSecondaryAction,
                )
            } else {
                P01CardCopy("当前未接入应用内更新分发与安装能力，因此不展示可执行更新按钮。")
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun OptionalUpdatePreview() {
    CryptoVpnTheme {
        OptionalUpdateScreen(
            uiState = optionalUpdatePreviewState(),
            onPrimaryAction = {},
            onSecondaryAction = {},
        )
    }
}
