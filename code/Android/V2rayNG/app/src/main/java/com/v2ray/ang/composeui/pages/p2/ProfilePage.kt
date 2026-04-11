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
    val profileFocus = rememberCoreLoopingIndex(itemCount = maxOf(uiState.highlights.size, 1), durationMillis = 4200)
    val accountInfo = uiState.checklist.firstOrNull { it.title == "邮箱" }?.detail
        ?: uiState.highlights.firstOrNull { it.badge == "LIVE" }?.title
        ?: "--"
    val onlineDevices = uiState.metrics.firstOrNull { it.label == "订单数量" }?.value ?: "--"
    val currentPlan = uiState.metrics.firstOrNull { it.label == "当前套餐" }?.value ?: "--"
    val accountStatus = uiState.metrics.firstOrNull { it.label == "账号状态" }?.value ?: "--"
    P2CorePageScaffold(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        badge = uiState.badge,
        activeSection = CoreNavSection.Profile,
        onBottomNav = onBottomNav,
    ) {
        P2CoreCard {
            P2CoreCardHeader(
                title = "账户信息",
                subtitle = accountInfo,
                trailing = uiState.badge,
                trailingColor = Color(0xFFEAF6FF),
            )
            P2CoreHeroValueCard(
                label = "当前套餐",
                value = currentPlan,
                supportingText = "订单数: $onlineDevices",
                highlight = uiState.badge,
                stats = uiState.metrics.drop(1).take(2).map { it.label to it.value },
            )
            P2CoreActionValueRow(
                label = "账号状态",
                value = accountStatus,
                actionLabel = uiState.primaryActionLabel,
                onAction = { onBottomNav(CryptoVpnRouteSpec.securityCenter.pattern) },
                valueColor = Color(0xFF2F5BFF),
            )
        }
        P2CoreCard {
            uiState.highlights.forEachIndexed { index, item ->
                val route = when (item.badge) {
                    "SEC" -> CryptoVpnRouteSpec.securityCenter.pattern
                    "ORDER" -> CryptoVpnRouteSpec.orderList.pattern
                    "INVITE" -> CryptoVpnRouteSpec.inviteCenter.pattern
                    "LEGAL" -> CryptoVpnRouteSpec.legalDocuments.pattern
                    "APP" -> CryptoVpnRouteSpec.aboutApp.pattern
                    else -> null
                }
                P2CoreListRow(
                    title = item.title,
                    subtitle = item.subtitle,
                    trailing = item.trailing,
                    emphasis = if (index == profileFocus % maxOf(uiState.highlights.size, 1)) {
                        P2CoreRowEmphasis.Brand
                    } else {
                        P2CoreRowEmphasis.Neutral
                    },
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
