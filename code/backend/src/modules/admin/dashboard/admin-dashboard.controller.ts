import { Controller, Get, UseGuards } from '@nestjs/common';
import { AdminAuthGuard } from '../auth/admin-auth.guard';
import { AdminDashboardService } from './admin-dashboard.service';

@Controller('admin/v1/dashboard')
@UseGuards(AdminAuthGuard)
export class AdminDashboardController {
  constructor(private readonly adminDashboardService: AdminDashboardService) {}

  @Get('summary')
  getSummary() {
    return this.adminDashboardService.getSummary();
  }
}
