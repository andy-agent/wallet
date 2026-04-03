import { Controller, Get, Query, UseGuards } from '@nestjs/common';
import { AdminAuthGuard } from '../auth/admin-auth.guard';
import { AdminSystemConfigsService } from './admin-system-configs.service';

@Controller('admin/v1/system-configs')
@UseGuards(AdminAuthGuard)
export class AdminSystemConfigsController {
  constructor(private readonly adminSystemConfigsService: AdminSystemConfigsService) {}

  @Get()
  listSystemConfigs(@Query('scope') scope?: string) {
    return this.adminSystemConfigsService.listSystemConfigs({ scope });
  }
}
