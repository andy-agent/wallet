import {
  ArrayMaxSize,
  IsArray,
  IsBoolean,
  IsIn,
  IsInt,
  IsOptional,
  IsString,
  MaxLength,
  Min,
  ValidateNested,
} from 'class-validator';
import { Type } from 'class-transformer';

class WalletSecretBackupPublicAddressDto {
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

export class UpsertWalletSecretBackupRequestDto {
  @IsOptional()
  @IsString()
  walletId?: string;

  @IsString()
  @IsIn(['MNEMONIC'])
  secretType!: 'MNEMONIC';

  @IsString()
  mnemonic!: string;

  @IsString()
  mnemonicHash!: string;

  @IsInt()
  @Min(12)
  mnemonicWordCount!: number;

  @IsOptional()
  @IsString()
  @MaxLength(64)
  walletName?: string;

  @IsOptional()
  @IsString()
  @MaxLength(32)
  sourceType?: string;

  @IsOptional()
  @IsArray()
  @ArrayMaxSize(8)
  @ValidateNested({ each: true })
  @Type(() => WalletSecretBackupPublicAddressDto)
  publicAddresses?: WalletSecretBackupPublicAddressDto[];
}
