package com.v2ray.ang.composeui.pages.vpn

import androidx.compose.animation.*
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
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 订单结果类型
 */
enum class OrderResultType {
    SUCCESS,    // 成功
    FAILED,     // 失败
    PENDING     // 处理中
}

/**
 * 订单结果页状态
 */
sealed class OrderResultState {
    object Idle : OrderResultState()
    data class Loaded(
        val resultType: OrderResultType,
        val orderId: String,
        val amount: String,
        val message: String,
        val txHash: String? = null
    ) : OrderResultState()
}

/**
 * 订单结果页ViewModel
 */
class OrderResultViewModel : ViewModel() {
    private val _state = MutableStateFlow<OrderResultState>(OrderResultState.Idle)
    val state: StateFlow<OrderResultState> = _state

    fun loadResult(
        resultType: OrderResultType = OrderResultType.SUCCESS,
        orderId: String = "ORD-20240115-001",
        amount: String = "$26.99",
        txHash: String? = "0x742d35...5f0bEb"
    ) {
        val message = when (resultType) {
            OrderResultType.SUCCESS -> "支付成功！您的套餐已激活。"
            OrderResultType.FAILED -> "支付失败，请重试或联系客服。"
            OrderResultType.PENDING -> "订单处理中，请稍后查看。"
        }
        
        _state.value = OrderResultState.Loaded(
            resultType = resultType,
            orderId = orderId,
            amount = amount,
            message = message,
            txHash = txHash
        )
    }
}

/**
 * 订单结果页
 * 显示订单支付成功/失败的结果
 */
@Composable
fun OrderResultPage(
    viewModel: OrderResultViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    resultType: OrderResultType = OrderResultType.SUCCESS,
    onNavigateToHome: () -> Unit = {},
    onNavigateToOrders: () -> Unit = {},
    onRetry: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadResult(resultType = resultType)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        when (val currentState = state) {
            is OrderResultState.Loaded -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    // 结果图标
                    ResultIcon(resultType = currentState.resultType)

                    Spacer(modifier = Modifier.height(32.dp))

                    // 结果标题
                    Text(
                        text = when (currentState.resultType) {
                            OrderResultType.SUCCESS -> "支付成功"
                            OrderResultType.FAILED -> "支付失败"
                            OrderResultType.PENDING -> "处理中"
                        },
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (currentState.resultType) {
                            OrderResultType.SUCCESS -> Color(0xFF22C55E)
                            OrderResultType.FAILED -> Color(0xFFEF4444)
                            OrderResultType.PENDING -> Color(0xFFF59E0B)
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 结果消息
                    Text(
                        text = currentState.message,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // 订单详情
                    OrderResultDetailsCard(state = currentState)

                    Spacer(modifier = Modifier.weight(1f))

                    // 操作按钮
                    when (currentState.resultType) {
                        OrderResultType.SUCCESS -> {
                            SuccessActions(
                                onNavigateToHome = onNavigateToHome,
                                onNavigateToOrders = onNavigateToOrders
                            )
                        }
                        OrderResultType.FAILED -> {
                            FailedActions(
                                onRetry = onRetry,
                                onNavigateToHome = onNavigateToHome
                            )
                        }
                        OrderResultType.PENDING -> {
                            PendingActions(
                                onNavigateToOrders = onNavigateToOrders
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
private fun ResultIcon(resultType: OrderResultType) {
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
        OrderResultType.SUCCESS -> Triple(
            Icons.Default.Check,
            Color(0xFF22C55E),
            Color(0xFF22C55E).copy(alpha = 0.1f)
        )
        OrderResultType.FAILED -> Triple(
            Icons.Default.Close,
            Color(0xFFEF4444),
            Color(0xFFEF4444).copy(alpha = 0.1f)
        )
        OrderResultType.PENDING -> Triple(
            Icons.Default.Schedule,
            Color(0xFFF59E0B),
            Color(0xFFF59E0B).copy(alpha = 0.1f)
        )
    }

    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(if (resultType == OrderResultType.SUCCESS) scale else 1f)
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
private fun OrderResultDetailsCard(state: OrderResultState.Loaded) {
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
            // 订单号
            ResultDetailRow(label = "订单号", value = state.orderId)

            Spacer(modifier = Modifier.height(12.dp))

            // 支付金额
            ResultDetailRow(label = "支付金额", value = state.amount)

            // 交易哈希（仅成功时显示）
            if (state.resultType == OrderResultType.SUCCESS && state.txHash != null) {
                Spacer(modifier = Modifier.height(12.dp))
                ResultDetailRow(label = "交易哈希", value = state.txHash)
            }
        }
    }
}

@Composable
private fun ResultDetailRow(label: String, value: String) {
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
private fun SuccessActions(
    onNavigateToHome: () -> Unit,
    onNavigateToOrders: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = onNavigateToHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "返回首页",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onNavigateToOrders,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "查看订单",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FailedActions(
    onRetry: () -> Unit,
    onNavigateToHome: () -> Unit
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
                text = "重新支付",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onNavigateToHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "返回首页",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PendingActions(
    onNavigateToOrders: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = onNavigateToOrders,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "查看订单状态",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "订单处理可能需要几分钟时间，请耐心等待。",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OrderResultPageSuccessPreview() {
    MaterialTheme {
        OrderResultPage(resultType = OrderResultType.SUCCESS)
    }
}

@Preview(showBackground = true)
@Composable
fun OrderResultPageFailedPreview() {
    MaterialTheme {
        OrderResultPage(resultType = OrderResultType.FAILED)
    }
}
