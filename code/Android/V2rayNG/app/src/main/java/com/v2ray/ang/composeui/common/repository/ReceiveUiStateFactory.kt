package com.v2ray.ang.composeui.common.repository

import com.v2ray.ang.payment.PaymentConfig
import com.v2ray.ang.payment.data.api.WalletChainItemData
import com.v2ray.ang.payment.data.api.WalletReceiveContextData
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric
import com.v2ray.ang.composeui.p2.model.ReceiveUiState
import com.v2ray.ang.composeui.p2.model.ReceiveVariantUi
import java.util.Locale

internal fun WalletReceiveContextData.toReceiveUiState(): ReceiveUiState {
    val currentNetworkLabel = receiveDisplayChainLabel(selectedNetworkCode)
    return ReceiveUiState(
        badge = "${selectedAssetCode} · $currentNetworkLabel",
        summary = "",
        primaryActionLabel = if (canShare) "分享二维码" else "暂无地址可分享",
        secondaryActionLabel = "复制地址",
        metrics = listOf(
            FeatureMetric("当前链", "${selectedAssetCode} · $currentNetworkLabel"),
            FeatureMetric("可切换", chainItems.joinToString(" / ") { receiveDisplayChainLabel(it.networkCode) }),
            FeatureMetric("地址数", addresses.size.toString()),
            FeatureMetric("校验状态", status),
        ),
        fields = listOf(
            FeatureField(
                key = "address",
                label = "收款地址",
                value = defaultAddress ?: "--",
                supportingText = "",
            ),
        ),
        highlights = listOf(
            FeatureListItem("当前网络", currentNetworkLabel, selectedAssetCode, "LIVE"),
            FeatureListItem("地址状态", status, "${addresses.size} 个地址", "ADDR"),
            FeatureListItem("数据源", "wallet/receive-context", "", "REAL"),
        ),
        variants = buildReceiveVariants(this),
        canShare = canShare,
        shareText = shareText.orEmpty(),
        walletExists = walletExists,
        receiveState = receiveState,
        note = "",
    )
}

internal fun ReceiveUiState.previewVariantSelection(
    assetId: String,
    chainId: String,
): ReceiveUiState {
    val nextVariant = variants.firstOrNull {
        it.assetId.equals(assetId, ignoreCase = true) &&
            it.chainId.equals(chainId, ignoreCase = true)
    } ?: variants.firstOrNull { it.chainId.equals(chainId, ignoreCase = true) }
        ?: return this
    val currentNetworkValue = "${nextVariant.assetId} · ${nextVariant.label}"
    val statusValue = nextVariant.status.ifBlank { "正在切换" }
    val noteValue = ""
    return copy(
        badge = currentNetworkValue,
        summary = noteValue,
        primaryActionLabel = if (nextVariant.canShare) "分享二维码" else "暂无地址可分享",
        secondaryActionLabel = "复制地址",
        metrics = metrics.mapIndexed { index, metric ->
            when (index) {
                0 -> metric.copy(value = currentNetworkValue)
                3 -> metric.copy(value = statusValue)
                else -> metric
            }
        },
        fields = fields.map { field ->
            if (field.key == "address") {
                field.copy(
                    value = nextVariant.address.ifBlank { "--" },
                    supportingText = noteValue,
                )
            } else {
                field
            }
        },
        highlights = highlights.mapIndexed { index, item ->
            when (index) {
                0 -> item.copy(subtitle = nextVariant.label, trailing = nextVariant.assetId)
                1 -> item.copy(subtitle = statusValue)
                else -> item
            }
        },
        variants = variants.map { variant ->
            variant.copy(
                selected = variant.assetId.equals(nextVariant.assetId, ignoreCase = true) &&
                    variant.chainId.equals(nextVariant.chainId, ignoreCase = true),
            )
        },
        canShare = nextVariant.canShare,
        shareText = nextVariant.shareText,
        note = noteValue,
    )
}

internal fun receiveDisplayChainLabel(networkCode: String): String = when (networkCode.uppercase(Locale.ROOT)) {
    "SOLANA" -> "Solana"
    "TRON" -> "TRON"
    else -> networkCode
}

private fun buildReceiveVariants(context: WalletReceiveContextData): List<ReceiveVariantUi> {
    return context.chainItems.map { item ->
        item.toReceiveVariant(context)
    }
}

private fun WalletChainItemData.toReceiveVariant(
    context: WalletReceiveContextData,
): ReceiveVariantUi {
    val selected = selected == true
    val address = if (selected) context.defaultAddress.orEmpty() else ""
    val label = receiveDisplayChainLabel(networkCode)
    return ReceiveVariantUi(
        assetId = resolveReceiveRouteAssetId(context.selectedAssetCode, this, context.selectedNetworkCode),
        chainId = networkCode.lowercase(Locale.ROOT),
        label = label,
        selected = selected,
        address = address,
        qrContent = address,
        shareText = if (selected) context.shareText.orEmpty() else "",
        status = if (selected) context.status else "点击切换到 $label",
        note = "",
        canShare = selected && context.canShare,
    )
}

private fun resolveReceiveRouteAssetId(
    selectedAssetCode: String,
    chain: WalletChainItemData,
    selectedNetworkCode: String,
): String {
    if (selectedAssetCode.equals(PaymentConfig.AssetCode.USDT, ignoreCase = true)) {
        return PaymentConfig.AssetCode.USDT
    }
    return if (chain.networkCode.equals(selectedNetworkCode, ignoreCase = true)) {
        selectedAssetCode
    } else {
        chain.nativeAssetCode
    }
}
