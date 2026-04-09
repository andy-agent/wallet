package com.v2ray.ang.composeui.pages.wallet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.theme.BackgroundDeepest
import com.v2ray.ang.composeui.theme.BackgroundOverlay
import com.v2ray.ang.composeui.theme.BackgroundPrimary
import com.v2ray.ang.composeui.theme.BackgroundSecondary
import com.v2ray.ang.composeui.theme.BackgroundTertiary
import com.v2ray.ang.composeui.theme.BorderDefault
import com.v2ray.ang.composeui.theme.Error
import com.v2ray.ang.composeui.theme.Info
import com.v2ray.ang.composeui.theme.Primary
import com.v2ray.ang.composeui.theme.SolanaPurple
import com.v2ray.ang.composeui.theme.TextPrimary
import com.v2ray.ang.composeui.theme.TextSecondary
import com.v2ray.ang.composeui.theme.TextTertiary
import com.v2ray.ang.composeui.theme.TronRed
import com.v2ray.ang.composeui.theme.USDTGreen
import com.v2ray.ang.composeui.theme.Warning

internal val WalletAccent = Primary
internal val WalletBackgroundTop = BackgroundOverlay
internal val WalletBackgroundBottom = BackgroundPrimary
internal val WalletSurface = BackgroundSecondary
internal val WalletSurfaceStrong = BackgroundTertiary
internal val WalletOutline = BorderDefault
internal val WalletTextPrimary = TextPrimary
internal val WalletTextSecondary = TextSecondary
internal val WalletTextTertiary = TextTertiary
internal val WalletDanger = Error
internal val WalletWarningSurface = Warning.copy(alpha = 0.14f)
internal val WalletPagePadding = 20.dp

internal data class WalletQuickAction(
    val label: String,
    val hint: String,
    val icon: ImageVector,
    val accent: Color,
    val onClick: () -> Unit,
)

internal data class WalletOverviewMetric(
    val label: String,
    val value: String,
)

@Composable
internal fun WalletPageBackdrop(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        WalletBackgroundTop,
                        WalletBackgroundBottom,
                    ),
                ),
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(WalletAccent.copy(alpha = 0.08f), Color.Transparent),
                        radius = 980f,
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Info.copy(alpha = 0.06f), Color.Transparent),
                        radius = 760f,
                    ),
                ),
        )
        content()
    }
}

@Composable
internal fun WalletGlassCard(
    modifier: Modifier = Modifier,
    accent: Color = WalletAccent,
    selected: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val borderColor = if (selected) {
        accent.copy(alpha = 0.92f)
    } else {
        WalletOutline
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = WalletSurface,
        border = BorderStroke(if (selected) 1.6.dp else 1.dp, borderColor),
        shadowElevation = if (selected) 6.dp else 0.dp,
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            accent.copy(alpha = 0.08f),
                            WalletSurface,
                            WalletSurfaceStrong,
                        ),
                    ),
                )
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = content,
        )
    }
}

@Composable
internal fun WalletBottomSheetCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        color = WalletSurface,
        border = BorderStroke(1.dp, WalletOutline),
        shadowElevation = 20.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(42.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(WalletOutline.copy(alpha = 0.9f)),
            )
            content()
        }
    }
}

@Composable
internal fun WalletSectionHeading(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = WalletTextPrimary,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = WalletTextSecondary,
        )
    }
}

@Composable
internal fun WalletMetricStrip(
    metrics: List<WalletOverviewMetric>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        metrics.forEach { metric ->
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                color = WalletSurfaceStrong,
                border = BorderStroke(1.dp, WalletOutline),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = metric.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = WalletTextTertiary,
                    )
                    Text(
                        text = metric.value,
                        style = MaterialTheme.typography.titleMedium,
                        color = WalletTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
internal fun WalletActionRow(
    actions: List<WalletQuickAction>,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(actions) { action ->
            WalletGlassCard(
                modifier = Modifier
                    .width(162.dp)
                    .clickable(onClick = action.onClick),
                accent = action.accent,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = action.accent.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, action.accent.copy(alpha = 0.28f)),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = action.icon,
                                contentDescription = action.label,
                                tint = action.accent,
                            )
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = action.label,
                            style = MaterialTheme.typography.titleSmall,
                            color = WalletTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = action.hint,
                            style = MaterialTheme.typography.bodySmall,
                            color = WalletTextSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun WalletTokenBadge(symbol: String, modifier: Modifier = Modifier) {
    val accent = walletAssetAccent(symbol)
    Surface(
        modifier = modifier.size(56.dp),
        shape = CircleShape,
        color = accent.copy(alpha = 0.18f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.3f)),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = symbol.take(1).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = accent,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
internal fun WalletTag(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color = WalletAccent,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = accent.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.24f)),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            style = MaterialTheme.typography.labelMedium,
            color = accent,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
internal fun WalletPrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(54.dp),
        enabled = enabled,
        shape = RoundedCornerShape(26.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = WalletAccent,
            contentColor = BackgroundDeepest,
            disabledContainerColor = WalletAccent.copy(alpha = 0.4f),
            disabledContentColor = BackgroundDeepest.copy(alpha = 0.6f),
        ),
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
        }
        if (icon != null) {
            Box(modifier = Modifier.width(8.dp))
        }
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

@Composable
internal fun WalletSecondaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(54.dp),
        enabled = enabled,
        shape = RoundedCornerShape(26.dp),
        border = BorderStroke(1.dp, WalletOutline),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = WalletSurfaceStrong,
            contentColor = WalletTextPrimary,
        ),
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
        }
        if (icon != null) {
            Box(modifier = Modifier.width(8.dp))
        }
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

@Composable
internal fun WalletToolbarIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = WalletTextPrimary,
) {
    Surface(
        modifier = modifier
            .size(44.dp)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = WalletSurface,
        border = BorderStroke(1.dp, WalletOutline),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
            )
        }
    }
}

@Composable
internal fun WalletSearchChrome(
    avatarLabel: String,
    onAvatarClick: () -> Unit,
    onSearchClick: () -> Unit,
    onTrailingClick: () -> Unit,
    trailingIcon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(
            modifier = Modifier
                .size(40.dp)
                .clickable(onClick = onAvatarClick),
            shape = CircleShape,
            color = WalletAccent.copy(alpha = 0.16f),
            border = BorderStroke(1.dp, WalletAccent.copy(alpha = 0.22f)),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = avatarLabel,
                    color = WalletAccent,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .clickable(onClick = onSearchClick),
            shape = RoundedCornerShape(22.dp),
            color = WalletSurface,
            border = BorderStroke(1.dp, WalletOutline),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = WalletTextTertiary,
                )
                Text(
                    text = "全局搜索",
                    style = MaterialTheme.typography.bodyMedium,
                    color = WalletTextTertiary,
                )
            }
        }
        WalletToolbarIconButton(
            icon = trailingIcon,
            contentDescription = "wallet toolbar action",
            onClick = onTrailingClick,
        )
    }
}

@Composable
internal fun WalletTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: ImageVector? = null,
    trailingDescription: String = "",
    onTrailingClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WalletToolbarIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "back",
            onClick = onBack,
        )
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge,
            color = WalletTextPrimary,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (trailingIcon != null) {
            WalletToolbarIconButton(
                icon = trailingIcon,
                contentDescription = trailingDescription,
                onClick = onTrailingClick,
            )
        } else {
            Box(modifier = Modifier.size(44.dp))
        }
    }
}

@Composable
internal fun WalletCloseBar(
    title: String? = null,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: ImageVector? = null,
    trailingDescription: String = "",
    onTrailingClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WalletToolbarIconButton(
            icon = Icons.Default.Close,
            contentDescription = "close",
            onClick = onClose,
        )
        if (title != null) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge,
                color = WalletTextPrimary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        } else {
            Box(modifier = Modifier.weight(1f))
        }
        if (trailingIcon != null) {
            WalletToolbarIconButton(
                icon = trailingIcon,
                contentDescription = trailingDescription,
                onClick = onTrailingClick,
            )
        } else {
            Box(modifier = Modifier.size(44.dp))
        }
    }
}

@Composable
internal fun WalletDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(WalletOutline.copy(alpha = 0.8f)),
    )
}

@Composable
internal fun WalletInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = WalletTextPrimary,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = WalletTextSecondary,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
internal fun WalletAssistPillButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .height(44.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = WalletAccent.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, WalletAccent.copy(alpha = 0.24f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = WalletTextPrimary,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = label,
                color = WalletTextPrimary,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
internal fun WalletInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 1,
    readOnly: Boolean = false,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        readOnly = readOnly,
        isError = isError,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = WalletTextPrimary),
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyLarge,
                color = WalletTextTertiary,
            )
        },
        leadingIcon = if (leadingIcon != null) {
            {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = WalletTextSecondary,
                )
            }
        } else {
            null
        },
        trailingIcon = trailingContent,
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = WalletSurface,
            unfocusedContainerColor = WalletSurface,
            disabledContainerColor = WalletSurface,
            focusedBorderColor = WalletAccent.copy(alpha = 0.62f),
            unfocusedBorderColor = WalletOutline,
            disabledBorderColor = WalletOutline,
            errorBorderColor = WalletDanger,
            cursorColor = WalletAccent,
            focusedTextColor = WalletTextPrimary,
            unfocusedTextColor = WalletTextPrimary,
            focusedPlaceholderColor = WalletTextTertiary,
            unfocusedPlaceholderColor = WalletTextTertiary,
            focusedLeadingIconColor = WalletTextSecondary,
            unfocusedLeadingIconColor = WalletTextSecondary,
            focusedTrailingIconColor = WalletTextSecondary,
            unfocusedTrailingIconColor = WalletTextSecondary,
        ),
    )
}

internal fun walletAssetAccent(symbol: String): Color {
    return when (symbol.uppercase()) {
        "USDT", "USDC", "USD24" -> USDTGreen
        "SOL" -> SolanaPurple
        "TRX", "TRON" -> TronRed
        "ETH" -> Info
        "BNB" -> Warning
        else -> WalletAccent
    }
}

internal fun walletNetworkLabel(symbol: String): String {
    return when (symbol.uppercase()) {
        "USD24" -> "Arbitrum"
        "USDT", "TRX", "TRON" -> "TRON"
        "SOL" -> "Solana"
        "ETH" -> "Ethereum"
        "BNB" -> "BNB Smart Chain"
        "MATIC" -> "Polygon"
        else -> "Mainnet"
    }
}

internal fun walletReferencePrice(symbol: String): String {
    return when (symbol.uppercase()) {
        "ETH" -> "$2,233.56"
        "SOL" -> "$175.80"
        "BNB" -> "$602.30"
        "USD24", "USDT", "USDC" -> "$1.00"
        else -> "--"
    }
}

internal fun walletReferenceChange(symbol: String): String {
    return when (symbol.uppercase()) {
        "ETH" -> "+5.68%"
        "SOL" -> "+3.40%"
        "BNB" -> "+1.22%"
        "USD24" -> "-0.08%"
        "USDT", "USDC" -> "0.00%"
        else -> "--"
    }
}

internal fun walletShortAddress(address: String): String {
    if (address.length <= 14) return address
    return "${address.take(8)}...${address.takeLast(6)}"
}
