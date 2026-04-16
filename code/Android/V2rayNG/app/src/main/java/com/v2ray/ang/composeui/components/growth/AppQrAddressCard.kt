package com.v2ray.ang.composeui.components.growth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import com.v2ray.ang.composeui.components.cards.QrAddressCard

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
    QrAddressCard(
        title = title,
        subtitle = subtitle,
        qrContent = address,
        address = address,
        addressLabel = addressLabel,
        supportingText = supportingText,
        status = status,
        modifier = modifier,
        footer = footer,
    )
}
