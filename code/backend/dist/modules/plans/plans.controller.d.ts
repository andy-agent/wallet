import { PlansService } from './plans.service';
export declare class PlansController {
    private readonly plansService;
    constructor(plansService: PlansService);
    listPlans(): {
        items: {
            planId: string;
            planCode: string;
            name: string;
            description: string;
            billingCycleMonths: number;
            priceUsd: string;
            maxActiveSessions: number;
            regionAccessPolicy: string;
            includesAdvancedRegions: boolean;
            allowedRegionIds: string[];
            displayOrder: number;
            status: string;
        }[];
    };
}
