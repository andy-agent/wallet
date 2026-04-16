package com.v2ray.ang.composeui.components.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import com.v2ray.ang.composeui.effects.MotionProfile
import com.v2ray.ang.composeui.effects.TechParticleBackground
import com.v2ray.ang.composeui.theme.AppTheme

enum class AppPageBackgroundStyle {
    None,
    Tech,
    Hero,
}

@Composable
fun AppPageScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    backgroundStyle: AppPageBackgroundStyle = AppPageBackgroundStyle.None,
    contentWindowInsets: WindowInsets = WindowInsets.safeDrawing,
    motionProfile: MotionProfile = MotionProfile.L1,
    showNetwork: Boolean = true,
    scrollable: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = AppTheme.spacing.pageHorizontal,
        vertical = AppTheme.spacing.sectionGap,
    ),
    background: (@Composable () -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (backgroundStyle) {
            AppPageBackgroundStyle.None -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppTheme.colors.bgApp),
            )

            AppPageBackgroundStyle.Tech -> TechParticleBackground(
                motionProfile = motionProfile,
                modifier = Modifier.fillMaxSize(),
                showNetwork = showNetwork,
            )

            AppPageBackgroundStyle.Hero -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(AppTheme.colors.bgApp, AppTheme.colors.bgSubtle),
                        ),
                    ),
            )
        }
        background?.invoke()
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            topBar = topBar,
            bottomBar = bottomBar,
            containerColor = Color.Transparent,
            contentWindowInsets = contentWindowInsets,
        ) { scaffoldPadding ->
            val mergedPadding = PaddingValues(
                start = scaffoldPadding.calculateLeftPadding(LayoutDirection.Ltr) + contentPadding.calculateLeftPadding(LayoutDirection.Ltr),
                top = scaffoldPadding.calculateTopPadding() + contentPadding.calculateTopPadding(),
                end = scaffoldPadding.calculateRightPadding(LayoutDirection.Ltr) + contentPadding.calculateRightPadding(LayoutDirection.Ltr),
                bottom = scaffoldPadding.calculateBottomPadding() + contentPadding.calculateBottomPadding(),
            )
            if (scrollable) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(mergedPadding)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sectionGap),
                ) {
                    content(PaddingValues())
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(mergedPadding),
                ) {
                    content(PaddingValues())
                }
            }
        }
    }
}
