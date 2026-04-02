import { AuthService } from '../auth/auth.service';
import { ProvisioningService } from '../provisioning/provisioning.service';
import { CreateOrderRequestDto } from './dto/create-order.request';
import { RefreshOrderStatusRequestDto } from './dto/refresh-order-status.request';
import { SubmitClientTxRequestDto } from './dto/submit-client-tx.request';
import { OrderRecord } from './orders.types';
export declare class OrdersService {
    private readonly authService;
    private readonly provisioningService;
    private readonly ordersByNo;
    private readonly idempotencyIndex;
    constructor(authService: AuthService, provisioningService: ProvisioningService);
    createOrder(accessToken: string, dto: CreateOrderRequestDto, idempotencyKey: string): OrderRecord;
    getOrder(accessToken: string, orderNo: string): OrderRecord;
    getPaymentTarget(accessToken: string, orderNo: string): {
        orderNo: string;
        networkCode: "SOLANA" | "TRON";
        assetCode: "SOL" | "USDT";
        collectionAddress: string;
        payableAmount: string;
        uniqueAmountDelta: string;
        qrText: string;
        expiresAt: string;
    };
    submitClientTx(accessToken: string, orderNo: string, dto: SubmitClientTxRequestDto): {};
    refreshStatus(accessToken: string, orderNo: string, _dto: RefreshOrderStatusRequestDto): OrderRecord;
    private mustGet;
}
