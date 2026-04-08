package com.v2ray.ang.composeui.components.shell

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v2ray.ang.composeui.theme.AppShape
import com.v2ray.ang.composeui.theme.BackgroundDeepest
import com.v2ray.ang.composeui.theme.Error
import com.v2ray.ang.composeui.theme.TextPrimary
import com.v2ray.ang.composeui.theme.TextSecondary
import com.v2ray.ang.composeui.theme.TextTertiary

private val HomeTopBarSurface = Color(0xFF151C1F)
private val HomeCardSurface = Color(0xFF13191D)
private val HomeCardSurfaceStrong = Color(0xFF1A2125)
private val HomeCardBorder = Color(0xFF1C272C)
private val HomeListDivider = Color(0xFF1A2328)
private val HomeAccent = Color(0xFF1DD6F1)
private val HomeAccentSoft = Color(0x331DD6F1)
private val HomePromoTop = Color(0xFF3A2A1C)
private val HomePromoBottom = Color(0xFFDB7D4D)
private val HomeHeroTop = Color(0xFF10345B)
private val HomeHeroBottom = Color(0xFF0C1A1E)

data class BitgetHomeQuickLink(
    val label: String,
    val icon: ImageVector,
    val accentColor: Color,
    val onClick: () -> Unit,
)

data class BitgetHomeHighlightCard(
    val metric: String,
    val unit: String,
    val title: String,
    val subtitle: String,
    val badge: String,
    val accentColor: Color,
    val onClick: () -> Unit,
)

data class BitgetHomeListEntry(
    val badge: String,
    val title: String,
    val subtitle: String,
    val metric: String,
    val metricLabel: String,
    val accentColor: Color,
    val onClick: () -> Unit,
)

data class BitgetHomeSheetAction(
    val label: String,
    val icon: ImageVector,
    val accentColor: Color,
    val onClick: () -> Unit,
)

@Composable
fun BitgetHomeTopBar(
    onAvatarClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFF38BC7), Color(0xFF8D73FF)),
                    ),
                )
                .clickable(onClick = onAvatarClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "B",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Row(
            modifier = Modifier
                .weight(1f)
                .height(42.dp)
                .clip(AppShape.ButtonFull)
                .background(HomeTopBarSurface)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = TextTertiary,
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "全局搜索",
                style = MaterialTheme.typography.bodyMedium,
                color = TextTertiary,
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(HomeTopBarSurface)
                .border(1.dp, HomeCardBorder, CircleShape)
                .clickable(onClick = onProfileClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = TextPrimary,
            )
        }
    }
}

@Composable
fun BitgetHomePortfolioHero(
    balance: String,
    changeText: String,
    changeLabel: String,
    primaryActionLabel: String,
    onPrimaryAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = balance,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 42.sp,
                    lineHeight = 44.sp,
                ),
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = changeText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (changeText.startsWith("-")) Error else HomeAccent,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = changeLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }
        }

        Button(
            onClick = onPrimaryAction,
            shape = AppShape.ButtonFull,
            colors = ButtonDefaults.buttonColors(
                containerColor = HomeAccent,
                contentColor = BackgroundDeepest,
            ),
            contentPadding = ButtonDefaults.ContentPadding,
        ) {
            Text(
                text = primaryActionLabel,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 6.dp),
            )
        }
    }
}

@Composable
fun BitgetHomeQuickLinks(
    actions: List<BitgetHomeQuickLink>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        actions.forEach { action ->
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = action.onClick),
                shape = AppShape.ButtonFull,
                color = HomeCardSurface,
                border = BorderStroke(1.dp, HomeCardBorder),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(action.accentColor.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = action.icon,
                            contentDescription = action.label,
                            tint = action.accentColor,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = action.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = TextSecondary,
                    )
                }
            }
        }
    }
}

@Composable
fun BitgetHomeCampaignCard(
    eyebrow: String,
    title: String,
    description: String,
    actionLabel: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        color = Color.Transparent,
        shadowElevation = 10.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(316.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(HomePromoTop, HomePromoBottom),
                    ),
                ),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 18.dp, end = 18.dp)
                    .size(width = 170.dp, height = 126.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.20f),
                                Color.Transparent,
                            ),
                        ),
                    ),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 34.dp, end = 40.dp)
                    .size(118.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.28f),
                                Color.Transparent,
                            ),
                        ),
                    ),
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Surface(
                    shape = AppShape.TagPill,
                    color = Color.Black.copy(alpha = 0.18f),
                ) {
                    Text(
                        text = eyebrow,
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 20.sp,
                            lineHeight = 28.sp,
                        ),
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary.copy(alpha = 0.88f),
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = onActionClick,
                        shape = AppShape.ButtonFull,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF7D27A),
                            contentColor = Color(0xFF4B2B00),
                        ),
                    ) {
                        Text(
                            text = actionLabel,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BitgetHomeOperationsHero(
    eyebrow: String,
    title: String,
    infoLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(30.dp),
        color = Color.Transparent,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(HomeHeroTop, HomeHeroBottom),
                    ),
                ),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 26.dp, end = 22.dp)
                    .size(136.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(HomeAccent.copy(alpha = 0.44f), Color.Transparent),
                        ),
                    ),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 58.dp, end = 52.dp)
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(HomeAccent, Color(0xFF5CF7FF)),
                        ),
                    ),
                    contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "S",
                    style = MaterialTheme.typography.headlineMedium,
                    color = BackgroundDeepest,
                    fontWeight = FontWeight.Bold,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(22.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = eyebrow,
                        style = MaterialTheme.typography.labelLarge,
                        color = HomeAccent,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 22.sp,
                            lineHeight = 30.sp,
                        ),
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Surface(
                    shape = AppShape.ButtonFull,
                    color = Color(0xFF0F2D4D),
                    border = BorderStroke(1.dp, HomeAccentSoft),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(HomeAccent.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = HomeAccent,
                                modifier = Modifier.size(11.dp),
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = infoLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            color = HomeAccent,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BitgetHomeHighlightsRow(
    cards: List<BitgetHomeHighlightCard>,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        items(cards) { card ->
            Surface(
                modifier = Modifier
                    .width(228.dp)
                    .clickable(onClick = card.onClick),
                shape = RoundedCornerShape(28.dp),
                color = HomeCardSurface,
                border = BorderStroke(1.dp, HomeCardBorder),
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = card.metric,
                                style = MaterialTheme.typography.headlineSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = card.unit,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(card.accentColor.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = card.badge,
                                style = MaterialTheme.typography.labelLarge,
                                color = card.accentColor,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = card.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary,
                        )
                        Text(
                            text = card.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BitgetHomeListCard(
    title: String,
    entries: List<BitgetHomeListEntry>,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = HomeCardSurfaceStrong,
        border = BorderStroke(1.dp, HomeCardBorder),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 20.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(10.dp))

            entries.forEachIndexed { index, entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable(onClick = entry.onClick)
                        .padding(vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(entry.accentColor.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = entry.badge,
                            style = MaterialTheme.typography.labelLarge,
                            color = entry.accentColor,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = entry.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = entry.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary,
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = entry.metric,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = entry.metricLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary,
                        )
                    }
                }

                if (index != entries.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(HomeListDivider),
                    )
                }
            }
        }
    }
}

@Composable
fun BitgetHomeActionsSheet(
    visible: Boolean,
    popularActions: List<BitgetHomeSheetAction>,
    assetActions: List<BitgetHomeSheetAction>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!visible) return

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.54f)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onDismiss),
        )

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding(),
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            color = HomeCardSurface,
            border = BorderStroke(1.dp, HomeCardBorder),
            shadowElevation = 18.dp,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(42.dp)
                        .height(5.dp)
                        .clip(AppShape.TagPill)
                        .background(Color.White.copy(alpha = 0.46f)),
                )

                BitgetHomeSheetSection(
                    title = "热门功能",
                    actions = popularActions,
                )
                BitgetHomeSheetSection(
                    title = "资产管理",
                    actions = assetActions,
                )
            }
        }
    }
}

@Composable
private fun BitgetHomeSheetSection(
    title: String,
    actions: List<BitgetHomeSheetAction>,
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
        )

        actions.chunked(4).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                rowItems.forEach { action ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = action.onClick),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(66.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(HomeCardSurfaceStrong)
                                .border(
                                    width = 1.dp,
                                    color = HomeCardBorder,
                                    shape = RoundedCornerShape(22.dp),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = action.icon,
                                contentDescription = action.label,
                                tint = action.accentColor,
                                modifier = Modifier.size(26.dp),
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = action.label,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                repeat(4 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
