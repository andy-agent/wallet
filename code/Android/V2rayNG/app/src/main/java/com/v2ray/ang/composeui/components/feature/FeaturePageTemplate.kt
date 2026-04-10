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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.v2ray.ang.composeui.components.navigation.CryptoVpnBottomBar
import com.v2ray.ang.composeui.components.navigation.CryptoVpnTopBar
import com.v2ray.ang.composeui.effects.MotionProfile
import com.v2ray.ang.composeui.theme.DividerLight
import com.v2ray.ang.composeui.theme.LayerWhite
import com.v2ray.ang.composeui.theme.TextMuted

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
    showBottomBar: Boolean = false,
    currentRoute: String = "",
    motionProfile: MotionProfile = MotionProfile.L1,
    onBottomNav: (String) -> Unit = {},
    onFieldChanged: (String, String) -> Unit = { _, _ -> },
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
) {
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
            if (showBottomBar) {
                CryptoVpnBottomBar(
                    currentRoute = currentRoute,
                    onRouteSelected = onBottomNav,
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
            item {
                Spacer(modifier = Modifier.height(8.dp))
                GradientHeroCard(
                    title = badge,
                    value = title,
                    subtitle = summary,
                    accent = heroAccent,
                )
            }

            if (metrics.isNotEmpty()) {
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 0.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(metrics) { metric ->
                            MiniMetricPill(
                                label = metric.label,
                                value = metric.value,
                            )
                        }
                    }
                }
            }

            if (fields.isNotEmpty()) {
                item {
                    TechCard {
                        Text("表单占位", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            fields.forEach { field ->
                                GlassTextField(
                                    value = field.value,
                                    label = field.label,
                                    onValueChange = { onFieldChanged(field.key, it) },
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

            if (highlights.isNotEmpty()) {
                item {
                    Text("关键模块", style = MaterialTheme.typography.titleLarge)
                }
                items(highlights) { item ->
                    FeatureListItemRow(item = item)
                }
            }

            if (checklist.isNotEmpty()) {
                item {
                    TechCard {
                        Text("交付检查", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            checklist.forEach { bullet ->
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

            item {
                TechCard {
                    Text("交付备注", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = note,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                    )
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
                Spacer(modifier = Modifier.height(if (showBottomBar) 110.dp else 24.dp))
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
                )
                if (item.subtitle.isNotBlank()) {
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (item.trailing.isNotBlank()) {
                    Text(
                        text = item.trailing,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                if (item.badge.isNotBlank()) {
                    Text(
                        text = item.badge,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                    )
                }
            }
        }
    }
}
