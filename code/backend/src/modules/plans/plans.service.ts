import { Injectable } from '@nestjs/common';

const BASIC_PLAN_ID = '11111111-1111-1111-1111-111111111111';
const ADVANCED_REGION_ID = '22222222-2222-2222-2222-222222222222';

@Injectable()
export class PlansService {
  listPlans() {
    return {
      items: [
        {
          planId: BASIC_PLAN_ID,
          planCode: 'BASIC_1M',
          name: '基础版-1个月',
          description: '基础线路月付套餐',
          billingCycleMonths: 1,
          priceUsd: '9.99',
          maxActiveSessions: 1,
          regionAccessPolicy: 'BASIC_ONLY',
          includesAdvancedRegions: false,
          allowedRegionIds: [ADVANCED_REGION_ID],
          displayOrder: 1,
          status: 'ACTIVE',
        },
      ],
    };
  }
}
