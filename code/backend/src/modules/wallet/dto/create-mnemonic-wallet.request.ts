import { Type } from 'class-transformer';
import {
  ArrayMaxSize,
  IsArray,
  IsBoolean,
  IsIn,
  IsOptional,
  IsString,
  MaxLength,
  ValidateNested,
} from 'class-validator';

class CreateMnemonicWalletKeySlotDto {
  @IsString()
  @MaxLength(32)
  slotCode!: string;

  @IsString()
  @IsIn(['EVM', 'SOLANA', 'TRON'])
  chainFamily!: 'EVM' | 'SOLANA' | 'TRON';

  @IsString()
  @IsIn(['MNEMONIC', 'PRIVATE_KEY'])
  derivationType!: 'MNEMONIC' | 'PRIVATE_KEY';

  @IsOptional()
  @IsString()
  @MaxLength(128)
  derivationPath?: string;
}

class CreateMnemonicWalletChainAccountDto {
  @IsString()
  @MaxLength(32)
  slotCode!: string;

  @IsString()
  @IsIn(['EVM', 'SOLANA', 'TRON'])
  chainFamily!: 'EVM' | 'SOLANA' | 'TRON';

  @IsString()
  @IsIn([
    'ETHEREUM',
    'BSC',
    'POLYGON',
    'ARBITRUM',
    'BASE',
    'OPTIMISM',
    'AVALANCHE_C',
    'SOLANA',
    'TRON',
  ])
  networkCode!:
    | 'ETHEREUM'
    | 'BSC'
    | 'POLYGON'
    | 'ARBITRUM'
    | 'BASE'
    | 'OPTIMISM'
    | 'AVALANCHE_C'
    | 'SOLANA'
    | 'TRON';

  @IsString()
  address!: string;

  @IsOptional()
  @IsBoolean()
  isEnabled?: boolean;

  @IsOptional()
  @IsBoolean()
  isDefaultReceive?: boolean;
}

export class CreateMnemonicWalletRequestDto {
  @IsString()
  @MaxLength(64)
  walletName!: string;

  @IsArray()
  @ArrayMaxSize(8)
  @ValidateNested({ each: true })
  @Type(() => CreateMnemonicWalletKeySlotDto)
  keySlots!: CreateMnemonicWalletKeySlotDto[];

  @IsArray()
  @ArrayMaxSize(16)
  @ValidateNested({ each: true })
  @Type(() => CreateMnemonicWalletChainAccountDto)
  chainAccounts!: CreateMnemonicWalletChainAccountDto[];
}
