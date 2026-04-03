import { Controller, Get, Query, UseGuards } from '@nestjs/common';
import { AdminAuthGuard } from '../auth/admin-auth.guard';
import { AdminPlansService } from './admin-plans.service';

@Controller('admin/v1/plans')
@UseGuards(AdminAuthGuard)
export class AdminPlansController {
  constructor(private readonly adminPlansService: AdminPlansService) {}

  @Get()
  listPlans(@Query('status') status?: string) {
    return this.adminPlansService.listPlans({ status });
  }
}
