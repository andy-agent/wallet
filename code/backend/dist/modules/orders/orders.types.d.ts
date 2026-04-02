export type OrderStatus = 'AWAITING_PAYMENT' | 'PAYMENT_DETECTED' | 'CONFIRMING' | 'PAID' | 'PROVISIONING' | 'COMPLETED' | 'EXPIRED' | 'UNDERPAID_REVIEW' | 'OVERPAID_REVIEW' | 'FAILED' | 'CANCELED';
export interface OrderRecord {
    orderId: string;
    orderNo: string;
    accountId: string;
    planCode: string;
    planName: string;
    orderType: 'NEW' | 'RENEWAL';
    quoteAssetCode: 'SOL' | 'USDT';
    quoteNetworkCode: 'SOLANA' | 'TRON';
    quoteUsdAmount: string;
    payableAmount: string;
    status: OrderStatus;
    expiresAt: string;
    confirmedAt: string | null;
    completedAt: string | null;
    failureReason: string | null;
    submittedClientTxHash: string | null;
}
