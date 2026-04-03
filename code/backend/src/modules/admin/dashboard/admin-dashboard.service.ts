import { Injectable } from '@nestjs/common';
import { AuthService } from '../../auth/auth.service';
import { OrdersService } from '../../orders/orders.service';
import { VpnService } from '../../vpn/vpn.service';
import { WithdrawalsService } from '../../withdrawals/withdrawals.service';

@Injectable()
export class AdminDashboardService {
  constructor(
    private readonly authService: AuthService,
    private readonly vpnService: VpnService,
    private readonly ordersService: OrdersService,
    private readonly withdrawalsService: WithdrawalsService,
  ) {}

  getSummary() {
    return {
      activeAccounts: this.authService.getTotalAccounts(),
      activeSubscriptions: this.vpnService.getActiveSubscriptionCount(),
      awaitingOrders: this.ordersService.getAwaitingOrderCount(),
      reviewOrders: this.ordersService.getReviewOrderCount(),
      pendingWithdrawals: this.withdrawalsService.getPendingWithdrawalCount(),
      todayPaidOrders: this.ordersService.getTodayPaidOrderCount(),
    };
  }
}
