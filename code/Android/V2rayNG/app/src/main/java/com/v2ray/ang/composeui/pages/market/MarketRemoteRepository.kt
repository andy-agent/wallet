package com.v2ray.ang.composeui.pages.market

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import com.v2ray.ang.BuildConfig
import com.v2ray.ang.payment.PaymentConfig
import com.v2ray.ang.payment.data.repository.PaymentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

internal class MarketRemoteRepository {
    private val api: MarketRemoteApi by lazy {
        val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                override fun checkClientTrusted(
                    chain: Array<java.security.cert.X509Certificate>,
                    authType: String,
                ) = Unit

                override fun checkServerTrusted(
                    chain: Array<java.security.cert.X509Certificate>,
                    authType: String,
                ) = Unit

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
            },
        )
        val sslContext = SSLContext.getInstance("SSL").apply {
            init(null, trustAllCerts, java.security.SecureRandom())
        }
        val client = OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier(HostnameVerifier { _, _ -> true })
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("User-Agent", "V2rayNG/${BuildConfig.VERSION_NAME} (Android)")
                    .addHeader("Accept", "application/json")
                    .addHeader("X-Client-Version", BuildConfig.VERSION_NAME)
                    .build()
                chain.proceed(request)
            }
            .build()

        Retrofit.Builder()
            .baseUrl(PaymentConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(PaymentRepository.createPaymentApiGson()))
            .build()
            .create(MarketRemoteApi::class.java)
    }

    suspend fun getOverview(): Result<MarketOverviewPayload> = request("加载行情概览失败") {
        api.getOverview()
    }

    suspend fun getSpotlights(): Result<MarketSpotlightsPayload> = request("加载热点卡片失败") {
        api.getSpotlights()
    }

    suspend fun resolveInstrumentId(symbol: String): Result<String> {
        if (symbol.isBlank()) {
            return Result.failure(IllegalArgumentException("行情标识为空"))
        }
        val directDetail = getInstrumentDetail(symbol)
        if (directDetail.isSuccess) {
            return Result.success(symbol)
        }
        return getOverview().mapCatching { overview ->
            overview.rows.firstOrNull {
                it.instrument.symbol.equals(symbol, ignoreCase = true)
            }?.instrument?.instrumentId ?: throw IllegalArgumentException("未找到 $symbol 的实时行情")
        }
    }

    suspend fun getInstrumentDetail(instrumentId: String): Result<MarketInstrumentDetailPayload> =
        request("加载行情详情失败") {
            api.getInstrumentDetail(instrumentId)
        }

    suspend fun getCandles(
        instrumentId: String,
        timeframe: String,
        limit: Int = 24,
    ): Result<MarketCandlesPayload> = request("加载 K 线失败") {
        api.getCandles(
            instrumentId = instrumentId,
            timeframe = timeframe,
            limit = limit,
        )
    }

    private suspend fun <T> request(
        fallbackMessage: String,
        block: suspend () -> Response<MarketEnvelope<T>>,
    ): Result<T> = withContext(Dispatchers.IO) {
        runCatching {
            val response = block()
            if (!response.isSuccessful) {
                throw IllegalStateException("$fallbackMessage (${response.code()})")
            }
            val body = response.body() ?: throw IllegalStateException("$fallbackMessage：响应为空")
            body.data ?: throw IllegalStateException(body.message.ifBlank { fallbackMessage })
        }
    }
}

private interface MarketRemoteApi {
    @GET("${PaymentConfig.API_VERSION}/market/overview")
    suspend fun getOverview(): Response<MarketEnvelope<MarketOverviewPayload>>

    @GET("${PaymentConfig.API_VERSION}/market/spotlights")
    suspend fun getSpotlights(): Response<MarketEnvelope<MarketSpotlightsPayload>>

    @GET("${PaymentConfig.API_VERSION}/market/instruments/{instrumentId}")
    suspend fun getInstrumentDetail(
        @Path(value = "instrumentId", encoded = true) instrumentId: String,
    ): Response<MarketEnvelope<MarketInstrumentDetailPayload>>

    @GET("${PaymentConfig.API_VERSION}/market/instruments/{instrumentId}/candles")
    suspend fun getCandles(
        @Path(value = "instrumentId", encoded = true) instrumentId: String,
        @Query("timeframe") timeframe: String,
        @Query("limit") limit: Int = 24,
    ): Response<MarketEnvelope<MarketCandlesPayload>>
}

internal data class MarketEnvelope<T>(
    val code: String,
    val message: String,
    val data: T?,
)

internal data class MarketOverviewPayload(
    val serverTime: Long,
    val categories: List<MarketCategorySummaryDto>,
    val boards: List<MarketBoardSummaryDto>,
    val rows: List<MarketOverviewRowDto>,
)

internal data class MarketCategorySummaryDto(
    val key: String,
    val label: String,
    val count: Int,
)

internal data class MarketBoardSummaryDto(
    val key: String,
    val label: String,
    val columnLabel: String,
)

internal data class MarketOverviewRowDto(
    val instrument: MarketInstrumentDto,
    val ticker24h: MarketTicker24hDto,
    val rankSignals: MarketRankSignalsDto = MarketRankSignalsDto(),
)

internal data class MarketInstrumentDto(
    val instrumentId: String,
    val symbol: String,
    val displayName: String,
    val marketType: String,
    val quoteCurrency: String,
    val displayPrecision: Int,
    val marketLabel: String,
    val sessionLabel: String? = null,
    val tags: List<MarketTagDto> = emptyList(),
    val categoryKeys: List<String> = emptyList(),
    val favorite: Boolean = false,
)

internal data class MarketTagDto(
    val key: String,
    val label: String,
    val tone: String? = null,
)

internal data class MarketTicker24hDto(
    val lastPrice: String? = null,
    val absChange24h: String? = null,
    val pctChange24h: String? = null,
    val high24h: String? = null,
    val low24h: String? = null,
    val turnover24h: String? = null,
    val baseVolume24h: String? = null,
    val marketCap: String? = null,
    val peRatio: String? = null,
)

internal data class MarketRankSignalsDto(
    val heatRank: Int? = null,
    val changeRank: Int? = null,
    val turnoverRank: Int? = null,
    val listingRank: Int? = null,
)

internal data class MarketSpotlightsPayload(
    val serverTime: Long,
    val items: List<MarketSpotlightDto>,
)

internal data class MarketSpotlightDto(
    val spotlightId: String,
    val instrumentId: String,
    val symbol: String,
    val eyebrow: String,
    val title: String,
    val subtitle: String,
    val primaryMetric: MarketMetricDto,
    val secondaryMetric: MarketMetricDto,
    val target: String? = null,
)

internal data class MarketMetricDto(
    val label: String,
    val value: String? = null,
)

internal data class MarketInstrumentDetailPayload(
    val serverTime: Long,
    val instrument: MarketInstrumentDto,
    val shareUrl: String? = null,
    val ticker24h: MarketTicker24hDto,
    val supportedTimeframes: List<MarketTimeframeOptionDto> = emptyList(),
    val supportedIndicators: List<String> = emptyList(),
    val overviewFacts: List<MarketFactDto> = emptyList(),
    val detailFacts: List<MarketFactDto> = emptyList(),
    val tradeAction: MarketTradeActionDto = MarketTradeActionDto(),
)

internal data class MarketTimeframeOptionDto(
    val key: String,
    val label: String,
)

internal data class MarketFactDto(
    val key: String,
    val label: String,
    val value: JsonElement? = null,
)

internal data class MarketTradeActionDto(
    val enabled: Boolean = false,
    val label: String = "",
    val target: String? = null,
)

internal data class MarketCandlesPayload(
    val serverTime: Long,
    val instrumentId: String,
    val timeframe: String,
    val candles: List<MarketCandleDto> = emptyList(),
    @SerializedName("indicatorSeries")
    val indicatorSeries: Map<String, List<JsonElement>> = emptyMap(),
)

internal data class MarketCandleDto(
    val openTime: Long,
    val closeTime: Long,
    val open: String? = null,
    val high: String? = null,
    val low: String? = null,
    val close: String? = null,
    val volume: String? = null,
    val turnover: String? = null,
    val closed: Boolean = true,
)
