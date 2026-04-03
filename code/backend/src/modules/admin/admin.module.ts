import { Module } from '@nestjs/common';
import { AuthModule } from '../auth/auth.module';
import { OrdersModule } from '../orders/orders.module';
import { VpnModule } from '../vpn/vpn.module';
import { WithdrawalsModule } from '../withdrawals/withdrawals.module';
import { AdminAuthController } from './auth/admin-auth.controller';
import { AdminAuthGuard } from './auth/admin-auth.guard';
import { AdminAuthService } from './auth/admin-auth.service';
import { AdminDashboardController } from './dashboard/admin-dashboard.controller';
import { AdminDashboardService } from './dashboard/admin-dashboard.service';

@Module({
  imports: [AuthModule, OrdersModule, VpnModule, WithdrawalsModule],
  controllers: [AdminAuthController, AdminDashboardController],
  providers: [AdminAuthService, AdminAuthGuard, AdminDashboardService],
  exports: [AdminAuthService, AdminAuthGuard],
})
export class AdminModule {}
