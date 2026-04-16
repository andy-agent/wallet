package com.v2ray.ang.composeui.pages.p2

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.components.actions.ActionCluster
import com.v2ray.ang.composeui.components.actions.ActionClusterAction
import com.v2ray.ang.composeui.components.app.AppPageScaffold
import com.v2ray.ang.composeui.components.buttons.AppButtonVariant
import com.v2ray.ang.composeui.components.cards.MetricCard
import com.v2ray.ang.composeui.components.chips.AppChip
import com.v2ray.ang.composeui.components.chips.AppChipTone
import com.v2ray.ang.composeui.components.navigation.AppTopBar
import com.v2ray.ang.composeui.components.rows.LabelValueRow
import com.v2ray.ang.composeui.components.sections.InfoSection
import com.v2ray.ang.composeui.p2.model.InviteShareEvent
import com.v2ray.ang.composeui.p2.model.InviteShareUiState
import com.v2ray.ang.composeui.p2.model.inviteSharePreviewState
import com.v2ray.ang.composeui.p2.viewmodel.InviteShareViewModel
import com.v2ray.ang.composeui.p0.ui.P01BottomNav
import com.v2ray.ang.composeui.p0.ui.P01HeaderHeroRing
import com.v2ray.ang.composeui.p0.ui.defaultP01Destinations
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.theme.AppTheme

@Composable
fun InviteShareRoute(
    viewModel: InviteShareViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    InviteShareScreen(
        uiState = uiState,
        onEvent = { event -> viewModel.onEvent(event) },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun InviteShareScreen(
    uiState: InviteShareUiState,
    onEvent: (InviteShareEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val primaryMetric = uiState.metrics.firstOrNull()
    val link = uiState.highlights.firstOrNull()?.subtitle ?: primaryMetric?.value ?: ""
    val inviteCode = uiState.metrics.getOrNull(1)?.value ?: "--"
    val copyLink = {
        onEvent(InviteShareEvent.PrimaryActionClicked)
        if (link.isNotBlank() && link != "--") {
            clipboardManager.setText(AnnotatedString(link))
            Toast.makeText(context, "推广链接已复制", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "当前暂无可复制链接", Toast.LENGTH_SHORT).show()
        }
    }
    val copyInviteCode = {
        onEvent(InviteShareEvent.SecondaryActionClicked)
        if (inviteCode.isNotBlank() && inviteCode != "--") {
            clipboardManager.setText(AnnotatedString(inviteCode))
            Toast.makeText(context, "邀请码已复制", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "当前暂无可复制邀请码", Toast.LENGTH_SHORT).show()
        }
    }

    AppPageScaffold(
        topBar = {
            AppTopBar(
                title = uiState.title,
                subtitle = uiState.subtitle,
                trailing = { P01HeaderHeroRing() },
            )
        },
        bottomBar = {
            P01BottomNav(
                currentRoute = "invite_center",
                destinations = defaultP01Destinations(),
                onNavigate = onBottomNav,
            )
        },
    ) {
        MetricCard(
            title = "推广邀请码",
            value = inviteCode.ifBlank { "CVPN-2025-GLOW" },
            badgeText = uiState.badge.takeIf { it.isNotBlank() },
            badgeTone = AppChipTone.Brand,
            emphasized = true,
        )

        val extraMetrics = uiState.metrics.drop(1).take(2)
        if (extraMetrics.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap),
            ) {
                extraMetrics.forEach { metric ->
                    MetricCard(
                        title = metric.label,
                        value = metric.value,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        InfoSection(
            title = "推广信息",
            subtitle = "复制链接或邀请码进行分享",
            trailing = { AppChip(text = "推广", tone = AppChipTone.Info) },
        ) {
            LabelValueRow(
                label = primaryMetric?.label ?: "推广链接",
                value = link.ifBlank { "--" },
                supportingText = uiState.note,
            )
            if (inviteCode.isNotBlank()) {
                LabelValueRow(label = "邀请码", value = inviteCode)
            }
        }

        ActionCluster(
            actions = listOfNotNull(
                uiState.primaryActionLabel.takeIf { it.isNotBlank() }?.let {
                    ActionClusterAction(
                        label = it,
                        onClick = copyLink,
                        variant = AppButtonVariant.Primary,
                    )
                },
                uiState.secondaryActionLabel?.takeIf { it.isNotBlank() }?.let {
                    ActionClusterAction(
                        label = it,
                        onClick = copyInviteCode,
                        variant = AppButtonVariant.Secondary,
                    )
                },
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun InviteSharePreview() {
    CryptoVpnTheme {
        InviteShareScreen(
            uiState = inviteSharePreviewState(),
            onEvent = {},
        )
    }
}
