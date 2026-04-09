package com.v2ray.ang.composeui.theme

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ControlPlaneDesignSystemTest {

    @Test
    fun `audit states stay aligned with control plane semantics`() {
        assertEquals(
            ControlPlaneTokens.Settlement.accent,
            ControlPlaneTokens.audit(AuditState.Ok).accent,
        )
        assertEquals(
            ControlPlaneTokens.Warning.accent,
            ControlPlaneTokens.audit(AuditState.Warn).accent,
        )
        assertEquals(
            ControlPlaneTokens.Critical.accent,
            ControlPlaneTokens.audit(AuditState.Critical).accent,
        )
        assertEquals(
            ControlPlaneTokens.Neutral.accent,
            ControlPlaneTokens.audit(AuditState.Unknown).accent,
        )
    }

    @Test
    fun `layer depths become more elevated as emphasis increases`() {
        val layer0 = ControlPlaneTokens.layer(ControlPlaneLayer.Level0)
        val layer1 = ControlPlaneTokens.layer(ControlPlaneLayer.Level1)
        val layer2 = ControlPlaneTokens.layer(ControlPlaneLayer.Level2)
        val layer3 = ControlPlaneTokens.layer(ControlPlaneLayer.Level3)

        assertEquals(0.dp, layer0.shadowElevation)
        assertTrue(layer1.shadowElevation > layer0.shadowElevation)
        assertTrue(layer2.shadowElevation > layer1.shadowElevation)
        assertTrue(layer3.shadowElevation > layer2.shadowElevation)
    }

    @Test
    fun `material scheme keeps the frozen white base and semantic accents`() {
        assertEquals(
            ControlPlaneTokens.layer(ControlPlaneLayer.Level0).container,
            ControlPlaneTokens.materialColorScheme.background,
        )
        assertEquals(ControlPlaneTokens.Infra.accent, ControlPlaneTokens.materialColorScheme.primary)
        assertEquals(
            ControlPlaneTokens.Settlement.accent,
            ControlPlaneTokens.materialColorScheme.secondary,
        )
        assertEquals(ControlPlaneTokens.Finance.accent, ControlPlaneTokens.materialColorScheme.tertiary)
    }
}
