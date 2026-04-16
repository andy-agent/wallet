import { IsIn, IsString } from 'class-validator';

export class CreateOrderRequestDto {
  @IsString()
  planCode!: string;

  @IsString()
  @IsIn(['NEW', 'RENEWAL'])
  orderType!: 'NEW' | 'RENEWAL';

  @IsString()
  quoteAssetCode!: string;

  @IsString()
  @IsIn(['SOLANA', 'TRON'])
  quoteNetworkCode!: 'SOLANA' | 'TRON';
}
