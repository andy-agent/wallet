import {
  ConflictException,
  Injectable,
  Logger,
  NotFoundException,
} from '@nestjs/common';
import { randomUUID } from 'crypto';
import { AuthService } from '../auth/auth.service';
import { ProvisioningService } from '../provisioning/provisioning.service';
import { SolanaClientService } from '../solana-client/solana-client.service';
import { CreateOrderRequestDto } from './dto/create-order.request';
import { RefreshOrderStatusRequestDto } from './dto/refresh-order-status.request';
import { SubmitClientTxRequestDto } from './dto/submit-client-tx.request';
import { OrderRecord } from './orders.types';

@Injectable()
export class OrdersService {
  private readonly logger = new Logger(OrdersService.name);
  private readonly ordersByNo = new Map<string, OrderRecord>();
  private readonly idempotencyIndex = new Map<string, string>();

  constructor(
    private readonly authService: AuthService,
    private readonly provisioningService: ProvisioningService,
    private readonly solanaClient: SolanaClientService,
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
      serviceEnabled:
        order.quoteNetworkCode === 'SOLANA' ? this.solanaClient.isEnabled() : false,
    };
  }

  submitClientTx(accessToken: string, orderNo: string, dto: SubmitClientTxRequestDto) {
    const order = this.getOrder(accessToken, orderNo);
    order.submittedClientTxHash = dto.txHash;
    order.status = 'PAYMENT_DETECTED';
    return {};
  }

  async refreshStatus(
    accessToken: string,
    orderNo: string,
    _dto: RefreshOrderStatusRequestDto,
  ) {
    const order = this.getOrder(accessToken, orderNo);

    // Terminal states: no further progression
    if (
      order.status === 'COMPLETED' ||
      order.status === 'FAILED' ||
      order.status === 'EXPIRED' ||
      order.status === 'CANCELED'
    ) {
      return order;
    }

    if (new Date(order.expiresAt).getTime() <= Date.now() && order.status === 'AWAITING_PAYMENT') {
      order.status = 'EXPIRED';
      return order;
    }

    // Try remote chain-side status for SOLANA orders with a submitted transaction
    if (order.quoteNetworkCode === 'SOLANA' && order.submittedClientTxHash && this.solanaClient.isEnabled()) {
      try {
        const remoteStatus = await this.solanaClient.getTransactionStatus({
          signature: order.submittedClientTxHash,
        });

        if (remoteStatus.status === 'confirmed' || remoteStatus.status === 'finalized') {
          if (order.status !== 'PAID' && order.status !== 'PROVISIONING') {
            order.status = 'PAID';
            order.confirmedAt = new Date().toISOString();
          }
          // Auto-advance provisioning when reaching PAID
          if (order.status === 'PAID') {
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

        if (remoteStatus.status === 'failed') {
          order.status = 'FAILED';
          order.failureReason = remoteStatus.error ?? 'Transaction failed on chain';
          return order;
        }

        if (remoteStatus.status === 'pending') {
          return order;
        }

        // Unknown remote status: do not advance order implicitly.
        return order;
      } catch (error) {
        this.logger.warn(
          `Solana chain-side status check failed for order ${orderNo}, falling back to in-memory progression`,
          error instanceof Error ? error.message : error,
        );
        // Graceful degradation: fall through to in-memory state machine
      }
    }

    // In-memory state machine progression (fallback or for non-SOLANA orders)
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
