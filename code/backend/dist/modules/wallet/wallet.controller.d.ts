import { ProxyBroadcastRequestDto } from './dto/proxy-broadcast.request';
import { TransferPrecheckRequestDto } from './dto/transfer-precheck.request';
import { UpsertWalletPublicAddressRequestDto } from './dto/upsert-wallet-public-address.request';
import { WalletService } from './wallet.service';
export declare class WalletController {
    private readonly walletService;
    constructor(walletService: WalletService);
    getChains(authorization?: string): {
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
    getAssetCatalog(authorization?: string, networkCode?: string): {
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
    upsertPublicAddress(authorization: string | undefined, body: UpsertWalletPublicAddressRequestDto): import("./wallet.service").WalletPublicAddressItem;
    listPublicAddresses(authorization?: string, networkCode?: string, assetCode?: string): {
        items: import("./wallet.service").WalletPublicAddressItem[];
    };
    transferPrecheck(authorization: string | undefined, body: TransferPrecheckRequestDto): {
        networkCode: "SOLANA" | "TRON";
        assetCode: "SOL" | "USDT" | "TRX";
        toAddressNormalized: string;
        amount: string;
        estimatedFee: string;
        directBroadcastEnabled: boolean;
        proxyBroadcastEnabled: boolean;
        warnings: string[];
    };
    proxyBroadcast(authorization: string | undefined, body: ProxyBroadcastRequestDto): {
        networkCode: "SOLANA" | "TRON";
        broadcasted: boolean;
        txHash: string;
        acceptedAt: string;
    };
    private extractBearer;
}
