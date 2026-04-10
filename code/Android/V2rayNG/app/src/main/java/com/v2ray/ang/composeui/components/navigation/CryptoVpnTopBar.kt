package com.v2ray.ang.composeui.components.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.theme.TextSoft
import com.v2ray.ang.composeui.theme.TextStrong

@Composable
fun CryptoVpnTopBar(
    title: String,
    subtitle: String? = null,
    actions: @Composable () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle.uppercase(),
                    color = TextSoft,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = TextStrong,
            )
        }
        actions()
    }
}
