package com.v2ray.ang.composeui.navigation

object Routes {
    // Splash & Version
    const val SPLASH = "splash"
    const val FORCE_UPDATE = "force_update"
    const val OPTIONAL_UPDATE = "optional_update"

    // Auth
    const val EMAIL_LOGIN = "email_login"
    const val EMAIL_REGISTER = "email_register"
    const val RESET_PASSWORD = "reset_password"

    // VPN
    const val VPN_HOME = "vpn_home"
    const val PLANS = "plans"
    const val REGION_SELECTION = "region_selection"
    const val ORDER_CHECKOUT = "order_checkout/{planId}"
    const val WALLET_PAYMENT_CONFIRM = "wallet_payment_confirm"
    const val ORDER_RESULT = "order_result"
    const val ORDER_LIST = "order_list"
    const val ORDER_DETAIL = "order_detail/{orderId}"

    // Wallet
    const val WALLET_ONBOARDING = "wallet_onboarding"
    const val WALLET_HOME = "wallet_home"
    const val ASSET_DETAIL = "asset_detail/{assetId}"
    const val RECEIVE = "receive"
    const val SEND = "send"
    const val SEND_RESULT = "send_result"
    const val WALLET_PAYMENT = "wallet_payment"

    // Growth
    const val INVITE_CENTER = "invite_center"
    const val COMMISSION_LEDGER = "commission_ledger"
    const val WITHDRAW = "withdraw"

    // Profile & Legal
    const val PROFILE = "profile"
    const val LEGAL_DOCUMENTS = "legal_documents"
    const val LEGAL_DOCUMENT_DETAIL = "legal_document_detail/{documentId}"

    // Helper functions for routes with parameters
    fun orderCheckout(planId: String) = "order_checkout/$planId"
    fun orderDetail(orderId: String) = "order_detail/$orderId"
    fun assetDetail(assetId: String) = "asset_detail/$assetId"
    fun legalDocumentDetail(documentId: String) = "legal_document_detail/$documentId"

    // Deep Link URIs
    object DeepLinks {
        const val BASE_URI = "cryptovpn://app"
        const val ORDER = "$BASE_URI/order/{orderId}"
        const val INVITE = "$BASE_URI/invite/{code}"
        const val WALLET = "$BASE_URI/wallet"
        const val VPN = "$BASE_URI/vpn"
    }
}