import { Controller, Get, Header } from '@nestjs/common';
import { PUBLIC_EDGE_REFERENCE_CACHE_CONTROL } from '../../common/http/public-cache-control';
import { PlansService } from './plans.service';

@Controller('client/v1/plans')
export class PlansController {
  constructor(private readonly plansService: PlansService) {}

  @Get()
  @Header('Cache-Control', PUBLIC_EDGE_REFERENCE_CACHE_CONTROL)
  listPlans() {
    return this.plansService.listPlans();
  }
}
