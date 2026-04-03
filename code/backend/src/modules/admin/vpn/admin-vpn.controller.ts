import { Controller, Get, Query, UseGuards } from '@nestjs/common';
import { AdminAuthGuard } from '../auth/admin-auth.guard';
import { AdminVpnService } from './admin-vpn.service';

@Controller('admin/v1/vpn')
@UseGuards(AdminAuthGuard)
export class AdminVpnController {
  constructor(private readonly adminVpnService: AdminVpnService) {}

  @Get('regions')
  listRegions(
    @Query('page') page?: string,
    @Query('pageSize') pageSize?: string,
    @Query('tier') tier?: string,
    @Query('status') status?: string,
  ) {
    return this.adminVpnService.listRegions({
      page: page ? parseInt(page, 10) : undefined,
      pageSize: pageSize ? parseInt(pageSize, 10) : undefined,
      tier,
      status,
    });
  }

  @Get('nodes')
  listNodes(
    @Query('page') page?: string,
    @Query('pageSize') pageSize?: string,
    @Query('regionId') regionId?: string,
    @Query('status') status?: string,
    @Query('healthStatus') healthStatus?: string,
  ) {
    return this.adminVpnService.listNodes({
      page: page ? parseInt(page, 10) : undefined,
      pageSize: pageSize ? parseInt(pageSize, 10) : undefined,
      regionId,
      status,
      healthStatus,
    });
  }
}
