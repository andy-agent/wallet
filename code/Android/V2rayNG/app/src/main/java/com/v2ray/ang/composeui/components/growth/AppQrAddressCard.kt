package com.v2ray.ang.composeui.components.growth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import com.v2ray.ang.composeui.components.chips.AppChip
import com.v2ray.ang.composeui.components.chips.AppChipTone
import com.v2ray.ang.composeui.components.cards.AppCard
import com.v2ray.ang.composeui.components.rows.AppLabelValueRow
import com.v2ray.ang.composeui.theme.AppTheme
import com.v2ray.ang.util.QRCodeDecoder

@Composable
fun AppQrAddressCard(
    title: String,
    address: String,
    modifier: Modifier = Modifier,
    subtitle: String = "",
    addressLabel: String = "二维码内容",
    supportingText: String = "",
    status: String? = null,
    footer: @Composable ColumnScope.() -> Unit = {},
) {
    val normalized = address.trim().takeUnless { it.isBlank() || it == "--" }
    val qrBitmap = remember(normalized) {
        normalized?.let { QRCodeDecoder.createQRCode(it, size = 720) }
    }
    AppCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space12)) {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space4)) {
                    Text(
                        text = title,
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                        color = AppTheme.colors.textPrimary,
                    )
                    if (subtitle.isNotBlank()) {
                        Text(
                            text = subtitle,
                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.textSecondary,
                        )
                    }
                }
                if (!status.isNullOrBlank()) {
                    AppChip(text = status, tone = AppChipTone.Info)
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = AppTheme.colors.bgSubtle,
                        shape = RoundedCornerShape(AppTheme.shapes.radiusLg),
                    )
                    .padding(vertical = AppTheme.spacing.space24),
                contentAlignment = Alignment.Center,
            ) {
                if (qrBitmap != null) {
                    Box(
                        modifier = Modifier
                            .size(212.dp)
                            .background(
                                color = AppTheme.colors.surfaceCard,
                                shape = RoundedCornerShape(AppTheme.shapes.radiusLg),
                            )
                            .padding(AppTheme.spacing.space12),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "二维码",
                            modifier = Modifier.size(188.dp),
                            contentScale = ContentScale.FillBounds,
                            filterQuality = FilterQuality.None,
                        )
                    }
                } else {
                    Text(
                        text = "暂无可生成二维码",
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                        color = AppTheme.colors.textSecondary,
                    )
                }
            }
            AppLabelValueRow(
                label = addressLabel,
                value = address,
                supportingText = supportingText,
            )
            footer()
        }
    }
}
