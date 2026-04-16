package com.v2ray.ang.composeui.pages.p2extended

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.components.feature.FeaturePageTemplate
import com.v2ray.ang.composeui.effects.MotionProfile
import com.v2ray.ang.composeui.p2extended.model.BackupMnemonicEvent
import com.v2ray.ang.composeui.p2extended.model.BackupMnemonicUiState
import com.v2ray.ang.composeui.p2extended.model.backupMnemonicPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.BackupMnemonicViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun BackupMnemonicRoute(
    viewModel: BackupMnemonicViewModel,
    onPrimaryAction: (() -> Unit)? = null,
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    BackupMnemonicScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                BackupMnemonicEvent.PrimaryActionClicked -> {
                    if (!uiState.isLoading) {
                        viewModel.submitBackupAcknowledgement(
                            onSuccess = { onPrimaryAction?.invoke() },
                            onError = { message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            },
                        )
                    }
                }
                BackupMnemonicEvent.SecondaryActionClicked -> if (!uiState.isLoading) onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun BackupMnemonicScreen(
    uiState: BackupMnemonicUiState,
    onEvent: (BackupMnemonicEvent) -> Unit,
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
        currentRoute = "backup_mnemonic",
        motionProfile = MotionProfile.L1,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(BackupMnemonicEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = { onEvent(BackupMnemonicEvent.PrimaryActionClicked) },
        onSecondaryAction = { onEvent(BackupMnemonicEvent.SecondaryActionClicked) },
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun BackupMnemonicPreview() {
    CryptoVpnTheme {
        BackupMnemonicScreen(
            uiState = backupMnemonicPreviewState(),
            onEvent = {},
        )
    }
}
