import { Module } from '@nestjs/common';
import { AuthModule } from '../auth/auth.module';
import { VpnModule } from '../vpn/vpn.module';
import { AccountsController } from './accounts.controller';
import { AccountsService } from './accounts.service';

@Module({
  imports: [AuthModule, VpnModule],
  controllers: [AccountsController],
  providers: [AccountsService],
})
export class AccountsModule {}
