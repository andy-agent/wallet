package com.v2ray.ang.composeui.pages.growth

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
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
import kotlin.random.Random

private enum class RewardTab(
    val label: String,
    val title: String,
    val summary: String,
    val accent: Color,
) {
    CARD(
        label = "银行卡",
        title = "银行卡返佣",
        summary = "消费返佣优先沉淀到可提现余额，保持 Discover 页首屏导购感。",
        accent = Color(0xFF9FF86F),
    ),
    SWAP(
        label = "Swap",
        title = "兑换分润",
        summary = "使用邀请码绑定后，受邀人每次兑换都会计入统一佣金账本。",
        accent = Color(0xFF78E7FF),
    ),
    CONTRACT(
        label = "合约",
        title = "合约奖励",
        summary = "高频交易奖励以阶梯规则累计，账本与提现页共享同一结算节奏。",
        accent = Color(0xFFFFD37E),
    ),
}

sealed class InviteCenterState {
    data object Loading : InviteCenterState()

    data class Loaded(
        val accountId: String,
        val inviteCode: String,
        val hasBinding: Boolean,
        val level1InviteCount: Int,
        val level2InviteCount: Int,
        val level1Income: String,
        val level2Income: String,
        val availableCommission: String,
        val frozenCommission: String,
        val withdrawnCommission: String,
        val minWithdrawAmount: String,
        val isSubmitting: Boolean = false,
        val inlineMessage: String? = null,
        val inlineMessageIsError: Boolean = false,
    ) : InviteCenterState() {
        val inviteLink: String = "https://api.residential-agent.com/invite/$inviteCode"
        val totalInvited: Int = level1InviteCount + level2InviteCount
    }

    data class Error(val message: String) : InviteCenterState()
}

class InviteCenterViewModel(application: Application) : AndroidViewModel(application) {
    private val growthBridgeRepository = GrowthBridgeRepository(application)
    private val _state = MutableStateFlow<InviteCenterState>(InviteCenterState.Loading)
    val state: StateFlow<InviteCenterState> = _state

    init {
        loadInviteData()
    }

    fun loadInviteData(
        inlineMessage: String? = (_state.value as? InviteCenterState.Loaded)?.inlineMessage,
        inlineMessageIsError: Boolean = false,
    ) {
        viewModelScope.launch {
            _state.value = InviteCenterState.Loading

            val referralResult = growthBridgeRepository.getReferralOverview()
            val summaryResult = growthBridgeRepository.getCommissionSummary()
            if (referralResult.isFailure) {
                _state.value = InviteCenterState.Error(
                    referralResult.exceptionOrNull()?.message ?: "加载邀请信息失败",
                )
                return@launch
            }
            if (summaryResult.isFailure) {
                _state.value = InviteCenterState.Error(
                    summaryResult.exceptionOrNull()?.message ?: "加载佣金信息失败",
                )
                return@launch
            }

            val referral = referralResult.getOrNull()
            val summary = summaryResult.getOrNull()
            if (referral == null || summary == null) {
                _state.value = InviteCenterState.Error("邀请数据为空")
                return@launch
            }

            _state.value = InviteCenterState.Loaded(
                accountId = referral.accountId,
                inviteCode = referral.referralCode,
                hasBinding = referral.hasBinding,
                level1InviteCount = referral.level1InviteCount,
                level2InviteCount = referral.level2InviteCount,
                level1Income = referral.level1IncomeUsdt,
                level2Income = referral.level2IncomeUsdt,
                availableCommission = summary.availableAmount,
                frozenCommission = summary.frozenAmount,
                withdrawnCommission = summary.withdrawnTotal,
                minWithdrawAmount = referral.minWithdrawAmountUsdt,
                inlineMessage = inlineMessage,
                inlineMessageIsError = inlineMessageIsError,
            )
        }
    }

    fun bindReferralCode(code: String) {
        val currentState = _state.value as? InviteCenterState.Loaded ?: return
        if (currentState.isSubmitting) return

        if (code.isBlank()) {
            _state.value = currentState.copy(
                inlineMessage = "请输入邀请码",
                inlineMessageIsError = true,
            )
            return
        }

        viewModelScope.launch {
            _state.value = currentState.copy(
                isSubmitting = true,
                inlineMessage = null,
                inlineMessageIsError = false,
            )
            growthBridgeRepository.bindReferralCode(code.trim()).onSuccess {
                loadInviteData(inlineMessage = "邀请码已更新", inlineMessageIsError = false)
            }.onFailure {
                _state.value = currentState.copy(
                    isSubmitting = false,
                    inlineMessage = it.message ?: "邀请码绑定失败",
                    inlineMessageIsError = true,
                )
            }
        }
    }

    fun clearInlineMessage() {
        val currentState = _state.value as? InviteCenterState.Loaded ?: return
        if (currentState.inlineMessage != null) {
            _state.value = currentState.copy(
                inlineMessage = null,
                inlineMessageIsError = false,
            )
        }
    }
}

@Composable
fun InviteCenterPage(
    viewModel: InviteCenterViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToCommission: () -> Unit = {},
    onNavigateToWithdraw: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    GrowthPageScaffold(
        topBar = {
            GrowthTopBar(
                onNavigateBack = onNavigateBack,
                trailingContent = {
                    GrowthTopActionPill(label = "奖励记录", onClick = onNavigateToCommission)
                },
            )
        },
    ) { paddingValues ->
        when (val currentState = state) {
            InviteCenterState.Loading -> Unit
            is InviteCenterState.Error -> {
                GrowthStatusView(
                    title = "邀请加载失败",
                    message = currentState.message,
                    modifier = Modifier.padding(paddingValues),
                )
            }

            is InviteCenterState.Loaded -> {
                InviteCenterContent(
                    state = currentState,
                    paddingValues = paddingValues,
                    onBindCode = viewModel::bindReferralCode,
                    onDismissInlineMessage = viewModel::clearInlineMessage,
                    onCopyCode = {
                        clipboardManager.setText(AnnotatedString(currentState.inviteCode))
                    },
                    onCopyLink = {
                        clipboardManager.setText(AnnotatedString(currentState.inviteLink))
                    },
                    onNavigateToCommission = onNavigateToCommission,
                    onNavigateToWithdraw = onNavigateToWithdraw,
                )
            }
        }
    }
}

@Composable
private fun InviteCenterContent(
    state: InviteCenterState.Loaded,
    paddingValues: PaddingValues,
    onBindCode: (String) -> Unit,
    onDismissInlineMessage: () -> Unit,
    onCopyCode: () -> Unit,
    onCopyLink: () -> Unit,
    onNavigateToCommission: () -> Unit,
    onNavigateToWithdraw: () -> Unit,
) {
    var draftCode by rememberSaveable(state.inviteCode, state.hasBinding) {
        mutableStateOf(if (state.hasBinding) state.inviteCode else "")
    }
    var selectedTab by rememberSaveable { mutableStateOf(RewardTab.CARD) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 8.dp,
            bottom = 28.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            InviteHeroSection(state = state)
        }

        item {
            InviteCodeCard(
                state = state,
                draftCode = draftCode,
                onDraftCodeChange = {
                    val sanitized = it.uppercase().filter { char -> char.isLetterOrDigit() }.take(15)
                    draftCode = sanitized
                    onDismissInlineMessage()
                },
                onGenerateRandom = {
                    draftCode = randomInviteCode()
                    onDismissInlineMessage()
                },
                onSubmit = { onBindCode(draftCode) },
            )
        }

        item {
            InviteRewardTabs(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
            )
        }

        item {
            InviteRewardCard(
                tab = selectedTab,
                availableCommission = state.availableCommission,
                totalInvited = state.totalInvited,
            )
        }

        item {
            InvitePerformanceCard(
                state = state,
                onCopyCode = onCopyCode,
                onCopyLink = onCopyLink,
                onNavigateToCommission = onNavigateToCommission,
                onNavigateToWithdraw = onNavigateToWithdraw,
            )
        }

        item {
            InviteRulesCard(minWithdrawAmount = state.minWithdrawAmount)
        }
    }
}

@Composable
private fun InviteHeroSection(state: InviteCenterState.Loaded) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "邀请中心",
                color = GrowthTextPrimary,
                fontSize = 32.sp,
                lineHeight = 36.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "邀请好友绑定，赚返佣",
                color = GrowthTextSecondary,
                fontSize = 15.sp,
            )

            GrowthBadge(
                text = "${maskAccount(state.accountId)} 获得奖励记录更新",
                containerColor = GrowthSurface.copy(alpha = 0.92f),
                contentColor = GrowthTextSecondary,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GrowthMetricBlock(
                    value = state.totalInvited.toString(),
                    label = "已邀请",
                    modifier = Modifier.weight(1f),
                )
                GrowthMetricBlock(
                    value = "$${state.availableCommission}",
                    label = "可提现",
                    modifier = Modifier.weight(1f),
                )
            }
        }

        InviteHeroArtwork()
    }
}

@Composable
private fun InviteHeroArtwork() {
    Box(
        modifier = Modifier
            .width(138.dp)
            .height(148.dp),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(108.dp)
                .clip(CircleShape)
                .background(Color(0xFFA4B2FF)),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(78.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF262F35), Color(0xFF44545E)),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "LJ",
                    color = GrowthTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                )
            }
        }

        listOf(
            Triple(Alignment.TopStart, Color(0xFFBBFFB4), Icons.Default.Share),
            Triple(Alignment.CenterStart, Color(0xFF4AE7EB), Icons.Default.ContentCopy),
            Triple(Alignment.TopEnd, Color(0xFFD6F3FF), Icons.Default.CardGiftcard),
        ).forEachIndexed { index, (alignment, color, icon) ->
            Surface(
                modifier = Modifier
                    .align(alignment)
                    .padding(
                        start = if (index == 1) 4.dp else 0.dp,
                        top = if (index == 0) 4.dp else 0.dp,
                    ),
                shape = CircleShape,
                color = color,
            ) {
                Box(
                    modifier = Modifier.size(if (index == 1) 30.dp else 26.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = GrowthPageBackgroundDeep,
                        modifier = Modifier.size(if (index == 1) 14.dp else 12.dp),
                    )
                }
            }
        }

        listOf(
            Pair(Alignment.BottomStart, Color(0xFF79F5FF)),
            Pair(Alignment.CenterEnd, Color(0xFFFFB8D3)),
            Pair(Alignment.BottomCenter, Color(0xFF2AD6EA)),
        ).forEach { (alignment, color) ->
            Box(
                modifier = Modifier
                    .align(alignment)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color),
            )
        }
    }
}

@Composable
private fun InviteCodeCard(
    state: InviteCenterState.Loaded,
    draftCode: String,
    onDraftCodeChange: (String) -> Unit,
    onGenerateRandom: () -> Unit,
    onSubmit: () -> Unit,
) {
    GrowthSectionCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "创建你的专属邀请码",
                    color = GrowthTextPrimary,
                    fontSize = 28.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "邀请好友绑定赚返佣",
                    color = GrowthTextSecondary,
                    fontSize = 13.sp,
                )
            }
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = GrowthAccentSoft,
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.CardGiftcard,
                        contentDescription = null,
                        tint = GrowthAccent,
                    )
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(22.dp),
            color = GrowthSurfaceRaised,
            border = androidx.compose.foundation.BorderStroke(1.dp, GrowthBorder),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                BasicTextField(
                    value = draftCode,
                    onValueChange = onDraftCodeChange,
                    modifier = Modifier.weight(1f),
                    enabled = !state.hasBinding,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = GrowthTextPrimary,
                        fontWeight = FontWeight.Medium,
                    ),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                    decorationBox = { innerTextField ->
                        if (draftCode.isBlank()) {
                            Text(
                                text = "8-15个字母或者数字",
                                color = GrowthTextTertiary,
                                fontSize = 18.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        innerTextField()
                    },
                )

                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .clickable(enabled = !state.hasBinding, onClick = onGenerateRandom),
                    shape = RoundedCornerShape(999.dp),
                    color = if (state.hasBinding) GrowthSurfaceStrong else GrowthAccentSoft,
                ) {
                    Text(
                        text = "随机",
                        color = if (state.hasBinding) GrowthTextTertiary else GrowthAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    )
                }
            }
        }

        GrowthPrimaryButton(
            text = when {
                state.isSubmitting -> "邀请码提交中"
                state.hasBinding -> "邀请码已绑定"
                else -> "创建邀请码"
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.hasBinding && !state.isSubmitting && draftCode.length in 8..15,
            onClick = onSubmit,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (state.hasBinding) "已绑定邀请码" else "当前邀请码",
                color = GrowthTextSecondary,
                fontSize = 14.sp,
            )
            Text(
                text = state.inviteCode,
                color = GrowthAccent,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        state.inlineMessage?.let {
            Text(
                text = it,
                color = if (state.inlineMessageIsError) GrowthNegative else GrowthPositive,
                fontSize = 13.sp,
            )
        }
    }
}

@Composable
private fun InviteRewardTabs(
    selectedTab: RewardTab,
    onTabSelected: (RewardTab) -> Unit,
) {
    GrowthSectionCard(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp)) {
        GrowthSectionTitle(
            title = "推荐奖励",
            subtitle = "Hero 表单之后进入 tabs 卡，再承接长内容区，不打断整体滚动节奏。",
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            RewardTab.entries.forEach { tab ->
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .clickable(onClick = { onTabSelected(tab) }),
                    color = if (tab == selectedTab) GrowthSurfaceStrong else GrowthSurfaceRaised,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (tab == selectedTab) GrowthAccent.copy(alpha = 0.9f) else GrowthBorder,
                    ),
                ) {
                    Text(
                        text = tab.label,
                        color = if (tab == selectedTab) GrowthTextPrimary else GrowthTextSecondary,
                        fontWeight = if (tab == selectedTab) FontWeight.SemiBold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun InviteRewardCard(
    tab: RewardTab,
    availableCommission: String,
    totalInvited: Int,
) {
    GrowthHighlightCard {
        GrowthBadge(
            text = tab.label,
            containerColor = tab.accent.copy(alpha = 0.18f),
            contentColor = tab.accent,
        )
        Text(
            text = tab.title,
            color = GrowthTextPrimary,
            fontSize = 24.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = tab.summary,
            color = GrowthTextSecondary,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GrowthStatChip(
                label = "当前可提",
                value = "$$availableCommission",
                modifier = Modifier.weight(1f),
                accentColor = tab.accent,
            )
            GrowthStatChip(
                label = "邀请人数",
                value = totalInvited.toString(),
                modifier = Modifier.weight(1f),
                accentColor = tab.accent,
            )
        }
    }
}

@Composable
private fun InvitePerformanceCard(
    state: InviteCenterState.Loaded,
    onCopyCode: () -> Unit,
    onCopyLink: () -> Unit,
    onNavigateToCommission: () -> Unit,
    onNavigateToWithdraw: () -> Unit,
) {
    val panels = remember(state.inviteCode, state.inviteLink) {
        listOf(
            Triple("邀请码", state.inviteCode, onCopyCode),
            Triple("专属链接", state.inviteLink, onCopyLink),
        )
    }

    GrowthSectionCard {
        GrowthSectionTitle(
            title = "推广素材",
            subtitle = "保持 Bitget 邀请页“表单卡之后是信息卡和操作卡”的连续层级。",
        )

        panels.forEachIndexed { index, (title, value, action) ->
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = GrowthSurfaceRaised,
                border = androidx.compose.foundation.BorderStroke(1.dp, GrowthBorder),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = title, color = GrowthTextSecondary, fontSize = 12.sp)
                        Text(
                            text = value,
                            color = GrowthTextPrimary,
                            fontSize = if (index == 0) 24.sp else 13.sp,
                            lineHeight = if (index == 0) 28.sp else 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = if (index == 0) 1 else 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                    IconButton(onClick = action) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = GrowthAccent,
                        )
                    }
                }
            }
        }

        GrowthSectionCard(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier,
        ) {
            GrowthInfoRow(
                label = "一级邀请",
                value = "${state.level1InviteCount} 人 / ${state.level1Income} USDT",
                emphasize = true,
            )
            GrowthListDivider()
            GrowthInfoRow(
                label = "二级邀请",
                value = "${state.level2InviteCount} 人 / ${state.level2Income} USDT",
                emphasize = true,
            )
            GrowthListDivider()
            GrowthInfoRow(label = "冻结收益", value = "${state.frozenCommission} USDT")
            GrowthListDivider()
            GrowthInfoRow(label = "累计到账", value = "${state.withdrawnCommission} USDT")
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GrowthSecondaryButton(
                text = "奖励记录",
                modifier = Modifier.weight(1f),
                onClick = onNavigateToCommission,
            )
            GrowthPrimaryButton(
                text = "去提现",
                modifier = Modifier.weight(1f),
                onClick = onNavigateToWithdraw,
            )
        }
    }
}

@Composable
private fun InviteRulesCard(minWithdrawAmount: String) {
    val rules = listOf(
        "邀请码支持 8-15 位字母或数字，完成绑定后会用于后续推广链路和素材分享。",
        "一级邀请与二级邀请的收益统一进入佣金账本，状态变化以账本列表和提现页为准。",
        "最低提现金额为 $minWithdrawAmount USDT，提现申请完成后通常需要 1-3 个工作日处理。",
        "推广页先强调邀请码与奖励，再下沉到规则说明，避免把说明文案挤占首屏主操作。",
    )

    GrowthSectionCard {
        GrowthSectionTitle(
            title = "活动规则",
            subtitle = "缺图页面按“摘要区 + 连续内容区”派生，这里把规则集中到同一张长卡中。",
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            rules.forEach { rule ->
                GrowthBulletItem(text = rule)
            }
        }
    }
}

private fun randomInviteCode(): String {
    val pool = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    return buildString {
        repeat(8) {
            append(pool[Random.nextInt(pool.length)])
        }
    }
}

private fun maskAccount(accountId: String): String {
    if (accountId.length <= 10) return accountId
    return "${accountId.take(6)}...${accountId.takeLast(4)}"
}

@Preview(showBackground = true)
@Composable
private fun InviteCenterPagePreview() {
    MaterialTheme {
        GrowthBitgetBackground {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
            ) {
                InviteCenterContent(
                    state = InviteCenterState.Loaded(
                        accountId = "0x40c0c7e7",
                        inviteCode = "5Z5VAW",
                        hasBinding = true,
                        level1InviteCount = 18,
                        level2InviteCount = 6,
                        level1Income = "81.50",
                        level2Income = "14.20",
                        availableCommission = "55.18",
                        frozenCommission = "11.00",
                        withdrawnCommission = "328.12",
                        minWithdrawAmount = "10.00",
                    ),
                    paddingValues = PaddingValues(),
                    onBindCode = {},
                    onDismissInlineMessage = {},
                    onCopyCode = {},
                    onCopyLink = {},
                    onNavigateToCommission = {},
                    onNavigateToWithdraw = {},
                )
            }
        }
    }
}
