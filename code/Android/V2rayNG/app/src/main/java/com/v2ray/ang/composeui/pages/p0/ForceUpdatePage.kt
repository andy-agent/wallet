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
import com.v2ray.ang.composeui.p0.model.ForceUpdateEvent
import com.v2ray.ang.composeui.p0.model.ForceUpdateUiState
import com.v2ray.ang.composeui.p0.model.forceUpdatePreviewState
import com.v2ray.ang.composeui.p0.ui.P01ButtonRow
import com.v2ray.ang.composeui.p0.ui.P01Card
import com.v2ray.ang.composeui.p0.ui.P01CardCopy
import com.v2ray.ang.composeui.p0.ui.P01CardHeader
import com.v2ray.ang.composeui.p0.ui.P01Chip
import com.v2ray.ang.composeui.p0.ui.P01MetricCell
import com.v2ray.ang.composeui.p0.ui.P01MetricGrid
import com.v2ray.ang.composeui.p0.ui.P01PhoneScaffold
import com.v2ray.ang.composeui.p0.ui.P01SuccessBadge
import com.v2ray.ang.composeui.p0.viewmodel.ForceUpdateViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun ForceUpdateRoute(
    viewModel: ForceUpdateViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    ForceUpdateScreen(
        uiState = uiState,
        onPrimaryAction = {
            viewModel.onEvent(ForceUpdateEvent.PrimaryActionClicked)
            onPrimaryAction()
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun ForceUpdateScreen(
    uiState: ForceUpdateUiState,
    onPrimaryAction: () -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P01PhoneScaffold(
        statusTime = "18:15",
        currentRoute = CryptoVpnRouteSpec.forceUpdate.name,
        onBottomNav = onBottomNav,
    ) {
        P01Card(centered = true) {
            P01SuccessBadge(symbol = "!", tint = Color(0xFFF6B155))
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
                )
            } else {
                P01CardCopy("当前未接入强制升级检查、下载来源和安装流程，因此只展示阻塞信息。")
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ForceUpdatePreview() {
    CryptoVpnTheme {
        ForceUpdateScreen(
            uiState = forceUpdatePreviewState(),
            onPrimaryAction = {},
        )
    }
}
