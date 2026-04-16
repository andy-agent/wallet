package com.app.vpncore.parser

import com.app.vpncore.model.VpnNode
import com.app.vpncore.model.VpnProtocol

class VpnParser {
    fun parseSubscription(payload: String): List<VpnNode> {
        return payload
            .lineSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .mapIndexed { index, line ->
                val protocol = when {
                    line.startsWith("vmess://", true) -> VpnProtocol.VMESS
                    line.startsWith("vless://", true) -> VpnProtocol.VLESS
                    else -> VpnProtocol.TROJAN
                }
                val country = listOf("JP", "SG", "US", "DE", "HK")[index % 5]
                VpnNode(
                    id = "$country-$index",
                    name = "$country · ${protocol.name.lowercase()} ${index + 1}",
                    protocol = protocol,
                    host = "$country.lowercase().vpn01.app",
                    port = 443 + index,
                    country = country,
                    latencyMs = 48 + index * 18,
                    isPremium = index % 2 == 0,
                    rawConfig = line,
                )
            }
            .toList()
    }
}
