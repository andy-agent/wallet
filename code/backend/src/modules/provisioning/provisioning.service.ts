import { Injectable } from '@nestjs/common';
import { ReferralService } from '../referral/referral.service';
import { VpnService } from '../vpn/vpn.service';

interface ProvisionOrderInput {
  accountId: string;
  planCode: string;
}

@Injectable()
export class ProvisioningService {
  constructor(
    private readonly vpnService: VpnService,
    private readonly referralService: ReferralService,
  ) {}

  provisionPaidOrder(
    input: ProvisionOrderInput & {
      orderNo: string;
      sourceAssetCode: 'SOL' | 'USDT';
      sourceAmount: string;
    },
  ) {
    const subscription = this.vpnService.activateSubscription(
      input.accountId,
      input.planCode,
    );
    this.referralService.recordCompletedOrder({
      accountId: input.accountId,
      orderNo: input.orderNo,
      sourceAssetCode: input.sourceAssetCode,
      sourceAmount: input.sourceAmount,
    });

    return {
      subscriptionId: subscription.subscriptionId,
      status: 'COMPLETED' as const,
    };
  }
}
