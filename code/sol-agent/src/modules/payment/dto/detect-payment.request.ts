import { IsNotEmpty, IsOptional, IsString } from 'class-validator';
import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';

export class DetectPaymentRequestDto {
  @ApiProperty({ description: '待检测的 Solana 地址' })
  @IsString()
  @IsNotEmpty()
  address: string;

  @ApiPropertyOptional({ description: '网络代码，默认 solana-mainnet' })
  @IsString()
  @IsOptional()
  networkCode?: string;

  @ApiPropertyOptional({ description: '期望收款金额（可选）' })
  @IsString()
  @IsOptional()
  expectedAmount?: string;
}
