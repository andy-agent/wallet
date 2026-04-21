import {
  Injectable,
  Logger,
  NotFoundException,
  ServiceUnavailableException,
} from '@nestjs/common';
import { MarketConfig } from './market.config';
import type {
  MarketDataProvider,
  ProviderCoinDetail,
  ProviderMarketCoin,
  ProviderOhlcPoint,
  ProviderSearchCoin,
  ProviderTokenQuote,
  ProviderTrendingCoin,
} from './market.types';

type CacheEntry<T> = {
  expiresAt: number;
  value: T;
};

type CoinGeckoMarketResponse = {
  id?: string;
  symbol?: string;
  name?: string;
  current_price?: number | null;
  price_change_24h?: number | null;
  price_change_percentage_24h?: number | null;
  price_change_percentage_24h_in_currency?: number | null;
  high_24h?: number | null;
  low_24h?: number | null;
  total_volume?: number | null;
  market_cap?: number | null;
  market_cap_rank?: number | null;
  last_updated?: string;
};

type CoinGeckoSearchResponse = {
  coins?: Array<{
    id?: string;
    symbol?: string;
    name?: string;
    market_cap_rank?: number | null;
  }>;
};

type CoinGeckoTrendingResponse = {
  coins?: Array<{
    item?: {
      id?: string;
      symbol?: string;
      name?: string;
      market_cap_rank?: number | null;
      score?: number | null;
      data?: {
        price?: number | null;
        total_volume?: string | number | null;
        market_cap?: string | number | null;
        price_change_percentage_24h?: {
          usd?: number | null;
        };
      };
    };
  }>;
};

type CoinGeckoDetailResponse = {
  id?: string;
  symbol?: string;
  name?: string;
  description?: {
    en?: string;
  };
  categories?: string[];
  links?: {
    homepage?: string[];
  };
  market_cap_rank?: number | null;
  market_data?: {
    current_price?: {
      usd?: number | null;
    };
    price_change_24h?: number | null;
    price_change_percentage_24h?: number | null;
    high_24h?: {
      usd?: number | null;
    };
    low_24h?: {
      usd?: number | null;
    };
    total_volume?: {
      usd?: number | null;
    };
    market_cap?: {
      usd?: number | null;
    };
  };
  last_updated?: string;
};

type CoinGeckoOhlcResponse = Array<[number, number, number, number, number]>;

type CoinGeckoSimpleTokenPriceResponse = {
  data?: {
    attributes?: {
      token_prices?: Record<string, string>;
      h24_price_change_percentage?: Record<string, string>;
    };
  };
};

type DexScreenerSearchResponse = {
  pairs?: Array<{
    chainId?: string;
    priceUsd?: string | null;
    priceChange?: {
      h24?: string | number | null;
    };
    liquidity?: {
      usd?: string | number | null;
    };
    baseToken?: {
      address?: string;
    };
    quoteToken?: {
      address?: string;
    };
  }>;
};

@Injectable()
export class CoinGeckoMarketDataProvider implements MarketDataProvider {
  private readonly logger = new Logger(CoinGeckoMarketDataProvider.name);
  private readonly cache = new Map<string, CacheEntry<unknown>>();

  constructor(private readonly config: MarketConfig) {}

  async getTopMarkets(limit: number): Promise<ProviderMarketCoin[]> {
    return this.getCached(
      `markets:top:${limit}`,
      async () => {
        const response = await this.get<CoinGeckoMarketResponse[]>(
          '/coins/markets',
          {
            vs_currency: 'usd',
            order: 'market_cap_desc',
            per_page: Math.max(1, Math.min(limit, 50)),
            page: 1,
            sparkline: false,
            price_change_percentage: '24h',
          },
        );
        return response.map((item) => this.mapMarketCoin(item));
      },
      this.config.getCacheTtlMs(),
    );
  }

  async getMarketsByIds(ids: string[]): Promise<ProviderMarketCoin[]> {
    const normalizedIds = Array.from(
      new Set(ids.map((id) => id.trim()).filter(Boolean)),
    );
    if (normalizedIds.length === 0) {
      return [];
    }

    return this.getCached(
      `markets:ids:${normalizedIds.sort().join(',')}`,
      async () => {
        const response = await this.get<CoinGeckoMarketResponse[]>(
          '/coins/markets',
          {
            vs_currency: 'usd',
            ids: normalizedIds.join(','),
            sparkline: false,
            price_change_percentage: '24h',
          },
        );
        return response.map((item) => this.mapMarketCoin(item));
      },
      this.config.getCacheTtlMs(),
    );
  }

  async searchCoins(query: string, limit: number): Promise<ProviderSearchCoin[]> {
    const normalizedQuery = query.trim();
    if (!normalizedQuery) {
      return [];
    }

    return this.getCached(
      `search:${normalizedQuery.toLowerCase()}:${limit}`,
      async () => {
        const response = await this.get<CoinGeckoSearchResponse>('/search', {
          query: normalizedQuery,
        });

        return (response.coins ?? [])
          .map((item) => ({
            id: item.id ?? '',
            symbol: item.symbol ?? '',
            name: item.name ?? '',
            marketCapRank: this.toNumberOrNull(item.market_cap_rank),
          }))
          .filter((item) => item.id && item.symbol && item.name)
          .slice(0, Math.max(1, Math.min(limit, 20)));
      },
      Math.min(this.config.getCacheTtlMs(), 30000),
    );
  }

  async getTrendingCoins(limit: number): Promise<ProviderTrendingCoin[]> {
    return this.getCached(
      `trending:${limit}`,
      async () => {
        const response = await this.get<CoinGeckoTrendingResponse>(
          '/search/trending',
        );

        return (response.coins ?? [])
          .map((entry) => this.mapTrendingCoin(entry.item))
          .filter((item): item is ProviderTrendingCoin => item !== null)
          .slice(0, Math.max(1, Math.min(limit, 15)));
      },
      Math.min(this.config.getCacheTtlMs(), 30000),
    );
  }

  async getCoinDetail(
    coinId: string,
    forceRefresh = false,
  ): Promise<ProviderCoinDetail> {
    return this.getCoinDetailInternal(coinId, forceRefresh);
  }

  async getOnchainTokenQuote(
    chainId: string,
    address: string,
    forceRefresh = false,
  ): Promise<ProviderTokenQuote | null> {
    const normalizedChainId = this.normalizeOnchainChainId(chainId);
    const normalizedAddress = address.trim().toLowerCase();
    if (!normalizedChainId || !normalizedAddress) {
      return null;
    }
    return this.getCached(
      `token-quote:${normalizedChainId}:${normalizedAddress}`,
      async () => {
        const response = await this.get<CoinGeckoSimpleTokenPriceResponse>(
          `/onchain/simple/networks/${normalizedChainId}/token_price/${normalizedAddress}`,
          {
            include_24hr_price_change: true,
          },
        );
        const tokenPrice = response.data?.attributes?.token_prices?.[normalizedAddress];
        const priceChange =
          response.data?.attributes?.h24_price_change_percentage?.[normalizedAddress];
        if (tokenPrice === undefined && priceChange === undefined) {
          return this.getDexScreenerTokenQuote(normalizedChainId, normalizedAddress);
        }
        return {
          currentPrice: this.parseLooseNumber(tokenPrice),
          priceChangePct24h: this.parseLooseNumber(priceChange),
          lastUpdatedAt: Date.now(),
        };
      },
      this.config.getCacheTtlMs(),
      forceRefresh,
    );
  }

  async getCoinDetailInternal(
    coinId: string,
    forceRefresh = false,
  ): Promise<ProviderCoinDetail> {
    const normalizedCoinId = coinId.trim();
    if (!normalizedCoinId) {
      throw new NotFoundException({
        code: 'MARKET_INSTRUMENT_NOT_FOUND',
        message: 'Market instrument not found',
      });
    }

    return this.getCached(
      `coin:${normalizedCoinId}`,
      async () => {
        const response = await this.get<CoinGeckoDetailResponse>(
          `/coins/${encodeURIComponent(normalizedCoinId)}`,
          {
            localization: false,
            tickers: false,
            market_data: true,
            community_data: false,
            developer_data: false,
            sparkline: false,
          },
        );

        if (!response.id || !response.symbol || !response.name) {
          throw new NotFoundException({
            code: 'MARKET_INSTRUMENT_NOT_FOUND',
            message: 'Market instrument not found',
          });
        }

        return {
          id: response.id,
          symbol: response.symbol,
          name: response.name,
          description: response.description?.en?.trim() || null,
          categories: response.categories ?? [],
          homepageUrl: response.links?.homepage?.find(Boolean) ?? null,
          marketCapRank: this.toNumberOrNull(response.market_cap_rank),
          currentPrice: this.toNumberOrNull(
            response.market_data?.current_price?.usd,
          ),
          priceChange24h: this.toNumberOrNull(
            response.market_data?.price_change_24h,
          ),
          priceChangePct24h: this.toNumberOrNull(
            response.market_data?.price_change_percentage_24h,
          ),
          high24h: this.toNumberOrNull(response.market_data?.high_24h?.usd),
          low24h: this.toNumberOrNull(response.market_data?.low_24h?.usd),
          totalVolume: this.toNumberOrNull(
            response.market_data?.total_volume?.usd,
          ),
          marketCap: this.toNumberOrNull(response.market_data?.market_cap?.usd),
          lastUpdatedAt: this.toTimestamp(response.last_updated),
        };
      },
      this.config.getCacheTtlMs(),
      forceRefresh,
    );
  }

  async getCoinOhlc(coinId: string, days: number): Promise<ProviderOhlcPoint[]> {
    const normalizedCoinId = coinId.trim();
    return this.getCached(
      `ohlc:${normalizedCoinId}:${days}`,
      async () => {
        const response = await this.get<CoinGeckoOhlcResponse>(
          `/coins/${encodeURIComponent(normalizedCoinId)}/ohlc`,
          {
            vs_currency: 'usd',
            days,
          },
        );

        return response.map((entry) => ({
          timestamp: entry[0],
          open: entry[1],
          high: entry[2],
          low: entry[3],
          close: entry[4],
        }));
      },
      Math.min(this.config.getCacheTtlMs(), 30000),
    );
  }

  private async getCached<T>(
    key: string,
    loader: () => Promise<T>,
    ttlMs: number,
    forceRefresh = false,
  ): Promise<T> {
    const now = Date.now();
    const cached = this.cache.get(key) as CacheEntry<T> | undefined;
    if (!forceRefresh && cached && cached.expiresAt > now) {
      return cached.value;
    }

    const value = await loader();
    this.cache.set(key, {
      expiresAt: now + ttlMs,
      value,
    });
    return value;
  }

  private async get<T>(
    path: string,
    params?: Record<string, string | number | boolean>,
  ): Promise<T> {
    const url = `${this.config.getProviderBaseUrl()}${path}`;

    try {
      const requestUrl = new URL(url);
      Object.entries(params ?? {}).forEach(([key, value]) => {
        requestUrl.searchParams.set(key, String(value));
      });

      const headers: Record<string, string> = {};
      const apiKey = this.config.getProviderApiKey();
      if (apiKey) {
        if (this.config.useDemoApiKey()) {
          headers['x-cg-demo-api-key'] = apiKey;
        } else {
          headers['x-cg-pro-api-key'] = apiKey;
        }
      }

      const response = await fetch(requestUrl, {
        headers,
        signal: AbortSignal.timeout(this.config.getProviderTimeoutMs()),
      });
      if (response.status === 404) {
        throw new NotFoundException({
          code: 'MARKET_INSTRUMENT_NOT_FOUND',
          message: 'Market instrument not found',
        });
      }
      if (!response.ok) {
        throw new Error(`Unexpected provider status: ${response.status}`);
      }
      return (await response.json()) as T;
    } catch (error) {
      this.logger.warn(
        `CoinGecko request failed for ${path}`,
        error instanceof Error ? error.message : error,
      );
      if (error instanceof NotFoundException) {
        throw error;
      }

      throw new ServiceUnavailableException({
        code: 'MARKET_PROVIDER_UNAVAILABLE',
        message: 'Market data provider is unavailable',
      });
    }
  }

  private normalizeOnchainChainId(chainId: string): string | null {
    switch (chainId.trim().toLowerCase()) {
      case 'solana':
        return 'solana';
      case 'tron':
        return 'tron';
      case 'ethereum':
        return 'eth';
      case 'bsc':
        return 'bsc';
      case 'polygon':
        return 'polygon_pos';
      case 'arbitrum':
        return 'arbitrum';
      case 'base':
        return 'base';
      case 'optimism':
        return 'optimism';
      case 'avalanche':
      case 'avalanche_c':
        return 'avax';
      default:
        return null;
    }
  }

  private async getDexScreenerTokenQuote(
    chainId: string,
    address: string,
  ): Promise<ProviderTokenQuote | null> {
    const normalizedChainId = this.normalizeDexScreenerChainId(chainId);
    if (!normalizedChainId) {
      return null;
    }
    try {
      const requestUrl = new URL(
        `${this.config.getDexScreenerBaseUrl().replace(/\/$/, '')}/dex/search`,
      );
      requestUrl.searchParams.set('q', address);
      const response = await fetch(requestUrl, {
        signal: AbortSignal.timeout(this.config.getProviderTimeoutMs()),
      });
      if (!response.ok) {
        return null;
      }
      const payload = (await response.json()) as DexScreenerSearchResponse;
      const match = (payload.pairs ?? [])
        .filter(
          (pair) =>
            this.normalizeDexScreenerChainId(pair.chainId) === normalizedChainId &&
            pair.baseToken?.address?.trim().toLowerCase() === address,
        )
        .sort((left, right) => {
          const leftLiquidity = this.toNumberOrNull(left.liquidity?.usd) ?? 0;
          const rightLiquidity = this.toNumberOrNull(right.liquidity?.usd) ?? 0;
          return rightLiquidity - leftLiquidity;
        })
        .find((pair) => this.parseLooseNumber(pair.priceUsd) != null);
      if (!match) {
        return null;
      }
      return {
        currentPrice: this.parseLooseNumber(match.priceUsd),
        priceChangePct24h: this.parseLooseNumber(match.priceChange?.h24),
        lastUpdatedAt: Date.now(),
      };
    } catch (error) {
      this.logger.warn(
        `DexScreener token quote fallback failed for ${chainId}:${address}`,
        error instanceof Error ? error.message : String(error),
      );
      return null;
    }
  }

  private normalizeDexScreenerChainId(chainId?: string): string | null {
    switch ((chainId ?? '').trim().toLowerCase()) {
      case 'solana':
        return 'solana';
      case 'tron':
        return 'tron';
      case 'ethereum':
        return 'eth';
      case 'eth':
        return 'eth';
      case 'bsc':
        return 'bsc';
      case 'polygon':
      case 'polygon_pos':
        return 'polygon_pos';
      case 'arbitrum':
        return 'arbitrum';
      case 'base':
        return 'base';
      case 'optimism':
        return 'optimism';
      case 'avax':
      case 'avalanche':
      case 'avalanche_c':
        return 'avax';
      default:
        return null;
    }
  }

  private mapMarketCoin(item: CoinGeckoMarketResponse): ProviderMarketCoin {
    return {
      id: item.id ?? '',
      symbol: item.symbol ?? '',
      name: item.name ?? '',
      currentPrice: this.toNumberOrNull(item.current_price),
      priceChange24h: this.toNumberOrNull(item.price_change_24h),
      priceChangePct24h: this.toNumberOrNull(
        item.price_change_percentage_24h_in_currency ??
          item.price_change_percentage_24h,
      ),
      high24h: this.toNumberOrNull(item.high_24h),
      low24h: this.toNumberOrNull(item.low_24h),
      totalVolume: this.toNumberOrNull(item.total_volume),
      marketCap: this.toNumberOrNull(item.market_cap),
      marketCapRank: this.toNumberOrNull(item.market_cap_rank),
      lastUpdatedAt: this.toTimestamp(item.last_updated),
    };
  }

  private mapTrendingCoin(
    item:
      | {
          id?: string;
          symbol?: string;
          name?: string;
          market_cap_rank?: number | null;
          score?: number | null;
          data?: {
            price?: number | null;
            total_volume?: string | number | null;
            market_cap?: string | number | null;
            price_change_percentage_24h?: {
              usd?: number | null;
            };
          };
        }
      | undefined,
  ): ProviderTrendingCoin | null {
    if (!item?.id || !item.symbol || !item.name) {
      return null;
    }

    return {
      id: item.id,
      symbol: item.symbol,
      name: item.name,
      marketCapRank: this.toNumberOrNull(item.market_cap_rank),
      score: this.toNumberOrNull(item.score) ?? 0,
      currentPrice: this.toNumberOrNull(item.data?.price),
      priceChangePct24h: this.toNumberOrNull(
        item.data?.price_change_percentage_24h?.usd,
      ),
      totalVolume: this.parseLooseNumber(item.data?.total_volume),
      marketCap: this.parseLooseNumber(item.data?.market_cap),
    };
  }

  private toNumberOrNull(value: unknown): number | null {
    return typeof value === 'number' && Number.isFinite(value) ? value : null;
  }

  private parseLooseNumber(value: unknown): number | null {
    if (typeof value === 'number' && Number.isFinite(value)) {
      return value;
    }
    if (typeof value !== 'string') {
      return null;
    }

    const normalized = value.replace(/[^0-9.-]/g, '');
    if (!normalized) {
      return null;
    }

    const parsed = Number(normalized);
    return Number.isFinite(parsed) ? parsed : null;
  }

  private toTimestamp(value: string | undefined): number {
    if (!value) {
      return Date.now();
    }

    const timestamp = Date.parse(value);
    return Number.isFinite(timestamp) ? timestamp : Date.now();
  }
}
