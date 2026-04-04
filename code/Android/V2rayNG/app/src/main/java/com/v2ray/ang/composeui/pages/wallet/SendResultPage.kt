package com.v2ray.ang.composeui.pages.wallet

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.bridge.wallet.WalletBridgeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * 发送结果类型
 */
enum class SendResultType {
    SUCCESS,    // 成功
    FAILED,     // 失败
    PENDING     // 处理中
}

/**
 * 发送结果页状态
 */
sealed class SendResultState {
    object Idle : SendResultState()
    data class Loaded(
        val resultType: SendResultType,
        val amount: String,
        val symbol: String,
        val recipient: String,
        val txHash: String?,
        val fee: String
    ) : SendResultState()
}

/**
 * 发送结果页ViewModel
 */
class SendResultViewModel(application: Application) : AndroidViewModel(application) {
    private val walletBridgeRepository = WalletBridgeRepository(application)
    private val _state = MutableStateFlow<SendResultState>(SendResultState.Idle)
    val state: StateFlow<SendResultState> = _state

    fun loadResult(fallbackResultType: SendResultType = SendResultType.PENDING) {
        viewModelScope.launch {
            val currentOrderId = walletBridgeRepository.getCurrentOrderId()
            if (currentOrderId.isNullOrBlank()) {
                _state.value = SendResultState.Loaded(
                    resultType = SendResultType.FAILED,
                    amount = "--",
                    symbol = "USDT",
                    recipient = "unknown",
                    txHash = null,
                    fee = "--",
                )
                return@launch
            }
            val cachedOrder = walletBridgeRepository.getCachedOrder(currentOrderId)
            val orderResult = walletBridgeRepository.getOrder(currentOrderId)
            val order = orderResult.getOrNull()
            val resolvedType = when ((order?.status ?: cachedOrder?.status).orEmpty().uppercase()) {
                "COMPLETED", "FULFILLED", "PAID" -> SendResultType.SUCCESS
                "CANCELED", "CANCELLED", "FAILED" -> SendResultType.FAILED
                else -> fallbackResultType
            }
            _state.value = SendResultState.Loaded(
                resultType = resolvedType,
                amount = order?.payment?.amountCrypto ?: cachedOrder?.amount ?: "--",
                symbol = order?.payment?.assetCode ?: cachedOrder?.assetCode ?: "USDT",
                recipient = order?.payment?.receiveAddress ?: cachedOrder?.planName ?: currentOrderId,
                txHash = order?.payment?.txHash,
                fee = "network fee by chain",
            )
        }
    }
}

/**
 * 发送结果页
 * 显示转账成功/失败的结果
 */
@Composable
fun SendResultPage(
    viewModel: SendResultViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    resultType: SendResultType = SendResultType.SUCCESS,
    onNavigateToHome: () -> Unit = {},
    onNavigateToWallet: () -> Unit = {},
    onRetry: () -> Unit = {},
    onViewExplorer: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(resultType) {
        viewModel.loadResult(fallbackResultType = resultType)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        when (val currentState = state) {
            is SendResultState.Loaded -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    // 结果图标
                    SendResultIcon(resultType = currentState.resultType)

                    Spacer(modifier = Modifier.height(32.dp))

                    // 结果标题
                    Text(
                        text = when (currentState.resultType) {
                            SendResultType.SUCCESS -> "发送成功"
                            SendResultType.FAILED -> "发送失败"
                            SendResultType.PENDING -> "处理中"
                        },
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (currentState.resultType) {
                            SendResultType.SUCCESS -> Color(0xFF22C55E)
                            SendResultType.FAILED -> Color(0xFFEF4444)
                            SendResultType.PENDING -> Color(0xFFF59E0B)
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 转账金额
                    Text(
                        text = "${currentState.amount} ${currentState.symbol}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 收款地址
                    Text(
                        text = "发送至: ${currentState.recipient}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // 交易详情
                    SendTransactionDetails(state = currentState)

                    Spacer(modifier = Modifier.weight(1f))

                    // 操作按钮
                    when (currentState.resultType) {
                        SendResultType.SUCCESS -> {
                            SendSuccessActions(
                                onNavigateToWallet = onNavigateToWallet,
                                onViewExplorer = onViewExplorer
                            )
                        }
                        SendResultType.FAILED -> {
                            SendFailedActions(
                                onRetry = onRetry,
                                onNavigateToWallet = onNavigateToWallet
                            )
                        }
                        SendResultType.PENDING -> {
                            SendPendingActions(
                                onNavigateToWallet = onNavigateToWallet
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            else -> {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun SendResultIcon(resultType: SendResultType) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val (icon, iconColor, bgColor) = when (resultType) {
        SendResultType.SUCCESS -> Triple(
            Icons.Default.Check,
            Color(0xFF22C55E),
            Color(0xFF22C55E).copy(alpha = 0.1f)
        )
        SendResultType.FAILED -> Triple(
            Icons.Default.Close,
            Color(0xFFEF4444),
            Color(0xFFEF4444).copy(alpha = 0.1f)
        )
        SendResultType.PENDING -> Triple(
            Icons.Default.Schedule,
            Color(0xFFF59E0B),
            Color(0xFFF59E0B).copy(alpha = 0.1f)
        )
    }

    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(if (resultType == SendResultType.SUCCESS) scale else 1f)
            .background(bgColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(60.dp),
            tint = iconColor
        )
    }
}

@Composable
private fun SendTransactionDetails(state: SendResultState.Loaded) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // 矿工费
            SendDetailRow(label = "矿工费", value = state.fee)

            // 交易哈希
            state.txHash?.let { txHash ->
                Spacer(modifier = Modifier.height(12.dp))
                SendDetailRow(label = "交易哈希", value = txHash)
            }
        }
    }
}

@Composable
private fun SendDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SendSuccessActions(
    onNavigateToWallet: () -> Unit,
    onViewExplorer: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = onNavigateToWallet,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "返回钱包",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onViewExplorer,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(
                imageVector = Icons.Default.OpenInBrowser,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "查看交易",
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun SendFailedActions(
    onRetry: () -> Unit,
    onNavigateToWallet: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "重试",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onNavigateToWallet,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "返回钱包",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SendPendingActions(
    onNavigateToWallet: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = onNavigateToWallet,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "返回钱包",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "交易正在处理中，可能需要几分钟时间确认。",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SendResultPageSuccessPreview() {
    MaterialTheme {
        SendResultPage(resultType = SendResultType.SUCCESS)
    }
}

@Preview(showBackground = true)
@Composable
fun SendResultPageFailedPreview() {
    MaterialTheme {
        SendResultPage(resultType = SendResultType.FAILED)
    }
}
