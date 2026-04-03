import { Injectable } from '@nestjs/common';
import { PlansService } from '../../plans/plans.service';

@Injectable()
export class AdminPlansService {
  constructor(private readonly plansService: PlansService) {}

  listPlans(params: { status?: string }) {
    const result = this.plansService.listPlans();

    let items = result.items;

    if (params.status) {
      items = items.filter((p) => p.status === params.status);
    }

    return {
      items,
      page: {
        page: 1,
        pageSize: items.length,
        total: items.length,
      },
    };
  }
}
