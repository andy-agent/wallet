package com.v2ray.ang.composeui.pages.wallet

import android.app.Application
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.bridge.wallet.WalletBridgeRepository
import com.v2ray.ang.composeui.theme.CryptoVPNTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class SendResultType {
    SUCCESS,
    FAILED,
    PENDING,
}

sealed class SendResultState {
    data object Idle : SendResultState()
    data class Loaded(
        val resultType: SendResultType,
        val amount: String,
        val symbol: String,
        val recipient: String,
        val txHash: String?,
        val fee: String,
    ) : SendResultState()
}

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

@Composable
fun SendResultPage(
    viewModel: SendResultViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    resultType: SendResultType = SendResultType.SUCCESS,
    onNavigateToHome: () -> Unit = {},
    onNavigateToWallet: () -> Unit = {},
    onRetry: () -> Unit = {},
    onViewExplorer: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(resultType) {
        viewModel.loadResult(fallbackResultType = resultType)
    }

    WalletPageBackdrop {
        when (val currentState = state) {
            is SendResultState.Loaded -> SendResultLoadedContent(
                state = currentState,
                onNavigateToHome = onNavigateToHome,
                onNavigateToWallet = onNavigateToWallet,
                onRetry = onRetry,
                onViewExplorer = onViewExplorer,
            )

            SendResultState.Idle -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = WalletAccent)
            }
        }
    }
}

@Composable
private fun SendResultLoadedContent(
    state: SendResultState.Loaded,
    onNavigateToHome: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onRetry: () -> Unit,
    onViewExplorer: () -> Unit,
) {
    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = WalletPagePadding, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                when (state.resultType) {
                    SendResultType.SUCCESS -> {
                        WalletPrimaryButton(
                            label = "返回钱包",
                            onClick = onNavigateToWallet,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        WalletSecondaryButton(
                            label = "查看交易",
                            onClick = onViewExplorer,
                            modifier = Modifier.fillMaxWidth(),
                            icon = Icons.Default.OpenInBrowser,
                        )
                    }

                    SendResultType.FAILED -> {
                        WalletPrimaryButton(
                            label = "重试",
                            onClick = onRetry,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        WalletSecondaryButton(
                            label = "返回钱包",
                            onClick = onNavigateToWallet,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    SendResultType.PENDING -> {
                        WalletPrimaryButton(
                            label = "返回钱包",
                            onClick = onNavigateToWallet,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Text(
                            text = "交易正在处理中，可能需要几分钟时间确认。",
                            style = MaterialTheme.typography.bodySmall,
                            color = WalletTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = WalletPagePadding,
                end = WalletPagePadding,
                top = 12.dp,
                bottom = 18.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                WalletCloseBar(title = "转账结果", onClose = onNavigateToHome)
            }

            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    SendResultIcon(resultType = state.resultType)
                }
            }

            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = when (state.resultType) {
                                SendResultType.SUCCESS -> "发送成功"
                                SendResultType.FAILED -> "发送失败"
                                SendResultType.PENDING -> "处理中"
                            },
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = resultAccent(state.resultType),
                        )
                        Text(
                            text = "${state.amount} ${state.symbol}",
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold,
                            color = WalletTextPrimary,
                        )
                        Text(
                            text = "发送至 ${walletShortAddress(state.recipient)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = WalletTextSecondary,
                        )
                    }
                }
            }

            item {
                WalletGlassCard(accent = resultAccent(state.resultType)) {
                    WalletInfoRow(label = "状态", value = state.resultType.name.lowercase().replaceFirstChar { it.uppercase() })
                    WalletInfoRow(label = "矿工费", value = state.fee)
                    state.txHash?.let { txHash ->
                        WalletInfoRow(label = "交易哈希", value = walletShortAddress(txHash))
                    }
                }
            }
        }
    }
}

@Composable
private fun SendResultIcon(resultType: SendResultType) {
    val infiniteTransition = rememberInfiniteTransition(label = "send-result")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "send-result-scale",
    )
    val icon = when (resultType) {
        SendResultType.SUCCESS -> Icons.Default.Check
        SendResultType.FAILED -> Icons.Default.Close
        SendResultType.PENDING -> Icons.Default.Schedule
    }
    val accent = resultAccent(resultType)

    Box(
        modifier = Modifier
            .size(132.dp)
            .scale(if (resultType == SendResultType.SUCCESS) scale else 1f)
            .background(accent.copy(alpha = 0.14f), shape = androidx.compose.foundation.shape.CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(60.dp),
        )
    }
}

private fun resultAccent(resultType: SendResultType): Color {
    return when (resultType) {
        SendResultType.SUCCESS -> WalletAccent
        SendResultType.FAILED -> WalletDanger
        SendResultType.PENDING -> Color(0xFFE8B35C)
    }
}

@Preview(showBackground = true)
@Composable
private fun SendResultPageSuccessPreview() {
    CryptoVPNTheme {
        SendResultPage(resultType = SendResultType.SUCCESS)
    }
}

@Preview(showBackground = true)
@Composable
private fun SendResultPageFailedPreview() {
    CryptoVPNTheme {
        SendResultPage(resultType = SendResultType.FAILED)
    }
}
