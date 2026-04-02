export declare class ProxyBroadcastRequestDto {
    networkCode: 'SOLANA' | 'TRON';
    assetCode: 'SOL' | 'TRX' | 'USDT';
    signedPayload: string;
    clientTxHash?: string;
}
