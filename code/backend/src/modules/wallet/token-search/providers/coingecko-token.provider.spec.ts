import { CoinGeckoTokenProvider } from './coingecko-token.provider';
import { TokenSearchConfig } from '../token-search.config';

describe('CoinGeckoTokenProvider', () => {
  const config = {
    getCoinGeckoApiKey: () => 'cg-key',
    getCoinGeckoBaseUrl: () => 'https://api.coingecko.com/api/v3',
    useCoinGeckoDemoKey: () => false,
    getDexScreenerBaseUrl: () => 'https://api.dexscreener.com/latest',
    getTimeoutMs: () => 1000,
  } as TokenSearchConfig;

  let provider: CoinGeckoTokenProvider;
  let fetchMock: jest.Mock;

  beforeEach(() => {
    provider = new CoinGeckoTokenProvider(config);
    fetchMock = jest.fn();
    global.fetch = fetchMock as unknown as typeof fetch;
  });

  it('falls back to dexscreener when coingecko keyword search finds no persistable address', async () => {
    fetchMock
      .mockResolvedValueOnce({
        ok: true,
        json: async () => ({
          coins: [{ id: 'usd-coin', symbol: 'usdc', name: 'USD Coin' }],
        }),
      })
      .mockResolvedValueOnce({
        ok: true,
        json: async () => ({
          id: 'usd-coin',
          symbol: 'usdc',
          name: 'USD Coin',
          platforms: {},
        }),
      })
      .mockResolvedValueOnce({
        ok: true,
        json: async () => ({
          pairs: [
            {
              chainId: 'base',
              baseToken: {
                address: '0x833589fCD6EDB6E08f4c7C32D4f71b54bdA02913',
                name: 'USD Coin',
                symbol: 'USDC',
              },
              info: {
                imageUrl: 'https://example.com/usdc.png',
              },
            },
          ],
        }),
      });

    const result = await provider.searchByKeyword('base', 'usdc');

    expect(result).toEqual([
      expect.objectContaining({
        tokenAddress: '0x833589fCD6EDB6E08f4c7C32D4f71b54bdA02913',
        chainId: 'base',
      }),
    ]);
    expect(fetchMock).toHaveBeenCalledTimes(3);
  });

  it('does not use dexscreener for exact address resolve', async () => {
    fetchMock.mockResolvedValueOnce({
      ok: true,
      json: async () => ({
        data: {
          attributes: {
            address: '0x833589fCD6EDB6E08f4c7C32D4f71b54bdA02913',
            name: 'USD Coin',
            symbol: 'USDC',
            decimals: 6,
            image_url: 'https://example.com/usdc.png',
          },
        },
      }),
    });

    const result = await provider.resolveByAddress(
      'base',
      '0x833589fCD6EDB6E08f4c7C32D4f71b54bdA02913',
    );

    expect(result).toEqual(
      expect.objectContaining({
        tokenAddress: '0x833589fCD6EDB6E08f4c7C32D4f71b54bdA02913',
        decimals: 6,
      }),
    );
    expect(fetchMock).toHaveBeenCalledTimes(1);
  });
});
