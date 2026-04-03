package com.cryptovpn.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cryptovpn.ui.components.buttons.ButtonSize
import com.cryptovpn.ui.components.buttons.PrimaryButton
import com.cryptovpn.ui.components.buttons.SecondaryButton
import com.cryptovpn.ui.theme.*

/**
 * 订阅状态卡片
 * 
 * 展示当前订阅状态
 * 
 * @param planName 套餐名称
 * @param expiryDate 到期日期
 * @param daysRemaining 剩余天数
 * @param totalDays 总天数
 * @param isActive 是否活跃
 * @param onRenew 续费回调
 * @param onUpgrade 升级回调
 * @param modifier 修饰符
 */
@Composable
fun SubscriptionCard(
    planName: String,
    expiryDate: String,
    daysRemaining: Int,
    totalDays: Int,
    isActive: Boolean = true,
    onRenew: () -> Unit,
    onUpgrade: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val progress = if (totalDays > 0) daysRemaining.toFloat() / totalDays else 0f
    val progressColor = when {
        daysRemaining <= 7 -> Error
        daysRemaining <= 30 -> Warning
        else -> Success
    }
    
    BaseCard(
        modifier = modifier,
        backgroundColor = BackgroundDeepest
    ) {
        // Status header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "当前套餐",
                    style = AppTypography.Body,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = planName,
                    style = AppTypography.H3,
                    color = TextPrimary
                )
            }
            
            // Status badge
            Box(
                modifier = Modifier
                    .background(
                        color = if (isActive) Success.copy(alpha = 0.15f) else ErrorLight,
                        shape = AppShape.TagPill
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isActive) "有效" else "已过期",
                    style = AppTypography.LabelMedium,
                    color = if (isActive) Success else Error
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Progress bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "剩余 $daysRemaining 天",
                style = AppTypography.BodySmall,
                color = TextSecondary
            )
            Text(
                text = "到期 $expiryDate",
                style = AppTypography.BodySmall,
                color = TextTertiary
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = progressColor,
            trackColor = BackgroundTertiary
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PrimaryButton(
                text = "立即续费",
                onClick = onRenew,
                modifier = Modifier.weight(1f),
                size = ButtonSize.MEDIUM
            )
            SecondaryButton(
                text = "升级套餐",
                onClick = onUpgrade,
                modifier = Modifier.weight(1f),
                size = ButtonSize.MEDIUM
            )
        }
    }
}

/**
 * 过期订阅卡片
 */
@Composable
fun ExpiredSubscriptionCard(
    planName: String,
    expiryDate: String,
    onRenew: () -> Unit,
    modifier: Modifier = Modifier
) {
    BaseCard(
        modifier = modifier,
        backgroundColor = BackgroundDeepest,
        borderColor = Error.copy(alpha = 0.3f),
        borderWidth = 1.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = planName,
                    style = AppTypography.H4,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "已于 $expiryDate 过期",
                    style = AppTypography.BodySmall,
                    color = Error
                )
            }
            
            PrimaryButton(
                text = "续费",
                onClick = onRenew,
                size = ButtonSize.SMALL,
                modifier = Modifier.width(80.dp)
            )
        }
    }
}

@Preview(name = "Subscription Card")
@Composable
fun SubscriptionCardPreview() {
    CryptoVPNTheme {
        Column(
            modifier = Modifier
                .background(BackgroundPrimary)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Active subscription (plenty of days)
            SubscriptionCard(
                planName = "年度会员",
                expiryDate = "2025-12-01",
                daysRemaining = 365,
                totalDays = 365,
                isActive = true,
                onRenew = {},
                onUpgrade = {}
            )
            
            // Active subscription (few days left)
            SubscriptionCard(
                planName = "月度会员",
                expiryDate = "2024-12-10",
                daysRemaining = 7,
                totalDays = 30,
                isActive = true,
                onRenew = {}
            )
            
            // Expired subscription
            ExpiredSubscriptionCard(
                planName = "季度会员",
                expiryDate = "2024-11-01",
                onRenew = {}
            )
        }
    }
}
