package com.cryptovpn.ui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.cryptovpn.ui.components.feature.FeaturePageTemplate
import com.cryptovpn.ui.effects.MotionProfile
import com.cryptovpn.ui.theme.CryptoVpnTheme
import com.cryptovpn.ui.p2extended.model.CreateWalletEvent
import com.cryptovpn.ui.p2extended.model.CreateWalletUiState
import com.cryptovpn.ui.p2extended.model.createWalletPreviewState
import com.cryptovpn.ui.p2extended.viewmodel.CreateWalletViewModel

@Composable
fun CreateWalletRoute(
    viewModel: CreateWalletViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    CreateWalletScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                CreateWalletEvent.PrimaryActionClicked -> onPrimaryAction()
                CreateWalletEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun CreateWalletScreen(
    uiState: CreateWalletUiState,
    onEvent: (CreateWalletEvent) -> Unit,
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
        currentRoute = "create_wallet",
        motionProfile = MotionProfile.L1,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(CreateWalletEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = {
            onEvent(CreateWalletEvent.PrimaryActionClicked)
        },
        onSecondaryAction = {
            onEvent(CreateWalletEvent.SecondaryActionClicked)
        },
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun CreateWalletPreview() {
    CryptoVpnTheme {
        CreateWalletScreen(
            uiState = createWalletPreviewState(),
            onEvent = {},
        )
    }
}
