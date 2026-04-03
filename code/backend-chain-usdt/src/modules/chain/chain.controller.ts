import {
  Body,
  Controller,
  Get,
  Headers,
  Param,
  Post,
  Query,
  UseGuards,
} from '@nestjs/common';
import { InternalAuthGuard } from '../../common/guards/internal-auth.guard';
import { ChainService } from './chain.service';

/**
 * DTO for transaction broadcast request
 */
interface BroadcastRequestDto {
  /** Signed transaction hex string */
  signedTx: string;
  /** Optional metadata for tracking */
  metadata?: Record<string, unknown>;
}

/**
 * DTO for batch transaction query
 */
interface BatchQueryRequestDto {
  txHashes: string[];
}

/**
 * Chain controller exposing TRON/TRC20 operations.
 * 
 * AUTHENTICATION: All endpoints require InternalAuthGuard
 * This ensures only authorized services (main backend API) can access
 * chain operations.
 * 
 * CAPABILITIES:
 * - GET /api/v1/chain/tx/:hash - Query single transaction
 * - POST /api/v1/chain/tx/batch - Query multiple transactions
 * - POST /api/v1/chain/broadcast - Broadcast signed transaction
 * - GET /api/v1/chain/block/current - Get current block info
 * - GET /api/v1/chain/address/validate - Validate TRON address
 * - GET /api/v1/chain/contract/usdt - Get USDT contract info
 */
@Controller('v1/chain')
@UseGuards(InternalAuthGuard)
export class ChainController {
  constructor(private readonly chainService: ChainService) {}

  /**
   * Query transaction status by hash.
   * 
   * @param txHash - Transaction hash
   * @returns Transaction details
   * 
   * Usage: Main backend calls this to check payment status
   */
  @Get('tx/:hash')
  async getTransaction(@Param('hash') txHash: string) {
    const result = await this.chainService.queryTransaction(txHash);
    return {
      found: result !== null,
      transaction: result,
    };
  }

  /**
   * Query multiple transactions in batch.
   * More efficient than multiple single queries.
   * 
   * @param body - Batch query request
   * @returns Map of txHash to transaction result
   * 
   * Usage: Worker batch-checks pending orders
   */
  @Post('tx/batch')
  async getTransactionsBatch(@Body() body: BatchQueryRequestDto) {
    const results = await this.chainService.queryTransactionsBatch(body.txHashes);
    
    // Convert Map to object for JSON serialization
    const transactions: Record<string, unknown> = {};
    results.forEach((value, key) => {
      transactions[key] = value;
    });

    return {
      count: body.txHashes.length,
      found: Array.from(results.values()).filter(r => r !== null).length,
      transactions,
    };
  }

  /**
   * Broadcast a signed transaction.
   * 
   * @param body - Broadcast request with signedTx
   * @returns Broadcast result
   * 
   * IMPORTANT: This endpoint only broadcasts PRE-SIGNED transactions.
   * It does NOT sign transactions - signing happens client-side or in main backend.
   * 
   * Usage: Main backend submits withdrawal transactions
   */
  @Post('broadcast')
  async broadcastTransaction(@Body() body: BroadcastRequestDto) {
    return this.chainService.broadcastTransaction(body.signedTx);
  }

  /**
   * Get current block information.
   * 
   * @returns Current block details
   * 
   * Usage: Main backend checks chain sync status
   */
  @Get('block/current')
  async getCurrentBlock() {
    return this.chainService.getCurrentBlock();
  }

  /**
   * Validate a TRON address format.
   * 
   * @param address - Address to validate
   * @returns Validation result
   * 
   * Usage: Main backend validates user withdrawal addresses
   */
  @Get('address/validate')
  validateAddress(@Query('address') address: string) {
    const isValid = this.chainService.validateAddress(address);
    return {
      address,
      valid: isValid,
      type: isValid ? 'tron' : 'invalid',
    };
  }

  /**
   * Get USDT TRC20 contract information.
   * 
   * @returns Contract address and metadata
   * 
   * Usage: Main backend displays contract info to users
   */
  @Get('contract/usdt')
  getUsdtContract() {
    return {
      ...this.chainService.getContractInfo(),
      network: 'tron-mainnet',
      standard: 'TRC20',
    };
  }

  /**
   * Get service capabilities and limits.
   * 
   * @returns Service configuration
   * 
   * Usage: Main backend discovers available features
   */
  @Get('capabilities')
  getCapabilities() {
    return {
      network: 'tron',
      chainId: '728126428',
      supportedTokens: ['USDT', 'TRX'],
      capabilities: {
        query: true,
        broadcast: true,
        batchQuery: true,
        addressValidation: true,
      },
      limits: {
        maxBatchSize: 100,
        rateLimitPerMinute: 1000,
      },
      mockMode: this.chainService.isMockMode(),
    };
  }
}
