export declare class UpsertWalletPublicAddressRequestDto {
    networkCode: 'SOLANA' | 'TRON';
    assetCode: 'SOL' | 'TRX' | 'USDT';
    address: string;
    isDefault: boolean;
}
