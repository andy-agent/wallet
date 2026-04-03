import { Module } from '@nestjs/common';
import { HealthController } from './health.controller';
import { TronRpcService } from '../chain/tron-rpc.service';

@Module({
  controllers: [HealthController],
  providers: [TronRpcService],
})
export class HealthModule {}
