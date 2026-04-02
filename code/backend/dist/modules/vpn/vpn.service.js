"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.VpnService = void 0;
const common_1 = require("@nestjs/common");
const crypto_1 = require("crypto");
const auth_service_1 = require("../auth/auth.service");
const BASIC_REGION_ID = '33333333-3333-3333-3333-333333333333';
const ADVANCED_REGION_ID = '22222222-2222-2222-2222-222222222222';
let VpnService = class VpnService {
    constructor(authService) {
        this.authService = authService;
        this.subscriptionsByAccountId = new Map();
    }
    getCurrentSubscription(accessToken) {
        const account = this.authService.getMe(accessToken);
        return this.getSubscriptionByAccountId(account.accountId);
    }
    listRegions(accessToken) {
        const account = this.authService.getMe(accessToken);
        const subscription = this.getSubscriptionByAccountId(account.accountId);
        if (subscription.status !== 'ACTIVE') {
            throw new common_1.ForbiddenException({
                code: 'SUBSCRIPTION_REQUIRED',
                message: 'Subscription required',
            });
        }
        return {
            items: [
                {
                    regionId: BASIC_REGION_ID,
                    regionCode: 'JP_BASIC',
                    displayName: '日本-基础线路',
                    tier: 'BASIC',
                    status: 'ACTIVE',
                    isAllowed: true,
                    remark: '基础可用区域',
                },
                {
                    regionId: ADVANCED_REGION_ID,
                    regionCode: 'US_LOW_LATENCY',
                    displayName: '美国-低延迟',
                    tier: 'ADVANCED',
                    status: 'ACTIVE',
                    isAllowed: false,
                    remark: '当前套餐无权限',
                },
            ],
        };
    }
    issueConfig(accessToken, params) {
        const session = this.authService.getSessionSummary(accessToken);
        const account = this.authService.getMe(accessToken);
        const subscription = this.getSubscriptionByAccountId(account.accountId);
        if (subscription.status !== 'ACTIVE') {
            throw new common_1.ForbiddenException({
                code: 'SUBSCRIPTION_REQUIRED',
                message: 'Subscription required',
            });
        }
        const regions = this.listRegions(accessToken).items;
        const region = regions.find((item) => item.regionCode === params.regionCode);
        if (!region) {
            throw new common_1.ConflictException({
                code: 'VPN_REGION_UNAVAILABLE',
                message: 'Region unavailable',
            });
        }
        if (region.status !== 'ACTIVE') {
            throw new common_1.ConflictException({
                code: 'VPN_REGION_UNAVAILABLE',
                message: 'Region unavailable',
            });
        }
        if (!region.isAllowed) {
            throw new common_1.ForbiddenException({
                code: 'VPN_REGION_FORBIDDEN',
                message: 'Region forbidden',
            });
        }
        return {
            regionCode: region.regionCode,
            connectionMode: params.connectionMode,
            configPayload: `vless://issued-${region.regionCode.toLowerCase()}-${session.sessionId}`,
            issuedAt: new Date().toISOString(),
            expireAt: new Date(Date.now() + 15 * 60 * 1000).toISOString(),
        };
    }
    getVpnStatus(accessToken) {
        const account = this.authService.getMe(accessToken);
        const subscription = this.getSubscriptionByAccountId(account.accountId);
        const session = this.authService.getSessionSummary(accessToken);
        return {
            subscriptionStatus: subscription.status,
            currentRegionCode: subscription.status === 'ACTIVE' ? 'JP_BASIC' : null,
            connectionMode: subscription.status === 'ACTIVE' ? 'global' : null,
            canIssueConfig: subscription.status === 'ACTIVE' && session.status === 'ACTIVE',
            sessionStatus: session.status,
        };
    }
    activateSubscription(accountId, planCode) {
        const subscription = {
            subscriptionId: (0, crypto_1.randomUUID)(),
            planCode,
            status: 'ACTIVE',
            startedAt: new Date().toISOString(),
            expireAt: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString(),
            daysRemaining: 30,
            isUnlimitedTraffic: true,
            maxActiveSessions: 1,
        };
        this.subscriptionsByAccountId.set(accountId, subscription);
        return subscription;
    }
    getSubscriptionByAccountId(accountId) {
        return (this.subscriptionsByAccountId.get(accountId) ?? {
            subscriptionId: '',
            planCode: '',
            status: 'NONE',
            startedAt: null,
            expireAt: null,
            daysRemaining: null,
            isUnlimitedTraffic: true,
            maxActiveSessions: 1,
        });
    }
};
exports.VpnService = VpnService;
exports.VpnService = VpnService = __decorate([
    (0, common_1.Injectable)(),
    __metadata("design:paramtypes", [auth_service_1.AuthService])
], VpnService);
//# sourceMappingURL=vpn.service.js.map