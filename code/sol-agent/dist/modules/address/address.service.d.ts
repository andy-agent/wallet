import { SolanaRpcService } from '../solana/solana.rpc.service';
import { GenerateAddressRequestDto } from './dto/generate-address.request';
export declare class AddressService {
    private readonly solanaRpc;
    private readonly logger;
    private addressStore;
    constructor(solanaRpc: SolanaRpcService);
    generateAddress(body: GenerateAddressRequestDto): {
        accountId: string;
        networkCode: string;
        address: string;
        publicKey: string;
        createdAt: string;
    };
    getAddress(accountId: string): any;
    getAddressInternal(accountId: string): any | null;
}
