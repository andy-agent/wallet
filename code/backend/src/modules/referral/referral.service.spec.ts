import { ConfigService } from '@nestjs/config';
import { AuthService } from '../auth/auth.service';
import { AuthAccount } from '../auth/auth.types';
import { PostgresDataAccessService } from '../database/postgres-data-access.service';
import { ReferralService } from './referral.service';

function createAccount(
  accountId: string,
  referralCode: string,
  inviterAccountId: string | null = null,
): AuthAccount {
  return {
    accountId,
    email: `${accountId}@example.com`,
    passwordHash: 'hash',
    status: 'ACTIVE',
    referralCode,
    inviterAccountId,
    createdAt: '2026-04-29T00:00:00.000Z',
    updatedAt: '2026-04-29T00:00:00.000Z',
  };
}

describe('ReferralService commission persistence', () => {
  function createService(initialAccounts: AuthAccount[]) {
    const accounts = new Map(
      initialAccounts.map((account) => [account.accountId, account]),
    );
    const authService = {
      getMe: jest.fn((accessToken: string) => accounts.get(accessToken)!),
      findAccountByReferralCode: jest.fn(
        (referralCode: string) =>
          Array.from(accounts.values()).find(
            (account) => account.referralCode === referralCode,
          ) ?? null,
      ),
      getAccountById: jest.fn(
        (accountId: string) => accounts.get(accountId) ?? null,
      ),
      maskEmail: jest.fn((accountId: string) => `${accountId}@masked`),
      countInvitees: jest.fn(
        (accountId: string, level: 'LEVEL1' | 'LEVEL2') => {
          return Array.from(accounts.values()).filter((account) => {
            if (level === 'LEVEL1') {
              return account.inviterAccountId === accountId;
            }
            const directInviter = account.inviterAccountId
              ? accounts.get(account.inviterAccountId)
              : null;
            return directInviter?.inviterAccountId === accountId;
          }).length;
        },
      ),
      setAccountInviter: jest.fn(
        async (accountId: string, inviterAccountId: string) => {
          const account = accounts.get(accountId)!;
          const updated = {
            ...account,
            inviterAccountId,
            updatedAt: '2026-04-29T01:00:00.000Z',
          };
          accounts.set(accountId, updated);
          return updated;
        },
      ),
    } as unknown as AuthService;
    const configService = {
      get: jest.fn(),
    } as unknown as ConfigService;
    const postgresDataAccessService = {
      isEnabled: jest.fn(() => true),
      upsertCommissionLedgerEntry: jest.fn(async (entry) => entry),
      listCommissionLedger: jest.fn(async () => ({
        items: [],
        page: 1,
        pageSize: 1000,
        total: 0,
      })),
      releaseMaturedCommissions: jest.fn(async () => 0),
    } as unknown as PostgresDataAccessService;

    return {
      accounts,
      authService,
      postgresDataAccessService,
      service: new ReferralService(
        authService,
        configService,
        postgresDataAccessService,
      ),
    };
  }

  it('persists inviter on bind so referral chains survive process restarts', async () => {
    const { authService, service } = createService([
      createAccount('root', 'ROOTCODE'),
      createAccount('inviter', 'INVITER', 'root'),
      createAccount('invitee', 'INVITEE'),
    ]);

    await service.bind('invitee', 'INVITER');

    expect(authService.setAccountInviter).toHaveBeenCalledWith(
      'invitee',
      'inviter',
    );
  });

  it('records completed orders from persisted inviter relationships', async () => {
    const { postgresDataAccessService, service } = createService([
      createAccount('root', 'ROOTCODE'),
      createAccount('inviter', 'INVITER', 'root'),
      createAccount('buyer', 'BUYER', 'inviter'),
    ]);

    await service.recordCompletedOrder({
      accountId: 'buyer',
      orderNo: 'ORD-1',
      sourceAssetCode: 'USDT',
      sourceAmount: '3.00000000',
    });

    expect(
      postgresDataAccessService.upsertCommissionLedgerEntry,
    ).toHaveBeenCalledTimes(2);
    expect(
      postgresDataAccessService.upsertCommissionLedgerEntry,
    ).toHaveBeenNthCalledWith(
      1,
      expect.objectContaining({
        beneficiaryAccountId: 'inviter',
        sourceOrderNo: 'ORD-1',
        sourceAccountId: 'buyer',
        commissionLevel: 'LEVEL1',
        settlementAmountUsdt: '0.75000000',
        status: 'FROZEN',
        withdrawRequestNo: null,
      }),
    );
    expect(
      postgresDataAccessService.upsertCommissionLedgerEntry,
    ).toHaveBeenNthCalledWith(
      2,
      expect.objectContaining({
        beneficiaryAccountId: 'root',
        sourceOrderNo: 'ORD-1',
        sourceAccountId: 'buyer',
        commissionLevel: 'LEVEL2',
        settlementAmountUsdt: '0.15000000',
        status: 'FROZEN',
        withdrawRequestNo: null,
      }),
    );
  });
});
