package com.v2ray.ang.composeui.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.theme.AuroraPurple
import com.v2ray.ang.composeui.theme.DividerLight
import com.v2ray.ang.composeui.theme.ElectricBlue
import com.v2ray.ang.composeui.theme.ElectricCyan
import com.v2ray.ang.composeui.theme.LayerWhite
import com.v2ray.ang.composeui.theme.ShadowBlue
import com.v2ray.ang.composeui.theme.TextBody

@Composable
fun PrimaryGlowButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ElectricBlue,
            contentColor = LayerWhite,
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
    ) {
        Text(text = text, modifier = Modifier.padding(vertical = 6.dp))
    }
}

@Composable
fun GradientCTAButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .background(
                brush = Brush.horizontalGradient(
                    listOf(ElectricBlue, ElectricCyan, AuroraPurple),
                ),
                shape = RoundedCornerShape(22.dp),
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            contentColor = LayerWhite,
        ),
        shape = RoundedCornerShape(22.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
    ) {
        Text(text = text, modifier = Modifier.padding(vertical = 6.dp))
    }
}

@Composable
fun SecondaryOutlineButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    icon: @Composable (() -> Unit)? = null,
    label: @Composable () -> Unit,
) {
    val shape = if (icon == null) RoundedCornerShape(22.dp) else CircleShape
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = if (icon == null) RoundedCornerShape(22.dp) else RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (selected) ElectricBlue.copy(alpha = 0.40f) else DividerLight,
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) ShadowBlue else LayerWhite.copy(alpha = 0.75f),
            contentColor = TextBody,
        ),
        elevation = null,
    ) {
        ProvideTextStyle(value = androidx.compose.material3.MaterialTheme.typography.bodySmall) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                icon?.invoke()
                label()
            }
        }
    }
}
