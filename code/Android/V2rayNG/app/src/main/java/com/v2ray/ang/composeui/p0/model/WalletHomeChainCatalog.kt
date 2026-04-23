package com.v2ray.ang.composeui.p0.model

import com.v2ray.ang.payment.data.api.WalletAssetItemData
import com.v2ray.ang.payment.data.api.WalletChainAccountData
import com.v2ray.ang.payment.data.api.WalletSummaryData
import java.util.Locale

private val selfCustodySeedNetworkCodes = listOf(
    "ETHEREUM",
    "BSC",
    "POLYGON",
    "ARBITRUM",
    "BASE",
    "OPTIMISM",
    "AVALANCHE_C",
    "SOLANA",
    "TRON",
)

internal fun normalizeWalletHomeChainId(networkCode: String): String = when (networkCode.uppercase(Locale.ROOT)) {
    "AVALANCHE_C" -> "avalanche"
    "SMARTCHAIN" -> "bsc"
    else -> networkCode.lowercase(Locale.ROOT)
}

internal fun walletHomeChainLabel(chainId: String): String = when (normalizeWalletHomeChainId(chainId)) {
    "tron" -> "TRON"
    "solana" -> "Solana"
    "ethereum" -> "Ethereum"
    "bsc" -> "BSC"
    "polygon" -> "Polygon"
    "arbitrum" -> "Arbitrum"
    "base" -> "Base"
    "optimism" -> "Optimism"
    "avalanche" -> "Avalanche"
    else -> chainId.uppercase(Locale.ROOT)
}

internal fun buildWalletHomeChainOptions(
    wallet: WalletSummaryData,
    chainAccounts: List<WalletChainAccountData>,
    assetCatalog: List<WalletAssetItemData> = emptyList(),
): List<WalletHomeChainOption> {
    val options = linkedMapOf<String, WalletHomeChainOption>()
    val enabledAccounts = chainAccounts
        .filter { it.isEnabled && it.address.isNotBlank() }
        .distinctBy { normalizeWalletHomeChainId(it.networkCode) }

    enabledAccounts.forEach { account ->
        options.putIfAbsent(
            normalizeWalletHomeChainId(account.networkCode),
            account.toWalletHomeChainOption(),
        )
    }

    if (wallet.walletKind.equals("SELF_CUSTODY", ignoreCase = true)) {
        val inferredAddressByChainId = inferSeedAddressByChainId(enabledAccounts)
        selfCustodySeedNetworkCodes
            .plus(assetCatalog.map { it.networkCode.uppercase(Locale.ROOT) })
            .distinct()
            .forEach { networkCode ->
                val chainId = normalizeWalletHomeChainId(networkCode)
                options.putIfAbsent(
                    chainId,
                    walletChainOption(
                        networkCode = networkCode,
                        address = inferredAddressByChainId[chainId].orEmpty(),
                    ),
                )
            }
    } else if (options.isEmpty()) {
        assetCatalog
            .filter { it.walletVisible }
            .map { it.networkCode }
            .distinct()
            .forEach { networkCode ->
                val chainId = normalizeWalletHomeChainId(networkCode)
                options.putIfAbsent(chainId, walletChainOption(networkCode = networkCode))
            }
    }

    return options.values.toList()
}

private fun inferSeedAddressByChainId(
    chainAccounts: List<WalletChainAccountData>,
): Map<String, String> {
    val addressByChainId = linkedMapOf<String, String>()
    chainAccounts.forEach { account ->
        addressByChainId.putIfAbsent(
            normalizeWalletHomeChainId(account.networkCode),
            account.address,
        )
    }

    chainAccounts
        .firstOrNull { it.chainFamily.equals("EVM", ignoreCase = true) && it.address.isNotBlank() }
        ?.address
        ?.let { evmAddress ->
            listOf(
                "ethereum",
                "bsc",
                "polygon",
                "arbitrum",
                "base",
                "optimism",
                "avalanche",
            ).forEach { chainId ->
                addressByChainId.putIfAbsent(chainId, evmAddress)
            }
        }

    return addressByChainId
}

private fun WalletChainAccountData.toWalletHomeChainOption(): WalletHomeChainOption =
    walletChainOption(
        networkCode = networkCode,
        address = address,
    )

private fun walletChainOption(
    networkCode: String,
    address: String = "",
): WalletHomeChainOption {
    val trimmedAddress = address.trim()
    return WalletHomeChainOption(
        chainId = normalizeWalletHomeChainId(networkCode),
        label = walletHomeChainLabel(networkCode),
        address = trimmedAddress,
        addressSuffix = trimmedAddress.takeLast(4).ifBlank { "----" },
    )
}
