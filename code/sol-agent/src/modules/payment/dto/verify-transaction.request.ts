import { Type } from 'class-transformer';
import {
  IsInt,
  IsNotEmpty,
  IsOptional,
  IsString,
  Matches,
  Max,
  Min,
} from 'class-validator';
import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';

export class VerifyTransactionRequestDto {
  @ApiProperty({ description: '待校验的 Solana 交易签名' })
  @IsString()
  @IsNotEmpty()
  signature: string;

  @ApiProperty({
    description: '期望收款地址（SOL 钱包地址或 SPL 代币账户/owner）',
  })
  @IsString()
  @IsNotEmpty()
  recipientAddress: string;

  @ApiProperty({ description: '资产代码，例如 SOL / USDT' })
  @IsString()
  @IsNotEmpty()
  assetCode: string;

  @ApiProperty({
    description: '期望到账金额，按人类可读小数表示，例如 1.25',
  })
  @Matches(/^\d+(\.\d+)?$/, {
    message: 'expectedAmount must be a non-negative decimal string',
  })
  expectedAmount: string;

  @ApiPropertyOptional({ description: '网络代码，默认 solana-mainnet' })
  @IsString()
  @IsOptional()
  networkCode?: string;

  @ApiPropertyOptional({
    description: 'SPL Token mint 地址；USDT mainnet 未传时会使用内置官方 mint',
  })
  @IsString()
  @IsOptional()
  mintAddress?: string;

  @ApiPropertyOptional({
    description:
      'SPL Token 精度。SOL 固定为 9，USDT 固定为 6，其他 token 需要显式传入',
  })
  @Type(() => Number)
  @IsInt()
  @Min(0)
  @Max(18)
  @IsOptional()
  assetDecimals?: number;
}
