import {
  existsSync,
  mkdirSync,
  readFileSync,
  renameSync,
  writeFileSync,
} from 'fs';
import { dirname } from 'path';
import {
  AuthAccount,
  AuthSession,
  VerificationCodeRecord,
} from '../auth/auth.types';
import { OrderStatus } from '../orders/orders.types';
import {
  PersistedWalletChainAccountRecord,
  PersistedWalletKeySlotRecord,
  PersistedWalletLifecycleRecord,
  PersistedWalletPublicAddressRecord,
  PersistedWalletSecretBackupRecord,
  PersistedWalletSecretBackupV2Record,
  PersistedWalletRecord,
} from '../wallet/wallet.types';
import { PersistedSubscriptionRecord } from '../vpn/vpn.types';
import { RuntimeStateRepository } from './runtime-state.repository';
import {
  PaymentScanCursorRecord,
  RuntimeStatePaymentContext,
  RuntimeStateListOrdersParams,
  RuntimeStateListOrdersResult,
  RuntimeStateSnapshot,
  StoredOnchainReceiptRecord,
  StoredOrderRecord,
} from './runtime-state.types';

const EMPTY_RUNTIME_STATE: RuntimeStateSnapshot = {
  version: 6,
  orders: [],
  idempotencyIndex: {},
  subscriptions: [],
  walletLifecycles: [],
  walletPublicAddresses: [],
  walletSecretBackups: [],
  wallets: [],
  walletKeySlots: [],
  walletChainAccounts: [],
  walletSecretBackupsV2: [],
  accounts: [],
  sessions: [],
  verificationCodes: [],
  onchainReceipts: [],
  paymentScanCursors: [],
};

export class FileRuntimeStateRepository extends RuntimeStateRepository {
  constructor(private readonly stateFilePath: string) {
    super();
    this.ensureStateFile();
  }

  async listAccounts(): Promise<AuthAccount[]> {
    return [...this.readSnapshot().accounts];
  }

  async saveAccount(account: AuthAccount): Promise<AuthAccount> {
    const snapshot = this.readSnapshot();
    const nextAccounts = snapshot.accounts.filter(
      (item) =>
        item.accountId !== account.accountId &&
        item.email.toLowerCase() !== account.email.toLowerCase(),
    );

    nextAccounts.push(account);
    snapshot.accounts = nextAccounts;
    this.writeSnapshot(snapshot);
    return account;
  }

  async listSessions(): Promise<AuthSession[]> {
    return [...this.readSnapshot().sessions];
  }

  async saveSession(session: AuthSession): Promise<AuthSession> {
    const snapshot = this.readSnapshot();
    const nextSessions = snapshot.sessions.filter(
      (item) => item.sessionId !== session.sessionId,
    );

    nextSessions.push(session);
    snapshot.sessions = nextSessions;
    this.writeSnapshot(snapshot);
    return session;
  }

  async listVerificationCodes(): Promise<VerificationCodeRecord[]> {
    return [...this.readSnapshot().verificationCodes];
  }

  async saveVerificationCode(
    record: VerificationCodeRecord,
  ): Promise<VerificationCodeRecord> {
    const snapshot = this.readSnapshot();
    const nextCodes = snapshot.verificationCodes.filter(
      (item) =>
        !(
          item.email.toLowerCase() === record.email.toLowerCase() &&
          item.purpose === record.purpose
        ),
    );

    nextCodes.push(record);
    snapshot.verificationCodes = nextCodes;
    this.writeSnapshot(snapshot);
    return record;
  }

  async createOrder(
    order: StoredOrderRecord,
    compositeIdempotencyKey: string,
  ): Promise<StoredOrderRecord> {
    const snapshot = this.readSnapshot();
    const existingOrderNo = snapshot.idempotencyIndex[compositeIdempotencyKey];

    if (existingOrderNo) {
      const existing = snapshot.orders.find(
        (item) => item.orderNo === existingOrderNo,
      );
      if (existing) {
        return existing;
      }
    }

    snapshot.orders.push(order);
    snapshot.idempotencyIndex[compositeIdempotencyKey] = order.orderNo;
    this.writeSnapshot(snapshot);
    return order;
  }

  async findOrderByNo(orderNo: string): Promise<StoredOrderRecord | null> {
    return (
      this.readSnapshot().orders.find((order) => order.orderNo === orderNo) ??
      null
    );
  }

  async saveOrder(order: StoredOrderRecord): Promise<StoredOrderRecord> {
    const snapshot = this.readSnapshot();
    const index = snapshot.orders.findIndex(
      (item) => item.orderNo === order.orderNo,
    );

    if (index === -1) {
      snapshot.orders.push(order);
    } else {
      snapshot.orders[index] = order;
    }

    this.writeSnapshot(snapshot);
    return order;
  }

  async listActiveOrdersForPaymentContext(params: {
    collectionAddress: string;
    quoteAssetCode: StoredOrderRecord['quoteAssetCode'];
    quoteNetworkCode: StoredOrderRecord['quoteNetworkCode'];
    statuses: OrderStatus[];
    activeAfter: number;
  }): Promise<StoredOrderRecord[]> {
    return this.readSnapshot().orders.filter((order) => {
      return (
        order.collectionAddress === params.collectionAddress &&
        order.quoteAssetCode === params.quoteAssetCode &&
        order.quoteNetworkCode === params.quoteNetworkCode &&
        params.statuses.includes(order.status) &&
        new Date(order.expiresAt).getTime() > params.activeAfter
      );
    });
  }

  async listActivePaymentContexts(params: {
    statuses: OrderStatus[];
    activeAfter: number;
  }): Promise<RuntimeStatePaymentContext[]> {
    const unique = new Map<string, RuntimeStatePaymentContext>();
    for (const order of this.readSnapshot().orders) {
      if (
        !params.statuses.includes(order.status) ||
        new Date(order.expiresAt).getTime() <= params.activeAfter
      ) {
        continue;
      }

      const key = [
        order.collectionAddress,
        order.quoteAssetCode,
        order.quoteNetworkCode,
      ].join(':');
      if (!unique.has(key)) {
        unique.set(key, {
          collectionAddress: order.collectionAddress,
          quoteAssetCode: order.quoteAssetCode,
          quoteNetworkCode: order.quoteNetworkCode,
        });
      }
    }

    return [...unique.values()];
  }

  async findPaymentScanCursor(
    context: RuntimeStatePaymentContext,
  ): Promise<PaymentScanCursorRecord | null> {
    return (
      this.readSnapshot().paymentScanCursors.find(
        (cursor) =>
          cursor.collectionAddress === context.collectionAddress &&
          cursor.quoteAssetCode === context.quoteAssetCode &&
          cursor.quoteNetworkCode === context.quoteNetworkCode,
      ) ?? null
    );
  }

  async savePaymentScanCursor(
    cursor: PaymentScanCursorRecord,
  ): Promise<PaymentScanCursorRecord> {
    const snapshot = this.readSnapshot();
    const next = snapshot.paymentScanCursors.filter(
      (item) => item.cursorKey !== cursor.cursorKey,
    );
    next.push(cursor);
    snapshot.paymentScanCursors = next;
    this.writeSnapshot(snapshot);
    return cursor;
  }

  async upsertOnchainReceipt(
    receipt: StoredOnchainReceiptRecord,
  ): Promise<StoredOnchainReceiptRecord> {
    const snapshot = this.readSnapshot();
    const next = snapshot.onchainReceipts.filter(
      (item) => item.receiptId !== receipt.receiptId,
    );
    next.push(receipt);
    snapshot.onchainReceipts = next;
    this.writeSnapshot(snapshot);
    return receipt;
  }

  async listOrders(
    params: RuntimeStateListOrdersParams,
  ): Promise<RuntimeStateListOrdersResult> {
    const page = Math.max(1, params.page ?? 1);
    const pageSize = Math.min(100, Math.max(1, params.pageSize ?? 20));

    let items = [...this.readSnapshot().orders];

    if (params.orderNo) {
      const keyword = params.orderNo.toLowerCase();
      items = items.filter((order) =>
        order.orderNo.toLowerCase().includes(keyword),
      );
    }

    if (params.status) {
      items = items.filter((order) => order.status === params.status);
    }

    if (params.accountId) {
      items = items.filter((order) => order.accountId === params.accountId);
    }

    items.sort((left, right) => right.createdAt.localeCompare(left.createdAt));

    const total = items.length;
    const start = (page - 1) * pageSize;
    const end = start + pageSize;

    return {
      items: items.slice(start, end),
      page: {
        page,
        pageSize,
        total,
      },
    };
  }

  async countOrdersByStatus(
    statuses: OrderStatus[],
    confirmedSince?: number,
  ): Promise<number> {
    return this.readSnapshot().orders.filter((order) => {
      if (!statuses.includes(order.status)) {
        return false;
      }

      if (typeof confirmedSince !== 'number') {
        return true;
      }

      return (
        !!order.confirmedAt &&
        new Date(order.confirmedAt).getTime() >= confirmedSince
      );
    }).length;
  }

  async findCurrentSubscriptionByAccountId(
    accountId: string,
  ): Promise<PersistedSubscriptionRecord | null> {
    return (
      this.readSnapshot().subscriptions
        .filter((subscription) => subscription.accountId === accountId)
        .sort((left, right) => right.updatedAt.localeCompare(left.updatedAt))[0] ??
      null
    );
  }

  async upsertSubscription(
    subscription: PersistedSubscriptionRecord,
  ): Promise<PersistedSubscriptionRecord> {
    const snapshot = this.readSnapshot();
    const nextSubscriptions = snapshot.subscriptions.filter(
      (item) =>
        item.accountId !== subscription.accountId &&
        item.orderNo !== subscription.orderNo,
    );

    nextSubscriptions.push(subscription);
    snapshot.subscriptions = nextSubscriptions;
    this.writeSnapshot(snapshot);
    return subscription;
  }

  async countActiveSubscriptions(): Promise<number> {
    return this.readSnapshot().subscriptions.filter(
      (subscription) => subscription.status === 'ACTIVE',
    ).length;
  }

  async findWalletLifecycleByAccountId(
    accountId: string,
  ): Promise<PersistedWalletLifecycleRecord | null> {
    return (
      this.readSnapshot().walletLifecycles
        .filter((item) => item.accountId === accountId)
        .sort((left, right) => right.updatedAt.localeCompare(left.updatedAt))[0] ??
      null
    );
  }

  async upsertWalletLifecycle(
    record: PersistedWalletLifecycleRecord,
  ): Promise<PersistedWalletLifecycleRecord> {
    const snapshot = this.readSnapshot();
    const nextWalletLifecycles = snapshot.walletLifecycles.filter(
      (item) => item.accountId !== record.accountId,
    );

    nextWalletLifecycles.push(record);
    snapshot.walletLifecycles = nextWalletLifecycles;
    this.writeSnapshot(snapshot);
    return record;
  }

  async listWalletPublicAddressesByAccountId(params: {
    accountId: string;
    networkCode?: PersistedWalletPublicAddressRecord['networkCode'];
    assetCode?: PersistedWalletPublicAddressRecord['assetCode'];
  }): Promise<PersistedWalletPublicAddressRecord[]> {
    let items = this.readSnapshot().walletPublicAddresses.filter(
      (item) => item.accountId === params.accountId,
    );

    if (params.networkCode) {
      items = items.filter((item) => item.networkCode === params.networkCode);
    }

    if (params.assetCode) {
      items = items.filter((item) => item.assetCode === params.assetCode);
    }

    return items
      .slice()
      .sort((left, right) => {
        const defaultDelta = Number(right.isDefault) - Number(left.isDefault);
        if (defaultDelta !== 0) {
          return defaultDelta;
        }
        return left.createdAt.localeCompare(right.createdAt);
      });
  }

  async countWalletPublicAddressesByAccountId(accountId: string): Promise<number> {
    return this.readSnapshot().walletPublicAddresses.filter(
      (item) => item.accountId === accountId,
    ).length;
  }

  async upsertWalletPublicAddress(
    record: PersistedWalletPublicAddressRecord,
  ): Promise<PersistedWalletPublicAddressRecord> {
    const snapshot = this.readSnapshot();
    const nextAddresses = snapshot.walletPublicAddresses.filter(
      (item) => item.addressId !== record.addressId,
    );

    nextAddresses.push(record);
    snapshot.walletPublicAddresses = nextAddresses;
    this.writeSnapshot(snapshot);
    return record;
  }

  async findWalletSecretBackupByAccountId(
    accountId: string,
  ): Promise<PersistedWalletSecretBackupRecord | null> {
    return (
      this.readSnapshot().walletSecretBackups
        .filter((item) => item.accountId === accountId)
        .sort((left, right) => right.updatedAt.localeCompare(left.updatedAt))[0] ??
      null
    );
  }

  async upsertWalletSecretBackup(
    record: PersistedWalletSecretBackupRecord,
  ): Promise<PersistedWalletSecretBackupRecord> {
    const snapshot = this.readSnapshot();
    const nextBackups = snapshot.walletSecretBackups.filter(
      (item) => item.accountId !== record.accountId,
    );

    nextBackups.push(record);
    snapshot.walletSecretBackups = nextBackups;
    this.writeSnapshot(snapshot);
    return record;
  }

  async listWalletsByAccountId(
    accountId: string,
  ): Promise<PersistedWalletRecord[]> {
    return this.readSnapshot().wallets
      .filter((item) => item.accountId === accountId)
      .slice()
      .sort((left, right) => {
        const defaultDelta = Number(right.isDefault) - Number(left.isDefault);
        if (defaultDelta != 0) {
          return defaultDelta;
        }
        return left.createdAt.localeCompare(right.createdAt);
      });
  }

  async findWalletById(walletId: string): Promise<PersistedWalletRecord | null> {
    return this.readSnapshot().wallets.find((item) => item.walletId === walletId) ?? null;
  }

  async insertWallet(wallet: PersistedWalletRecord): Promise<PersistedWalletRecord> {
    const snapshot = this.readSnapshot();
    snapshot.wallets = snapshot.wallets.filter((item) => item.walletId !== wallet.walletId);
    snapshot.wallets.push(wallet);
    this.writeSnapshot(snapshot);
    return wallet;
  }

  async updateWallet(wallet: PersistedWalletRecord): Promise<PersistedWalletRecord> {
    return this.insertWallet(wallet);
  }

  async setDefaultWallet(
    accountId: string,
    walletId: string,
  ): Promise<PersistedWalletRecord> {
    const snapshot = this.readSnapshot();
    let updated: PersistedWalletRecord | null = null;
    snapshot.wallets = snapshot.wallets.map((item) => {
      if (item.accountId !== accountId) {
        return item;
      }
      const next = {
        ...item,
        isDefault: item.walletId === walletId,
        updatedAt: item.walletId === walletId ? new Date().toISOString() : item.updatedAt,
      };
      if (next.walletId === walletId) {
        updated = next;
      }
      return next;
    });
    if (!updated) {
      throw new Error(`Wallet ${walletId} not found for account ${accountId}`);
    }
    this.writeSnapshot(snapshot);
    return updated;
  }

  async listWalletKeySlotsByWalletId(
    walletId: string,
  ): Promise<PersistedWalletKeySlotRecord[]> {
    return this.readSnapshot().walletKeySlots
      .filter((item) => item.walletId === walletId)
      .slice()
      .sort((left, right) => left.slotCode.localeCompare(right.slotCode));
  }

  async insertWalletKeySlot(
    keySlot: PersistedWalletKeySlotRecord,
  ): Promise<PersistedWalletKeySlotRecord> {
    const snapshot = this.readSnapshot();
    snapshot.walletKeySlots = snapshot.walletKeySlots.filter(
      (item) => item.keySlotId !== keySlot.keySlotId,
    );
    snapshot.walletKeySlots.push(keySlot);
    this.writeSnapshot(snapshot);
    return keySlot;
  }

  async listWalletChainAccountsByWalletId(
    walletId: string,
  ): Promise<PersistedWalletChainAccountRecord[]> {
    return this.readSnapshot().walletChainAccounts
      .filter((item) => item.walletId === walletId)
      .slice()
      .sort((left, right) => left.networkCode.localeCompare(right.networkCode));
  }

  async findWalletChainAccountById(
    chainAccountId: string,
  ): Promise<PersistedWalletChainAccountRecord | null> {
    return this.readSnapshot().walletChainAccounts.find(
      (item) => item.chainAccountId === chainAccountId,
    ) ?? null;
  }

  async insertWalletChainAccount(
    chainAccount: PersistedWalletChainAccountRecord,
  ): Promise<PersistedWalletChainAccountRecord> {
    const snapshot = this.readSnapshot();
    snapshot.walletChainAccounts = snapshot.walletChainAccounts.filter(
      (item) => item.chainAccountId !== chainAccount.chainAccountId,
    );
    snapshot.walletChainAccounts.push(chainAccount);
    this.writeSnapshot(snapshot);
    return chainAccount;
  }

  async findWalletSecretBackupByWalletId(
    walletId: string,
  ): Promise<PersistedWalletSecretBackupV2Record | null> {
    return this.readSnapshot().walletSecretBackupsV2
      .filter((item) => item.walletId === walletId)
      .sort((left, right) => right.updatedAt.localeCompare(left.updatedAt))[0] ?? null;
  }

  async upsertWalletSecretBackupV2(
    record: PersistedWalletSecretBackupV2Record,
  ): Promise<PersistedWalletSecretBackupV2Record> {
    const snapshot = this.readSnapshot();
    snapshot.walletSecretBackupsV2 = snapshot.walletSecretBackupsV2.filter(
      (item) => item.walletId !== record.walletId,
    );
    snapshot.walletSecretBackupsV2.push(record);
    this.writeSnapshot(snapshot);
    return record;
  }

  async clearWalletDomainByAccountId(accountId: string): Promise<void> {
    const snapshot = this.readSnapshot();
    const walletIds = new Set(
      snapshot.wallets
        .filter((item) => item.accountId === accountId)
        .map((item) => item.walletId),
    );
    const keySlotIds = new Set(
      snapshot.walletKeySlots
        .filter((item) => walletIds.has(item.walletId))
        .map((item) => item.keySlotId),
    );

    snapshot.walletLifecycles = snapshot.walletLifecycles.filter(
      (item) => item.accountId !== accountId,
    );
    snapshot.walletPublicAddresses = snapshot.walletPublicAddresses.filter(
      (item) => item.accountId !== accountId,
    );
    snapshot.wallets = snapshot.wallets.filter((item) => item.accountId !== accountId);
    snapshot.walletKeySlots = snapshot.walletKeySlots.filter(
      (item) => !keySlotIds.has(item.keySlotId),
    );
    snapshot.walletChainAccounts = snapshot.walletChainAccounts.filter(
      (item) => !walletIds.has(item.walletId),
    );

    this.writeSnapshot(snapshot);
  }

  private ensureStateFile() {
    const directory = dirname(this.stateFilePath);
    if (!existsSync(directory)) {
      mkdirSync(directory, { recursive: true });
    }

    if (!existsSync(this.stateFilePath)) {
      this.writeSnapshot(EMPTY_RUNTIME_STATE);
    }
  }

  private readSnapshot(): RuntimeStateSnapshot {
    this.ensureStateFile();
    const raw = readFileSync(this.stateFilePath, 'utf8').trim();
    if (!raw) {
      return { ...EMPTY_RUNTIME_STATE };
    }
    return this.normalizeSnapshot(JSON.parse(raw));
  }

  private writeSnapshot(snapshot: RuntimeStateSnapshot) {
    const directory = dirname(this.stateFilePath);
    if (!existsSync(directory)) {
      mkdirSync(directory, { recursive: true });
    }

    const tempPath = `${this.stateFilePath}.${process.pid}.tmp`;
    writeFileSync(tempPath, JSON.stringify(snapshot, null, 2), 'utf8');
    renameSync(tempPath, this.stateFilePath);
  }

  private normalizeSnapshot(raw: Partial<RuntimeStateSnapshot>) {
    return {
      version: 6,
      orders: raw.orders ?? [],
      idempotencyIndex: raw.idempotencyIndex ?? {},
      subscriptions: raw.subscriptions ?? [],
      walletLifecycles: raw.walletLifecycles ?? [],
      walletPublicAddresses: raw.walletPublicAddresses ?? [],
      walletSecretBackups: raw.walletSecretBackups ?? [],
      wallets: raw.wallets ?? [],
      walletKeySlots: raw.walletKeySlots ?? [],
      walletChainAccounts: raw.walletChainAccounts ?? [],
      walletSecretBackupsV2: raw.walletSecretBackupsV2 ?? [],
      accounts: raw.accounts ?? [],
      sessions: raw.sessions ?? [],
      verificationCodes: raw.verificationCodes ?? [],
      onchainReceipts: raw.onchainReceipts ?? [],
      paymentScanCursors: raw.paymentScanCursors ?? [],
    } satisfies RuntimeStateSnapshot;
  }
}
