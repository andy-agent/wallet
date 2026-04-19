import { IsBoolean, IsOptional, IsString, MaxLength } from 'class-validator';

export class UpdateWalletRequestDto {
  @IsOptional()
  @IsString()
  @MaxLength(64)
  walletName?: string;

  @IsOptional()
  @IsBoolean()
  isArchived?: boolean;
}
