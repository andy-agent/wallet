import { Injectable } from '@nestjs/common';
import { PostgresDataAccessService } from '../../database/postgres-data-access.service';
import { PlansService } from '../../plans/plans.service';

@Injectable()
export class AdminPlansService {
  constructor(
    private readonly plansService: PlansService,
    private readonly postgresDataAccessService: PostgresDataAccessService,
  ) {}

  async listPlans(params: { page?: number; pageSize?: number; status?: string }) {
    if (this.postgresDataAccessService.isEnabled()) {
      const result = await this.postgresDataAccessService.listPlans(params);
      return result;
    }

    const result = await this.plansService.listPlans();
    let items = result.items;

    if (params.status) {
      items = items.filter(
        (plan) => (plan as { status?: string }).status === params.status,
      );
    }

    const page = Math.max(1, params.page ?? 1);
    const pageSize = Math.min(
      100,
      Math.max(1, params.pageSize ?? items.length ?? 20),
    );
    const total = items.length;
    const start = (page - 1) * pageSize;

    return {
      items: items.slice(start, start + pageSize),
      page,
      pageSize,
      total,
    };
  }
}
