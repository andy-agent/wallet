package com.app.feature.wallet.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.common.components.GradientCard
import com.app.common.components.SecondaryButton
import com.app.common.components.StatusChip
import com.app.common.widgets.MetricPill
import com.app.core.theme.BluePrimary
import com.app.core.theme.CardGlassStrong
import com.app.core.theme.TextSecondary
import com.app.core.ui.AppScaffold
import com.app.feature.wallet.viewmodel.WalletViewModel

@Composable
fun ReceiveScreen(
    symbol: String,
    viewModel: WalletViewModel = viewModel(),
    onBack: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    val asset = state.assets.firstOrNull { it.symbol.equals(symbol, true) }
    var address by rememberSaveable(symbol) { mutableStateOf("") }
    var actionFeedback by rememberSaveable(symbol) { mutableStateOf<String?>(null) }
    val receiveDetails = if (address.isBlank()) null else viewModel.receiveDetails(symbol, address)

    LaunchedEffect(symbol) {
        viewModel.receiveAddress(symbol) { address = it }
    }

    AppScaffold(title = "收款页", onBack = onBack) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 18.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            GradientCard(title = "接收 ${asset?.symbol ?: symbol}", subtitle = asset?.name ?: "链上收款") {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricPill("网络", receiveDetails?.network?.chainName ?: "--")
                    MetricPill("地址类型", address.takeLast(6).ifBlank { "--" })
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    receiveDetails?.networkGuidance ?: "正在加载地址与网络信息。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }

            GradientCard(title = "收款二维码", subtitle = "分享二维码或直接复制链上地址") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .background(CardGlassStrong.copy(alpha = 0.68f), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    PseudoQrCode(seed = address.ifBlank { symbol })
                }
                Spacer(Modifier.height(12.dp))
                Text(address.ifBlank { "地址加载中..." }, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    SecondaryButton(
                        text = "复制地址",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            actionFeedback = if (address.isBlank()) "地址尚未生成，暂时无法复制。" else "地址已复制到剪贴板（mock）。"
                        },
                    )
                    SecondaryButton(
                        text = "分享收款",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            actionFeedback = receiveDetails?.shareText ?: "地址尚未生成，暂时无法分享。"
                        },
                    )
                }
                actionFeedback?.let { feedback ->
                    Spacer(Modifier.height(12.dp))
                    StatusChip(text = feedback, positive = !feedback.contains("无法"))
                }
            }

            GradientCard(title = "到账说明", subtitle = "Memo/Tag 与网络风险提示") {
                Text(
                    receiveDetails?.memoGuidance ?: "正在加载到账说明。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "建议让付款方先发送一笔小额测试转账，确认网络、地址与币种完全一致后再发起大额转账。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }
        }
    }
}

@Composable
private fun PseudoQrCode(seed: String) {
    val bits = seed.encodeToByteArray().toList().flatMap { byte -> (0 until 8).map { bit -> (byte.toInt() shr bit) and 1 } }
    Canvas(modifier = Modifier.size(176.dp)) {
        val cells = 21
        val cellSize = size.width / cells
        for (row in 0 until cells) {
            for (col in 0 until cells) {
                val index = (row * cells + col) % bits.size.coerceAtLeast(1)
                val finder = (row < 5 && col < 5) || (row < 5 && col > 15) || (row > 15 && col < 5)
                val fill = finder || bits.getOrElse(index) { 0 } == 1
                drawRect(
                    color = if (fill) BluePrimary else Color.White,
                    topLeft = Offset(col * cellSize, row * cellSize),
                    size = androidx.compose.ui.geometry.Size(cellSize - 2.dp.toPx(), cellSize - 2.dp.toPx()),
                )
            }
        }
    }
}
