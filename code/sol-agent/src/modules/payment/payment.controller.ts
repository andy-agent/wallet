import {
  Body,
  Controller,
  Get,
  HttpCode,
  Param,
  Post,
  Query,
  UseGuards,
} from '@nestjs/common';
import { ApiTags, ApiOperation, ApiBearerAuth } from '@nestjs/swagger';
import { InternalAuthGuard } from '../../common/guards/internal-auth.guard';
import { PaymentService } from './payment.service';
import { DetectPaymentRequestDto } from './dto/detect-payment.request';
import { VerifyTransactionRequestDto } from './dto/verify-transaction.request';

@ApiTags('Payment')
@UseGuards(InternalAuthGuard)
@ApiBearerAuth('x-internal-auth')
@Controller('internal/v1/payment')
export class PaymentController {
  constructor(private readonly paymentService: PaymentService) {}

  @Get(':address/status')
  @ApiOperation({ summary: '查询地址收款状态（调用 Solana RPC）' })
  async getPaymentStatus(
    @Param('address') address: string,
    @Query('networkCode') networkCode?: string,
  ) {
    return this.paymentService.getPaymentStatus(address, networkCode);
  }

  @Post('detect')
  @HttpCode(200)
  @ApiOperation({ summary: '主动检测指定地址收款（调用 Solana RPC）' })
  async detectPayment(@Body() body: DetectPaymentRequestDto) {
    return this.paymentService.detectPayment(body);
  }

  @Post('verify')
  @HttpCode(200)
  @ApiOperation({
    summary: '按签名校验交易是否向目标地址支付了指定 SOL/SPL 金额',
  })
  async verifyTransaction(@Body() body: VerifyTransactionRequestDto) {
    return this.paymentService.verifyTransaction(body);
  }
}
