import { Injectable, Logger } from '@nestjs/common';
import { SolanaClientService } from '../solana-client/solana-client.service';
import { TronClientService } from '../tron-client/tron-client.service';

type ChainSideServiceHealth = {
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

type AggregatedChainSideHealth = {
  enabled: boolean;
  status: 'disabled' | 'healthy' | 'degraded';
  message: string;
  services: {
    solana: ChainSideServiceHealth;
    tron: ChainSideServiceHealth;
  };
};

@Injectable()
export class HealthService {
  private readonly logger = new Logger(HealthService.name);

  constructor(
    private readonly solanaClient: SolanaClientService,
    private readonly tronClient: TronClientService,
  ) {}

  async getHealth() {
    const chainSide = await this.getChainSideHealth();

    return {
      status: chainSide.status === 'degraded' ? 'degraded' : 'healthy',
      service: 'cryptovpn-backend',
      chainSide,
    };
  }

  private async getChainSideHealth(): Promise<AggregatedChainSideHealth> {
    const services = {
      solana: await this.getSolanaHealth(),
      tron: await this.getTronHealth(),
    };

    const enabled = Object.values(services).some((service) => service.enabled);
    if (!enabled) {
      return {
        enabled: false,
        status: 'disabled',
        message: 'Remote chain-side health checks are disabled',
        services,
      };
    }

    const degraded = Object.values(services).some(
      (service) => service.enabled && service.status === 'degraded',
    );

    return {
      enabled: true,
      status: degraded ? 'degraded' : 'healthy',
      message: degraded
        ? 'One or more remote chain-side services are degraded'
        : 'Remote chain-side services are reachable',
      services,
    };
  }

  private async getSolanaHealth(): Promise<ChainSideServiceHealth> {
    if (!this.solanaClient.isEnabled()) {
      return {
        enabled: false,
        status: 'disabled',
        message: 'Solana remote health checks are disabled',
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
        'Solana remote chain-side health check failed',
        error instanceof Error ? error.message : error,
      );

      return {
        enabled: true,
        status: 'degraded',
        message: 'Remote chain-side health check failed',
      };
    }
  }

  private async getTronHealth(): Promise<ChainSideServiceHealth> {
    if (!this.tronClient.isEnabled()) {
      return {
        enabled: false,
        status: 'disabled',
        message: 'TRON remote health checks are disabled',
      };
    }

    try {
      const upstream = await this.tronClient.health();
      const blockHeight = upstream.checks?.chain?.blockHeight;
      return {
        enabled: true,
        status: upstream.status === 'healthy' ? 'healthy' : 'degraded',
        message:
          upstream.status === 'healthy'
            ? 'TRON remote chain-side service is reachable'
            : 'TRON remote chain-side service reported a non-healthy status',
        upstream: {
          status: upstream.status,
          version: upstream.version,
          blockHeight,
        },
      };
    } catch (error) {
      this.logger.warn(
        'TRON remote chain-side health check failed',
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
