package com.app.core.utils

object Constants {
    const val APP_NAME = "vpn01"
    const val MOCK_SUBSCRIPTION_PAYLOAD = """
vmess://jp-tokyo-01
vless://sg-singapore-02
trojan://us-california-03
vmess://de-frankfurt-04
"""

    val DEFAULT_MNEMONIC = listOf(
        "crystal", "harbor", "velvet", "orbit", "ocean", "signal",
        "planet", "canyon", "anchor", "frozen", "lotus", "spark",
    )

    val DEFAULT_PRICE_SERIES = listOf(0.12f, 0.13f, 0.15f, 0.14f, 0.17f, 0.21f, 0.24f, 0.22f, 0.26f)
}
