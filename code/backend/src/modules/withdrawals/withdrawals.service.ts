import {
  BadRequestException,
  ConflictException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { randomUUID } from 'crypto';
import { AuthService } from '../auth/auth.service';
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
  ) {}

  createWithdrawal(
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
      return this.mustGet(account.accountId, existingRequestNo);
    }

    const record: WithdrawalRecord = {
      requestNo: `WDR-${Date.now()}`,
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
    const current = this.withdrawalsByAccountId.get(account.accountId) ?? [];
    current.push(record);
    this.withdrawalsByAccountId.set(account.accountId, current);
    this.idempotencyIndex.set(compositeKey, record.requestNo);
    return record;
  }

  listWithdrawals(accessToken: string, status?: string) {
    const account = this.authService.getMe(accessToken);
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

  getWithdrawal(accessToken: string, requestNo: string) {
    const account = this.authService.getMe(accessToken);
    return this.mustGet(account.accountId, requestNo);
  }

  private mustGet(accountId: string, requestNo: string) {
    const items = this.withdrawalsByAccountId.get(accountId) ?? [];
    const record = items.find((item) => item.requestNo === requestNo);
    if (!record) {
      throw new NotFoundException({
        code: 'WITHDRAW_NOT_FOUND',
        message: 'Withdrawal not found',
      });
    }
    return record;
  }
}
