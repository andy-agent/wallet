import {
  Injectable,
  NotFoundException,
  ServiceUnavailableException,
} from '@nestjs/common';
import {
  PlanMutationInput,
  PostgresDataAccessService,
} from '../../database/postgres-data-access.service';
import { PlansService } from '../../plans/plans.service';
import { UpsertAdminPlanRequestDto } from './dto/upsert-admin-plan.request';

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

  async createPlan(body: UpsertAdminPlanRequestDto) {
    if (!this.postgresDataAccessService.isEnabled()) {
      throw new ServiceUnavailableException({
        code: 'PLAN_MANAGEMENT_UNAVAILABLE',
        message: 'Plan management requires PostgreSQL backend',
      });
    }

    const result = await this.postgresDataAccessService.createPlan(
      this.toPlanMutationInput(body),
    );
    if (!result) {
      throw new ServiceUnavailableException({
        code: 'PLAN_CREATE_FAILED',
        message: 'Failed to create plan',
      });
    }
    return result;
  }

  async updatePlan(planId: string, body: UpsertAdminPlanRequestDto) {
    if (!this.postgresDataAccessService.isEnabled()) {
      throw new ServiceUnavailableException({
        code: 'PLAN_MANAGEMENT_UNAVAILABLE',
        message: 'Plan management requires PostgreSQL backend',
      });
    }

    const existing = await this.postgresDataAccessService.getPlanById(planId);
    if (!existing) {
      throw new NotFoundException({
        code: 'PLAN_NOT_FOUND',
        message: 'Plan not found',
      });
    }

    const result = await this.postgresDataAccessService.updatePlan(
      planId,
      this.toPlanMutationInput(body),
    );
    if (!result) {
      throw new ServiceUnavailableException({
        code: 'PLAN_UPDATE_FAILED',
        message: 'Failed to update plan',
      });
    }
    return result;
  }

  private toPlanMutationInput(body: UpsertAdminPlanRequestDto): PlanMutationInput {
    return {
      planCode: body.planCode.trim(),
      name: body.name.trim(),
      description: body.description?.trim() || null,
      billingCycleMonths: body.billingCycleMonths,
      priceUsd: body.priceUsd.trim(),
      isUnlimitedTraffic: body.isUnlimitedTraffic,
      maxActiveSessions: body.maxActiveSessions,
      regionAccessPolicy: body.regionAccessPolicy,
      includesAdvancedRegions: body.includesAdvancedRegions,
      allowedRegionIds: body.allowedRegionIds ?? [],
      displayOrder: body.displayOrder,
      status: body.status,
    };
  }
}
