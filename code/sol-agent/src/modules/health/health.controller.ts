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
    
    return {
      status: rpcHealth.healthy ? 'healthy' : 'degraded',
      service: 'sol-agent',
      rpc: rpcHealth,
    };
  }
}
