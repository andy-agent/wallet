package com.v2ray.ang.composeui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.p2extended.model.ImportMnemonicEvent
import com.v2ray.ang.composeui.p2extended.model.ImportMnemonicUiState
import com.v2ray.ang.composeui.p2extended.model.importMnemonicPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.ImportMnemonicViewModel

@Composable
fun ImportMnemonicRoute(
    viewModel: ImportMnemonicViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    ImportMnemonicScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                ImportMnemonicEvent.PrimaryActionClicked -> onPrimaryAction()
                ImportMnemonicEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun ImportMnemonicScreen(
    uiState: ImportMnemonicUiState,
    onEvent: (ImportMnemonicEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P2ExtendedFeatureTemplate(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        hubLabel = uiState.badge,
        onHubClick = { onEvent(ImportMnemonicEvent.Refresh) },
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = { onEvent(ImportMnemonicEvent.PrimaryActionClicked) },
        secondaryActionLabel = uiState.secondaryActionLabel,
        onSecondaryAction = { onEvent(ImportMnemonicEvent.SecondaryActionClicked) },
        metrics = uiState.metrics,
        fields = uiState.fields,
        highlights = uiState.highlights,
        checklist = uiState.checklist,
        note = uiState.note,
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ImportMnemonicPreview() {
    CryptoVpnTheme {
        ImportMnemonicScreen(
            uiState = importMnemonicPreviewState(),
            onEvent = {},
        )
    }
}
