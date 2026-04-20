import { Injectable, Logger } from '@nestjs/common';
import { TokenSearchConfig } from './token-search.config';
import {
  dedupeCustomTokenCandidates,
  detectCustomTokenSearchMode,
  normalizeCustomTokenChainId,
  type ChainTokenSearchProvider,
  type CustomTokenSearchCandidate,
} from './token-search.types';
import { JupiterTokenProvider } from './providers/jupiter-token.provider';
import { CoinGeckoTokenProvider } from './providers/coingecko-token.provider';
import { TronScanTokenProvider } from './providers/tronscan-token.provider';

type CacheEntry = {
  expiresAt: number;
  value: CustomTokenSearchCandidate[];
};

@Injectable()
export class CustomTokenSearchService {
  private readonly logger = new Logger(CustomTokenSearchService.name);
  private readonly cache = new Map<string, CacheEntry>();

  constructor(
    private readonly config: TokenSearchConfig,
    private readonly jupiterProvider: JupiterTokenProvider,
    private readonly coinGeckoProvider: CoinGeckoTokenProvider,
    private readonly tronScanProvider: TronScanTokenProvider,
  ) {}

  async search(chainId: string, query: string): Promise<CustomTokenSearchCandidate[]> {
    const normalizedChainId = normalizeCustomTokenChainId(chainId);
    const normalizedQuery = query.trim();
    if (!normalizedChainId || !normalizedQuery) {
      return [];
    }
    const provider = this.resolveProvider(normalizedChainId);
    const mode = detectCustomTokenSearchMode(normalizedChainId, normalizedQuery);
    return this.getCached(
      provider.providerName,
      normalizedChainId,
      mode,
      normalizedQuery,
      async () => {
        if (mode === 'address') {
          const item = await provider.resolveByAddress(
            normalizedChainId,
            normalizedQuery,
          );
          return item ? [item] : [];
        }
        const items = await provider.searchByKeyword(
          normalizedChainId,
          normalizedQuery,
        );
        return dedupeCustomTokenCandidates(items).slice(0, 10);
      },
    );
  }

  private resolveProvider(chainId: ReturnType<typeof normalizeCustomTokenChainId>) {
    switch (chainId) {
      case 'solana':
        return this.jupiterProvider;
      case 'tron':
        return this.tronScanProvider;
      default:
        return this.coinGeckoProvider;
    }
  }

  private async getCached(
    providerName: ChainTokenSearchProvider['providerName'],
    chainId: string,
    mode: string,
    query: string,
    loader: () => Promise<CustomTokenSearchCandidate[]>,
  ) {
    const key = `${providerName}:${chainId}:${mode}:${query.toLowerCase()}`;
    const now = Date.now();
    const cached = this.cache.get(key);
    if (cached && cached.expiresAt > now) {
      return cached.value;
    }
    try {
      const value = await loader();
      this.cache.set(key, {
        expiresAt: now + this.config.getCacheTtlMs(),
        value,
      });
      return value;
    } catch (error) {
      this.logger.warn(
        `Custom token search failed for ${key}`,
        error instanceof Error ? error.message : String(error),
      );
      return [];
    }
  }
}
