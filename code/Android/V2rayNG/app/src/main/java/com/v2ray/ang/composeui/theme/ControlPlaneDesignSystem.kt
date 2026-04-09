package com.v2ray.ang.composeui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class ControlPlaneIntent {
    Infra,
    Settlement,
    Finance,
    Neutral,
}

enum class AuditState {
    Ok,
    Warn,
    Critical,
    Unknown,
}

enum class ControlPlaneLayer {
    Level0,
    Level1,
    Level2,
    Level3,
}

@Immutable
data class AccentPalette(
    val accent: Color,
    val onAccent: Color,
    val container: Color,
    val onContainer: Color,
    val border: Color,
)

@Immutable
data class AuditPalette(
    val accent: Color,
    val container: Color,
    val onContainer: Color,
    val border: Color,
    val emphasis: Color,
)

@Immutable
data class LayerPalette(
    val container: Color,
    val outline: Color,
    val shadowElevation: Dp,
    val tonalElevation: Dp,
)

@Immutable
data class MotionToken(
    val durationMillis: Int,
    val delayMillis: Int = 0,
    val easing: Easing,
)

@Immutable
data class ControlPlaneMotionPalette(
    val stateChange: MotionToken,
    val screenEnter: MotionToken,
    val emphasis: MotionToken,
    val settlementConfirmation: MotionToken,
    val maxGlowAlpha: Float,
    val maxLiveEffectsPerScreen: Int,
)

object ControlPlaneTokens {
    val Ink = Color(0xFF162231)
    val InkSecondary = Color(0xFF536375)
    val InkTertiary = Color(0xFF7C8A99)
    val InkDisabled = Color(0xFFA8B2BF)

    val Infra = AccentPalette(
        accent = Color(0xFF5F7FA6),
        onAccent = Color.White,
        container = Color(0xFFE9F1F9),
        onContainer = Ink,
        border = Color(0xFFBACBDF),
    )
    val Settlement = AccentPalette(
        accent = Color(0xFF4E8872),
        onAccent = Color.White,
        container = Color(0xFFEAF5F0),
        onContainer = Ink,
        border = Color(0xFFB8D4C6),
    )
    val Finance = AccentPalette(
        accent = Color(0xFF786EAD),
        onAccent = Color.White,
        container = Color(0xFFF0ECF9),
        onContainer = Ink,
        border = Color(0xFFC8C0E4),
    )
    val Neutral = AccentPalette(
        accent = Color(0xFF8D98A6),
        onAccent = Color.White,
        container = Color(0xFFF2F4F7),
        onContainer = Ink,
        border = Color(0xFFD6DDE5),
    )

    private val AuditOk = AuditPalette(
        accent = Settlement.accent,
        container = Settlement.container,
        onContainer = Ink,
        border = Settlement.border,
        emphasis = Color(0x144E8872),
    )
    val Warning = AuditPalette(
        accent = Color(0xFFC5893D),
        container = Color(0xFFFCF3E7),
        onContainer = Ink,
        border = Color(0xFFE1C28D),
        emphasis = Color(0x14C5893D),
    )
    val Critical = AuditPalette(
        accent = Color(0xFFC05A5A),
        container = Color(0xFFFBEDED),
        onContainer = Ink,
        border = Color(0xFFE4B4B4),
        emphasis = Color(0x14C05A5A),
    )
    private val AuditUnknown = AuditPalette(
        accent = Neutral.accent,
        container = Neutral.container,
        onContainer = InkSecondary,
        border = Neutral.border,
        emphasis = Color(0x148D98A6),
    )

    private val Layer0 = LayerPalette(
        container = Color(0xFFFFFFFF),
        outline = Color(0xFFE2EAF2),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    )
    private val Layer1 = LayerPalette(
        container = Color(0xFFFBFCFE),
        outline = Color(0xFFDCE5EE),
        shadowElevation = 2.dp,
        tonalElevation = 0.dp,
    )
    private val Layer2 = LayerPalette(
        container = Color(0xFFF6F9FC),
        outline = Color(0xFFD4DFEA),
        shadowElevation = 6.dp,
        tonalElevation = 1.dp,
    )
    private val Layer3 = LayerPalette(
        container = Color(0xFFF2F6FA),
        outline = Color(0xFFCAD8E5),
        shadowElevation = 10.dp,
        tonalElevation = 2.dp,
    )

    private val StandardEasing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)
    private val GentleEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

    val Motion = ControlPlaneMotionPalette(
        stateChange = MotionToken(durationMillis = 180, easing = StandardEasing),
        screenEnter = MotionToken(durationMillis = 260, easing = GentleEasing),
        emphasis = MotionToken(durationMillis = 220, easing = StandardEasing),
        settlementConfirmation = MotionToken(
            durationMillis = 320,
            delayMillis = 40,
            easing = GentleEasing,
        ),
        maxGlowAlpha = 0.08f,
        maxLiveEffectsPerScreen = 1,
    )

    val materialColorScheme: ColorScheme = lightColorScheme(
        primary = Infra.accent,
        onPrimary = Infra.onAccent,
        primaryContainer = Infra.container,
        onPrimaryContainer = Infra.onContainer,
        secondary = Settlement.accent,
        onSecondary = Settlement.onAccent,
        secondaryContainer = Settlement.container,
        onSecondaryContainer = Settlement.onContainer,
        tertiary = Finance.accent,
        onTertiary = Finance.onAccent,
        tertiaryContainer = Finance.container,
        onTertiaryContainer = Finance.onContainer,
        error = Critical.accent,
        onError = Color.White,
        errorContainer = Critical.container,
        onErrorContainer = Critical.onContainer,
        background = Layer0.container,
        onBackground = Ink,
        surface = Layer1.container,
        onSurface = Ink,
        surfaceVariant = Layer2.container,
        onSurfaceVariant = InkSecondary,
        outline = Layer1.outline,
        outlineVariant = Layer2.outline,
        scrim = Ink.copy(alpha = 0.18f),
    )

    fun intent(intent: ControlPlaneIntent): AccentPalette = when (intent) {
        ControlPlaneIntent.Infra -> Infra
        ControlPlaneIntent.Settlement -> Settlement
        ControlPlaneIntent.Finance -> Finance
        ControlPlaneIntent.Neutral -> Neutral
    }

    fun audit(state: AuditState): AuditPalette = when (state) {
        AuditState.Ok -> AuditOk
        AuditState.Warn -> Warning
        AuditState.Critical -> Critical
        AuditState.Unknown -> AuditUnknown
    }

    fun layer(layer: ControlPlaneLayer): LayerPalette = when (layer) {
        ControlPlaneLayer.Level0 -> Layer0
        ControlPlaneLayer.Level1 -> Layer1
        ControlPlaneLayer.Level2 -> Layer2
        ControlPlaneLayer.Level3 -> Layer3
    }
}
