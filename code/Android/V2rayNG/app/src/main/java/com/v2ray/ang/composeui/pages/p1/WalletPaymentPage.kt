package com.v2ray.ang.composeui.pages.p1

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.components.feature.FeaturePageTemplate
import com.v2ray.ang.composeui.effects.MotionProfile
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.p1.model.P1ScreenState
import com.v2ray.ang.composeui.p1.model.WalletPaymentEvent
import com.v2ray.ang.composeui.p1.model.WalletPaymentUiState
import com.v2ray.ang.composeui.p1.model.walletPaymentPreviewState
import com.v2ray.ang.composeui.p1.viewmodel.WalletPaymentViewModel

@Composable
fun WalletPaymentRoute(
    viewModel: WalletPaymentViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    WalletPaymentScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                WalletPaymentEvent.PrimaryActionClicked -> onPrimaryAction()
                WalletPaymentEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun WalletPaymentScreen(
    uiState: WalletPaymentUiState,
    onEvent: (WalletPaymentEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val stateInfo = uiState.stateInfo
    val detailHighlights = uiState.detailLines.map {
        FeatureListItem(
            title = it.label,
            subtitle = it.value,
        )
    }
    val effectiveHighlights = if (uiState.highlights.isNotEmpty()) {
        uiState.highlights + detailHighlights
    } else {
        detailHighlights
    }
    val badge = when (stateInfo.state) {
        P1ScreenState.Loading -> "加载中"
        P1ScreenState.Error -> stateInfo.title.ifBlank { "加载失败" }
        P1ScreenState.Empty -> stateInfo.title.ifBlank { "暂无支付上下文" }
        P1ScreenState.Unavailable -> stateInfo.title.ifBlank { "能力未接入" }
        P1ScreenState.Content -> uiState.badge
    }
    val summary = when (stateInfo.state) {
        P1ScreenState.Content -> uiState.summary
        else -> stateInfo.message.ifBlank { uiState.summary }
    }
    val note = when (stateInfo.state) {
        P1ScreenState.Content -> uiState.note
        else -> listOfNotNull(stateInfo.message.takeIf { it.isNotBlank() }, uiState.note.takeIf { it.isNotBlank() })
            .distinct()
            .joinToString("\n")
    }
    FeaturePageTemplate(
        title = uiState.title,
        subtitle = uiState.subtitle,
        badge = badge,
        summary = summary,
        heroAccent = uiState.heroAccent,
        metrics = uiState.metrics,
        fields = uiState.fields,
        highlights = effectiveHighlights,
        checklist = uiState.checklist,
        note = note,
        primaryActionLabel = if (uiState.order != null && stateInfo.state == P1ScreenState.Content) uiState.primaryActionLabel else "",
        secondaryActionLabel = uiState.secondaryActionLabel,
        showBottomBar = false,
        currentRoute = CryptoVpnRouteSpec.plans.name,
        motionProfile = MotionProfile.L2,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(WalletPaymentEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = {
            onEvent(WalletPaymentEvent.PrimaryActionClicked)
        },
        onSecondaryAction = {
            onEvent(WalletPaymentEvent.SecondaryActionClicked)
        },
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun WalletPaymentPreview() {
    CryptoVpnTheme {
        WalletPaymentScreen(
            uiState = walletPaymentPreviewState(),
            onEvent = {},
        )
    }
}
