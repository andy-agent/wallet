package com.v2ray.ang.composeui.pages.p2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.p2.model.WithdrawEvent
import com.v2ray.ang.composeui.p2.model.WithdrawUiState
import com.v2ray.ang.composeui.p2.model.withdrawPreviewState
import com.v2ray.ang.composeui.p2.viewmodel.WithdrawViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun WithdrawRoute(
    viewModel: WithdrawViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    WithdrawScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                WithdrawEvent.PrimaryActionClicked -> Unit
                WithdrawEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun WithdrawScreen(
    uiState: WithdrawUiState,
    onEvent: (WithdrawEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val truthState = buildP2TruthState(
        badge = uiState.badge,
        summary = uiState.summary,
        note = listOfNotNull(uiState.note.takeIf { it.isNotBlank() }, uiState.feedbackMessage).joinToString("\n"),
        checklist = uiState.checklist,
        isLoading = uiState.isLoading || uiState.isSubmitting,
        errorMessage = uiState.errorMessage,
        emptyMessage = uiState.emptyMessage,
        blockerTitle = uiState.blockerTitle,
        blockerMessage = uiState.blockerMessage,
        primaryActionLabel = uiState.primaryActionLabel,
        secondaryActionLabel = uiState.secondaryActionLabel,
    )
    P2TruthFeaturePage(
        title = uiState.title,
        subtitle = uiState.subtitle,
        heroAccent = uiState.heroAccent,
        metrics = uiState.metrics,
        fields = uiState.fields,
        highlights = uiState.highlights,
        truthState = truthState,
        currentRoute = "invite_center",
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(WithdrawEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = { onEvent(WithdrawEvent.PrimaryActionClicked) },
        onSecondaryAction = { onEvent(WithdrawEvent.SecondaryActionClicked) },
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun WithdrawPreview() {
    CryptoVpnTheme {
        WithdrawScreen(
            uiState = withdrawPreviewState(),
            onEvent = {},
        )
    }
}
