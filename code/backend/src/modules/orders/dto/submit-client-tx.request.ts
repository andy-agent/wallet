import { IsIn, IsOptional, IsString } from 'class-validator';

export class SubmitClientTxRequestDto {
  @IsString()
  txHash!: string;

  @IsString()
  @IsIn(['SOLANA', 'TRON'])
  networkCode!: 'SOLANA' | 'TRON';

  @IsOptional()
  @IsString()
  signedAt?: string;

  @IsOptional()
  @IsString()
  payerWalletId?: string;

  @IsOptional()
  @IsString()
  payerChainAccountId?: string;

  @IsOptional()
  @IsString()
  submittedFromAddress?: string;
}
