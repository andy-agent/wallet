import { Module } from '@nestjs/common';
import { AuthModule } from '../auth/auth.module';
import { MarketModule } from '../market/market.module';
import { ProvisioningModule } from '../provisioning/provisioning.module';
import { SolanaClientModule } from '../solana-client/solana-client.module';
import { TronClientModule } from '../tron-client/tron-client.module';
import { OrderPaymentMatcherScheduler } from './order-payment-matcher.scheduler';
import { OrderPaymentMatcherService } from './order-payment-matcher.service';
import { OrdersController } from './orders.controller';
import { OrdersService } from './orders.service';

@Module({
  imports: [AuthModule, MarketModule, ProvisioningModule, SolanaClientModule, TronClientModule],
  controllers: [OrdersController],
  providers: [OrdersService, OrderPaymentMatcherService, OrderPaymentMatcherScheduler],
  exports: [OrdersService, OrderPaymentMatcherService],
})
export class OrdersModule {}
