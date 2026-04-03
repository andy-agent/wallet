import { Controller, Get, Query, UseGuards } from '@nestjs/common';
import { AdminAuthGuard } from '../auth/admin-auth.guard';
import { AdminWithdrawalsService } from './admin-withdrawals.service';

@Controller('admin/v1/withdrawals')
@UseGuards(AdminAuthGuard)
export class AdminWithdrawalsController {
  constructor(private readonly adminWithdrawalsService: AdminWithdrawalsService) {}

  @Get()
  listWithdrawals(
    @Query('page') page?: string,
    @Query('pageSize') pageSize?: string,
    @Query('status') status?: string,
    @Query('accountEmail') accountEmail?: string,
  ) {
    return this.adminWithdrawalsService.listWithdrawals({
      page: page ? parseInt(page, 10) : undefined,
      pageSize: pageSize ? parseInt(pageSize, 10) : undefined,
      status,
      accountEmail,
    });
  }
}
