package com.cryptovpn.ui.pages.p1

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.cryptovpn.ui.components.feature.FeaturePageTemplate
import com.cryptovpn.ui.effects.MotionProfile
import com.cryptovpn.ui.theme.CryptoVpnTheme
import com.cryptovpn.ui.p1.model.OrderResultEvent
import com.cryptovpn.ui.p1.model.OrderResultUiState
import com.cryptovpn.ui.p1.model.orderResultPreviewState
import com.cryptovpn.ui.p1.viewmodel.OrderResultViewModel

@Composable
fun OrderResultRoute(
    viewModel: OrderResultViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    OrderResultScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                OrderResultEvent.PrimaryActionClicked -> onPrimaryAction()
                OrderResultEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun OrderResultScreen(
    uiState: OrderResultUiState,
    onEvent: (OrderResultEvent) -> Unit,
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
        currentRoute = "order_result",
        motionProfile = MotionProfile.L2,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(OrderResultEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = {
            onEvent(OrderResultEvent.PrimaryActionClicked)
        },
        onSecondaryAction = {
            onEvent(OrderResultEvent.SecondaryActionClicked)
        },
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun OrderResultPreview() {
    CryptoVpnTheme {
        OrderResultScreen(
            uiState = orderResultPreviewState(),
            onEvent = {},
        )
    }
}
