import { Module } from '@nestjs/common';
import { AuthModule } from '../auth/auth.module';
import { VpnController } from './vpn.controller';
import { VpnService } from './vpn.service';
import { SubscriptionController } from './subscription.controller';

@Module({
  imports: [AuthModule],
  controllers: [VpnController, SubscriptionController],
  providers: [VpnService],
  exports: [VpnService],
})
export class VpnModule {}
