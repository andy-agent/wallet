package com.cryptovpn.ui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.cryptovpn.ui.components.feature.FeaturePageTemplate
import com.cryptovpn.ui.effects.MotionProfile
import com.cryptovpn.ui.theme.CryptoVpnTheme
import com.cryptovpn.ui.p2extended.model.NftGalleryEvent
import com.cryptovpn.ui.p2extended.model.NftGalleryUiState
import com.cryptovpn.ui.p2extended.model.nftGalleryPreviewState
import com.cryptovpn.ui.p2extended.viewmodel.NftGalleryViewModel

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
        currentRoute = "nft_gallery",
        motionProfile = MotionProfile.L1,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(NftGalleryEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = {
            onEvent(NftGalleryEvent.PrimaryActionClicked)
        },
        onSecondaryAction = {
            onEvent(NftGalleryEvent.SecondaryActionClicked)
        },
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
