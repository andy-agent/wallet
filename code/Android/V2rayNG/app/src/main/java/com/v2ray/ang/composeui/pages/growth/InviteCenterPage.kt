package com.v2ray.ang.composeui.pages.growth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * 邀请中心页状态
 */
sealed class InviteCenterState {
    object Idle : InviteCenterState()
    object Loading : InviteCenterState()
    data class Loaded(
        val inviteCode: String,
        val inviteLink: String,
        val totalInvited: Int,
        val totalCommission: String,
        val pendingCommission: String,
        val commissionRate: String
    ) : InviteCenterState()
    data class Error(val message: String) : InviteCenterState()
}

/**
 * 邀请中心页ViewModel
 */
class InviteCenterViewModel : ViewModel() {
    private val _state = MutableStateFlow<InviteCenterState>(InviteCenterState.Idle)
    val state: StateFlow<InviteCenterState> = _state

    init {
        loadInviteData()
    }

    private fun loadInviteData() {
        _state.value = InviteCenterState.Loaded(
            inviteCode = "CRYPTO2024",
            inviteLink = "https://cryptovpn.app/invite/CRYPTO2024",
            totalInvited = 15,
            totalCommission = "$156.80",
            pendingCommission = "$23.50",
            commissionRate = "20%"
        )
    }
}

/**
 * 邀请中心页
 * 显示邀请码、邀请链接和邀请统计数据
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteCenterPage(
    viewModel: InviteCenterViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToCommission: () -> Unit = {},
    onNavigateToWithdraw: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("邀请中心") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is InviteCenterState.Loaded -> {
                    val loadedState = state as InviteCenterState.Loaded
                    InviteCenterContent(
                        state = loadedState,
                        onCopyCode = {
                            clipboardManager.setText(AnnotatedString(loadedState.inviteCode))
                            snackbarHostState.currentSnackbarData?.dismiss()
                        },
                        onCopyLink = {
                            clipboardManager.setText(AnnotatedString(loadedState.inviteLink))
                            snackbarHostState.currentSnackbarData?.dismiss()
                        },
                        onShare = { /* 分享 */ },
                        onNavigateToCommission = onNavigateToCommission,
                        onNavigateToWithdraw = onNavigateToWithdraw
                    )
                }
                is InviteCenterState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is InviteCenterState.Error -> {
                    ErrorView(message = (state as InviteCenterState.Error).message)
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun InviteCenterContent(
    state: InviteCenterState.Loaded,
    onCopyCode: () -> Unit,
    onCopyLink: () -> Unit,
    onShare: () -> Unit,
    onNavigateToCommission: () -> Unit,
    onNavigateToWithdraw: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 统计卡片
        StatsCard(
            totalInvited = state.totalInvited,
            totalCommission = state.totalCommission,
            pendingCommission = state.pendingCommission,
            commissionRate = state.commissionRate,
            onNavigateToCommission = onNavigateToCommission,
            onNavigateToWithdraw = onNavigateToWithdraw
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 邀请码卡片
        InviteCodeCard(
            inviteCode = state.inviteCode,
            onCopyCode = onCopyCode
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 邀请链接卡片
        InviteLinkCard(
            inviteLink = state.inviteLink,
            onCopyLink = onCopyLink,
            onShare = onShare
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 邀请规则
        InviteRulesCard()

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun StatsCard(
    totalInvited: Int,
    totalCommission: String,
    pendingCommission: String,
    commissionRate: String,
    onNavigateToCommission: () -> Unit,
    onNavigateToWithdraw: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // 标题
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "我的邀请收益",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Surface(
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "返佣比例 $commissionRate",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 统计数据
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = totalInvited.toString(),
                    label = "已邀请",
                    onClick = onNavigateToCommission
                )
                StatItem(
                    value = totalCommission,
                    label = "累计收益",
                    onClick = onNavigateToCommission
                )
                StatItem(
                    value = pendingCommission,
                    label = "待提现",
                    onClick = onNavigateToWithdraw
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun InviteCodeCard(
    inviteCode: String,
    onCopyCode: () -> Unit
) {
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
            Text(
                text = "我的邀请码",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 邀请码显示
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = inviteCode,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onCopyCode) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InviteLinkCard(
    inviteLink: String,
    onCopyLink: () -> Unit,
    onShare: () -> Unit
) {
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
            Text(
                text = "邀请链接",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 链接显示
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = inviteLink,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                        maxLines = 1
                    )
                    IconButton(onClick = onCopyLink) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 分享按钮
            Button(
                onClick = onShare,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "分享邀请链接",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun InviteRulesCard() {
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
            Text(
                text = "邀请规则",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            val rules = listOf(
                "邀请好友注册并完成首单，您可获得20%佣金",
                "佣金将在好友订单完成后自动计入您的账户",
                "佣金可随时提现至您的钱包",
                "邀请人数无上限，多邀多得"
            )

            rules.forEachIndexed { index, rule ->
                RuleItem(number = index + 1, text = rule)
                if (index < rules.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun RuleItem(number: Int, text: String) {
    Row {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 20.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ErrorView(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "加载失败",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InviteCenterPagePreview() {
    MaterialTheme {
        InviteCenterPage()
    }
}
