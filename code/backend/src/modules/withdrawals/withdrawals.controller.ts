import { Body, Controller, Get, Headers, Param, Post, Query } from '@nestjs/common';
import { CreateWithdrawalRequestDto } from './dto/create-withdrawal.request';
import { WithdrawalsService } from './withdrawals.service';

@Controller('client/v1/withdrawals')
export class WithdrawalsController {
  constructor(private readonly withdrawalsService: WithdrawalsService) {}

  @Post()
  createWithdrawal(
    @Headers('authorization') authorization: string | undefined,
    @Headers('x-idempotency-key') idempotencyKey: string | undefined,
    @Body() body: CreateWithdrawalRequestDto,
  ) {
    return this.withdrawalsService.createWithdrawal(
      this.extractBearer(authorization),
      body,
      idempotencyKey ?? '',
    );
  }

  @Get()
  listWithdrawals(
    @Headers('authorization') authorization?: string,
    @Query('status') status?: string,
  ) {
    return this.withdrawalsService.listWithdrawals(
      this.extractBearer(authorization),
      status,
    );
  }

  @Get(':requestNo')
  getWithdrawal(
    @Headers('authorization') authorization: string | undefined,
    @Param('requestNo') requestNo: string,
  ) {
    return this.withdrawalsService.getWithdrawal(
      this.extractBearer(authorization),
      requestNo,
    );
  }

  private extractBearer(authorization?: string) {
    if (!authorization?.startsWith('Bearer ')) {
      return '';
    }
    return authorization.slice('Bearer '.length);
  }
}
