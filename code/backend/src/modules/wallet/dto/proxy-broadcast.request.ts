import { IsIn, IsOptional, IsString } from 'class-validator';

export class ProxyBroadcastRequestDto {
  @IsString()
  @IsIn(['SOLANA', 'TRON'])
  networkCode!: 'SOLANA' | 'TRON';

  @IsString()
  @IsIn(['SOL', 'TRX', 'USDT'])
  assetCode!: 'SOL' | 'TRX' | 'USDT';

  @IsString()
  signedPayload!: string;

  @IsOptional()
  @IsString()
  clientTxHash?: string;
}
