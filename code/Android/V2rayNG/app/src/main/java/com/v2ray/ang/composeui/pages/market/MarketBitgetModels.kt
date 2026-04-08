package com.v2ray.ang.composeui.pages.market

import com.v2ray.ang.composeui.pages.vpn.VpnChartCandle

internal enum class MarketCategory(val label: String) {
    FAVORITES("自选"),
    HOT("热门"),
    AI("新币"),
    US_STOCKS("公链"),
    NIGHT("Meme"),
}

internal enum class MarketBoard(
    val label: String,
    val columnLabel: String,
) {
    HOT("热门", "涨跌/热度"),
    GAINERS("涨幅榜", "24H涨跌"),
    VOLUME("成交额", "成交额"),
    NEW("新币榜", "新热度"),
}

internal enum class MarketTagTone {
    ACCENT,
    POSITIVE,
    NEGATIVE,
    NEUTRAL,
}

internal data class MarketTag(
    val label: String,
    val tone: MarketTagTone = MarketTagTone.NEUTRAL,
)

internal data class MarketQuote(
    val symbol: String,
    val name: String,
    val market: String,
    val lastPrice: String,
    val changeAmount: String,
    val changePercent: String,
    val volume24h: String,
    val marketCap: String,
    val peRatio: String,
    val dayRange: String,
    val categories: Set<MarketCategory>,
    val tags: List<MarketTag> = emptyList(),
    val isFavorite: Boolean = false,
    val changeRateValue: Float,
    val volumeRankValue: Float,
    val heatRankValue: Float,
    val listingRankValue: Float,
)

internal data class MarketSpotlight(
    val eyebrow: String,
    val title: String,
    val subtitle: String,
    val primaryValue: String,
    val secondaryValue: String,
    val symbol: String,
)

internal data class MarketDetailMetric(
    val label: String,
    val value: String,
)

internal data class MarketTimeframe(
    val label: String,
    val candles: List<VpnChartCandle>,
    val rightLabels: List<String>,
    val bottomLabels: List<String>,
    val calloutLines: List<Pair<String, String>>,
)

internal data class MarketQuoteDetail(
    val symbol: String,
    val companyName: String,
    val marketLabel: String,
    val lastPrice: String,
    val changeAmount: String,
    val changePercent: String,
    val sessionLabel: String,
    val metrics: List<MarketDetailMetric>,
    val ranges: List<MarketTimeframe>,
    val indicators: List<String>,
    val overviewFacts: List<Pair<String, String>>,
    val detailFacts: List<Pair<String, String>>,
    val thesis: List<String>,
)

internal val marketOverviewCategories = listOf(
    MarketCategory.FAVORITES,
    MarketCategory.HOT,
    MarketCategory.AI,
    MarketCategory.US_STOCKS,
    MarketCategory.NIGHT,
)

internal val marketOverviewBoards = listOf(
    MarketBoard.HOT,
    MarketBoard.GAINERS,
    MarketBoard.VOLUME,
    MarketBoard.NEW,
)

internal val marketSampleQuotes = listOf(
    MarketQuote(
        symbol = "BTC",
        name = "Bitcoin",
        market = "CRYPTO",
        lastPrice = "\$71,220.00",
        changeAmount = "+1,240.00",
        changePercent = "+1.77%",
        volume24h = "\$33.80B",
        marketCap = "\$1.40T",
        peRatio = "--",
        dayRange = "\$69,880 - \$71,540",
        categories = setOf(MarketCategory.HOT),
        tags = listOf(
            MarketTag("主流", MarketTagTone.ACCENT),
        ),
        isFavorite = true,
        changeRateValue = 1.77f,
        volumeRankValue = 33.8f,
        heatRankValue = 99f,
        listingRankValue = 24f,
    ),
    MarketQuote(
        symbol = "ETH",
        name = "Ethereum",
        market = "CRYPTO",
        lastPrice = "\$3,640.82",
        changeAmount = "+118.42",
        changePercent = "+3.36%",
        volume24h = "\$18.40B",
        marketCap = "\$437.90B",
        peRatio = "--",
        dayRange = "\$3,488.20 - \$3,662.40",
        categories = setOf(MarketCategory.HOT),
        tags = listOf(MarketTag("主网", MarketTagTone.NEUTRAL)),
        isFavorite = true,
        changeRateValue = 3.36f,
        volumeRankValue = 18.4f,
        heatRankValue = 96f,
        listingRankValue = 20f,
    ),
    MarketQuote(
        symbol = "SOL",
        name = "Solana",
        market = "CRYPTO",
        lastPrice = "\$182.36",
        changeAmount = "+8.11",
        changePercent = "+4.66%",
        volume24h = "\$8.80B",
        marketCap = "\$82.70B",
        peRatio = "--",
        dayRange = "\$171.60 - \$184.22",
        categories = setOf(MarketCategory.HOT, MarketCategory.US_STOCKS),
        tags = listOf(MarketTag("公链", MarketTagTone.POSITIVE)),
        changeRateValue = 4.66f,
        volumeRankValue = 8.8f,
        heatRankValue = 91f,
        listingRankValue = 36f,
    ),
    MarketQuote(
        symbol = "BGB",
        name = "Bitget Token",
        market = "CRYPTO",
        lastPrice = "\$1.31",
        changeAmount = "+0.08",
        changePercent = "+6.50%",
        volume24h = "\$620.00M",
        marketCap = "\$1.83B",
        peRatio = "--",
        dayRange = "\$1.21 - \$1.34",
        categories = setOf(MarketCategory.HOT, MarketCategory.AI),
        tags = listOf(MarketTag("平台币", MarketTagTone.ACCENT)),
        changeRateValue = 6.50f,
        volumeRankValue = 0.62f,
        heatRankValue = 90f,
        listingRankValue = 64f,
    ),
    MarketQuote(
        symbol = "XRP",
        name = "XRP",
        market = "CRYPTO",
        lastPrice = "\$0.64",
        changeAmount = "+0.03",
        changePercent = "+4.91%",
        volume24h = "\$2.40B",
        marketCap = "\$35.20B",
        peRatio = "--",
        dayRange = "\$0.60 - \$0.65",
        categories = setOf(MarketCategory.HOT),
        tags = listOf(MarketTag("支付", MarketTagTone.NEUTRAL)),
        changeRateValue = 4.91f,
        volumeRankValue = 2.4f,
        heatRankValue = 88f,
        listingRankValue = 34f,
    ),
    MarketQuote(
        symbol = "DOGE",
        name = "Dogecoin",
        market = "CRYPTO",
        lastPrice = "\$0.18",
        changeAmount = "+0.01",
        changePercent = "+7.82%",
        volume24h = "\$1.90B",
        marketCap = "\$25.40B",
        peRatio = "--",
        dayRange = "\$0.16 - \$0.19",
        categories = setOf(MarketCategory.HOT, MarketCategory.NIGHT),
        tags = listOf(MarketTag("Meme", MarketTagTone.ACCENT)),
        changeRateValue = 7.82f,
        volumeRankValue = 1.9f,
        heatRankValue = 92f,
        listingRankValue = 48f,
    ),
    MarketQuote(
        symbol = "SUI",
        name = "Sui",
        market = "CRYPTO",
        lastPrice = "\$1.58",
        changeAmount = "+0.11",
        changePercent = "+7.48%",
        volume24h = "\$780.00M",
        marketCap = "\$4.11B",
        peRatio = "--",
        dayRange = "\$1.43 - \$1.61",
        categories = setOf(MarketCategory.AI, MarketCategory.US_STOCKS),
        tags = listOf(MarketTag("新币", MarketTagTone.POSITIVE)),
        changeRateValue = 7.48f,
        volumeRankValue = 0.78f,
        heatRankValue = 86f,
        listingRankValue = 94f,
    ),
    MarketQuote(
        symbol = "PEPE",
        name = "Pepe",
        market = "CRYPTO",
        lastPrice = "\$0.0000128",
        changeAmount = "+0.0000016",
        changePercent = "+14.29%",
        volume24h = "\$1.12B",
        marketCap = "\$5.38B",
        peRatio = "--",
        dayRange = "\$0.0000109 - \$0.0000132",
        categories = setOf(MarketCategory.NIGHT, MarketCategory.AI),
        tags = listOf(MarketTag("Meme", MarketTagTone.ACCENT)),
        changeRateValue = 14.29f,
        volumeRankValue = 1.12f,
        heatRankValue = 94f,
        listingRankValue = 97f,
    ),
)

internal val marketSampleSpotlights = listOf(
    MarketSpotlight(
        eyebrow = "热搜",
        title = "BTC",
        subtitle = "Bitcoin",
        primaryValue = "+1.77%",
        secondaryValue = "\$71,220.00",
        symbol = "BTC",
    ),
    MarketSpotlight(
        eyebrow = "新币冲榜",
        title = "SUI",
        subtitle = "Sui",
        primaryValue = "+7.48%",
        secondaryValue = "\$1.58",
        symbol = "SUI",
    ),
    MarketSpotlight(
        eyebrow = "Meme 热门",
        title = "PEPE",
        subtitle = "Pepe",
        primaryValue = "+14.29%",
        secondaryValue = "\$1.12B",
        symbol = "PEPE",
    ),
)

internal fun MarketBoard.primaryMetric(quote: MarketQuote): String {
    return when (this) {
        MarketBoard.HOT -> quote.changePercent
        MarketBoard.GAINERS -> quote.changePercent
        MarketBoard.VOLUME -> quote.volume24h
        MarketBoard.NEW -> "热度 ${quote.listingRankValue.toInt()}"
    }
}

internal fun MarketBoard.secondaryMetric(quote: MarketQuote): String {
    return when (this) {
        MarketBoard.HOT -> "热度 ${quote.heatRankValue.toInt()}"
        MarketBoard.GAINERS -> "成交额 ${quote.volume24h}"
        MarketBoard.VOLUME -> "24H ${quote.changePercent}"
        MarketBoard.NEW -> "24H ${quote.changePercent}"
    }
}

internal fun filterMarketQuotes(
    quotes: List<MarketQuote>,
    query: String,
    category: MarketCategory,
    board: MarketBoard,
): List<MarketQuote> {
    val normalizedQuery = query.trim()
    val filtered = quotes.filter { quote ->
        val matchesQuery = normalizedQuery.isBlank() ||
            quote.symbol.contains(normalizedQuery, ignoreCase = true) ||
            quote.name.contains(normalizedQuery, ignoreCase = true) ||
            quote.tags.any { it.label.contains(normalizedQuery, ignoreCase = true) }
        val matchesCategory = when (category) {
            MarketCategory.FAVORITES -> quote.isFavorite
            else -> quote.categories.contains(category)
        }
        matchesQuery && matchesCategory
    }
    return when (board) {
        MarketBoard.HOT -> filtered.sortedByDescending { it.heatRankValue }
        MarketBoard.GAINERS -> filtered.sortedByDescending { it.changeRateValue }
        MarketBoard.VOLUME -> filtered.sortedByDescending { it.volumeRankValue }
        MarketBoard.NEW -> filtered.sortedByDescending { it.listingRankValue }
    }
}

internal fun marketSampleQuoteDetail(quote: MarketQuote = marketSampleQuotes.first()): MarketQuoteDetail {
    val dayLow = quote.dayRange.substringBefore(" - ").trim()
    val dayHigh = quote.dayRange.substringAfter(" - ").trim()
    val tagSummary = quote.tags.joinToString(" / ") { it.label }.ifBlank { "主流市场" }
    return MarketQuoteDetail(
        symbol = quote.symbol,
        companyName = quote.name,
        marketLabel = "${quote.market} · 行情",
        lastPrice = quote.lastPrice,
        changeAmount = quote.changeAmount,
        changePercent = quote.changePercent,
        sessionLabel = quote.tags.firstOrNull()?.label ?: "行情",
        metrics = listOf(
            MarketDetailMetric("24H 最高", dayHigh),
            MarketDetailMetric("24H 最低", dayLow),
            MarketDetailMetric("成交额", quote.volume24h),
            MarketDetailMetric("总市值", quote.marketCap),
            MarketDetailMetric("标签", tagSummary),
        ),
        ranges = listOf(
            MarketTimeframe(
                label = "1小时",
                candles = buildMarketCandles(
                    base = 83.4f,
                    deltas = listOf(1.4f, -0.9f, 1.8f, 2.2f, -0.7f, 1.1f, 2.3f, -1.1f, 1.6f, 0.8f),
                ),
                rightLabels = listOf("92.5", "89.2", "85.8", "82.5"),
                bottomLabels = listOf("19:30", "20:15", "21:00", "21:45"),
                calloutLines = listOf(
                    "时间" to "2026-04-07 21:12",
                    "开盘" to "\$84.90",
                    "最高" to "\$92.52",
                    "最低" to "\$83.12",
                    "收盘" to "\$90.01",
                    "涨跌额" to "+9.56",
                    "涨跌幅" to "+11.89%",
                ),
            ),
            MarketTimeframe(
                label = "4小时",
                candles = buildMarketCandles(
                    base = 76.8f,
                    deltas = listOf(2.8f, -1.6f, 3.2f, 1.4f, -0.8f, 3.6f, -1.0f, 2.2f, 1.8f, 2.4f),
                ),
                rightLabels = listOf("95.0", "89.0", "83.0", "77.0"),
                bottomLabels = listOf("04/06", "04/07", "04/07", "04/08"),
                calloutLines = listOf(
                    "时间" to "2026-04-08 00:00",
                    "开盘" to "\$79.24",
                    "最高" to "\$92.52",
                    "最低" to "\$76.80",
                    "收盘" to "\$90.01",
                    "涨跌额" to "+10.77",
                    "涨跌幅" to "+13.60%",
                ),
            ),
            MarketTimeframe(
                label = "12小时",
                candles = buildMarketCandles(
                    base = 70.5f,
                    deltas = listOf(3.1f, 2.2f, -1.2f, 3.8f, -0.6f, 2.4f, 1.3f, -0.9f, 4.1f, 2.2f),
                ),
                rightLabels = listOf("96.0", "88.0", "80.0", "72.0"),
                bottomLabels = listOf("03/31", "04/02", "04/04", "04/08"),
                calloutLines = listOf(
                    "时间" to "2026-04-08",
                    "开盘" to "\$71.14",
                    "最高" to "\$92.52",
                    "最低" to "\$69.40",
                    "收盘" to "\$90.01",
                    "涨跌额" to "+18.87",
                    "涨跌幅" to "+26.51%",
                ),
            ),
            MarketTimeframe(
                label = "1天",
                candles = buildMarketCandles(
                    base = 61.2f,
                    deltas = listOf(2.4f, 3.3f, -1.5f, 2.1f, 4.5f, -0.8f, 3.4f, 1.7f, 2.0f, 3.1f),
                ),
                rightLabels = listOf("98.0", "86.0", "74.0", "62.0"),
                bottomLabels = listOf("01/10", "02/12", "03/14", "04/08"),
                calloutLines = listOf(
                    "时间" to "2026-04-08",
                    "开盘" to "\$63.80",
                    "最高" to "\$92.52",
                    "最低" to "\$61.20",
                    "收盘" to "\$90.01",
                    "涨跌额" to "+26.21",
                    "涨跌幅" to "+41.06%",
                ),
            ),
            MarketTimeframe(
                label = "更多",
                candles = buildMarketCandles(
                    base = 54.0f,
                    deltas = listOf(2.0f, 1.8f, 2.6f, -0.7f, 3.8f, 2.1f, -1.3f, 4.0f, 2.5f, 3.2f),
                ),
                rightLabels = listOf("100.0", "85.0", "70.0", "55.0"),
                bottomLabels = listOf("2025 Q3", "2025 Q4", "2026 Q1", "2026 Q2"),
                calloutLines = listOf(
                    "时间" to "近 4 季度",
                    "开盘" to "\$56.20",
                    "最高" to "\$92.52",
                    "最低" to "\$54.00",
                    "收盘" to "\$90.01",
                    "涨跌额" to "+33.81",
                    "涨跌幅" to "+60.16%",
                ),
            ),
        ),
        indicators = listOf("MA", "BOLL", "MACD", "KDJ", "RSI", "WR"),
        overviewFacts = listOf(
            "24H 区间" to quote.dayRange,
            "24H 成交额" to quote.volume24h,
            "总市值" to quote.marketCap,
            "涨跌额" to quote.changeAmount,
            "概念标签" to tagSummary,
        ),
        detailFacts = listOf(
            "交易所" to quote.market,
            "币种名称" to quote.name,
            "榜单位置" to "按 ${marketOverviewBoards.first().label} 浏览",
            "24H 涨跌" to quote.changePercent,
            "24H 区间" to quote.dayRange,
            "标签" to tagSummary,
        ),
        thesis = listOf(
            "详情页继续保留双 tabs、左侧主价格、右侧关键指标列、时间切换、K 线和底部 CTA 的骨架。",
            "榜单入口与详情页解耦，Market 负责浏览与发现，不再混入 VPN 套餐信息。",
            "当前数据仍是页面骨架样本，后续可替换为真实市场源而不改布局层级。",
        ),
    )
}

private fun buildMarketCandles(
    base: Float,
    deltas: List<Float>,
): List<VpnChartCandle> {
    var current = base
    return deltas.mapIndexed { index, delta ->
        val open = current + if (index % 2 == 0) -0.9f else 0.7f
        val close = open + delta
        val high = maxOf(open, close) + 1.4f + ((index % 3) * 0.35f)
        val low = minOf(open, close) - 1.3f - ((index % 2) * 0.25f)
        current = close
        VpnChartCandle(
            open = open,
            close = close,
            high = high,
            low = low,
        )
    }
}
