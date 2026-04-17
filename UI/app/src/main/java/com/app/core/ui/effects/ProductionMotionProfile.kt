package com.app.core.ui.effects

data class MotionProfile(
    val preset: EffectLabPreset,
    val enabled: Set<EffectToggle>,
)

object ProductionMotionProfile {
    private val basePreset = EffectLabPreset.P3

    val current = MotionProfile(
        preset = basePreset,
        enabled = basePreset.enabled,
    )

    val particleCount: Int = current.preset.particleCount
    val orbitDurationMs: Int = current.preset.orbitDurationMs

    fun isEnabled(toggle: EffectToggle): Boolean = toggle in current.enabled
}
