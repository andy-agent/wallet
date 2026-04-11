package com.v2ray.ang.composeui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.p2extended.model.ImportPrivateKeyEvent
import com.v2ray.ang.composeui.p2extended.model.ImportPrivateKeyUiState
import com.v2ray.ang.composeui.p2extended.model.importPrivateKeyPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.ImportPrivateKeyViewModel

@Composable
fun ImportPrivateKeyRoute(
    viewModel: ImportPrivateKeyViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    ImportPrivateKeyScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                ImportPrivateKeyEvent.PrimaryActionClicked -> onPrimaryAction()
                ImportPrivateKeyEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun ImportPrivateKeyScreen(
    uiState: ImportPrivateKeyUiState,
    onEvent: (ImportPrivateKeyEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P2ExtendedFeatureTemplate(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        hubLabel = uiState.badge,
        onHubClick = { onEvent(ImportPrivateKeyEvent.Refresh) },
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = { onEvent(ImportPrivateKeyEvent.PrimaryActionClicked) },
        secondaryActionLabel = uiState.secondaryActionLabel,
        onSecondaryAction = { onEvent(ImportPrivateKeyEvent.SecondaryActionClicked) },
        metrics = uiState.metrics,
        fields = uiState.fields,
        highlights = uiState.highlights,
        checklist = uiState.checklist,
        note = uiState.note,
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ImportPrivateKeyPreview() {
    CryptoVpnTheme {
        ImportPrivateKeyScreen(
            uiState = importPrivateKeyPreviewState(),
            onEvent = {},
        )
    }
}
