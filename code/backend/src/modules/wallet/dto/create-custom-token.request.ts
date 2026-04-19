import { IsInt, IsOptional, IsString, Max, MaxLength, Min } from 'class-validator';

export class CreateCustomTokenRequestDto {
  @IsString()
  @MaxLength(32)
  chainId!: string;

  @IsString()
  @MaxLength(128)
  tokenAddress!: string;

  @IsString()
  @MaxLength(128)
  name!: string;

  @IsString()
  @MaxLength(32)
  symbol!: string;

  @IsInt()
  @Min(0)
  @Max(30)
  decimals!: number;

  @IsOptional()
  @IsString()
  @MaxLength(512)
  iconUrl?: string;
}
