package com.v2ray.ang.composeui.pages.p2

import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.p2.model.ReceiveEvent
import com.v2ray.ang.composeui.p2.model.ReceiveUiState
import com.v2ray.ang.composeui.p2.model.receivePreviewState
import com.v2ray.ang.composeui.p2.viewmodel.ReceiveViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun ReceiveRoute(
    viewModel: ReceiveViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(uiState.redirectRoute) {
        uiState.redirectRoute?.let { onBottomNav(it) }
    }
    ReceiveScreen(
        uiState = uiState,
        onEvent = { event -> viewModel.onEvent(event) },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun ReceiveScreen(
    uiState: ReceiveUiState,
    onEvent: (ReceiveEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val chips = uiState.metrics.take(3).map { it.value }
    val activeVariant = uiState.variants.firstOrNull { it.selected }
        ?: uiState.variants.firstOrNull()
    val address = activeVariant?.address?.takeUnless { it.isBlank() }
        ?: uiState.fields.firstOrNull()?.value
        ?: "--"
    val qrContent = activeVariant?.qrContent?.takeUnless { it.isBlank() }
        ?: address.takeUnless { it.isBlank() || it == "--" }
        .orEmpty()
    val status = activeVariant?.status ?: uiState.metrics.getOrNull(3)?.value ?: "已校验"
    val addressPreview = if (address.length > 14) "${address.take(6)}...${address.takeLast(6)}" else address
    val supportingNote = activeVariant?.note?.takeUnless { it.isBlank() } ?: uiState.note
    val shareText = activeVariant?.shareText?.takeUnless { it.isBlank() } ?: uiState.shareText
    val canShare = activeVariant?.canShare ?: uiState.canShare
    P2CorePageScaffold(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        badge = uiState.badge,
        activeSection = CoreNavSection.Wallet,
        onBottomNav = onBottomNav,
        secureHubLabel = receiveHubLabel(status, chips.firstOrNull()),
    ) {
        P2CoreHeroValueCard(
            label = "当前网络",
            value = chips.firstOrNull() ?: (uiState.badge ?: "--"),
            supportingText = "",
            highlight = uiState.badge,
            stats = emptyList(),
        )
        P2CoreQrAddressCard(
            title = "收款码",
            subtitle = "",
            status = null,
            statusColor = androidx.compose.ui.graphics.Color(0xFFE6FFF6),
            address = address,
            qrContent = qrContent,
            addressLabel = "收款地址",
            supportingText = supportingNote,
        ) {
            if (uiState.variants.isNotEmpty()) {
                P2CoreFilterRow(
                    chips = uiState.variants.map { it.label to it.selected },
                    onChipClick = { index ->
                        uiState.variants.getOrNull(index)?.let { variant ->
                            if (!variant.selected) {
                                onEvent(
                                    ReceiveEvent.VariantSelected(
                                        variant.assetId,
                                        variant.chainId,
                                    ),
                                )
                            }
                        }
                    },
                )
            } else if (chips.isNotEmpty()) {
                P2CoreChipRow(items = chips, activeIndex = 0)
            }
            CoreActionRow(
                primaryActionLabel = uiState.primaryActionLabel ?: "分享二维码",
                onPrimaryAction = {
                    onEvent(ReceiveEvent.PrimaryActionClicked)
                    if (canShare && shareText.isNotBlank()) {
                        try {
                            context.startActivity(
                                Intent.createChooser(
                                    Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                    },
                                    uiState.primaryActionLabel ?: "分享二维码",
                                ),
                            )
                            Toast.makeText(context, "已打开系统分享", Toast.LENGTH_SHORT).show()
                        } catch (_: ActivityNotFoundException) {
                            Toast.makeText(context, "未找到可分享的应用", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "当前链暂无可分享地址", Toast.LENGTH_SHORT).show()
                    }
                },
                secondaryActionLabel = uiState.secondaryActionLabel ?: "复制地址",
                onSecondaryAction = {
                    onEvent(ReceiveEvent.SecondaryActionClicked)
                    if (address.isNotBlank() && address != "--") {
                        clipboardManager.setText(AnnotatedString(address))
                        Toast.makeText(context, "收款地址已复制", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "当前链暂无可复制地址", Toast.LENGTH_SHORT).show()
                    }
                },
            )
        }
    }
}

@Composable
private fun P2CoreFilterRow(
    chips: List<Pair<String, Boolean>>,
    onChipClick: (Int) -> Unit,
) {
    androidx.compose.foundation.layout.Row(
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
    ) {
        chips.forEachIndexed { index, (label, active) ->
            androidx.compose.material3.FilterChip(
                selected = active,
                onClick = { onChipClick(index) },
                label = { androidx.compose.material3.Text(label) },
            )
        }
    }
}

private fun receiveHubLabel(
    status: String,
    network: String?,
): String = when {
    status.contains("校验") -> "READY"
    !network.isNullOrBlank() -> network.take(4).uppercase()
    else -> "SCAN"
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ReceivePreview() {
    CryptoVpnTheme {
        ReceiveScreen(
            uiState = receivePreviewState(),
            onEvent = {},
        )
    }
}
