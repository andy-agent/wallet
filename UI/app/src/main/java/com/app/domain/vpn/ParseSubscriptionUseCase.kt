package com.app.domain.vpn

import com.app.AppGraph

class ParseSubscriptionUseCase {
    operator fun invoke(payload: String) = AppGraph.vpnParser.parseSubscription(payload)
}
