import { Module } from '@nestjs/common';
import { SolanaClientModule } from '../solana-client/solana-client.module';
import { HealthController } from './health.controller';
import { HealthService } from './health.service';

@Module({
  imports: [SolanaClientModule],
  controllers: [HealthController],
  providers: [HealthService],
})
export class HealthModule {}
