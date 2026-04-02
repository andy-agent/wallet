import { IsIn, IsString } from 'class-validator';

export class CreateWithdrawalRequestDto {
  @IsString()
  amount!: string;

  @IsString()
  payoutAddress!: string;

  @IsString()
  @IsIn(['USDT'])
  assetCode!: 'USDT';

  @IsString()
  @IsIn(['SOLANA'])
  networkCode!: 'SOLANA';
}
