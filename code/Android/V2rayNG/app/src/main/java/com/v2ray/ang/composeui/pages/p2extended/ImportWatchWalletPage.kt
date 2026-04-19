package com.v2ray.ang.composeui.pages.p2extended

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.components.feature.FeaturePageTemplate
import com.v2ray.ang.composeui.effects.MotionProfile
import com.v2ray.ang.composeui.p2extended.model.ImportWatchWalletEvent
import com.v2ray.ang.composeui.p2extended.model.ImportWatchWalletUiState
import com.v2ray.ang.composeui.p2extended.model.importWatchWalletPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.ImportWatchWalletViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun ImportWatchWalletRoute(
    viewModel: ImportWatchWalletViewModel,
    onPrimaryAction: (String?) -> Unit = { _ -> },
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    ImportWatchWalletScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                ImportWatchWalletEvent.PrimaryActionClicked -> if (!uiState.isLoading) {
                    viewModel.submitImport(
                        onSuccess = onPrimaryAction,
                        onError = { message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        },
                    )
                }
                ImportWatchWalletEvent.SecondaryActionClicked -> if (!uiState.isLoading) onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun ImportWatchWalletScreen(
    uiState: ImportWatchWalletUiState,
    onEvent: (ImportWatchWalletEvent) -> Unit,
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
        currentRoute = "import_watch_wallet",
        motionProfile = MotionProfile.L1,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(ImportWatchWalletEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = { onEvent(ImportWatchWalletEvent.PrimaryActionClicked) },
        onSecondaryAction = { onEvent(ImportWatchWalletEvent.SecondaryActionClicked) },
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ImportWatchWalletPreview() {
    CryptoVpnTheme {
        ImportWatchWalletScreen(
            uiState = importWatchWalletPreviewState(),
            onEvent = {},
        )
    }
}
