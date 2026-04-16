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
        assertEquals(Color(0xFFF7FAFF), ColorTokens.BackgroundBase)
        assertEquals(Color(0xFFFFFFFF), ColorTokens.CardBase)
        assertEquals(Color(0xFF4F7CFF), ColorTokens.BrandPrimary)
        assertEquals(Color(0xFF25D7FF), ColorTokens.BrandSecondary)
        assertEquals(Color(0xFF8C7CFF), ColorTokens.AccentPurple)
        assertEquals(Color(0xFFFF6B7A), ColorTokens.StatusError)
        assertEquals(ColorTokens.BackgroundBase, AppWhite)
        assertEquals(ColorTokens.BrandPrimary, ElectricBlue)
    }

    @Test
    fun `shape tokens stay aligned with the target radius ladder`() {
        assertEquals(8.dp, ShapeTokens.RadiusXs)
        assertEquals(12.dp, ShapeTokens.RadiusS)
        assertEquals(16.dp, ShapeTokens.RadiusM)
        assertEquals(20.dp, ShapeTokens.RadiusL)
        assertEquals(24.dp, ShapeTokens.RadiusXl)
        assertEquals(RoundedCornerShape(8.dp), CryptoVpnShapes.extraSmall)
        assertEquals(RoundedCornerShape(12.dp), CryptoVpnShapes.small)
        assertEquals(RoundedCornerShape(16.dp), CryptoVpnShapes.medium)
        assertEquals(RoundedCornerShape(20.dp), CryptoVpnShapes.large)
        assertEquals(RoundedCornerShape(24.dp), CryptoVpnShapes.extraLarge)
    }

    @Test
    fun `typography tokens keep page title above section title and body`() {
        val scale = TypographyTokens.medium()
        assertEquals(28.sp, scale.pageTitle.fontSize)
        assertEquals(18.sp, scale.sectionTitle.fontSize)
        assertEquals(14.sp, scale.body.fontSize)
        assertEquals(11.sp, scale.navLabel.fontSize)
        assertTrue(
            scale.pageTitle.fontSize > scale.sectionTitle.fontSize,
        )
        assertTrue(
            scale.sectionTitle.fontSize > scale.body.fontSize,
        )
        assertTrue(
            scale.body.fontSize > scale.navLabel.fontSize,
        )
    }
}
