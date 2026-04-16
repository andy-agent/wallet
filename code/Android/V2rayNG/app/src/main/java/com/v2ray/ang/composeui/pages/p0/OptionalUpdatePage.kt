package com.v2ray.ang.composeui.pages.p0

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.model.OptionalUpdateEvent
import com.v2ray.ang.composeui.p0.model.OptionalUpdateUiState
import com.v2ray.ang.composeui.p0.model.optionalUpdatePreviewState
import com.v2ray.ang.composeui.p0.ui.P01ButtonRow
import com.v2ray.ang.composeui.p0.ui.P01Card
import com.v2ray.ang.composeui.p0.ui.P01CardCopy
import com.v2ray.ang.composeui.p0.ui.P01CardHeader
import com.v2ray.ang.composeui.p0.ui.P01PhoneScaffold
import com.v2ray.ang.composeui.p0.ui.P01SuccessBadge
import com.v2ray.ang.composeui.p0.viewmodel.OptionalUpdateViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun OptionalUpdateRoute(
    viewModel: OptionalUpdateViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    OptionalUpdateScreen(
        uiState = uiState,
        onPrimaryAction = {
            viewModel.onEvent(OptionalUpdateEvent.PrimaryActionClicked)
            onPrimaryAction()
        },
        onSecondaryAction = {
            viewModel.onEvent(OptionalUpdateEvent.SecondaryActionClicked)
            onSecondaryAction?.invoke()
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun OptionalUpdateScreen(
    uiState: OptionalUpdateUiState,
    onPrimaryAction: () -> Unit,
    onSecondaryAction: () -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P01PhoneScaffold(
        currentRoute = CryptoVpnRouteSpec.vpnHome.name,
        onBottomNav = onBottomNav,
    ) {
        P01Card(centered = true) {
            P01SuccessBadge(symbol = "v", tint = Color(0xFF49D89B))
            P01CardHeader(title = uiState.title.ifBlank { "发现新版本" })
            P01ButtonRow(
                primaryLabel = uiState.primaryActionLabel.ifBlank { "立即更新" },
                onPrimaryClick = onPrimaryAction,
                secondaryLabel = uiState.secondaryActionLabel ?: "稍后提醒",
                onSecondaryClick = onSecondaryAction,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun OptionalUpdatePreview() {
    CryptoVpnTheme {
        OptionalUpdateScreen(
            uiState = optionalUpdatePreviewState(),
            onPrimaryAction = {},
            onSecondaryAction = {},
        )
    }
}
