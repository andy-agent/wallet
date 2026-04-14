package com.v2ray.ang.composeui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object AppTypographyFactory {

    fun small(): Typography {
        return Typography(
            headlineLarge = TextStyle(
                fontSize = 24.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Bold
            ),
            headlineMedium = TextStyle(
                fontSize = 20.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.Bold
            ),
            headlineSmall = TextStyle(
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.SemiBold
            ),
            titleLarge = TextStyle(
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            titleMedium = TextStyle(
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.SemiBold
            ),
            titleSmall = TextStyle(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium
            ),
            bodyLarge = TextStyle(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Normal
            ),
            bodyMedium = TextStyle(
                fontSize = 13.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Normal
            ),
            bodySmall = TextStyle(
                fontSize = 12.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Normal
            ),
            labelLarge = TextStyle(
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium
            ),
            labelMedium = TextStyle(
                fontSize = 11.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Medium
            ),
            labelSmall = TextStyle(
                fontSize = 10.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }

    fun normal(): Typography {
        return Typography(
            headlineLarge = TextStyle(
                fontSize = 26.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Bold
            ),
            headlineMedium = TextStyle(
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold
            ),
            headlineSmall = TextStyle(
                fontSize = 20.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.SemiBold
            ),
            titleLarge = TextStyle(
                fontSize = 20.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.Bold
            ),
            titleMedium = TextStyle(
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.SemiBold
            ),
            titleSmall = TextStyle(
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium
            ),
            bodyLarge = TextStyle(
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Normal
            ),
            bodyMedium = TextStyle(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Normal
            ),
            bodySmall = TextStyle(
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Normal
            ),
            labelLarge = TextStyle(
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium
            ),
            labelMedium = TextStyle(
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium
            ),
            labelSmall = TextStyle(
                fontSize = 11.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }

    fun large(): Typography {
        return Typography(
            headlineLarge = TextStyle(
                fontSize = 28.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.Bold
            ),
            headlineMedium = TextStyle(
                fontSize = 24.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Bold
            ),
            headlineSmall = TextStyle(
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.SemiBold
            ),
            titleLarge = TextStyle(
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold
            ),
            titleMedium = TextStyle(
                fontSize = 19.sp,
                lineHeight = 25.sp,
                fontWeight = FontWeight.SemiBold
            ),
            titleSmall = TextStyle(
                fontSize = 17.sp,
                lineHeight = 23.sp,
                fontWeight = FontWeight.Medium
            ),
            bodyLarge = TextStyle(
                fontSize = 16.sp,
                lineHeight = 23.sp,
                fontWeight = FontWeight.Normal
            ),
            bodyMedium = TextStyle(
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Normal
            ),
            bodySmall = TextStyle(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Normal
            ),
            labelLarge = TextStyle(
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium
            ),
            labelMedium = TextStyle(
                fontSize = 13.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Medium
            ),
            labelSmall = TextStyle(
                fontSize = 12.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
fun rememberAdaptiveTypography(): Typography {
    val width = LocalConfiguration.current.screenWidthDp
    return when {
        width < 360 -> AppTypographyFactory.small()
        width < 412 -> AppTypographyFactory.normal()
        else -> AppTypographyFactory.large()
    }
}
