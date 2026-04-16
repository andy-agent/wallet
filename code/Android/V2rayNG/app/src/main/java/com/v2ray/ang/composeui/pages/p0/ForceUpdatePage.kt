package com.v2ray.ang.composeui.pages.p0

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.model.ForceUpdateEvent
import com.v2ray.ang.composeui.p0.model.ForceUpdateUiState
import com.v2ray.ang.composeui.p0.model.forceUpdatePreviewState
import com.v2ray.ang.composeui.p0.ui.P01Card
import com.v2ray.ang.composeui.p0.ui.P01CardCopy
import com.v2ray.ang.composeui.p0.ui.P01CardHeader
import com.v2ray.ang.composeui.p0.ui.P01PhoneScaffold
import com.v2ray.ang.composeui.p0.ui.P01PrimaryButton
import com.v2ray.ang.composeui.p0.ui.P01SuccessBadge
import com.v2ray.ang.composeui.p0.viewmodel.ForceUpdateViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun ForceUpdateRoute(
    viewModel: ForceUpdateViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    ForceUpdateScreen(
        uiState = uiState,
        onPrimaryAction = {
            viewModel.onEvent(ForceUpdateEvent.PrimaryActionClicked)
            onPrimaryAction()
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun ForceUpdateScreen(
    uiState: ForceUpdateUiState,
    onPrimaryAction: () -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P01PhoneScaffold(
        currentRoute = CryptoVpnRouteSpec.vpnHome.name,
        onBottomNav = onBottomNav,
    ) {
        P01Card(centered = true) {
            P01SuccessBadge(symbol = "!", tint = androidx.compose.ui.graphics.Color(0xFFF6B155))
            P01CardHeader(title = uiState.title.ifBlank { "需要更新应用" })
            P01PrimaryButton(
                text = uiState.primaryActionLabel.ifBlank { "立即更新" },
                onClick = onPrimaryAction,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ForceUpdatePreview() {
    CryptoVpnTheme {
        ForceUpdateScreen(
            uiState = forceUpdatePreviewState(),
            onPrimaryAction = {},
        )
    }
}
