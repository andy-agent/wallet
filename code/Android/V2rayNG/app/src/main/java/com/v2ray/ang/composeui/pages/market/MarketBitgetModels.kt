package com.v2ray.ang.composeui.pages.market

import com.v2ray.ang.composeui.pages.vpn.VpnChartCandle

internal enum class MarketCategory(val label: String) {
    FAVORITES("自选"),
    HOT("热门"),
    AI("AI"),
    US_STOCKS("美股"),
    NIGHT("夜盘"),
}

internal enum class MarketBoard(val label: String) {
    HOT("热门"),
    GAINERS("涨幅榜"),
    VOLUME("成交额"),
    NEW("新上线"),
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
        symbol = "CRWVon",
        name = "CoreWeave",
        market = "NASDAQ",
        lastPrice = "\$90.01",
        changeAmount = "+9.56",
        changePercent = "+11.89%",
        volume24h = "\$5.30B",
        marketCap = "\$38.62B",
        peRatio = "92.44",
        dayRange = "\$80.45 - \$92.52",
        categories = setOf(MarketCategory.HOT, MarketCategory.AI, MarketCategory.US_STOCKS, MarketCategory.NIGHT),
        tags = listOf(
            MarketTag("夜盘", MarketTagTone.ACCENT),
            MarketTag("AI", MarketTagTone.POSITIVE),
        ),
        isFavorite = true,
        changeRateValue = 11.89f,
        volumeRankValue = 5.3f,
        heatRankValue = 98f,
        listingRankValue = 96f,
    ),
    MarketQuote(
        symbol = "NVDA",
        name = "NVIDIA",
        market = "NASDAQ",
        lastPrice = "\$911.42",
        changeAmount = "+21.18",
        changePercent = "+2.38%",
        volume24h = "\$42.10B",
        marketCap = "\$2.26T",
        peRatio = "75.20",
        dayRange = "\$889.74 - \$918.50",
        categories = setOf(MarketCategory.HOT, MarketCategory.AI, MarketCategory.US_STOCKS),
        tags = listOf(MarketTag("龙头", MarketTagTone.ACCENT)),
        isFavorite = true,
        changeRateValue = 2.38f,
        volumeRankValue = 42.1f,
        heatRankValue = 99f,
        listingRankValue = 58f,
    ),
    MarketQuote(
        symbol = "MSTR",
        name = "MicroStrategy",
        market = "NASDAQ",
        lastPrice = "\$1,648.20",
        changeAmount = "+95.30",
        changePercent = "+6.14%",
        volume24h = "\$12.40B",
        marketCap = "\$29.80B",
        peRatio = "--",
        dayRange = "\$1,540.12 - \$1,667.00",
        categories = setOf(MarketCategory.HOT, MarketCategory.US_STOCKS),
        tags = listOf(MarketTag("BTC Beta", MarketTagTone.POSITIVE)),
        changeRateValue = 6.14f,
        volumeRankValue = 12.4f,
        heatRankValue = 93f,
        listingRankValue = 52f,
    ),
    MarketQuote(
        symbol = "TSLA",
        name = "Tesla",
        market = "NASDAQ",
        lastPrice = "\$212.40",
        changeAmount = "-5.20",
        changePercent = "-2.39%",
        volume24h = "\$18.60B",
        marketCap = "\$675.40B",
        peRatio = "61.70",
        dayRange = "\$208.12 - \$219.44",
        categories = setOf(MarketCategory.HOT, MarketCategory.US_STOCKS, MarketCategory.NIGHT),
        tags = listOf(MarketTag("波动", MarketTagTone.NEGATIVE)),
        changeRateValue = -2.39f,
        volumeRankValue = 18.6f,
        heatRankValue = 91f,
        listingRankValue = 47f,
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
        categories = setOf(MarketCategory.HOT),
        tags = listOf(MarketTag("Layer 1", MarketTagTone.NEUTRAL)),
        changeRateValue = 4.66f,
        volumeRankValue = 8.8f,
        heatRankValue = 88f,
        listingRankValue = 62f,
    ),
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
        tags = listOf(MarketTag("避险", MarketTagTone.ACCENT)),
        changeRateValue = 1.77f,
        volumeRankValue = 33.8f,
        heatRankValue = 95f,
        listingRankValue = 42f,
    ),
    MarketQuote(
        symbol = "AMD",
        name = "Advanced Micro Devices",
        market = "NASDAQ",
        lastPrice = "\$175.04",
        changeAmount = "+4.02",
        changePercent = "+2.35%",
        volume24h = "\$5.20B",
        marketCap = "\$283.10B",
        peRatio = "60.50",
        dayRange = "\$169.80 - \$176.10",
        categories = setOf(MarketCategory.AI, MarketCategory.US_STOCKS),
        tags = listOf(MarketTag("算力", MarketTagTone.POSITIVE)),
        changeRateValue = 2.35f,
        volumeRankValue = 5.2f,
        heatRankValue = 84f,
        listingRankValue = 55f,
    ),
    MarketQuote(
        symbol = "ARM",
        name = "Arm Holdings",
        market = "NASDAQ",
        lastPrice = "\$131.85",
        changeAmount = "+7.92",
        changePercent = "+6.39%",
        volume24h = "\$4.10B",
        marketCap = "\$137.00B",
        peRatio = "688.20",
        dayRange = "\$123.40 - \$133.08",
        categories = setOf(MarketCategory.AI, MarketCategory.US_STOCKS),
        tags = listOf(MarketTag("新热度", MarketTagTone.ACCENT)),
        changeRateValue = 6.39f,
        volumeRankValue = 4.1f,
        heatRankValue = 86f,
        listingRankValue = 89f,
    ),
)

internal val marketSampleSpotlights = listOf(
    MarketSpotlight(
        eyebrow = "夜盘异动",
        title = "CRWVon 拉升到榜首",
        subtitle = "Bitget-style quote detail anchor",
        primaryValue = "+11.89%",
        secondaryValue = "\$90.01",
        symbol = "CRWVon",
    ),
    MarketSpotlight(
        eyebrow = "AI 热点",
        title = "算力链继续扩散",
        subtitle = "NVDA / AMD / ARM",
        primaryValue = "3 只冲榜",
        secondaryValue = "AI 板块",
        symbol = "NVDA",
    ),
)

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
    return MarketQuoteDetail(
        symbol = quote.symbol,
        companyName = quote.name,
        marketLabel = "${quote.market} · Public quote",
        lastPrice = quote.lastPrice,
        changeAmount = quote.changeAmount,
        changePercent = quote.changePercent,
        sessionLabel = quote.tags.firstOrNull()?.label ?: "行情",
        metrics = listOf(
            MarketDetailMetric("当日最高", "\$92.52"),
            MarketDetailMetric("当日最低", "\$80.11"),
            MarketDetailMetric("成交额", "\$5.30B"),
            MarketDetailMetric("总市值", "\$38.62B"),
            MarketDetailMetric("市盈率", "92.44"),
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
            "当日开盘" to "\$80.32",
            "52 周最高" to "\$116.24",
            "52 周最低" to "\$39.80",
            "流通股本" to "4.29 亿",
            "所属赛道" to "AI 云 / GPU 基础设施",
        ),
        detailFacts = listOf(
            "交易所" to quote.market,
            "公司总部" to "New Jersey, US",
            "市值分层" to "Large Cap Growth",
            "近四季营收增速" to "81.2%",
            "分析师观点" to "继续关注 AI 基建订单兑现",
        ),
        thesis = listOf(
            "顶部结构对齐 `bitget_route_home.*`：双 tabs、左侧主价格、右侧关键指标列、时间切换、K 线和底部 CTA。",
            "列表入口与详情分离，Market 负责公开浏览与发现，避免继续把 Quote detail 伪装成 VPN 套餐详情。",
            "数据为页面骨架样本，后续可以直接替换为真实市场源而不改布局层级。",
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
