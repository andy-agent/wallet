import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';

@Injectable()
export class MarketConfig {
  constructor(private readonly configService: ConfigService) {}

  getProviderBaseUrl(): string {
    return (
      this.configService.get<string>('MARKET_PROVIDER_BASE_URL') ??
      'https://api.coingecko.com/api/v3'
    );
  }

  getProviderTimeoutMs(): number {
    return Number(
      this.configService.get<string>('MARKET_PROVIDER_TIMEOUT_MS') ?? 8000,
    );
  }

  getCacheTtlMs(): number {
    return Number(this.configService.get<string>('MARKET_CACHE_TTL_MS') ?? 60000);
  }
}
