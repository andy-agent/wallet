import { Injectable, Logger } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import {
  PaymentScanCursorRecord,
  RuntimeStatePaymentContext,
  StoredOnchainReceiptRecord,
} from '../database/runtime-state.types';
import { RuntimeStateRepository } from '../database/runtime-state.repository';
import { SolanaClientService } from '../solana-client/solana-client.service';
import {
  NormalizedIncomingTransfer,
  ScanIncomingTransfersResponse,
} from '../solana-client/solana-client.types';
import { OrderStatus } from './orders.types';

const MATCHER_SCAN_STATUSES: OrderStatus[] = [
  'AWAITING_PAYMENT',
  'PAYMENT_DETECTED',
  'CONFIRMING',
  'UNDERPAID_REVIEW',
  'OVERPAID_REVIEW',
];

@Injectable()
export class OrderPaymentMatcherService {
  private readonly logger = new Logger(OrderPaymentMatcherService.name);
  private inFlight = false;

  constructor(
    private readonly configService: ConfigService,
    private readonly runtimeStateRepository: RuntimeStateRepository,
    private readonly solanaClient: SolanaClientService,
  ) {}

  isEnabled(): boolean {
    return (
      this.configService.get<string>('PAYMENT_MATCHER_ENABLED') === 'true'
    );
  }

  async scanActiveContextsOnce() {
    if (!this.isEnabled()) {
      return {
        enabled: false,
        scannedContexts: 0,
        storedEvents: 0,
      };
    }

    if (this.inFlight) {
      this.logger.debug('Order payment matcher tick skipped because a prior run is still active');
      return {
        enabled: true,
        skipped: true,
        scannedContexts: 0,
        storedEvents: 0,
      };
    }

    this.inFlight = true;
    try {
      const contexts =
        await this.runtimeStateRepository.listActivePaymentContexts({
          statuses: MATCHER_SCAN_STATUSES,
          activeAfter: Date.now(),
        });

      let storedEvents = 0;
      for (const context of contexts) {
        if (context.quoteNetworkCode !== 'SOLANA') {
          continue;
        }

        const cursor =
          await this.runtimeStateRepository.findPaymentScanCursor(context);
        const response = await this.scanContext(context, cursor);

        for (const event of response.events) {
          await this.runtimeStateRepository.upsertOnchainReceipt(
            this.toOnchainReceipt(context, event),
          );
          storedEvents += 1;
        }

        const nextCursor = this.toCursorRecord(context, cursor, response);
        if (nextCursor) {
          await this.runtimeStateRepository.savePaymentScanCursor(nextCursor);
        }
      }

      return {
        enabled: true,
        scannedContexts: contexts.length,
        storedEvents,
      };
    } finally {
      this.inFlight = false;
    }
  }

  private async scanContext(
    context: RuntimeStatePaymentContext,
    cursor: PaymentScanCursorRecord | null,
  ) {
    return this.solanaClient.scanIncomingTransfers({
      collectionAddress: context.collectionAddress,
      assetCode: context.quoteAssetCode,
      mint:
        context.quoteAssetCode === 'USDT'
          ? this.solanaClient.getUsdtMint()
          : null,
      cursor: cursor
        ? {
            beforeSignature: cursor.beforeSignature,
            minSlotExclusive: cursor.lastSlot,
          }
        : null,
      limit: 50,
    });
  }

  private toOnchainReceipt(
    context: RuntimeStatePaymentContext,
    event: NormalizedIncomingTransfer,
  ): StoredOnchainReceiptRecord {
    return {
      receiptId: `${context.quoteNetworkCode}:${event.signature}:${event.eventIndex}`,
      quoteNetworkCode: context.quoteNetworkCode,
      quoteAssetCode: context.quoteAssetCode,
      collectionAddress: context.collectionAddress,
      txHash: event.signature,
      eventIndex: event.eventIndex,
      recipientTokenAccount: event.recipientTokenAccount ?? null,
      fromAddress: event.fromAddress ?? null,
      mint: event.mint ?? null,
      amount: event.amount,
      amountMinor: event.amountRaw,
      confirmationStatus: event.confirmationStatus,
      slot: event.slot,
      blockTime:
        typeof event.blockTime === 'number'
          ? new Date(event.blockTime * 1000).toISOString()
          : null,
      observedAt: new Date().toISOString(),
      matchedOrderNo: null,
      matchStatus: 'UNMATCHED',
      matcherRemark: null,
      rawPayload: event.rawPayload ?? null,
    };
  }

  private toCursorRecord(
    context: RuntimeStatePaymentContext,
    previous: PaymentScanCursorRecord | null,
    response: ScanIncomingTransfersResponse,
  ): PaymentScanCursorRecord | null {
    const oldestEvent = response.events[response.events.length - 1];
    const newestEvent = response.events[0];
    const beforeSignature =
      response.nextCursor?.beforeSignature ??
      oldestEvent?.signature ??
      previous?.beforeSignature ??
      null;
    const minSlotExclusive =
      response.nextCursor?.minSlotExclusive ??
      previous?.lastSlot ??
      null;

    if (
      beforeSignature === null &&
      minSlotExclusive === null &&
      !newestEvent &&
      !previous
    ) {
      return null;
    }

    return {
      cursorKey: this.buildCursorKey(context),
      quoteNetworkCode: context.quoteNetworkCode,
      quoteAssetCode: context.quoteAssetCode,
      collectionAddress: context.collectionAddress,
      beforeSignature,
      lastSignature: newestEvent?.signature ?? previous?.lastSignature ?? null,
      lastSlot: newestEvent?.slot ?? minSlotExclusive ?? previous?.lastSlot ?? null,
      updatedAt: new Date().toISOString(),
    };
  }

  private buildCursorKey(context: RuntimeStatePaymentContext) {
    return [
      context.quoteNetworkCode,
      context.quoteAssetCode,
      context.collectionAddress,
    ].join(':');
  }
}
