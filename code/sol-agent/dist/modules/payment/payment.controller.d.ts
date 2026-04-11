import { PaymentService } from './payment.service';
import { DetectPaymentRequestDto } from './dto/detect-payment.request';
import { ScanIncomingTransfersRequestDto } from './dto/scan-incoming.request';
import { VerifyTransactionRequestDto } from './dto/verify-transaction.request';
export declare class PaymentController {
    private readonly paymentService;
    constructor(paymentService: PaymentService);
    getPaymentStatus(address: string, networkCode?: string): Promise<{
        address: string;
        networkCode: string;
        status: string;
        receivedAmount: string;
        expectedAmount: null;
        txHash: string | null;
        confirmations: number;
        balance: number;
        recentTxCount: number;
        updatedAt: string;
    } | {
        address: string;
        networkCode: string;
        status: string;
        receivedAmount: string;
        expectedAmount: null;
        txHash: null;
        confirmations: number;
        error: string;
        updatedAt: string;
    }>;
    detectPayment(body: DetectPaymentRequestDto): Promise<{
        address: string;
        networkCode: string;
        status: string;
        receivedAmount: string;
        expectedAmount: string | null;
        txHash: string | null;
        confirmations: number;
        recentTransactions: string[];
        updatedAt: string;
    } | {
        address: string;
        networkCode: string;
        status: string;
        receivedAmount: string;
        expectedAmount: string | null;
        txHash: null;
        confirmations: number;
        error: string;
        updatedAt: string;
    }>;
    scanIncomingTransfers(body: ScanIncomingTransfersRequestDto): Promise<import("./dto/scan-incoming.response").ScanIncomingTransfersResponseDto>;
    verifyTransaction(body: VerifyTransactionRequestDto): Promise<{
        signature: string;
        networkCode: string;
        status: "pending" | "failed" | "mismatch" | "verified";
        recipientAddress: string;
        assetCode: string;
        assetKind: "NATIVE_SOL" | "SPL_TOKEN";
        mintAddress: string | null;
        decimals: number;
        expectedAmount: string;
        expectedAmountRaw: string;
        receivedAmount: string;
        receivedAmountRaw: string;
        recipientMatched: boolean;
        amountSatisfied: boolean;
        matchedAccounts: string[];
        slot: number | null;
        blockTime: number | null;
        error: string | undefined;
        verifiedAt: string;
    }>;
}
