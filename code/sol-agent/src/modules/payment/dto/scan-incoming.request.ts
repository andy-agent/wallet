import { Type } from 'class-transformer';
import {
  IsInt,
  IsNotEmpty,
  IsOptional,
  IsString,
  Max,
  Min,
} from 'class-validator';
import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';

export class ScanIncomingTransfersRequestDto {
  @ApiProperty({
    description: '共享收款地址；当前第一版直接按该地址枚举关联签名',
  })
  @IsString()
  @IsNotEmpty()
  collectionAddress: string;

  @ApiProperty({ description: '资产代码，例如 SOL / USDT' })
  @IsString()
  @IsNotEmpty()
  assetCode: string;

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

  @ApiPropertyOptional({
    description: '用于继续翻页扫描的 before signature 游标',
  })
  @IsString()
  @IsOptional()
  beforeSignature?: string;

  @ApiPropertyOptional({
    description: '仅返回 slot 大于该值的入账事件；用于多轮扫描去重',
  })
  @Type(() => Number)
  @IsInt()
  @Min(0)
  @IsOptional()
  minSlotExclusive?: number;

  @ApiPropertyOptional({
    description: '单次扫描的最大签名数，默认 20，最大 100',
  })
  @Type(() => Number)
  @IsInt()
  @Min(1)
  @Max(100)
  @IsOptional()
  limit?: number;
}
