package com.v2ray.ang.composeui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v2ray.ang.composeui.theme.tokens.ColorTokens
import com.v2ray.ang.composeui.theme.tokens.ShapeTokens
import com.v2ray.ang.composeui.theme.tokens.SpacingTokens
import com.v2ray.ang.composeui.theme.tokens.TypographyTokens
import com.v2ray.ang.composeui.theme.tokens.toMaterialTypography
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
        val material = scale.toMaterialTypography()
        assertEquals(scale.metricL.fontSize, material.displayLarge.fontSize)
        assertEquals(scale.headlineL.fontSize, material.displayMedium.fontSize)
        assertEquals(scale.headlineM.fontSize, material.displaySmall.fontSize)
        assertEquals(28.sp, scale.headlineL.fontSize)
        assertEquals(18.sp, scale.titleL.fontSize)
        assertEquals(14.sp, scale.labelL.fontSize)
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

    @Test
    fun `spacing tokens adapt page horizontal and section gap by breakpoint`() {
        val compact = SpacingTokens.compact()
        val medium = SpacingTokens.medium()
        val expanded = SpacingTokens.expanded()

        assertEquals(16.dp, compact.pageHorizontal)
        assertEquals(20.dp, medium.pageHorizontal)
        assertEquals(24.dp, expanded.pageHorizontal)

        assertEquals(12.dp, compact.sectionGap)
        assertEquals(16.dp, medium.sectionGap)
        assertEquals(20.dp, expanded.sectionGap)
    }
}
