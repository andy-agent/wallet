import {
  BadRequestException,
  ConflictException,
  Injectable,
} from '@nestjs/common';
import { randomUUID } from 'crypto';
import { ConfigService } from '@nestjs/config';
import { AuthService } from '../auth/auth.service';
import { ReferralBindingRecord, CommissionLedgerRecord } from './referral.types';

@Injectable()
export class ReferralService {
  private readonly bindings = new Map<string, ReferralBindingRecord>();
  private readonly ledgerByBeneficiary = new Map<string, CommissionLedgerRecord[]>();

  constructor(
    private readonly authService: AuthService,
    private readonly configService: ConfigService,
  ) {}

  getOverview(accessToken: string) {
    const account = this.authService.getMe(accessToken);
    const binding = this.bindings.get(account.accountId);
    const ledger = this.ledgerByBeneficiary.get(account.accountId) ?? [];
    const level1Income = ledger
      .filter((item) => item.commissionLevel === 'LEVEL1')
      .reduce((sum, item) => sum + Number(item.settlementAmountUsdt), 0);
    const level2Income = ledger
      .filter((item) => item.commissionLevel === 'LEVEL2')
      .reduce((sum, item) => sum + Number(item.settlementAmountUsdt), 0);
    const available = ledger
      .filter((item) => item.status === 'AVAILABLE')
      .reduce((sum, item) => sum + Number(item.settlementAmountUsdt), 0);
    const frozen = ledger
      .filter((item) => item.status === 'FROZEN')
      .reduce((sum, item) => sum + Number(item.settlementAmountUsdt), 0);

    return {
      accountId: account.accountId,
      referralCode: account.referralCode,
      hasBinding: Boolean(binding),
      level1InviteCount: this.countInvitees(account.accountId, 'LEVEL1'),
      level2InviteCount: this.countInvitees(account.accountId, 'LEVEL2'),
      level1IncomeUsdt: level1Income.toFixed(2),
      level2IncomeUsdt: level2Income.toFixed(2),
      availableAmountUsdt: available.toFixed(2),
      frozenAmountUsdt: frozen.toFixed(2),
      minWithdrawAmountUsdt: '10.00',
    };
  }

  getShareContext(accessToken: string) {
    const overview = this.getOverview(accessToken);
    const shareBaseUrl =
      this.configService.get<string>('REFERRAL_SHARE_BASE_URL')?.trim() ||
      'https://vpn.residential-agent.com/invite?code=';
    const shareLink = this.buildShareLink(shareBaseUrl, overview.referralCode);
    return {
      referralCode: overview.referralCode,
      shareLink,
      shareTitle: 'CryptoVPN 邀请链接',
      shareMessage: `使用我的邀请码 ${overview.referralCode} 注册 CryptoVPN：${shareLink}`,
      level1InviteCount: overview.level1InviteCount,
      level2InviteCount: overview.level2InviteCount,
      availableAmountUsdt: overview.availableAmountUsdt,
      frozenAmountUsdt: overview.frozenAmountUsdt,
      hasBinding: overview.hasBinding,
    };
  }

  bind(accessToken: string, referralCode: string) {
    const account = this.authService.getMe(accessToken);
    if (this.bindings.has(account.accountId)) {
      throw new ConflictException({
        code: 'REFERRAL_BINDING_LOCKED',
        message: 'Referral binding already exists',
      });
    }

    const inviter = this.authService.findAccountByReferralCode(referralCode);
    if (!inviter) {
      throw new BadRequestException({
        code: 'REFERRAL_CODE_INVALID',
        message: 'Referral code invalid',
      });
    }

    if (inviter.accountId === account.accountId) {
      throw new BadRequestException({
        code: 'REFERRAL_SELF_BIND_FORBIDDEN',
        message: 'Referral self bind forbidden',
      });
    }

    const inviterBinding = this.bindings.get(inviter.accountId);
    const binding: ReferralBindingRecord = {
      inviteeAccountId: account.accountId,
      inviterLevel1AccountId: inviter.accountId,
      inviterLevel2AccountId: inviterBinding?.inviterLevel1AccountId ?? null,
      codeUsed: referralCode,
      status: 'BOUND',
      boundAt: new Date().toISOString(),
      lockedAt: null,
    };

    this.bindings.set(account.accountId, binding);
    return {};
  }

  getSummary(accessToken: string) {
    const account = this.authService.getMe(accessToken);
    const ledger = this.ledgerByBeneficiary.get(account.accountId) ?? [];
    const amounts = this.sumAmounts(ledger);

    return {
      settlementAssetCode: 'USDT',
      settlementNetworkCode: 'SOLANA',
      availableAmount: amounts.available.toFixed(2),
      frozenAmount: amounts.frozen.toFixed(2),
      withdrawingAmount: amounts.withdrawing.toFixed(2),
      withdrawnTotal: amounts.withdrawn.toFixed(2),
    };
  }

  getLedger(accessToken: string, status?: string) {
    const account = this.authService.getMe(accessToken);
    let items = this.ledgerByBeneficiary.get(account.accountId) ?? [];
    if (status) {
      items = items.filter((item) => item.status === status);
    }
    return {
      items: items.map((item) => ({
        entryNo: item.entryNo,
        sourceOrderNo: item.sourceOrderNo,
        sourceAccountMasked: this.authService.maskEmail(item.sourceAccountId),
        commissionLevel: item.commissionLevel,
        sourceAssetCode: item.sourceAssetCode,
        sourceAmount: item.sourceAmount,
        fxRateSnapshot: item.fxRateSnapshot,
        settlementAmountUsdt: item.settlementAmountUsdt,
        status: item.status,
        createdAt: item.createdAt,
        availableAt: item.availableAt,
      })),
      page: {
        page: 1,
        pageSize: items.length || 20,
        total: items.length,
      },
    };
  }

  recordCompletedOrder(input: {
    accountId: string;
    orderNo: string;
    sourceAssetCode: 'SOL' | 'USDT';
    sourceAmount: string;
  }) {
    const binding = this.bindings.get(input.accountId);
    if (!binding) {
      return;
    }

    if (binding.inviterLevel1AccountId) {
      this.addLedgerEntry(
        binding.inviterLevel1AccountId,
        input,
        'LEVEL1',
        0.25,
      );
    }
    if (binding.inviterLevel2AccountId) {
      this.addLedgerEntry(
        binding.inviterLevel2AccountId,
        input,
        'LEVEL2',
        0.05,
      );
    }

    binding.status = 'LOCKED';
    binding.lockedAt = new Date().toISOString();
  }

  lockAvailableForWithdrawal(accountId: string, amount: number) {
    const items = this.ledgerByBeneficiary.get(accountId) ?? [];
    let remaining = amount;
    for (const item of items) {
      if (item.status !== 'AVAILABLE') {
        continue;
      }
      item.status = 'LOCKED_WITHDRAWAL';
      remaining -= Number(item.settlementAmountUsdt);
      if (remaining <= 0) {
        break;
      }
    }
  }

  unlockWithdrawal(accountId: string) {
    const items = this.ledgerByBeneficiary.get(accountId) ?? [];
    for (const item of items) {
      if (item.status === 'LOCKED_WITHDRAWAL') {
        item.status = 'AVAILABLE';
      }
    }
  }

  getBalances(accountId: string) {
    return this.sumAmounts(this.ledgerByBeneficiary.get(accountId) ?? []);
  }

  private addLedgerEntry(
    beneficiaryAccountId: string,
    input: { accountId: string; orderNo: string; sourceAssetCode: 'SOL' | 'USDT'; sourceAmount: string },
    level: 'LEVEL1' | 'LEVEL2',
    rate: number,
  ) {
    const existing = this.ledgerByBeneficiary.get(beneficiaryAccountId) ?? [];
    const amount = Number(input.sourceAmount) * rate;
    existing.push({
      entryNo: `LEDGER-${randomUUID()}`,
      beneficiaryAccountId,
      sourceOrderNo: input.orderNo,
      sourceAccountId: input.accountId,
      commissionLevel: level,
      sourceAssetCode: input.sourceAssetCode,
      sourceAmount: input.sourceAmount,
      fxRateSnapshot: '1.00',
      settlementAmountUsdt: amount.toFixed(2),
      status: 'FROZEN',
      createdAt: new Date().toISOString(),
      availableAt: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString(),
    });
    this.ledgerByBeneficiary.set(beneficiaryAccountId, existing);
  }

  private countInvitees(accountId: string, level: 'LEVEL1' | 'LEVEL2') {
    let count = 0;
    for (const binding of this.bindings.values()) {
      if (
        (level === 'LEVEL1' && binding.inviterLevel1AccountId === accountId) ||
        (level === 'LEVEL2' && binding.inviterLevel2AccountId === accountId)
      ) {
        count += 1;
      }
    }
    return count;
  }

  private sumAmounts(items: CommissionLedgerRecord[]) {
    return items.reduce(
      (acc, item) => {
        const amount = Number(item.settlementAmountUsdt);
        if (item.status === 'AVAILABLE') acc.available += amount;
        if (item.status === 'FROZEN') acc.frozen += amount;
        if (item.status === 'LOCKED_WITHDRAWAL') acc.withdrawing += amount;
        if (item.status === 'WITHDRAWN') acc.withdrawn += amount;
        return acc;
      },
      { available: 0, frozen: 0, withdrawing: 0, withdrawn: 0 },
    );
  }

  private buildShareLink(base: string, referralCode: string) {
    if (base.includes('{code}')) {
      return base.replace('{code}', encodeURIComponent(referralCode));
    }
    if (base.endsWith('=')) {
      return `${base}${encodeURIComponent(referralCode)}`;
    }
    if (base.includes('?')) {
      return `${base}&code=${encodeURIComponent(referralCode)}`;
    }
    return `${base.replace(/\/$/, '')}/${encodeURIComponent(referralCode)}`;
  }
}
