import { PaymentService } from './payment.service';
import { DetectPaymentRequestDto } from './dto/detect-payment.request';
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
}
