package com.v2ray.ang.composeui.pages.p2extended

import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.FileProvider
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.p2extended.model.SecurityCenterEvent
import com.v2ray.ang.composeui.p2extended.model.SecurityCenterUiState
import com.v2ray.ang.composeui.p2extended.model.securityCenterPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.SecurityCenterViewModel
import java.io.File

@Composable
fun SecurityCenterRoute(
    viewModel: SecurityCenterViewModel,
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    SecurityCenterScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                SecurityCenterEvent.PrimaryActionClicked -> {
                    viewModel.exportWallet(
                        onSuccess = { result ->
                            val exportContent = result.exportContent
                            val exportFileName = result.exportFileName
                            if (exportContent.isNullOrBlank() || exportFileName.isNullOrBlank()) {
                                Toast.makeText(context, "导出加密备份失败", Toast.LENGTH_SHORT).show()
                            } else {
                                val exportFile = File(context.cacheDir, exportFileName)
                                exportFile.writeText(exportContent)
                                val exportUri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.cache",
                                    exportFile,
                                )
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = result.exportMimeType
                                    putExtra(Intent.EXTRA_STREAM, exportUri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "导出加密备份"))
                            }
                        },
                        onError = { message -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show() },
                    )
                }
                SecurityCenterEvent.SecondaryActionClicked -> {
                    viewModel.logout(
                        onSuccess = { onSecondaryAction?.invoke() },
                        onError = { message -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show() },
                    )
                }
                SecurityCenterEvent.DestructiveActionClicked -> {
                    viewModel.clearLocalWallet(
                        onSuccess = { message -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show() },
                        onError = { message -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show() },
                    )
                }
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun SecurityCenterScreen(
    uiState: SecurityCenterUiState,
    onEvent: (SecurityCenterEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val metrics = uiState.metrics.map { it.label to it.value }.ifEmpty {
        listOf("状态" to "未接入")
    }
    val securityStatus = uiState.checklist
        .mapNotNull { bullet ->
            val title = bullet.title.takeMeaningfulSecurityText()
            val detail = bullet.detail.takeMeaningfulSecurityText()
            if (title == null || detail == null) null else "$title：$detail" to isHealthySecurityState("$title $detail")
        }
        .take(2)
        .ifEmpty {
            listOf(
                "未接入" to false,
            )
        }
    val securityItems = uiState.highlights
        .mapNotNull { item ->
            val title = item.title.takeMeaningfulSecurityText()
            val detail = item.subtitle.takeMeaningfulSecurityText()
            if (title == null || detail == null) {
                null
            } else {
                Triple(title, detail, item.badge ?: item.trailing.takeMeaningfulSecurityText())
            }
        }
        .take(5)
    val riskBadge = securityItems.firstOrNull { (_, detail, badge) ->
        val combined = listOfNotNull(detail, badge).joinToString(" ")
        !isHealthySecurityState(combined)
    }?.third ?: uiState.badge.takeMeaningfulSecurityText() ?: "未接入"
    P2ExtendedPageScaffold(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        currentRoute = "security_center",
        onBottomNav = onBottomNav,
        hubLabel = riskBadge,
        onHubClick = { onEvent(SecurityCenterEvent.Refresh) },
    ) {
        KpiRow(metrics)
        if (securityStatus.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            P2Card(title = "实时安全状态") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    securityStatus.forEach { (label, healthy) ->
                        SecurityStatusPill(
                            label = label,
                            healthy = healthy,
                        )
                    }
                }
            }
        }
        if (securityItems.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                securityItems.forEach { (title, detail, badge) ->
                    val combined = listOfNotNull(title, detail, badge).joinToString(" ")
                    P2SecurityActionCard(
                        title = title,
                        detail = detail,
                        badge = badge,
                        risk = !isHealthySecurityState(combined),
                    )
                }
            }
        }
        if (uiState.localWalletPresent && uiState.primaryActionLabel != null) {
            Spacer(modifier = Modifier.height(10.dp))
            P2SecurityActionCard(
                title = uiState.primaryActionLabel,
                detail = "导出服务端保存的密文备份包，用于离线保存或后续恢复。",
                badge = "导出",
                onClick = { onEvent(SecurityCenterEvent.PrimaryActionClicked) },
            )
        }
        if (uiState.destructiveActionLabel != null && uiState.localWalletPresent) {
            Spacer(modifier = Modifier.height(10.dp))
            P2SecurityActionCard(
                title = uiState.destructiveActionLabel,
                detail = "仅清除当前设备上的本地加密钱包材料，不删除服务端生命周期和备份记录。",
                badge = "危险",
                risk = true,
                onClick = { onEvent(SecurityCenterEvent.DestructiveActionClicked) },
            )
        }
        if (uiState.secondaryActionLabel != null) {
            Spacer(modifier = Modifier.height(10.dp))
            P2SecurityActionCard(
                title = uiState.secondaryActionLabel,
                detail = "退出当前账号会话，但不会删除本地钱包，适合切换账号或重新登录。",
                badge = "会话",
                onClick = { onEvent(SecurityCenterEvent.SecondaryActionClicked) },
            )
        }
    }
}

private fun String?.takeMeaningfulSecurityText(): String? {
    val normalized = this?.trim().orEmpty()
    return normalized.takeUnless { it.isBlank() || it.isSecurityPlaceholderText() }
}

private fun String.isSecurityPlaceholderText(): Boolean {
    val lower = lowercase()
    val markers = listOf(
        "mock",
        "preview",
        "stub",
        "drop-in",
        "repository",
        "navigation",
        "route",
        "viewmodel",
        "占位",
    )
    return markers.any(lower::contains)
}

private fun isHealthySecurityState(text: String): Boolean {
    val lower = text.lowercase()
    val riskMarkers = listOf("风险", "异常", "拦截", "待接入", "待确认", "未", "warning")
    return riskMarkers.none(lower::contains)
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun SecurityCenterPreview() {
    CryptoVpnTheme {
        SecurityCenterScreen(
            uiState = securityCenterPreviewState(),
            onEvent = {},
        )
    }
}
