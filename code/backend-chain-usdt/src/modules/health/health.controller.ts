import { Controller, Get } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { TronRpcService } from '../chain/tron-rpc.service';

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
      rpcUrl?: string;
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
  constructor(
    private readonly configService: ConfigService,
    private readonly tronRpcService: TronRpcService,
  ) {}

  @Get()
  async getHealth(): Promise<HealthStatus> {
    const mockMode = this.configService.get<string>('MOCK_CHAIN') === 'true';
    const rpcUrl = this.configService.get<string>('TRON_RPC_URL') ?? 'https://api.trongrid.io';
    
    let chainStatus: HealthStatus['checks']['chain'] = {
      status: mockMode ? 'mock' : 'connected',
      network: 'tron-mainnet',
      rpcUrl: mockMode ? undefined : rpcUrl,
    };

    // If not in mock mode, check actual RPC connection
    if (!mockMode) {
      const isHealthy = this.tronRpcService.isHealthy();
      const currentBlock = await this.tronRpcService.getCurrentBlock();
      
      chainStatus = {
        status: isHealthy ? 'connected' : 'disconnected',
        network: 'tron-mainnet',
        blockHeight: currentBlock?.block_header?.raw_data?.number,
        rpcUrl,
      };
    }

    // Determine overall status
    let status: HealthStatus['status'] = 'healthy';
    if (!mockMode && chainStatus.status === 'disconnected') {
      status = 'degraded';
    }

    return {
      status,
      service: this.configService.get<string>('SERVICE_NAME') ?? 'chain-usdt',
      version: this.configService.get<string>('SERVICE_VERSION') ?? '0.0.1',
      timestamp: new Date().toISOString(),
      checks: {
        chain: chainStatus,
      },
    };
  }

  @Get('ready')
  async getReadiness(): Promise<{ ready: boolean; reason?: string }> {
    const mockMode = this.configService.get<string>('MOCK_CHAIN') === 'true';
    
    // In mock mode, always ready
    if (mockMode) {
      return { ready: true };
    }

    // Check RPC connection
    const isHealthy = this.tronRpcService.isHealthy();
    if (!isHealthy) {
      return { 
        ready: false, 
        reason: 'TRON RPC connection not established' 
      };
    }

    // Verify we can get current block
    const currentBlock = await this.tronRpcService.getCurrentBlock();
    if (!currentBlock) {
      return { 
        ready: false, 
        reason: 'Cannot retrieve current block from TRON RPC' 
      };
    }

    return { ready: true };
  }
}
