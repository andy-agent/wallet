package com.v2ray.ang.composeui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v2ray.ang.composeui.theme.tokens.ColorTokens
import com.v2ray.ang.composeui.theme.tokens.ShapeTokens
import com.v2ray.ang.composeui.theme.tokens.TypographyTokens
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ControlPlaneDesignSystemTest {

    @Test
    fun `color tokens follow the target visual system palette`() {
        assertEquals(Color(0xFFF7FAFF), ColorTokens.light.bgApp)
        assertEquals(Color(0xFFFFFFFF), ColorTokens.light.surfaceCard)
        assertEquals(Color(0xFF4F7CFF), ColorTokens.light.brandPrimary)
        assertEquals(Color(0xFF25D7FF), ColorTokens.light.brandSecondary)
        assertEquals(Color(0xFF8C7CFF), ColorTokens.light.accentPurple)
        assertEquals(Color(0xFFFF6B7A), ColorTokens.light.error)
        assertEquals(ColorTokens.light.bgApp, AppWhite)
        assertEquals(ColorTokens.light.brandPrimary, ElectricBlue)
    }

    @Test
    fun `shape tokens stay aligned with the target radius ladder`() {
        assertEquals(8.dp, ShapeTokens.default.radiusXs)
        assertEquals(12.dp, ShapeTokens.default.radiusSm)
        assertEquals(16.dp, ShapeTokens.default.radiusMd)
        assertEquals(20.dp, ShapeTokens.default.radiusLg)
        assertEquals(24.dp, ShapeTokens.default.radiusXl)
        assertEquals(RoundedCornerShape(8.dp), CryptoVpnShapes.extraSmall)
        assertEquals(RoundedCornerShape(12.dp), CryptoVpnShapes.small)
        assertEquals(RoundedCornerShape(16.dp), CryptoVpnShapes.medium)
        assertEquals(RoundedCornerShape(20.dp), CryptoVpnShapes.large)
        assertEquals(RoundedCornerShape(24.dp), CryptoVpnShapes.extraLarge)
    }

    @Test
    fun `typography tokens keep page title above section title and body`() {
        val scale = TypographyTokens.medium()
        assertEquals(28.sp, scale.headlineL.fontSize)
        assertEquals(18.sp, scale.titleL.fontSize)
        assertEquals(14.sp, scale.bodyM.fontSize)
        assertEquals(11.sp, scale.navLabel.fontSize)
        assertTrue(
            scale.headlineL.fontSize > scale.titleL.fontSize,
        )
        assertTrue(
            scale.titleL.fontSize > scale.bodyM.fontSize,
        )
        assertTrue(
            scale.bodyM.fontSize > scale.navLabel.fontSize,
        )
    }
}
