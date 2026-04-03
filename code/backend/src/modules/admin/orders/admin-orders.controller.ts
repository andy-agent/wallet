import { Controller, Get, Param, Query, UseGuards } from '@nestjs/common';
import { AdminAuthGuard } from '../auth/admin-auth.guard';
import { AdminOrdersService } from './admin-orders.service';

@Controller('admin/v1/orders')
@UseGuards(AdminAuthGuard)
export class AdminOrdersController {
  constructor(private readonly adminOrdersService: AdminOrdersService) {}

  @Get()
  listOrders(
    @Query('page') page?: string,
    @Query('pageSize') pageSize?: string,
    @Query('orderNo') orderNo?: string,
    @Query('status') status?: string,
    @Query('email') email?: string,
  ) {
    return this.adminOrdersService.listOrders({
      page: page ? parseInt(page, 10) : undefined,
      pageSize: pageSize ? parseInt(pageSize, 10) : undefined,
      orderNo,
      status,
      email,
    });
  }

  @Get(':orderNo')
  getOrderDetail(@Param('orderNo') orderNo: string) {
    return this.adminOrdersService.getOrderDetail(orderNo);
  }
}
