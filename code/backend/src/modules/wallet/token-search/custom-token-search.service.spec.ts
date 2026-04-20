import { CustomTokenSearchService } from './custom-token-search.service';
import { TokenSearchConfig } from './token-search.config';
import { JupiterTokenProvider } from './providers/jupiter-token.provider';
import { CoinGeckoTokenProvider } from './providers/coingecko-token.provider';
import { TronScanTokenProvider } from './providers/tronscan-token.provider';
import type {
  CustomTokenSearchCandidate,
  SupportedCustomTokenChainId,
} from './token-search.types';

describe('CustomTokenSearchService', () => {
  const config = {
    getCacheTtlMs: () => 300000,
  } as TokenSearchConfig;

  const jupiter = {
    providerName: 'jupiter',
    searchByKeyword: jest.fn<
      Promise<CustomTokenSearchCandidate[]>,
      [SupportedCustomTokenChainId, string]
    >(),
    resolveByAddress: jest.fn<
      Promise<CustomTokenSearchCandidate | null>,
      [SupportedCustomTokenChainId, string]
    >(),
  } as unknown as jest.Mocked<JupiterTokenProvider>;

  const coingecko = {
    providerName: 'coingecko',
    searchByKeyword: jest.fn<
      Promise<CustomTokenSearchCandidate[]>,
      [SupportedCustomTokenChainId, string]
    >(),
    resolveByAddress: jest.fn<
      Promise<CustomTokenSearchCandidate | null>,
      [SupportedCustomTokenChainId, string]
    >(),
  } as unknown as jest.Mocked<CoinGeckoTokenProvider>;

  const tronscan = {
    providerName: 'tronscan',
    searchByKeyword: jest.fn<
      Promise<CustomTokenSearchCandidate[]>,
      [SupportedCustomTokenChainId, string]
    >(),
    resolveByAddress: jest.fn<
      Promise<CustomTokenSearchCandidate | null>,
      [SupportedCustomTokenChainId, string]
    >(),
  } as unknown as jest.Mocked<TronScanTokenProvider>;

  const service = new CustomTokenSearchService(
    config,
    jupiter,
    coingecko,
    tronscan,
  );

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('routes exact solana mint to jupiter resolve', async () => {
    jupiter.resolveByAddress.mockResolvedValue({
      tokenAddress: 'EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v',
      name: 'USD Coin',
      symbol: 'USDC',
      decimals: 6,
      iconUrl: null,
      chainId: 'solana',
    });

    const result = await service.search(
      'solana',
      'EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v',
    );

    expect(jupiter.resolveByAddress).toHaveBeenCalledWith(
      'solana',
      'EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v',
    );
    expect(jupiter.searchByKeyword).not.toHaveBeenCalled();
    expect(result).toHaveLength(1);
  });

  it('routes tron keyword to tronscan search', async () => {
    tronscan.searchByKeyword.mockResolvedValue([]);

    await service.search('tron', 'usdt');

    expect(tronscan.searchByKeyword).toHaveBeenCalledWith('tron', 'usdt');
    expect(tronscan.resolveByAddress).not.toHaveBeenCalled();
  });

  it('routes evm keyword to coingecko search', async () => {
    coingecko.searchByKeyword.mockResolvedValue([]);

    await service.search('base', 'usdc');

    expect(coingecko.searchByKeyword).toHaveBeenCalledWith('base', 'usdc');
    expect(coingecko.resolveByAddress).not.toHaveBeenCalled();
  });

  it('uses cache for repeated queries', async () => {
    coingecko.searchByKeyword.mockResolvedValue([]);

    await service.search('ethereum', 'usdt');
    await service.search('ethereum', 'usdt');

    expect(coingecko.searchByKeyword).toHaveBeenCalledTimes(1);
  });
});
