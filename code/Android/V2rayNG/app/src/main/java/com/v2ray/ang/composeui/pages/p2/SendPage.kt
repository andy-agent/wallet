package com.v2ray.ang.composeui.pages.p2

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import com.v2ray.ang.composeui.components.inputs.GlassTextField
import com.v2ray.ang.composeui.p2.model.SendEvent
import com.v2ray.ang.composeui.p2.model.SendUiState
import com.v2ray.ang.composeui.p2.model.sendPreviewState
import com.v2ray.ang.composeui.p2.viewmodel.SendViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun SendRoute(
    viewModel: SendViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(uiState.redirectRoute) {
        uiState.redirectRoute?.let { onBottomNav(it) }
    }
    SendScreen(
        uiState = uiState,
        onEvent = { event -> viewModel.onEvent(event) },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun SendScreen(
    uiState: SendUiState,
    onEvent: (SendEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val context = LocalContext.current
    LaunchedEffect(uiState.feedbackMessage) {
        uiState.feedbackMessage
            ?.takeIf { it.isNotBlank() && it != "正在发送..." }
            ?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }
    val infoItems = if (uiState.fields.isNotEmpty()) {
        uiState.fields.take(4).map { it.label to it.value }
    } else {
        uiState.metrics.take(4).map { it.label to it.value }
    }
    val safetyStatus = uiState.checklist.firstOrNull { it.title.contains("广播") }?.detail
        ?: if (uiState.highlights.isEmpty()) "待检查" else "已载入 ${uiState.highlights.size} 项"
    P2CorePageScaffold(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        badge = uiState.badge,
        activeSection = CoreNavSection.Wallet,
        onBottomNav = onBottomNav,
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = {
            onEvent(SendEvent.PrimaryActionClicked)
        },
    ) {
        P2CoreHeroValueCard(
            label = uiState.availableBalanceLabel,
            value = uiState.availableBalance,
            supportingText = uiState.balanceSupportingText,
            highlight = uiState.badge,
            stats = listOf(
                "广播能力" to (uiState.metrics.getOrNull(2)?.value ?: "--"),
                "预检查" to (uiState.metrics.getOrNull(3)?.value ?: "--"),
            ),
        )
        if (uiState.networkOptions.isNotEmpty()) {
            SendFilterRow(
                chips = uiState.networkOptions.map { it.label to it.selected },
                onChipClick = { index ->
                    uiState.networkOptions.getOrNull(index)?.let { option ->
                        if (!option.selected) {
                            onEvent(SendEvent.NetworkSelected(option.id))
                        }
                    }
                },
            )
        }
        if (uiState.assetOptions.isNotEmpty()) {
            SendFilterRow(
                chips = uiState.assetOptions.map { it.label to it.selected },
                onChipClick = { index ->
                    uiState.assetOptions.getOrNull(index)?.let { option ->
                        if (!option.selected) {
                            onEvent(SendEvent.AssetSelected(option.id))
                        }
                    }
                },
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            uiState.fields.forEach { field ->
                GlassTextField(
                    value = field.value,
                    label = field.label,
                    onValueChange = { onEvent(SendEvent.FieldChanged(field.key, it)) },
                )
                if (field.supportingText.isNotBlank()) {
                    Text(
                        text = field.supportingText,
                        color = Color(0xFF66739D),
                    )
                }
            }
        }
        P2CoreChartInfoBlock(
            title = "发送参数",
            subtitle = uiState.note,
            chips = infoItems.map { it.first }.take(4),
            infoItems = infoItems,
            highlight = uiState.badge,
            accent = Color(0xFF19B78C),
        )
        if (uiState.highlights.isNotEmpty()) {
            P2CoreCard {
                P2CoreCardHeader(title = "安全检查", trailing = safetyStatus, trailingColor = Color(0xFFEAF6FF))
                uiState.highlights.forEach { item ->
                    P2CoreListRow(
                        title = item.title,
                        subtitle = item.subtitle,
                        trailing = item.trailing,
                    )
                }
            }
        }
    }
}

@Composable
private fun SendFilterRow(
    chips: List<Pair<String, Boolean>>,
    onChipClick: (Int) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        chips.forEachIndexed { index, (label, active) ->
            FilterChip(
                selected = active,
                onClick = { onChipClick(index) },
                label = { Text(label) },
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun SendPreview() {
    CryptoVpnTheme {
        SendScreen(
            uiState = sendPreviewState(),
            onEvent = {},
        )
    }
}
