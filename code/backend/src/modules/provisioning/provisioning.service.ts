import { Injectable } from '@nestjs/common';
import { VpnService } from '../vpn/vpn.service';

interface ProvisionOrderInput {
  accountId: string;
  planCode: string;
}

@Injectable()
export class ProvisioningService {
  constructor(private readonly vpnService: VpnService) {}

  provisionPaidOrder(input: ProvisionOrderInput) {
    const subscription = this.vpnService.activateSubscription(
      input.accountId,
      input.planCode,
    );

    return {
      subscriptionId: subscription.subscriptionId,
      status: 'COMPLETED' as const,
    };
  }
}
