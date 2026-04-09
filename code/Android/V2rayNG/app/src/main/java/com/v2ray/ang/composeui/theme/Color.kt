package com.v2ray.ang.composeui.theme

import androidx.compose.ui.graphics.Color

// White-base financial control plane colors
val BackgroundDeepest = ControlPlaneTokens.Ink
val BackgroundPrimary = ControlPlaneTokens.layer(ControlPlaneLayer.Level0).container
val BackgroundSecondary = ControlPlaneTokens.layer(ControlPlaneLayer.Level1).container
val BackgroundTertiary = ControlPlaneTokens.layer(ControlPlaneLayer.Level2).container
val BackgroundOverlay = ControlPlaneTokens.layer(ControlPlaneLayer.Level3).container

// Primary system semantics
val Primary = ControlPlaneTokens.Infra.accent
val PrimaryHover = Color(0xFF4F6D91)
val PrimaryDisabled = Primary.copy(alpha = 0.36f)
val PrimaryContainerTint = ControlPlaneTokens.Infra.container

// Functional colors aligned to explicit audit semantics
val Success = ControlPlaneTokens.Settlement.accent
val Warning = ControlPlaneTokens.Warning.accent
val Error = ControlPlaneTokens.Critical.accent
val Info = ControlPlaneTokens.Infra.accent

// VPN status colors
val VPNConnected = Success
val VPNConnecting = Info
val VPNDisconnected = ControlPlaneTokens.Neutral.accent

// Text colors
val TextPrimary = ControlPlaneTokens.Ink
val TextSecondary = ControlPlaneTokens.InkSecondary
val TextTertiary = ControlPlaneTokens.InkTertiary
val TextDisabled = ControlPlaneTokens.InkDisabled

// Border & divider colors
val BorderDefault = ControlPlaneTokens.layer(ControlPlaneLayer.Level1).outline
val BorderFocus = Primary
val BorderError = Error
val DividerColor = ControlPlaneTokens.layer(ControlPlaneLayer.Level2).outline

// Accent surfaces
val AccentSurface = ControlPlaneTokens.Infra.container
val AccentSurfaceStrong = Color(0xFFE2ECF7)
val AccentSurfaceInfo = ControlPlaneTokens.Infra.container
val AccentSurfaceWarm = ControlPlaneTokens.Warning.container

// Chain Colors
val SolanaPurple = ControlPlaneTokens.Finance.accent
val TronRed = Color(0xFFFF060A)
val USDTGreen = Color(0xFF26A17B)

// Low-alpha emphasis washes used instead of cinematic neon
val GlowBlue = ControlPlaneTokens.Infra.accent.copy(alpha = ControlPlaneTokens.Motion.maxGlowAlpha)
val GlowGreen =
    ControlPlaneTokens.Settlement.accent.copy(alpha = ControlPlaneTokens.Motion.maxGlowAlpha)
val GlowYellow =
    ControlPlaneTokens.Warning.accent.copy(alpha = ControlPlaneTokens.Motion.maxGlowAlpha)

// Legacy color aliases for backward compatibility
val TextPrimaryWhite = TextPrimary
val TextSecondaryGray = TextSecondary
val TextTertiaryGray = TextTertiary
