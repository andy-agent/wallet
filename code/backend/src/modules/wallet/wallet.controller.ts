import { Body, Controller, Get, Headers, Post, Query } from '@nestjs/common';
import { BuildTransferRequestDto } from './dto/build-transfer.request';
import { ProxyBroadcastRequestDto } from './dto/proxy-broadcast.request';
import { TransferPrecheckRequestDto } from './dto/transfer-precheck.request';
import { UpsertWalletLifecycleRequestDto } from './dto/upsert-wallet-lifecycle.request';
import { UpsertWalletPublicAddressRequestDto } from './dto/upsert-wallet-public-address.request';
import { UpsertWalletSecretBackupRequestDto } from './dto/upsert-wallet-secret-backup.request';
import { WalletService } from './wallet.service';

@Controller('client/v1/wallet')
export class WalletController {
  constructor(private readonly walletService: WalletService) {}

  @Get('chains')
  getChains(@Headers('authorization') authorization?: string) {
    return this.walletService.getChains(this.extractBearer(authorization));
  }

  @Get('assets/catalog')
  getAssetCatalog(
    @Headers('authorization') authorization?: string,
    @Query('networkCode') networkCode?: string,
  ) {
    return this.walletService.getAssetCatalog(
      this.extractBearer(authorization),
      networkCode,
    );
  }

  @Get('overview')
  getOverview(
    @Headers('authorization') authorization?: string,
    @Query('walletId') walletId?: string,
  ) {
    return this.walletService.getOverview(this.extractBearer(authorization), walletId);
  }

  @Get('balances')
  getBalances(
    @Headers('authorization') authorization?: string,
    @Query('walletId') walletId?: string,
  ) {
    return this.walletService.getBalances(this.extractBearer(authorization), walletId);
  }

  @Get('lifecycle')
  getLifecycle(@Headers('authorization') authorization?: string) {
    return this.walletService.getWalletLifecycle(this.extractBearer(authorization));
  }

  @Post('lifecycle')
  upsertLifecycle(
    @Headers('authorization') authorization: string | undefined,
    @Body() body: UpsertWalletLifecycleRequestDto,
  ) {
    return this.walletService.upsertWalletLifecycle(
      this.extractBearer(authorization),
      body,
    );
  }

  @Get('receive-context')
  getReceiveContext(
    @Headers('authorization') authorization?: string,
    @Query() query?: Record<string, string | undefined>,
  ) {
    return this.walletService.getReceiveContext(
      this.extractBearer(authorization),
      this.resolveNetworkCode(query),
      this.resolveAssetCode(query),
    );
  }

  @Post('public-addresses')
  upsertPublicAddress(
    @Headers('authorization') authorization: string | undefined,
    @Body() body: UpsertWalletPublicAddressRequestDto,
  ) {
    return this.walletService.upsertPublicAddress(this.extractBearer(authorization), body);
  }

  @Get('public-addresses')
  listPublicAddresses(
    @Headers('authorization') authorization?: string,
    @Query() query?: Record<string, string | undefined>,
  ) {
    return this.walletService.listPublicAddresses(
      this.extractBearer(authorization),
      this.resolveNetworkCode(query) as 'SOLANA' | 'TRON' | undefined,
      this.resolveAssetCode(query) as 'SOL' | 'TRX' | 'USDT' | undefined,
    );
  }

  @Post('secret-backups')
  upsertSecretBackup(
    @Headers('authorization') authorization: string | undefined,
    @Body() body: UpsertWalletSecretBackupRequestDto,
  ) {
    return this.walletService.upsertSecretBackup(this.extractBearer(authorization), body);
  }

  @Get('secret-backups')
  getSecretBackupMetadata(
    @Headers('authorization') authorization: string | undefined,
  ) {
    return this.walletService.getSecretBackupMetadata(this.extractBearer(authorization));
  }

  @Get('secret-backups/export')
  getSecretBackupExport(
    @Headers('authorization') authorization: string | undefined,
  ) {
    return this.walletService.getSecretBackupExport(this.extractBearer(authorization));
  }

  @Post('transfer/build')
  async buildTransfer(
    @Headers('authorization') authorization: string | undefined,
    @Body() body: BuildTransferRequestDto,
  ) {
    return this.walletService.buildTransfer(this.extractBearer(authorization), body);
  }

  @Post('transfer/precheck')
  async transferPrecheck(
    @Headers('authorization') authorization: string | undefined,
    @Body() body: TransferPrecheckRequestDto,
  ) {
    return this.walletService.transferPrecheck(this.extractBearer(authorization), body);
  }

  @Post('transfer/proxy-broadcast')
  async proxyBroadcast(
    @Headers('authorization') authorization: string | undefined,
    @Body() body: ProxyBroadcastRequestDto,
  ) {
    return this.walletService.proxyBroadcast(this.extractBearer(authorization), body);
  }

  private extractBearer(authorization?: string) {
    if (!authorization?.startsWith('Bearer ')) {
      return '';
    }
    return authorization.slice('Bearer '.length);
  }

  private resolveNetworkCode(query?: Record<string, string | undefined>) {
    return query?.networkCode ?? query?.chainId;
  }

  private resolveAssetCode(query?: Record<string, string | undefined>) {
    return query?.assetCode ?? query?.assetId;
  }
}
