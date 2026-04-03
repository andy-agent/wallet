import { IsNotEmpty, IsOptional, IsString } from 'class-validator';
import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';

export class GenerateAddressRequestDto {
  @ApiProperty({ description: '业务侧账户 ID' })
  @IsString()
  @IsNotEmpty()
  accountId: string;

  @ApiPropertyOptional({ description: '网络代码，默认 solana-mainnet' })
  @IsString()
  @IsOptional()
  networkCode?: string;
}
