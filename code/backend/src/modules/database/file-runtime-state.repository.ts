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
import { PersistedWalletLifecycleRecord } from '../wallet/wallet.types';
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
  version: 4,
  orders: [],
  idempotencyIndex: {},
  subscriptions: [],
  walletLifecycles: [],
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
      version: 4,
      orders: raw.orders ?? [],
      idempotencyIndex: raw.idempotencyIndex ?? {},
      subscriptions: raw.subscriptions ?? [],
      walletLifecycles: raw.walletLifecycles ?? [],
      accounts: raw.accounts ?? [],
      sessions: raw.sessions ?? [],
      verificationCodes: raw.verificationCodes ?? [],
      onchainReceipts: raw.onchainReceipts ?? [],
      paymentScanCursors: raw.paymentScanCursors ?? [],
    } satisfies RuntimeStateSnapshot;
  }
}
