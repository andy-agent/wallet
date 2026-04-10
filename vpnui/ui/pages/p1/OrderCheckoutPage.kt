package com.cryptovpn.ui.pages.p1

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.cryptovpn.ui.components.feature.FeaturePageTemplate
import com.cryptovpn.ui.effects.MotionProfile
import com.cryptovpn.ui.theme.CryptoVpnTheme
import com.cryptovpn.ui.p1.model.OrderCheckoutEvent
import com.cryptovpn.ui.p1.model.OrderCheckoutUiState
import com.cryptovpn.ui.p1.model.orderCheckoutPreviewState
import com.cryptovpn.ui.p1.viewmodel.OrderCheckoutViewModel

@Composable
fun OrderCheckoutRoute(
    viewModel: OrderCheckoutViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    OrderCheckoutScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                OrderCheckoutEvent.PrimaryActionClicked -> onPrimaryAction()
                OrderCheckoutEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun OrderCheckoutScreen(
    uiState: OrderCheckoutUiState,
    onEvent: (OrderCheckoutEvent) -> Unit,
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
        currentRoute = "order_checkout",
        motionProfile = MotionProfile.L2,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(OrderCheckoutEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = {
            onEvent(OrderCheckoutEvent.PrimaryActionClicked)
        },
        onSecondaryAction = {
            onEvent(OrderCheckoutEvent.SecondaryActionClicked)
        },
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun OrderCheckoutPreview() {
    CryptoVpnTheme {
        OrderCheckoutScreen(
            uiState = orderCheckoutPreviewState(),
            onEvent = {},
        )
    }
}
