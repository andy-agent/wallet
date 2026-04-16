package com.v2ray.ang.composeui.pages.p1

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.components.app.AppPageBackgroundStyle
import com.v2ray.ang.composeui.components.app.AppPageScaffold
import com.v2ray.ang.composeui.components.buttons.AppButtonSize
import com.v2ray.ang.composeui.components.buttons.AppPrimaryButton
import com.v2ray.ang.composeui.components.cards.AppCard
import com.v2ray.ang.composeui.components.cards.AppCardVariant
import com.v2ray.ang.composeui.components.chips.AppChip
import com.v2ray.ang.composeui.components.chips.AppChipTone
import com.v2ray.ang.composeui.components.feedback.EmptyStateCard
import com.v2ray.ang.composeui.components.navigation.AppTopBar
import com.v2ray.ang.composeui.components.navigation.AppTopBarMode
import com.v2ray.ang.composeui.components.rows.LabelValueRow
import com.v2ray.ang.composeui.components.sections.InfoSection
import com.v2ray.ang.composeui.p1.model.PlanOptionUi
import com.v2ray.ang.composeui.p1.model.PlansEvent
import com.v2ray.ang.composeui.p1.model.PlansUiState
import com.v2ray.ang.composeui.p1.model.plansPreviewState
import com.v2ray.ang.composeui.p1.viewmodel.PlansViewModel
import com.v2ray.ang.composeui.theme.AppTheme
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

private val PlansGlowBlue = Color(0x224F7CFF)
private val PlansGlowCyan = Color(0x1825D7FF)

@Composable
fun PlansRoute(
    viewModel: PlansViewModel,
    onPrimaryAction: (String) -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    PlansScreen(
        uiState = uiState,
        onRefresh = { viewModel.onEvent(PlansEvent.Refresh) },
        onPrimaryAction = { planCode ->
            viewModel.onEvent(PlansEvent.PrimaryActionClicked)
            onPrimaryAction(planCode)
        },
        onSecondaryAction = {
            viewModel.onEvent(PlansEvent.SecondaryActionClicked)
            onSecondaryAction?.invoke()
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun PlansScreen(
    uiState: PlansUiState,
    onRefresh: () -> Unit,
    onPrimaryAction: (String) -> Unit,
    onSecondaryAction: () -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    var selectedPlanCode by rememberSaveable { mutableStateOf<String?>(null) }
    LaunchedEffect(uiState.selectedPlanCode, uiState.plans) {
        val availableCodes = uiState.plans.map { it.planCode }
        if (selectedPlanCode == null || selectedPlanCode !in availableCodes) {
            selectedPlanCode = uiState.selectedPlanCode ?: availableCodes.firstOrNull()
        }
    }
    val selectedPlan = remember(uiState.plans, selectedPlanCode) {
        uiState.plans.firstOrNull { it.planCode == selectedPlanCode } ?: uiState.plans.firstOrNull()
    }
    val payEnabledText = selectedPlan?.paymentMethods?.firstOrNull()?.substringBefore('-')
        ?.takeIf { it.isNotBlank() }
        ?: "支持钱包直付"

    AppPageScaffold(
        backgroundStyle = AppPageBackgroundStyle.Hero,
        contentPadding = PaddingValues(
            horizontal = AppTheme.spacing.pageHorizontal,
            vertical = AppTheme.spacing.space20,
        ),
        background = {
            PlansBackgroundGlow()
        },
        bottomBar = {
            PlansBottomBar(
                selectedPlan = selectedPlan,
                buttonLabel = uiState.primaryActionLabel.ifBlank { "继续结算" },
                enabled = selectedPlan != null || uiState.screenState.hasError,
                onPayClick = {
                    selectedPlan?.let { onPrimaryAction(it.planCode) } ?: onRefresh()
                },
            )
        },
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 680.dp),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sectionGap),
        ) {
            AppTopBar(
                title = uiState.title,
                subtitle = uiState.subtitle,
                mode = AppTopBarMode.Hero,
                actions = {
                    AppChip(
                        text = payEnabledText,
                        tone = AppChipTone.Success,
                    )
                },
            )

            PlansStatusSection(
                currentPlanName = uiState.currentPlanName,
                accessDescription = uiState.currentPlanDescription,
                paymentMethods = selectedPlan?.paymentMethods.orEmpty(),
                statusText = uiState.currentPlanStatusText,
            )

            if (uiState.screenState.hasError) {
                EmptyStateCard(
                    title = "套餐服务不可用",
                    message = uiState.screenState.unavailableMessage
                        ?: uiState.screenState.errorMessage
                        ?: "当前套餐服务异常，请稍后重试。",
                    actionLabel = "重新加载",
                    onAction = onRefresh,
                )
            } else if (uiState.plans.isEmpty()) {
                EmptyStateCard(
                    title = "当前没有可售套餐",
                    message = uiState.screenState.emptyMessage ?: "稍后再试，或切换到其他页面继续浏览。",
                    actionLabel = uiState.secondaryActionLabel,
                    onAction = onSecondaryAction,
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space12),
                ) {
                    Text(
                        text = "可选套餐",
                        style = MaterialTheme.typography.titleLarge,
                        color = AppTheme.colors.textPrimary,
                    )
                    uiState.plans.forEach { plan ->
                        SubscriptionPlanCard(
                            plan = plan,
                            selected = plan.planCode == selectedPlan?.planCode,
                            onClick = { selectedPlanCode = plan.planCode },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlansStatusSection(
    currentPlanName: String,
    accessDescription: String,
    paymentMethods: List<String>,
    statusText: String,
) {
    InfoSection(
        title = "当前状态",
        subtitle = accessDescription,
        trailing = {
            AppChip(
                text = statusText,
                tone = planStatusTone(statusText),
            )
        },
    ) {
        LabelValueRow(
            label = "当前计划",
            value = currentPlanName,
        )
        androidx.compose.material3.HorizontalDivider(color = AppTheme.colors.dividerSubtle)
        LabelValueRow(
            label = "支付网络",
            value = paymentMethods.firstOrNull()?.substringBefore('-') ?: "--",
            supportingText = paymentMethods.joinToString("/").takeIf { it.isNotBlank() } ?: "等待可用支付网络",
        )
        androidx.compose.material3.HorizontalDivider(color = AppTheme.colors.dividerSubtle)
        LabelValueRow(
            label = "状态",
            value = statusText,
        )
    }
}

@Composable
private fun SubscriptionPlanCard(
    plan: PlanOptionUi,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val variant = if (selected) AppCardVariant.Highlight else AppCardVariant.Default
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        variant = variant,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space12),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space4),
                ) {
                    Text(
                        text = plan.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = AppTheme.colors.textPrimary,
                    )
                    if (plan.description.isNotBlank()) {
                        Text(
                            text = plan.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppTheme.colors.textSecondary,
                        )
                    }
                }
                AppChip(
                    text = when {
                        selected -> "已选择"
                        !plan.badge.isNullOrBlank() -> plan.badge
                        else -> "可购买"
                    },
                    tone = if (selected) AppChipTone.Brand else AppChipTone.Neutral,
                )
            }

            Text(
                text = plan.priceText,
                style = AppTheme.typography.metricL,
                color = AppTheme.colors.textPrimary,
            )

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.space8),
            ) {
                buildList {
                    add(plan.durationText)
                    add(plan.maxSessionsText)
                    addAll(plan.paymentMethods)
                }.filter { it.isNotBlank() }.forEach { tag ->
                    AppChip(
                        text = tag,
                        tone = if (selected) AppChipTone.Brand else AppChipTone.Neutral,
                        selected = selected,
                    )
                }
            }
        }
    }
}

@Composable
private fun PlansBottomBar(
    selectedPlan: PlanOptionUi?,
    buttonLabel: String,
    enabled: Boolean,
    onPayClick: () -> Unit,
) {
    Surface(
        color = AppTheme.colors.surfaceCard.copy(alpha = 0.96f),
        shadowElevation = AppTheme.elevation.card,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = AppTheme.spacing.pageHorizontal, vertical = AppTheme.spacing.space12),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space8),
        ) {
            selectedPlan?.let { plan ->
                Text(
                    text = "已选 ${plan.title} · ${plan.priceText}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppTheme.colors.textSecondary,
                )
            }
            AppPrimaryButton(
                text = buttonLabel,
                onClick = onPayClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
            )
        }
    }
}

@Composable
private fun PlansBackgroundGlow() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(220.dp)
                .background(PlansGlowBlue, RoundedCornerShape(999.dp))
                .blur(48.dp),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(top = 360.dp)
                .size(260.dp)
                .background(PlansGlowCyan, RoundedCornerShape(999.dp))
                .blur(60.dp),
        )
    }
}

private fun planStatusTone(statusText: String): AppChipTone = when {
    statusText.contains("尚未", ignoreCase = true) -> AppChipTone.Warning
    statusText.contains("未", ignoreCase = true) -> AppChipTone.Warning
    statusText.contains("待", ignoreCase = true) -> AppChipTone.Warning
    statusText.contains("可用", ignoreCase = true) -> AppChipTone.Success
    statusText.contains("有效", ignoreCase = true) -> AppChipTone.Success
    statusText.contains("ACTIVE", ignoreCase = true) -> AppChipTone.Success
    else -> AppChipTone.Info
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F7FF)
@Composable
private fun PlansPreview() {
    CryptoVpnTheme {
        PlansScreen(
            uiState = plansPreviewState(),
            onRefresh = {},
            onPrimaryAction = {},
            onSecondaryAction = {},
        )
    }
}
