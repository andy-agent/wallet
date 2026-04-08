import {
  AuthAccount,
  AuthSession,
  VerificationCodeRecord,
} from '../auth/auth.types';
import { OrderStatus } from '../orders/orders.types';
import { PersistedSubscriptionRecord } from '../vpn/vpn.types';
import {
  RuntimeStateListOrdersParams,
  RuntimeStateListOrdersResult,
  StoredOrderRecord,
} from './runtime-state.types';

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

  onModuleDestroy?(): Promise<void>;
}
