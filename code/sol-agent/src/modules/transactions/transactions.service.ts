import { Injectable, Logger } from '@nestjs/common';
import { SolanaRpcService } from '../solana/solana.rpc.service';

export interface TransactionStatusResponse {
  /** Transaction signature */
  signature: string;
  /** Current status */
  status: 'pending' | 'confirmed' | 'finalized' | 'failed';
  /** Confirmation count */
  confirmations: number;
  /** Error message if failed */
  error?: string;
  /** Block time if available */
  blockTime?: number;
  /** Slot */
  slot?: number;
}

@Injectable()
export class TransactionsService {
  private readonly logger = new Logger(TransactionsService.name);

  constructor(private readonly solanaRpc: SolanaRpcService) {}

  /**
   * Get transaction status by signature
   * Maps Solana transaction info to backend-expected format
   */
  async getTransactionStatus(
    signature: string,
    networkCode?: string,
  ): Promise<TransactionStatusResponse> {
    const effectiveNetworkCode = networkCode ?? 'solana-mainnet';

    try {
      // Fetch transaction details from Solana RPC
      const txResult = await this.solanaRpc.getTransaction(
        signature,
        effectiveNetworkCode,
      );

      // If transaction is not found (null), treat as pending
      if (!txResult.transaction) {
        return {
          signature,
          status: 'pending',
          confirmations: 0,
        };
      }

      const tx = txResult.transaction;

      // Map Solana transaction status to our format
      // Solana transaction structure from getTransaction:
      // - slot: number
      // - blockTime: number | null
      // - meta: { err: object | null, fee: number, ... }
      // - transaction: { signatures, message }

      const hasError = tx.meta?.err !== null;
      const slot = tx.slot;
      const blockTime = tx.blockTime ?? undefined;

      // Determine status based on transaction metadata
      // Note: This is a simplified mapping. In production, you might want to
      // check confirmation status via getSignatureStatuses for more accuracy
      let status: TransactionStatusResponse['status'];
      let confirmations = 0;

      if (hasError) {
        status = 'failed';
        confirmations = 0;
      } else {
        // Transaction exists and has no error - at least confirmed
        // Without explicit confirmation status, we use 'confirmed' as default
        // In a full implementation, query getSignatureStatuses for precise status
        status = 'confirmed';
        confirmations = 1; // Simplified: at least 1 confirmation
      }

      return {
        signature,
        status,
        confirmations,
        error: hasError ? JSON.stringify(tx.meta.err) : undefined,
        blockTime,
        slot,
      };
    } catch (error) {
      this.logger.error(
        `Failed to get transaction status for ${signature}:`,
        error,
      );

      // Return pending status on error (graceful degradation)
      // This prevents backend from marking orders as failed due to transient RPC issues
      return {
        signature,
        status: 'pending',
        confirmations: 0,
        error:
          error instanceof Error
            ? error.message
            : 'Failed to query transaction status',
      };
    }
  }
}
