package com.v2ray.ang.composeui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.p2extended.model.NftGalleryEvent
import com.v2ray.ang.composeui.p2extended.model.NftGalleryUiState
import com.v2ray.ang.composeui.p2extended.model.nftGalleryPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.NftGalleryViewModel

@Composable
fun NftGalleryRoute(
    viewModel: NftGalleryViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    NftGalleryScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                NftGalleryEvent.PrimaryActionClicked -> onPrimaryAction()
                NftGalleryEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun NftGalleryScreen(
    uiState: NftGalleryUiState,
    onEvent: (NftGalleryEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P2ExtendedFeatureTemplate(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        hubLabel = uiState.badge,
        onHubClick = { onEvent(NftGalleryEvent.Refresh) },
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = { onEvent(NftGalleryEvent.PrimaryActionClicked) },
        secondaryActionLabel = uiState.secondaryActionLabel,
        onSecondaryAction = { onEvent(NftGalleryEvent.SecondaryActionClicked) },
        metrics = uiState.metrics,
        fields = uiState.fields,
        highlights = uiState.highlights,
        checklist = uiState.checklist,
        note = uiState.note,
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun NftGalleryPreview() {
    CryptoVpnTheme {
        NftGalleryScreen(
            uiState = nftGalleryPreviewState(),
            onEvent = {},
        )
    }
}
