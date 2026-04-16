package com.v2ray.ang.composeui.pages.p2

import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.components.actions.ActionCluster
import com.v2ray.ang.composeui.components.actions.ActionClusterAction
import com.v2ray.ang.composeui.components.app.AppPageBackgroundStyle
import com.v2ray.ang.composeui.components.app.AppPageScaffold
import com.v2ray.ang.composeui.components.buttons.AppButtonVariant
import com.v2ray.ang.composeui.components.cards.PaymentSummaryCard
import com.v2ray.ang.composeui.components.cards.PaymentSummaryField
import com.v2ray.ang.composeui.components.cards.QrAddressCard
import com.v2ray.ang.composeui.components.chips.AppChip
import com.v2ray.ang.composeui.components.chips.AppChipTone
import com.v2ray.ang.composeui.components.navigation.AppTopBar
import com.v2ray.ang.composeui.components.navigation.AppTopBarMode
import com.v2ray.ang.composeui.components.navigation.CryptoVpnBottomBar
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p2.model.ReceiveEvent
import com.v2ray.ang.composeui.p2.model.ReceiveUiState
import com.v2ray.ang.composeui.p2.model.receivePreviewState
import com.v2ray.ang.composeui.p2.viewmodel.ReceiveViewModel
import com.v2ray.ang.composeui.theme.AppTheme
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.theme.tokens.OverviewBaselineTokens

private val ReceiveGlowBlue = Color(0x224F7CFF)
private val ReceiveGlowMint = Color(0x162ED8A3)

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
    val baseline = OverviewBaselineTokens.primary
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val activeVariant = uiState.variants.firstOrNull { it.selected } ?: uiState.variants.firstOrNull()
    val address = activeVariant?.address?.takeUnless { it.isBlank() }
        ?: uiState.fields.firstOrNull()?.value
        ?: "--"
    val qrContent = activeVariant?.qrContent?.takeUnless { it.isBlank() }
        ?: address.takeUnless { it.isBlank() || it == "--" }
        .orEmpty()
    val status = activeVariant?.status ?: uiState.metrics.getOrNull(3)?.value ?: "同步中"
    val supportingNote = activeVariant?.note?.takeUnless { it.isBlank() } ?: uiState.note
    val shareText = activeVariant?.shareText?.takeUnless { it.isBlank() } ?: uiState.shareText
    val canShare = activeVariant?.canShare ?: uiState.canShare
    val summaryFields = buildList {
        uiState.metrics.getOrNull(0)?.value?.takeIf { it.isNotBlank() }?.let {
            add(PaymentSummaryField("当前链", it))
        }
        uiState.metrics.getOrNull(1)?.value?.takeIf { it.isNotBlank() }?.let {
            add(PaymentSummaryField("可切换", it))
        }
        uiState.metrics.getOrNull(2)?.value?.takeIf { it.isNotBlank() }?.let {
            add(PaymentSummaryField("地址数", it))
        }
        add(PaymentSummaryField("校验状态", status))
    }

    AppPageScaffold(
        backgroundStyle = AppPageBackgroundStyle.Hero,
        background = { ReceiveBackgroundGlow() },
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
            verticalArrangement = Arrangement.spacedBy(baseline.sectionGap),
        ) {
            AppTopBar(
                title = uiState.title,
                subtitle = uiState.subtitle,
                mode = AppTopBarMode.Hero,
                actions = {
                    AppChip(
                        text = uiState.badge,
                        tone = AppChipTone.Info,
                    )
                },
            )

            PaymentSummaryCard(
                title = "当前状态",
                subtitle = uiState.summary,
                fields = summaryFields,
            )

            QrAddressCard(
                title = "收款码",
                subtitle = "复制地址或分享二维码给对方完成收款",
                qrContent = qrContent,
                address = address,
                addressLabel = "收款地址",
                supportingText = supportingNote,
                status = status,
                modifier = Modifier.fillMaxWidth(),
                footer = {
                    if (uiState.variants.isNotEmpty()) {
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.space8),
                        ) {
                            uiState.variants.forEach { variant ->
                                AppChip(
                                    text = variant.label,
                                    tone = AppChipTone.Brand,
                                    selected = variant.selected,
                                    onClick = {
                                        if (!variant.selected) {
                                            onEvent(
                                                ReceiveEvent.VariantSelected(
                                                    variant.assetId,
                                                    variant.chainId,
                                                ),
                                            )
                                        }
                                    },
                                )
                            }
                        }
                    }
                },
            )

            ActionCluster(
                actions = listOf(
                    ActionClusterAction(
                        label = uiState.primaryActionLabel.ifBlank { "分享二维码" },
                        onClick = {
                            onEvent(ReceiveEvent.PrimaryActionClicked)
                            if (canShare && shareText.isNotBlank()) {
                                try {
                                    context.startActivity(
                                        Intent.createChooser(
                                            Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(Intent.EXTRA_TEXT, shareText)
                                            },
                                            uiState.primaryActionLabel.ifBlank { "分享二维码" },
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
                        variant = AppButtonVariant.Primary,
                    ),
                    ActionClusterAction(
                        label = uiState.secondaryActionLabel ?: "复制地址",
                        onClick = {
                            onEvent(ReceiveEvent.SecondaryActionClicked)
                            if (address.isNotBlank() && address != "--") {
                                clipboardManager.setText(AnnotatedString(address))
                                Toast.makeText(context, "收款地址已复制", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "当前链暂无可复制地址", Toast.LENGTH_SHORT).show()
                            }
                        },
                        variant = AppButtonVariant.Secondary,
                    ),
                ),
            )
        }
    }
}

@Composable
private fun ReceiveBackgroundGlow() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 36.dp),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(220.dp)
                .background(ReceiveGlowBlue, RoundedCornerShape(999.dp))
                .blur(48.dp),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(top = 320.dp)
                .size(260.dp)
                .background(ReceiveGlowMint, RoundedCornerShape(999.dp))
                .blur(60.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ReceivePreview() {
    CryptoVpnTheme {
        Surface {
            ReceiveScreen(
                uiState = receivePreviewState(),
                onEvent = {},
            )
        }
    }
}
