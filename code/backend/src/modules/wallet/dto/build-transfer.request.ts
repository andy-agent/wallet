import { IsIn, IsOptional, IsString } from 'class-validator';

export class BuildTransferRequestDto {
  @IsString()
  @IsIn(['SOLANA', 'TRON'])
  networkCode!: 'SOLANA' | 'TRON';

  @IsString()
  assetCode!: string;

  @IsString()
  fromAddress!: string;

  @IsString()
  toAddress!: string;

  @IsString()
  amount!: string;

  @IsOptional()
  @IsString()
  orderNo?: string;
}
