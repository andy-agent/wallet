package com.v2ray.ang.composeui.pages.growth

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
}

@Composable
fun InviteCenterPage(
    viewModel: InviteCenterViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToCommission: () -> Unit = {},
    onNavigateToWithdraw: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    GrowthPageScaffold(
        title = "邀请中心",
        onNavigateBack = onNavigateBack
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GrowthPageBackground)
                .padding(paddingValues)
        ) {
            when (val currentState = state) {
                is InviteCenterState.Loaded -> {
                    InviteCenterContent(
                        state = currentState,
                        onCopyCode = {
                            clipboardManager.setText(AnnotatedString(currentState.inviteCode))
                        },
                        onCopyLink = {
                            clipboardManager.setText(AnnotatedString(currentState.inviteLink))
                        },
                        onShare = {},
                        onNavigateToCommission = onNavigateToCommission,
                        onNavigateToWithdraw = onNavigateToWithdraw
                    )
                }

                is InviteCenterState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = GrowthAccent)
                    }
                }

                is InviteCenterState.Error -> {
                    GrowthStatusView(
                        title = "邀请加载失败",
                        message = currentState.message
                    )
                }

                InviteCenterState.Idle -> Unit
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        InviteHeroCard(
            state = state,
            onNavigateToCommission = onNavigateToCommission,
            onNavigateToWithdraw = onNavigateToWithdraw
        )
        InviteChannelCard(
            inviteCode = state.inviteCode,
            inviteLink = state.inviteLink,
            onCopyCode = onCopyCode,
            onCopyLink = onCopyLink,
            onShare = onShare
        )
        InviteRulesCard()
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun InviteHeroCard(
    state: InviteCenterState.Loaded,
    onNavigateToCommission: () -> Unit,
    onNavigateToWithdraw: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .background(GrowthHeroGradient)
                .padding(22.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Discover Growth",
                        color = Color(0xFF5E4300),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.pendingCommission,
                        color = Color(0xFF161A1E),
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "当前可提收益",
                        color = Color(0xFF5E4300),
                        fontSize = 13.sp
                    )
                }
                GrowthBadge(text = state.commissionRate)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GrowthStatChip(
                    label = "已邀请",
                    value = state.totalInvited.toString(),
                    modifier = Modifier.weight(1f)
                )
                GrowthStatChip(
                    label = "累计到账",
                    value = state.totalCommission,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onNavigateToWithdraw,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF161A1E),
                        contentColor = GrowthTextPrimary
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("去提现", fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = onNavigateToCommission,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.18f),
                        contentColor = Color(0xFF161A1E)
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("查看账本", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun InviteChannelCard(
    inviteCode: String,
    inviteLink: String,
    onCopyCode: () -> Unit,
    onCopyLink: () -> Unit,
    onShare: () -> Unit
) {
    GrowthSectionCard {
        GrowthSectionTitle(
            title = "推广素材",
            subtitle = "使用邀请码或专属链接分发给好友，延续 Bitget Discover 风格的信息密度。"
        )
        Spacer(modifier = Modifier.height(18.dp))

        InviteInfoPanel(
            title = "邀请码",
            value = inviteCode,
            actionText = "复制",
            actionIcon = Icons.Default.ContentCopy,
            onAction = onCopyCode
        )

        Spacer(modifier = Modifier.height(14.dp))

        InviteInfoPanel(
            title = "专属链接",
            value = inviteLink,
            actionText = "复制",
            actionIcon = Icons.Default.ContentCopy,
            singleLine = true,
            onAction = onCopyLink
        )

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = onShare,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = GrowthAccent,
                contentColor = Color(0xFF161A1E)
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("分享邀请素材", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun InviteInfoPanel(
    title: String,
    value: String,
    actionText: String,
    actionIcon: androidx.compose.ui.graphics.vector.ImageVector,
    singleLine: Boolean = false,
    onAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(GrowthSurfaceRaised)
            .padding(16.dp)
    ) {
        Text(text = title, color = GrowthTextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                color = GrowthTextPrimary,
                fontSize = if (singleLine) 13.sp else 28.sp,
                fontWeight = FontWeight.Bold,
                maxLines = if (singleLine) 1 else Int.MAX_VALUE,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(onClick = onAction) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(actionIcon, contentDescription = actionText, tint = GrowthAccent)
                }
            }
        }
    }
}

@Composable
private fun InviteRulesCard() {
    val rules = listOf(
        "一级邀请返佣 20%，二级邀请返佣 10%，结算后会直接进入可提现余额。",
        "订单完成后自动入账，无需手动确认；账本页面可查看每条来源与结算状态。",
        "推广链路优先突出邀请码和专属链接，保持 Discover 页的入口清晰度。",
        "邀请人数不限，收益可累计后统一提现到绑定的钱包地址。"
    )

    GrowthSectionCard {
        GrowthSectionTitle(
            title = "活动规则",
            subtitle = "文案与排版采用 Bitget Discover/Growth 的导购节奏：先收益，再入口，最后规则。"
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("收益导向", "入口前置", "即时提现").forEach {
                GrowthBadge(text = it)
                Spacer(modifier = Modifier.width(0.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        rules.forEachIndexed { index, rule ->
            GrowthBulletItem(text = "${index + 1}. $rule")
            if (index != rules.lastIndex) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InviteCenterPagePreview() {
    MaterialTheme {
        InviteCenterContent(
            state = InviteCenterState.Loaded(
                inviteCode = "LJ8888",
                inviteLink = "https://api.residential-agent.com/invite/LJ8888",
                totalInvited = 24,
                totalCommission = "$328.12",
                pendingCommission = "$81.60",
                commissionRate = "L1 20% / L2 10%"
            ),
            onCopyCode = {},
            onCopyLink = {},
            onShare = {},
            onNavigateToCommission = {},
            onNavigateToWithdraw = {}
        )
    }
}
