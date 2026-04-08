import {
  AuthAccount,
  AuthSession,
  VerificationCodeRecord,
} from '../auth/auth.types';
import { OrderRecord } from '../orders/orders.types';
import { PersistedSubscriptionRecord } from '../vpn/vpn.types';

export interface StoredOrderRecord extends OrderRecord {
  createdAt: string;
  idempotencyKey: string;
  collectionAddress: string;
  uniqueAmountDelta: string;
}

export interface RuntimeStateSnapshot {
  version: 1 | 2;
  orders: StoredOrderRecord[];
  idempotencyIndex: Record<string, string>;
  subscriptions: PersistedSubscriptionRecord[];
  accounts: AuthAccount[];
  sessions: AuthSession[];
  verificationCodes: VerificationCodeRecord[];
}

export interface RuntimeStateListOrdersParams {
  page?: number;
  pageSize?: number;
  orderNo?: string;
  status?: string;
  accountId?: string;
}

export interface RuntimeStateListOrdersResult {
  items: StoredOrderRecord[];
  page: {
    page: number;
    pageSize: number;
    total: number;
  };
}
