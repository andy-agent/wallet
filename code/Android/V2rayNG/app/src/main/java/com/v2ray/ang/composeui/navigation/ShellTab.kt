package com.v2ray.ang.composeui.navigation

enum class ShellTab(
    val key: String,
    val label: String,
) {
    HOME("home", "Home"),
    MARKET("market", "Market"),
    VPN("vpn", "VPN"),
    WALLET("wallet", "Wallet"),
    PROFILE("profile", "Profile"),
    ;

    companion object {
        fun fromKey(value: String?): ShellTab {
            val normalized = value?.trim()?.lowercase()
            return when (normalized) {
                "discover" -> MARKET
                else -> entries.firstOrNull { it.key == normalized } ?: HOME
            }
        }
    }
}
