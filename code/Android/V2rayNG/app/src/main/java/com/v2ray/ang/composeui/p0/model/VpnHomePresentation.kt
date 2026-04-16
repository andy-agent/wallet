package com.v2ray.ang.composeui.p0.model

import com.v2ray.ang.payment.data.api.WalletOverviewData
import java.util.Locale

internal fun resolveVpnOverviewValueText(
    walletOverview: WalletOverviewData?,
    ordersSyncUnavailable: Boolean,
): String {
    val assets = walletOverview?.assetItems
        ?.filter { it.walletVisible }
        .orEmpty()

    if (assets.isEmpty()) {
        return if (ordersSyncUnavailable) "--" else "余额待同步"
    }

    val readyBalances = assets.mapNotNull { asset ->
        val amount = asset.availableBalanceUiAmount
            ?.trim()
            ?.takeIf { it.isNotEmpty() && !it.equals("null", ignoreCase = true) }
            ?.toDoubleOrNull()
            ?: return@mapNotNull null
        if (!asset.availableBalanceStatus.equals("READY", ignoreCase = true)) {
            return@mapNotNull null
        }
        asset.assetCode.uppercase(Locale.US) to amount
    }

    if (readyBalances.isEmpty()) {
        return if (ordersSyncUnavailable) "--" else "余额待同步"
    }

    val totalsByAsset = linkedMapOf<String, Double>()
    readyBalances.forEach { (assetCode, amount) ->
        totalsByAsset[assetCode] = (totalsByAsset[assetCode] ?: 0.0) + amount
    }

    val positiveParts = totalsByAsset.entries
        .filter { (_, amount) -> amount > 0 }
        .sortedBy { overviewAssetPriority(it.key) }
        .map { (assetCode, amount) -> "${trimOverviewAmount(amount)} $assetCode" }

    if (positiveParts.isNotEmpty()) {
        return when {
            positiveParts.size <= 2 -> positiveParts.joinToString(" · ")
            else -> positiveParts.take(2).joinToString(" · ") + " +${positiveParts.size - 2}"
        }
    }

    return "0"
}

private fun overviewAssetPriority(assetCode: String): Int = when (assetCode) {
    "USDT" -> 0
    "USDC" -> 1
    "SOL" -> 2
    "TRX" -> 3
    else -> 9
}

private fun trimOverviewAmount(value: Double): String {
    return String.format(Locale.US, "%.9f", value)
        .replace(Regex("(\\.\\d*?[1-9])0+$"), "$1")
        .replace(Regex("\\.0+$"), "")
}
