package com.v2ray.ang.composeui.pages.p2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p2.model.LegalDocumentsEvent
import com.v2ray.ang.composeui.p2.model.LegalDocumentsUiState
import com.v2ray.ang.composeui.p2.model.legalDocumentsPreviewState
import com.v2ray.ang.composeui.p2.viewmodel.LegalDocumentsViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun LegalDocumentsRoute(
    viewModel: LegalDocumentsViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    LegalDocumentsScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                LegalDocumentsEvent.PrimaryActionClicked -> onPrimaryAction()
                LegalDocumentsEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun LegalDocumentsScreen(
    uiState: LegalDocumentsUiState,
    onEvent: (LegalDocumentsEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val documentFocus = rememberCoreLoopingIndex(itemCount = maxOf(uiState.highlights.size, 1), durationMillis = 4200)
    val firstDocumentRoute = uiState.highlights.firstOrNull { it.badge.isNotBlank() }
        ?.badge
        ?.let(CryptoVpnRouteSpec::legalDocumentDetailRoute)
    P2CorePageScaffold(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.note,
        badge = uiState.badge,
        activeSection = CoreNavSection.Profile,
        onBottomNav = onBottomNav,
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = {
            firstDocumentRoute?.let(onBottomNav)
        },
        secondaryActionLabel = uiState.secondaryActionLabel,
        onSecondaryAction = { onBottomNav(CryptoVpnRouteSpec.profile.pattern) },
    ) {
        P2CoreCard {
            uiState.highlights.forEachIndexed { index, item ->
                val destination = item.badge.takeIf { it.isNotBlank() }?.let(CryptoVpnRouteSpec::legalDocumentDetailRoute)
                P2CoreListRow(
                    title = item.title,
                    subtitle = item.subtitle,
                    trailing = item.trailing,
                    emphasis = if (index == documentFocus % maxOf(uiState.highlights.size, 1)) {
                        P2CoreRowEmphasis.Brand
                    } else {
                        P2CoreRowEmphasis.Neutral
                    },
                    trailingColor = if (index == 0) Color(0xFF2F5BFF) else Color(0xFF66739D),
                    onClick = destination?.let { { onBottomNav(it) } },
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun LegalDocumentsPreview() {
    CryptoVpnTheme {
        LegalDocumentsScreen(
            uiState = legalDocumentsPreviewState(),
            onEvent = {},
        )
    }
}
