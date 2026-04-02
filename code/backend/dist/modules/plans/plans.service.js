"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.PlansService = void 0;
const common_1 = require("@nestjs/common");
const BASIC_PLAN_ID = '11111111-1111-1111-1111-111111111111';
const ADVANCED_REGION_ID = '22222222-2222-2222-2222-222222222222';
let PlansService = class PlansService {
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
};
exports.PlansService = PlansService;
exports.PlansService = PlansService = __decorate([
    (0, common_1.Injectable)()
], PlansService);
//# sourceMappingURL=plans.service.js.map