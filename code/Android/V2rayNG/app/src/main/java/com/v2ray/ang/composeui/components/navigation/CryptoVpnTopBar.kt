package com.v2ray.ang.composeui.components.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.v2ray.ang.composeui.p0.ui.P01HeaderHeroRing

@Composable
fun CryptoVpnTopBar(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    actions: @Composable () -> Unit = { P01HeaderHeroRing() },
) {
    AppTopBar(
        title = title,
        subtitle = subtitle,
        modifier = modifier,
        trailing = actions,
    )
}
