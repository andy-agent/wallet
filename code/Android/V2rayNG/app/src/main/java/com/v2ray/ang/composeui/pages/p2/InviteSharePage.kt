package com.v2ray.ang.composeui.pages.p2

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.components.actions.AppCopyShareActions
import com.v2ray.ang.composeui.components.growth.AppGrowthPageScaffold
import com.v2ray.ang.composeui.components.growth.AppHeroStat
import com.v2ray.ang.composeui.components.growth.AppHeroValueCard
import com.v2ray.ang.composeui.components.growth.AppMetricGrid
import com.v2ray.ang.composeui.components.growth.AppMetricGridItem
import com.v2ray.ang.composeui.components.growth.AppQrAddressCard
import com.v2ray.ang.composeui.components.rows.AppLabelValueRow
import com.v2ray.ang.composeui.p2.model.InviteShareEvent
import com.v2ray.ang.composeui.p2.model.InviteShareUiState
import com.v2ray.ang.composeui.p2.model.inviteSharePreviewState
import com.v2ray.ang.composeui.p2.viewmodel.InviteShareViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

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

    AppGrowthPageScaffold(
        title = uiState.title,
        subtitle = uiState.subtitle,
        note = uiState.summary,
        badge = uiState.badge,
        currentRoute = "invite_center",
        onBottomNav = onBottomNav,
    ) {
        AppHeroValueCard(
            title = "推广邀请码",
            value = inviteCode.ifBlank { "CVPN-2025-GLOW" },
            supportingText = uiState.summary,
            highlight = uiState.badge.takeIf { it.isNotBlank() },
            stats = uiState.metrics.drop(1).take(2).map { AppHeroStat(it.label, it.value) },
        )

        AppMetricGrid(
            items = uiState.metrics.map { AppMetricGridItem(it.label, it.value) },
            emphasizedIndexes = setOf(0),
        )

        AppQrAddressCard(
            title = "推广二维码",
            subtitle = "复制链接或邀请码进行分享",
            address = link.ifBlank { "--" },
            addressLabel = primaryMetric?.label ?: "推广链接",
            supportingText = uiState.note,
            status = uiState.badge.takeIf { it.isNotBlank() },
        ) {
            if (inviteCode.isNotBlank()) {
                AppLabelValueRow(label = "邀请码", value = inviteCode)
            }
        }

        AppCopyShareActions(
            primaryLabel = uiState.primaryActionLabel,
            onPrimaryClick = copyLink,
            secondaryLabel = uiState.secondaryActionLabel,
            onSecondaryClick = copyInviteCode,
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
