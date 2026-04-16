package com.v2ray.ang.composeui.pages.p2

import android.content.ActivityNotFoundException
import android.content.Intent
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
import com.v2ray.ang.composeui.p2.model.InviteCenterEvent
import com.v2ray.ang.composeui.p2.model.InviteCenterUiState
import com.v2ray.ang.composeui.p2.model.inviteCenterPreviewState
import com.v2ray.ang.composeui.p2.viewmodel.InviteCenterViewModel
import com.v2ray.ang.composeui.p0.ui.P01BottomNav
import com.v2ray.ang.composeui.p0.ui.P01HeaderHeroRing
import com.v2ray.ang.composeui.p0.ui.defaultP01Destinations
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.theme.AppTheme

@Composable
fun InviteCenterRoute(
    viewModel: InviteCenterViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    InviteCenterScreen(
        uiState = uiState,
        onEvent = { event -> viewModel.onEvent(event) },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun InviteCenterScreen(
    uiState: InviteCenterUiState,
    onEvent: (InviteCenterEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val inviteCode = uiState.inviteCode.ifBlank { "--" }
    val sharePayload = uiState.shareMessage.takeIf { it.isNotBlank() } ?: uiState.shareLink
    val copyInviteCode = {
        onEvent(InviteCenterEvent.PrimaryActionClicked)
        if (inviteCode.isNotBlank() && inviteCode != "--") {
            clipboardManager.setText(AnnotatedString(inviteCode))
            Toast.makeText(context, "邀请码已复制", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "当前暂无可复制邀请码", Toast.LENGTH_SHORT).show()
        }
    }
    val shareLink = {
        onEvent(InviteCenterEvent.SecondaryActionClicked)
        if (sharePayload.isNotBlank()) {
            try {
                context.startActivity(
                    Intent.createChooser(
                        Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, sharePayload)
                        },
                        uiState.secondaryActionLabel ?: "分享推广链接",
                    ),
                )
                Toast.makeText(context, "已打开系统分享", Toast.LENGTH_SHORT).show()
            } catch (_: ActivityNotFoundException) {
                Toast.makeText(context, "未找到可分享的应用", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "当前暂无可分享推广链接", Toast.LENGTH_SHORT).show()
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
            title = "邀请收益总览",
            value = uiState.metrics.firstOrNull()?.value ?: "--",
            badgeText = uiState.badge,
            badgeTone = AppChipTone.Info,
            emphasized = true,
        )

        val secondaryMetrics = uiState.metrics.drop(1).take(2)
        if (secondaryMetrics.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap),
            ) {
                secondaryMetrics.forEach { metric ->
                    MetricCard(
                        title = metric.label,
                        value = metric.value,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        InfoSection(
            title = "邀请信息",
            subtitle = "邀请码、推广链接与实时状态",
            trailing = { AppChip(text = "实时同步", tone = AppChipTone.Info) },
        ) {
            LabelValueRow(label = "邀请码", value = inviteCode)
            if (sharePayload.isNotBlank()) {
                LabelValueRow(
                    label = "推广链接",
                    value = sharePayload,
                    supportingText = "可直接复制或系统分享",
                )
            }
            if (uiState.note.isNotBlank()) {
                LabelValueRow(
                    label = "状态说明",
                    value = "已就绪",
                    supportingText = uiState.note,
                    badgeText = uiState.badge.takeIf { it.isNotBlank() },
                    badgeTone = AppChipTone.Brand,
                )
            }
        }

        ActionCluster(
            actions = listOfNotNull(
                uiState.primaryActionLabel.takeIf { it.isNotBlank() }?.let {
                    ActionClusterAction(
                        label = it,
                        onClick = copyInviteCode,
                        variant = AppButtonVariant.Primary,
                    )
                },
                uiState.secondaryActionLabel?.takeIf { it.isNotBlank() }?.let {
                    ActionClusterAction(
                        label = it,
                        onClick = shareLink,
                        variant = AppButtonVariant.Secondary,
                    )
                },
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun InviteCenterPreview() {
    CryptoVpnTheme {
        InviteCenterScreen(
            uiState = inviteCenterPreviewState(),
            onEvent = {},
        )
    }
}
