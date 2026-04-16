package com.v2ray.ang.composeui.pages.p2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Toll
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.components.actions.ActionCluster
import com.v2ray.ang.composeui.components.actions.ActionClusterAction
import com.v2ray.ang.composeui.components.app.AppPageBackgroundStyle
import com.v2ray.ang.composeui.components.app.AppPageScaffold
import com.v2ray.ang.composeui.components.buttons.AppButtonVariant
import com.v2ray.ang.composeui.components.chips.AppChip
import com.v2ray.ang.composeui.components.chips.AppChipTone
import com.v2ray.ang.composeui.components.listitems.AppListRow
import com.v2ray.ang.composeui.components.navigation.AppTopBar
import com.v2ray.ang.composeui.components.navigation.AppTopBarMode
import com.v2ray.ang.composeui.components.navigation.CryptoVpnBottomBar
import com.v2ray.ang.composeui.components.rows.LabelValueRow
import com.v2ray.ang.composeui.components.sections.InfoSection
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p2.model.ProfileEvent
import com.v2ray.ang.composeui.p2.model.ProfileUiState
import com.v2ray.ang.composeui.p2.model.profilePreviewState
import com.v2ray.ang.composeui.p2.viewmodel.ProfileViewModel
import com.v2ray.ang.composeui.theme.AppTheme
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.theme.tokens.OverviewBaselineTokens

private data class ProfileEntryUi(
    val title: String,
    val subtitle: String,
    val value: String,
    val route: String,
    val icon: ImageVector,
    val tone: AppChipTone,
)

@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    ProfileScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                ProfileEvent.PrimaryActionClicked -> onPrimaryAction()
                ProfileEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onEvent: (ProfileEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val baseline = OverviewBaselineTokens.primary
    val accountLabel = uiState.checklist.firstOrNull()?.detail?.takeIf { it.isNotBlank() } ?: "--"
    val accountStatus = uiState.note.ifBlank { uiState.metrics.getOrNull(2)?.value ?: "--" }
    val entryItems = buildProfileEntries(uiState.highlights)
    val quickActions = buildList {
        if (uiState.primaryActionLabel.isNotBlank()) {
            add(
                ActionClusterAction(
                    label = uiState.primaryActionLabel,
                    onClick = { onEvent(ProfileEvent.PrimaryActionClicked) },
                    variant = AppButtonVariant.Primary,
                ),
            )
        }
        if (!uiState.secondaryActionLabel.isNullOrBlank()) {
            add(
                ActionClusterAction(
                    label = uiState.secondaryActionLabel,
                    onClick = { onEvent(ProfileEvent.SecondaryActionClicked) },
                    variant = AppButtonVariant.Secondary,
                ),
            )
        }
    }

    AppPageScaffold(
        backgroundStyle = AppPageBackgroundStyle.Tech,
        bottomBar = {
            CryptoVpnBottomBar(
                currentRoute = CryptoVpnRouteSpec.profile.name,
                onRouteSelected = onBottomNav,
            )
        },
        contentPadding = PaddingValues(
            horizontal = baseline.pageHorizontal,
            vertical = baseline.pageTopSpacing,
        ),
    ) { _ ->
        AppTopBar(
            title = uiState.title,
            subtitle = uiState.subtitle,
            mode = AppTopBarMode.Hero,
        )

        InfoSection(
            title = "账户信息",
            subtitle = accountLabel,
            trailing = {
                if (accountStatus.isNotBlank() && accountStatus != "--") {
                    AppChip(
                        text = accountStatus,
                        tone = profileStatusTone(accountStatus),
                    )
                }
            },
        ) {
            LabelValueRow(
                label = "当前套餐",
                value = uiState.metrics.getOrNull(0)?.value ?: "--",
            )
            HorizontalDivider(color = AppTheme.colors.dividerSubtle)
            LabelValueRow(
                label = "订单数",
                value = uiState.metrics.getOrNull(1)?.value ?: "0",
            )
            HorizontalDivider(color = AppTheme.colors.dividerSubtle)
            LabelValueRow(
                label = "账户状态",
                value = uiState.metrics.getOrNull(2)?.value ?: accountStatus,
            )
        }

        if (quickActions.isNotEmpty()) {
            ActionCluster(
                actions = quickActions,
                layoutMode = if (quickActions.size > 1) {
                    com.v2ray.ang.composeui.components.actions.ActionClusterLayoutMode.Row
                } else {
                    com.v2ray.ang.composeui.components.actions.ActionClusterLayoutMode.Stack
                },
            )
        }

        InfoSection(
            title = "常用入口",
            subtitle = "账户、钱包、订阅与支持入口",
        ) {
            entryItems.forEachIndexed { index, item ->
                AppListRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onBottomNav(item.route) },
                    title = item.title,
                    subtitle = item.subtitle,
                    value = item.value,
                    leading = {
                        ProfileEntryIcon(
                            icon = item.icon,
                            tone = item.tone,
                        )
                    },
                    trailing = {
                        AppChip(
                            text = "进入",
                            tone = AppChipTone.Neutral,
                        )
                    },
                )
                if (index != entryItems.lastIndex) {
                    HorizontalDivider(color = AppTheme.colors.dividerSubtle)
                }
            }
        }
    }
}

@Composable
private fun ProfileEntryIcon(
    icon: ImageVector,
    tone: AppChipTone,
) {
    val bgColor = when (tone) {
        AppChipTone.Success -> AppTheme.colors.successBg
        AppChipTone.Warning -> AppTheme.colors.warningBg
        AppChipTone.Error -> AppTheme.colors.errorBg
        AppChipTone.Info -> AppTheme.colors.infoBg
        AppChipTone.Brand -> AppTheme.colors.infoBg
        AppChipTone.Neutral -> AppTheme.colors.surfaceElevated
    }
    val tintColor = when (tone) {
        AppChipTone.Success -> AppTheme.colors.success
        AppChipTone.Warning -> AppTheme.colors.warning
        AppChipTone.Error -> AppTheme.colors.error
        AppChipTone.Info -> AppTheme.colors.info
        AppChipTone.Brand -> AppTheme.colors.brandPrimary
        AppChipTone.Neutral -> AppTheme.colors.textSecondary
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tintColor,
            modifier = Modifier.size(20.dp),
        )
    }
}

private fun buildProfileEntries(highlights: List<FeatureListItem>): List<ProfileEntryUi> {
    val itemsByTitle = highlights.associateBy { it.title }
    return listOf(
        profileEntry(
            title = "安全中心",
            route = CryptoVpnRouteSpec.securityCenter.pattern,
            icon = Icons.Rounded.Security,
            tone = AppChipTone.Info,
            source = itemsByTitle["安全中心"],
            fallbackSubtitle = "助记词、设备、会话与本地钱包管理",
        ),
        profileEntry(
            title = "钱包管理",
            route = CryptoVpnRouteSpec.walletManagerRoute("primary_wallet"),
            icon = Icons.Rounded.Wallet,
            tone = AppChipTone.Brand,
            source = itemsByTitle["钱包管理"] ?: itemsByTitle["订单与订阅"],
            fallbackSubtitle = "管理当前钱包与新增钱包",
        ),
        profileEntry(
            title = "订单与订阅",
            route = CryptoVpnRouteSpec.orderList.pattern,
            icon = Icons.AutoMirrored.Rounded.ReceiptLong,
            tone = AppChipTone.Success,
            source = itemsByTitle["订单与订阅"],
            fallbackSubtitle = "查看支付、续费与订单状态",
        ),
        profileEntry(
            title = "邀请中心",
            route = CryptoVpnRouteSpec.inviteCenter.pattern,
            icon = Icons.Rounded.Toll,
            tone = AppChipTone.Info,
            source = itemsByTitle["邀请中心"],
            fallbackSubtitle = "推广链接与佣金收入",
        ),
        profileEntry(
            title = "法务文档",
            route = CryptoVpnRouteSpec.legalDocuments.pattern,
            icon = Icons.Rounded.Description,
            tone = AppChipTone.Warning,
            source = itemsByTitle["法务文档"],
            fallbackSubtitle = "服务协议、隐私与免责声明",
        ),
        profileEntry(
            title = "关于应用",
            route = CryptoVpnRouteSpec.aboutApp.pattern,
            icon = Icons.Rounded.Info,
            tone = AppChipTone.Neutral,
            source = itemsByTitle["关于应用"],
            fallbackSubtitle = "版本、更新与帮助",
        ),
    )
}

private fun profileEntry(
    title: String,
    route: String,
    icon: ImageVector,
    tone: AppChipTone,
    source: FeatureListItem?,
    fallbackSubtitle: String,
): ProfileEntryUi {
    val normalizedSubtitle = when (title) {
        "订单与订阅" -> {
            when {
                !source?.trailing.isNullOrBlank() && source?.trailing != "进入" -> "最近订单 ${source?.trailing}"
                !source?.subtitle.isNullOrBlank() && source?.subtitle != "进入" -> "共 ${source?.subtitle} 笔订单"
                else -> fallbackSubtitle
            }
        }

        else -> source?.subtitle
            ?.takeIf { it.isNotBlank() && it != "进入" && it != "--" }
            ?: source?.trailing?.takeIf { it.isNotBlank() && it != "进入" && it != "--" }
            ?: fallbackSubtitle
    }

    val normalizedValue = when (title) {
        "订单与订阅" -> source?.subtitle?.takeIf { it.isNotBlank() && it.all(Char::isDigit) } ?: ""
        "关于应用" -> source?.trailing?.takeIf { it.isNotBlank() && it != "进入" && it != "--" } ?: ""
        else -> ""
    }

    return ProfileEntryUi(
        title = title,
        subtitle = normalizedSubtitle,
        value = normalizedValue,
        route = route,
        icon = icon,
        tone = tone,
    )
}

private fun profileStatusTone(status: String): AppChipTone = when {
    status.contains("ACTIVE", ignoreCase = true) -> AppChipTone.Success
    status.contains("完成", ignoreCase = true) -> AppChipTone.Success
    status.contains("已", ignoreCase = true) -> AppChipTone.Success
    status.contains("未", ignoreCase = true) -> AppChipTone.Warning
    status.contains("PENDING", ignoreCase = true) -> AppChipTone.Warning
    status.contains("ERROR", ignoreCase = true) -> AppChipTone.Error
    else -> AppChipTone.Info
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ProfilePreview() {
    CryptoVpnTheme {
        ProfileScreen(
            uiState = profilePreviewState(),
            onEvent = {},
        )
    }
}
