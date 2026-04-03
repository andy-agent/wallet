import { Controller, Get, Version } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';

interface HealthStatus {
  status: 'healthy' | 'degraded' | 'unhealthy';
  service: string;
  version: string;
  timestamp: string;
  checks: {
    chain: {
      status: 'connected' | 'disconnected' | 'mock';
      network: string;
      blockHeight?: number;
    };
  };
}

/**
 * Health check endpoint for the chain service.
 * Used by:
 * - Load balancers (e.g., nginx, Cloudflare)
 * - Kubernetes health probes
 * - Main backend API to check chain service availability
 * - Monitoring systems
 */
@Controller('healthz')
export class HealthController {
  constructor(private readonly configService: ConfigService) {}

  @Get()
  getHealth(): HealthStatus {
    const mockMode = this.configService.get<string>('MOCK_CHAIN') === 'true';
    
    return {
      status: 'healthy',
      service: this.configService.get<string>('SERVICE_NAME') ?? 'chain-usdt',
      version: this.configService.get<string>('SERVICE_VERSION') ?? '0.0.1',
      timestamp: new Date().toISOString(),
      checks: {
        chain: {
          status: mockMode ? 'mock' : 'connected',
          network: 'tron-mainnet',
          blockHeight: mockMode ? 12345678 : undefined,
        },
      },
    };
  }

  @Get('ready')
  getReadiness(): { ready: boolean; reason?: string } {
    // Placeholder for readiness check
    // In production, this would verify:
    // - RPC connection is established
    // - Database connection is healthy (if using db)
    // - Required configuration is loaded
    return { ready: true };
  }
}
