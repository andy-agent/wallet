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
import com.v2ray.ang.composeui.p2extended.model.AddCustomTokenEvent
import com.v2ray.ang.composeui.p2extended.model.AddCustomTokenUiState
import com.v2ray.ang.composeui.p2extended.model.addCustomTokenPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.AddCustomTokenViewModel

@Composable
fun AddCustomTokenRoute(
    viewModel: AddCustomTokenViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    AddCustomTokenScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                AddCustomTokenEvent.PrimaryActionClicked -> onPrimaryAction()
                AddCustomTokenEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun AddCustomTokenScreen(
    uiState: AddCustomTokenUiState,
    onEvent: (AddCustomTokenEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P2ExtendedFeatureTemplate(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        hubLabel = uiState.badge,
        onHubClick = { onEvent(AddCustomTokenEvent.Refresh) },
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = { onEvent(AddCustomTokenEvent.PrimaryActionClicked) },
        secondaryActionLabel = uiState.secondaryActionLabel,
        onSecondaryAction = { onEvent(AddCustomTokenEvent.SecondaryActionClicked) },
        metrics = uiState.metrics,
        fields = uiState.fields,
        highlights = uiState.highlights,
        checklist = uiState.checklist,
        note = uiState.note,
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun AddCustomTokenPreview() {
    CryptoVpnTheme {
        AddCustomTokenScreen(
            uiState = addCustomTokenPreviewState(),
            onEvent = {},
        )
    }
}
