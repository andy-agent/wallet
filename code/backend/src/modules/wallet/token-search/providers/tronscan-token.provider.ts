import { Injectable, Logger } from '@nestjs/common';
import { TokenSearchConfig } from '../token-search.config';
import type {
  ChainTokenSearchProvider,
  CustomTokenSearchCandidate,
  SupportedCustomTokenChainId,
} from '../token-search.types';

type TronScanSearchResponse = Array<{
  token_id?: string;
  name?: string;
  abbr?: string;
  icon?: string | null;
  type?: number;
}>;

type TronScanContractResponse = {
  data?: Array<{
    address?: string;
    trc20token?: {
      name?: string;
      symbol?: string;
      decimals?: string | number;
      icon_url?: string | null;
      contract_address?: string;
    };
  }>;
};

@Injectable()
export class TronScanTokenProvider implements ChainTokenSearchProvider {
  readonly providerName = 'tronscan';
  private readonly logger = new Logger(TronScanTokenProvider.name);

  constructor(private readonly config: TokenSearchConfig) {}

  async searchByKeyword(
    chainId: SupportedCustomTokenChainId,
    query: string,
  ): Promise<CustomTokenSearchCandidate[]> {
    if (chainId !== 'tron') {
      return [];
    }
    const apiKey = this.config.getTronScanApiKey();
    if (!apiKey) {
      this.logger.warn('TRONSCAN_API_KEY missing, skip TronScan search');
      return [];
    }
    try {
      const url = new URL(`${this.config.getTronScanBaseUrl().replace(/\/$/, '')}/search/bar`);
      url.searchParams.set('term', query.trim());
      url.searchParams.set('type', 'token');
      url.searchParams.set('limit', '10');
      url.searchParams.set('start', '0');
      const response = await fetch(url, {
        headers: {
          'TRON-PRO-API-KEY': apiKey,
        },
        signal: AbortSignal.timeout(this.config.getTimeoutMs()),
      });
      if (!response.ok) {
        throw new Error(`TronScan status ${response.status}`);
      }
      const items = (await response.json()) as TronScanSearchResponse;
      return items
        .filter((item) => item.type === 20 && item.token_id)
        .map((item) => ({
          tokenAddress: item.token_id!.trim(),
          name: item.name?.trim() || item.abbr?.trim() || item.token_id!.trim(),
          symbol: item.abbr?.trim()?.toUpperCase() || 'TOKEN',
          decimals: 6,
          iconUrl: item.icon?.trim() || null,
          chainId: 'tron' as const,
        }))
        .slice(0, 10);
    } catch (error) {
      this.logger.warn(
        `TronScan search failed for query=${query}`,
        error instanceof Error ? error.message : String(error),
      );
      return [];
    }
  }

  async resolveByAddress(
    chainId: SupportedCustomTokenChainId,
    address: string,
  ): Promise<CustomTokenSearchCandidate | null> {
    if (chainId !== 'tron') {
      return null;
    }
    const apiKey = this.config.getTronScanApiKey();
    if (!apiKey) {
      this.logger.warn('TRONSCAN_API_KEY missing, skip TronScan address resolve');
      return null;
    }
    try {
      const url = new URL(`${this.config.getTronScanBaseUrl().replace(/\/$/, '')}/contracts`);
      url.searchParams.set('search', address.trim());
      url.searchParams.set('limit', '1');
      url.searchParams.set('start', '0');
      url.searchParams.set('verified-only', 'false');
      url.searchParams.set('open-source-only', 'false');
      const response = await fetch(url, {
        headers: {
          'TRON-PRO-API-KEY': apiKey,
        },
        signal: AbortSignal.timeout(this.config.getTimeoutMs()),
      });
      if (!response.ok) {
        throw new Error(`TronScan status ${response.status}`);
      }
      const payload = (await response.json()) as TronScanContractResponse;
      const item = (payload.data ?? []).find(
        (entry) =>
          entry.address?.trim().toLowerCase() === address.trim().toLowerCase(),
      );
      if (!item?.address) {
        return null;
      }
      const tokenInfo = item.trc20token;
      return {
        tokenAddress: tokenInfo?.contract_address?.trim() || item.address.trim(),
        name:
          tokenInfo?.name?.trim() ||
          tokenInfo?.symbol?.trim() ||
          item.address.trim(),
        symbol: tokenInfo?.symbol?.trim()?.toUpperCase() || 'TOKEN',
        decimals: Number(tokenInfo?.decimals ?? 6),
        iconUrl: tokenInfo?.icon_url?.trim() || null,
        chainId: 'tron',
      };
    } catch (error) {
      this.logger.warn(
        `TronScan resolveByAddress failed for ${address}`,
        error instanceof Error ? error.message : String(error),
      );
      return null;
    }
  }
}
