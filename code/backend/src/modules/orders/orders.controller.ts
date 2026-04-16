import { Body, Controller, Get, Headers, Param, Post, Query } from '@nestjs/common';
import { CreateOrderRequestDto } from './dto/create-order.request';
import { RefreshOrderStatusRequestDto } from './dto/refresh-order-status.request';
import { SubmitClientTxRequestDto } from './dto/submit-client-tx.request';
import { OrdersService } from './orders.service';

@Controller('client/v1/orders')
export class OrdersController {
  constructor(private readonly ordersService: OrdersService) {}

  @Get()
  listOrders(
    @Headers('authorization') authorization: string | undefined,
    @Query('page') page?: string,
    @Query('pageSize') pageSize?: string,
    @Query('orderNo') orderNo?: string,
    @Query('status') status?: string,
  ) {
    return this.ordersService.listOwnedOrders(this.extractBearer(authorization), {
      page: page ? parseInt(page, 10) : undefined,
      pageSize: pageSize ? parseInt(pageSize, 10) : undefined,
      orderNo,
      status,
    });
  }

  @Post()
  createOrder(
    @Headers('authorization') authorization: string | undefined,
    @Headers('x-idempotency-key') idempotencyKey: string | undefined,
    @Body() body: CreateOrderRequestDto,
  ) {
    return this.ordersService.createOrder(
      this.extractBearer(authorization),
      body,
      idempotencyKey ?? '',
    );
  }

  @Get(':orderNo')
  getOrder(
    @Headers('authorization') authorization: string | undefined,
    @Param('orderNo') orderNo: string,
  ) {
    return this.ordersService.getOrder(this.extractBearer(authorization), orderNo);
  }

  @Get(':orderNo/payment-target')
  getPaymentTarget(
    @Headers('authorization') authorization: string | undefined,
    @Param('orderNo') orderNo: string,
  ) {
    return this.ordersService.getPaymentTarget(this.extractBearer(authorization), orderNo);
  }

  @Post(':orderNo/submit-client-tx')
  submitClientTx(
    @Headers('authorization') authorization: string | undefined,
    @Param('orderNo') orderNo: string,
    @Body() body: SubmitClientTxRequestDto,
  ) {
    return this.ordersService.submitClientTx(
      this.extractBearer(authorization),
      orderNo,
      body,
    );
  }

  @Post(':orderNo/refresh-status')
  async refreshStatus(
    @Headers('authorization') authorization: string | undefined,
    @Param('orderNo') orderNo: string,
    @Body() body: RefreshOrderStatusRequestDto,
  ) {
    return this.ordersService.refreshStatus(
      this.extractBearer(authorization),
      orderNo,
      body,
    );
  }

  private extractBearer(authorization?: string) {
    if (!authorization?.startsWith('Bearer ')) {
      return '';
    }
    return authorization.slice('Bearer '.length);
  }
}
