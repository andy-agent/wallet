package com.app.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.app.core.theme.AppDimens
import com.app.core.theme.BluePrimary
import com.app.core.theme.BlueSecondary
import com.app.core.theme.TextTertiary

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val background = if (enabled) {
        Brush.horizontalGradient(listOf(BluePrimary, BlueSecondary))
    } else {
        Brush.horizontalGradient(listOf(TextTertiary.copy(alpha = 0.4f), TextTertiary.copy(alpha = 0.28f)))
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(AppDimens.buttonHeight)
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
