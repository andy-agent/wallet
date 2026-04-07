package com.v2ray.ang.composeui.navigation

import android.net.Uri

object Routes {
    // Splash & Version
    const val SPLASH = "splash"
    const val FORCE_UPDATE = "force_update"
    const val OPTIONAL_UPDATE = "optional_update"

    // Shell
    const val APP_SHELL = "app_shell"

    // Auth
    const val EMAIL_LOGIN = "email_login"
    const val EMAIL_REGISTER = "email_register"
    const val RESET_PASSWORD = "reset_password"

    // VPN
    const val VPN_HOME = "vpn_home"
    const val PLANS = "plans"
    const val REGION_SELECTION = "region_selection"
    const val ORDER_CHECKOUT = "order_checkout"
    const val ORDER_CHECKOUT_TEMPLATE = "order_checkout/{planId}"
    const val WALLET_PAYMENT_CONFIRM = "wallet_payment_confirm"
    const val ORDER_RESULT = "order_result"
    const val ORDER_LIST = "order_list"
    const val ORDER_DETAIL = "order_detail"
    const val ORDER_DETAIL_TEMPLATE = "order_detail/{orderId}"

    // Wallet
    const val WALLET_ONBOARDING = "wallet_onboarding"
    const val WALLET_HOME = "wallet_home"
    const val ASSET_DETAIL = "asset_detail"
    const val ASSET_DETAIL_TEMPLATE = "asset_detail/{assetId}"
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
    const val LEGAL_DOCUMENT_DETAIL = "legal_document_detail"
    const val LEGAL_DOCUMENT_DETAIL_TEMPLATE = "legal_document_detail/{documentId}"

    data class RouteMatch(
        val pattern: String,
        val route: String,
        val args: Map<String, String> = emptyMap(),
        val template: String = pattern,
    )

    private val staticRoutes = setOf(
        SPLASH,
        FORCE_UPDATE,
        OPTIONAL_UPDATE,
        APP_SHELL,
        EMAIL_LOGIN,
        EMAIL_REGISTER,
        RESET_PASSWORD,
        VPN_HOME,
        PLANS,
        REGION_SELECTION,
        WALLET_PAYMENT_CONFIRM,
        ORDER_RESULT,
        ORDER_LIST,
        WALLET_ONBOARDING,
        WALLET_HOME,
        RECEIVE,
        SEND,
        SEND_RESULT,
        WALLET_PAYMENT,
        INVITE_CENTER,
        COMMISSION_LEDGER,
        WITHDRAW,
        PROFILE,
        LEGAL_DOCUMENTS,
    )

    fun normalize(route: String?): String {
        val candidate = route?.trim().orEmpty()
        if (candidate.isBlank()) return SPLASH
        return resolve(candidate)?.route ?: fallback(candidate)
    }

    fun resolve(route: String?): RouteMatch? {
        val candidate = route?.trim().orEmpty()
        if (candidate.isBlank()) return null
        val base = routeBase(candidate)
        val queryArgs = queryArgs(candidate)

        if (base in staticRoutes) {
            return RouteMatch(pattern = base, route = candidate, args = queryArgs)
        }

        val matches = listOfNotNull(
            matchPathArg(candidate, ORDER_CHECKOUT, "planId", queryArgs),
            matchPathArg(candidate, ORDER_DETAIL, "orderId", queryArgs),
            matchPathArg(candidate, ASSET_DETAIL, "assetId", queryArgs),
            matchPathArg(candidate, LEGAL_DOCUMENT_DETAIL, "documentId", queryArgs),
        )
        return matches.firstOrNull()
    }

    fun routeBase(route: String): String = route.substringBefore('?')

    fun queryParam(route: String, key: String): String? {
        if (!route.contains('?')) return null
        return Uri.parse("${DeepLinks.BASE_URI}/$route").getQueryParameter(key)
    }

    fun appShell(tab: ShellTab = ShellTab.HOME): String = withQuery(
        APP_SHELL,
        mapOf("tab" to tab.key),
    )

    fun orderCheckout(planId: String): String = withPathArg(ORDER_CHECKOUT, planId)
    fun orderCheckoutRoute(planId: String): String = orderCheckout(planId)

    fun orderDetail(orderId: String): String = withPathArg(ORDER_DETAIL, orderId)
    fun orderDetailRoute(orderId: String): String = orderDetail(orderId)

    fun assetDetail(assetId: String): String = withPathArg(ASSET_DETAIL, assetId)
    fun assetDetailRoute(assetId: String): String = assetDetail(assetId)

    fun legalDocumentDetail(documentId: String): String = withPathArg(LEGAL_DOCUMENT_DETAIL, documentId)
    fun legalDocumentDetailRoute(documentId: String): String = legalDocumentDetail(documentId)

    fun walletPaymentConfirm(
        orderId: String? = null,
        amount: String? = null,
    ): String = withQuery(
        WALLET_PAYMENT_CONFIRM,
        mapOf(
            "orderId" to orderId,
            "amount" to amount,
        ),
    )

    fun orderResult(
        orderId: String? = null,
        resultType: String? = null,
    ): String = withQuery(
        ORDER_RESULT,
        mapOf(
            "orderId" to orderId,
            "resultType" to resultType,
        ),
    )

    fun send(symbol: String? = null): String = withQuery(
        SEND,
        mapOf("symbol" to symbol),
    )

    fun sendResult(resultType: String? = null): String = withQuery(
        SEND_RESULT,
        mapOf("resultType" to resultType),
    )

    fun walletPayment(
        orderId: String? = null,
        planName: String? = null,
        amount: String? = null,
    ): String = withQuery(
        WALLET_PAYMENT,
        mapOf(
            "orderId" to orderId,
            "planName" to planName,
            "amount" to amount,
        ),
    )

    private fun matchPathArg(
        route: String,
        baseRoute: String,
        argName: String,
        queryArgs: Map<String, String>,
    ): RouteMatch? {
        val base = routeBase(route)
        val prefix = "$baseRoute/"
        if (!base.startsWith(prefix)) return null
        val arg = Uri.decode(base.removePrefix(prefix))
            .takeIf { it.isNotBlank() && !(it.startsWith("{") && it.endsWith("}")) }
            ?: return null
        return RouteMatch(
            pattern = baseRoute,
            route = route,
            args = queryArgs + mapOf(argName to arg),
            template = when (baseRoute) {
                ORDER_CHECKOUT -> ORDER_CHECKOUT_TEMPLATE
                ORDER_DETAIL -> ORDER_DETAIL_TEMPLATE
                ASSET_DETAIL -> ASSET_DETAIL_TEMPLATE
                LEGAL_DOCUMENT_DETAIL -> LEGAL_DOCUMENT_DETAIL_TEMPLATE
                else -> baseRoute
            },
        )
    }

    private fun queryArgs(route: String): Map<String, String> {
        if (!route.contains('?')) return emptyMap()
        val uri = Uri.parse("${DeepLinks.BASE_URI}/$route")
        return uri.queryParameterNames.associateWith { key ->
            uri.getQueryParameter(key).orEmpty()
        }.filterValues { it.isNotBlank() }
    }

    private fun withPathArg(baseRoute: String, arg: String): String =
        "$baseRoute/${Uri.encode(arg)}"

    private fun withQuery(baseRoute: String, rawArgs: Map<String, String?>): String {
        val args = rawArgs.filterValues { !it.isNullOrBlank() }
        if (args.isEmpty()) return baseRoute

        return buildString {
            append(baseRoute)
            append('?')
            append(
                args.entries.joinToString("&") { (key, value) ->
                    "${Uri.encode(key)}=${Uri.encode(value)}"
                },
            )
        }
    }

    private fun fallback(route: String): String {
        val base = routeBase(route)
        return when {
            base == ORDER_CHECKOUT || base.startsWith("$ORDER_CHECKOUT/") -> PLANS
            base == ORDER_DETAIL || base.startsWith("$ORDER_DETAIL/") -> ORDER_LIST
            base == ASSET_DETAIL || base.startsWith("$ASSET_DETAIL/") -> WALLET_HOME
            base == LEGAL_DOCUMENT_DETAIL || base.startsWith("$LEGAL_DOCUMENT_DETAIL/") -> LEGAL_DOCUMENTS
            else -> SPLASH
        }
    }

    object DeepLinks {
        const val BASE_URI = "cryptovpn://app"
        const val ORDER = "$BASE_URI/order/{orderId}"
        const val INVITE = "$BASE_URI/invite/{code}"
        const val WALLET = "$BASE_URI/wallet"
        const val VPN = "$BASE_URI/vpn"
    }
}
