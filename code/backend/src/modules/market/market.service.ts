import {
  BadRequestException,
  Inject,
  Injectable,
  NotFoundException,
  ServiceUnavailableException,
} from '@nestjs/common';
import {
  MARKET_BOARDS,
  MARKET_CATEGORIES,
  MARKET_DATA_PROVIDER,
  MARKET_SUPPORTED_INDICATORS,
  MARKET_SUPPORTED_TIMEFRAMES,
} from './market.types';
import type {
  MarketBoardKey,
  MarketCandlesResponse,
  MarketCategoryKey,
  MarketDataProvider,
  MarketFavoritesResponse,
  MarketFactItem,
  MarketInstrumentDetailResponse,
  MarketInstrumentRef,
  MarketOverviewResponse,
  MarketOverviewRow,
  MarketRankingsResponse,
  MarketRankSignals,
  MarketSearchResponse,
  MarketSpotlightItem,
  MarketSpotlightsResponse,
  MarketTag,
  MarketTicker24h,
  MarketTimeframeKey,
  ProviderCoinDetail,
  ProviderMarketCoin,
  ProviderOhlcPoint,
  ProviderSearchCoin,
} from './market.types';

const MEME_KEYWORDS = ['bonk', 'doge', 'floki', 'meme', 'pepe', 'pengu', 'shib'];
const PUBLIC_CHAIN_IDS = new Set([
  'aptos',
  'arbitrum',
  'avalanche-2',
  'bitcoin',
  'cardano',
  'ethereum',
  'hyperliquid',
  'polkadot',
  'solana',
  'sui',
  'the-open-network',
  'toncoin',
  'tron',
]);

type RankMaps = {
  changeRanks: Map<string, number>;
  turnoverRanks: Map<string, number>;
  listingRanks: Map<string, number>;
};

@Injectable()
export class MarketService {
  constructor(
    @Inject(MARKET_DATA_PROVIDER)
    private readonly provider: MarketDataProvider,
  ) {}

  async getOverview(): Promise<MarketOverviewResponse> {
    const [topMarkets, trendingCoins] = await Promise.all([
      this.provider.getTopMarkets(18),
      this.provider.getTrendingCoins(8),
    ]);

    const topMarketIds = new Set(topMarkets.map((coin) => coin.id));
    const extraIds = trendingCoins
      .map((coin) => coin.id)
      .filter((id) => !topMarketIds.has(id));
    const extraMarkets = await this.provider.getMarketsByIds(extraIds);
    const marketMap = new Map<string, ProviderMarketCoin>();

    for (const coin of [...topMarkets, ...extraMarkets]) {
      if (coin.id) {
        marketMap.set(coin.id, coin);
      }
    }

    const orderedCoins = this.uniqueCoinIds([
      ...trendingCoins.map((coin) => coin.id),
      ...topMarkets.map((coin) => coin.id),
    ])
      .map((id) => marketMap.get(id))
      .filter((coin): coin is ProviderMarketCoin => Boolean(coin))
      .slice(0, 24);

    const trendingRanks = new Map<string, number>();
    trendingCoins.forEach((coin, index) => trendingRanks.set(coin.id, index + 1));

    const rankMaps = this.buildRankMaps(orderedCoins, trendingRanks);
    const rows = orderedCoins.map((coin) =>
      this.toOverviewRow(coin, trendingRanks, rankMaps),
    );

    return {
      serverTime: this.getServerTime(orderedCoins.map((coin) => coin.lastUpdatedAt)),
      categories: this.buildCategorySummary(rows),
      boards: MARKET_BOARDS,
      rows,
    };
  }

  async getSearch(query: string): Promise<MarketSearchResponse> {
    const normalizedQuery = query.trim();
    if (!normalizedQuery) {
      return {
        serverTime: Date.now(),
        query: '',
        total: 0,
        rows: [],
      };
    }

    const searchResults = await this.provider.searchCoins(normalizedQuery, 10);
    const marketCoins = await this.provider.getMarketsByIds(
      searchResults.map((result) => result.id),
    );
    const marketMap = new Map(marketCoins.map((coin) => [coin.id, coin]));
    const orderedCoins = searchResults
      .map((result) => marketMap.get(result.id))
      .filter((coin): coin is ProviderMarketCoin => Boolean(coin));

    const rankMaps = this.buildRankMaps(orderedCoins, new Map());
    const rows = orderedCoins.map((coin) =>
      this.toOverviewRow(coin, new Map(), rankMaps),
    );

    return {
      serverTime: this.getServerTime(orderedCoins.map((coin) => coin.lastUpdatedAt)),
      query: normalizedQuery,
      total: rows.length,
      rows,
    };
  }

  async getSpotlights(): Promise<MarketSpotlightsResponse> {
    const trendingCoins = await this.provider.getTrendingCoins(5);
    const marketCoins = await this.provider.getMarketsByIds(
      trendingCoins.map((coin) => coin.id),
    );
    const marketMap = new Map(marketCoins.map((coin) => [coin.id, coin]));

    const items: MarketSpotlightItem[] = trendingCoins
      .map((trendingCoin, index) => {
        const marketCoin = marketMap.get(trendingCoin.id);
        if (!marketCoin) {
          return null;
        }

        return {
          spotlightId: `trending:${marketCoin.id}`,
          instrumentId: this.toInstrumentId(marketCoin.id),
          symbol: marketCoin.symbol.toUpperCase(),
          eyebrow: `热度榜 #${index + 1}`,
          title: marketCoin.name,
          subtitle: `${marketCoin.symbol.toUpperCase()} · CRYPTO`,
          primaryMetric: {
            label: '24H涨跌',
            value: this.toDecimalString(marketCoin.priceChangePct24h),
          },
          secondaryMetric: {
            label: '24H成交额',
            value: this.toDecimalString(marketCoin.totalVolume),
          },
          target: `/market/${this.toInstrumentId(marketCoin.id)}`,
        };
      })
      .filter((item): item is MarketSpotlightItem => Boolean(item));

    return {
      serverTime: this.getServerTime(
        marketCoins.map((coin) => coin.lastUpdatedAt).filter(Boolean),
      ),
      items,
    };
  }

  getFavorites(): MarketFavoritesResponse {
    return {
      serverTime: Date.now(),
      revision: 'anonymous:0',
      items: [],
    };
  }

  async getRankings(): Promise<MarketRankingsResponse> {
    const overview = await this.getOverview();
    return {
      serverTime: overview.serverTime,
      items: overview.rows.map((row) => ({
        instrumentId: row.instrument.instrumentId,
        rankSignals: row.rankSignals,
      })),
    };
  }

  async getInstrumentDetail(
    identifier: string,
    forceRefresh = false,
  ): Promise<MarketInstrumentDetailResponse> {
    const coinId = await this.resolveCoinId(identifier);
    const coin = await this.getDetailableCoin(coinId, forceRefresh);
    const categoryKeys = this.deriveCategoryKeysFromDetail(coin);
    const tags = this.buildTags(categoryKeys, coin.categories);
    const instrument = this.toInstrumentRef(
      coin.id,
      coin.symbol,
      coin.name,
      coin.currentPrice,
      categoryKeys,
      tags,
    );
    const shareUrl =
      coin.homepageUrl ?? `https://www.coingecko.com/en/coins/${coin.id}`;

    return {
      serverTime: coin.lastUpdatedAt,
      instrument,
      shareUrl,
      ticker24h: this.toTicker24h(coin),
      supportedTimeframes: MARKET_SUPPORTED_TIMEFRAMES,
      supportedIndicators: MARKET_SUPPORTED_INDICATORS,
      overviewFacts: this.buildOverviewFacts(coin, tags),
      detailFacts: this.buildDetailFacts(coin, categoryKeys, tags),
      tradeAction: {
        enabled: Boolean(shareUrl),
        label: '查看市场',
        target: shareUrl,
      },
    };
  }

  async getOnchainTokenQuote(
    chainId: string,
    address: string,
    forceRefresh = false,
  ) {
    const quote = await this.provider.getOnchainTokenQuote(
      chainId,
      address,
      forceRefresh,
    );
    if (!quote) {
      return null;
    }
    return {
      currentPrice: this.toDecimalString(quote.currentPrice),
      priceChangePct24h: this.toDecimalString(quote.priceChangePct24h),
      updatedAt: quote.lastUpdatedAt,
    };
  }

  private async getDetailableCoin(
    coinId: string,
    forceRefresh = false,
  ): Promise<ProviderCoinDetail> {
    try {
      return await this.provider.getCoinDetail(coinId, forceRefresh);
    } catch (error) {
      if (!(error instanceof ServiceUnavailableException)) {
        throw error;
      }

      const fallbackCoin = (await this.provider.getMarketsByIds([coinId]))[0];
      if (!fallbackCoin) {
        throw error;
      }
      return this.toFallbackCoinDetail(fallbackCoin);
    }
  }

  async getCandles(
    identifier: string,
    timeframe: string | undefined,
    limit: string | undefined,
  ): Promise<MarketCandlesResponse> {
    const normalizedTimeframe = this.parseTimeframe(timeframe);
    const normalizedLimit = this.parseLimit(limit);
    const coinId = await this.resolveCoinId(identifier);
    const source = this.getCandleSource(normalizedTimeframe);
    const ohlc = await this.provider.getCoinOhlc(coinId, source.days);
    const candles = this.aggregateOhlc(
      ohlc,
      source.groupSize,
      normalizedLimit,
      source.baseIntervalMs,
    );
    const indicatorSeries = Object.fromEntries(
      MARKET_SUPPORTED_INDICATORS.map((indicator) => [indicator, []]),
    );

    return {
      serverTime: Date.now(),
      instrumentId: this.toInstrumentId(coinId),
      timeframe: normalizedTimeframe,
      candles,
      indicatorSeries,
    };
  }

  private async resolveCoinId(identifier: string): Promise<string> {
    const normalizedIdentifier = identifier.trim();
    if (!normalizedIdentifier) {
      throw new NotFoundException({
        code: 'MARKET_INSTRUMENT_NOT_FOUND',
        message: 'Market instrument not found',
      });
    }

    if (normalizedIdentifier.startsWith('crypto:')) {
      return normalizedIdentifier.slice('crypto:'.length);
    }

    if (
      /^[a-z0-9-]+$/.test(normalizedIdentifier) &&
      normalizedIdentifier === normalizedIdentifier.toLowerCase()
    ) {
      return normalizedIdentifier;
    }

    const results = await this.provider.searchCoins(normalizedIdentifier, 10);
    const normalizedLower = normalizedIdentifier.toLowerCase();
    const exactSymbol = results.find(
      (coin) => coin.symbol.toLowerCase() === normalizedLower,
    );
    const exactId = results.find((coin) => coin.id.toLowerCase() === normalizedLower);
    const exactName = results.find(
      (coin) => coin.name.toLowerCase() === normalizedLower,
    );
    const resolved = exactId ?? exactSymbol ?? exactName ?? results[0];

    if (!resolved) {
      throw new NotFoundException({
        code: 'MARKET_INSTRUMENT_NOT_FOUND',
        message: 'Market instrument not found',
      });
    }

    return resolved.id;
  }

  private buildRankMaps(
    coins: ProviderMarketCoin[],
    trendingRanks: Map<string, number>,
  ): RankMaps {
    return {
      changeRanks: this.rankCoins(coins, (coin) => coin.priceChangePct24h),
      turnoverRanks: this.rankCoins(coins, (coin) => coin.totalVolume),
      listingRanks: this.rankCoins(
        coins.filter((coin) => this.isNewCoin(coin, trendingRanks.has(coin.id))),
        (coin) => {
          if (coin.marketCapRank === null) {
            return null;
          }
          return -coin.marketCapRank;
        },
      ),
    };
  }

  private rankCoins(
    coins: ProviderMarketCoin[],
    extract: (coin: ProviderMarketCoin) => number | null,
  ): Map<string, number> {
    const rankedCoins = coins
      .map((coin) => ({
        id: coin.id,
        value: extract(coin),
      }))
      .filter(
        (coin): coin is { id: string; value: number } => coin.value !== null,
      )
      .sort((left, right) => right.value - left.value);

    const ranks = new Map<string, number>();
    rankedCoins.forEach((coin, index) => ranks.set(coin.id, index + 1));
    return ranks;
  }

  private toOverviewRow(
    coin: ProviderMarketCoin,
    trendingRanks: Map<string, number>,
    rankMaps: RankMaps,
  ): MarketOverviewRow {
    const isHot = trendingRanks.has(coin.id) || (coin.marketCapRank ?? 9999) <= 10;
    const categoryKeys = this.deriveCategoryKeysFromMarketCoin(coin, isHot);
    const tags = this.buildTags(categoryKeys);

    return {
      instrument: this.toInstrumentRef(
        coin.id,
        coin.symbol,
        coin.name,
        coin.currentPrice,
        categoryKeys,
        tags,
      ),
      ticker24h: this.toTicker24h(coin),
      rankSignals: this.buildRankSignals(coin, trendingRanks, rankMaps),
    };
  }

  private buildRankSignals(
    coin: ProviderMarketCoin,
    trendingRanks: Map<string, number>,
    rankMaps: RankMaps,
  ): MarketRankSignals {
    return {
      heatRank: trendingRanks.get(coin.id) ?? coin.marketCapRank ?? null,
      changeRank: rankMaps.changeRanks.get(coin.id) ?? null,
      turnoverRank: rankMaps.turnoverRanks.get(coin.id) ?? null,
      listingRank: rankMaps.listingRanks.get(coin.id) ?? null,
    };
  }

  private buildCategorySummary(rows: MarketOverviewRow[]) {
    const counts = new Map<MarketCategoryKey, number>();
    MARKET_CATEGORIES.forEach((category) => counts.set(category.key, 0));

    rows.forEach((row) => {
      row.instrument.categoryKeys.forEach((key) => {
        const categoryKey = key as MarketCategoryKey;
        counts.set(categoryKey, (counts.get(categoryKey) ?? 0) + 1);
      });
    });

    return MARKET_CATEGORIES.map((category) => ({
      ...category,
      count: counts.get(category.key) ?? 0,
    }));
  }

  private toInstrumentRef(
    coinId: string,
    symbol: string,
    name: string,
    currentPrice: number | null,
    categoryKeys: MarketCategoryKey[],
    tags: MarketTag[],
  ): MarketInstrumentRef {
    return {
      instrumentId: this.toInstrumentId(coinId),
      symbol: symbol.toUpperCase(),
      displayName: name,
      marketType: 'CRYPTO',
      quoteCurrency: 'USD',
      displayPrecision: this.getDisplayPrecision(currentPrice),
      marketLabel: 'CRYPTO',
      sessionLabel: '24x7',
      tags,
      categoryKeys,
      favorite: false,
    };
  }

  private toTicker24h(
    coin: Pick<
      ProviderMarketCoin | ProviderCoinDetail,
      | 'currentPrice'
      | 'priceChange24h'
      | 'priceChangePct24h'
      | 'high24h'
      | 'low24h'
      | 'totalVolume'
      | 'marketCap'
    >,
  ): MarketTicker24h {
    return {
      lastPrice: this.toDecimalString(coin.currentPrice),
      absChange24h: this.toDecimalString(coin.priceChange24h),
      pctChange24h: this.toDecimalString(coin.priceChangePct24h),
      high24h: this.toDecimalString(coin.high24h),
      low24h: this.toDecimalString(coin.low24h),
      turnover24h: this.toDecimalString(coin.totalVolume),
      baseVolume24h: null,
      marketCap: this.toDecimalString(coin.marketCap),
      peRatio: null,
    };
  }

  private toFallbackCoinDetail(coin: ProviderMarketCoin): ProviderCoinDetail {
    return {
      id: coin.id,
      symbol: coin.symbol,
      name: coin.name,
      description: null,
      categories: [],
      homepageUrl: null,
      marketCapRank: coin.marketCapRank,
      currentPrice: coin.currentPrice,
      priceChange24h: coin.priceChange24h,
      priceChangePct24h: coin.priceChangePct24h,
      high24h: coin.high24h,
      low24h: coin.low24h,
      totalVolume: coin.totalVolume,
      marketCap: coin.marketCap,
      lastUpdatedAt: coin.lastUpdatedAt,
    };
  }

  private buildOverviewFacts(
    coin: ProviderCoinDetail,
    tags: MarketTag[],
  ): MarketFactItem[] {
    return [
      {
        key: 'range24h',
        label: '24H 区间',
        value: {
          low: this.toDecimalString(coin.low24h),
          high: this.toDecimalString(coin.high24h),
        },
      },
      {
        key: 'turnover24h',
        label: '24H 成交额',
        value: this.toDecimalString(coin.totalVolume),
      },
      {
        key: 'marketCap',
        label: '总市值',
        value: this.toDecimalString(coin.marketCap),
      },
      {
        key: 'absChange24h',
        label: '涨跌额',
        value: this.toDecimalString(coin.priceChange24h),
      },
      {
        key: 'conceptTags',
        label: '概念标签',
        value: tags.map((tag) => tag.label),
      },
    ];
  }

  private buildDetailFacts(
    coin: ProviderCoinDetail,
    categoryKeys: MarketCategoryKey[],
    tags: MarketTag[],
  ): MarketFactItem[] {
    return [
      {
        key: 'market',
        label: '市场',
        value: 'CRYPTO',
      },
      {
        key: 'name',
        label: '标的名称',
        value: coin.name,
      },
      {
        key: 'boards',
        label: '所属榜单',
        value: categoryKeys.map((key) => this.categoryLabel(key)),
      },
      {
        key: 'pctChange24h',
        label: '24H 涨跌',
        value: this.toDecimalString(coin.priceChangePct24h),
      },
      {
        key: 'range24h',
        label: '24H 区间',
        value: {
          low: this.toDecimalString(coin.low24h),
          high: this.toDecimalString(coin.high24h),
        },
      },
      {
        key: 'tags',
        label: '标签',
        value: tags.map((tag) => tag.label),
      },
    ];
  }

  private deriveCategoryKeysFromMarketCoin(
    coin: ProviderMarketCoin,
    isHot: boolean,
  ): MarketCategoryKey[] {
    const categoryKeys = new Set<MarketCategoryKey>();

    if (isHot) {
      categoryKeys.add('hot');
    }
    if (this.isNewCoin(coin, isHot)) {
      categoryKeys.add('new_coin');
    }
    if (this.isPublicChain(coin.id, coin.symbol, [])) {
      categoryKeys.add('public_chain');
    }
    if (this.isMemeCoin(coin.id, coin.symbol, coin.name, [])) {
      categoryKeys.add('meme');
    }

    return Array.from(categoryKeys);
  }

  private deriveCategoryKeysFromDetail(coin: ProviderCoinDetail): MarketCategoryKey[] {
    const categoryKeys = new Set<MarketCategoryKey>();
    const isHot = (coin.marketCapRank ?? 9999) <= 10;

    if (isHot) {
      categoryKeys.add('hot');
    }
    if (this.isNewCoin(coin, isHot)) {
      categoryKeys.add('new_coin');
    }
    if (this.isPublicChain(coin.id, coin.symbol, coin.categories)) {
      categoryKeys.add('public_chain');
    }
    if (this.isMemeCoin(coin.id, coin.symbol, coin.name, coin.categories)) {
      categoryKeys.add('meme');
    }

    return Array.from(categoryKeys);
  }

  private isNewCoin(
    coin: Pick<ProviderMarketCoin | ProviderCoinDetail, 'marketCapRank'>,
    isHot: boolean,
  ): boolean {
    const rank = coin.marketCapRank ?? Number.MAX_SAFE_INTEGER;
    return isHot && rank > 25 && rank <= 250;
  }

  private isPublicChain(
    coinId: string,
    symbol: string,
    categories: string[],
  ): boolean {
    if (PUBLIC_CHAIN_IDS.has(coinId)) {
      return true;
    }

    const normalizedCategories = categories.map((category) => category.toLowerCase());
    return (
      normalizedCategories.some((category) => category.includes('layer 1')) ||
      normalizedCategories.some((category) => category.includes('ecosystem')) ||
      normalizedCategories.some((category) => category.includes('smart contract')) ||
      ['ada', 'apt', 'arb', 'avax', 'btc', 'eth', 'sol', 'sui', 'ton', 'trx'].includes(
        symbol.toLowerCase(),
      )
    );
  }

  private isMemeCoin(
    coinId: string,
    symbol: string,
    name: string,
    categories: string[],
  ): boolean {
    const haystacks = [
      coinId.toLowerCase(),
      symbol.toLowerCase(),
      name.toLowerCase(),
      ...categories.map((category) => category.toLowerCase()),
    ];
    return MEME_KEYWORDS.some((keyword) =>
      haystacks.some((haystack) => haystack.includes(keyword)),
    );
  }

  private buildTags(
    categoryKeys: MarketCategoryKey[],
    extraLabels: string[] = [],
  ): MarketTag[] {
    const tags: MarketTag[] = [];

    if (categoryKeys.includes('hot')) {
      tags.push({ key: 'hot', label: '热门', tone: 'accent' });
    }
    if (categoryKeys.includes('new_coin')) {
      tags.push({ key: 'new_coin', label: '新币', tone: 'positive' });
    }
    if (categoryKeys.includes('public_chain')) {
      tags.push({ key: 'public_chain', label: '公链', tone: 'positive' });
    }
    if (categoryKeys.includes('meme')) {
      tags.push({ key: 'meme', label: 'Meme', tone: 'accent' });
    }

    extraLabels
      .map((label) => label.trim())
      .filter(Boolean)
      .slice(0, 2)
      .forEach((label) => {
        const key = this.slugify(label);
        if (!tags.some((tag) => tag.key === key || tag.label === label)) {
          tags.push({
            key,
            label,
            tone: 'neutral',
          });
        }
      });

    return tags.slice(0, 3);
  }

  private parseTimeframe(value: string | undefined): MarketTimeframeKey {
    if (!value) {
      return '4h';
    }

    if (
      value === '1h' ||
      value === '4h' ||
      value === '12h' ||
      value === '1d'
    ) {
      return value;
    }

    throw new BadRequestException({
      code: 'MARKET_INVALID_TIMEFRAME',
      message: 'Unsupported timeframe',
    });
  }

  private parseLimit(value: string | undefined): number {
    if (!value) {
      return 48;
    }

    const parsed = Number.parseInt(value, 10);
    if (!Number.isFinite(parsed) || parsed < 1 || parsed > 200) {
      throw new BadRequestException({
        code: 'MARKET_INVALID_LIMIT',
        message: 'Limit must be between 1 and 200',
      });
    }

    return parsed;
  }

  private getCandleSource(timeframe: MarketTimeframeKey) {
    switch (timeframe) {
      case '1h':
        return { days: 1, groupSize: 2, baseIntervalMs: 30 * 60 * 1000 };
      case '4h':
        return { days: 30, groupSize: 1, baseIntervalMs: 4 * 60 * 60 * 1000 };
      case '12h':
        return { days: 30, groupSize: 3, baseIntervalMs: 4 * 60 * 60 * 1000 };
      case '1d':
        return { days: 30, groupSize: 6, baseIntervalMs: 4 * 60 * 60 * 1000 };
    }
  }

  private aggregateOhlc(
    points: ProviderOhlcPoint[],
    groupSize: number,
    limit: number,
    baseIntervalMs: number,
  ) {
    const sortedPoints = [...points].sort((left, right) => left.timestamp - right.timestamp);
    const aggregated = [];

    for (let index = 0; index < sortedPoints.length; index += groupSize) {
      const group = sortedPoints.slice(index, index + groupSize);
      if (group.length === 0) {
        continue;
      }

      const lastPoint = group[group.length - 1];
      const closeTime = lastPoint.timestamp + baseIntervalMs - 1;

      aggregated.push({
        openTime: group[0].timestamp,
        closeTime,
        open: this.toDecimalString(group[0].open),
        high: this.toDecimalString(
          Math.max(...group.map((point) => point.high)),
        ),
        low: this.toDecimalString(Math.min(...group.map((point) => point.low))),
        close: this.toDecimalString(lastPoint.close),
        volume: null,
        turnover: null,
        closed: Date.now() > closeTime,
      });
    }

    return aggregated.slice(-limit);
  }

  private categoryLabel(key: MarketCategoryKey): string {
    return MARKET_CATEGORIES.find((category) => category.key === key)?.label ?? key;
  }

  private toInstrumentId(coinId: string): string {
    return `crypto:${coinId}`;
  }

  private getDisplayPrecision(currentPrice: number | null): number {
    if (currentPrice === null) {
      return 2;
    }
    if (currentPrice >= 1000) {
      return 2;
    }
    if (currentPrice >= 1) {
      return 2;
    }
    if (currentPrice >= 0.01) {
      return 4;
    }
    if (currentPrice >= 0.0001) {
      return 6;
    }
    return 8;
  }

  private toDecimalString(value: number | null): string | null {
    if (value === null || !Number.isFinite(value)) {
      return null;
    }

    return value.toLocaleString('en-US', {
      useGrouping: false,
      maximumFractionDigits: 20,
    });
  }

  private slugify(value: string): string {
    return value
      .toLowerCase()
      .replace(/[^a-z0-9]+/g, '-')
      .replace(/^-+|-+$/g, '');
  }

  private getServerTime(timestamps: number[]): number {
    const validTimestamps = timestamps.filter(Number.isFinite);
    if (validTimestamps.length === 0) {
      return Date.now();
    }

    return Math.max(...validTimestamps);
  }

  private uniqueCoinIds(ids: string[]): string[] {
    return Array.from(new Set(ids.filter(Boolean)));
  }
}
