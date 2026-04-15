import { Body, Controller, Get, Param, Post, Put, Query, UseGuards } from '@nestjs/common';
import { AdminAuthGuard } from '../auth/admin-auth.guard';
import { AdminPlansService } from './admin-plans.service';
import { UpsertAdminPlanRequestDto } from './dto/upsert-admin-plan.request';

@Controller('admin/v1/plans')
@UseGuards(AdminAuthGuard)
export class AdminPlansController {
  constructor(private readonly adminPlansService: AdminPlansService) {}

  @Get()
  listPlans(
    @Query('page') page?: string,
    @Query('pageSize') pageSize?: string,
    @Query('status') status?: string,
  ) {
    return this.adminPlansService.listPlans({
      page: page ? parseInt(page, 10) : undefined,
      pageSize: pageSize ? parseInt(pageSize, 10) : undefined,
      status,
    });
  }

  @Post()
  createPlan(@Body() body: UpsertAdminPlanRequestDto) {
    return this.adminPlansService.createPlan(body);
  }

  @Put(':planId')
  updatePlan(
    @Param('planId') planId: string,
    @Body() body: UpsertAdminPlanRequestDto,
  ) {
    return this.adminPlansService.updatePlan(planId, body);
  }
}
