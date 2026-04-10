package com.cryptovpn.ui.pages.p1

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.cryptovpn.ui.components.feature.FeaturePageTemplate
import com.cryptovpn.ui.effects.MotionProfile
import com.cryptovpn.ui.theme.CryptoVpnTheme
import com.cryptovpn.ui.p1.model.OrderDetailEvent
import com.cryptovpn.ui.p1.model.OrderDetailUiState
import com.cryptovpn.ui.p1.model.orderDetailPreviewState
import com.cryptovpn.ui.p1.viewmodel.OrderDetailViewModel

@Composable
fun OrderDetailRoute(
    viewModel: OrderDetailViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    OrderDetailScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                OrderDetailEvent.PrimaryActionClicked -> onPrimaryAction()
                OrderDetailEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun OrderDetailScreen(
    uiState: OrderDetailUiState,
    onEvent: (OrderDetailEvent) -> Unit,
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
        currentRoute = "order_detail",
        motionProfile = MotionProfile.L2,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(OrderDetailEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = {
            onEvent(OrderDetailEvent.PrimaryActionClicked)
        },
        onSecondaryAction = {
            onEvent(OrderDetailEvent.SecondaryActionClicked)
        },
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun OrderDetailPreview() {
    CryptoVpnTheme {
        OrderDetailScreen(
            uiState = orderDetailPreviewState(),
            onEvent = {},
        )
    }
}
