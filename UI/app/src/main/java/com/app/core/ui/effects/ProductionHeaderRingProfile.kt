package com.app.core.ui.effects

object ProductionHeaderRingProfile {
    val preset = HeaderRingPreset.R3
    val glyph = preset.glyph
    val enabledLayers = preset.layers - HeaderRingLayer.InnerRing - HeaderRingLayer.OrbitSweep
}
