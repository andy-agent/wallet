import { Controller, Get, Query, UseGuards } from '@nestjs/common';
import { AdminAuthGuard } from '../auth/admin-auth.guard';
import { AdminLegalService } from './admin-legal.service';

@Controller('admin/v1/legal-documents')
@UseGuards(AdminAuthGuard)
export class AdminLegalController {
  constructor(private readonly adminLegalService: AdminLegalService) {}

  @Get()
  listLegalDocuments(
    @Query('page') page?: string,
    @Query('pageSize') pageSize?: string,
    @Query('docType') docType?: string,
    @Query('status') status?: string,
  ) {
    return this.adminLegalService.listLegalDocuments({
      page: page ? parseInt(page, 10) : undefined,
      pageSize: pageSize ? parseInt(pageSize, 10) : undefined,
      docType,
      status,
    });
  }
}
