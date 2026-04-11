import { Injectable } from '@nestjs/common';
import { PostgresDataAccessService } from '../database/postgres-data-access.service';

const TEST_BASIC_PLAN_ID = '11111111-1111-1111-1111-111111111111';
const TEST_ADVANCED_REGION_ID = '22222222-2222-2222-2222-222222222222';

@Injectable()
export class PlansService {
  constructor(
    private readonly postgresDataAccessService: PostgresDataAccessService,
  ) {}

  async listPlans() {
    if (this.postgresDataAccessService.isEnabled()) {
      const result = await this.postgresDataAccessService.listPlans({
        page: 1,
        pageSize: 100,
        status: 'ACTIVE',
      });
      return {
        items: result.items,
      };
    }

    if (process.env.NODE_ENV === 'test') {
      return {
        items: [
          {
            planId: TEST_BASIC_PLAN_ID,
            planCode: 'BASIC_1M',
            name: '基础版-1个月',
            description: '基础线路月付套餐',
            billingCycleMonths: 1,
            priceUsd: '9.99',
            maxActiveSessions: 1,
            regionAccessPolicy: 'BASIC_ONLY',
            includesAdvancedRegions: false,
            allowedRegionIds: [TEST_ADVANCED_REGION_ID],
            displayOrder: 1,
            status: 'ACTIVE',
          },
        ],
      };
    }

    return {
      items: [],
    };
  }
}
