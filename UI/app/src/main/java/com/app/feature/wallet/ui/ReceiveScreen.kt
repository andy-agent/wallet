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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.common.components.*
import com.app.common.widgets.*
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
    var address by remember { mutableStateOf("") }
    LaunchedEffect(symbol) { viewModel.receiveAddress(symbol) { address = it } }
    AppScaffold(title = "收款页", onBack = onBack) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            GradientCard(title = "接收 $symbol", subtitle = "二维码与链上地址") {
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
                Text(address, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricPill("网络", symbol)
                    MetricPill("地址尾号", address.takeLast(6))
                }
            }
            GradientCard(title = "使用说明", subtitle = "分享二维码或复制地址") {
                Text("后续可接入系统分享、复制和实际二维码生成。", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun PseudoQrCode(seed: String) {
    val bits = remember(seed) { seed.encodeToByteArray().toList().flatMap { byte -> (0 until 8).map { bit -> (byte.toInt() shr bit) and 1 } } }
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
