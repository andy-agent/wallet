import { Module } from '@nestjs/common';
import { AuthModule } from '../auth/auth.module';
import { ProvisioningModule } from '../provisioning/provisioning.module';
import { SolanaClientModule } from '../solana-client/solana-client.module';
import { OrderPaymentMatcherScheduler } from './order-payment-matcher.scheduler';
import { OrderPaymentMatcherService } from './order-payment-matcher.service';
import { OrdersController } from './orders.controller';
import { OrdersService } from './orders.service';

@Module({
  imports: [AuthModule, ProvisioningModule, SolanaClientModule],
  controllers: [OrdersController],
  providers: [OrdersService, OrderPaymentMatcherService, OrderPaymentMatcherScheduler],
  exports: [OrdersService, OrderPaymentMatcherService],
})
export class OrdersModule {}
