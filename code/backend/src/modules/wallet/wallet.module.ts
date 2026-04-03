import { Module } from '@nestjs/common';
import { AuthModule } from '../auth/auth.module';
import { SolanaClientModule } from '../solana-client/solana-client.module';
import { WalletController } from './wallet.controller';
import { WalletService } from './wallet.service';

@Module({
  imports: [AuthModule, SolanaClientModule],
  controllers: [WalletController],
  providers: [WalletService],
})
export class WalletModule {}
