package com.v2ray.ang.composeui.components.listitems

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.v2ray.ang.composeui.p0.model.AssetHolding
import com.v2ray.ang.composeui.p0.model.RegionSpeed
import com.v2ray.ang.composeui.p0.model.WatchSignal
import com.v2ray.ang.composeui.components.chips.AppChip
import com.v2ray.ang.composeui.components.chips.AppChipTone
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import com.v2ray.ang.composeui.theme.AppTheme

@Deprecated("Freeze legacy business adapter file. Prefer dedicated feature row files.")
@Composable
fun AssetRow(
    asset: AssetHolding,
    modifier: Modifier = Modifier,
) {
    AppListItem(
        title = "${asset.symbol} · ${asset.chainLabel}",
        subtitle = asset.balanceText,
        value = asset.valueText,
        supportingText = asset.detailText,
        modifier = modifier,
        trailing = {
            AppChip(
                text = asset.changeText,
                tone = if (asset.changePositive) AppChipTone.Success else AppChipTone.Error,
            )
        },
    )
}

@Deprecated("Freeze legacy business adapter file. Prefer dedicated feature row files.")
@Composable
fun RegionSpeedRow(
    item: RegionSpeed,
    modifier: Modifier = Modifier,
) {
    NodeRow(item = item, modifier = modifier)
}

@Deprecated("Freeze legacy business adapter file. Prefer dedicated feature row files.")
@Composable
fun NodeRow(
    item: RegionSpeed,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    AppListItem(
        title = item.regionName,
        subtitle = "${item.protocol} · ${item.load}",
        value = "${item.latencyMs} ms",
        supportingText = item.regionCode,
        emphasized = item.isAllowed,
        modifier = modifier,
        onClick = onClick,
        trailing = {
            if (!item.isAllowed) {
                AppChip(text = "不可用", tone = AppChipTone.Warning)
            }
        },
    )
}

@Deprecated("Freeze legacy business adapter file. Prefer dedicated feature row files.")
@Composable
fun WatchSignalRow(
    signal: WatchSignal,
    modifier: Modifier = Modifier,
) {
    AppListItem(
        title = signal.symbol,
        subtitle = signal.reason,
        value = signal.changeText,
        supportingText = signal.volumeText,
        modifier = modifier,
        trailing = {
            AppChip(
                text = if (signal.isPositive) "上涨" else "下跌",
                tone = if (signal.isPositive) AppChipTone.Success else AppChipTone.Error,
            )
        },
    )
}
