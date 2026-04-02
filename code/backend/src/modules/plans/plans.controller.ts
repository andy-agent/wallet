import { Controller, Get } from '@nestjs/common';
import { PlansService } from './plans.service';

@Controller('client/v1/plans')
export class PlansController {
  constructor(private readonly plansService: PlansService) {}

  @Get()
  listPlans() {
    return this.plansService.listPlans();
  }
}
