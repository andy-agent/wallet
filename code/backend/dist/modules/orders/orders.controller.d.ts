import { CreateOrderRequestDto } from './dto/create-order.request';
import { RefreshOrderStatusRequestDto } from './dto/refresh-order-status.request';
import { SubmitClientTxRequestDto } from './dto/submit-client-tx.request';
import { OrdersService } from './orders.service';
export declare class OrdersController {
    private readonly ordersService;
    constructor(ordersService: OrdersService);
    createOrder(authorization: string | undefined, idempotencyKey: string | undefined, body: CreateOrderRequestDto): import("./orders.types").OrderRecord;
    getOrder(authorization: string | undefined, orderNo: string): import("./orders.types").OrderRecord;
    getPaymentTarget(authorization: string | undefined, orderNo: string): {
        orderNo: string;
        networkCode: "SOLANA" | "TRON";
        assetCode: "SOL" | "USDT";
        collectionAddress: string;
        payableAmount: string;
        uniqueAmountDelta: string;
        qrText: string;
        expiresAt: string;
    };
    submitClientTx(authorization: string | undefined, orderNo: string, body: SubmitClientTxRequestDto): {};
    refreshStatus(authorization: string | undefined, orderNo: string, body: RefreshOrderStatusRequestDto): import("./orders.types").OrderRecord;
    private extractBearer;
}
