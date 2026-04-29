import {
  BadRequestException,
  ConflictException,
  Injectable,
} from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { randomUUID } from 'crypto';
import { AuthService } from '../auth/auth.service';
import { PostgresDataAccessService } from '../database/postgres-data-access.service';
import {
  CommissionLedgerRecord,
  CommissionSummaryRecord,
  ReferralBindingRecord,
} from './referral.types';

const LEVEL1_COMMISSION_RATE = 0.25;
const LEVEL2_COMMISSION_RATE = 0.05;
const DEFAULT_COMMISSION_FREEZE_MS = 20 * 60 * 1000;

@Injectable()
export class ReferralService {
  private readonly bindings = new Map<string, ReferralBindingRecord>();
  private readonly ledgerByBeneficiary = new Map<
    string,
    CommissionLedgerRecord[]
  >();

  constructor(
    private readonly authService: AuthService,
    private readonly configService: ConfigService,
    private readonly postgresDataAccessService?: PostgresDataAccessService,
  ) {}

  async getOverview(accessToken: string) {
    const account = this.authService.getMe(accessToken);
    const binding = this.resolveBinding(account.accountId);
    const ledger = await this.getLedgerForAccount(account.accountId);
    const level1Income = ledger
      .filter((item) => item.commissionLevel === 'LEVEL1')
      .reduce((sum, item) => sum + Number(item.settlementAmountUsdt), 0);
    const level2Income = ledger
      .filter((item) => item.commissionLevel === 'LEVEL2')
      .reduce((sum, item) => sum + Number(item.settlementAmountUsdt), 0);
    const amounts = this.sumAmounts(ledger);

    return {
      accountId: account.accountId,
      referralCode: account.referralCode,
      hasBinding: Boolean(binding),
      level1InviteCount: this.countInvitees(account.accountId, 'LEVEL1'),
      level2InviteCount: this.countInvitees(account.accountId, 'LEVEL2'),
      level1IncomeUsdt: level1Income.toFixed(2),
      level2IncomeUsdt: level2Income.toFixed(2),
      availableAmountUsdt: amounts.available.toFixed(2),
      frozenAmountUsdt: amounts.frozen.toFixed(2),
      minWithdrawAmountUsdt: '10.00',
    };
  }

  async getShareContext(accessToken: string) {
    const overview = await this.getOverview(accessToken);
    const shareBaseUrl =
      this.configService.get<string>('REFERRAL_SHARE_BASE_URL')?.trim() ||
      'https://api.residential-agent.com/invite?code=';
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

  resolvePublic(referralCode?: string) {
    const normalizedCode = referralCode?.trim().toUpperCase() ?? '';
    if (!normalizedCode) {
      throw new BadRequestException({
        code: 'REFERRAL_CODE_REQUIRED',
        message: 'Referral code is required',
      });
    }

    const inviter = this.authService.findAccountByReferralCode(normalizedCode);
    if (!inviter) {
      throw new BadRequestException({
        code: 'REFERRAL_CODE_INVALID',
        message: 'Referral code invalid',
      });
    }

    return {
      referralCode: normalizedCode,
      inviterLabel: this.authService.maskEmail(inviter.accountId),
      shareTitle: 'CryptoVPN 邀请',
      headline: '接受邀请，下载并打开 CryptoVPN',
      description: '安装 App 后使用当前邀请码注册或登录，即可建立推广关系。',
      downloadUrl:
        this.configService.get<string>('APP_DOWNLOAD_URL')?.trim() || null,
      openAppUrl:
        this.configService.get<string>('APP_OPEN_URL')?.trim() || null,
    };
  }

  async bind(accessToken: string, referralCode: string) {
    const account = this.authService.getMe(accessToken);
    const normalizedCode = referralCode.trim().toUpperCase();
    if (this.resolveBinding(account.accountId)) {
      throw new ConflictException({
        code: 'REFERRAL_BINDING_LOCKED',
        message: 'Referral binding already exists',
      });
    }

    const inviter = this.authService.findAccountByReferralCode(normalizedCode);
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

    const inviterBinding = this.resolveBinding(inviter.accountId);
    const binding: ReferralBindingRecord = {
      inviteeAccountId: account.accountId,
      inviterLevel1AccountId: inviter.accountId,
      inviterLevel2AccountId:
        inviterBinding?.inviterLevel1AccountId ??
        inviter.inviterAccountId ??
        null,
      codeUsed: normalizedCode,
      status: 'BOUND',
      boundAt: new Date().toISOString(),
      lockedAt: null,
    };

    await this.authService.setAccountInviter(
      account.accountId,
      inviter.accountId,
    );
    this.bindings.set(account.accountId, binding);
    return {};
  }

  async getSummary(accessToken: string) {
    const account = this.authService.getMe(accessToken);
    const amounts = await this.getBalances(account.accountId);

    return {
      settlementAssetCode: 'USDT',
      settlementNetworkCode: 'SOLANA',
      availableAmount: amounts.available.toFixed(2),
      frozenAmount: amounts.frozen.toFixed(2),
      withdrawingAmount: amounts.withdrawing.toFixed(2),
      withdrawnTotal: amounts.withdrawn.toFixed(2),
    };
  }

  async getLedger(accessToken: string, status?: string) {
    const account = this.authService.getMe(accessToken);
    const ledger = await this.getLedgerForAccount(account.accountId, status);
    return {
      items: ledger.map((item) => ({
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
        pageSize: ledger.length || 20,
        total: ledger.length,
      },
    };
  }

  async recordCompletedOrder(input: {
    accountId: string;
    orderNo: string;
    sourceAssetCode: string;
    sourceAmount: string;
  }) {
    const binding = this.resolveBinding(input.accountId);
    if (!binding) {
      return;
    }

    if (binding.inviterLevel1AccountId) {
      await this.addLedgerEntry(
        binding.inviterLevel1AccountId,
        input,
        'LEVEL1',
        LEVEL1_COMMISSION_RATE,
      );
    }
    if (binding.inviterLevel2AccountId) {
      await this.addLedgerEntry(
        binding.inviterLevel2AccountId,
        input,
        'LEVEL2',
        LEVEL2_COMMISSION_RATE,
      );
    }

    binding.status = 'LOCKED';
    binding.lockedAt = new Date().toISOString();
  }

  async lockAvailableForWithdrawal(
    accountId: string,
    amount: number,
    requestNo: string,
  ) {
    await this.releaseMaturedCommissions();
    if (this.postgresDataAccessService?.isEnabled()) {
      return this.postgresDataAccessService.lockAvailableCommissionEntries({
        accountId,
        amountUsdt: amount,
        requestNo,
      });
    }

    const items = this.ledgerByBeneficiary.get(accountId) ?? [];
    let remaining = amount;
    const locked: CommissionLedgerRecord[] = [];
    for (const item of items) {
      if (item.status !== 'AVAILABLE') {
        continue;
      }
      item.status = 'LOCKED_WITHDRAWAL';
      item.withdrawRequestNo = requestNo;
      locked.push(item);
      remaining -= Number(item.settlementAmountUsdt);
      if (remaining <= 0) {
        break;
      }
    }
    if (remaining > 0.00000001) {
      for (const item of locked) {
        item.status = 'AVAILABLE';
        item.withdrawRequestNo = null;
      }
      return [];
    }
    return locked;
  }

  async unlockWithdrawal(requestNo: string) {
    if (this.postgresDataAccessService?.isEnabled()) {
      await this.postgresDataAccessService.updateCommissionLedgerStatusForWithdrawal(
        {
          requestNo,
          status: 'AVAILABLE',
        },
      );
      return;
    }

    for (const items of this.ledgerByBeneficiary.values()) {
      for (const item of items) {
        if (
          item.status === 'LOCKED_WITHDRAWAL' &&
          item.withdrawRequestNo === requestNo
        ) {
          item.status = 'AVAILABLE';
          item.withdrawRequestNo = null;
        }
      }
    }
  }

  async markWithdrawalCompleted(requestNo: string) {
    if (this.postgresDataAccessService?.isEnabled()) {
      await this.postgresDataAccessService.updateCommissionLedgerStatusForWithdrawal(
        {
          requestNo,
          status: 'WITHDRAWN',
        },
      );
      return;
    }

    for (const items of this.ledgerByBeneficiary.values()) {
      for (const item of items) {
        if (
          item.status === 'LOCKED_WITHDRAWAL' &&
          item.withdrawRequestNo === requestNo
        ) {
          item.status = 'WITHDRAWN';
        }
      }
    }
  }

  async getBalances(accountId: string): Promise<CommissionSummaryRecord> {
    return this.sumAmounts(await this.getLedgerForAccount(accountId));
  }

  private async addLedgerEntry(
    beneficiaryAccountId: string,
    input: {
      accountId: string;
      orderNo: string;
      sourceAssetCode: string;
      sourceAmount: string;
    },
    level: 'LEVEL1' | 'LEVEL2',
    rate: number,
  ) {
    const now = new Date();
    const amount = Number(input.sourceAmount) * rate;
    const entry: CommissionLedgerRecord = {
      entryNo: `LEDGER-${randomUUID()}`,
      beneficiaryAccountId,
      sourceOrderNo: input.orderNo,
      sourceAccountId: input.accountId,
      commissionLevel: level,
      sourceAssetCode: input.sourceAssetCode,
      sourceAmount: input.sourceAmount,
      fxRateSnapshot: '1.00',
      settlementAmountUsdt: amount.toFixed(8),
      status: 'FROZEN',
      createdAt: now.toISOString(),
      availableAt: new Date(
        now.getTime() + this.getCommissionFreezeMs(),
      ).toISOString(),
      withdrawRequestNo: null,
      updatedAt: now.toISOString(),
    };

    if (this.postgresDataAccessService?.isEnabled()) {
      await this.postgresDataAccessService.upsertCommissionLedgerEntry(entry);
      return;
    }

    const existing = this.ledgerByBeneficiary.get(beneficiaryAccountId) ?? [];
    if (
      existing.some(
        (item) =>
          item.sourceOrderNo === input.orderNo &&
          item.commissionLevel === level,
      )
    ) {
      return;
    }
    existing.push(entry);
    this.ledgerByBeneficiary.set(beneficiaryAccountId, existing);
  }

  private countInvitees(accountId: string, level: 'LEVEL1' | 'LEVEL2') {
    return this.authService.countInvitees(accountId, level);
  }

  private resolveBinding(accountId: string): ReferralBindingRecord | null {
    const binding = this.bindings.get(accountId);
    if (binding) {
      return binding;
    }
    const account = this.authService.getAccountById(accountId);
    if (!account?.inviterAccountId) {
      return null;
    }
    const inviter = this.authService.getAccountById(account.inviterAccountId);
    if (!inviter) {
      return null;
    }
    return {
      inviteeAccountId: account.accountId,
      inviterLevel1AccountId: inviter.accountId,
      inviterLevel2AccountId: inviter.inviterAccountId ?? null,
      codeUsed: inviter.referralCode,
      status: 'BOUND',
      boundAt: account.createdAt,
      lockedAt: null,
    };
  }

  private async getLedgerForAccount(accountId: string, status?: string) {
    await this.releaseMaturedCommissions();
    if (this.postgresDataAccessService?.isEnabled()) {
      const result = await this.postgresDataAccessService.listCommissionLedger({
        beneficiaryAccountId: accountId,
        status,
        page: 1,
        pageSize: 1000,
      });
      return result.items;
    }

    let items = this.ledgerByBeneficiary.get(accountId) ?? [];
    if (status) {
      items = items.filter((item) => item.status === status);
    }
    return items;
  }

  private async releaseMaturedCommissions() {
    const now = new Date().toISOString();
    if (this.postgresDataAccessService?.isEnabled()) {
      await this.postgresDataAccessService.releaseMaturedCommissions(now);
      return;
    }

    for (const items of this.ledgerByBeneficiary.values()) {
      for (const item of items) {
        if (
          item.status === 'FROZEN' &&
          item.availableAt &&
          new Date(item.availableAt).getTime() <= Date.now()
        ) {
          item.status = 'AVAILABLE';
          item.updatedAt = now;
        }
      }
    }
  }

  private sumAmounts(items: CommissionLedgerRecord[]): CommissionSummaryRecord {
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

  private getCommissionFreezeMs() {
    const configured = this.configService
      .get<string>('COMMISSION_FREEZE_MS')
      ?.trim();
    const parsed = configured ? Number(configured) : Number.NaN;
    return Number.isFinite(parsed) && parsed >= 0
      ? parsed
      : DEFAULT_COMMISSION_FREEZE_MS;
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
