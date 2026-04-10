package com.v2ray.ang.composeui.components.listitems

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.p0.model.AssetHolding
import com.v2ray.ang.composeui.p0.model.RegionSpeed
import com.v2ray.ang.composeui.p0.model.WatchSignal
import com.v2ray.ang.composeui.theme.DangerRed
import com.v2ray.ang.composeui.theme.DividerLight
import com.v2ray.ang.composeui.theme.LayerWhite
import com.v2ray.ang.composeui.theme.SignalGreen
import com.v2ray.ang.composeui.theme.SurfaceCloud
import com.v2ray.ang.composeui.theme.TextMuted

@Composable
fun AssetRow(
    asset: AssetHolding,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = LayerWhite.copy(alpha = 0.86f),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, DividerLight),
        tonalElevation = 0.dp,
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("${asset.symbol} · ${asset.chainLabel}", style = MaterialTheme.typography.titleMedium)
                Text(asset.balanceText, style = MaterialTheme.typography.bodySmall, color = TextMuted)
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(asset.valueText, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = asset.changeText,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (asset.changePositive) SignalGreen else DangerRed,
                )
            }
        }
    }
}

@Composable
fun RegionSpeedRow(
    item: RegionSpeed,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SurfaceCloud.copy(alpha = 0.64f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, DividerLight),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(item.regionName, style = MaterialTheme.typography.titleMedium)
                Text("${item.protocol} · ${item.load}", color = TextMuted, style = MaterialTheme.typography.bodySmall)
            }
            Text("${item.latencyMs} ms", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun WatchSignalRow(
    signal: WatchSignal,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = LayerWhite.copy(alpha = 0.86f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, DividerLight),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(signal.symbol, style = MaterialTheme.typography.titleMedium)
                Text(signal.reason, style = MaterialTheme.typography.bodySmall, color = TextMuted)
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    signal.changeText,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (signal.isPositive) SignalGreen else DangerRed,
                )
                Text(signal.volumeText, style = MaterialTheme.typography.bodySmall, color = TextMuted)
            }
        }
    }
}
