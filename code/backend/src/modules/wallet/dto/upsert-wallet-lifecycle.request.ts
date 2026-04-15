import { IsIn, IsInt, IsOptional, IsString, MaxLength, Min } from 'class-validator';

export class UpsertWalletLifecycleRequestDto {
  @IsString()
  @IsIn(['CREATE', 'IMPORT', 'ACKNOWLEDGE_BACKUP', 'CONFIRM_BACKUP'])
  action!: 'CREATE' | 'IMPORT' | 'ACKNOWLEDGE_BACKUP' | 'CONFIRM_BACKUP';

  @IsOptional()
  @IsString()
  @MaxLength(64)
  displayName?: string;

  @IsOptional()
  @IsString()
  mnemonic?: string;

  @IsOptional()
  @IsString()
  mnemonicHash?: string;

  @IsOptional()
  @IsInt()
  @Min(1)
  mnemonicWordCount?: number;
}
