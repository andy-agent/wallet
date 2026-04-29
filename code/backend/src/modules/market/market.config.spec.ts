import { ConfigService } from '@nestjs/config';
import { MarketConfig } from './market.config';

describe('MarketConfig', () => {
  it('uses demo API key header only for the public CoinGecko hostname', () => {
    const config = new MarketConfig({
      get: (key: string) =>
        key === 'COINGECKO_BASE_URL'
          ? 'https://api.coingecko.com/api/v3'
          : undefined,
    } as ConfigService);

    expect(config.useDemoApiKey()).toBe(true);
  });

  it('uses pro API key header for the CoinGecko pro hostname', () => {
    const config = new MarketConfig({
      get: (key: string) =>
        key === 'COINGECKO_BASE_URL'
          ? 'https://pro-api.coingecko.com/api/v3'
          : undefined,
    } as ConfigService);

    expect(config.useDemoApiKey()).toBe(false);
  });
});
