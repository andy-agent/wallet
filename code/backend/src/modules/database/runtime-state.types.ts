import {
  AuthAccount,
  AuthSession,
  VerificationCodeRecord,
} from '../auth/auth.types';
import { OrderRecord } from '../orders/orders.types';
import { PersistedWalletLifecycleRecord } from '../wallet/wallet.types';
import { PersistedSubscriptionRecord } from '../vpn/vpn.types';

export interface StoredOrderRecord extends OrderRecord {
  createdAt: string;
  idempotencyKey: string;
  collectionAddress: string;
}

export interface RuntimeStatePaymentContext {
  collectionAddress: string;
  quoteAssetCode: StoredOrderRecord['quoteAssetCode'];
  quoteNetworkCode: StoredOrderRecord['quoteNetworkCode'];
}

export type StoredOnchainReceiptMatchStatus =
  | 'UNMATCHED'
  | 'MATCHED'
  | 'AMBIGUOUS'
  | 'IGNORED';

export type StoredOnchainReceiptConfirmationStatus =
  | 'processed'
  | 'confirmed'
  | 'finalized'
  | 'failed'
  | 'unknown';

export interface StoredOnchainReceiptRecord extends RuntimeStatePaymentContext {
  receiptId: string;
  txHash: string;
  eventIndex: number;
  recipientTokenAccount: string | null;
  fromAddress: string | null;
  mint: string | null;
  amount: string;
  amountMinor: string;
  confirmationStatus: StoredOnchainReceiptConfirmationStatus;
  slot: number | null;
  blockTime: string | null;
  observedAt: string;
  matchedOrderNo: string | null;
  matchStatus: StoredOnchainReceiptMatchStatus;
  matcherRemark: string | null;
  rawPayload: Record<string, unknown> | null;
}

export interface PaymentScanCursorRecord extends RuntimeStatePaymentContext {
  cursorKey: string;
  beforeSignature: string | null;
  lastSignature: string | null;
  lastSlot: number | null;
  updatedAt: string;
}

export interface RuntimeStateSnapshot {
  version: 1 | 2 | 3 | 4;
  orders: StoredOrderRecord[];
  idempotencyIndex: Record<string, string>;
  subscriptions: PersistedSubscriptionRecord[];
  walletLifecycles: PersistedWalletLifecycleRecord[];
  accounts: AuthAccount[];
  sessions: AuthSession[];
  verificationCodes: VerificationCodeRecord[];
  onchainReceipts: StoredOnchainReceiptRecord[];
  paymentScanCursors: PaymentScanCursorRecord[];
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
