/**
 * Solana Client Configuration
 *
 * Configuration for sol/usdt chain-side service client.
 * Uses NestJS ConfigService pattern.
 */

import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import type { SolanaServiceConfig } from './solana-client.types';

const DEFAULT_SOLANA_MAINNET_PUBLIC_RPC = 'https://api.mainnet-beta.solana.com';
const DEFAULT_SOLANA_DEVNET_PUBLIC_RPC = 'https://api.devnet.solana.com';

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

  getRpcUrl(network: 'mainnet' | 'devnet'): string {
    return this.getPreferredRpcUrl(network);
  }

  getPreferredRpcUrl(network: 'mainnet' | 'devnet'): string {
    return this.getOrderedRpcUrls(network)[0] ?? this.getDefaultPublicRpcUrl(network);
  }

  getPreferredWsUrl(network: 'mainnet' | 'devnet'): string {
    return this.getOrderedWsUrls(network)[0] ?? this.deriveWsUrlFromRpcUrl(this.getPreferredRpcUrl(network));
  }

  getOrderedRpcUrls(network: 'mainnet' | 'devnet'): string[] {
    return Array.from(
      new Set([
        ...this.getPrivateRpcUrls(network),
        ...this.getPublicRpcUrls(network),
      ]),
    );
  }

  getOrderedWsUrls(network: 'mainnet' | 'devnet'): string[] {
    const configured = Array.from(
      new Set([
        ...this.getPrivateWsUrls(network),
        ...this.getPublicWsUrls(network),
      ]),
    );

    if (configured.length > 0) {
      return configured;
    }

    return this.getOrderedRpcUrls(network).map((rpcUrl) =>
      this.deriveWsUrlFromRpcUrl(rpcUrl),
    );
  }

  getPrivateRpcUrls(network: 'mainnet' | 'devnet'): string[] {
    if (network === 'devnet') {
      return this.readUrlList([
        'SOLANA_RPC_URL_DEVNETS',
        'SOLANA_RPC_URL_DEVNET',
        'SOLANA_RPC_URLS',
        'SOLANA_RPC_URL',
      ]);
    }

    return this.readUrlList([
      'SOLANA_RPC_URL_MAINNETS',
      'SOLANA_RPC_URL_MAINNET',
      'SOLANA_RPC_URLS',
      'SOLANA_RPC_URL',
    ]);
  }

  getPublicRpcUrls(network: 'mainnet' | 'devnet'): string[] {
    const configured =
      network === 'devnet'
        ? this.readUrlList([
            'SOLANA_PUBLIC_RPC_URL_DEVNETS',
            'SOLANA_PUBLIC_RPC_URL_DEVNET',
            'SOLANA_PUBLIC_RPC_URLS',
            'SOLANA_PUBLIC_RPC_URL',
          ])
        : this.readUrlList([
            'SOLANA_PUBLIC_RPC_URL_MAINNETS',
            'SOLANA_PUBLIC_RPC_URL_MAINNET',
            'SOLANA_PUBLIC_RPC_URLS',
            'SOLANA_PUBLIC_RPC_URL',
          ]);

    return configured.length > 0
      ? configured
      : [this.getDefaultPublicRpcUrl(network)];
  }

  getPrivateWsUrls(network: 'mainnet' | 'devnet'): string[] {
    if (network === 'devnet') {
      return this.readUrlList([
        'SOLANA_WS_URL_DEVNETS',
        'SOLANA_WS_URL_DEVNET',
        'SOLANA_WS_URLS',
        'SOLANA_WS_URL',
      ]);
    }

    return this.readUrlList([
      'SOLANA_WS_URL_MAINNETS',
      'SOLANA_WS_URL_MAINNET',
      'SOLANA_WS_URLS',
      'SOLANA_WS_URL',
    ]);
  }

  getPublicWsUrls(network: 'mainnet' | 'devnet'): string[] {
    return network === 'devnet'
      ? this.readUrlList([
          'SOLANA_PUBLIC_WS_URL_DEVNETS',
          'SOLANA_PUBLIC_WS_URL_DEVNET',
          'SOLANA_PUBLIC_WS_URLS',
          'SOLANA_PUBLIC_WS_URL',
        ])
      : this.readUrlList([
          'SOLANA_PUBLIC_WS_URL_MAINNETS',
          'SOLANA_PUBLIC_WS_URL_MAINNET',
          'SOLANA_PUBLIC_WS_URLS',
          'SOLANA_PUBLIC_WS_URL',
        ]);
  }

  private getDefaultPublicRpcUrl(network: 'mainnet' | 'devnet') {
    return network === 'devnet'
      ? DEFAULT_SOLANA_DEVNET_PUBLIC_RPC
      : DEFAULT_SOLANA_MAINNET_PUBLIC_RPC;
  }

  private deriveWsUrlFromRpcUrl(rpcUrl: string) {
    if (rpcUrl.startsWith('https://')) {
      return `wss://${rpcUrl.slice('https://'.length)}`;
    }
    if (rpcUrl.startsWith('http://')) {
      return `ws://${rpcUrl.slice('http://'.length)}`;
    }
    return rpcUrl;
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
   * Default: false in production, true otherwise
   */
  useDevnet(): boolean {
    const devnet = this.configService.get<string>('SOLANA_SERVICE_USE_DEVNET');
    if (devnet !== undefined) {
      return devnet === 'true';
    }
    const nodeEnv = (
      this.configService.get<string>('NODE_ENV') ??
      process.env.NODE_ENV ??
      'development'
    ).toLowerCase();
    return nodeEnv !== 'production';
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
