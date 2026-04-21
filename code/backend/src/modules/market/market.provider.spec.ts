import { CoinGeckoMarketDataProvider } from './market.provider';

describe('CoinGeckoMarketDataProvider', () => {
  const config = {
    getProviderBaseUrl: () => 'https://api.coingecko.com/api/v3',
    getProviderApiKey: () => 'demo-key',
    useDemoApiKey: () => true,
    getProviderTimeoutMs: () => 2000,
    getCacheTtlMs: () => 300000,
    getDexScreenerBaseUrl: () => 'https://api.dexscreener.com/latest',
  };

  afterEach(() => {
    jest.restoreAllMocks();
  });

  it('falls back to DexScreener when CoinGecko onchain quote is empty', async () => {
    const provider = new CoinGeckoMarketDataProvider(config as never);
    const fetchSpy = jest
      .spyOn(global, 'fetch')
      .mockResolvedValueOnce({
        ok: true,
        json: async () => ({
          data: {
            attributes: {
              token_prices: {},
              h24_price_change_percentage: {},
            },
          },
        }),
      } as Response)
      .mockResolvedValueOnce({
        ok: true,
        json: async () => ({
          pairs: [
            {
              chainId: 'solana',
              priceUsd: '0.0002841',
              priceChange: { h24: '12.34' },
              liquidity: { usd: '12345' },
              baseToken: {
                address: '8zFP8GeszFz7FvuHesguekTxDjm4KLsJEYBZTKyMLEoE',
              },
            },
          ],
        }),
      } as Response);

    const quote = await provider.getOnchainTokenQuote(
      'solana',
      '8zFP8GeszFz7FvuHesguekTxDjm4KLsJEYBZTKyMLEoE',
      true,
    );

    expect(fetchSpy).toHaveBeenCalledTimes(2);
    expect(quote).toEqual(
      expect.objectContaining({
        currentPrice: 0.0002841,
        priceChangePct24h: 12.34,
      }),
    );
  });
});
