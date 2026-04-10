package com.v2ray.ang.composeui.pages.p2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
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
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                InviteShareEvent.PrimaryActionClicked -> onPrimaryAction()
                InviteShareEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun InviteShareScreen(
    uiState: InviteShareUiState,
    onEvent: (InviteShareEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val link = uiState.highlights.firstOrNull()?.subtitle ?: uiState.metrics.firstOrNull()?.value ?: ""
    P2CorePageScaffold(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        badge = null,
        activeSection = CoreNavSection.Growth,
        onBottomNav = onBottomNav,
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = { onEvent(InviteShareEvent.PrimaryActionClicked) },
        secondaryActionLabel = uiState.secondaryActionLabel,
        onSecondaryAction = { onEvent(InviteShareEvent.SecondaryActionClicked) },
    ) {
        P2CoreCard {
            P2CoreQrPlaceholder()
            Text("推广链接", style = MaterialTheme.typography.labelMedium, color = Color(0xFF6D789E))
            Text(link, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF182345))
        }
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
