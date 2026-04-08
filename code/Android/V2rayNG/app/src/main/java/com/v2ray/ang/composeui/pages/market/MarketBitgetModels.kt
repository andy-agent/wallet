package com.v2ray.ang.composeui.pages.market

import com.google.gson.JsonElement
import com.v2ray.ang.composeui.pages.vpn.VpnChartCandle
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

internal enum class MarketCategory(
    val key: String,
    val label: String,
) {
    FAVORITES("favorites", "自选"),
    HOT("hot", "热门"),
    AI("new_coin", "新币"),
    US_STOCKS("public_chain", "公链"),
    NIGHT("meme", "Meme"),
    ;

    companion object {
        fun fromKey(key: String): MarketCategory? = entries.firstOrNull { it.key == key }
    }
}

internal enum class MarketBoard(
    val key: String,
    val label: String,
    val columnLabel: String,
) {
    HOT("hot", "热门", "涨跌/热度"),
    GAINERS("gainers", "涨幅榜", "24H涨跌"),
    VOLUME("volume", "成交额", "成交额"),
    NEW("new_listing", "新币榜", "新热度"),
    ;
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
    val instrumentId: String,
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
    val turnover24hValue: Double,
    val heatRank: Int? = null,
    val listingRank: Int? = null,
)

internal data class MarketSpotlight(
    val instrumentId: String,
    val symbol: String,
    val eyebrow: String,
    val title: String,
    val subtitle: String,
    val primaryValue: String,
    val secondaryValue: String,
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
    val tradeActionLabel: String,
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

internal fun MarketOverviewPayload.toMarketQuotes(): List<MarketQuote> {
    return rows.map { row ->
        val precision = row.instrument.displayPrecision.coerceIn(0, 8)
        val tags = row.instrument.tags.map { it.toMarketTag() }
        val categories = row.instrument.categoryKeys.mapNotNull { MarketCategory.fromKey(it) }.toSet()
        MarketQuote(
            instrumentId = row.instrument.instrumentId,
            symbol = row.instrument.symbol,
            name = row.instrument.displayName,
            market = row.instrument.marketLabel,
            lastPrice = formatMarketPrice(row.ticker24h.lastPrice, precision),
            changeAmount = formatSignedPrice(row.ticker24h.absChange24h, precision),
            changePercent = formatSignedPercent(row.ticker24h.pctChange24h),
            volume24h = formatCompactUsd(row.ticker24h.turnover24h),
            marketCap = formatCompactUsd(row.ticker24h.marketCap),
            peRatio = formatRatio(row.ticker24h.peRatio),
            dayRange = formatRange(
                low = row.ticker24h.low24h,
                high = row.ticker24h.high24h,
                precision = precision,
            ),
            categories = categories,
            tags = tags,
            isFavorite = row.instrument.favorite,
            changeRateValue = row.ticker24h.pctChange24h?.toFloatOrNull() ?: 0f,
            turnover24hValue = row.ticker24h.turnover24h?.toDoubleOrNull() ?: 0.0,
            heatRank = row.rankSignals.heatRank,
            listingRank = row.rankSignals.listingRank,
        )
    }
}

internal fun MarketSpotlightsPayload.toMarketSpotlights(): List<MarketSpotlight> {
    return items.map { item ->
        MarketSpotlight(
            instrumentId = item.instrumentId,
            symbol = item.symbol,
            eyebrow = item.eyebrow,
            title = item.title,
            subtitle = item.subtitle,
            primaryValue = formatMetricValue(item.primaryMetric),
            secondaryValue = formatMetricValue(item.secondaryMetric),
        )
    }
}

internal fun MarketInstrumentDetailPayload.toMarketQuoteDetail(
    ranges: List<MarketTimeframe>,
): MarketQuoteDetail {
    val precision = instrument.displayPrecision.coerceIn(0, 8)
    val tagsSummary = instrument.tags.joinToString(" / ") { it.label }.ifBlank { instrument.marketLabel }
    val detailPairs = detailFacts.map { fact ->
        fact.label to formatFactValue(
            key = fact.key,
            value = fact.value,
            precision = precision,
        )
    }.toMutableList()
    if (!tradeAction.target.isNullOrBlank()) {
        detailPairs += "目标" to tradeAction.target
    }
    return MarketQuoteDetail(
        symbol = instrument.symbol,
        companyName = instrument.displayName,
        marketLabel = "${instrument.marketLabel} · 行情",
        lastPrice = formatMarketPrice(ticker24h.lastPrice, precision),
        changeAmount = formatSignedPrice(ticker24h.absChange24h, precision),
        changePercent = formatSignedPercent(ticker24h.pctChange24h),
        sessionLabel = instrument.sessionLabel ?: instrument.marketLabel,
        metrics = listOf(
            MarketDetailMetric("24H 最高", formatMarketPrice(ticker24h.high24h, precision)),
            MarketDetailMetric("24H 最低", formatMarketPrice(ticker24h.low24h, precision)),
            MarketDetailMetric("成交额", formatCompactUsd(ticker24h.turnover24h)),
            MarketDetailMetric("总市值", formatCompactUsd(ticker24h.marketCap)),
            MarketDetailMetric("标签", tagsSummary),
        ),
        ranges = ranges,
        indicators = supportedIndicators,
        overviewFacts = overviewFacts.map { fact ->
            fact.label to formatFactValue(
                key = fact.key,
                value = fact.value,
                precision = precision,
            )
        },
        detailFacts = detailPairs,
        tradeActionLabel = tradeAction.label.ifBlank { "查看市场" },
    )
}

internal fun MarketCandlesPayload.toMarketTimeframe(
    label: String,
    precision: Int,
): MarketTimeframe {
    val entries = candles.mapNotNull { candle ->
        val open = candle.open?.toFloatOrNull() ?: return@mapNotNull null
        val close = candle.close?.toFloatOrNull() ?: return@mapNotNull null
        val high = candle.high?.toFloatOrNull() ?: return@mapNotNull null
        val low = candle.low?.toFloatOrNull() ?: return@mapNotNull null
        VpnChartCandle(
            open = open,
            close = close,
            high = high,
            low = low,
        )
    }
    val highs = entries.map { it.high }
    val lows = entries.map { it.low }
    val max = highs.maxOrNull()
    val min = lows.minOrNull()
    val rightLabels = if (max != null && min != null) {
        val step = (max - min).takeIf { it > 0f }?.div(3f) ?: 0f
        listOf(max, max - step, max - (step * 2f), min).map { value ->
            formatPlainNumber(value.toBigDecimal(), precision)
        }
    } else {
        emptyList()
    }
    val bottomLabels = candles.sampleBottomLabels(timeframe)
    val calloutLines = candles.lastOrNull()?.let { candle ->
        buildList {
            add("时间" to formatCandleTime(candle.closeTime, timeframe))
            add("开盘" to formatMarketPrice(candle.open, precision))
            add("最高" to formatMarketPrice(candle.high, precision))
            add("最低" to formatMarketPrice(candle.low, precision))
            add("收盘" to formatMarketPrice(candle.close, precision))
            val change = candle.close?.toBigDecimalOrNull()
                ?.subtract(candle.open?.toBigDecimalOrNull() ?: BigDecimal.ZERO)
            add("涨跌额" to formatSignedPrice(change?.toPlainString(), precision))
            val pct = candle.open?.toBigDecimalOrNull()
                ?.takeIf { it.compareTo(BigDecimal.ZERO) != 0 }
                ?.let { open ->
                    change?.multiply(BigDecimal("100"))?.divide(open, 6, RoundingMode.HALF_UP)
                }
            add("涨跌幅" to formatSignedPercent(pct?.toPlainString()))
        }
    } ?: emptyList()
    return MarketTimeframe(
        label = label,
        candles = entries,
        rightLabels = rightLabels,
        bottomLabels = bottomLabels,
        calloutLines = calloutLines,
    )
}

internal fun MarketBoard.primaryMetric(quote: MarketQuote): String {
    return when (this) {
        MarketBoard.HOT -> quote.changePercent
        MarketBoard.GAINERS -> quote.changePercent
        MarketBoard.VOLUME -> quote.volume24h
        MarketBoard.NEW -> quote.listingRank?.let { "热度 $it" } ?: "--"
    }
}

internal fun MarketBoard.secondaryMetric(quote: MarketQuote): String {
    return when (this) {
        MarketBoard.HOT -> quote.heatRank?.let { "热度 $it" } ?: "热度 --"
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
        MarketBoard.HOT -> filtered.sortedWith(
            compareBy<MarketQuote> { it.heatRank ?: Int.MAX_VALUE }
                .thenByDescending { it.changeRateValue },
        )

        MarketBoard.GAINERS -> filtered.sortedByDescending { it.changeRateValue }
        MarketBoard.VOLUME -> filtered.sortedByDescending { it.turnover24hValue }
        MarketBoard.NEW -> filtered.sortedWith(
            compareBy<MarketQuote> { it.listingRank ?: Int.MAX_VALUE }
                .thenByDescending { it.changeRateValue },
        )
    }
}

private fun MarketTagDto.toMarketTag(): MarketTag {
    return MarketTag(
        label = label,
        tone = when (tone) {
            "accent" -> MarketTagTone.ACCENT
            "positive" -> MarketTagTone.POSITIVE
            "negative" -> MarketTagTone.NEGATIVE
            else -> MarketTagTone.NEUTRAL
        },
    )
}

private fun formatMetricValue(metric: MarketMetricDto): String {
    val raw = metric.value
    return when {
        raw.isNullOrBlank() -> "--"
        metric.label.contains("涨跌") -> formatSignedPercent(raw)
        metric.label.contains("成交额") -> formatCompactUsd(raw)
        else -> raw
    }
}

private fun formatFactValue(
    key: String,
    value: JsonElement?,
    precision: Int,
): String {
    return when (key) {
        "range24h" -> {
            val low = value?.asJsonObject?.get("low")?.asStringOrNull()
            val high = value?.asJsonObject?.get("high")?.asStringOrNull()
            formatRange(low = low, high = high, precision = precision)
        }

        "turnover24h", "marketCap" -> formatCompactUsd(value.asStringOrNull())
        "absChange24h" -> formatSignedPrice(value.asStringOrNull(), precision)
        "pctChange24h" -> formatSignedPercent(value.asStringOrNull())
        "conceptTags", "boards", "tags" -> value.asStringList().ifEmpty { listOf("--") }.joinToString(" / ")
        else -> when {
            value == null || value.isJsonNull -> "--"
            value.isJsonArray -> value.asStringList().joinToString(" / ").ifBlank { "--" }
            else -> value.asStringOrNull().orEmpty().ifBlank { "--" }
        }
    }
}

private fun formatRange(
    low: String?,
    high: String?,
    precision: Int,
): String {
    if (low.isNullOrBlank() || high.isNullOrBlank()) return "--"
    return "${formatMarketPrice(low, precision)} - ${formatMarketPrice(high, precision)}"
}

private fun formatMarketPrice(
    value: String?,
    precision: Int,
): String {
    val decimal = value?.toBigDecimalOrNull() ?: return "--"
    return "$${formatPlainNumber(decimal, precision)}"
}

private fun formatSignedPrice(
    value: String?,
    precision: Int,
): String {
    val decimal = value?.toBigDecimalOrNull() ?: return "--"
    val sign = if (decimal.compareTo(BigDecimal.ZERO) >= 0) "+" else "-"
    return "$sign$${formatPlainNumber(decimal.abs(), precision)}"
}

private fun formatSignedPercent(value: String?): String {
    val decimal = value?.toBigDecimalOrNull() ?: return "--"
    val sign = if (decimal.compareTo(BigDecimal.ZERO) >= 0) "+" else "-"
    return "$sign${formatPlainNumber(decimal.abs(), 2)}%"
}

private fun formatCompactUsd(value: String?): String {
    val decimal = value?.toBigDecimalOrNull() ?: return "--"
    val abs = decimal.abs()
    val divisor = when {
        abs >= BigDecimal("1000000000000") -> BigDecimal("1000000000000") to "T"
        abs >= BigDecimal("1000000000") -> BigDecimal("1000000000") to "B"
        abs >= BigDecimal("1000000") -> BigDecimal("1000000") to "M"
        abs >= BigDecimal("1000") -> BigDecimal("1000") to "K"
        else -> BigDecimal.ONE to ""
    }
    val scaled = decimal.divide(divisor.first, 4, RoundingMode.HALF_UP)
    return "$${formatPlainNumber(scaled, 2)}${divisor.second}"
}

private fun formatRatio(value: String?): String {
    val decimal = value?.toBigDecimalOrNull() ?: return "--"
    return formatPlainNumber(decimal, 2)
}

private fun formatPlainNumber(
    value: BigDecimal,
    precision: Int,
): String {
    val maxFractionDigits = precision.coerceIn(0, 8)
    val minFractionDigits = when {
        maxFractionDigits == 0 -> 0
        value.abs() >= BigDecimal.ONE -> minOf(2, maxFractionDigits)
        else -> minOf(maxFractionDigits, 2)
    }
    val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = minFractionDigits
        maximumFractionDigits = maxFractionDigits
        roundingMode = RoundingMode.HALF_UP
    }
    return formatter.format(value)
}

private fun List<MarketCandleDto>.sampleBottomLabels(timeframe: String): List<String> {
    if (isEmpty()) return emptyList()
    val indexes = listOf(
        0,
        size / 3,
        (size * 2) / 3,
        size - 1,
    ).distinct().sorted()
    return indexes.map { index ->
        formatBottomLabel(this[index].openTime, timeframe)
    }
}

private fun formatBottomLabel(timestamp: Long, timeframe: String): String {
    val formatter = when (timeframe) {
        "1h" -> DateTimeFormatter.ofPattern("HH:mm")
        "4h", "12h" -> DateTimeFormatter.ofPattern("MM/dd HH:mm")
        else -> DateTimeFormatter.ofPattern("MM/dd")
    }
    return Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}

private fun formatCandleTime(timestamp: Long, timeframe: String): String {
    val formatter = when (timeframe) {
        "1h" -> DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        "4h", "12h" -> DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        else -> DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }
    return Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}

private fun JsonElement?.asStringOrNull(): String? {
    return when {
        this == null || isJsonNull -> null
        isJsonPrimitive -> asString
        else -> null
    }
}

private fun JsonElement?.asStringList(): List<String> {
    return when {
        this == null || isJsonNull || !isJsonArray -> emptyList()
        else -> asJsonArray.mapNotNull { element -> element.asStringOrNull() }
    }
}
