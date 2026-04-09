package com.v2ray.ang.composeui.components.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.theme.AppShape
import com.v2ray.ang.composeui.theme.ControlPlaneLayer
import com.v2ray.ang.composeui.theme.ControlPlaneTokens
import com.v2ray.ang.composeui.theme.TextPrimary

@Composable
fun BaseCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val layer = ControlPlaneTokens.layer(ControlPlaneLayer.Level1)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = AppShape.Card,
        colors = CardDefaults.cardColors(
            containerColor = layer.container,
            contentColor = TextPrimary,
        ),
        border = BorderStroke(1.dp, layer.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = layer.shadowElevation),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}
