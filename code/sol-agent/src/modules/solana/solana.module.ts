import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { SolanaRpcService } from './solana.rpc.service';

@Module({
  imports: [ConfigModule],
  providers: [SolanaRpcService],
  exports: [SolanaRpcService],
})
export class SolanaModule {}
