package com.cryptovpn.ui.pages.p1

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.cryptovpn.ui.components.feature.FeaturePageTemplate
import com.cryptovpn.ui.effects.MotionProfile
import com.cryptovpn.ui.theme.CryptoVpnTheme
import com.cryptovpn.ui.p1.model.OrderListEvent
import com.cryptovpn.ui.p1.model.OrderListUiState
import com.cryptovpn.ui.p1.model.orderListPreviewState
import com.cryptovpn.ui.p1.viewmodel.OrderListViewModel

@Composable
fun OrderListRoute(
    viewModel: OrderListViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    OrderListScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                OrderListEvent.PrimaryActionClicked -> onPrimaryAction()
                OrderListEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun OrderListScreen(
    uiState: OrderListUiState,
    onEvent: (OrderListEvent) -> Unit,
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
        showBottomBar = true,
        currentRoute = "plans",
        motionProfile = MotionProfile.L2,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(OrderListEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = {
            onEvent(OrderListEvent.PrimaryActionClicked)
        },
        onSecondaryAction = {
            onEvent(OrderListEvent.SecondaryActionClicked)
        },
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun OrderListPreview() {
    CryptoVpnTheme {
        OrderListScreen(
            uiState = orderListPreviewState(),
            onEvent = {},
        )
    }
}
