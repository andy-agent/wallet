/**
 * Solana Client Configuration
 *
 * Configuration for sol/usdt chain-side service client.
 * Uses NestJS ConfigService pattern.
 */

import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import type { SolanaServiceConfig } from './solana-client.types';

@Injectable()
export class SolanaClientConfig {
  constructor(private readonly configService: ConfigService) {}

  /**
   * Get the full service configuration
   */
  getConfig(): SolanaServiceConfig {
    return {
      baseUrl: this.getBaseUrl(),
      apiKey: this.getApiKey(),
      timeoutMs: this.getTimeoutMs(),
      enabled: this.isEnabled(),
    };
  }

  /**
   * Base URL for sol/usdt service
   * Default: http://localhost:8080 (for local development)
   */
  getBaseUrl(): string {
    return (
      this.configService.get<string>('SOLANA_SERVICE_URL') ??
      'http://localhost:8080'
    );
  }

  /**
   * API key for service authentication
   * Optional: only required if service has auth enabled
   */
  getApiKey(): string | undefined {
    return this.configService.get<string>('SOLANA_SERVICE_API_KEY');
  }

  /**
   * Request timeout in milliseconds
   * Default: 30000 (30 seconds)
   */
  getTimeoutMs(): number {
    const timeout = this.configService.get<string>('SOLANA_SERVICE_TIMEOUT_MS');
    return timeout ? parseInt(timeout, 10) : 30000;
  }

  /**
   * Whether real chain calls are enabled
   * When false, client operates in mock/simulation mode
   * Default: false (safety first - mock mode by default)
   */
  isEnabled(): boolean {
    const enabled = this.configService.get<string>('SOLANA_SERVICE_ENABLED');
    return enabled === 'true';
  }

  /**
   * Whether to use devnet instead of mainnet
   * Default: true (safety first - devnet by default)
   */
  useDevnet(): boolean {
    const devnet = this.configService.get<string>('SOLANA_SERVICE_USE_DEVNET');
    return devnet !== 'false'; // default true
  }

  /**
   * Max retry attempts for failed requests
   * Default: 3
   */
  getMaxRetries(): number {
    const retries = this.configService.get<string>(
      'SOLANA_SERVICE_MAX_RETRIES',
    );
    return retries ? parseInt(retries, 10) : 3;
  }
}
