export type SubscriptionStatus =
  | 'PENDING_ACTIVATION'
  | 'ACTIVE'
  | 'EXPIRED'
  | 'SUSPENDED'
  | 'CANCELED'
  | 'NONE';

export interface SubscriptionState {
  subscriptionId: string;
  planCode: string;
  status: SubscriptionStatus;
  startedAt: string | null;
  expireAt: string | null;
  daysRemaining: number | null;
  isUnlimitedTraffic: boolean;
  maxActiveSessions: number;
  marzbanUsername: string | null;
  subscriptionUrl: string | null;
}

export interface PersistedSubscriptionRecord extends SubscriptionState {
  accountId: string;
  orderNo: string;
  createdAt: string;
  updatedAt: string;
}
