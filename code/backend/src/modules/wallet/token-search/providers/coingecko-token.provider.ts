import { Injectable, Logger } from '@nestjs/common';
import { TokenSearchConfig } from '../token-search.config';
import {
  dedupeCustomTokenCandidates,
  type ChainTokenSearchProvider,
  type CustomTokenSearchCandidate,
  type SupportedCustomTokenChainId,
} from '../token-search.types';

type CoinGeckoSearchResponse = {
  coins?: Array<{
    id?: string;
    symbol?: string;
    name?: string;
  }>;
};

type CoinGeckoCoinDetailResponse = {
  id?: string;
  symbol?: string;
  name?: string;
  platforms?: Record<string, string>;
};

type CoinGeckoOnchainTokenInfoResponse = {
  data?: {
    attributes?: {
      address?: string;
      name?: string;
      symbol?: string;
      decimals?: number;
      image_url?: string | null;
      image?: {
        thumb?: string | null;
        small?: string | null;
        large?: string | null;
      };
    };
  };
};

type DexScreenerSearchResponse = {
  pairs?: Array<{
    chainId?: string;
    baseToken?: {
      address?: string;
      name?: string;
      symbol?: string;
    };
    info?: {
      imageUrl?: string;
    };
  }>;
};

@Injectable()
export class CoinGeckoTokenProvider implements ChainTokenSearchProvider {
  readonly providerName = 'coingecko';
  private readonly logger = new Logger(CoinGeckoTokenProvider.name);

  constructor(private readonly config: TokenSearchConfig) {}

  async searchByKeyword(
    chainId: SupportedCustomTokenChainId,
    query: string,
  ): Promise<CustomTokenSearchCandidate[]> {
    if (!this.isSupported(chainId)) {
      return [];
    }
    const apiKey = this.config.getCoinGeckoApiKey();
    if (!apiKey) {
      this.logger.warn('COINGECKO_API_KEY missing, skip CoinGecko keyword search');
      return [];
    }
    const primary = await this.searchCoinGeckoKeyword(chainId, query, apiKey);
    if (primary.length > 0) {
      return primary;
    }
    return this.searchDexScreenerFallback(chainId, query);
  }

  async resolveByAddress(
    chainId: SupportedCustomTokenChainId,
    address: string,
  ): Promise<CustomTokenSearchCandidate | null> {
    if (!this.isSupported(chainId)) {
      return null;
    }
    const apiKey = this.config.getCoinGeckoApiKey();
    if (!apiKey) {
      this.logger.warn('COINGECKO_API_KEY missing, skip CoinGecko address resolve');
      return null;
    }
    return this.resolveCoinGeckoOnchain(chainId, address, apiKey);
  }

  private async searchCoinGeckoKeyword(
    chainId: SupportedCustomTokenChainId,
    query: string,
    apiKey: string,
  ): Promise<CustomTokenSearchCandidate[]> {
    try {
      const url = new URL(`${this.config.getCoinGeckoBaseUrl().replace(/\/$/, '')}/search`);
      url.searchParams.set('query', query.trim());
      const searchResponse = await fetch(url, {
        headers: this.apiHeaders(apiKey),
        signal: AbortSignal.timeout(this.config.getTimeoutMs()),
      });
      if (!searchResponse.ok) {
        throw new Error(`CoinGecko search status ${searchResponse.status}`);
      }
      const searchPayload = (await searchResponse.json()) as CoinGeckoSearchResponse;
      const topCoins = (searchPayload.coins ?? []).slice(0, 5);
      const resolved = await Promise.all(
        topCoins.map(async (coin) => {
          if (!coin.id) {
            return null;
          }
          const detailUrl = new URL(
            `${this.config.getCoinGeckoBaseUrl().replace(/\/$/, '')}/coins/${encodeURIComponent(coin.id)}`,
          );
          detailUrl.searchParams.set('localization', 'false');
          detailUrl.searchParams.set('tickers', 'false');
          detailUrl.searchParams.set('market_data', 'false');
          detailUrl.searchParams.set('community_data', 'false');
          detailUrl.searchParams.set('developer_data', 'false');
          detailUrl.searchParams.set('sparkline', 'false');
          const detailResponse = await fetch(detailUrl, {
            headers: this.apiHeaders(apiKey),
            signal: AbortSignal.timeout(this.config.getTimeoutMs()),
          });
          if (!detailResponse.ok) {
            return null;
          }
          const detailPayload = (await detailResponse.json()) as CoinGeckoCoinDetailResponse;
          const address =
            detailPayload.platforms?.[this.assetPlatformId(chainId)]?.trim() || '';
          if (!address) {
            return null;
          }
          return this.resolveCoinGeckoOnchain(chainId, address, apiKey);
        }),
      );
      return dedupeCustomTokenCandidates(
        resolved.filter((item): item is CustomTokenSearchCandidate => item !== null),
      ).slice(0, 10);
    } catch (error) {
      this.logger.warn(
        `CoinGecko keyword search failed for ${chainId}:${query}`,
        error instanceof Error ? error.message : String(error),
      );
      return [];
    }
  }

  private async resolveCoinGeckoOnchain(
    chainId: SupportedCustomTokenChainId,
    address: string,
    apiKey: string,
  ): Promise<CustomTokenSearchCandidate | null> {
    try {
      const url = new URL(
        `${this.config.getCoinGeckoBaseUrl().replace(/\/$/, '')}/onchain/networks/${this.networkId(chainId)}/tokens/${address.trim()}/info`,
      );
      const response = await fetch(url, {
        headers: this.apiHeaders(apiKey),
        signal: AbortSignal.timeout(this.config.getTimeoutMs()),
      });
      if (!response.ok) {
        throw new Error(`CoinGecko onchain status ${response.status}`);
      }
      const payload = (await response.json()) as CoinGeckoOnchainTokenInfoResponse;
      const attributes = payload.data?.attributes;
      const tokenAddress = attributes?.address?.trim();
      const name = attributes?.name?.trim();
      const symbol = attributes?.symbol?.trim()?.toUpperCase();
      const decimals = attributes?.decimals;
      if (!tokenAddress || !name || !symbol || typeof decimals !== 'number') {
        return null;
      }
      return {
        tokenAddress,
        name,
        symbol,
        decimals,
        iconUrl:
          attributes?.image_url?.trim() ||
          attributes?.image?.small?.trim() ||
          attributes?.image?.thumb?.trim() ||
          attributes?.image?.large?.trim() ||
          null,
        chainId,
      };
    } catch (error) {
      this.logger.warn(
        `CoinGecko resolveByAddress failed for ${chainId}:${address}`,
        error instanceof Error ? error.message : String(error),
      );
      return null;
    }
  }

  private async searchDexScreenerFallback(
    chainId: SupportedCustomTokenChainId,
    query: string,
  ): Promise<CustomTokenSearchCandidate[]> {
    try {
      const url = new URL(`${this.config.getDexScreenerBaseUrl().replace(/\/$/, '')}/dex/search`);
      url.searchParams.set('q', query.trim());
      const response = await fetch(url, {
        signal: AbortSignal.timeout(this.config.getTimeoutMs()),
      });
      if (!response.ok) {
        throw new Error(`DexScreener status ${response.status}`);
      }
      const payload = (await response.json()) as DexScreenerSearchResponse;
      const items = (payload.pairs ?? [])
        .filter((pair) => this.normalizeDexChainId(pair.chainId) === chainId)
        .map((pair) => {
          const address = pair.baseToken?.address?.trim();
          const name = pair.baseToken?.name?.trim();
          const symbol = pair.baseToken?.symbol?.trim()?.toUpperCase();
          if (!address || !name || !symbol) {
            return null;
          }
          return {
            tokenAddress: address,
            name,
            symbol,
            decimals: 18,
            iconUrl: pair.info?.imageUrl?.trim() || null,
            chainId,
          } satisfies CustomTokenSearchCandidate;
        })
        .filter((item): item is CustomTokenSearchCandidate => item !== null);
      return dedupeCustomTokenCandidates(items).slice(0, 10);
    } catch (error) {
      this.logger.warn(
        `DexScreener fallback failed for ${chainId}:${query}`,
        error instanceof Error ? error.message : String(error),
      );
      return [];
    }
  }

  private isSupported(chainId: SupportedCustomTokenChainId) {
    return [
      'ethereum',
      'bsc',
      'polygon',
      'arbitrum',
      'base',
      'optimism',
      'avalanche',
    ].includes(chainId);
  }

  private apiHeaders(apiKey: string) {
    const headers: Record<string, string> = {};
    if (this.config.useCoinGeckoDemoKey()) {
      headers['x-cg-demo-api-key'] = apiKey;
    } else {
      headers['x-cg-pro-api-key'] = apiKey;
    }
    return headers;
  }

  private networkId(chainId: SupportedCustomTokenChainId): string {
    switch (chainId) {
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
        return 'avax';
      case 'ethereum':
      default:
        return 'eth';
    }
  }

  private assetPlatformId(chainId: SupportedCustomTokenChainId): string {
    switch (chainId) {
      case 'bsc':
        return 'binance-smart-chain';
      case 'polygon':
        return 'polygon-pos';
      case 'arbitrum':
        return 'arbitrum-one';
      case 'base':
        return 'base';
      case 'optimism':
        return 'optimistic-ethereum';
      case 'avalanche':
        return 'avalanche';
      case 'ethereum':
      default:
        return 'ethereum';
    }
  }

  private normalizeDexChainId(chainId?: string): SupportedCustomTokenChainId | null {
    switch ((chainId ?? '').trim().toLowerCase()) {
      case 'ethereum':
        return 'ethereum';
      case 'bsc':
        return 'bsc';
      case 'polygon':
        return 'polygon';
      case 'arbitrum':
        return 'arbitrum';
      case 'base':
        return 'base';
      case 'optimism':
        return 'optimism';
      case 'avalanche':
      case 'avalanche_c':
        return 'avalanche';
      default:
        return null;
    }
  }
}
