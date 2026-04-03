import { Module } from '@nestjs/common';
import { AuthModule } from '../auth/auth.module';
import { OrdersModule } from '../orders/orders.module';
import { PlansModule } from '../plans/plans.module';
import { VpnModule } from '../vpn/vpn.module';
import { WithdrawalsModule } from '../withdrawals/withdrawals.module';
import { AdminAccountsController } from './accounts/admin-accounts.controller';
import { AdminAccountsService } from './accounts/admin-accounts.service';
import { AdminAuthController } from './auth/admin-auth.controller';
import { AdminAuthGuard } from './auth/admin-auth.guard';
import { AdminAuthService } from './auth/admin-auth.service';
import { AdminDashboardController } from './dashboard/admin-dashboard.controller';
import { AdminDashboardService } from './dashboard/admin-dashboard.service';
import { AdminOrdersController } from './orders/admin-orders.controller';
import { AdminOrdersService } from './orders/admin-orders.service';
import { AdminPlansController } from './plans/admin-plans.controller';
import { AdminPlansService } from './plans/admin-plans.service';

@Module({
  imports: [AuthModule, OrdersModule, PlansModule, VpnModule, WithdrawalsModule],
  controllers: [
    AdminAuthController,
    AdminDashboardController,
    AdminAccountsController,
    AdminOrdersController,
    AdminPlansController,
  ],
  providers: [
    AdminAuthService,
    AdminAuthGuard,
    AdminDashboardService,
    AdminAccountsService,
    AdminOrdersService,
    AdminPlansService,
  ],
  exports: [AdminAuthService, AdminAuthGuard],
})
export class AdminModule {}
