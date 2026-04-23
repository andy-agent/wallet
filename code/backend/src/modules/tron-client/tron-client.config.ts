import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import type { TronServiceConfig } from './tron-client.types';

const DEFAULT_TRON_PUBLIC_FULL_NODE = 'https://api.trongrid.io';

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

  getPreferredFullNodeUrl(): string {
    return this.getOrderedFullNodeUrls()[0] ?? DEFAULT_TRON_PUBLIC_FULL_NODE;
  }

  getOrderedFullNodeUrls(): string[] {
    return Array.from(
      new Set([
        ...this.getPrivateFullNodeUrls(),
        ...this.getPublicFullNodeUrls(),
      ]),
    );
  }

  getPrivateFullNodeUrls(): string[] {
    return this.readUrlList([
      'TRON_FULL_NODES',
      'TRON_FULL_NODE',
      'TRON_RPC_URLS',
      'TRON_RPC_URL',
    ]);
  }

  getPublicFullNodeUrls(): string[] {
    const configured = this.readUrlList([
      'TRON_PUBLIC_FULL_NODES',
      'TRON_PUBLIC_FULL_NODE',
      'TRON_PUBLIC_RPC_URLS',
      'TRON_PUBLIC_RPC_URL',
    ]);

    return configured.length > 0
      ? configured
      : [DEFAULT_TRON_PUBLIC_FULL_NODE];
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

  private readUrlList(keys: string[]): string[] {
    return Array.from(
      new Set(
        keys.flatMap((key) =>
          (this.configService.get<string>(key) ?? '')
            .split(',')
            .map((item) => item.trim())
            .filter((item) => item.length > 0),
        ),
      ),
    );
  }
}
