package com.app.data.remote.mock

import com.app.common.model.ChainUiModel
import com.app.data.model.Asset
import com.app.data.model.LegalDocument
import com.app.data.model.TokenPricePoint
import com.app.data.model.Transaction
import com.app.data.model.TransactionDirection
import com.app.data.model.TransactionStatus
import com.app.data.model.UserProfile
import com.app.data.model.WalletSetupOption
import com.app.core.utils.Constants

class MockWalletDataSource {
    fun assets() = listOf(
        Asset("eth", "ethereum", "ETH", "Ethereum", 1.64, 3240.20, 4.12, "0x49bF77A4e5d9f55E0aA3B0eTH"),
        Asset("usdt", "tron", "USDT", "Tether", 5491.42, 1.0, 0.0, "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t"),
        Asset("sol", "solana", "SOL", "Solana", 28.5, 172.40, -3.54, "So1anaDemoWalletAddress"),
        Asset("btc", "bitcoin", "BTC", "Bitcoin", 0.24, 64890.0, 2.1, "bc1qdemo49b7k8wallet"),
    )

    fun transactions() = listOf(
        Transaction("tx1", "USDT", "TRON", 149.0, 149.0, TransactionDirection.Payment, TransactionStatus.Confirmed, System.currentTimeMillis() - 3_600_000, "TR7...pay", "0xpay149"),
        Transaction("tx2", "SOL", "Solana", 1.24, 213.7, TransactionDirection.Send, TransactionStatus.Confirmed, System.currentTimeMillis() - 7_200_000, "So1...to", "0xsol124"),
        Transaction("tx3", "ETH", "Ethereum", 0.18, 583.2, TransactionDirection.Receive, TransactionStatus.Confirmed, System.currentTimeMillis() - 86_400_000, "0x49...eth", "0xeth018"),
    )

    fun profile() = UserProfile("u1", "Glow Ops", "ops@vpn01.app", "Level 4", 3, 24892.42, "VPN01-88A")

    fun walletSetupOptions() = listOf(
        WalletSetupOption("create", "创建新钱包", "生成一套新的多链助记词", "建议新用户使用"),
        WalletSetupOption("import_mnemonic", "导入助记词", "通过助记词恢复现有钱包", "适合多链资产迁移"),
        WalletSetupOption("watch_only", "观察钱包", "仅查看地址与资产，不导入私钥", "更安全的只读模式"),
    )

    fun mnemonicWords(): List<String> = Constants.DEFAULT_MNEMONIC

    fun chainItems() = listOf(
        ChainUiModel("ethereum", "Ethereum", "ETH", true),
        ChainUiModel("tron", "TRON", "TRX", true),
        ChainUiModel("solana", "Solana", "SOL", true),
        ChainUiModel("base", "Base", "BASE", false),
    )

    fun priceSeries(symbol: String) = when (symbol.uppercase()) {
        "ENJ" -> listOf("06:00" to 0.12f, "10:00" to 0.13f, "14:00" to 0.14f, "18:00" to 0.18f, "22:00" to 0.21f, "06:00" to 0.24f)
        "SOL" -> listOf("06:00" to 182.0f, "10:00" to 176.0f, "14:00" to 180.0f, "18:00" to 174.0f, "22:00" to 171.0f, "06:00" to 172.4f)
        else -> listOf("06:00" to 0.92f, "10:00" to 1.04f, "14:00" to 1.16f, "18:00" to 1.10f, "22:00" to 1.21f, "06:00" to 1.28f)
    }.map { TokenPricePoint(it.first, it.second) }

    fun legalDocs() = listOf(
        LegalDocument("terms", "服务协议", "账号、钱包与 VPN 使用规则", "1. 服务范围\\n2. 钱包自托管说明\\n3. VPN 节点使用规则\\n4. 退款与争议处理"),
        LegalDocument("privacy", "隐私政策", "设备信息与连接日志处理方式", "我们仅保留最小必要日志以支持风控、支付校验与 VPN 状态同步。"),
        LegalDocument("risk", "风险披露", "数字资产与跨链使用风险", "数字资产价格波动明显，跨链桥接与节点连接均可能受第三方网络环境影响。"),
    )
}
