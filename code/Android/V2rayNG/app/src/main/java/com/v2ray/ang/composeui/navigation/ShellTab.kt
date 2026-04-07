package com.v2ray.ang.composeui.navigation

enum class ShellTab(
    val key: String,
    val label: String,
) {
    HOME("home", "Home"),
    WALLET("wallet", "Wallet"),
    VPN("vpn", "VPN"),
    DISCOVER("discover", "Discover"),
    PROFILE("profile", "Profile"),
    ;

    companion object {
        fun fromKey(value: String?): ShellTab {
            return entries.firstOrNull { it.key == value?.trim()?.lowercase() } ?: HOME
        }
    }
}
