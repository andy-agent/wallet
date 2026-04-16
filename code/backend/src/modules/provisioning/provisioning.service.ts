import { Injectable } from '@nestjs/common';
import { MarzbanService } from '../marzban/marzban.service';
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
    private readonly marzbanService: MarzbanService,
    private readonly referralService: ReferralService,
  ) {}

  async provisionPaidOrder(
    input: ProvisionOrderInput & {
      orderNo: string;
      sourceAssetCode: string;
      sourceAmount: string;
    },
  ) {
    const subscription = await this.vpnService.activateSubscription(
      input.accountId,
      input.planCode,
      input.orderNo,
    );
    const marzbanUser = await this.marzbanService.ensureUserForSubscription({
      subscriptionId: subscription.subscriptionId,
      existingUsername: subscription.marzbanUsername,
      expireAt: subscription.expireAt,
      isUnlimitedTraffic: subscription.isUnlimitedTraffic,
    });
    await this.vpnService.attachMarzbanAccess(input.accountId, {
      marzbanUsername: marzbanUser.username,
      subscriptionUrl: marzbanUser.subscriptionUrl,
      expireAt: marzbanUser.expireAt,
    });
    this.referralService.recordCompletedOrder({
      accountId: input.accountId,
      orderNo: input.orderNo,
      sourceAssetCode: input.sourceAssetCode,
      sourceAmount: input.sourceAmount,
    });

    return {
      subscriptionId: subscription.subscriptionId,
      marzbanUsername: marzbanUser.username,
      subscriptionUrl: marzbanUser.subscriptionUrl,
      status: 'COMPLETED' as const,
    };
  }
}
