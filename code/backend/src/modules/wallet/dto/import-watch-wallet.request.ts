import { IsIn, IsString, MaxLength } from 'class-validator';

export class ImportWatchWalletRequestDto {
  @IsString()
  @MaxLength(64)
  walletName!: string;

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
}
