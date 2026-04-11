package com.v2ray.ang.composeui.pages.p2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p2.model.ProfileEvent
import com.v2ray.ang.composeui.p2.model.ProfileUiState
import com.v2ray.ang.composeui.p2.model.profilePreviewState
import com.v2ray.ang.composeui.p2.viewmodel.ProfileViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    ProfileScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                ProfileEvent.PrimaryActionClicked -> onPrimaryAction()
                ProfileEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onEvent: (ProfileEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P2CorePageScaffold(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.note,
        badge = uiState.badge,
        activeSection = CoreNavSection.Profile,
        onBottomNav = onBottomNav,
    ) {
        P2CoreCard {
            P2CoreCardHeader(
                title = "账户信息",
                subtitle = uiState.checklist.firstOrNull()?.detail ?: "hello@cryptovpn.app · GLOW OPS",
                trailing = uiState.badge,
                trailingColor = Color(0xFFEAF6FF),
            )
            P2CoreHeroValueCard(
                label = "当前套餐",
                value = uiState.metrics.firstOrNull()?.value ?: "--",
                supportingText = "设备在线: ${uiState.checklist.getOrNull(1)?.detail ?: "--"}",
                highlight = uiState.badge,
                stats = uiState.metrics.drop(1).take(2).map { it.label to it.value },
            )
            P2CoreActionValueRow(
                label = "账号状态",
                value = uiState.note,
                actionLabel = "安全中心",
                onAction = { onBottomNav(CryptoVpnRouteSpec.securityCenter.pattern) },
                valueColor = Color(0xFF2F5BFF),
            )
        }
        P2CoreCard {
            uiState.highlights.forEachIndexed { index, item ->
                val route = when (index) {
                    0 -> CryptoVpnRouteSpec.securityCenter.pattern
                    1 -> CryptoVpnRouteSpec.orderList.pattern
                    2 -> CryptoVpnRouteSpec.inviteCenter.pattern
                    3 -> CryptoVpnRouteSpec.legalDocuments.pattern
                    4 -> "about_app"
                    else -> null
                }
                P2CoreListRow(
                    title = item.title,
                    subtitle = item.subtitle,
                    trailing = item.trailing,
                    emphasis = P2CoreRowEmphasis.Brand,
                    trailingColor = Color(0xFF2F5BFF),
                    onClick = route?.let { { onBottomNav(it) } },
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ProfilePreview() {
    CryptoVpnTheme {
        ProfileScreen(
            uiState = profilePreviewState(),
            onEvent = {},
        )
    }
}
