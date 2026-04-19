import { Module } from '@nestjs/common';
import { AuthModule } from '../auth/auth.module';
import { OrdersModule } from '../orders/orders.module';
import { SolanaClientModule } from '../solana-client/solana-client.module';
import { TronClientModule } from '../tron-client/tron-client.module';
import { WalletController } from './wallet.controller';
import { WalletsController } from './wallets.controller';
import { WalletBackupCryptoService } from './wallet-backup-crypto.service';
import { WalletBackupRelayService } from './wallet-backup-relay.service';
import { WalletService } from './wallet.service';

@Module({
  imports: [AuthModule, OrdersModule, SolanaClientModule, TronClientModule],
  controllers: [WalletController, WalletsController],
  providers: [WalletService, WalletBackupCryptoService, WalletBackupRelayService],
})
export class WalletModule {}
