package com.v2ray.ang.composeui.pages.p2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.p2.model.AssetDetailEvent
import com.v2ray.ang.composeui.p2.model.AssetDetailUiState
import com.v2ray.ang.composeui.p2.model.assetDetailPreviewState
import com.v2ray.ang.composeui.p2.viewmodel.AssetDetailViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun AssetDetailRoute(
    viewModel: AssetDetailViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    AssetDetailScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                AssetDetailEvent.PrimaryActionClicked -> onPrimaryAction()
                AssetDetailEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun AssetDetailScreen(
    uiState: AssetDetailUiState,
    onEvent: (AssetDetailEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val truthState = buildP2TruthState(
        badge = uiState.badge,
        summary = uiState.summary,
        note = uiState.note,
        checklist = uiState.checklist,
        isLoading = uiState.isLoading,
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
        currentRoute = "wallet_home",
        onBottomNav = onBottomNav,
        onPrimaryAction = { onEvent(AssetDetailEvent.PrimaryActionClicked) },
        onSecondaryAction = { onEvent(AssetDetailEvent.SecondaryActionClicked) },
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun AssetDetailPreview() {
    CryptoVpnTheme {
        AssetDetailScreen(
            uiState = assetDetailPreviewState(),
            onEvent = {},
        )
    }
}
