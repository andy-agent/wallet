package com.v2ray.ang.composeui.pages.p2

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p2.model.AboutAppEvent
import com.v2ray.ang.composeui.p2.model.AboutAppUiState
import com.v2ray.ang.composeui.p2.model.aboutAppPreviewState
import com.v2ray.ang.composeui.p2.viewmodel.AboutAppViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun AboutAppRoute(
    viewModel: AboutAppViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    AboutAppScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                AboutAppEvent.PrimaryActionClicked -> onPrimaryAction()
                AboutAppEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun AboutAppScreen(
    uiState: AboutAppUiState,
    onEvent: (AboutAppEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val context = LocalContext.current
    val aboutFocus = rememberCoreLoopingIndex(itemCount = maxOf(uiState.highlights.size, 1), durationMillis = 4200)
    P2CorePageScaffold(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        badge = uiState.badge.takeIf { it.isNotBlank() },
        activeSection = CoreNavSection.Profile,
        onBottomNav = onBottomNav,
        secureHubLabel = aboutHubLabel(aboutFocus),
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = { onEvent(AboutAppEvent.PrimaryActionClicked) },
        secondaryActionLabel = uiState.secondaryActionLabel,
        onSecondaryAction = { onBottomNav(CryptoVpnRouteSpec.legalDocuments.pattern) },
    ) {
        P2CoreCard {
            uiState.highlights.forEachIndexed { index, item ->
                val onClick: (() -> Unit)? = when (item.badge) {
                    "LEGAL" -> ({ onBottomNav(CryptoVpnRouteSpec.legalDocuments.pattern) })
                    "WEB", "SUPPORT" -> ({
                        val url = item.subtitle.takeIf { it.startsWith("http") }
                        if (url != null) {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                        }
                    })
                    else -> null
                }
                P2CoreListRow(
                    title = item.title,
                    subtitle = item.subtitle,
                    trailing = item.trailing,
                    trailingColor = if (onClick != null) Color(0xFF2F5BFF) else Color(0xFF66739D),
                    onClick = onClick,
                )
            }
        }
    }
}

private fun aboutHubLabel(index: Int): String = when (index) {
    0 -> "APP"
    1 -> "VER"
    2 -> "DOC"
    else -> "INFO"
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun AboutAppPreview() {
    CryptoVpnTheme {
        AboutAppScreen(
            uiState = aboutAppPreviewState(),
            onEvent = {},
        )
    }
}
