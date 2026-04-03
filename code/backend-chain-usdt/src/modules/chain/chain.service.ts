import { Injectable, Logger } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { TronRpcService } from './tron-rpc.service';

export interface TransactionQueryResult {
  txHash: string;
  status: 'pending' | 'confirmed' | 'failed' | 'not_found';
  from: string;
  to: string;
  amount: string;
  token: 'USDT' | 'TRX';
  blockHeight?: number;
  confirmations: number;
  timestamp?: string;
  fee?: string;
}

export interface BroadcastResult {
  success: boolean;
  txHash?: string;
  errorCode?: string;
  errorMessage?: string;
  acceptedAt?: string;
}

export interface BlockInfo {
  height: number;
  hash: string;
  timestamp: string;
  txCount: number;
}

/**
 * Chain service for TRON/TRC20 operations.
 * 
 * CAPABILITIES:
 * - Query transaction status by hash (real RPC)
 * - Broadcast signed transactions (real RPC)
 * - Get block information (real RPC)
 * - Validate TRON addresses (local)
 * 
 * LIMITATIONS (by design):
 * - No multi-chain aggregation
 * - No auto-withdrawal
 * - No transaction signing (only broadcasts pre-signed)
 */
@Injectable()
export class ChainService {
  private readonly logger = new Logger(ChainService.name);
  private readonly mockMode: boolean;
  private readonly usdtContract: string;

  constructor(
    private readonly configService: ConfigService,
    private readonly tronRpcService: TronRpcService,
  ) {
    this.mockMode = this.configService.get<string>('MOCK_CHAIN') === 'true';
    this.usdtContract = this.configService.get<string>('TRON_USDT_CONTRACT') ?? 
      'TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t';
    this.logger.log(`Chain service initialized (mockMode: ${this.mockMode})`);
  }

  /**
   * Query transaction status by hash.
   * 
   * @param txHash - Transaction hash to query
   * @returns Transaction details or null if not found
   */
  async queryTransaction(txHash: string): Promise<TransactionQueryResult | null> {
    this.logger.debug(`Querying transaction: ${txHash}`);

    if (this.mockMode) {
      return this.getMockTransaction(txHash);
    }

    try {
      // Get transaction info (receipt)
      const txInfo = await this.tronRpcService.getTransactionInfo(txHash);
      
      // Get raw transaction for parsing TRC20 details
      const rawTx = await this.tronRpcService.getTransaction(txHash);

      if (!txInfo && !rawTx) {
        // Transaction not found - could be pending or non-existent
        return {
          txHash,
          status: 'pending',
          from: '',
          to: '',
          amount: '0',
          token: 'USDT',
          confirmations: 0,
        };
      }

      // Determine status from receipt
      let status: TransactionQueryResult['status'] = 'pending';
      if (txInfo) {
        const receiptResult = txInfo.receipt?.result;
        if (receiptResult === 'SUCCESS' || txInfo.result === 'SUCCESS') {
          status = 'confirmed';
        } else if (receiptResult === 'FAILED' || txInfo.result === 'FAILED') {
          status = 'failed';
        } else if (txInfo.blockNumber > 0) {
          status = 'confirmed';
        }
      }

      // Parse TRC20 transfer details
      let from = '';
      let to = '';
      let amount = '0';
      let token: 'USDT' | 'TRX' = 'TRX';

      if (rawTx) {
        const parsed = this.tronRpcService.parseTrc20Transfer(rawTx);
        if (parsed) {
          from = parsed.from ?? '';
          to = parsed.to ?? '';
          amount = parsed.amount ?? '0';
          // Check if it's USDT contract
          if (parsed.contractAddress === this.usdtContract ||
              parsed.contractAddress?.toLowerCase() === this.usdtContract.toLowerCase()) {
            token = 'USDT';
          }
        } else {
          // Try to parse as TRX transfer
          const tx = rawTx as {
            raw_data?: {
              contract?: Array<{
                type?: string;
                parameter?: {
                  value?: {
                    owner_address?: string;
                    to_address?: string;
                    amount?: number;
                  };
                };
              }>;
            };
          };
          const contract = tx.raw_data?.contract?.[0];
          if (contract?.type === 'TransferContract') {
            const value = contract.parameter?.value;
            if (value) {
              from = value.owner_address ? 
                this.tronRpcService['tronWeb']?.address.fromHex(value.owner_address) ?? '' : '';
              to = value.to_address ? 
                this.tronRpcService['tronWeb']?.address.fromHex(value.to_address) ?? '' : '';
              amount = value.amount ? (value.amount / 1e6).toString() : '0';
              token = 'TRX';
            }
          }
        }
      }

      // Calculate confirmations if we have current block
      let confirmations = 0;
      const currentBlock = await this.tronRpcService.getCurrentBlock();
      if (txInfo?.blockNumber && currentBlock?.block_header?.raw_data?.number) {
        confirmations = currentBlock.block_header.raw_data.number - txInfo.blockNumber + 1;
      }

      return {
        txHash,
        status,
        from,
        to,
        amount: token === 'USDT' 
          ? (Number(amount) / 1e6).toFixed(6) 
          : amount,
        token,
        blockHeight: txInfo?.blockNumber || undefined,
        confirmations: Math.max(0, confirmations),
        timestamp: txInfo?.blockTimeStamp 
          ? new Date(txInfo.blockTimeStamp).toISOString() 
          : undefined,
        fee: txInfo?.receipt?.energy_fee 
          ? (txInfo.receipt.energy_fee / 1e6).toFixed(6)
          : undefined,
      };
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Unknown error';
      this.logger.error(`Error querying transaction ${txHash}: ${errorMessage}`);
      return null;
    }
  }

  /**
   * Query multiple transactions in batch.
   * 
   * @param txHashes - Array of transaction hashes
   * @returns Map of txHash to transaction result
   */
  async queryTransactionsBatch(txHashes: string[]): Promise<Map<string, TransactionQueryResult | null>> {
    this.logger.debug(`Querying ${txHashes.length} transactions in batch`);
    
    const results = new Map<string, TransactionQueryResult | null>();
    
    // Process sequentially to avoid rate limiting
    for (const txHash of txHashes) {
      results.set(txHash, await this.queryTransaction(txHash));
    }
    
    return results;
  }

  /**
   * Broadcast a signed transaction to the network.
   * 
   * @param signedTxHex - Signed transaction hex string
   * @returns Broadcast result with txHash or error
   * 
   * SECURITY: This does NOT sign transactions - only broadcasts pre-signed txs
   */
  async broadcastTransaction(signedTxHex: string): Promise<BroadcastResult> {
    this.logger.debug(`Broadcasting transaction (length: ${signedTxHex.length})`);

    if (this.mockMode) {
      return this.getMockBroadcastResult();
    }

    try {
      // Parse the signed transaction from hex
      let signedTx: { [key: string]: unknown } | string;
      try {
        signedTx = JSON.parse(Buffer.from(signedTxHex, 'hex').toString('utf8')) as { [key: string]: unknown };
      } catch {
        // Try as raw hex without JSON wrapper
        signedTx = signedTxHex;
      }

      const result = await this.tronRpcService.broadcastTransaction(signedTx as { [key: string]: unknown });

      if (result.success) {
        return {
          success: true,
          txHash: result.txid,
          acceptedAt: new Date().toISOString(),
        };
      } else {
        return {
          success: false,
          errorCode: result.code || 'BROADCAST_FAILED',
          errorMessage: result.message || 'Failed to broadcast transaction',
        };
      }
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Unknown error';
      this.logger.error(`Error broadcasting transaction: ${errorMessage}`);
      return {
        success: false,
        errorCode: 'BROADCAST_ERROR',
        errorMessage: errorMessage,
      };
    }
  }

  /**
   * Get current block information.
   * 
   * @returns Current block details
   */
  async getCurrentBlock(): Promise<BlockInfo> {
    this.logger.debug('Getting current block');

    if (this.mockMode) {
      return this.getMockBlockInfo();
    }

    const block = await this.tronRpcService.getCurrentBlock();
    
    if (!block) {
      throw new Error('Failed to get current block from RPC');
    }

    return {
      height: block.block_header.raw_data.number,
      hash: block.blockID,
      timestamp: new Date(block.block_header.raw_data.timestamp).toISOString(),
      txCount: block.transactions?.length ?? 0,
    };
  }

  /**
   * Validate a TRON address.
   * 
   * @param address - Address to validate
   * @returns Whether the address is valid
   */
  validateAddress(address: string): boolean {
    // TRON addresses start with 'T' and are 34 characters long
    return /^T[0-9a-zA-Z]{33}$/.test(address);
  }

  /**
   * Get USDT TRC20 contract information.
   * 
   * @returns Contract details
   */
  getContractInfo(): { address: string; decimals: number; symbol: string } {
    return {
      address: this.usdtContract,
      decimals: 6,
      symbol: 'USDT',
    };
  }

  /**
   * Check if service is running in mock mode.
   * 
   * @returns Whether mock mode is enabled
   */
  isMockMode(): boolean {
    return this.mockMode;
  }

  // Mock data generators for mock mode

  private getMockTransaction(txHash: string): TransactionQueryResult {
    return {
      txHash,
      status: 'confirmed',
      from: 'TFromAddress123456789',
      to: 'TToAddress123456789',
      amount: '100.000000',
      token: 'USDT',
      blockHeight: 12345678,
      confirmations: 21,
      timestamp: new Date().toISOString(),
      fee: '1.000000',
    };
  }

  private getMockBroadcastResult(): BroadcastResult {
    const mockTxHash = '0x' + Array.from({ length: 64 }, () => 
      Math.floor(Math.random() * 16).toString(16)
    ).join('');

    return {
      success: true,
      txHash: mockTxHash,
      acceptedAt: new Date().toISOString(),
    };
  }

  private getMockBlockInfo(): BlockInfo {
    return {
      height: 12345678,
      hash: '0x' + Array.from({ length: 64 }, () => 
        Math.floor(Math.random() * 16).toString(16)
      ).join(''),
      timestamp: new Date().toISOString(),
      txCount: 150,
    };
  }
}
