package com.v2ray.ang.composeui.components.feature

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric
import com.v2ray.ang.composeui.components.app.TechScaffold
import com.v2ray.ang.composeui.components.buttons.GradientCTAButton
import com.v2ray.ang.composeui.components.buttons.SecondaryOutlineButton
import com.v2ray.ang.composeui.components.cards.GradientHeroCard
import com.v2ray.ang.composeui.components.cards.MiniMetricPill
import com.v2ray.ang.composeui.components.cards.TechCard
import com.v2ray.ang.composeui.components.inputs.GlassTextField
import com.v2ray.ang.composeui.components.navigation.CryptoVpnTopBar
import com.v2ray.ang.composeui.effects.MotionProfile
import com.v2ray.ang.composeui.p0.ui.P01BottomNav
import com.v2ray.ang.composeui.p0.ui.defaultP01Destinations
import com.v2ray.ang.composeui.theme.DividerLight
import com.v2ray.ang.composeui.theme.LayerWhite
import com.v2ray.ang.composeui.theme.TextMuted

@Deprecated("Freeze legacy template; avoid new dependencies.")
@Composable
fun FeaturePageTemplate(
    title: String,
    subtitle: String,
    badge: String,
    summary: String,
    heroAccent: String,
    metrics: List<FeatureMetric>,
    fields: List<FeatureField>,
    highlights: List<FeatureListItem>,
    checklist: List<FeatureBullet>,
    note: String,
    primaryActionLabel: String,
    secondaryActionLabel: String? = null,
    showBottomBar: Boolean = true,
    currentRoute: String = "",
    motionProfile: MotionProfile = MotionProfile.L1,
    onBottomNav: (String) -> Unit = {},
    onFieldChanged: (String, String) -> Unit = { _, _ -> },
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
) {
    val renderBottomBar = showBottomBar
    val hasHeroCopy = badge.isNotBlank() || summary.isNotBlank()
    val visibleMetrics = metrics.filter { it.label.isNotBlank() || it.value.isNotBlank() }
    val visibleFields = fields.filter {
        it.label.isNotBlank() || it.value.isNotBlank() || it.supportingText.isNotBlank()
    }
    val visibleHighlights = highlights.filter {
        it.title.isNotBlank() || it.subtitle.isNotBlank() || it.trailing.isNotBlank() || it.badge.isNotBlank()
    }
    val visibleChecklist = checklist.filter { it.title.isNotBlank() || it.detail.isNotBlank() }
    val hasNote = note.isNotBlank()
    TechScaffold(
        motionProfile = motionProfile,
        showNetwork = true,
        topBar = {
            CryptoVpnTopBar(
                title = title,
                subtitle = subtitle,
            )
        },
        bottomBar = {
            if (renderBottomBar) {
                P01BottomNav(
                    currentRoute = currentRoute,
                    destinations = defaultP01Destinations(),
                    onNavigate = onBottomNav,
                )
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            if (hasHeroCopy) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    GradientHeroCard(
                        title = badge,
                        value = title,
                        subtitle = summary,
                        accent = heroAccent,
                    )
                }
            }

            if (visibleMetrics.isNotEmpty()) {
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 0.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(visibleMetrics) { metric ->
                            MiniMetricPill(
                                label = metric.label,
                                value = metric.value,
                            )
                        }
                    }
                }
            }

            if (visibleFields.isNotEmpty()) {
                item {
                    TechCard {
                        Text("输入信息", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            visibleFields.forEach { field ->
                                GlassTextField(
                                    value = field.value,
                                    label = field.label,
                                    onValueChange = { onFieldChanged(field.key, it) },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = field.placeholder,
                                )
                                if (field.supportingText.isNotBlank()) {
                                    Text(
                                        text = field.supportingText,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (visibleHighlights.isNotEmpty()) {
                item {
                    Text("关键信息", style = MaterialTheme.typography.titleLarge)
                }
                items(visibleHighlights) { item ->
                    FeatureListItemRow(item = item)
                }
            }

            if (visibleChecklist.isNotEmpty()) {
                item {
                    TechCard {
                        Text("状态说明", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            visibleChecklist.forEach { bullet ->
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = bullet.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                    if (bullet.detail.isNotBlank()) {
                                        Text(
                                            text = bullet.detail,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextMuted,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (hasNote) {
                item {
                    TechCard {
                        Text("补充说明", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = note,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                        )
                    }
                }
            }

            item {
                GradientCTAButton(
                    text = primaryActionLabel,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onPrimaryAction,
                )
            }

            if (!secondaryActionLabel.isNullOrBlank() && onSecondaryAction != null) {
                item {
                    SecondaryOutlineButton(
                        onClick = onSecondaryAction,
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(secondaryActionLabel)
                        },
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(if (renderBottomBar) 110.dp else 24.dp))
            }
        }
    }
}

@Composable
private fun FeatureListItemRow(
    item: FeatureListItem,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = LayerWhite.copy(alpha = 0.88f),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, DividerLight),
        tonalElevation = 0.dp,
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (item.subtitle.isNotBlank()) {
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.widthIn(max = 168.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (item.trailing.isNotBlank()) {
                    Text(
                        text = item.trailing,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End,
                    )
                }
                if (item.badge.isNotBlank()) {
                    Text(
                        text = item.badge,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End,
                    )
                }
            }
        }
    }
}
