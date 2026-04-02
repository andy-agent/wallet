import { Module } from '@nestjs/common';
import { ReferralModule } from '../referral/referral.module';
import { VpnModule } from '../vpn/vpn.module';
import { ProvisioningService } from './provisioning.service';

@Module({
  imports: [VpnModule, ReferralModule],
  providers: [ProvisioningService],
  exports: [ProvisioningService],
})
export class ProvisioningModule {}
