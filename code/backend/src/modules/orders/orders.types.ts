export type OrderStatus =
  | 'AWAITING_PAYMENT'
  | 'PAYMENT_DETECTED'
  | 'CONFIRMING'
  | 'PAID'
  | 'PROVISIONING'
  | 'COMPLETED'
  | 'EXPIRED'
  | 'UNDERPAID_REVIEW'
  | 'OVERPAID_REVIEW'
  | 'FAILED'
  | 'CANCELED';

export interface OrderRecord {
  orderId: string;
  orderNo: string;
  accountId: string;
  payerWalletId: string | null;
  payerChainAccountId: string | null;
  submittedFromAddress: string | null;
  planCode: string;
  planName: string;
  orderType: 'NEW' | 'RENEWAL';
  quoteAssetCode: string;
  quoteNetworkCode: 'SOLANA' | 'TRON';
  quoteUsdAmount: string;
  baseAmount: string;
  uniqueAmountDelta: string;
  payableAmount: string;
  status: OrderStatus;
  expiresAt: string;
  confirmedAt: string | null;
  completedAt: string | null;
  failureReason: string | null;
  submittedClientTxHash: string | null;
  matchedOnchainTxHash: string | null;
  paymentMatchedAt: string | null;
  matcherRemark: string | null;
}
