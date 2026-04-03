import { Injectable } from '@nestjs/common';
import { AuthService } from '../../auth/auth.service';
import { VpnService } from '../../vpn/vpn.service';

@Injectable()
export class AdminAccountsService {
  constructor(
    private readonly authService: AuthService,
    private readonly vpnService: VpnService,
  ) {}

  listAccounts(params: {
    page?: number;
    pageSize?: number;
    email?: string;
    status?: string;
  }) {
    const result = this.authService.listAccounts(params);

    // Enrich with subscription info
    const enrichedItems = result.items.map((account) => {
      const subscription = this.vpnService.getSubscriptionByAccountIdForAdmin(account.accountId);
      return {
        ...account,
        subscription,
      };
    });

    return {
      items: enrichedItems,
      page: result.page,
    };
  }

  getAccountDetail(accountId: string) {
    const account = this.authService.getAccountDetail(accountId);
    const subscription = this.vpnService.getSubscriptionByAccountIdForAdmin(accountId);

    return {
      ...account,
      subscription,
    };
  }
}
