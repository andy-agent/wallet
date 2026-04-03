package com.cryptovpn.ui.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cryptovpn.ui.components.buttons.IconButtonSize
import com.cryptovpn.ui.components.buttons.IconButtonVariant
import com.cryptovpn.ui.components.buttons.IconButton
import com.cryptovpn.ui.theme.*

/**
 * 顶部应用栏组件
 * 
 * @param title 标题
 * @param onBackClick 返回点击回调
 * @param modifier 修饰符
 * @param actions 右侧操作按钮
 * @param showBackButton 是否显示返回按钮
 * @param backgroundColor 背景色
 */
@Composable
fun TopAppBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    showBackButton: Boolean = true,
    backgroundColor: Color = BackgroundPrimary
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Back button
        if (showBackButton && onBackClick != null) {
            Box(
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                IconButton(
                    icon = Icons.Default.ArrowBack,
                    onClick = onBackClick,
                    size = IconButtonSize.MEDIUM,
                    variant = IconButtonVariant.GHOST
                )
            }
        }
        
        // Title
        Text(
            text = title,
            style = AppTypography.H4,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.align(Alignment.Center)
        )
        
        // Actions
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            actions()
        }
    }
}

/**
 * 居中大标题应用栏
 */
@Composable
fun CenterAlignedTopAppBar(
    title: String,
    subtitle: String? = null,
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BackgroundPrimary)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBackClick != null) {
                IconButton(
                    icon = Icons.Default.ArrowBack,
                    onClick = onBackClick,
                    size = IconButtonSize.MEDIUM,
                    variant = IconButtonVariant.GHOST
                )
            } else {
                Spacer(modifier = Modifier.size(44.dp))
            }
            
            Row {
                actions()
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = title,
            style = AppTypography.H2,
            color = TextPrimary
        )
        
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = AppTypography.Body,
                color = TextSecondary
            )
        }
    }
}

/**
 * 底部导航栏项数据
 */
data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector? = null
)

/**
 * 底部导航栏组件
 * 
 * @param items 导航项列表
 * @param selectedRoute 当前选中路由
 * @param onItemSelected 选中回调
 * @param modifier 修饰符
 */
@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    selectedRoute: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = BackgroundSecondary,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = item.route == selectedRoute
                BottomNavItem(
                    item = item,
                    isSelected = isSelected,
                    onClick = { onItemSelected(item.route) }
                )
            }
        }
    }
}

/**
 * 底部导航项
 */
@Composable
private fun BottomNavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (isSelected && item.selectedIcon != null) {
                item.selectedIcon
            } else {
                item.icon
            },
            contentDescription = item.title,
            modifier = Modifier.size(24.dp),
            tint = if (isSelected) Primary else TextTertiary
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = item.title,
            style = AppTypography.LabelSmall,
            color = if (isSelected) Primary else TextTertiary
        )
    }
}

/**
 * 返回按钮（独立组件）
 */
@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        icon = Icons.Default.ArrowBack,
        onClick = onClick,
        modifier = modifier,
        size = IconButtonSize.MEDIUM,
        variant = IconButtonVariant.GHOST
    )
}

@Preview(name = "App Bars")
@Composable
fun AppBarsPreview() {
    CryptoVPNTheme {
        Column(
            modifier = Modifier
                .background(BackgroundPrimary)
        ) {
            // Standard top app bar
            TopAppBar(
                title = "设置",
                onBackClick = {}
            )
            
            // Top app bar with actions
            TopAppBar(
                title = "我的订单",
                onBackClick = {},
                actions = {
                    IconButton(
                        icon = androidx.compose.material.icons.Icons.Default.MoreVert,
                        onClick = {},
                        size = IconButtonSize.MEDIUM,
                        variant = IconButtonVariant.GHOST
                    )
                }
            )
            
            // Center aligned top app bar
            CenterAlignedTopAppBar(
                title = "我的资产",
                subtitle = "管理您的加密货币资产",
                onBackClick = {}
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Bottom navigation bar
            BottomNavigationBar(
                items = listOf(
                    BottomNavItem(
                        route = "home",
                        title = "首页",
                        icon = androidx.compose.material.icons.Icons.Default.Home,
                        selectedIcon = androidx.compose.material.icons.Icons.Default.Home
                    ),
                    BottomNavItem(
                        route = "servers",
                        title = "节点",
                        icon = androidx.compose.material.icons.Icons.Default.Public
                    ),
                    BottomNavItem(
                        route = "wallet",
                        title = "钱包",
                        icon = androidx.compose.material.icons.Icons.Default.AccountBalanceWallet
                    ),
                    BottomNavItem(
                        route = "profile",
                        title = "我的",
                        icon = androidx.compose.material.icons.Icons.Default.Person
                    )
                ),
                selectedRoute = "home",
                onItemSelected = {}
            )
        }
    }
}
