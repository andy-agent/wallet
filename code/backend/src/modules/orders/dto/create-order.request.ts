import { IsIn, IsString } from 'class-validator';
import { IsOptional } from 'class-validator';

export class CreateOrderRequestDto {
  @IsString()
  planCode!: string;

  @IsOptional()
  @IsString()
  selectedRegionCode?: string;

  @IsString()
  @IsIn(['NEW', 'RENEWAL'])
  orderType!: 'NEW' | 'RENEWAL';

  @IsString()
  quoteAssetCode!: string;

  @IsString()
  @IsIn(['SOLANA', 'TRON'])
  quoteNetworkCode!: 'SOLANA' | 'TRON';

  @IsOptional()
  @IsString()
  payerWalletId?: string;

  @IsOptional()
  @IsString()
  payerChainAccountId?: string;
}
