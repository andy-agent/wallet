package com.v2ray.ang.composeui.p0.model

import com.v2ray.ang.payment.data.api.WalletAssetItemData
import com.v2ray.ang.payment.data.api.WalletChainItemData
import com.v2ray.ang.payment.data.api.WalletLifecycleData
import com.v2ray.ang.payment.data.api.WalletOverviewData
import java.math.BigDecimal
import java.util.Locale

private val terminalWalletOrderStatuses = setOf("FAILED", "EXPIRED", "CANCELED")

internal fun WalletLifecycleData?.hasUsableWallet(): Boolean {
    return this?.walletExists == true &&
        !this.sourceType.equals("LEGACY", ignoreCase = true)
}

internal fun WalletOverviewData.toWalletHomeUiState(
    lifecycle: WalletLifecycleData?,
): WalletHomeUiState {
    val assets = assetItems
        .filter { it.walletVisible && it.hasPositiveBalance() }
        .map { it.toWalletAssetHolding() }

    return WalletHomeUiState(
        isLoading = false,
        loadState = if (assets.isEmpty()) P0LoadState.EMPTY else P0LoadState.READY,
        accountLabel = accountEmail,
        totalBalanceText = "${assetItems.sumOf { it.orderCount ?: 0 }} 笔交易",
        summaryLabel = alerts.firstOrNull() ?: "交易记录",
        selectedChainId = selectedNetworkCode.lowercase(Locale.ROOT),
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

internal fun walletHomeChainLabel(chainId: String): String = when (chainId.lowercase(Locale.ROOT)) {
    "tron" -> "TRON"
    "solana" -> "Solana"
    "ethereum" -> "Ethereum"
    "base" -> "Base"
    else -> chainId.uppercase(Locale.ROOT)
}

private fun WalletChainItemData.toWalletChainSummary(): WalletChainSummary {
    return WalletChainSummary(
        chainId = networkCode.lowercase(Locale.ROOT),
        label = walletHomeChainLabel(networkCode),
        balanceText = "${orderCount ?: 0} 笔订单",
        accent = if (hasConfiguredAddress == true) "已配置地址" else "待配置地址",
        itemCount = assetCount ?: 0,
    )
}

private fun WalletAssetItemData.toWalletAssetHolding(): AssetHolding {
    return AssetHolding(
        symbol = assetCode,
        chainLabel = walletHomeChainLabel(networkCode),
        balanceText = formatWalletAvailableBalance(availableBalanceUiAmount, assetCode),
        valueText = balanceStatusLabel(availableBalanceStatus),
        changeText = walletHomeChainLabel(networkCode),
        changePositive = true,
        detailText = displayName,
    )
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

private fun balanceStatusLabel(status: String?): String = when (status?.uppercase(Locale.US)) {
    "READY" -> "链上可用余额"
    "UNAVAILABLE" -> "余额待同步"
    "NO_ADDRESS" -> "未配置地址"
    else -> "资产详情"
}
