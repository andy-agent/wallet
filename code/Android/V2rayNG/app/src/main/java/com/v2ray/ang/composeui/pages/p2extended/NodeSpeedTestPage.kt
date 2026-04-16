package com.v2ray.ang.composeui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.components.feature.FeaturePageTemplate
import com.v2ray.ang.composeui.effects.MotionProfile
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.p2extended.model.NodeSpeedTestEvent
import com.v2ray.ang.composeui.p2extended.model.NodeSpeedTestUiState
import com.v2ray.ang.composeui.p2extended.model.nodeSpeedTestPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.NodeSpeedTestViewModel

@Composable
fun NodeSpeedTestRoute(
    viewModel: NodeSpeedTestViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    NodeSpeedTestScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                NodeSpeedTestEvent.PrimaryActionClicked -> onPrimaryAction()
                NodeSpeedTestEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun NodeSpeedTestScreen(
    uiState: NodeSpeedTestUiState,
    onEvent: (NodeSpeedTestEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    FeaturePageTemplate(
        title = uiState.title,
        subtitle = "",
        badge = "",
        summary = "",
        heroAccent = uiState.heroAccent,
        metrics = uiState.metrics,
        fields = uiState.fields,
        highlights = emptyList(),
        checklist = emptyList(),
        note = "",
        primaryActionLabel = uiState.primaryActionLabel,
        secondaryActionLabel = uiState.secondaryActionLabel,
        showBottomBar = false,
        currentRoute = "node_speed_test",
        motionProfile = MotionProfile.L1,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(NodeSpeedTestEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = {
            onEvent(NodeSpeedTestEvent.PrimaryActionClicked)
        },
        onSecondaryAction = {
            onEvent(NodeSpeedTestEvent.SecondaryActionClicked)
        },
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun NodeSpeedTestPreview() {
    CryptoVpnTheme {
        NodeSpeedTestScreen(
            uiState = nodeSpeedTestPreviewState(),
            onEvent = {},
        )
    }
}
