package com.cryptovpn.ui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.cryptovpn.ui.components.feature.FeaturePageTemplate
import com.cryptovpn.ui.effects.MotionProfile
import com.cryptovpn.ui.theme.CryptoVpnTheme
import com.cryptovpn.ui.p2extended.model.ImportMnemonicEvent
import com.cryptovpn.ui.p2extended.model.ImportMnemonicUiState
import com.cryptovpn.ui.p2extended.model.importMnemonicPreviewState
import com.cryptovpn.ui.p2extended.viewmodel.ImportMnemonicViewModel

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
    FeaturePageTemplate(
        title = uiState.title,
        subtitle = uiState.subtitle,
        badge = uiState.badge,
        summary = uiState.summary,
        heroAccent = uiState.heroAccent,
        metrics = uiState.metrics,
        fields = uiState.fields,
        highlights = uiState.highlights,
        checklist = uiState.checklist,
        note = uiState.note,
        primaryActionLabel = uiState.primaryActionLabel,
        secondaryActionLabel = uiState.secondaryActionLabel,
        showBottomBar = false,
        currentRoute = "import_mnemonic",
        motionProfile = MotionProfile.L1,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(ImportMnemonicEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = {
            onEvent(ImportMnemonicEvent.PrimaryActionClicked)
        },
        onSecondaryAction = {
            onEvent(ImportMnemonicEvent.SecondaryActionClicked)
        },
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
