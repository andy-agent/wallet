package com.v2ray.ang.composeui.theme

import androidx.compose.ui.graphics.Color
import com.v2ray.ang.composeui.theme.tokens.ColorTokens

val AppWhite: Color
    get() = ColorTokens.light.bgApp

val LayerWhite: Color
    get() = ColorTokens.light.surfaceCard

val SurfaceCloud: Color
    get() = ColorTokens.light.bgSubtle

val SurfaceGlaze: Color
    get() = ColorTokens.light.infoBg

val StrokeSoft: Color
    get() = ColorTokens.light.dividerSubtle

val ElectricBlue: Color
    get() = ColorTokens.light.brandPrimary

val ElectricCyan: Color
    get() = ColorTokens.light.brandSecondary

val AuroraPurple: Color
    get() = ColorTokens.light.accentPurple

val SignalGreen: Color
    get() = ColorTokens.light.success

val WarningAmber: Color
    get() = ColorTokens.light.warning

val DangerRed: Color
    get() = ColorTokens.light.error

val TextStrong: Color
    get() = ColorTokens.light.textPrimary

val TextBody: Color
    get() = ColorTokens.light.textSecondary

val TextMuted: Color
    get() = ColorTokens.light.textSecondary

val TextSoft: Color
    get() = ColorTokens.light.textTertiary

val ShadowBlue: Color
    get() = ColorTokens.light.shadowColor

val GlowBlue: Color
    get() = ColorTokens.light.brandPrimary.copy(alpha = 0.20f)

val GlowCyan: Color
    get() = ColorTokens.light.brandSecondary.copy(alpha = 0.20f)

val GlowPurple: Color
    get() = ColorTokens.light.accentPurple.copy(alpha = 0.20f)

val SuccessTint: Color
    get() = ColorTokens.light.successBg

val WarningTint: Color
    get() = ColorTokens.light.warningBg

val DangerTint: Color
    get() = ColorTokens.light.errorBg

val InfoTint: Color
    get() = ColorTokens.light.infoBg

val DividerLight: Color
    get() = ColorTokens.light.borderSubtle

val NavInactive: Color
    get() = ColorTokens.light.navInactive

val ScreenStroke: Color
    get() = ColorTokens.light.dividerSubtle
