package com.cryptovpn.navigation

/**
 * Legacy compatibility facade.
 * Active source of truth is [CryptoVpnRouteSpec].
 */
object Routes {
    const val SPLASH = "splash"
    const val FORCE_UPDATE = "force_update"
    const val OPTIONAL_UPDATE = "optional_update"
    const val EMAIL_LOGIN = "email_login"
    const val EMAIL_REGISTER = "email_register"
    const val RESET_PASSWORD = "reset_password"
    const val VPN_HOME = "vpn_home"
    const val PLANS = "plans"
    const val REGION_SELECTION = "region_selection"
    const val ORDER_CHECKOUT = "order_checkout/{planId}"
    const val WALLET_PAYMENT_CONFIRM = "wallet_payment_confirm/{orderId}"
    const val ORDER_RESULT = "order_result/{orderId}"
    const val ORDER_LIST = "order_list"
    const val ORDER_DETAIL = "order_detail/{orderId}"
    const val WALLET_ONBOARDING = "wallet_onboarding"
    const val WALLET_HOME = "wallet_home"
    const val WALLET_PAYMENT = "wallet_payment"
    const val ASSET_DETAIL = "asset_detail/{assetId}/{chainId}"
    const val RECEIVE = "receive/{assetId}/{chainId}"
    const val SEND = "send/{assetId}/{chainId}"
    const val SEND_RESULT = "send_result/{txId}"
    const val INVITE_CENTER = "invite_center"
    const val COMMISSION_LEDGER = "commission_ledger"
    const val WITHDRAW = "withdraw"
    const val PROFILE = "profile"
    const val LEGAL_DOCUMENTS = "legal_documents"
    const val LEGAL_DOCUMENT_DETAIL = "legal_document_detail/{documentId}"

    fun orderCheckout(planId: String) = CryptoVpnRouteSpec.orderCheckoutRoute(planId)
    fun walletPaymentConfirm(orderId: String) = CryptoVpnRouteSpec.walletPaymentConfirmRoute(orderId)
    fun orderResult(orderId: String) = CryptoVpnRouteSpec.orderResultRoute(orderId)
    fun orderDetail(orderId: String) = CryptoVpnRouteSpec.orderDetailRoute(orderId)
    fun assetDetail(assetId: String, chainId: String) = CryptoVpnRouteSpec.assetDetailRoute(assetId, chainId)
    fun receive(assetId: String, chainId: String) = CryptoVpnRouteSpec.receiveRoute(assetId, chainId)
    fun send(assetId: String, chainId: String) = CryptoVpnRouteSpec.sendRoute(assetId, chainId)
    fun sendResult(txId: String) = CryptoVpnRouteSpec.sendResultRoute(txId)
    fun legalDocumentDetail(documentId: String) = CryptoVpnRouteSpec.legalDocumentDetailRoute(documentId)
}
