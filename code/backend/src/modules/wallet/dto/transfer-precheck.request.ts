import { IsIn, IsOptional, IsString } from 'class-validator';

export class TransferPrecheckRequestDto {
  @IsString()
  @IsIn(['SOLANA', 'TRON'])
  networkCode!: 'SOLANA' | 'TRON';

  @IsString()
  @IsIn(['SOL', 'TRX', 'USDT'])
  assetCode!: 'SOL' | 'TRX' | 'USDT';

  @IsString()
  toAddress!: string;

  @IsString()
  amount!: string;

  @IsOptional()
  @IsString()
  orderNo?: string;
}
