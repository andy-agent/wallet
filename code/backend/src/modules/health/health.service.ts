import { Injectable, Logger } from '@nestjs/common';
import { SolanaClientService } from '../solana-client/solana-client.service';

type AggregatedChainSideHealth = {
  enabled: boolean;
  status: 'disabled' | 'healthy' | 'degraded';
  message: string;
  upstream?: {
    status: 'healthy' | 'degraded' | 'unhealthy';
    version?: string;
    blockHeight?: number;
    rpcLatencyMs?: number;
  };
};

@Injectable()
export class HealthService {
  private readonly logger = new Logger(HealthService.name);

  constructor(private readonly solanaClient: SolanaClientService) {}

  async getHealth() {
    const chainSide = await this.getChainSideHealth();

    return {
      status: chainSide.status === 'degraded' ? 'degraded' : 'healthy',
      service: 'cryptovpn-backend',
      chainSide,
    };
  }

  private async getChainSideHealth(): Promise<AggregatedChainSideHealth> {
    if (!this.solanaClient.isEnabled()) {
      return {
        enabled: false,
        status: 'disabled',
        message: 'Remote chain-side health checks are disabled',
      };
    }

    try {
      const upstream = await this.solanaClient.health();
      return {
        enabled: true,
        status: upstream.status === 'healthy' ? 'healthy' : 'degraded',
        message:
          upstream.status === 'healthy'
            ? 'Remote chain-side service is reachable'
            : 'Remote chain-side service reported a non-healthy status',
        upstream: {
          status: upstream.status,
          version: upstream.version,
          blockHeight: upstream.blockHeight,
          rpcLatencyMs: upstream.rpcLatencyMs,
        },
      };
    } catch (error) {
      this.logger.warn(
        'Remote chain-side health check failed',
        error instanceof Error ? error.message : error,
      );

      return {
        enabled: true,
        status: 'degraded',
        message: 'Remote chain-side health check failed',
      };
    }
  }
}
