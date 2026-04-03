package com.cryptovpn.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cryptovpn.ui.components.buttons.ButtonSize
import com.cryptovpn.ui.components.buttons.PrimaryButton
import com.cryptovpn.ui.theme.*

/**
 * 套餐卡片组件
 * 
 * 用于展示VPN套餐信息
 * 
 * @param name 套餐名称
 * @param price 价格
 * @param originalPrice 原价（可选）
 * @param duration 时长
 * @param features 功能列表
 * @param isRecommended 是否推荐
 * @param isSelected 是否选中
 * @param onClick 点击回调
 * @param onSubscribe 订阅回调
 * @param modifier 修饰符
 */
@Composable
fun PlanCard(
    name: String,
    price: String,
    originalPrice: String? = null,
    duration: String,
    features: List<String>,
    isRecommended: Boolean = false,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    onSubscribe: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = when {
        isRecommended -> Primary
        isSelected -> Primary
        else -> BorderPrimary
    }
    
    val borderWidth = if (isRecommended || isSelected) 2.dp else 1.dp
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(AppShape.Card)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = AppShape.Card
            )
            .background(
                color = BackgroundSecondary,
                shape = AppShape.Card
            )
            .clickable(onClick = onClick)
    ) {
        // Header with gradient if recommended
        if (isRecommended) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Primary, PrimaryHover)
                        ),
                        shape = RoundedCornerShape(
                            topStart = RadiusLarge,
                            topEnd = RadiusLarge
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "推荐",
                    style = AppTypography.LabelMedium,
                    color = TextPrimary
                )
            }
        }
        
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Plan name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    style = AppTypography.H4,
                    color = TextPrimary
                )
                
                if (isSelected) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.CheckCircle,
                        contentDescription = "已选择",
                        tint = Success,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Price
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = price,
                    style = AppTypography.NumberLarge,
                    color = Primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "/$duration",
                    style = AppTypography.Body,
                    color = TextSecondary
                )
                
                if (originalPrice != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = originalPrice,
                        style = AppTypography.BodySmall,
                        color = TextTertiary,
                        textDecoration = TextDecoration.LineThrough
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BackgroundTertiary)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Features
            features.forEach { feature ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Check,
                        contentDescription = null,
                        tint = Success,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = feature,
                        style = AppTypography.Body,
                        color = TextSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Subscribe button
            PrimaryButton(
                text = if (isSelected) "已选择" else "立即订阅",
                onClick = onSubscribe,
                size = ButtonSize.MEDIUM,
                enabled = !isSelected
            )
        }
    }
}

/**
 * 紧凑版套餐卡片（用于列表）
 */
@Composable
fun PlanCardCompact(
    name: String,
    price: String,
    duration: String,
    isRecommended: Boolean = false,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(AppShape.Card)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Primary else BorderPrimary,
                shape = AppShape.Card
            )
            .background(
                color = BackgroundSecondary,
                shape = AppShape.Card
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    style = AppTypography.H4,
                    color = TextPrimary
                )
                
                if (isRecommended) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "推荐",
                        style = AppTypography.LabelSmall,
                        color = Primary,
                        modifier = Modifier
                            .background(
                                color = Primary.copy(alpha = 0.15f),
                                shape = AppShape.Tag
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = duration,
                style = AppTypography.BodySmall,
                color = TextSecondary
            )
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = price,
                style = AppTypography.NumberMedium,
                color = Primary
            )
            
            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.CheckCircle,
                    contentDescription = "已选择",
                    tint = Success,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(name = "Plan Card")
@Composable
fun PlanCardPreview() {
    CryptoVPNTheme {
        Column(
            modifier = Modifier
                .background(BackgroundPrimary)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Recommended plan
            PlanCard(
                name = "年度会员",
                price = "$59.99",
                originalPrice = "$99.99",
                duration = "年",
                features = listOf(
                    "无限流量",
                    "全球50+节点",
                    "5台设备同时在线",
                    "24/7客服支持"
                ),
                isRecommended = true,
                isSelected = false,
                onClick = {},
                onSubscribe = {}
            )
            
            // Selected plan
            PlanCard(
                name = "月度会员",
                price = "$9.99",
                duration = "月",
                features = listOf(
                    "无限流量",
                    "全球50+节点",
                    "3台设备同时在线"
                ),
                isRecommended = false,
                isSelected = true,
                onClick = {},
                onSubscribe = {}
            )
            
            // Compact plan
            PlanCardCompact(
                name = "季度会员",
                price = "$19.99",
                duration = "3个月",
                isRecommended = true,
                isSelected = false,
                onClick = {}
            )
        }
    }
}
