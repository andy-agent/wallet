package com.cryptovpn.ui.global.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.cryptovpn.ui.components.buttons.GradientCTAButton
import com.cryptovpn.ui.components.buttons.SecondaryOutlineButton
import com.cryptovpn.ui.components.cards.TechCard
import com.cryptovpn.ui.theme.CryptoVpnTheme
import com.cryptovpn.ui.theme.TextMuted

@Composable
fun SessionEvictedDialogRoute(
    viewModel: SessionEvictedDialogViewModel,
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    SessionEvictedDialogContent(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                SessionEvictedDialogEvent.Confirm -> onConfirm()
                SessionEvictedDialogEvent.Dismiss -> onDismiss()
                SessionEvictedDialogEvent.Refresh -> Unit
            }
        },
    )
}

@Composable
fun SessionEvictedDialogContent(
    uiState: SessionEvictedDialogUiState,
    onEvent: (SessionEvictedDialogEvent) -> Unit,
) {
    Dialog(
        onDismissRequest = { onEvent(SessionEvictedDialogEvent.Dismiss) },
    ) {
        TechCard(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = uiState.title,
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = uiState.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                )
                uiState.impacts.forEach { bullet ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = bullet.title,
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(
                            text = bullet.detail,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                GradientCTAButton(
                    text = uiState.primaryActionLabel,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onEvent(SessionEvictedDialogEvent.Confirm) },
                )
                SecondaryOutlineButton(
                    onClick = { onEvent(SessionEvictedDialogEvent.Dismiss) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(uiState.secondaryActionLabel) },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SessionEvictedDialogPreview() {
    CryptoVpnTheme {
        SessionEvictedDialogContent(
            uiState = sessionEvictedDialogPreviewState(),
            onEvent = {},
        )
    }
}
