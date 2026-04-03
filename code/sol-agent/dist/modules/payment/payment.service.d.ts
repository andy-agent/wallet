import { DetectPaymentRequestDto } from './dto/detect-payment.request';
export declare class PaymentService {
    getPaymentStatus(address: string, networkCode?: string): {
        address: string;
        networkCode: string;
        status: string;
        receivedAmount: string;
        expectedAmount: null;
        txHash: null;
        confirmations: number;
        updatedAt: string;
        note: string;
    };
    detectPayment(body: DetectPaymentRequestDto): {
        address: string;
        networkCode: string;
        status: string;
        receivedAmount: string;
        expectedAmount: string | null;
        txHash: null;
        confirmations: number;
        updatedAt: string;
        note: string;
    };
}
