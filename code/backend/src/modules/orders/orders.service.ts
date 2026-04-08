import {
  ConflictException,
  Injectable,
  Logger,
  NotFoundException,
} from '@nestjs/common';
import { randomUUID } from 'crypto';
import { AuthService } from '../auth/auth.service';
import { RuntimeStateRepository } from '../database/runtime-state.repository';
import { StoredOrderRecord } from '../database/runtime-state.types';
import { ProvisioningService } from '../provisioning/provisioning.service';
import { SolanaClientService } from '../solana-client/solana-client.service';
import { CreateOrderRequestDto } from './dto/create-order.request';
import { RefreshOrderStatusRequestDto } from './dto/refresh-order-status.request';
import { SubmitClientTxRequestDto } from './dto/submit-client-tx.request';
import { OrderRecord } from './orders.types';

@Injectable()
export class OrdersService {
  private readonly logger = new Logger(OrdersService.name);

  constructor(
    private readonly authService: AuthService,
    private readonly runtimeStateRepository: RuntimeStateRepository,
    private readonly provisioningService: ProvisioningService,
    private readonly solanaClient: SolanaClientService,
  ) {}

  async createOrder(
    accessToken: string,
    dto: CreateOrderRequestDto,
    idempotencyKey: string,
  ) {
    const account = this.authService.getMe(accessToken);
    const compositeKey = `${account.accountId}:${idempotencyKey}`;
    if (!idempotencyKey) {
      throw new ConflictException({
        code: 'ORDER_CREATE_CONFLICT',
        message: 'Missing idempotency key',
      });
    }

    const orderId = randomUUID();
    const orderNo = `ORD-${Date.now()}-${orderId.slice(0, 8).toUpperCase()}`;
    const storedOrder: StoredOrderRecord = {
      createdAt: new Date().toISOString(),
      idempotencyKey: compositeKey,
      collectionAddress: this.buildCollectionAddress(dto.quoteNetworkCode, orderId),
      uniqueAmountDelta: this.buildUniqueAmountDelta(orderId),
      orderId,
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

    const order = await this.runtimeStateRepository.createOrder(
      storedOrder,
      compositeKey,
    );
    return this.toOrderRecord(order);
  }

  async getOrder(accessToken: string, orderNo: string) {
    const account = this.authService.getMe(accessToken);
    const order = await this.mustGet(orderNo);
    if (order.accountId !== account.accountId) {
      throw new NotFoundException({
        code: 'ORDER_NOT_FOUND',
        message: 'Order not found',
      });
    }
    return this.toOrderRecord(order);
  }

  async getPaymentTarget(accessToken: string, orderNo: string) {
    const order = await this.mustGetOwned(accessToken, orderNo);
    return {
      orderNo: order.orderNo,
      networkCode: order.quoteNetworkCode,
      assetCode: order.quoteAssetCode,
      collectionAddress: order.collectionAddress,
      payableAmount: order.payableAmount,
      uniqueAmountDelta: order.uniqueAmountDelta,
      qrText: `${order.quoteNetworkCode}:${order.orderNo}:${order.payableAmount}`,
      expiresAt: order.expiresAt,
      serviceEnabled:
        order.quoteNetworkCode === 'SOLANA' ? this.solanaClient.isEnabled() : false,
    };
  }

  async submitClientTx(
    accessToken: string,
    orderNo: string,
    dto: SubmitClientTxRequestDto,
  ) {
    const order = await this.mustGetOwned(accessToken, orderNo);
    order.submittedClientTxHash = dto.txHash;
    order.status = order.status === 'AWAITING_PAYMENT' ? 'PAYMENT_DETECTED' : order.status;
    await this.runtimeStateRepository.saveOrder(order);
    return {};
  }

  async refreshStatus(
    accessToken: string,
    orderNo: string,
    _dto: RefreshOrderStatusRequestDto,
  ) {
    const order = await this.mustGetOwned(accessToken, orderNo);

    // Terminal states: no further progression
    if (
      order.status === 'COMPLETED' ||
      order.status === 'FAILED' ||
      order.status === 'EXPIRED' ||
      order.status === 'CANCELED'
    ) {
      return this.toOrderRecord(order);
    }

    if (new Date(order.expiresAt).getTime() <= Date.now() && order.status === 'AWAITING_PAYMENT') {
      order.status = 'EXPIRED';
      await this.runtimeStateRepository.saveOrder(order);
      return this.toOrderRecord(order);
    }

    if (!order.submittedClientTxHash) {
      return this.toOrderRecord(order);
    }

    if (order.quoteNetworkCode === 'SOLANA') {
      try {
        const remoteStatus = await this.solanaClient.getTransactionStatus({
          signature: order.submittedClientTxHash,
        });

        if (remoteStatus.status === 'confirmed' || remoteStatus.status === 'finalized') {
          return this.markPaidAndProvision(order);
        }

        if (remoteStatus.status === 'failed') {
          order.status = 'FAILED';
          order.failureReason = remoteStatus.error ?? 'Transaction failed on chain';
          await this.runtimeStateRepository.saveOrder(order);
          return this.toOrderRecord(order);
        }

        if (remoteStatus.status === 'pending') {
          if (order.status === 'PAYMENT_DETECTED') {
            order.status = 'CONFIRMING';
            await this.runtimeStateRepository.saveOrder(order);
          }
          return this.toOrderRecord(order);
        }

        return this.toOrderRecord(order);
      } catch (error) {
        this.logger.warn(
          `Solana chain-side status check failed for order ${orderNo}, preserving persisted status`,
          error instanceof Error ? error.message : error,
        );
        return this.toOrderRecord(order);
      }
    }

    return this.toOrderRecord(order);
  }

  async getAwaitingOrderCount() {
    return this.runtimeStateRepository.countOrdersByStatus(['AWAITING_PAYMENT']);
  }

  async getReviewOrderCount() {
    return this.runtimeStateRepository.countOrdersByStatus([
      'UNDERPAID_REVIEW',
      'OVERPAID_REVIEW',
    ]);
  }

  async getTodayPaidOrderCount() {
    const startOfDay = new Date();
    startOfDay.setHours(0, 0, 0, 0);
    return this.runtimeStateRepository.countOrdersByStatus(
      ['PAID', 'PROVISIONING', 'COMPLETED'],
      startOfDay.getTime(),
    );
  }

  private async mustGet(orderNo: string) {
    const order = await this.runtimeStateRepository.findOrderByNo(orderNo);
    if (!order) {
      throw new NotFoundException({
        code: 'ORDER_NOT_FOUND',
        message: 'Order not found',
      });
    }
    return order;
  }

  async listOrders(params: {
    page?: number;
    pageSize?: number;
    orderNo?: string;
    status?: string;
    accountId?: string;
  }) {
    const result = await this.runtimeStateRepository.listOrders(params);
    return {
      items: result.items.map((order) => this.toOrderRecord(order)),
      page: result.page,
    };
  }

  async getOrderByNo(orderNo: string) {
    return this.toOrderRecord(await this.mustGet(orderNo));
  }

  private async mustGetOwned(accessToken: string, orderNo: string) {
    const account = this.authService.getMe(accessToken);
    const order = await this.mustGet(orderNo);
    if (order.accountId !== account.accountId) {
      throw new NotFoundException({
        code: 'ORDER_NOT_FOUND',
        message: 'Order not found',
      });
    }
    return order;
  }

  private async markPaidAndProvision(
    order: StoredOrderRecord,
  ): Promise<OrderRecord> {
    if (order.status !== 'PAID' && order.status !== 'PROVISIONING' && order.status !== 'COMPLETED') {
      order.status = 'PAID';
      order.confirmedAt = new Date().toISOString();
      await this.runtimeStateRepository.saveOrder(order);
    }

    if (order.status === 'PAID') {
      order.status = 'PROVISIONING';
      await this.runtimeStateRepository.saveOrder(order);
      await this.provisioningService.provisionPaidOrder({
        accountId: order.accountId,
        planCode: order.planCode,
        orderNo: order.orderNo,
        sourceAssetCode: order.quoteAssetCode,
        sourceAmount: order.quoteUsdAmount,
      });
      order.status = 'COMPLETED';
      order.completedAt = new Date().toISOString();
      await this.runtimeStateRepository.saveOrder(order);
    }

    return this.toOrderRecord(order);
  }

  private toOrderRecord(order: StoredOrderRecord): OrderRecord {
    const { collectionAddress: _collectionAddress, createdAt: _createdAt, idempotencyKey: _idempotencyKey, uniqueAmountDelta: _uniqueAmountDelta, ...publicOrder } =
      order;
    return publicOrder;
  }

  private buildCollectionAddress(
    networkCode: StoredOrderRecord['quoteNetworkCode'],
    seed: string,
  ) {
    const base =
      networkCode === 'SOLANA'
        ? 'So11111111111111111111111111111111111111112'
        : 'TR7NhqjeKQxGTCi8q8ZY4pL8otSzgjLj6t';
    const alphabet = '123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz';
    const suffixLength = Math.min(6, base.length);
    let suffix = '';

    for (let index = 0; index < suffixLength; index++) {
      const charCode = seed.charCodeAt(index % seed.length);
      suffix += alphabet[charCode % alphabet.length];
    }

    return `${base.slice(0, base.length - suffix.length)}${suffix}`;
  }

  private buildUniqueAmountDelta(seed: string) {
    let accumulator = 0;
    for (const char of seed) {
      accumulator = (accumulator + char.charCodeAt(0)) % 999999;
    }
    return `0.${String(accumulator + 1).padStart(6, '0')}`;
  }
}
