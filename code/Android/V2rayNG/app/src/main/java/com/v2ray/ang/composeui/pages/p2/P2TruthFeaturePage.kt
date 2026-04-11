package com.v2ray.ang.composeui.pages.p2

import androidx.compose.runtime.Composable
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric
import com.v2ray.ang.composeui.components.feature.FeaturePageTemplate
import com.v2ray.ang.composeui.effects.MotionProfile

internal data class P2TruthState(
    val badge: String,
    val summary: String,
    val note: String,
    val checklist: List<FeatureBullet>,
    val primaryActionLabel: String,
    val secondaryActionLabel: String?,
)

internal fun buildP2TruthState(
    badge: String,
    summary: String,
    note: String,
    checklist: List<FeatureBullet>,
    isLoading: Boolean,
    errorMessage: String?,
    emptyMessage: String?,
    blockerTitle: String?,
    blockerMessage: String?,
    primaryActionLabel: String?,
    secondaryActionLabel: String?,
): P2TruthState {
    val stateTitle = when {
        isLoading -> "加载中"
        !errorMessage.isNullOrBlank() -> "加载失败"
        !blockerTitle.isNullOrBlank() -> blockerTitle
        !emptyMessage.isNullOrBlank() -> badge.ifBlank { "空态" }
        else -> badge.ifBlank { "真实数据" }
    }
    val stateSummary = when {
        isLoading -> "正在加载当前页面的真实数据与状态。"
        !errorMessage.isNullOrBlank() -> errorMessage
        !emptyMessage.isNullOrBlank() -> emptyMessage
        !blockerMessage.isNullOrBlank() -> blockerMessage
        else -> summary
    }
    val stateNote = when {
        !blockerMessage.isNullOrBlank() && blockerMessage != stateSummary -> blockerMessage
        note.isNotBlank() -> note
        else -> stateSummary
    }
    val stateChecklist = if (checklist.isNotEmpty()) {
        checklist
    } else {
        listOf(FeatureBullet("状态", stateSummary))
    }
    val actionsEnabled = !isLoading && errorMessage.isNullOrBlank()

    return P2TruthState(
        badge = stateTitle,
        summary = stateSummary,
        note = stateNote,
        checklist = stateChecklist,
        primaryActionLabel = primaryActionLabel.takeIf { actionsEnabled && !it.isNullOrBlank() }.orEmpty(),
        secondaryActionLabel = secondaryActionLabel.takeIf { actionsEnabled && !it.isNullOrBlank() },
    )
}

@Composable
internal fun P2TruthFeaturePage(
    title: String,
    subtitle: String,
    heroAccent: String,
    metrics: List<FeatureMetric>,
    fields: List<FeatureField>,
    highlights: List<FeatureListItem>,
    truthState: P2TruthState,
    currentRoute: String,
    onBottomNav: (String) -> Unit,
    onFieldChanged: (String, String) -> Unit = { _, _ -> },
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
) {
    FeaturePageTemplate(
        title = title,
        subtitle = subtitle,
        badge = truthState.badge,
        summary = truthState.summary,
        heroAccent = heroAccent,
        metrics = metrics,
        fields = fields,
        highlights = highlights,
        checklist = truthState.checklist,
        note = truthState.note,
        primaryActionLabel = truthState.primaryActionLabel,
        secondaryActionLabel = truthState.secondaryActionLabel,
        showBottomBar = true,
        currentRoute = currentRoute,
        motionProfile = MotionProfile.L2,
        onBottomNav = onBottomNav,
        onFieldChanged = onFieldChanged,
        onPrimaryAction = onPrimaryAction,
        onSecondaryAction = onSecondaryAction,
    )
}
