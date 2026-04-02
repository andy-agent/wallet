import { AuthService } from '../auth/auth.service';
import { ProxyBroadcastRequestDto } from './dto/proxy-broadcast.request';
import { TransferPrecheckRequestDto } from './dto/transfer-precheck.request';
import { UpsertWalletPublicAddressRequestDto } from './dto/upsert-wallet-public-address.request';
export interface WalletPublicAddressItem {
    addressId: string;
    accountId: string;
    networkCode: 'SOLANA' | 'TRON';
    assetCode: 'SOL' | 'TRX' | 'USDT';
    address: string;
    isDefault: boolean;
    createdAt: string;
}
export declare class WalletService {
    private readonly authService;
    private readonly publicAddresses;
    constructor(authService: AuthService);
    getChains(accessToken: string): {
        items: {
            networkCode: string;
            displayName: string;
            nativeAssetCode: string;
            directBroadcastEnabled: boolean;
            proxyBroadcastEnabled: boolean;
            requiredConfirmations: number;
            publicRpcUrl: string;
        }[];
    };
    getAssetCatalog(accessToken: string, networkCode?: string): {
        items: ({
            assetId: `${string}-${string}-${string}-${string}-${string}`;
            networkCode: string;
            assetCode: string;
            displayName: string;
            symbol: string;
            decimals: number;
            isNative: boolean;
            contractAddress: null;
            walletVisible: boolean;
            orderPayable: boolean;
        } | {
            assetId: `${string}-${string}-${string}-${string}-${string}`;
            networkCode: string;
            assetCode: string;
            displayName: string;
            symbol: string;
            decimals: number;
            isNative: boolean;
            contractAddress: string;
            walletVisible: boolean;
            orderPayable: boolean;
        })[];
    };
    upsertPublicAddress(accessToken: string, dto: UpsertWalletPublicAddressRequestDto): WalletPublicAddressItem;
    listPublicAddresses(accessToken: string, networkCode?: string, assetCode?: string): {
        items: WalletPublicAddressItem[];
    };
    transferPrecheck(accessToken: string, dto: TransferPrecheckRequestDto): {
        networkCode: "SOLANA" | "TRON";
        assetCode: "SOL" | "USDT" | "TRX";
        toAddressNormalized: string;
        amount: string;
        estimatedFee: string;
        directBroadcastEnabled: boolean;
        proxyBroadcastEnabled: boolean;
        warnings: string[];
    };
    proxyBroadcast(accessToken: string, dto: ProxyBroadcastRequestDto): {
        networkCode: "SOLANA" | "TRON";
        broadcasted: boolean;
        txHash: string;
        acceptedAt: string;
    };
    private isAddressValid;
}
