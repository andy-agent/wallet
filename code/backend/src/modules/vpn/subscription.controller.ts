import { Controller, Get, Headers } from '@nestjs/common';
import { VpnService } from './vpn.service';

@Controller('client/v1/subscriptions')
export class SubscriptionController {
  constructor(private readonly vpnService: VpnService) {}

  @Get('current')
  getCurrent(@Headers('authorization') authorization?: string) {
    return this.vpnService.getCurrentSubscription(this.extractBearer(authorization));
  }

  private extractBearer(authorization?: string) {
    if (!authorization?.startsWith('Bearer ')) {
      return '';
    }
    return authorization.slice('Bearer '.length);
  }
}
