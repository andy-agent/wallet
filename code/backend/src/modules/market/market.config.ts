import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';

@Injectable()
export class MarketConfig {
  constructor(private readonly configService: ConfigService) {}

  getProviderBaseUrl(): string {
    return (
      this.configService.get<string>('MARKET_PROVIDER_BASE_URL') ??
      this.configService.get<string>('COINGECKO_BASE_URL') ??
      'https://api.coingecko.com/api/v3'
    );
  }

  getProviderApiKey(): string | null {
    return this.configService.get<string>('COINGECKO_API_KEY')?.trim() || null;
  }

  useDemoApiKey(): boolean {
    try {
      return (
        new URL(this.getProviderBaseUrl()).hostname === 'api.coingecko.com'
      );
    } catch {
      return true;
    }
  }

  getProviderTimeoutMs(): number {
    return Number(
      this.configService.get<string>('MARKET_PROVIDER_TIMEOUT_MS') ?? 20000,
    );
  }

  getCacheTtlMs(): number {
    return Number(
      this.configService.get<string>('MARKET_CACHE_TTL_MS') ?? 300000,
    );
  }

  getDexScreenerBaseUrl(): string {
    return (
      this.configService.get<string>('MARKET_DEXSCREENER_BASE_URL') ??
      this.configService.get<string>('DEXSCREENER_BASE_URL') ??
      'https://api.dexscreener.com/latest'
    );
  }
}
