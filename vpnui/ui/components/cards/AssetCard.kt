package com.cryptovpn.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cryptovpn.ui.theme.*

/**
 * 资产卡片组件
 * 
 * 用于展示用户资产信息
 * 
 * @param title 标题
 * @param amount 金额
 * @param currency 币种
 * @param icon 图标
 * @param iconBackgroundColor 图标背景色
 * @param onClick 点击回调
 * @param modifier 修饰符
 */
@Composable
fun AssetCard(
    title: String,
    amount: String,
    currency: String,
    icon: ImageVector,
    iconBackgroundColor: Color = Primary.copy(alpha = 0.15f),
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    BaseCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = iconBackgroundColor,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = AppTypography.Body,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = amount,
                        style = AppTypography.NumberMedium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = currency,
                        style = AppTypography.BodySmall,
                        color = TextSecondary
                    )
                }
            }
            
            // Arrow
            if (onClick != null) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TextTertiary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * 总览资产卡片（带渐变背景）
 */
@Composable
fun TotalAssetCard(
    totalAmount: String,
    currency: String,
    onDeposit: () -> Unit,
    onWithdraw: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(AppShape.CardLarge)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Primary.copy(alpha = 0.8f),
                        PrimaryHover.copy(alpha = 0.9f)
                    )
                )
            )
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = "总资产",
                style = AppTypography.Body,
                color = TextPrimary.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = totalAmount,
                    style = AppTypography.NumberLarge,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = currency,
                    style = AppTypography.H4,
                    color = TextPrimary.copy(alpha = 0.8f)
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable(onClick = onDeposit),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "充值",
                        style = AppTypography.LabelLarge,
                        color = TextPrimary
                    )
                }
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable(onClick = onWithdraw),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "提现",
                        style = AppTypography.LabelLarge,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

/**
 * 佣金资产卡片
 */
@Composable
fun CommissionAssetCard(
    totalCommission: String,
    todayCommission: String,
    referralCount: Int,
    onWithdraw: () -> Unit,
    modifier: Modifier = Modifier
) {
    BaseCard(
        modifier = modifier,
        backgroundColor = BackgroundDeepest
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "累计佣金",
                        style = AppTypography.Body,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = totalCommission,
                        style = AppTypography.NumberMedium,
                        color = Success
                    )
                }
                
                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Success.copy(alpha = 0.15f))
                        .clickable(onClick = onWithdraw)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "提现",
                        style = AppTypography.LabelMedium,
                        color = Success
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BackgroundTertiary)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                AssetStatItem(
                    label = "今日佣金",
                    value = todayCommission
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(32.dp)
                        .background(BackgroundTertiary)
                )
                AssetStatItem(
                    label = "邀请人数",
                    value = "$referralCount"
                )
            }
        }
    }
}

/**
 * 资产统计项
 */
@Composable
private fun AssetStatItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = AppTypography.NumberSmall,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = AppTypography.BodySmall,
            color = TextSecondary
        )
    }
}

@Preview(name = "Asset Card")
@Composable
fun AssetCardPreview() {
    CryptoVPNTheme {
        Column(
            modifier = Modifier
                .background(BackgroundPrimary)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Total asset card
            TotalAssetCard(
                totalAmount = "1,234.56",
                currency = "USDT",
                onDeposit = {},
                onWithdraw = {}
            )
            
            // Asset card
            AssetCard(
                title = "可用余额",
                amount = "500.00",
                currency = "USDT",
                icon = androidx.compose.material.icons.Icons.Default.AccountBalanceWallet,
                onClick = {}
            )
            
            // Commission card
            CommissionAssetCard(
                totalCommission = "$256.80",
                todayCommission = "$12.50",
                referralCount = 15,
                onWithdraw = {}
            )
        }
    }
}
