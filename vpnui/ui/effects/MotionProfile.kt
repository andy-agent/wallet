package com.cryptovpn.ui.effects

enum class MotionProfile(
    val particleCount: Int,
    val networkAlpha: Float,
    val glowAlpha: Float,
    val orbitDurationMs: Int,
    val pulseDurationMs: Int,
) {
    L1(
        particleCount = 16,
        networkAlpha = 0.18f,
        glowAlpha = 0.18f,
        orbitDurationMs = 18000,
        pulseDurationMs = 2400,
    ),
    L2(
        particleCount = 28,
        networkAlpha = 0.26f,
        glowAlpha = 0.26f,
        orbitDurationMs = 12000,
        pulseDurationMs = 1800,
    ),
    L3(
        particleCount = 46,
        networkAlpha = 0.36f,
        glowAlpha = 0.34f,
        orbitDurationMs = 8000,
        pulseDurationMs = 1300,
    ),
}
