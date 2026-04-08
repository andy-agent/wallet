import { OrderStatus } from '../orders/orders.types';
import { PersistedSubscriptionRecord } from '../vpn/vpn.types';
import {
  RuntimeStateListOrdersParams,
  RuntimeStateListOrdersResult,
  StoredOrderRecord,
} from './runtime-state.types';

export abstract class RuntimeStateRepository {
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
