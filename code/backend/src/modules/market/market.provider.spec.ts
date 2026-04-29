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

  it('preserves Solana token address casing for CoinGecko onchain quotes', async () => {
    const provider = new CoinGeckoMarketDataProvider(config as never);
    const fetchSpy = jest.spyOn(global, 'fetch').mockResolvedValueOnce({
      ok: true,
      json: async () => ({
        data: {
          attributes: {
            token_prices: {
              '8zFP8GeszFz7FvuHesguekTxDjm4KLsJEYBZTKyMLEoE': '0.000234',
            },
            h24_price_change_percentage: {
              '8zFP8GeszFz7FvuHesguekTxDjm4KLsJEYBZTKyMLEoE': '0.83',
            },
          },
        },
      }),
    } as Response);

    const quote = await provider.getOnchainTokenQuote(
      'solana',
      '8zFP8GeszFz7FvuHesguekTxDjm4KLsJEYBZTKyMLEoE',
      true,
    );

    const requestUrl = fetchSpy.mock.calls[0][0] as URL;
    expect(requestUrl.pathname).toContain(
      '/token_price/8zFP8GeszFz7FvuHesguekTxDjm4KLsJEYBZTKyMLEoE',
    );
    expect(quote).toEqual(
      expect.objectContaining({
        currentPrice: 0.000234,
        priceChangePct24h: 0.83,
      }),
    );
  });

  it('falls back to DexScreener when CoinGecko onchain quote request fails', async () => {
    const provider = new CoinGeckoMarketDataProvider(config as never);
    const fetchSpy = jest
      .spyOn(global, 'fetch')
      .mockResolvedValueOnce({
        ok: false,
        status: 400,
      } as Response)
      .mockResolvedValueOnce({
        ok: true,
        json: async () => ({
          pairs: [
            {
              chainId: 'solana',
              priceUsd: '0.0002337',
              priceChange: { h24: '0.81' },
              liquidity: { usd: '23456' },
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
        currentPrice: 0.0002337,
        priceChangePct24h: 0.81,
      }),
    );
  });
});
