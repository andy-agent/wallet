package com.app.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.platform.LocalConfiguration

@Immutable
sealed interface WindowAdaptive {
    data object Compact : WindowAdaptive
    data object Medium : WindowAdaptive
    data object Expanded : WindowAdaptive
}

@Composable
fun rememberWindowAdaptive(): WindowAdaptive {
    val widthDp = LocalConfiguration.current.screenWidthDp
    return when {
        widthDp >= 840 -> WindowAdaptive.Expanded
        widthDp >= 600 -> WindowAdaptive.Medium
        else -> WindowAdaptive.Compact
    }
}
