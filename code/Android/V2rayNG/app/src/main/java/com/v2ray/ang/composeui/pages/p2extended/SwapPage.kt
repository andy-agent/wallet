package com.v2ray.ang.composeui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.p2extended.model.SwapEvent
import com.v2ray.ang.composeui.p2extended.model.SwapUiState
import com.v2ray.ang.composeui.p2extended.model.swapPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.SwapViewModel

@Composable
fun SwapRoute(
    viewModel: SwapViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    SwapScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                SwapEvent.PrimaryActionClicked -> onPrimaryAction()
                SwapEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun SwapScreen(
    uiState: SwapUiState,
    onEvent: (SwapEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P2ExtendedFeatureTemplate(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        hubLabel = uiState.badge,
        onHubClick = { onEvent(SwapEvent.Refresh) },
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = { onEvent(SwapEvent.PrimaryActionClicked) },
        secondaryActionLabel = uiState.secondaryActionLabel,
        onSecondaryAction = { onEvent(SwapEvent.SecondaryActionClicked) },
        metrics = uiState.metrics,
        fields = uiState.fields,
        highlights = uiState.highlights,
        checklist = uiState.checklist,
        note = uiState.note,
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun SwapPreview() {
    CryptoVpnTheme {
        SwapScreen(
            uiState = swapPreviewState(),
            onEvent = {},
        )
    }
}
