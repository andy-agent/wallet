package com.v2ray.ang.composeui.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.components.chips.AppChip
import com.v2ray.ang.composeui.components.chips.AppChipTone
import com.v2ray.ang.composeui.components.rows.LabelValueRow
import com.v2ray.ang.composeui.components.rows.LabelValueDisplayMode
import com.v2ray.ang.composeui.components.rows.LabelValueRowLayoutMode
import com.v2ray.ang.composeui.theme.AppTheme
import com.v2ray.ang.util.QRCodeDecoder

@Composable
fun QrAddressCard(
    title: String,
    qrContent: String,
    modifier: Modifier = Modifier,
    subtitle: String = "",
    address: String = "",
    addressLabel: String = "二维码内容",
    supportingText: String = "",
    status: String? = null,
    emptyLabel: String = "暂无可生成二维码",
    footer: @Composable ColumnScope.() -> Unit = {},
) {
    val normalized = qrContent.trim().takeUnless { it.isBlank() || it == "--" }
    val qrBitmap = remember(normalized) {
        normalized?.let { QRCodeDecoder.createQRCode(it, size = 720) }
    }

    AppCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space12)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
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
                    AppChip(
                        text = status,
                        tone = AppChipTone.Info,
                    )
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
                        text = emptyLabel,
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                        color = AppTheme.colors.textSecondary,
                    )
                }
            }

            if (address.isNotBlank()) {
                LabelValueRow(
                    label = addressLabel,
                    value = address,
                    supportingText = supportingText,
                    layoutMode = LabelValueRowLayoutMode.Stacked,
                    valueDisplayMode = LabelValueDisplayMode.LongCompact,
                )
            }

            footer()
        }
    }
}
