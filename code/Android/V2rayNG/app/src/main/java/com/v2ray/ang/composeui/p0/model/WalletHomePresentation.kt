package com.v2ray.ang.composeui.p0.model

import com.v2ray.ang.payment.data.api.WalletAssetItemData
import com.v2ray.ang.payment.data.api.WalletChainItemData
import com.v2ray.ang.payment.data.api.WalletLifecycleData
import com.v2ray.ang.payment.data.api.WalletOverviewData
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

private val terminalWalletOrderStatuses = setOf("FAILED", "EXPIRED", "CANCELED")

internal fun WalletLifecycleData?.hasUsableWallet(): Boolean {
    return this?.walletExists == true &&
        !this.sourceType.equals("LEGACY", ignoreCase = true)
}

internal fun WalletOverviewData.toWalletHomeUiState(
    lifecycle: WalletLifecycleData?,
): WalletHomeUiState {
    val assets = assetItems
        .filter { it.walletVisible && (it.hasPositiveBalance() || it.isCustom) }
        .sortedWith(
            compareBy<WalletAssetItemData>(
                {
                    when {
                        it.hasPositiveBalance() || (it.valueUsd?.toDoubleOrNull() ?: 0.0) > 0.0 -> 0
                        it.isCustom -> 1
                        else -> 2
                    }
                },
                { it.symbol.uppercase(Locale.ROOT) },
            ),
        )
        .map { it.toWalletAssetHolding() }

    return WalletHomeUiState(
        isLoading = false,
        loadState = if (assets.isEmpty()) P0LoadState.EMPTY else P0LoadState.READY,
        accountLabel = accountEmail,
        totalBalanceText = "${assetItems.sumOf { it.orderCount ?: 0 }} 笔交易",
        totalPortfolioValueText = formatWalletUsdValue(totalPortfolioValueUsd),
        priceUpdatedLabel = formatWalletPriceUpdatedLabel(priceUpdatedAt),
        summaryLabel = alerts.firstOrNull() ?: "交易记录",
        selectedChainId = normalizeWalletHomeChainId(selectedNetworkCode),
        chains = chainItems.map { it.toWalletChainSummary() },
        assets = assets,
        alertBanner = alerts.joinToString(" · "),
        emptyMessage = if (assets.isEmpty()) "当前没有链上资产或订单记录。" else null,
        walletExists = lifecycle.hasUsableWallet(),
        walletLifecycleStatus = lifecycle?.lifecycleStatus ?: "NOT_CREATED",
        walletId = lifecycle?.walletId,
        walletDisplayName = lifecycle?.displayName,
        walletNextAction = lifecycle?.nextAction ?: "CREATE_OR_IMPORT",
    )
}

internal fun buildWalletPortfolioValue(assets: List<AssetHolding>): String {
    val totals = assets.mapNotNull { extractWalletUsdAmount(it.valueText) }
    if (totals.isEmpty()) {
        return "$0.00"
    }
    return "$" + "%.2f".format(Locale.US, totals.sum())
}

internal fun formatWalletAssetValueDisplay(raw: String?): String {
    val usdAmount = extractWalletUsdAmount(raw)
    if (usdAmount != null) {
        return "$" + "%.2f".format(Locale.US, usdAmount)
    }
    return raw?.trim().takeUnless { it.isNullOrBlank() } ?: "$0.00"
}

private fun WalletChainItemData.toWalletChainSummary(): WalletChainSummary {
    return WalletChainSummary(
        chainId = normalizeWalletHomeChainId(networkCode),
        label = walletHomeChainLabel(networkCode),
        balanceText = "${orderCount ?: 0} 笔订单",
        accent = if (hasConfiguredAddress == true) "已配置地址" else "待配置地址",
        itemCount = assetCount ?: 0,
    )
}

private fun WalletAssetItemData.toWalletAssetHolding(): AssetHolding {
    return AssetHolding(
        tokenKey = buildWalletAssetTokenKey(this),
        symbol = assetCode,
        chainLabel = walletHomeChainLabel(networkCode),
        balanceText = formatWalletAvailableBalance(availableBalanceUiAmount, assetCode),
        valueText = formatWalletUsdValue(valueUsd),
        unitPriceText = formatWalletUsdValue(unitPriceUsd),
        changeText = formatWalletPriceChangeText(priceChangePct24h, priceStatus),
        changePositive = isWalletPriceChangePositive(priceChangePct24h),
        priceStatusText = balanceStatusLabel(priceStatus, availableBalanceStatus),
        priceUpdatedAt = priceUpdatedAt,
        detailText = displayName,
        customTokenId = customTokenId,
        isCustom = isCustom,
        iconUrl = iconUrl,
    )
}

private fun buildWalletAssetTokenKey(asset: WalletAssetItemData): String {
    val chainId = normalizeWalletHomeChainId(asset.networkCode)
    if (asset.isNative || asset.contractAddress.isNullOrBlank()) {
        return "$chainId:native:${asset.symbol.uppercase(Locale.ROOT)}"
    }
    return asset.contractAddress.trim().lowercase(Locale.ROOT)
}

private fun WalletAssetItemData.hasPositiveBalance(): Boolean {
    val raw = availableBalanceUiAmount?.trim()
        ?.takeUnless { it.isNullOrBlank() || it.equals("null", ignoreCase = true) }
        ?: return false
    return raw.toBigDecimalOrNull()?.compareTo(BigDecimal.ZERO) == 1
}

private fun formatWalletAvailableBalance(
    amount: String?,
    assetCode: String,
): String {
    val normalized = amount
        ?.trim()
        ?.takeIf { it.isNotEmpty() && !it.equals("null", ignoreCase = true) }
        ?: return "--"
    return "${trimTrailingZeros(normalized)} $assetCode"
}

private fun trimTrailingZeros(value: String): String {
    return value
        .replace(Regex("(\\.\\d*?[1-9])0+$"), "$1")
        .replace(Regex("\\.0+$"), "")
}

private fun extractWalletUsdAmount(raw: String?): Double? {
    val normalized = raw.orEmpty().replace(",", "").trim()
    if (!normalized.contains("$")) {
        return null
    }
    val match = Regex("""\$\s*(-?\d+(?:\.\d+)?)""").find(normalized) ?: return null
    return match.groupValues.getOrNull(1)?.toDoubleOrNull()
}

private fun formatWalletUsdValue(raw: String?): String {
    val value = raw?.trim()?.toDoubleOrNull() ?: return "$0.00"
    val absolute = abs(value)
    return when {
        absolute == 0.0 -> "$0.00"
        absolute >= 0.01 -> "$" + "%.2f".format(Locale.US, value)
        absolute >= 0.00000001 -> "$" + "%.8f".format(Locale.US, value)
        else -> "<$0.000001"
    }
}

private fun formatWalletPriceChangeText(
    raw: String?,
    priceStatus: String?,
): String = when {
    !raw.isNullOrBlank() -> {
        val value = raw.toDoubleOrNull()
        if (value == null) "暂无报价" else "%+.2f%%".format(Locale.US, value)
    }
    priceStatus.equals("UNAVAILABLE", ignoreCase = true) -> "暂无报价"
    else -> "--"
}

private fun isWalletPriceChangePositive(raw: String?): Boolean {
    return (raw?.toDoubleOrNull() ?: 0.0) >= 0
}

private fun formatWalletPriceUpdatedLabel(raw: String?): String {
    val instant = raw?.let { runCatching { Instant.parse(it) }.getOrNull() } ?: return "价格待同步"
    val formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm", Locale.US)
        .withZone(ZoneId.systemDefault())
    return "价格更新于 ${formatter.format(instant)}"
}

private fun balanceStatusLabel(
    priceStatus: String?,
    balanceStatus: String?,
): String = when {
    priceStatus.equals("READY", ignoreCase = true) -> "已报价"
    priceStatus.equals("FIXED", ignoreCase = true) -> "固定报价"
    priceStatus.equals("UNAVAILABLE", ignoreCase = true) -> "暂无报价"
    balanceStatus?.uppercase(Locale.US) == "READY" -> "链上可用余额"
    balanceStatus?.uppercase(Locale.US) == "UNAVAILABLE" -> "余额待同步"
    balanceStatus?.uppercase(Locale.US) == "NO_ADDRESS" -> "未配置地址"
    else -> "资产详情"
}
