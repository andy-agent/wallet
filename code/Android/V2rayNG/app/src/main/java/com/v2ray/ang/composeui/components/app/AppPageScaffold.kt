package com.v2ray.ang.composeui.components.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.v2ray.ang.composeui.effects.MotionProfile
import com.v2ray.ang.composeui.theme.AppTheme

@Composable
fun AppPageScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    motionProfile: MotionProfile = MotionProfile.L1,
    showNetwork: Boolean = true,
    scrollable: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = AppTheme.spacing.pageHorizontal,
        vertical = AppTheme.spacing.sectionGap,
    ),
    content: @Composable ColumnScope.() -> Unit,
) {
    TechScaffold(
        modifier = modifier,
        motionProfile = motionProfile,
        showNetwork = showNetwork,
        topBar = topBar,
        bottomBar = bottomBar,
    ) { padding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(contentPadding)
            .then(
                if (scrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier,
            )

        Column(
            modifier = contentModifier,
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sectionGap),
            content = content,
        )
    }
}
