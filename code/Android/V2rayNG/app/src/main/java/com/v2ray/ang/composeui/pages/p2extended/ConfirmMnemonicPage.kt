package com.v2ray.ang.composeui.pages.p2extended

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.components.feature.FeaturePageTemplate
import com.v2ray.ang.composeui.effects.MotionProfile
import com.v2ray.ang.composeui.p2extended.model.ConfirmMnemonicEvent
import com.v2ray.ang.composeui.p2extended.model.ConfirmMnemonicUiState
import com.v2ray.ang.composeui.p2extended.model.confirmMnemonicPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.ConfirmMnemonicViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun ConfirmMnemonicRoute(
    viewModel: ConfirmMnemonicViewModel,
    onPrimaryAction: (() -> Unit)? = null,
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    ConfirmMnemonicScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                ConfirmMnemonicEvent.PrimaryActionClicked -> {
                    if (!uiState.isLoading) {
                        viewModel.submitConfirm(
                            onSuccess = { onPrimaryAction?.invoke() },
                            onError = { message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            },
                        )
                    }
                }
                ConfirmMnemonicEvent.SecondaryActionClicked -> if (!uiState.isLoading) onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun ConfirmMnemonicScreen(
    uiState: ConfirmMnemonicUiState,
    onEvent: (ConfirmMnemonicEvent) -> Unit,
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
        currentRoute = "confirm_mnemonic",
        motionProfile = MotionProfile.L1,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(ConfirmMnemonicEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = { onEvent(ConfirmMnemonicEvent.PrimaryActionClicked) },
        onSecondaryAction = { onEvent(ConfirmMnemonicEvent.SecondaryActionClicked) },
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ConfirmMnemonicPreview() {
    CryptoVpnTheme {
        ConfirmMnemonicScreen(
            uiState = confirmMnemonicPreviewState(),
            onEvent = {},
        )
    }
}
