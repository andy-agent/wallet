import { Controller, Get } from '@nestjs/common';
import { ApiTags, ApiOperation } from '@nestjs/swagger';
import { SolanaRpcService } from '../solana/solana.rpc.service';

@ApiTags('Health')
@Controller('healthz')
export class HealthController {
  constructor(private readonly solanaRpc: SolanaRpcService) {}

  @Get()
  @ApiOperation({ summary: '服务健康检查' })
  async getHealth() {
    const rpcHealth = await this.solanaRpc.checkHealth();
    const realtimeHealth = await this.solanaRpc.checkRealtimeHealth();
    
    return {
      status: rpcHealth.healthy
        ? realtimeHealth.healthy
          ? 'healthy'
          : 'degraded'
        : 'degraded',
      service: 'sol-agent',
      rpc: rpcHealth,
      realtime: realtimeHealth,
    };
  }
}
