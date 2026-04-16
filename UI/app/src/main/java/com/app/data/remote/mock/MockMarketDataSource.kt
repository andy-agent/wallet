package com.app.data.remote.mock

import com.app.data.model.MarketSignal
import com.app.data.model.MarketTicker
import com.app.data.model.RiskSignal
import com.app.data.model.SignalSeverity
import com.app.data.model.TokenPricePoint
import com.app.data.remote.api.MarketApi
import com.app.data.remote.dto.MarketTickerDto

class MockMarketDataSource : MarketApi {
    private val tickers = listOf(
        MarketTickerDto("ENJ", "Enjin Coin", 0.24, 44.12, 246_000_000.0, 18_500_000.0),
        MarketTickerDto("SOL", "Solana", 172.4, -12.3, 73_500_000_000.0, 6_200_000_000.0),
        MarketTickerDto("ARB", "Arbitrum", 1.23, -18.6, 3_600_000_000.0, 910_000_000.0),
        MarketTickerDto("BTC", "Bitcoin", 64890.0, 2.1, 1_280_000_000_000.0, 23_800_000_000.0),
        MarketTickerDto("ETH", "Ethereum", 3240.12, 1.4, 388_000_000_000.0, 12_100_000_000.0),
        MarketTickerDto("AVAX", "Avalanche", 28.92, -9.8, 11_500_000_000.0, 820_000_000.0),
    )

    override suspend fun getOverview(): List<MarketTickerDto> = tickers
    override suspend fun getTicker(symbol: String): MarketTickerDto? = tickers.firstOrNull { it.symbol.equals(symbol, true) }

    fun abnormalSignals() = listOf(
        MarketSignal("m1", "ENJ", "资金流入异常", "24h 资金净流入显著高于均值", SignalSeverity.High),
        MarketSignal("m2", "SOL", "大额抛售", "链上检测到疑似大户分批卖出", SignalSeverity.High),
        MarketSignal("m3", "ARB", "波动率异常", "波动率升至近 30 日高位", SignalSeverity.Medium),
    )

    fun riskSignals(symbol: String) = when (symbol.uppercase()) {
        "ENJ" -> listOf(
            RiskSignal("r1", "风险信号", "短时追涨情绪明显，注意回撤风险。"),
            RiskSignal("r2", "波动率异常", "分钟级波动显著高于均值。"),
            RiskSignal("r3", "资金快速流入", "疑似聪明钱连续加仓。", positive = true),
        )
        else -> listOf(
            RiskSignal("r4", "回调压力", "短期均线承压，谨慎追高。"),
            RiskSignal("r5", "社交热度下降", "讨论热度较前一周期回落。"),
        )
    }

    fun priceSeries(symbol: String): List<TokenPricePoint> = when (symbol.uppercase()) {
        "ENJ" -> listOf("06:00" to 0.12f, "12:30" to 0.13f, "18:00" to 0.16f, "22:00" to 0.18f, "03:00" to 0.21f, "06:00" to 0.24f)
        else -> listOf("05:00" to 0.09f, "10:00" to 0.10f, "15:00" to 0.11f, "20:00" to 0.10f, "01:00" to 0.12f, "06:00" to 0.14f)
    }.map { TokenPricePoint(it.first, it.second) }

    fun watchlist(): List<MarketTicker> = tickers.takeLast(3).map { MarketTicker(it.symbol, it.name, it.priceUsd, it.change24h, it.marketCapUsd, it.volume24hUsd) }

    fun aiSummary(symbol: String): String = when (symbol.uppercase()) {
        "ENJ" -> "短期强势，但已进入高波动区。建议：谨慎追高，关注量价背离。"
        "SOL" -> "处于回调段，若成交量继续萎缩，可能再探支撑。"
        else -> "保持观察，等待趋势确认后再决策。"
    }
}
