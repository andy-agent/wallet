import { OrderRecord } from '../orders/orders.types';
import { PersistedSubscriptionRecord } from '../vpn/vpn.types';

export interface StoredOrderRecord extends OrderRecord {
  createdAt: string;
  idempotencyKey: string;
  collectionAddress: string;
  uniqueAmountDelta: string;
}

export interface RuntimeStateSnapshot {
  version: 1;
  orders: StoredOrderRecord[];
  idempotencyIndex: Record<string, string>;
  subscriptions: PersistedSubscriptionRecord[];
}
