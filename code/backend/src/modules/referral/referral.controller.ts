import { Body, Controller, Get, Headers, Post, Query } from '@nestjs/common';
import { ReferralBindRequestDto } from './dto/referral-bind.request';
import { ReferralService } from './referral.service';

@Controller('client/v1')
export class ReferralController {
  constructor(private readonly referralService: ReferralService) {}

  @Get('referral/overview')
  getOverview(@Headers('authorization') authorization?: string) {
    return this.referralService.getOverview(this.extractBearer(authorization));
  }

  @Get('referral/share-context')
  getShareContext(@Headers('authorization') authorization?: string) {
    return this.referralService.getShareContext(this.extractBearer(authorization));
  }

  @Post('referral/bind')
  bind(
    @Headers('authorization') authorization: string | undefined,
    @Body() body: ReferralBindRequestDto,
  ) {
    return this.referralService.bind(
      this.extractBearer(authorization),
      body.referralCode,
    );
  }

  @Get('commissions/summary')
  getSummary(@Headers('authorization') authorization?: string) {
    return this.referralService.getSummary(this.extractBearer(authorization));
  }

  @Get('commissions/ledger')
  getLedger(
    @Headers('authorization') authorization?: string,
    @Query('status') status?: string,
  ) {
    return this.referralService.getLedger(this.extractBearer(authorization), status);
  }

  private extractBearer(authorization?: string) {
    if (!authorization?.startsWith('Bearer ')) {
      return '';
    }
    return authorization.slice('Bearer '.length);
  }
}
