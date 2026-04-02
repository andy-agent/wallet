import { Module } from '@nestjs/common';
import { VpnModule } from '../vpn/vpn.module';
import { ProvisioningService } from './provisioning.service';

@Module({
  imports: [VpnModule],
  providers: [ProvisioningService],
  exports: [ProvisioningService],
})
export class ProvisioningModule {}
