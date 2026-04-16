package com.v2ray.ang.composeui.common.repository

import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureMetric
import com.v2ray.ang.composeui.p2.model.SendChoiceUi
import com.v2ray.ang.composeui.p2.model.SendRouteArgs
import com.v2ray.ang.composeui.p2.model.SendUiState
import com.v2ray.ang.payment.data.api.WalletAssetItemData
import com.v2ray.ang.payment.data.api.WalletChainItemData
import com.v2ray.ang.payment.data.api.WalletOverviewData
import java.util.Locale

internal fun WalletOverviewData.toSendUiState(
    args: SendRouteArgs,
    currentOrderId: String? = null,
): SendUiState {
    val selectedChain = resolveSelectedSendChain(args)
    val assetsForChain = assetItems.filter { asset ->
        asset.walletVisible && asset.networkCode.equals(selectedChain?.networkCode, ignoreCase = true)
    }
    val selectedAsset = resolveSelectedSendAsset(args, assetsForChain)
    val networkLabel = receiveDisplayChainLabel(selectedChain?.networkCode ?: sendChainIdToNetworkCode(args.chainId))
    val assetLabel = selectedAsset?.assetCode ?: args.assetId
    val currentAssetValue = "$assetLabel · $networkLabel"
    val availableBalance = selectedAsset?.availableBalanceUiAmount
        ?.takeIf { !it.isNullOrBlank() && !it.equals("null", ignoreCase = true) }
        ?: "--"
    val route = SendRouteArgs(
        assetId = assetLabel,
        chainId = sendNetworkCodeToChainId(selectedChain?.networkCode ?: sendChainIdToNetworkCode(args.chainId)),
    )
    val memoField = currentOrderId
        ?.takeIf { it.isNotBlank() }
        ?.let {
            FeatureField(
                key = "memo",
                label = "备注",
                value = it,
                supportingText = "订单号 / 对账",
            )
        }
    return SendUiState(
        badge = "",
        subtitle = "",
        summary = "",
        availableBalance = availableBalance,
        balanceSupportingText = "",
        metrics = listOf(
            FeatureMetric("当前资产", currentAssetValue),
            FeatureMetric("可发送余额", availableBalance),
            FeatureMetric("广播能力", describeBroadcastCapability(selectedChain)),
            FeatureMetric("预检查", "已接发送前校验"),
        ),
        fields = buildList {
            add(FeatureField("to", "收款地址", "", "粘贴或扫码 $networkLabel 地址"))
            add(
                FeatureField(
                    "amount",
                    "发送数量",
                    "",
                    availableBalance.takeUnless { it == "--" } ?: "输入数量",
                ),
            )
            memoField?.let(::add)
        },
        highlights = emptyList(),
        checklist = emptyList(),
        note = "",
        networkOptions = chainItems.map { chain ->
            SendChoiceUi(
                id = sendNetworkCodeToChainId(chain.networkCode),
                label = receiveDisplayChainLabel(chain.networkCode),
                selected = chain.networkCode.equals(selectedChain?.networkCode, ignoreCase = true),
            )
        },
        assetOptions = assetsForChain.map { asset ->
            SendChoiceUi(
                id = asset.assetCode,
                label = asset.assetCode,
                selected = asset.assetCode.equals(selectedAsset?.assetCode, ignoreCase = true),
            )
        },
        currentRoute = route,
        feedbackMessage = null,
    )
}

internal fun sendChainIdToNetworkCode(chainId: String): String = when (chainId.lowercase(Locale.ROOT)) {
    "sol", "solana" -> "SOLANA"
    else -> "TRON"
}

internal fun sendNetworkCodeToChainId(networkCode: String): String = when (networkCode.uppercase(Locale.ROOT)) {
    "SOLANA" -> "solana"
    else -> "tron"
}

private fun WalletOverviewData.resolveSelectedSendChain(args: SendRouteArgs): WalletChainItemData? {
    val requested = sendChainIdToNetworkCode(args.chainId)
    return chainItems.firstOrNull { chain ->
        chain.networkCode.equals(requested, ignoreCase = true)
    } ?: chainItems.firstOrNull { chain ->
        chain.networkCode.equals(selectedNetworkCode, ignoreCase = true)
    } ?: chainItems.firstOrNull()
}

private fun resolveSelectedSendAsset(
    args: SendRouteArgs,
    assetsForChain: List<WalletAssetItemData>,
): WalletAssetItemData? {
    return assetsForChain.firstOrNull { asset ->
        asset.assetCode.equals(args.assetId, ignoreCase = true)
    } ?: assetsForChain.firstOrNull()
}

private fun describeBroadcastCapability(chain: WalletChainItemData?): String {
    if (chain == null) {
        return "能力未知"
    }
    return when {
        chain.directBroadcastEnabled && chain.proxyBroadcastEnabled -> "后端支持直连 / 代理"
        chain.proxyBroadcastEnabled -> "后端仅支持代理"
        chain.directBroadcastEnabled -> "后端仅支持直连"
        else -> "后端未开放广播"
    }
}
