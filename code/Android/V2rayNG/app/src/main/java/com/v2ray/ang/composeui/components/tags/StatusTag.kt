package com.v2ray.ang.composeui.components.tags

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.theme.AppShape
import com.v2ray.ang.composeui.theme.AuditState
import com.v2ray.ang.composeui.theme.ControlPlaneTokens

enum class StatusType {
    OK,
    WARN,
    CRITICAL,
    UNKNOWN,
}

@Composable
fun StatusTag(
    text: String,
    type: StatusType,
    modifier: Modifier = Modifier,
) {
    val palette = ControlPlaneTokens.audit(type.toAuditState())

    Surface(
        modifier = modifier,
        shape = AppShape.TagPill,
        color = palette.container,
        contentColor = palette.onContainer,
        border = BorderStroke(1.dp, palette.border),
    ) {
        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
            Spacer(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(palette.accent),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

private fun StatusType.toAuditState(): AuditState = when (this) {
    StatusType.OK -> AuditState.Ok
    StatusType.WARN -> AuditState.Warn
    StatusType.CRITICAL -> AuditState.Critical
    StatusType.UNKNOWN -> AuditState.Unknown
}
