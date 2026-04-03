package com.cryptovpn.ui.pages.wallet

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cryptovpn.ui.theme.CryptoVPNTheme
import java.math.BigDecimal

/**
 * 发送结果页
 */
@Composable
fun SendResultPage(
    isSuccess: Boolean,
    txHash: String? = null,
    amount: BigDecimal? = null,
    recipientAddress: String? = null,
    symbol: String = "USDT",
    errorMessage: String? = null,
    onViewTransaction: (String) -> Unit = {},
    onBackToWallet: () -> Unit = {},
    onRetry: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1020))
    ) {
        if (isSuccess) {
            SuccessContent(
                txHash = txHash,
                amount = amount,
                recipientAddress = recipientAddress,
                symbol = symbol,
                onViewTransaction = onViewTransaction,
                onBackToWallet = onBackToWallet
            )
        } else {
            FailureContent(
                errorMessage = errorMessage,
                onRetry = onRetry,
                onBackToWallet = onBackToWallet
            )
        }
    }
}

@Composable
private fun SuccessContent(
    txHash: String?,
    amount: BigDecimal?,
    recipientAddress: String?,
    symbol: String,
    onViewTransaction: (String) -> Unit,
    onBackToWallet: () -> Unit
) {
    val scale by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.15f))
        
        // 成功图标
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF22C55E).copy(alpha = 0.3f),
                            Color(0xFF22C55E).copy(alpha = 0.1f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF22C55E)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "成功",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 状态标题
        Text(
            text = "转账成功",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "您的转账已成功提交到区块链网络",
            color = Color(0xFF9CA3AF),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // 交易详情卡片
        TransactionDetailsCard(
            txHash = txHash,
            amount = amount,
            recipientAddress = recipientAddress,
            symbol = symbol
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // 操作按钮
        SuccessActionButtons(
            txHash = txHash,
            onViewTransaction = onViewTransaction,
            onBackToWallet = onBackToWallet
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun FailureContent(
    errorMessage: String?,
    onRetry: () -> Unit,
    onBackToWallet: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.15f))
        
        // 失败图标
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color(0xFFEF4444).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEF4444)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "失败",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 状态标题
        Text(
            text = "转账失败",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = errorMessage ?: "交易未能成功提交，请稍后重试",
            color = Color(0xFF9CA3AF),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // 操作按钮
        FailureActionButtons(
            onRetry = onRetry,
            onBackToWallet = onBackToWallet
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun TransactionDetailsCard(
    txHash: String?,
    amount: BigDecimal?,
    recipientAddress: String?,
    symbol: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1F2937))
            .padding(20.dp)
    ) {
        // 金额
        amount?.let {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "-$it",
                    color = Color(0xFFEF4444),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = symbol,
                    color = Color(0xFF9CA3AF),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Bottom)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Divider(color = Color(0xFF374151), thickness = 0.5.dp)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 收款地址
        recipientAddress?.let { address ->
            DetailItem(
                label = "收款地址",
                value = address.take(8) + "..." + address.takeLast(8),
                icon = Icons.Outlined.AccountCircle
            )
            
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        // 交易哈希
        txHash?.let { hash ->
            DetailItem(
                label = "交易哈希",
                value = hash.take(12) + "..." + hash.takeLast(8),
                icon = Icons.Outlined.Receipt,
                showCopy = true
            )
            
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        // 网络
        DetailItem(
            label = "网络",
            value = "TRON (TRC20)",
            icon = Icons.Outlined.NetworkCheck
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // 状态
        DetailItem(
            label = "状态",
            value = "已确认",
            valueColor = Color(0xFF22C55E),
            icon = Icons.Outlined.CheckCircle
        )
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String,
    valueColor: Color = Color.White,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    showCopy: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF6B7280),
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = label,
            color = Color(0xFF9CA3AF),
            fontSize = 14.sp,
            modifier = Modifier.width(80.dp)
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = value,
            color = valueColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        
        if (showCopy) {
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { /* 复制哈希 */ },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ContentCopy,
                    contentDescription = "复制",
                    tint = Color(0xFF1D4ED8),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun SuccessActionButtons(
    txHash: String?,
    onViewTransaction: (String) -> Unit,
    onBackToWallet: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 查看交易按钮
        txHash?.let { hash ->
            OutlinedButton(
                onClick = { onViewTransaction(hash) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = Color(0xFF374151)
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "查看交易",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        // 返回钱包按钮
        Button(
            onClick = onBackToWallet,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1D4ED8)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Wallet,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "返回钱包",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun FailureActionButtons(
    onRetry: () -> Unit,
    onBackToWallet: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 重试按钮
        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1D4ED8)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "重新发送",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
        
        // 返回钱包按钮
        OutlinedButton(
            onClick = onBackToWallet,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = Color(0xFF374151)
            )
        ) {
            Text(
                text = "返回钱包",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1020)
@Composable
fun SendResultPageSuccessPreview() {
    CryptoVPNTheme {
        SendResultPage(
            isSuccess = true,
            txHash = "0x742d35Cc6634C0532925a3b844Bc9e7595f0bEbD",
            amount = BigDecimal("100.00"),
            recipientAddress = "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t",
            symbol = "USDT"
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1020)
@Composable
fun SendResultPageFailurePreview() {
    CryptoVPNTheme {
        SendResultPage(
            isSuccess = false,
            errorMessage = "网络连接超时，请检查网络后重试"
        )
    }
}
