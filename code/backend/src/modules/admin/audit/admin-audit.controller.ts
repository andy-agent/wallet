import { Controller, Get, Query, UseGuards } from '@nestjs/common';
import { AdminAuthGuard } from '../auth/admin-auth.guard';
import { AdminAuditService } from './admin-audit.service';

@Controller('admin/v1/audit-logs')
@UseGuards(AdminAuthGuard)
export class AdminAuditController {
  constructor(private readonly adminAuditService: AdminAuditService) {}

  @Get()
  listAuditLogs(
    @Query('page') page?: string,
    @Query('pageSize') pageSize?: string,
    @Query('module') module?: string,
    @Query('actorType') actorType?: string,
    @Query('targetType') targetType?: string,
    @Query('dateRange') dateRange?: string,
  ) {
    return this.adminAuditService.listAuditLogs({
      page: page ? parseInt(page, 10) : undefined,
      pageSize: pageSize ? parseInt(pageSize, 10) : undefined,
      module,
      actorType,
      targetType,
      dateRange,
    });
  }
}
