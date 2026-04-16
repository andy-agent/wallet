package com.app.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.core.theme.BluePrimary
import com.app.core.theme.BlueSecondary
import com.app.core.theme.BorderSubtle
import com.app.core.theme.CardGlass

@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BorderSubtle, RoundedCornerShape(26.dp)),
        color = CardGlass,
        shadowElevation = 6.dp,
        shape = RoundedCornerShape(26.dp),
    ) {
        Column(
            modifier = Modifier
                .background(Brush.linearGradient(listOf(Color.White, BlueSecondary.copy(alpha = 0.08f), BluePrimary.copy(alpha = 0.05f))))
                .padding(18.dp),
        ) {
            if (title != null) {
                Text(title, style = MaterialTheme.typography.titleMedium)
            }
            if (subtitle != null) {
                Spacer(Modifier.height(4.dp))
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f))
                Spacer(Modifier.height(12.dp))
            }
            content()
        }
    }
}
