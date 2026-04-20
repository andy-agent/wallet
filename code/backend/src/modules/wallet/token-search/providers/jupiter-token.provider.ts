import { Injectable, Logger } from '@nestjs/common';
import { TokenSearchConfig } from '../token-search.config';
import type {
  ChainTokenSearchProvider,
  CustomTokenSearchCandidate,
  SupportedCustomTokenChainId,
} from '../token-search.types';

type JupiterSearchItem = {
  id?: string;
  name?: string;
  symbol?: string;
  icon?: string | null;
  decimals?: number;
};

@Injectable()
export class JupiterTokenProvider implements ChainTokenSearchProvider {
  readonly providerName = 'jupiter';
  private readonly logger = new Logger(JupiterTokenProvider.name);

  constructor(private readonly config: TokenSearchConfig) {}

  async searchByKeyword(
    chainId: SupportedCustomTokenChainId,
    query: string,
  ): Promise<CustomTokenSearchCandidate[]> {
    if (chainId !== 'solana') {
      return [];
    }
    return this.search(query);
  }

  async resolveByAddress(
    chainId: SupportedCustomTokenChainId,
    address: string,
  ): Promise<CustomTokenSearchCandidate | null> {
    if (chainId !== 'solana') {
      return null;
    }
    const items = await this.search(address);
    return (
      items.find(
        (item) =>
          item.tokenAddress.trim().toLowerCase() === address.trim().toLowerCase(),
      ) ?? null
    );
  }

  private async search(query: string): Promise<CustomTokenSearchCandidate[]> {
    const apiKey = this.config.getJupiterApiKey();
    if (!apiKey) {
      this.logger.warn('JUPITER_API_KEY missing, skip Jupiter search');
      return [];
    }
    const url = new URL(`${this.config.getJupiterBaseUrl().replace(/\/$/, '')}/search`);
    url.searchParams.set('query', query.trim());
    try {
      const response = await fetch(url, {
        headers: {
          'x-api-key': apiKey,
        },
        signal: AbortSignal.timeout(this.config.getTimeoutMs()),
      });
      if (!response.ok) {
        throw new Error(`Jupiter status ${response.status}`);
      }
      const payload = (await response.json()) as JupiterSearchItem[];
      return payload
        .map((item) => this.toCandidate(item))
        .filter((item): item is CustomTokenSearchCandidate => item !== null)
        .slice(0, 10);
    } catch (error) {
      this.logger.warn(
        `Jupiter search failed for query=${query}`,
        error instanceof Error ? error.message : String(error),
      );
      return [];
    }
  }

  private toCandidate(item: JupiterSearchItem): CustomTokenSearchCandidate | null {
    const tokenAddress = item.id?.trim();
    const name = item.name?.trim();
    const symbol = item.symbol?.trim().toUpperCase();
    if (!tokenAddress || !name || !symbol || typeof item.decimals !== 'number') {
      return null;
    }
    return {
      tokenAddress,
      name,
      symbol,
      decimals: item.decimals,
      iconUrl: item.icon?.trim() || null,
      chainId: 'solana',
    };
  }
}
