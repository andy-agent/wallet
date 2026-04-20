import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';

@Injectable()
export class TokenSearchConfig {
  constructor(private readonly configService: ConfigService) {}

  getTimeoutMs(): number {
    return Number(this.configService.get<string>('TOKEN_SEARCH_TIMEOUT_MS') ?? 12000);
  }

  getCacheTtlMs(): number {
    return Number(this.configService.get<string>('TOKEN_SEARCH_CACHE_TTL_MS') ?? 300000);
  }

  getJupiterBaseUrl(): string {
    return this.configService.get<string>('JUPITER_BASE_URL') ?? 'https://api.jup.ag/tokens/v2';
  }

  getJupiterApiKey(): string | null {
    return this.configService.get<string>('JUPITER_API_KEY')?.trim() || null;
  }

  getJupiterOrganizationId(): string | null {
    return this.configService.get<string>('JUPITER_ORGANIZATION_ID')?.trim() || null;
  }

  getCoinGeckoBaseUrl(): string {
    return this.configService.get<string>('COINGECKO_BASE_URL') ?? 'https://api.coingecko.com/api/v3';
  }

  getCoinGeckoApiKey(): string | null {
    return this.configService.get<string>('COINGECKO_API_KEY')?.trim() || null;
  }

  useCoinGeckoDemoKey(): boolean {
    const apiKey = this.getCoinGeckoApiKey();
    return Boolean(apiKey && apiKey.startsWith('CG-'));
  }

  getTronScanBaseUrl(): string {
    return this.configService.get<string>('TRONSCAN_BASE_URL') ?? 'https://apilist.tronscanapi.com/api';
  }

  getTronScanApiKey(): string | null {
    return this.configService.get<string>('TRONSCAN_API_KEY')?.trim() || null;
  }

  getTronScanApplicationName(): string | null {
    return this.configService.get<string>('TRONSCAN_APPLICATION_NAME')?.trim() || null;
  }

  getDexScreenerBaseUrl(): string {
    return this.configService.get<string>('DEXSCREENER_BASE_URL') ?? 'https://api.dexscreener.com/latest';
  }
}
