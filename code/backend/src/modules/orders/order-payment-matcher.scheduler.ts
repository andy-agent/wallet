import { Injectable, Logger } from '@nestjs/common';
import { Interval } from '@nestjs/schedule';
import { OrderPaymentMatcherService } from './order-payment-matcher.service';

@Injectable()
export class OrderPaymentMatcherScheduler {
  private readonly logger = new Logger(OrderPaymentMatcherScheduler.name);

  constructor(
    private readonly orderPaymentMatcherService: OrderPaymentMatcherService,
  ) {}

  @Interval(15000)
  async runTick() {
    try {
      await this.orderPaymentMatcherService.scanActiveContextsOnce();
    } catch (error) {
      this.logger.warn(
        'Order payment matcher tick failed',
        error instanceof Error ? error.message : String(error),
      );
    }
  }
}
