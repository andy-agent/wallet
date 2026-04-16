package com.v2ray.ang.composeui.theme.tokens

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

data class TypographyScaleTokens(
    val pageTitle: TextStyle,
    val sectionTitle: TextStyle,
    val displayValue: TextStyle,
    val valueM: TextStyle,
    val body: TextStyle,
    val caption: TextStyle,
    val label: TextStyle,
    val navLabel: TextStyle,
)

object TypographyTokens {

    fun compact(): TypographyScaleTokens = TypographyScaleTokens(
        pageTitle = TextStyle(
            fontSize = 26.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        sectionTitle = TextStyle(
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        displayValue = TextStyle(
            fontSize = 26.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight.Bold,
        ),
        valueM = TextStyle(
            fontSize = 16.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        body = TextStyle(
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal,
        ),
        caption = TextStyle(
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Normal,
        ),
        label = TextStyle(
            fontSize = 11.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Medium,
        ),
        navLabel = TextStyle(
            fontSize = 10.sp,
            lineHeight = 12.sp,
            fontWeight = FontWeight.Medium,
        ),
    )

    fun medium(): TypographyScaleTokens = TypographyScaleTokens(
        pageTitle = TextStyle(
            fontSize = 28.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        sectionTitle = TextStyle(
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        displayValue = TextStyle(
            fontSize = 28.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Bold,
        ),
        valueM = TextStyle(
            fontSize = 16.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        body = TextStyle(
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal,
        ),
        caption = TextStyle(
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Normal,
        ),
        label = TextStyle(
            fontSize = 12.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Medium,
        ),
        navLabel = TextStyle(
            fontSize = 11.sp,
            lineHeight = 12.sp,
            fontWeight = FontWeight.Medium,
        ),
    )

    fun expanded(): TypographyScaleTokens = TypographyScaleTokens(
        pageTitle = TextStyle(
            fontSize = 30.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        sectionTitle = TextStyle(
            fontSize = 20.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        displayValue = TextStyle(
            fontSize = 30.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.Bold,
        ),
        valueM = TextStyle(
            fontSize = 17.sp,
            lineHeight = 23.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        body = TextStyle(
            fontSize = 15.sp,
            lineHeight = 21.sp,
            fontWeight = FontWeight.Normal,
        ),
        caption = TextStyle(
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Normal,
        ),
        label = TextStyle(
            fontSize = 12.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Medium,
        ),
        navLabel = TextStyle(
            fontSize = 11.sp,
            lineHeight = 12.sp,
            fontWeight = FontWeight.Medium,
        ),
    )

    fun materialCompact(): Typography = compact().toMaterialTypography()

    fun materialMedium(): Typography = medium().toMaterialTypography()

    fun materialExpanded(): Typography = expanded().toMaterialTypography()
}

fun TypographyScaleTokens.toMaterialTypography(): Typography = Typography(
    headlineLarge = pageTitle,
    headlineMedium = displayValue,
    headlineSmall = sectionTitle,
    titleLarge = sectionTitle,
    titleMedium = valueM,
    titleSmall = body.copy(fontWeight = FontWeight.Medium),
    bodyLarge = body,
    bodyMedium = body,
    bodySmall = caption,
    labelLarge = label,
    labelMedium = label,
    labelSmall = navLabel,
)
