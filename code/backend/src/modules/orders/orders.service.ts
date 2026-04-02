import {
  ConflictException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { randomUUID } from 'crypto';
import { AuthService } from '../auth/auth.service';
import { ProvisioningService } from '../provisioning/provisioning.service';
import { CreateOrderRequestDto } from './dto/create-order.request';
import { RefreshOrderStatusRequestDto } from './dto/refresh-order-status.request';
import { SubmitClientTxRequestDto } from './dto/submit-client-tx.request';
import { OrderRecord } from './orders.types';

@Injectable()
export class OrdersService {
  private readonly ordersByNo = new Map<string, OrderRecord>();
  private readonly idempotencyIndex = new Map<string, string>();

  constructor(
    private readonly authService: AuthService,
    private readonly provisioningService: ProvisioningService,
  ) {}

  createOrder(accessToken: string, dto: CreateOrderRequestDto, idempotencyKey: string) {
    const account = this.authService.getMe(accessToken);
    const compositeKey = `${account.accountId}:${idempotencyKey}`;
    const existingOrderNo = this.idempotencyIndex.get(compositeKey);
    if (existingOrderNo) {
      return this.mustGet(existingOrderNo);
    }

    if (!idempotencyKey) {
      throw new ConflictException({
        code: 'ORDER_CREATE_CONFLICT',
        message: 'Missing idempotency key',
      });
    }

    const orderNo = `ORD-${Date.now()}`;
    const order: OrderRecord = {
      orderId: randomUUID(),
      orderNo,
      accountId: account.accountId,
      planCode: dto.planCode,
      planName: dto.planCode === 'BASIC_1M' ? '基础版-1个月' : dto.planCode,
      orderType: dto.orderType,
      quoteAssetCode: dto.quoteAssetCode,
      quoteNetworkCode: dto.quoteNetworkCode,
      quoteUsdAmount: '9.99',
      payableAmount: dto.quoteAssetCode === 'SOL' ? '0.04500000' : '9.990000',
      status: 'AWAITING_PAYMENT',
      expiresAt: new Date(Date.now() + 15 * 60 * 1000).toISOString(),
      confirmedAt: null,
      completedAt: null,
      failureReason: null,
      submittedClientTxHash: null,
    };

    this.ordersByNo.set(orderNo, order);
    this.idempotencyIndex.set(compositeKey, orderNo);
    return order;
  }

  getOrder(accessToken: string, orderNo: string) {
    const account = this.authService.getMe(accessToken);
    const order = this.mustGet(orderNo);
    if (order.accountId !== account.accountId) {
      throw new NotFoundException({
        code: 'ORDER_NOT_FOUND',
        message: 'Order not found',
      });
    }
    return order;
  }

  getPaymentTarget(accessToken: string, orderNo: string) {
    const order = this.getOrder(accessToken, orderNo);
    return {
      orderNo: order.orderNo,
      networkCode: order.quoteNetworkCode,
      assetCode: order.quoteAssetCode,
      collectionAddress:
        order.quoteNetworkCode === 'SOLANA'
          ? 'So11111111111111111111111111111111111111112'
          : 'TR7NhqjeKQxGTCi8q8ZY4pL8otSzgjLj6t',
      payableAmount: order.payableAmount,
      uniqueAmountDelta: '0.000001',
      qrText: `${order.quoteNetworkCode}:${order.orderNo}:${order.payableAmount}`,
      expiresAt: order.expiresAt,
    };
  }

  submitClientTx(accessToken: string, orderNo: string, dto: SubmitClientTxRequestDto) {
    const order = this.getOrder(accessToken, orderNo);
    order.submittedClientTxHash = dto.txHash;
    order.status = 'PAYMENT_DETECTED';
    return {};
  }

  refreshStatus(
    accessToken: string,
    orderNo: string,
    _dto: RefreshOrderStatusRequestDto,
  ) {
    const order = this.getOrder(accessToken, orderNo);

    if (new Date(order.expiresAt).getTime() <= Date.now() && order.status === 'AWAITING_PAYMENT') {
      order.status = 'EXPIRED';
      return order;
    }

    if (order.submittedClientTxHash && order.status === 'PAYMENT_DETECTED') {
      order.status = 'CONFIRMING';
    } else if (order.submittedClientTxHash && order.status === 'CONFIRMING') {
      order.status = 'PAID';
      order.confirmedAt = new Date().toISOString();
    } else if (order.status === 'PAID') {
      order.status = 'PROVISIONING';
      this.provisioningService.provisionPaidOrder({
        accountId: order.accountId,
        planCode: order.planCode,
        orderNo: order.orderNo,
        sourceAssetCode: order.quoteAssetCode,
        sourceAmount: order.quoteUsdAmount,
      });
      order.status = 'COMPLETED';
      order.completedAt = new Date().toISOString();
    }

    return order;
  }

  private mustGet(orderNo: string) {
    const order = this.ordersByNo.get(orderNo);
    if (!order) {
      throw new NotFoundException({
        code: 'ORDER_NOT_FOUND',
        message: 'Order not found',
      });
    }
    return order;
  }
}
