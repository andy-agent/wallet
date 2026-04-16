import { IsOptional, IsString, MaxLength } from 'class-validator';

export class CreateWalletRequestDto {
  @IsString()
  @MaxLength(64)
  walletName!: string;

  @IsOptional()
  @IsString()
  mode?: string;
}
