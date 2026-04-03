import { Injectable, Logger, OnModuleInit } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { TronWeb } from 'tronweb';

export interface TronTransactionInfo {
  id: string;
  blockNumber: number;
  blockTimeStamp: number;
  contractResult?: string[];
  contract_address?: string;
  receipt?: {
    energy_fee?: number;
    energy_usage_total?: number;
    net_fee?: number;
    result?: string;
  };
  result?: string;
}

export interface TronBlockInfo {
  blockID: string;
  block_header: {
    raw_data: {
      number: number;
      timestamp: number;
      txTrieRoot: string;
      parentHash: string;
      witness_address: string;
    };
    witness_signature: string;
  };
  transactions?: unknown[];
}

/**
 * Service for interacting with TRON blockchain via RPC.
 * 
 * This is a minimal implementation focused on:
 * - Health checks via getNowBlock
 * - Transaction queries
 * - Broadcast signed transactions
 * 
 * Uses TronWeb library for RPC communication.
 */
@Injectable()
export class TronRpcService implements OnModuleInit {
  private readonly logger = new Logger(TronRpcService.name);
  private tronWeb: InstanceType<typeof TronWeb> | null = null;
  private readonly rpcUrl: string;
  private readonly apiKey?: string;
  private isConnected = false;

  constructor(private readonly configService: ConfigService) {
    this.rpcUrl = this.configService.get<string>('TRON_RPC_URL') ?? 'https://api.trongrid.io';
    this.apiKey = this.configService.get<string>('TRON_API_KEY') ?? undefined;
  }

  async onModuleInit(): Promise<void> {
    const mockMode = this.configService.get<string>('MOCK_CHAIN') === 'true';
    if (mockMode) {
      this.logger.log('Mock mode enabled, skipping TRON RPC initialization');
      return;
    }

    await this.initialize();
  }

  /**
   * Initialize TRON Web connection.
   */
  private async initialize(): Promise<void> {
    try {
      const fullHost = this.rpcUrl;
      
      // Create TronWeb instance without private key (read-only operations)
      this.tronWeb = new TronWeb({
        fullHost,
        headers: this.apiKey ? { 'TRON-PRO-API-KEY': this.apiKey } : undefined,
      }) as InstanceType<typeof TronWeb>;

      // Test connection by getting current block
      await this.getCurrentBlock();
      this.isConnected = true;
      this.logger.log(`TRON RPC connected: ${fullHost}`);
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Unknown error';
      this.logger.error(`Failed to connect to TRON RPC: ${errorMessage}`);
      this.isConnected = false;
      this.tronWeb = null;
    }
  }

  /**
   * Check if RPC connection is healthy.
   */
  isHealthy(): boolean {
    return this.isConnected && this.tronWeb !== null;
  }

  /**
   * Get current block information from TRON network.
   */
  async getCurrentBlock(): Promise<TronBlockInfo | null> {
    if (!this.tronWeb) {
      return null;
    }

    try {
      const block = await this.tronWeb.trx.getCurrentBlock();
      this.isConnected = true;
      return block as TronBlockInfo;
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Unknown error';
      this.logger.error(`Failed to get current block: ${errorMessage}`);
      this.isConnected = false;
      return null;
    }
  }

  /**
   * Query transaction information by hash.
   */
  async getTransactionInfo(txHash: string): Promise<TronTransactionInfo | null> {
    if (!this.tronWeb) {
      return null;
    }

    try {
      // Get confirmed transaction info
      const info = await this.tronWeb.trx.getTransactionInfo(txHash);
      
      // If no info or blockNumber is 0, transaction not found or pending
      if (!info || info.blockNumber === 0) {
        return null;
      }

      this.isConnected = true;
      return info as TronTransactionInfo;
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Unknown error';
      this.logger.error(`Failed to get transaction info for ${txHash}: ${errorMessage}`);
      
      // Check if it's a "transaction not found" error
      if (errorMessage.includes('Transaction not found') || 
          errorMessage.includes('transaction info not found')) {
        return null;
      }
      
      this.isConnected = false;
      return null;
    }
  }

  /**
   * Query raw transaction by hash (for getting from/to/amount details).
   */
  async getTransaction(txHash: string): Promise<unknown | null> {
    if (!this.tronWeb) {
      return null;
    }

    try {
      const tx = await this.tronWeb.trx.getTransaction(txHash);
      this.isConnected = true;
      return tx;
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Unknown error';
      this.logger.error(`Failed to get transaction ${txHash}: ${errorMessage}`);
      
      if (errorMessage.includes('Transaction not found')) {
        return null;
      }
      
      this.isConnected = false;
      return null;
    }
  }

  /**
   * Broadcast a signed transaction to the TRON network.
   */
  async broadcastTransaction(signedTx: { [key: string]: unknown }): Promise<{
    success: boolean;
    txid?: string;
    code?: string;
    message?: string;
  }> {
    if (!this.tronWeb) {
      return {
        success: false,
        code: 'RPC_NOT_AVAILABLE',
        message: 'TRON RPC not initialized',
      };
    }

    try {
      const result = await this.tronWeb.trx.broadcast(signedTx as unknown as Parameters<TronWeb['trx']['broadcast']>[0]);
      this.isConnected = true;
      
      if (result.result === true) {
        return {
          success: true,
          txid: result.txid as string,
        };
      } else {
        return {
          success: false,
          code: String(result.code) || 'BROADCAST_FAILED',
          message: result.message 
            ? Buffer.from(result.message as string, 'hex').toString('utf8')
            : 'Broadcast failed',
        };
      }
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Unknown error';
      this.logger.error(`Failed to broadcast transaction: ${errorMessage}`);
      this.isConnected = false;
      
      return {
        success: false,
        code: 'BROADCAST_ERROR',
        message: errorMessage,
      };
    }
  }

  /**
   * Get the USDT TRC20 contract instance.
   */
  getUsdtContract(): { address: string; decimals: number; symbol: string } {
    return {
      address: this.configService.get<string>('TRON_USDT_CONTRACT') ?? 
        'TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t',
      decimals: 6,
      symbol: 'USDT',
    };
  }

  /**
   * Parse TRC20 transfer data from transaction.
   * This is a helper to extract USDT transfer details from raw transaction.
   */
  parseTrc20Transfer(rawTx: unknown): {
    from?: string;
    to?: string;
    amount?: string;
    contractAddress?: string;
  } | null {
    try {
      const tx = rawTx as {
        raw_data?: {
          contract?: Array<{
            parameter?: {
              value?: {
                data?: string;
                contract_address?: string;
                owner_address?: string;
              };
            };
          }>;
        };
      };

      const contract = tx.raw_data?.contract?.[0];
      if (!contract?.parameter?.value?.data) {
        return null;
      }

      const data = contract.parameter.value.data as string;
      const contractAddress = contract.parameter.value.contract_address;
      
      // TRC20 transfer method signature: transfer(address,uint256) = 0xa9059cbb
      if (!data.startsWith('a9059cbb')) {
        return null;
      }

      // Parse recipient address (32 bytes, offset 4 + 12 bytes padding)
      const toHex = data.slice(16, 56);
      const to = this.tronWeb?.address.fromHex('41' + toHex) ?? '';

      // Parse amount (32 bytes)
      const amountHex = data.slice(56, 88);
      const amount = BigInt('0x' + amountHex).toString();

      // Get sender from owner_address
      const fromHex = contract.parameter.value.owner_address ?? '';
      const from = fromHex ? (this.tronWeb?.address.fromHex(fromHex) ?? '') : '';

      return {
        from,
        to,
        amount,
        contractAddress,
      };
    } catch (error) {
      this.logger.error('Failed to parse TRC20 transfer data');
      return null;
    }
  }
}
