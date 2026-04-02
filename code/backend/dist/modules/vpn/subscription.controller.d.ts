import { VpnService } from './vpn.service';
export declare class SubscriptionController {
    private readonly vpnService;
    constructor(vpnService: VpnService);
    getCurrent(authorization?: string): import("./vpn.service").SubscriptionState;
    private extractBearer;
}
