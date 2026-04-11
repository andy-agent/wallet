import { SolanaRpcService } from '../solana/solana.rpc.service';
import { GenerateAddressRequestDto } from './dto/generate-address.request';
export declare class AddressService {
    private readonly solanaRpc;
    private readonly logger;
    private addressStore;
    constructor(solanaRpc: SolanaRpcService);
    generateAddress(body: GenerateAddressRequestDto): {
        [x: string]: any;
    };
    getAddress(accountId: string): {
        [x: string]: any;
    };
    getAddressInternal(accountId: string): any | null;
    private sanitizeAddressData;
}
