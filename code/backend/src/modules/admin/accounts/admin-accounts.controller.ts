import { Controller, Get, Param, Query, UseGuards } from '@nestjs/common';
import { AdminAuthGuard } from '../auth/admin-auth.guard';
import { AdminAccountsService } from './admin-accounts.service';

@Controller('admin/v1/accounts')
@UseGuards(AdminAuthGuard)
export class AdminAccountsController {
  constructor(private readonly adminAccountsService: AdminAccountsService) {}

  @Get()
  listAccounts(
    @Query('page') page?: string,
    @Query('pageSize') pageSize?: string,
    @Query('email') email?: string,
    @Query('status') status?: string,
  ) {
    return this.adminAccountsService.listAccounts({
      page: page ? parseInt(page, 10) : undefined,
      pageSize: pageSize ? parseInt(pageSize, 10) : undefined,
      email,
      status,
    });
  }

  @Get(':accountId')
  getAccountDetail(@Param('accountId') accountId: string) {
    return this.adminAccountsService.getAccountDetail(accountId);
  }
}
