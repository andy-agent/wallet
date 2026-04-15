import { IsIn, IsOptional, IsString, MaxLength } from 'class-validator';

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
}
