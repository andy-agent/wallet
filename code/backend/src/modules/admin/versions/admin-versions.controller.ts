import { Controller, Get, Query, UseGuards } from '@nestjs/common';
import { AdminAuthGuard } from '../auth/admin-auth.guard';
import { AdminVersionsService } from './admin-versions.service';

@Controller('admin/v1/app-versions')
@UseGuards(AdminAuthGuard)
export class AdminVersionsController {
  constructor(private readonly adminVersionsService: AdminVersionsService) {}

  @Get()
  listAppVersions(
    @Query('page') page?: string,
    @Query('pageSize') pageSize?: string,
    @Query('status') status?: string,
    @Query('channel') channel?: string,
  ) {
    return this.adminVersionsService.listAppVersions({
      page: page ? parseInt(page, 10) : undefined,
      pageSize: pageSize ? parseInt(pageSize, 10) : undefined,
      status,
      channel,
    });
  }
}
