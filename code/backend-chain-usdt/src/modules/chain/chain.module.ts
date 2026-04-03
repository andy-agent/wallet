import { Module } from '@nestjs/common';
import { ChainController } from './chain.controller';
import { ChainService } from './chain.service';
import { TronRpcService } from './tron-rpc.service';

@Module({
  controllers: [ChainController],
  providers: [ChainService, TronRpcService],
  exports: [ChainService, TronRpcService],
})
export class ChainModule {}
