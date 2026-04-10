import {
  BadRequestException,
  ConflictException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { randomUUID } from 'crypto';
import { AuthService } from '../auth/auth.service';
import { PostgresDataAccessService } from '../database/postgres-data-access.service';
import { ReferralService } from '../referral/referral.service';
import { CreateWithdrawalRequestDto } from './dto/create-withdrawal.request';

export interface WithdrawalRecord {
  requestNo: string;
  accountId: string;
  amount: string;
  assetCode: 'USDT';
  networkCode: 'SOLANA';
  payoutAddress: string;
  status:
    | 'SUBMITTED'
    | 'UNDER_REVIEW'
    | 'APPROVED'
    | 'REJECTED'
    | 'BROADCASTING'
    | 'CHAIN_CONFIRMING'
    | 'COMPLETED'
    | 'FAILED'
    | 'CANCELED';
  failReason: string | null;
  txHash: string | null;
  createdAt: string;
  reviewedAt: string | null;
  completedAt: string | null;
}

@Injectable()
export class WithdrawalsService {
  private readonly withdrawalsByAccountId = new Map<string, WithdrawalRecord[]>();
  private readonly idempotencyIndex = new Map<string, string>();

  constructor(
    private readonly authService: AuthService,
    private readonly referralService: ReferralService,
    private readonly postgresDataAccessService: PostgresDataAccessService,
  ) {}

  async createWithdrawal(
    accessToken: string,
    dto: CreateWithdrawalRequestDto,
    idempotencyKey: string,
  ) {
    const account = this.authService.getMe(accessToken);
    const balances = this.referralService.getBalances(account.accountId);
    const amount = Number(dto.amount);

    if (amount < 10) {
      throw new BadRequestException({
        code: 'WITHDRAW_MIN_AMOUNT_NOT_MET',
        message: 'Minimum withdraw amount not met',
      });
    }

    if (balances.available < amount) {
      throw new ConflictException({
        code: 'WITHDRAW_INSUFFICIENT_AVAILABLE_BALANCE',
        message: 'Insufficient available balance',
      });
    }

    if (!dto.payoutAddress || dto.payoutAddress.length < 10) {
      throw new BadRequestException({
        code: 'WITHDRAW_ADDRESS_INVALID',
        message: 'Invalid payout address',
      });
    }

    const compositeKey = `${account.accountId}:${idempotencyKey}`;
    const existingRequestNo = this.idempotencyIndex.get(compositeKey);
    if (existingRequestNo) {
      return this.mustGet(account.accountId, existingRequestNo, account.email);
    }

    const requestNo = `WDR-${Date.now().toString(36).toUpperCase()}-${randomUUID().slice(0, 6).toUpperCase()}`;
    const record: WithdrawalRecord = {
      requestNo,
      accountId: account.accountId,
      amount: dto.amount,
      assetCode: 'USDT',
      networkCode: 'SOLANA',
      payoutAddress: dto.payoutAddress,
      status: 'SUBMITTED',
      failReason: null,
      txHash: null,
      createdAt: new Date().toISOString(),
      reviewedAt: null,
      completedAt: null,
    };

    this.referralService.lockAvailableForWithdrawal(account.accountId, amount);
    this.idempotencyIndex.set(compositeKey, record.requestNo);

    if (this.postgresDataAccessService.isEnabled()) {
      const persisted = await this.postgresDataAccessService.createWithdrawalRequest({
        requestNo: record.requestNo,
        accountId: record.accountId,
        amount: record.amount,
        assetCode: record.assetCode,
        networkCode: record.networkCode,
        payoutAddress: record.payoutAddress,
      });
      if (persisted) {
        return persisted;
      }
    }

    const current = this.withdrawalsByAccountId.get(account.accountId) ?? [];
    current.push(record);
    this.withdrawalsByAccountId.set(account.accountId, current);
    return record;
  }

  async listWithdrawals(accessToken: string, status?: string) {
    const account = this.authService.getMe(accessToken);
    if (this.postgresDataAccessService.isEnabled()) {
      const result = await this.postgresDataAccessService.listWithdrawRequests({
        page: 1,
        pageSize: 100,
        accountId: account.accountId,
        status,
      });
      return {
        items: result.items,
        page: {
          page: result.page,
          pageSize: result.pageSize,
          total: result.total,
        },
      };
    }

    let items = this.withdrawalsByAccountId.get(account.accountId) ?? [];
    if (status) {
      items = items.filter((item) => item.status === status);
    }
    return {
      items,
      page: {
        page: 1,
        pageSize: items.length || 20,
        total: items.length,
      },
    };
  }

  async getWithdrawal(accessToken: string, requestNo: string) {
    const account = this.authService.getMe(accessToken);
    if (this.postgresDataAccessService.isEnabled()) {
      const item = await this.postgresDataAccessService.findWithdrawalByAccountAndRequestNo(
        account.accountId,
        requestNo,
      );
      if (!item) {
        throw new NotFoundException({
          code: 'WITHDRAW_NOT_FOUND',
          message: 'Withdrawal not found',
        });
      }
      return item;
    }
    return this.mustGet(account.accountId, requestNo, account.email);
  }

  async getPendingWithdrawalCount() {
    if (this.postgresDataAccessService.isEnabled()) {
      return this.postgresDataAccessService.countPendingWithdrawalRequests();
    }

    let count = 0;
    for (const items of this.withdrawalsByAccountId.values()) {
      for (const item of items) {
        if (item.status === 'SUBMITTED' || item.status === 'UNDER_REVIEW') {
          count++;
        }
      }
    }
    return count;
  }

  async listWithdrawalsForAdmin(params: {
    page?: number;
    pageSize?: number;
    status?: string;
    accountEmail?: string;
  }) {
    if (this.postgresDataAccessService.isEnabled()) {
      return this.postgresDataAccessService.listWithdrawRequests(params);
    }

    const page = Math.max(1, params.page ?? 1);
    const pageSize = Math.min(100, Math.max(1, params.pageSize ?? 20));
    let items = Array.from(this.withdrawalsByAccountId.values()).flatMap((value) => value);
    if (params.status) {
      items = items.filter((item) => item.status === params.status);
    }
    if (params.accountEmail) {
      const loweredEmail = params.accountEmail.toLowerCase();
      items = items.filter((item) => {
        const email = this.authService.getAccountById(item.accountId)?.email ?? '';
        return email.toLowerCase().includes(loweredEmail);
      });
    }
    const total = items.length;
    const start = (page - 1) * pageSize;
    const end = start + pageSize;
    return {
      items: items.slice(start, end).map((item) => ({
        ...item,
        accountEmail: this.authService.getAccountById(item.accountId)?.email ?? null,
      })),
      page,
      pageSize,
      total,
    };
  }

  private mustGet(accountId: string, requestNo: string, accountEmail?: string) {
    const items = this.withdrawalsByAccountId.get(accountId) ?? [];
    const record = items.find((item) => item.requestNo === requestNo);
    if (!record) {
      throw new NotFoundException({
        code: 'WITHDRAW_NOT_FOUND',
        message: 'Withdrawal not found',
      });
    }
    return {
      ...record,
      accountEmail: accountEmail ?? this.authService.getAccountById(accountId)?.email ?? null,
    };
  }
}
