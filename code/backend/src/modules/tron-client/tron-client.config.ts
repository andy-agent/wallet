import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import type { TronServiceConfig } from './tron-client.types';

@Injectable()
export class TronClientConfig {
  constructor(private readonly configService: ConfigService) {}

  getConfig(): TronServiceConfig {
    return {
      baseUrl: this.getBaseUrl(),
      apiKey: this.getApiKey(),
      timeoutMs: this.getTimeoutMs(),
      enabled: this.isEnabled(),
    };
  }

  getBaseUrl(): string {
    return this.configService.get<string>('TRON_SERVICE_URL') ?? 'http://localhost:4000/api';
  }

  getApiKey(): string | undefined {
    return this.configService.get<string>('TRON_SERVICE_API_KEY');
  }

  getTimeoutMs(): number {
    const timeout = this.configService.get<string>('TRON_SERVICE_TIMEOUT_MS');
    return timeout ? parseInt(timeout, 10) : 30000;
  }

  isEnabled(): boolean {
    const enabled = this.configService.get<string>('TRON_SERVICE_ENABLED');
    return enabled === 'true';
  }
}
