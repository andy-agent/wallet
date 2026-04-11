import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';

export class ScanIncomingTransferItemDto {
  @ApiProperty({ description: '链上交易签名' })
  signature: string;

  @ApiPropertyOptional({ description: '交易所在 slot', nullable: true })
  slot: number | null;

  @ApiPropertyOptional({ description: 'blockTime 秒级时间戳', nullable: true })
  blockTime: number | null;

  @ApiPropertyOptional({
    description: 'Solana signature confirmation status',
    nullable: true,
  })
  confirmationStatus: string | null;

  @ApiProperty({ description: '共享收款地址' })
  collectionAddress: string;

  @ApiProperty({ description: '资产代码，例如 SOL / USDT' })
  assetCode: string;

  @ApiProperty({ description: '资产类型：NATIVE_SOL / SPL_TOKEN' })
  assetKind: string;

  @ApiPropertyOptional({ description: 'SPL mint 地址', nullable: true })
  mintAddress: string | null;

  @ApiProperty({ description: '资产精度' })
  decimals: number;

  @ApiProperty({ description: '到账金额，人类可读小数' })
  amount: string;

  @ApiProperty({ description: '到账金额，最小单位整数' })
  amountRaw: string;

  @ApiProperty({
    description: '命中的接收账户列表；SOL 为钱包地址，SPL 为 token account',
    type: [String],
  })
  matchedAccounts: string[];
}

export class ScanIncomingTransfersResponseDto {
  @ApiProperty({ description: '共享收款地址' })
  collectionAddress: string;

  @ApiProperty({ description: '网络代码' })
  networkCode: string;

  @ApiProperty({ description: '资产代码，例如 SOL / USDT' })
  assetCode: string;

  @ApiProperty({ description: '资产类型：NATIVE_SOL / SPL_TOKEN' })
  assetKind: string;

  @ApiPropertyOptional({ description: 'SPL mint 地址', nullable: true })
  mintAddress: string | null;

  @ApiProperty({ description: '资产精度' })
  decimals: number;

  @ApiProperty({ description: '本次扫描处理的签名条数' })
  scannedSignatures: number;

  @ApiProperty({ description: '本次标准化后识别出的入账条数' })
  matchedTransfers: number;

  @ApiPropertyOptional({
    description: '继续向后翻页时可使用的 before signature',
    nullable: true,
  })
  nextBeforeSignature: string | null;

  @ApiProperty({
    description: '标准化的共享地址入账事件',
    type: [ScanIncomingTransferItemDto],
  })
  items: ScanIncomingTransferItemDto[];
}
