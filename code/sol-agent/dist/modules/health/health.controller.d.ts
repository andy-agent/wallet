import { SolanaRpcService } from '../solana/solana.rpc.service';
export declare class HealthController {
    private readonly solanaRpc;
    constructor(solanaRpc: SolanaRpcService);
    getHealth(): Promise<{
        status: string;
        service: string;
        rpc: {
            healthy: boolean;
            network: string;
            slot?: number;
            error?: string;
        };
    }>;
}
