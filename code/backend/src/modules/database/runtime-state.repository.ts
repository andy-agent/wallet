import {
  AuthAccount,
  AuthSession,
  VerificationCodeRecord,
} from '../auth/auth.types';
import { OrderStatus } from '../orders/orders.types';
import { PersistedSubscriptionRecord } from '../vpn/vpn.types';
import {
  PaymentScanCursorRecord,
  RuntimeStatePaymentContext,
  RuntimeStateListOrdersParams,
  RuntimeStateListOrdersResult,
  StoredOnchainReceiptRecord,
  StoredOrderRecord,
} from './runtime-state.types';
import {
  PersistedWalletLifecycleRecord,
  PersistedWalletPublicAddressRecord,
  PersistedWalletSecretBackupRecord,
} from '../wallet/wallet.types';

export abstract class RuntimeStateRepository {
  abstract listAccounts(): Promise<AuthAccount[]>;

  abstract saveAccount(account: AuthAccount): Promise<AuthAccount>;

  abstract listSessions(): Promise<AuthSession[]>;

  abstract saveSession(session: AuthSession): Promise<AuthSession>;

  abstract listVerificationCodes(): Promise<VerificationCodeRecord[]>;

  abstract saveVerificationCode(
    record: VerificationCodeRecord,
  ): Promise<VerificationCodeRecord>;

  abstract createOrder(
    order: StoredOrderRecord,
    compositeIdempotencyKey: string,
  ): Promise<StoredOrderRecord>;

  abstract findOrderByNo(orderNo: string): Promise<StoredOrderRecord | null>;

  abstract saveOrder(order: StoredOrderRecord): Promise<StoredOrderRecord>;

  abstract listActiveOrdersForPaymentContext(params: {
    collectionAddress: string;
    quoteAssetCode: StoredOrderRecord['quoteAssetCode'];
    quoteNetworkCode: StoredOrderRecord['quoteNetworkCode'];
    statuses: OrderStatus[];
    activeAfter: number;
  }): Promise<StoredOrderRecord[]>;

  abstract listActivePaymentContexts(params: {
    statuses: OrderStatus[];
    activeAfter: number;
  }): Promise<RuntimeStatePaymentContext[]>;

  abstract findPaymentScanCursor(
    context: RuntimeStatePaymentContext,
  ): Promise<PaymentScanCursorRecord | null>;

  abstract savePaymentScanCursor(
    cursor: PaymentScanCursorRecord,
  ): Promise<PaymentScanCursorRecord>;

  abstract upsertOnchainReceipt(
    receipt: StoredOnchainReceiptRecord,
  ): Promise<StoredOnchainReceiptRecord>;

  abstract listOrders(
    params: RuntimeStateListOrdersParams,
  ): Promise<RuntimeStateListOrdersResult>;

  abstract countOrdersByStatus(
    statuses: OrderStatus[],
    confirmedSince?: number,
  ): Promise<number>;

  abstract findCurrentSubscriptionByAccountId(
    accountId: string,
  ): Promise<PersistedSubscriptionRecord | null>;

  abstract upsertSubscription(
    subscription: PersistedSubscriptionRecord,
  ): Promise<PersistedSubscriptionRecord>;

  abstract countActiveSubscriptions(): Promise<number>;

  abstract findWalletLifecycleByAccountId(
    accountId: string,
  ): Promise<PersistedWalletLifecycleRecord | null>;

  abstract upsertWalletLifecycle(
    record: PersistedWalletLifecycleRecord,
  ): Promise<PersistedWalletLifecycleRecord>;

  abstract listWalletPublicAddressesByAccountId(params: {
    accountId: string;
    networkCode?: PersistedWalletPublicAddressRecord['networkCode'];
    assetCode?: PersistedWalletPublicAddressRecord['assetCode'];
  }): Promise<PersistedWalletPublicAddressRecord[]>;

  abstract countWalletPublicAddressesByAccountId(accountId: string): Promise<number>;

  abstract upsertWalletPublicAddress(
    record: PersistedWalletPublicAddressRecord,
  ): Promise<PersistedWalletPublicAddressRecord>;

  abstract findWalletSecretBackupByAccountId(
    accountId: string,
  ): Promise<PersistedWalletSecretBackupRecord | null>;

  abstract upsertWalletSecretBackup(
    record: PersistedWalletSecretBackupRecord,
  ): Promise<PersistedWalletSecretBackupRecord>;

  onModuleDestroy?(): Promise<void>;
}
