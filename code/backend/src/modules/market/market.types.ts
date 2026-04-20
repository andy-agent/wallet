export const MARKET_DATA_PROVIDER = Symbol('MARKET_DATA_PROVIDER');

export type DecimalString = string;
export type TimestampMs = number;

export type MarketTagTone = 'accent' | 'positive' | 'negative' | 'neutral';
export type MarketCategoryKey =
  | 'favorites'
  | 'hot'
  | 'new_coin'
  | 'public_chain'
  | 'meme';
export type MarketBoardKey = 'hot' | 'gainers' | 'volume' | 'new_listing';
export type MarketTimeframeKey = '1h' | '4h' | '12h' | '1d';

export interface MarketTag {
  key: string;
  label: string;
  tone?: MarketTagTone;
}

export interface MarketInstrumentRef {
  instrumentId: string;
  symbol: string;
  displayName: string;
  marketType: 'CRYPTO';
  quoteCurrency: 'USD';
  displayPrecision: number;
  marketLabel: string;
  sessionLabel: string | null;
  tags: MarketTag[];
  categoryKeys: string[];
  favorite: boolean;
}

export interface MarketTicker24h {
  lastPrice: DecimalString | null;
  absChange24h: DecimalString | null;
  pctChange24h: DecimalString | null;
  high24h: DecimalString | null;
  low24h: DecimalString | null;
  turnover24h: DecimalString | null;
  baseVolume24h?: DecimalString | null;
  marketCap?: DecimalString | null;
  peRatio?: DecimalString | null;
}

export interface MarketRankSignals {
  heatRank?: number | null;
  changeRank?: number | null;
  turnoverRank?: number | null;
  listingRank?: number | null;
}

export interface MarketOverviewRow {
  instrument: MarketInstrumentRef;
  ticker24h: MarketTicker24h;
  rankSignals: MarketRankSignals;
}

export interface MarketCategorySummary {
  key: MarketCategoryKey;
  label: string;
  count: number;
}

export interface MarketBoardSummary {
  key: MarketBoardKey;
  label: string;
  columnLabel: string;
}

export interface MarketOverviewResponse {
  serverTime: TimestampMs;
  categories: MarketCategorySummary[];
  boards: MarketBoardSummary[];
  rows: MarketOverviewRow[];
}

export interface MarketSearchResponse {
  serverTime: TimestampMs;
  query: string;
  total: number;
  rows: MarketOverviewRow[];
}

export interface MarketMetricValue {
  label: string;
  value: DecimalString | null;
}

export interface MarketSpotlightItem {
  spotlightId: string;
  instrumentId: string;
  symbol: string;
  eyebrow: string;
  title: string;
  subtitle: string;
  primaryMetric: MarketMetricValue;
  secondaryMetric: MarketMetricValue;
  target: string;
}

export interface MarketSpotlightsResponse {
  serverTime: TimestampMs;
  items: MarketSpotlightItem[];
}

export interface MarketFavoritesResponse {
  serverTime: TimestampMs;
  revision: string;
  items: string[];
}

export interface MarketRankingsResponse {
  serverTime: TimestampMs;
  items: Array<{
    instrumentId: string;
    rankSignals: MarketRankSignals;
  }>;
}

export interface MarketTimeframeOption {
  key: MarketTimeframeKey;
  label: string;
}

export type MarketFactValue =
  | string
  | string[]
  | null
  | {
      low: string | null;
      high: string | null;
    };

export interface MarketFactItem {
  key: string;
  label: string;
  value: MarketFactValue;
}

export interface MarketTradeAction {
  enabled: boolean;
  label: string;
  target: string | null;
}

export interface MarketInstrumentDetailResponse {
  serverTime: TimestampMs;
  instrument: MarketInstrumentRef;
  shareUrl: string | null;
  ticker24h: MarketTicker24h;
  supportedTimeframes: MarketTimeframeOption[];
  supportedIndicators: string[];
  overviewFacts: MarketFactItem[];
  detailFacts: MarketFactItem[];
  tradeAction: MarketTradeAction;
}

export interface MarketCandle {
  openTime: TimestampMs;
  closeTime: TimestampMs;
  open: DecimalString | null;
  high: DecimalString | null;
  low: DecimalString | null;
  close: DecimalString | null;
  volume?: DecimalString | null;
  turnover?: DecimalString | null;
  closed: boolean;
}

export interface MarketCandlesResponse {
  serverTime: TimestampMs;
  instrumentId: string;
  timeframe: MarketTimeframeKey;
  candles: MarketCandle[];
  indicatorSeries: Record<string, unknown[]>;
}

export interface ProviderMarketCoin {
  id: string;
  symbol: string;
  name: string;
  currentPrice: number | null;
  priceChange24h: number | null;
  priceChangePct24h: number | null;
  high24h: number | null;
  low24h: number | null;
  totalVolume: number | null;
  marketCap: number | null;
  marketCapRank: number | null;
  lastUpdatedAt: TimestampMs;
}

export interface ProviderSearchCoin {
  id: string;
  symbol: string;
  name: string;
  marketCapRank: number | null;
}

export interface ProviderTrendingCoin extends ProviderSearchCoin {
  score: number;
  currentPrice: number | null;
  priceChangePct24h: number | null;
  totalVolume: number | null;
  marketCap: number | null;
}

export interface ProviderCoinDetail {
  id: string;
  symbol: string;
  name: string;
  description: string | null;
  categories: string[];
  homepageUrl: string | null;
  marketCapRank: number | null;
  currentPrice: number | null;
  priceChange24h: number | null;
  priceChangePct24h: number | null;
  high24h: number | null;
  low24h: number | null;
  totalVolume: number | null;
  marketCap: number | null;
  lastUpdatedAt: TimestampMs;
}

export interface ProviderTokenQuote {
  currentPrice: number | null;
  priceChangePct24h: number | null;
  lastUpdatedAt: TimestampMs;
}

export interface ProviderOhlcPoint {
  timestamp: TimestampMs;
  open: number;
  high: number;
  low: number;
  close: number;
}

export interface MarketDataProvider {
  getTopMarkets(limit: number): Promise<ProviderMarketCoin[]>;
  getMarketsByIds(ids: string[]): Promise<ProviderMarketCoin[]>;
  searchCoins(query: string, limit: number): Promise<ProviderSearchCoin[]>;
  getTrendingCoins(limit: number): Promise<ProviderTrendingCoin[]>;
  getCoinDetail(coinId: string, forceRefresh?: boolean): Promise<ProviderCoinDetail>;
  getCoinOhlc(coinId: string, days: number): Promise<ProviderOhlcPoint[]>;
  getOnchainTokenQuote(
    chainId: string,
    address: string,
    forceRefresh?: boolean,
  ): Promise<ProviderTokenQuote | null>;
}

export const MARKET_CATEGORIES: MarketCategorySummary[] = [
  { key: 'favorites', label: '自选', count: 0 },
  { key: 'hot', label: '热门', count: 0 },
  { key: 'new_coin', label: '新币', count: 0 },
  { key: 'public_chain', label: '公链', count: 0 },
  { key: 'meme', label: 'Meme', count: 0 },
];

export const MARKET_BOARDS: MarketBoardSummary[] = [
  { key: 'hot', label: '热门', columnLabel: '涨跌/热度' },
  { key: 'gainers', label: '涨幅榜', columnLabel: '24H涨跌' },
  { key: 'volume', label: '成交额', columnLabel: '成交额' },
  { key: 'new_listing', label: '新币榜', columnLabel: '新热度' },
];

export const MARKET_SUPPORTED_TIMEFRAMES: MarketTimeframeOption[] = [
  { key: '1h', label: '1小时' },
  { key: '4h', label: '4小时' },
  { key: '12h', label: '12小时' },
  { key: '1d', label: '1天' },
];

export const MARKET_SUPPORTED_INDICATORS = [
  'MA',
  'BOLL',
  'MACD',
  'KDJ',
  'RSI',
  'WR',
];
