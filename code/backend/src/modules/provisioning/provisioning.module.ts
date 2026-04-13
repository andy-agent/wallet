import { Module } from '@nestjs/common';
import { MarzbanModule } from '../marzban/marzban.module';
import { ReferralModule } from '../referral/referral.module';
import { VpnModule } from '../vpn/vpn.module';
import { ProvisioningService } from './provisioning.service';

@Module({
  imports: [VpnModule, ReferralModule, MarzbanModule],
  providers: [ProvisioningService],
  exports: [ProvisioningService],
})
export class ProvisioningModule {}
