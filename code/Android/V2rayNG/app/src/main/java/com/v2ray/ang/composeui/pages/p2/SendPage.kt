package com.v2ray.ang.composeui.pages.p2

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.v2ray.ang.composeui.components.inputs.GlassTextField
import com.v2ray.ang.composeui.p2.model.SendEvent
import com.v2ray.ang.composeui.p2.model.SendUiState
import com.v2ray.ang.composeui.p2.model.sendPreviewState
import com.v2ray.ang.composeui.p2.viewmodel.SendViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.util.QRCodeDecoder
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanCustomCode
import io.github.g00fy2.quickie.config.BarcodeFormat
import io.github.g00fy2.quickie.config.ScannerConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SendRoute(
    viewModel: SendViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(uiState.redirectRoute) {
        uiState.redirectRoute?.let { onBottomNav(it) }
    }
    SendScreen(
        uiState = uiState,
        onEvent = { event -> viewModel.onEvent(event) },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun SendScreen(
    uiState: SendUiState,
    onEvent: (SendEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showScanOptions by remember { mutableStateOf(false) }
    val scanLauncher = rememberLauncherForActivityResult(
        contract = ScanCustomCode(),
    ) { result ->
        if (result !is QRResult.QRSuccess) {
            return@rememberLauncherForActivityResult
        }
        val scannedText = result.content.rawValue.orEmpty().trim()
        if (scannedText.isBlank()) {
            Toast.makeText(context, "未识别到可用地址", Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }
        onEvent(SendEvent.FieldChanged("to", normalizeScannedAddress(scannedText)))
    }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        if (uri == null) {
            return@rememberLauncherForActivityResult
        }
        scope.launch {
            val scannedText = decodeQrFromImageUri(context, uri)
            if (scannedText.isNullOrBlank()) {
                Toast.makeText(context, "未识别到二维码内容", Toast.LENGTH_SHORT).show()
                return@launch
            }
            onEvent(SendEvent.FieldChanged("to", normalizeScannedAddress(scannedText)))
        }
    }
    LaunchedEffect(uiState.feedbackMessage) {
        uiState.feedbackMessage
            ?.takeIf { it.isNotBlank() && it != "正在发送..." }
            ?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }
    if (showScanOptions) {
        AlertDialog(
            onDismissRequest = { showScanOptions = false },
            title = { Text("选择方式") },
            text = { Text("直接扫码或从图片识别二维码") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showScanOptions = false
                        scanLauncher.launch(
                            ScannerConfig.build {
                                setHapticSuccessFeedback(true)
                                setShowTorchToggle(true)
                                setShowCloseButton(true)
                                setBarcodeFormats(listOf(BarcodeFormat.QR_CODE))
                            },
                        )
                    },
                ) {
                    Text("扫码")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showScanOptions = false
                        imagePickerLauncher.launch("image/*")
                    },
                ) {
                    Text("图片")
                }
            },
        )
    }
    val toField = uiState.fields.firstOrNull { it.key == "to" }
    val amountField = uiState.fields.firstOrNull { it.key == "amount" }
    val memoField = uiState.fields.firstOrNull { it.key == "memo" }
    P2CorePageScaffold(
        kicker = "",
        title = uiState.title,
        subtitle = "",
        badge = "",
        activeSection = CoreNavSection.Wallet,
        onBottomNav = onBottomNav,
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = {
            onEvent(SendEvent.PrimaryActionClicked)
        },
    ) {
        P2CoreHeroValueCard(
            label = uiState.availableBalanceLabel,
            value = uiState.availableBalance,
            supportingText = "",
        )
        if (uiState.networkOptions.isNotEmpty()) {
            SendFilterRow(
                chips = uiState.networkOptions.map { it.label to it.selected },
                onChipClick = { index ->
                    uiState.networkOptions.getOrNull(index)?.let { option ->
                        if (!option.selected) {
                            onEvent(SendEvent.NetworkSelected(option.id))
                        }
                    }
                },
            )
        }
        if (uiState.assetOptions.isNotEmpty()) {
            SendFilterRow(
                chips = uiState.assetOptions.map { it.label to it.selected },
                onChipClick = { index ->
                    uiState.assetOptions.getOrNull(index)?.let { option ->
                        if (!option.selected) {
                            onEvent(SendEvent.AssetSelected(option.id))
                        }
                    }
                },
            )
        }

        toField?.let { field ->
            P2CoreCard {
                SendFieldHeader(
                    title = field.label,
                    actionLabel = "扫码",
                    onActionClick = {
                        showScanOptions = true
                    },
                )
                GlassTextField(
                    value = field.value,
                    label = "",
                    placeholder = field.supportingText,
                    onValueChange = { onEvent(SendEvent.FieldChanged(field.key, it)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        amountField?.let { field ->
            P2CoreCard {
                SendFieldHeader(title = field.label)
                GlassTextField(
                    value = field.value,
                    label = "",
                    placeholder = field.supportingText,
                    onValueChange = { onEvent(SendEvent.FieldChanged(field.key, it)) },
                )
            }
        }

        memoField?.takeIf { it.value.isNotBlank() }?.let { field ->
            P2CoreCard {
                SendFieldHeader(title = field.label)
                GlassTextField(
                    value = field.value,
                    label = "",
                    placeholder = field.supportingText,
                    onValueChange = { onEvent(SendEvent.FieldChanged(field.key, it)) },
                )
            }
        }
    }
}

@Composable
private fun SendFilterRow(
    chips: List<Pair<String, Boolean>>,
    onChipClick: (Int) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        chips.forEachIndexed { index, (label, active) ->
            FilterChip(
                selected = active,
                onClick = { onChipClick(index) },
                label = { Text(label) },
            )
        }
    }
}

@Composable
private fun SendFieldHeader(
    title: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = Color(0xFF182345),
        )
        if (!actionLabel.isNullOrBlank() && onActionClick != null) {
            OutlinedButton(
                onClick = onActionClick,
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(actionLabel)
            }
        } else {
            Box(modifier = Modifier)
        }
    }
}

private fun normalizeScannedAddress(rawValue: String): String {
    val normalized = rawValue.trim()
    val scheme = normalized.substringBefore(":", missingDelimiterValue = "").lowercase()
    if (scheme in setOf("tron", "solana", "trx", "sol")) {
        return normalized.substringAfter(":", normalized).substringBefore("?").ifBlank { normalized }
    }
    return normalized
}

private suspend fun decodeQrFromImageUri(
    context: android.content.Context,
    uri: Uri,
): String? = withContext(Dispatchers.IO) {
    runCatching {
        context.contentResolver.openInputStream(uri)?.use { input ->
            QRCodeDecoder.syncDecodeQRCode(BitmapFactory.decodeStream(input))
        }
    }.getOrNull()
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun SendPreview() {
    CryptoVpnTheme {
        SendScreen(
            uiState = sendPreviewState(),
            onEvent = {},
        )
    }
}
