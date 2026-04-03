import { Injectable } from '@nestjs/common';
import { AuthService } from '../../auth/auth.service';
import { WithdrawalsService } from '../../withdrawals/withdrawals.service';

@Injectable()
export class AdminWithdrawalsService {
  constructor(
    private readonly withdrawalsService: WithdrawalsService,
    private readonly authService: AuthService,
  ) {}

  listWithdrawals(params: {
    page?: number;
    pageSize?: number;
    status?: string;
    accountEmail?: string;
  }) {
    // For now, return mock data since the withdrawals service uses in-memory storage
    // In a real implementation, we would query all withdrawals from the database
    const mockWithdrawals = [
      {
        requestNo: 'WDR-001',
        accountId: 'mock-account-1',
        accountEmail: 'user1@example.com',
        amount: '50.00',
        assetCode: 'USDT',
        networkCode: 'SOLANA',
        payoutAddress: '0x1234567890abcdef',
        status: 'SUBMITTED',
        failReason: null,
        txHash: null,
        createdAt: new Date().toISOString(),
        reviewedAt: null,
        completedAt: null,
      },
      {
        requestNo: 'WDR-002',
        accountId: 'mock-account-2',
        accountEmail: 'user2@example.com',
        amount: '100.00',
        assetCode: 'USDT',
        networkCode: 'SOLANA',
        payoutAddress: '0xabcdef1234567890',
        status: 'UNDER_REVIEW',
        failReason: null,
        txHash: null,
        createdAt: new Date(Date.now() - 86400000).toISOString(),
        reviewedAt: null,
        completedAt: null,
      },
    ];

    let items = [...mockWithdrawals];

    if (params.status) {
      items = items.filter((w) => w.status === params.status);
    }

    if (params.accountEmail) {
      items = items.filter((w) =>
        w.accountEmail.toLowerCase().includes(params.accountEmail!.toLowerCase()),
      );
    }

    const page = Math.max(1, params.page ?? 1);
    const pageSize = Math.min(100, Math.max(1, params.pageSize ?? 20));
    const total = items.length;
    const start = (page - 1) * pageSize;
    const end = start + pageSize;
    const paginatedItems = items.slice(start, end);

    return {
      items: paginatedItems,
      page: {
        page,
        pageSize,
        total,
      },
    };
  }
}
