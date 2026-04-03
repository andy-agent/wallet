import { Module } from '@nestjs/common';
import { SolanaModule } from '../solana/solana.module';
import { HealthController } from './health.controller';

@Module({
  imports: [SolanaModule],
  controllers: [HealthController],
})
export class HealthModule {}
