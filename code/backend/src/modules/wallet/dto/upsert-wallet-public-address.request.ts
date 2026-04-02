import { IsBoolean, IsIn, IsString } from 'class-validator';

export class UpsertWalletPublicAddressRequestDto {
  @IsString()
  @IsIn(['SOLANA', 'TRON'])
  networkCode!: 'SOLANA' | 'TRON';

  @IsString()
  @IsIn(['SOL', 'TRX', 'USDT'])
  assetCode!: 'SOL' | 'TRX' | 'USDT';

  @IsString()
  address!: string;

  @IsBoolean()
  isDefault!: boolean;
}
