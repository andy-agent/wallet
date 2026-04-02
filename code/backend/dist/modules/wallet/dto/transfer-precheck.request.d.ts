export declare class TransferPrecheckRequestDto {
    networkCode: 'SOLANA' | 'TRON';
    assetCode: 'SOL' | 'TRX' | 'USDT';
    toAddress: string;
    amount: string;
    orderNo?: string;
}
