import { Injectable } from '@nestjs/common';
import { AuthService } from '../../auth/auth.service';
import { OrdersService } from '../../orders/orders.service';

@Injectable()
export class AdminOrdersService {
  constructor(
    private readonly ordersService: OrdersService,
    private readonly authService: AuthService,
  ) {}

  async listOrders(params: {
    page?: number;
    pageSize?: number;
    orderNo?: string;
    status?: string;
    email?: string;
  }) {
    // Find accountId by email if provided
    let accountId: string | undefined;
    if (params.email) {
      // Search through all accounts to find matching email
      const accountsResult = this.authService.listAccounts({
        email: params.email,
        page: 1,
        pageSize: 100,
      });
      if (accountsResult.items.length > 0) {
        // Use the first matching account
        accountId = accountsResult.items[0].accountId;
      } else {
        // No matching accounts, return empty result
        return {
          items: [],
          page: params.page ?? 1,
          pageSize: params.pageSize ?? 20,
          total: 0,
        };
      }
    }

    const result = await this.ordersService.listOrders({
      page: params.page,
      pageSize: params.pageSize,
      orderNo: params.orderNo,
      status: params.status,
      accountId,
    });

    // Enrich with account email
    const enrichedItems = result.items.map((order) => {
      const email = this.authService.getAccountById(order.accountId)?.email ?? 'unknown';
      return {
        ...order,
        accountEmail: email,
      };
    });

    return {
      items: enrichedItems,
      page: result.page.page,
      pageSize: result.page.pageSize,
      total: result.page.total,
    };
  }

  async getOrderDetail(orderNo: string) {
    const order = await this.ordersService.getOrderByNo(orderNo);
    const email = this.authService.getAccountById(order.accountId)?.email ?? 'unknown';

    return {
      ...order,
      accountEmail: email,
    };
  }
}
