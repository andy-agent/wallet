package com.v2ray.ang.composeui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ControlPlaneDesignSystemTest {

    @Test
    fun `core palette keeps the white base and semantic accents`() {
        assertEquals(Color(0xFFF7FBFF), AppWhite)
        assertEquals(Color(0xFFFFFFFF), LayerWhite)
        assertEquals(Color(0xFF4268FF), ElectricBlue)
        assertEquals(Color(0xFF20C4F4), ElectricCyan)
        assertEquals(Color(0xFFB58DFF), AuroraPurple)
        assertEquals(Color(0xFFFF6175), DangerRed)
    }

    @Test
    fun `shape scale stays ordered from compact to spacious`() {
        assertEquals(RoundedCornerShape(10.dp), CryptoVpnShapes.extraSmall)
        assertEquals(RoundedCornerShape(14.dp), CryptoVpnShapes.small)
        assertEquals(RoundedCornerShape(20.dp), CryptoVpnShapes.medium)
        assertEquals(RoundedCornerShape(28.dp), CryptoVpnShapes.large)
        assertEquals(RoundedCornerShape(34.dp), CryptoVpnShapes.extraLarge)
    }

    @Test
    fun `typography hierarchy keeps large headings above body text`() {
        assertTrue(
            CryptoVpnTypography.headlineLarge.fontSize > CryptoVpnTypography.titleLarge.fontSize,
        )
        assertTrue(
            CryptoVpnTypography.titleLarge.fontSize > CryptoVpnTypography.bodyLarge.fontSize,
        )
        assertTrue(
            CryptoVpnTypography.bodyLarge.fontSize > CryptoVpnTypography.labelSmall.fontSize,
        )
    }
}
