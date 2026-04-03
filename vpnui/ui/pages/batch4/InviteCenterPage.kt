package com.cryptovpn.ui.pages.growth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cryptovpn.ui.theme.CryptoVPNTheme
import com.cryptovpn.ui.components.CommonTopAppBar
import com.cryptovpn.ui.components.LoadingIndicator
import java.math.BigDecimal

/**
 * 邀请中心页状态
 */
sealed class InviteCenterState {
    data object Loading : InviteCenterState()
    data class Loaded(
        val inviteCode: String,
        val inviteLink: String,
        val level1Count: Int,
        val level2Count: Int,
        val level1Earnings: BigDecimal,
        val level2Earnings: BigDecimal,
        val totalEarnings: BigDecimal,
        val withdrawableBalance: BigDecimal,
        val isWithdrawDisabled: Boolean = false,
        val isBindingLocked: Boolean = false,
        val minWithdrawAmount: BigDecimal = BigDecimal("10.00"),
        val commissionRates: CommissionRates
    ) : InviteCenterState()
    data class WithdrawDisabled(
        val reason: String,
        val inviteCode: String
    ) : InviteCenterState()
    data class BindingLocked(
        val message: String,
        val unlockTime: Long? = null
    ) : InviteCenterState()
    data class Error(
        val message: String,
        val canRetry: Boolean = true
    ) : InviteCenterState()
}

/**
 * 佣金费率
 */
data class CommissionRates(
    val level1Rate: String,
    val level2Rate: String
)

/**
 * 邀请中心页
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteCenterPage(
    onNavigateBack: () -> Unit = {},
    onNavigateToLedger: () -> Unit = {},
    onNavigateToWithdraw: () -> Unit = {},
    viewModel: InviteCenterViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    var showCopiedSnackbar by remember { mutableStateOf(false) }
    var showRulesDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = "邀请中心",
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(onClick = onNavigateToLedger) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "佣金记录",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        snackbarHost = {
            if (showCopiedSnackbar) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = Color(0xFF22C55E),
                    contentColor = Color.White
                ) {
                    Text("已复制到剪贴板")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0B1020))
                .padding(paddingValues)
        ) {
            when (val currentState = state) {
                is InviteCenterState.Loading -> {
                    LoadingIndicator()
                }
                is InviteCenterState.Loaded -> {
                    InviteCenterContent(
                        state = currentState,
                        onCopyCode = { code ->
                            clipboardManager.setText(AnnotatedString(code))
                            showCopiedSnackbar = true
                        },
                        onCopyLink = { link ->
                            clipboardManager.setText(AnnotatedString(link))
                            showCopiedSnackbar = true
                        },
                        onShare = { viewModel.shareInviteLink() },
                        onWithdrawClick = onNavigateToWithdraw,
                        onViewRules = { showRulesDialog = true }
                    )
                }
                is InviteCenterState.WithdrawDisabled -> {
                    WithdrawDisabledView(
                        reason = currentState.reason,
                        inviteCode = currentState.inviteCode,
                        onCopyCode = { code ->
                            clipboardManager.setText(AnnotatedString(code))
                            showCopiedSnackbar = true
                        },
                        onShare = { viewModel.shareInviteLink() }
                    )
                }
                is InviteCenterState.BindingLocked -> {
                    BindingLockedView(
                        message = currentState.message,
                        unlockTime = currentState.unlockTime
                    )
                }
                is InviteCenterState.Error -> {
                    ErrorRetryView(
                        message = currentState.message,
                        canRetry = currentState.canRetry,
                        onRetry = { viewModel.retry() }
                    )
                }
            }
        }
    }

    // 分佣规则对话框
    if (showRulesDialog && state is InviteCenterState.Loaded) {
        val loadedState = state as InviteCenterState.Loaded
        CommissionRulesDialog(
            rates = loadedState.commissionRates,
            onDismiss = { showRulesDialog = false }
        )
    }
}

@Composable
private fun InviteCenterContent(
    state: InviteCenterState.Loaded,
    onCopyCode: (String) -> Unit,
    onCopyLink: (String) -> Unit,
    onShare: () -> Unit,
    onWithdrawClick: () -> Unit,
    onViewRules: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }
        
        // 邀请码卡片
        item {
            InviteCodeCard(
                inviteCode = state.inviteCode,
                inviteLink = state.inviteLink,
                onCopyCode = onCopyCode,
                onCopyLink = onCopyLink,
                onShare = onShare
            )
        }
        
        // 统计网格
        item {
            StatisticsGrid(
                level1Count = state.level1Count,
                level2Count = state.level2Count,
                level1Earnings = state.level1Earnings,
                level2Earnings = state.level2Earnings
            )
        }
        
        // 余额卡片
        item {
            BalanceCard(
                totalEarnings = state.totalEarnings,
                withdrawableBalance = state.withdrawableBalance,
                minWithdrawAmount = state.minWithdrawAmount,
                isWithdrawDisabled = state.isWithdrawDisabled,
                onWithdrawClick = onWithdrawClick
            )
        }
        
        // 提示横幅
        item {
            TipsBanner()
        }
        
        // 操作按钮
        item {
            ActionButtons(
                onShare = onShare,
                onViewRules = onViewRules
            )
        }
        
        // 分佣规则
        item {
            CommissionRulesSection(
                rates = state.commissionRates,
                onViewDetails = onViewRules
            )
        }
        
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun InviteCodeCard(
    inviteCode: String,
    inviteLink: String,
    onCopyCode: (String) -> Unit,
    onCopyLink: (String) -> Unit,
    onShare: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1D4ED8),
                        Color(0xFF3B82F6)
                    )
                )
            )
            .padding(20.dp)
    ) {
        Text(
            text = "我的邀请码",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = inviteCode,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            IconButton(
                onClick = { onCopyCode(inviteCode) },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = Icons.Outlined.ContentCopy,
                    contentDescription = "复制",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Divider(color = Color.White.copy(alpha = 0.2f), thickness = 0.5.dp)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "邀请链接",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = inviteLink.take(35) + "...",
                    color = Color.White,
                    fontSize = 13.sp
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { onCopyLink(inviteLink) },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = "复制链接",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                IconButton(
                    onClick = onShare,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "分享",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatisticsGrid(
    level1Count: Int,
    level2Count: Int,
    level1Earnings: BigDecimal,
    level2Earnings: BigDecimal
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "一级邀请",
                count = level1Count,
                earnings = level1Earnings,
                icon = Icons.Outlined.People,
                iconColor = Color(0xFF22C55E),
                modifier = Modifier.weight(1f)
            )
            
            StatCard(
                title = "二级邀请",
                count = level2Count,
                earnings = level2Earnings,
                icon = Icons.Outlined.Groups,
                iconColor = Color(0xFF3B82F6),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    count: Int,
    earnings: BigDecimal,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1F2937))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = title,
                color = Color(0xFF9CA3AF),
                fontSize = 13.sp
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "$count 人",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "收益 $$earnings",
            color = Color(0xFF22C55E),
            fontSize = 13.sp
        )
    }
}

@Composable
private fun BalanceCard(
    totalEarnings: BigDecimal,
    withdrawableBalance: BigDecimal,
    minWithdrawAmount: BigDecimal,
    isWithdrawDisabled: Boolean,
    onWithdrawClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF111827),
                        Color(0xFF1F2937)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = Color(0xFF374151),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "累计收益",
                    color = Color(0xFF9CA3AF),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$$totalEarnings",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "可提现",
                    color = Color(0xFF9CA3AF),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$$withdrawableBalance",
                    color = Color(0xFF22C55E),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Divider(color = Color(0xFF374151), thickness = 0.5.dp)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "最低提现: $$minWithdrawAmount",
                color = Color(0xFF6B7280),
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )
            
            Button(
                onClick = onWithdrawClick,
                enabled = !isWithdrawDisabled && withdrawableBalance >= minWithdrawAmount,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF22C55E),
                    disabledContainerColor = Color(0xFF22C55E).copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = "立即提现",
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun TipsBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFEF3C7).copy(alpha = 0.1f))
            .border(
                width = 1.dp,
                color = Color(0xFFF59E0B).copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Lightbulb,
            contentDescription = null,
            tint = Color(0xFFF59E0B),
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = "邀请小贴士",
                color = Color(0xFFF59E0B),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "分享您的邀请码给朋友，当他们充值时您将获得佣金奖励。一级邀请可获得更高比例的佣金！",
                color = Color(0xFFF59E0B).copy(alpha = 0.8f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun ActionButtons(
    onShare: () -> Unit,
    onViewRules: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onShare,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1D4ED8)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("立即分享")
        }
        
        OutlinedButton(
            onClick = onViewRules,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = Color(0xFF374151)
            )
        {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("分佣规则")
        }
    }
}

@Composable
private fun CommissionRulesSection(
    rates: CommissionRates,
    onViewDetails: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "分佣规则",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RateCard(
                level = "一级",
                rate = rates.level1Rate,
                description = "直接邀请的好友",
                modifier = Modifier.weight(1f)
            )
            
            RateCard(
                level = "二级",
                rate = rates.level2Rate,
                description = "好友邀请的用户",
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        TextButton(
            onClick = onViewDetails,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = "查看详情",
                color = Color(0xFF1D4ED8),
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun RateCard(
    level: String,
    rate: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1F2937))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = level,
            color = Color(0xFF9CA3AF),
            fontSize = 13.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = rate,
            color = Color(0xFF22C55E),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = description,
            color = Color(0xFF6B7280),
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun WithdrawDisabledView(
    reason: String,
    inviteCode: String,
    onCopyCode: (String) -> Unit,
    onShare: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = Color(0xFFF59E0B),
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "提现功能暂时不可用",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = reason,
            color = Color(0xFF9CA3AF),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 仍然可以分享邀请码
        InviteCodeCard(
            inviteCode = inviteCode,
            inviteLink = "",
            onCopyCode = onCopyCode,
            onCopyLink = {},
            onShare = onShare
        )
    }
}

@Composable
private fun BindingLockedView(
    message: String,
    unlockTime: Long?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = Color(0xFFEF4444),
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "邀请功能已锁定",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            color = Color(0xFF9CA3AF),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        
        unlockTime?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "解锁时间: ${formatUnlockTime(it)}",
                color = Color(0xFFF59E0B),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun CommissionRulesDialog(
    rates: CommissionRates,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1F2937),
        title = {
            Text(
                text = "分佣规则详情",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column {
                Text(
                    text = "佣金计算方式",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                RuleItem(
                    icon = Icons.Outlined.LooksOne,
                    title = "一级邀请 (${rates.level1Rate})",
                    description = "您直接邀请的好友充值时，您将获得充值金额的 ${rates.level1Rate} 作为佣金。"
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                RuleItem(
                    icon = Icons.Outlined.LooksTwo,
                    title = "二级邀请 (${rates.level2Rate})",
                    description = "您的好友邀请的用户充值时，您将获得充值金额的 ${rates.level2Rate} 作为佣金。"
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Divider(color = Color(0xFF374151))
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "注意事项",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                BulletPoint("佣金将在好友充值成功后自动计入您的账户")
                BulletPoint("最低提现金额为 $10.00")
                BulletPoint("提现申请将在 1-3 个工作日内处理")
                BulletPoint("禁止通过不正当手段获取佣金，违规将被封号")
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1D4ED8)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("我知道了")
            }
        }
    )
}

@Composable
private fun RuleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF1D4ED8).copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF1D4ED8),
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                color = Color(0xFF9CA3AF),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun BulletPoint(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Color(0xFFF59E0B))
                .padding(top = 6.dp)
        )
        
        Spacer(modifier = Modifier.width(10.dp))
        
        Text(
            text = text,
            color = Color(0xFF9CA3AF),
            fontSize = 13.sp,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun ErrorRetryView(
    message: String,
    canRetry: Boolean,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = Color(0xFFEF4444),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        if (canRetry) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1D4ED8)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("重试")
            }
        }
    }
}

private fun formatUnlockTime(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val formatter = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
    return formatter.format(date)
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1020)
@Composable
fun InviteCenterPagePreview() {
    CryptoVPNTheme {
        InviteCenterContent(
            state = InviteCenterState.Loaded(
                inviteCode = "CRYPTO2024",
                inviteLink = "https://cryptovpn.app/invite/CRYPTO2024",
                level1Count = 15,
                level2Count = 42,
                level1Earnings = BigDecimal("125.50"),
                level2Earnings = BigDecimal("45.25"),
                totalEarnings = BigDecimal("170.75"),
                withdrawableBalance = BigDecimal("85.30"),
                commissionRates = CommissionRates(
                    level1Rate = "10%",
                    level2Rate = "5%"
                )
            ),
            onCopyCode = {},
            onCopyLink = {},
            onShare = {},
            onWithdrawClick = {},
            onViewRules = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1020)
@Composable
fun InviteCenterPageLoadingPreview() {
    CryptoVPNTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0B1020))
        ) {
            LoadingIndicator()
        }
    }
}
