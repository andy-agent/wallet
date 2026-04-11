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
import com.v2ray.ang.composeui.p2extended.model.ImportWalletMethodEvent
import com.v2ray.ang.composeui.p2extended.model.ImportWalletMethodUiState
import com.v2ray.ang.composeui.p2extended.model.importWalletMethodPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.ImportWalletMethodViewModel

@Composable
fun ImportWalletMethodRoute(
    viewModel: ImportWalletMethodViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    ImportWalletMethodScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                ImportWalletMethodEvent.PrimaryActionClicked -> onPrimaryAction()
                ImportWalletMethodEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun ImportWalletMethodScreen(
    uiState: ImportWalletMethodUiState,
    onEvent: (ImportWalletMethodEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P2ExtendedFeatureTemplate(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        hubLabel = uiState.badge,
        onHubClick = { onEvent(ImportWalletMethodEvent.Refresh) },
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = { onEvent(ImportWalletMethodEvent.PrimaryActionClicked) },
        secondaryActionLabel = uiState.secondaryActionLabel,
        onSecondaryAction = { onEvent(ImportWalletMethodEvent.SecondaryActionClicked) },
        metrics = uiState.metrics,
        fields = uiState.fields,
        highlights = uiState.highlights,
        checklist = uiState.checklist,
        note = uiState.note,
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ImportWalletMethodPreview() {
    CryptoVpnTheme {
        ImportWalletMethodScreen(
            uiState = importWalletMethodPreviewState(),
            onEvent = {},
        )
    }
}
