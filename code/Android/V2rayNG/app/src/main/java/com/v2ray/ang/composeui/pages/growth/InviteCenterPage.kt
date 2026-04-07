package com.v2ray.ang.composeui.pages.growth

import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.bridge.growth.GrowthBridgeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private val GrowthBg = Color(0xFF0A101A)
private val GrowthSurface = Color(0xFF121A28)
private val GrowthSurfaceSoft = Color(0xFF1A2638)
private val GrowthPrimary = Color(0xFF00E5A8)
private val GrowthPrimarySoft = Color(0x3313F1B2)
private val GrowthText = Color(0xFFEAF0F7)
private val GrowthMuted = Color(0xFF8D9AB0)
private val GrowthDanger = Color(0xFFF45B69)

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
class InviteCenterViewModel(application: Application) : AndroidViewModel(application) {
    private val growthBridgeRepository = GrowthBridgeRepository(application)
    private val _state = MutableStateFlow<InviteCenterState>(InviteCenterState.Idle)
    val state: StateFlow<InviteCenterState> = _state

    init {
        loadInviteData()
    }

    private fun loadInviteData() {
        viewModelScope.launch {
            _state.value = InviteCenterState.Loading

            val referral = growthBridgeRepository.getReferralOverview()
            val summary = growthBridgeRepository.getCommissionSummary()
            if (referral.isFailure) {
                _state.value = InviteCenterState.Error(referral.exceptionOrNull()?.message ?: "加载邀请信息失败")
                return@launch
            }
            if (summary.isFailure) {
                _state.value = InviteCenterState.Error(summary.exceptionOrNull()?.message ?: "加载佣金信息失败")
                return@launch
            }

            val referralData = referral.getOrNull() ?: run {
                _state.value = InviteCenterState.Error("邀请数据为空")
                return@launch
            }
            val summaryData = summary.getOrNull() ?: run {
                _state.value = InviteCenterState.Error("佣金数据为空")
                return@launch
            }

            val inviteCode = referralData.referralCode
            _state.value = InviteCenterState.Loaded(
                inviteCode = inviteCode,
                inviteLink = "https://api.residential-agent.com/invite/$inviteCode",
                totalInvited = referralData.level1InviteCount + referralData.level2InviteCount,
                totalCommission = "$${summaryData.withdrawnTotal}",
                pendingCommission = "$${summaryData.availableAmount}",
                commissionRate = "L1 20% / L2 10%"
            )
        }
    }

    fun bindReferralCode(code: String) {
        if (code.isBlank()) return
        viewModelScope.launch {
            growthBridgeRepository.bindReferralCode(code)
            loadInviteData()
        }
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
        containerColor = GrowthBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Discover Growth",
                        color = GrowthText,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = GrowthText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GrowthBg,
                    titleContentColor = GrowthText,
                    navigationIconContentColor = GrowthText
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GrowthBg)
                .padding(paddingValues)
        ) {
            when (val current = state) {
                is InviteCenterState.Loaded -> {
                    InviteCenterContent(
                        state = current,
                        onCopyCode = {
                            clipboardManager.setText(AnnotatedString(current.inviteCode))
                        },
                        onCopyLink = {
                            clipboardManager.setText(AnnotatedString(current.inviteLink))
                        },
                        onShare = { },
                        onNavigateToCommission = onNavigateToCommission,
                        onNavigateToWithdraw = onNavigateToWithdraw
                    )
                }

                is InviteCenterState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = GrowthPrimary)
                    }
                }

                is InviteCenterState.Error -> {
                    InviteErrorView(message = current.message)
                }

                else -> Unit
            }
        }
    }

    LaunchedEffect(state) {
        when (state) {
            is InviteCenterState.Loaded -> snackbarHostState.currentSnackbarData?.dismiss()
            else -> Unit
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
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        GrowthHeroCard(
            totalInvited = state.totalInvited,
            totalCommission = state.totalCommission,
            pendingCommission = state.pendingCommission,
            commissionRate = state.commissionRate,
            onNavigateToCommission = onNavigateToCommission,
            onNavigateToWithdraw = onNavigateToWithdraw
        )

        Spacer(modifier = Modifier.height(14.dp))

        InviteCodeCard(inviteCode = state.inviteCode, onCopyCode = onCopyCode)

        Spacer(modifier = Modifier.height(12.dp))

        InviteLinkCard(
            inviteLink = state.inviteLink,
            onCopyLink = onCopyLink,
            onShare = onShare
        )

        Spacer(modifier = Modifier.height(12.dp))

        InviteRulesCard()

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun GrowthHeroCard(
    totalInvited: Int,
    totalCommission: String,
    pendingCommission: String,
    commissionRate: String,
    onNavigateToCommission: () -> Unit,
    onNavigateToWithdraw: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = GrowthSurface),
        border = BorderStroke(1.dp, GrowthPrimarySoft)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0x3322F5C6), Color.Transparent, Color(0x26284259))
                    )
                )
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Growth Hub",
                    color = GrowthText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = GrowthPrimarySoft,
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        text = commissionRate,
                        color = GrowthPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = pendingCommission,
                color = GrowthText,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "可提现收益",
                color = GrowthMuted,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatPill(
                    modifier = Modifier.weight(1f),
                    label = "已邀请",
                    value = totalInvited.toString(),
                    onClick = onNavigateToCommission
                )
                StatPill(
                    modifier = Modifier.weight(1f),
                    label = "累计收益",
                    value = totalCommission,
                    onClick = onNavigateToCommission
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionOutlineButton(
                    modifier = Modifier.weight(1f),
                    label = "佣金明细",
                    icon = Icons.Default.TrendingUp,
                    onClick = onNavigateToCommission
                )
                Button(
                    onClick = onNavigateToWithdraw,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GrowthPrimary,
                        contentColor = Color(0xFF072117)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "立即提现",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun StatPill(
    modifier: Modifier,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(GrowthSurfaceSoft)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(text = value, color = GrowthText, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(text = label, color = GrowthMuted, fontSize = 11.sp)
    }
}

@Composable
private fun ActionOutlineButton(
    modifier: Modifier,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, GrowthPrimarySoft, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = GrowthPrimary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.size(6.dp))
        Text(text = label, color = GrowthPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun InviteCodeCard(
    inviteCode: String,
    onCopyCode: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = GrowthSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "邀请码", color = GrowthMuted, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(GrowthSurfaceSoft)
                    .padding(start = 14.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = inviteCode,
                    color = GrowthText,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onCopyCode) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = GrowthPrimary
                    )
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
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = GrowthSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "专属邀请链接", color = GrowthMuted, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(GrowthSurfaceSoft)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = inviteLink,
                    color = GrowthText,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onCopyLink) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = GrowthPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ActionOutlineButton(
                    modifier = Modifier.weight(1f),
                    label = "复制链接",
                    icon = Icons.Default.ContentCopy,
                    onClick = onCopyLink
                )
                Button(
                    onClick = onShare,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF243146),
                        contentColor = GrowthText
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.IosShare,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(text = "分享", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun InviteRulesCard() {
    val rules = listOf(
        "邀请好友注册并完成首单，可获一级返佣 20%",
        "二级关系订单按 10% 结算，自动计入可提现余额",
        "达到最低门槛后可发起链上提现申请",
        "平台会对异常邀请行为进行风控审核"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = GrowthSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ArrowOutward,
                    contentDescription = null,
                    tint = GrowthPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text(
                    text = "计划规则",
                    color = GrowthText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            rules.forEachIndexed { index, item ->
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = "${index + 1}",
                        color = GrowthPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(GrowthPrimarySoft)
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = item,
                        color = GrowthMuted,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (index != rules.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun InviteErrorView(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "加载失败", color = GrowthDanger, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = message, color = GrowthMuted, fontSize = 13.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun InviteCenterPagePreview() {
    MaterialTheme {
        InviteCenterPage()
    }
}
