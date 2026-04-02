import { VpnService } from '../vpn/vpn.service';
interface ProvisionOrderInput {
    accountId: string;
    planCode: string;
}
export declare class ProvisioningService {
    private readonly vpnService;
    constructor(vpnService: VpnService);
    provisionPaidOrder(input: ProvisionOrderInput): {
        subscriptionId: string;
        status: "COMPLETED";
    };
}
export {};
