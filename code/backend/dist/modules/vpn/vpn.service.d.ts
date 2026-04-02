import { AuthService } from '../auth/auth.service';
export interface SubscriptionState {
    subscriptionId: string;
    planCode: string;
    status: 'PENDING_ACTIVATION' | 'ACTIVE' | 'EXPIRED' | 'SUSPENDED' | 'CANCELED' | 'NONE';
    startedAt: string | null;
    expireAt: string | null;
    daysRemaining: number | null;
    isUnlimitedTraffic: boolean;
    maxActiveSessions: number;
}
export declare class VpnService {
    private readonly authService;
    private readonly subscriptionsByAccountId;
    constructor(authService: AuthService);
    getCurrentSubscription(accessToken: string): SubscriptionState;
    listRegions(accessToken: string): {
        items: {
            regionId: string;
            regionCode: string;
            displayName: string;
            tier: string;
            status: string;
            isAllowed: boolean;
            remark: string;
        }[];
    };
    issueConfig(accessToken: string, params: {
        regionCode: string;
        connectionMode: 'global' | 'rule';
    }): {
        regionCode: string;
        connectionMode: "global" | "rule";
        configPayload: string;
        issuedAt: string;
        expireAt: string;
    };
    getVpnStatus(accessToken: string): {
        subscriptionStatus: "ACTIVE" | "EXPIRED" | "PENDING_ACTIVATION" | "SUSPENDED" | "CANCELED" | "NONE";
        currentRegionCode: string | null;
        connectionMode: string | null;
        canIssueConfig: boolean;
        sessionStatus: import("../auth/auth.types").SessionStatus;
    };
    activateSubscription(accountId: string, planCode: string): SubscriptionState;
    private getSubscriptionByAccountId;
}
