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

  async getSummary() {
    const [
      activeSubscriptions,
      awaitingOrders,
      reviewOrders,
      todayPaidOrders,
    ] = await Promise.all([
      this.vpnService.getActiveSubscriptionCount(),
      this.ordersService.getAwaitingOrderCount(),
      this.ordersService.getReviewOrderCount(),
      this.ordersService.getTodayPaidOrderCount(),
    ]);

    return {
      activeAccounts: this.authService.getTotalAccounts(),
      activeSubscriptions,
      awaitingOrders,
      reviewOrders,
      pendingWithdrawals: this.withdrawalsService.getPendingWithdrawalCount(),
      todayPaidOrders,
    };
  }
}
