import { Module } from '@nestjs/common';
import { AuthModule } from '../auth/auth.module';
import { OrdersModule } from '../orders/orders.module';
import { PlansModule } from '../plans/plans.module';
import { VpnModule } from '../vpn/vpn.module';
import { WithdrawalsModule } from '../withdrawals/withdrawals.module';
import { AdminAccountsController } from './accounts/admin-accounts.controller';
import { AdminAccountsService } from './accounts/admin-accounts.service';
import { AdminAuditController } from './audit/admin-audit.controller';
import { AdminAuditService } from './audit/admin-audit.service';
import { AdminAuthController } from './auth/admin-auth.controller';
import { AdminAuthGuard } from './auth/admin-auth.guard';
import { AdminAuthService } from './auth/admin-auth.service';
import { AdminDashboardController } from './dashboard/admin-dashboard.controller';
import { AdminDashboardService } from './dashboard/admin-dashboard.service';
import { AdminLegalController } from './legal/admin-legal.controller';
import { AdminLegalService } from './legal/admin-legal.service';
import { AdminOrdersController } from './orders/admin-orders.controller';
import { AdminOrdersService } from './orders/admin-orders.service';
import { AdminPlansController } from './plans/admin-plans.controller';
import { AdminPlansService } from './plans/admin-plans.service';
import { AdminSystemConfigsController } from './system-configs/admin-system-configs.controller';
import { AdminSystemConfigsService } from './system-configs/admin-system-configs.service';
import { AdminVersionsController } from './versions/admin-versions.controller';
import { AdminVersionsService } from './versions/admin-versions.service';
import { AdminVpnController } from './vpn/admin-vpn.controller';
import { AdminVpnService } from './vpn/admin-vpn.service';
import { AdminWithdrawalsController } from './withdrawals/admin-withdrawals.controller';
import { AdminWithdrawalsService } from './withdrawals/admin-withdrawals.service';

@Module({
  imports: [AuthModule, OrdersModule, PlansModule, VpnModule, WithdrawalsModule],
  controllers: [
    AdminAuthController,
    AdminDashboardController,
    AdminAccountsController,
    AdminOrdersController,
    AdminPlansController,
    AdminWithdrawalsController,
    AdminAuditController,
    AdminSystemConfigsController,
    AdminLegalController,
    AdminVersionsController,
    AdminVpnController,
  ],
  providers: [
    AdminAuthService,
    AdminAuthGuard,
    AdminDashboardService,
    AdminAccountsService,
    AdminOrdersService,
    AdminPlansService,
    AdminWithdrawalsService,
    AdminAuditService,
    AdminSystemConfigsService,
    AdminLegalService,
    AdminVersionsService,
    AdminVpnService,
  ],
  exports: [AdminAuthService, AdminAuthGuard],
})
export class AdminModule {}
