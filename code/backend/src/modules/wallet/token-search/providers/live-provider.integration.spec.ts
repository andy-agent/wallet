import { TokenSearchConfig } from '../token-search.config';
import { JupiterTokenProvider } from './jupiter-token.provider';
import { CoinGeckoTokenProvider } from './coingecko-token.provider';
import { TronScanTokenProvider } from './tronscan-token.provider';

  const liveConfig = {
    getTimeoutMs: () => 15000,
    getCacheTtlMs: () => 300000,
    getJupiterBaseUrl: () => process.env.JUPITER_BASE_URL ?? 'https://api.jup.ag/tokens/v2',
    getJupiterApiKey: () => process.env.JUPITER_API_KEY ?? null,
    getJupiterOrganizationId: () => process.env.JUPITER_ORGANIZATION_ID ?? null,
    getCoinGeckoBaseUrl: () =>
      process.env.COINGECKO_BASE_URL ?? 'https://api.coingecko.com/api/v3',
    getCoinGeckoApiKey: () => process.env.COINGECKO_API_KEY ?? null,
    useCoinGeckoDemoKey: () => true,
    getTronScanBaseUrl: () =>
      process.env.TRONSCAN_BASE_URL ?? 'https://apilist.tronscanapi.com/api',
  getTronScanApiKey: () => process.env.TRONSCAN_API_KEY ?? null,
  getTronScanApplicationName: () =>
    process.env.TRONSCAN_APPLICATION_NAME ?? null,
  getDexScreenerBaseUrl: () =>
    process.env.DEXSCREENER_BASE_URL ?? 'https://api.dexscreener.com/latest',
} as TokenSearchConfig;

describe('Live custom token search providers', () => {
  const jupiter = new JupiterTokenProvider(liveConfig);
  const coingecko = new CoinGeckoTokenProvider(liveConfig);
  const tronscan = new TronScanTokenProvider(liveConfig);

  beforeAll(() => {
    const required = [
      'JUPITER_API_KEY',
      'JUPITER_ORGANIZATION_ID',
      'COINGECKO_API_KEY',
      'TRONSCAN_API_KEY',
      'TRONSCAN_APPLICATION_NAME',
    ];
    const missing = required.filter((key) => !process.env[key]);
    if (missing.length > 0) {
      throw new Error(`Missing required live provider env: ${missing.join(', ')}`);
    }
  });

  it('Jupiter resolves exact Solana mint', async () => {
    const result = await jupiter.resolveByAddress(
      'solana',
      'EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v',
    );
    expect(result).toEqual(
      expect.objectContaining({
        tokenAddress: 'EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v',
        symbol: 'USDC',
        chainId: 'solana',
      }),
    );
  });

  it('CoinGecko resolves exact Base token contract', async () => {
    const result = await coingecko.resolveByAddress(
      'base',
      '0x833589fCD6EDB6E08f4c7C32D4f71b54bdA02913',
    );
    expect(result).toEqual(
      expect.objectContaining({
        tokenAddress: '0x833589fcd6edb6e08f4c7c32d4f71b54bda02913',
        symbol: 'USDC',
        chainId: 'base',
      }),
    );
  });

  it('TronScan resolves exact TRON token contract', async () => {
    const result = await tronscan.resolveByAddress(
      'tron',
      'TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t',
    );
    expect(result).toEqual(
      expect.objectContaining({
        tokenAddress: 'TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t',
        symbol: 'USDT',
        chainId: 'tron',
      }),
    );
  });

  it('CoinGecko keyword search returns EVM results or fallback results with precise addresses', async () => {
    const result = await coingecko.searchByKeyword('base', 'usdc');
    expect(result.length).toBeGreaterThan(0);
    expect(result[0]).toEqual(
      expect.objectContaining({
        tokenAddress: expect.stringMatching(/^0x[a-fA-F0-9]{40}$/),
        chainId: 'base',
      }),
    );
  });
});
