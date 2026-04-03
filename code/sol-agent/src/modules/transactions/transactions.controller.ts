import {
  Controller,
  Get,
  Param,
  Query,
  UseGuards,
} from '@nestjs/common';
import { ApiTags, ApiOperation, ApiBearerAuth } from '@nestjs/swagger';
import { InternalAuthGuard } from '../../common/guards/internal-auth.guard';
import { TransactionsService, TransactionStatusResponse } from './transactions.service';

@ApiTags('Transactions')
@UseGuards(InternalAuthGuard)
@ApiBearerAuth('x-internal-auth')
@Controller('v1/transactions')
export class TransactionsController {
  constructor(private readonly transactionsService: TransactionsService) {}

  @Get(':signature')
  @ApiOperation({
    summary: 'Get transaction status by signature',
    description:
      'Query transaction status from Solana RPC. Returns pending if not found, confirmed/finalized if successful, or failed if transaction had errors.',
  })
  async getTransactionStatus(
    @Param('signature') signature: string,
    @Query('network') network?: 'mainnet' | 'devnet',
  ): Promise<TransactionStatusResponse> {
    // Map 'mainnet'/'devnet' to internal network code format
    const networkCode = network
      ? `solana-${network}`
      : 'solana-mainnet';

    return this.transactionsService.getTransactionStatus(signature, networkCode);
  }
}
