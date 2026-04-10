import { Injectable } from '@nestjs/common';
import { WithdrawalsService } from '../../withdrawals/withdrawals.service';

@Injectable()
export class AdminWithdrawalsService {
  constructor(private readonly withdrawalsService: WithdrawalsService) {}

  async listWithdrawals(params: {
    page?: number;
    pageSize?: number;
    status?: string;
    accountEmail?: string;
  }) {
    return this.withdrawalsService.listWithdrawalsForAdmin(params);
  }
}
