import { Body, Controller, Get, Headers, Post, Query } from '@nestjs/common';
import { ProxyBroadcastRequestDto } from './dto/proxy-broadcast.request';
import { TransferPrecheckRequestDto } from './dto/transfer-precheck.request';
import { UpsertWalletPublicAddressRequestDto } from './dto/upsert-wallet-public-address.request';
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
    @Query('networkCode') networkCode?: string,
    @Query('assetCode') assetCode?: string,
  ) {
    return this.walletService.listPublicAddresses(
      this.extractBearer(authorization),
      networkCode,
      assetCode,
    );
  }

  @Post('transfer/precheck')
  transferPrecheck(
    @Headers('authorization') authorization: string | undefined,
    @Body() body: TransferPrecheckRequestDto,
  ) {
    return this.walletService.transferPrecheck(this.extractBearer(authorization), body);
  }

  @Post('transfer/proxy-broadcast')
  proxyBroadcast(
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
}
