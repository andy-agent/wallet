import { Controller, Get, Post, Body, Headers } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';

/**
 * Internal controller for service discovery and diagnostics.
 * 
 * SECURITY NOTE: Some endpoints may be public (info, discovery)
 * while others should be protected in production.
 */
@Controller('internal')
export class InternalController {
  constructor(private readonly configService: ConfigService) {}

  /**
   * Service discovery endpoint.
   * Allows main backend to discover this chain service.
   * 
   * PUBLIC: No auth required for discovery
   */
  @Get('discovery')
  getDiscoveryInfo() {
    return {
      service: 'chain-usdt',
      type: 'tron-trc20',
      version: this.configService.get<string>('SERVICE_VERSION') ?? '0.0.1',
      endpoints: {
        base: '/api/v1/chain',
        health: '/api/healthz',
        docs: '/api/docs',
      },
      network: {
        name: 'tron-mainnet',
        chainId: '728126428',
        nativeCurrency: 'TRX',
        supportedTokens: ['USDT'],
      },
    };
  }

  /**
   * Auth test endpoint for internal services.
   * Validates that API key authentication is working.
   * 
   * PROTECTED: Requires X-API-Key header
   */
  @Post('auth/test')
  testAuth(@Headers('x-api-key') apiKey: string) {
    // If we reach here, auth middleware/guard passed
    return {
      authenticated: true,
      service: 'chain-usdt',
      timestamp: new Date().toISOString(),
    };
  }

  /**
   * Get service configuration (sanitized).
   * 
   * PROTECTED: Should require internal auth in production
   */
  @Get('config')
  getConfig() {
    return {
      service: {
        name: this.configService.get<string>('SERVICE_NAME') ?? 'chain-usdt',
        version: this.configService.get<string>('SERVICE_VERSION') ?? '0.0.1',
        port: this.configService.get<number>('PORT') ?? 3001,
        mockMode: this.configService.get<string>('MOCK_CHAIN') === 'true',
      },
      chain: {
        network: 'tron-mainnet',
        rpcUrl: this.maskUrl(
          this.configService.get<string>('TRON_RPC_URL') ?? 'https://api.trongrid.io'
        ),
        contract: this.maskAddress(
          this.configService.get<string>('TRON_USDT_CONTRACT') ?? 
          'TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t'
        ),
      },
    };
  }

  private maskUrl(url: string): string {
    try {
      const u = new URL(url);
      return `${u.protocol}//${u.hostname}***`;
    } catch {
      return '***';
    }
  }

  private maskAddress(address: string): string {
    if (address.length < 8) return '***';
    return `${address.slice(0, 6)}...${address.slice(-4)}`;
  }
}
