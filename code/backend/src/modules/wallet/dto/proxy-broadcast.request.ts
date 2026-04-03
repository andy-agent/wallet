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

  /** Target address (for validation) - added for sol/usdt service integration */
  @IsOptional()
  @IsString()
  toAddress?: string;

  /** Serialized transaction (base64) - for sol/usdt service broadcast */
  @IsOptional()
  @IsString()
  serializedTx?: string;
}
