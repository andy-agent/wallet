export declare class CreateOrderRequestDto {
    planCode: string;
    orderType: 'NEW' | 'RENEWAL';
    quoteAssetCode: 'SOL' | 'USDT';
    quoteNetworkCode: 'SOLANA' | 'TRON';
}
