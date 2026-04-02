import { IssueVpnConfigRequestDto } from './dto/issue-vpn-config.request';
import { VpnService } from './vpn.service';
export declare class VpnController {
    private readonly vpnService;
    constructor(vpnService: VpnService);
    getRegions(authorization?: string): {
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
    issueConfig(authorization: string | undefined, body: IssueVpnConfigRequestDto): {
        regionCode: string;
        connectionMode: "global" | "rule";
        configPayload: string;
        issuedAt: string;
        expireAt: string;
    };
    getStatus(authorization?: string): {
        subscriptionStatus: "ACTIVE" | "EXPIRED" | "PENDING_ACTIVATION" | "SUSPENDED" | "CANCELED" | "NONE";
        currentRegionCode: string | null;
        connectionMode: string | null;
        canIssueConfig: boolean;
        sessionStatus: import("../auth/auth.types").SessionStatus;
    };
    private extractBearer;
}
