import { Module } from '@nestjs/common';
import { AuthModule } from '../auth/auth.module';
import { ReferralModule } from '../referral/referral.module';
import { WithdrawalsController } from './withdrawals.controller';
import { WithdrawalsService } from './withdrawals.service';

@Module({
  imports: [AuthModule, ReferralModule],
  controllers: [WithdrawalsController],
  providers: [WithdrawalsService],
})
export class WithdrawalsModule {}
