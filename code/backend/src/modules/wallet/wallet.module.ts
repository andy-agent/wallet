import { Module } from '@nestjs/common';
import { AuthModule } from '../auth/auth.module';
import { OrdersModule } from '../orders/orders.module';
import { MarketModule } from '../market/market.module';
import { SolanaClientModule } from '../solana-client/solana-client.module';
import { TronClientModule } from '../tron-client/tron-client.module';
import { WalletController } from './wallet.controller';
import { WalletsController } from './wallets.controller';
import { WalletBackupCryptoService } from './wallet-backup-crypto.service';
import { WalletBackupRelayService } from './wallet-backup-relay.service';
import { WalletService } from './wallet.service';
import { CustomTokenSearchService } from './token-search/custom-token-search.service';
import { TokenSearchConfig } from './token-search/token-search.config';
import { JupiterTokenProvider } from './token-search/providers/jupiter-token.provider';
import { CoinGeckoTokenProvider } from './token-search/providers/coingecko-token.provider';
import { TronScanTokenProvider } from './token-search/providers/tronscan-token.provider';

@Module({
  imports: [AuthModule, OrdersModule, MarketModule, SolanaClientModule, TronClientModule],
  controllers: [WalletController, WalletsController],
  providers: [
    WalletService,
    WalletBackupCryptoService,
    WalletBackupRelayService,
    TokenSearchConfig,
    CustomTokenSearchService,
    JupiterTokenProvider,
    CoinGeckoTokenProvider,
    TronScanTokenProvider,
  ],
})
export class WalletModule {}
